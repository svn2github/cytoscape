package csplugins.ucsd.rmkelley.PathBlastBrowser.Layout;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.geom.Point2D;
import java.io.*;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.util.GinyFactory;
import cytoscape.data.Semantics;
import cytoscape.view.CyWindow;
import cytoscape.view.GraphViewController;
import cytoscape.data.readers.GMLTree;

public class DualLayoutTask extends Thread{
  CyWindow cyWindow;
  private static String TITLE1 = "Split Graph";
  private static String SPLIT_STRING = "\\|";
  private static double SMALL_DISTANCE = 0.001;
  private static double CUTOFF = 15;
  private double SPRING_STIFFNESS = 10;
  private double SPRING_LENGTH = 500;
  private double HOMOLOGY_STIFFNESS = 50;
  private double HOMOLOGY_LENGTH = 50;
  private double MAX_DISTANCE = 50;
  private int MAX_ITERATIONS = 100;
  double increment = 0.02;
  private double COOLING_DECREMENT = increment/100;
  private double GAP = 200;
  private double electricalRepulsion = 50000000;
  DualLayoutCommandLineParser parser;
  private CyNetwork sifNetwork;



  public DualLayoutTask(CyNetwork sifNetwork,CyWindow cyWindow,DualLayoutCommandLineParser parser){
    this.sifNetwork = sifNetwork;
    this.cyWindow = cyWindow;
    this.parser = parser;
  }
  public void run(){

    //get the graph view object from the window.
    GraphView graphView = cyWindow.getView();
    //get the network object; this contains the graph
    
    //String callerID = "DualLayout.actionPerformed";
    //sifNetwork.beginActivity(callerID);
    //this is the graph structure; it should never be null,
    RootGraph sifRoot = sifNetwork.getRootGraph();


    //first make a new network in which to put the result 
    //and GraphObjAttributes to put the attributes associated
    //with the nodes
    RootGraph newRoot = GinyFactory.createRootGraph();
    GraphObjAttributes newNodeAttributes = new GraphObjAttributes();
    GraphObjAttributes newEdgeAttributes = new GraphObjAttributes();

    //These are maps from the name of a node to the node itself
    //don't use graphObjAttributes here because
    //I want to keep the left nodes separated from the right
    //nodes
    HashMap left_name2node = new HashMap();
    HashMap right_name2node = new HashMap();

    //this hasmap maps from a node to a vector of nodes which have an established
    //homology with that node
    HashMap node2NodeVec = new HashMap();

    GraphObjAttributes nodeAttributes = sifNetwork.getNodeAttributes();
    Iterator compatNodeIt = sifRoot.nodesList().iterator();
    NodePairSet homologyPairSet = new NodePairSet();
    while(compatNodeIt.hasNext()){
      Node current = (Node)compatNodeIt.next();
      //String name = current.getIdentifier();
      String name = nodeAttributes.getCanonicalName(current);
      String [] names = name.split(SPLIT_STRING);
      Node leftNode = (Node)left_name2node.get(names[0]);
      if(leftNode == null){
	leftNode = newRoot.getNode(newRoot.createNode());
	leftNode.setIdentifier(names[0]);
	newNodeAttributes.addNameMapping(names[0],leftNode);	
	left_name2node.put(names[0],leftNode);
      }

      Node rightNode = (Node)right_name2node.get(names[1]);
      if(rightNode == null){
	rightNode = newRoot.getNode(newRoot.createNode());
	rightNode.setIdentifier(names[1]);
	newNodeAttributes.addNameMapping(names[1],rightNode);
	right_name2node.put(names[1],rightNode);
      }

      homologyPairSet.add(leftNode,rightNode);
    }

    
    //for each edge in the compatability graph, split it into two edges
    //and add each of these edges to the new root graph 
    Iterator compatEdgeIt = sifRoot.edgesList().iterator();
    GraphObjAttributes compatEdgeAttributes = sifNetwork.getEdgeAttributes();
    while(compatEdgeIt.hasNext()){
      Edge current = (Edge)compatEdgeIt.next();
      //figure out the names of the four end points for the two edges
      String [] sourceSplat = nodeAttributes.getCanonicalName(current.getSource()).split(SPLIT_STRING);
      String [] targetSplat = nodeAttributes.getCanonicalName(current.getTarget()).split(SPLIT_STRING);


      String compatInteraction = (String)compatEdgeAttributes.get("interaction",compatEdgeAttributes.getCanonicalName(current));
      String leftInteraction,rightInteraction;	
      if(compatInteraction.length() == 2){
	leftInteraction = compatInteraction.substring(0,1)+"1";
	rightInteraction = compatInteraction.substring(1,2)+"2";
      }
      else{
	//I'm not sure what is in the interaction string
	leftInteraction = "m1";
	rightInteraction = "m2";
      }
      //create the new left edge and associate its attributes
      //don't make self edges
      //need to make a check here to see if this edge has already been
      //added into the graph
      if(!sourceSplat[0].equals(targetSplat[0])){

	String leftName = sourceSplat[0]+" ("+leftInteraction+") "+targetSplat[0];
	if(!newEdgeAttributes.getObjectMap().keySet().contains(leftName)){
	  Edge leftEdge = newRoot.getEdge(newRoot.createEdge((Node)left_name2node.get(sourceSplat[0]),(Node)left_name2node.get(targetSplat[0]),true));
	  leftEdge.setIdentifier(leftName);
	  newEdgeAttributes.addNameMapping(leftName,leftEdge);
	  newEdgeAttributes.add("interaction",leftName,leftInteraction);
	}
      }
      //create the new right edge and associate its attributes
      
      if(!sourceSplat[1].equals(targetSplat[1])){
	String rightName = sourceSplat[1]+" ("+rightInteraction+") "+targetSplat[1];
	if(!newEdgeAttributes.getObjectMap().keySet().contains(rightName)){
	  Edge rightEdge = newRoot.getEdge(newRoot.createEdge((Node)right_name2node.get(sourceSplat[1]),(Node)right_name2node.get(targetSplat[1]),true));
	  rightEdge.setIdentifier(rightName);
	  newEdgeAttributes.addNameMapping(rightName,rightEdge);
	  newEdgeAttributes.add("interaction",rightName,rightInteraction);
	}
      }
    }


    //now that the root graph has been created, put it into a window
    //CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(), new CyNetwork(newRoot,newNodeAttributes,newEdgeAttributes), DualLayout.NEW_TITLE);
    CyNetwork gmlNetwork = new CyNetwork(newRoot,newNodeAttributes,newEdgeAttributes);
    cyWindow.getNetwork().setNewGraphFrom(gmlNetwork,false);
    HashSet leftNodeSet = new HashSet(left_name2node.values());
    HashSet rightNodeSet = new HashSet(right_name2node.values());
    GraphView newView = cyWindow.getView();
    cyWindow.getMainFrame().setVisible(false);
    SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter(newView,leftNodeSet,rightNodeSet,homologyPairSet);
    layouter.doLayout();
    gmlNetwork.setNeedsLayout(false);
    cyWindow.getMainFrame().setVisible(true);
    
    //get all the node views for the nodes in the two different categories
    Vector leftNodeViews = new Vector();
    Iterator leftNodeIt = left_name2node.values().iterator();
    while(leftNodeIt.hasNext()){
      leftNodeViews.add(newView.getNodeView((Node)leftNodeIt.next()));
    }
    Vector rightNodeViews = new Vector();
    Iterator rightNodeIt = right_name2node.values().iterator();
    while(rightNodeIt.hasNext()){
      Node rightNode = (Node)rightNodeIt.next();
      rightNodeViews.add(newView.getNodeView(rightNode));
    }

    //probably need to calculate the offset here
    double maxLeft = Double.NEGATIVE_INFINITY;
    Iterator leftViewIt = leftNodeViews.iterator();
    while(leftViewIt.hasNext()){
      maxLeft = Math.max(maxLeft,((NodeView)leftViewIt.next()).getXPosition());
    }

    double minRight = Double.POSITIVE_INFINITY;
    Iterator rightViewIt = rightNodeViews.iterator();
    while(rightViewIt.hasNext()){
      minRight = Math.min(minRight,((NodeView)rightViewIt.next()).getXPosition());
    }


    //move all the right nodes over by the offset
    double offset = (maxLeft-minRight)+GAP;
    rightViewIt = rightNodeViews.iterator();
    while(rightViewIt.hasNext()){
      NodeView rightView = (NodeView)rightViewIt.next();
      rightView.setXPosition(rightView.getXPosition()+offset);
    }
    
    //make sure all the nodes have their position updated
    Iterator nodeViewIt = newView.getNodeViewsIterator();
    while(nodeViewIt.hasNext()){
      ((NodeView)nodeViewIt.next()).setNodePosition(true);
    }

        
    if(parser.addEdges()){
      GraphPerspective newPerspective = newView.getGraphPerspective();
      HashMap outerMap = homologyPairSet.getOuterMap();
      for(Iterator outerSetIt = outerMap.keySet().iterator();
	  outerSetIt.hasNext();){
	Node outerNode = (Node)outerSetIt.next();
	for(Iterator innerSetIt = ((Set)outerMap.get(outerNode)).iterator();
	    innerSetIt.hasNext();){
	  Node innerNode = (Node)innerSetIt.next();
	  //want to add a homology edge to the from the left node to the rightnode
	  Node leftNode,rightNode;
	  if(leftNodeSet.contains(outerNode)){
	    leftNode = outerNode;
	    rightNode = innerNode;
	  }
	  else{
	    leftNode = innerNode;
	    rightNode = outerNode;
	  }
	  Edge homologyEdge = newRoot.getEdge(newRoot.createEdge(leftNode,rightNode,false));
	  String homologyName = ""+leftNode+" (hm) "+rightNode;
	  homologyEdge.setIdentifier(homologyName);
	  newEdgeAttributes.addNameMapping(homologyName,homologyEdge);
	  newEdgeAttributes.add("interaction",homologyName,"hm");
	  newPerspective.restoreEdge(homologyEdge);
	}
      }
    }
    

    if(parser.applyColor()){
      System.out.println("Color option not implemeneted yet");
    }

    if(parser.save()){
      String name = parser.getGMLname(); 
      try {
	FileWriter fileWriter = new FileWriter(name);
	GMLTree result = new GMLTree(cyWindow);
	fileWriter.write(result.toString());
	fileWriter.close();
      }
      catch (IOException ioe) {
	System.err.println("Error while writing " + name);
	ioe.printStackTrace();
      }
    }
    if(parser.exit()){
      System.exit(0);
    }
    
    //sifNetwork.endActivity(callerID);
  }

}

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
  private double GAP = 200;
  private double OFFSET = 500;
  DualLayoutCommandLineParser parser;
  private CyNetwork sifNetwork;


  /**
   * @param sifNetwork a subgraph of the compatability graph that is a conserved complex
   */
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
    
    //tryy to guess how many species we are tyring to align
    int k = 0;
    {
      List nodes = sifRoot.nodesList();
      if (nodes == null || nodes.size() <= 0) {
	throw new IllegalArgumentException("No nodes in this graph");
      } // end of if ()
      Node firstNode = (Node)nodes.get(0);
      String firstName = sifNetwork.getNodeAttributes().getCanonicalName(firstNode);
      String [] splat = firstName.split("\\|");
      k = splat.length;
      if (k <= 1) {
	throw new IllegalArgumentException("Must align at least 2 species");
      } // end of if ()
    } 
    

      
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
    //HashMap left_name2node = new HashMap();
    //HashMap right_name2node = new HashMap();
    //this is a vector of hashmap that map from the name of a node
    //to the actual node for each species.
    Vector name2Node_Vector = new Vector();
    for (int  idx= 0;  idx<k ; idx++) {
      name2Node_Vector.add(new HashMap());
    } // end of for (int  = 0;  < ; ++)
   
    GraphObjAttributes nodeAttributes = sifNetwork.getNodeAttributes();
    Iterator compatNodeIt = sifRoot.nodesList().iterator();
    NodePairSet homologyPairSet = new NodePairSet();
    //this maps from a node to the species that node belongs
    //to
    HashMap node2Species = new HashMap();
    while(compatNodeIt.hasNext()){
      Node current = (Node)compatNodeIt.next();
      //String name = current.getIdentifier();
      String name = nodeAttributes.getCanonicalName(current);
      String [] names = name.split(SPLIT_STRING);
      if (names.length != k) {
	//awww, shit
	throw new IllegalArgumentException("Incorrect value of k");
      } // end of if ()
      
      Vector nodes = new Vector(k);
      for (int idx = 0; idx < k ; idx++) {
	HashMap name2Node = (HashMap)name2Node_Vector.get(idx);
	Node idxNode = (Node)name2Node.get(names[idx]);
	if (idxNode == null) {
	  idxNode = newRoot.getNode(newRoot.createNode());
	  idxNode.setIdentifier(names[idx]);
	  newNodeAttributes.addNameMapping(names[idx],idxNode);
	  name2Node.put(names[idx],idxNode);
	  node2Species.put(idxNode,new Integer(idx));
	} // end of if ()
	nodes.add(idxNode);
	
      } // end of for (int  = 0;  < ; ++)
      
      for (int idx = 0;idx<k ;idx++) {
	for (int idy = idx+1;idy<k;idy++) {
	  homologyPairSet.add((Node)nodes.get(idx),(Node)nodes.get(idy));
	} // end of for (int  = 0;  < ; ++)
      } // end of for (int  = 0;  < ; ++)
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
      //this is a vector of interaction types which stores the equivalent interaction for each of the species
      Vector interactionTypes = new Vector(k);
      if(compatInteraction.length() == k){
	for (int idx = 0; idx < k; idx++) {
	  interactionTypes.add(compatInteraction.substring(idx,idx+1)+idx);
	} // end of for (int  = 0;  < ; ++)
      }
      else{
	for (int idx = 0; idx < k; k++) {
	  interactionTypes.add("?"+idx);
	} // end of for (int  = 0;  < ; ++)
	
      }


      for (int idx = 0;idx  < k; idx++) {
	//creat the new nedge
	//don't make self edges
	//need to make a check here to see iff this edge
	//has already been added into the graph
	if (!sourceSplat[idx].equals(targetSplat[idx])) {
	  String idxName = sourceSplat[idx]+" ("+interactionTypes.get(idx)+") "+targetSplat[idx];
	  //if (!newEdgeAttributes.getObjectMap().keySet().contains(idxName)) {
	  HashMap name2Node = (HashMap)name2Node_Vector.get(idx);
	  Node sourceNode = (Node)name2Node.get(sourceSplat[idx]);
	  Node targetNode = (Node)name2Node.get(targetSplat[idx]);
	  if (!newRoot.isNeighbor(sourceNode,targetNode)) {
	    Edge idxEdge = newRoot.getEdge(newRoot.createEdge(sourceNode,targetNode,true));
	    idxEdge.setIdentifier(idxName);
	    newEdgeAttributes.addNameMapping(idxName,idxEdge);
	    newEdgeAttributes.add("interaction",idxName,(String)interactionTypes.get(idx));
	  } // end of if ()
	  
	} // end of if ()
	
      } // end of for (int this  = 0;  < ; ++)
      
    }
    //now that the root graph has been created, put it into a window
  //CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(), new CyNetwork(newRoot,newNodeAttributes,newEdgeAttributes), DualLayout.NEW_TITLE);
  CyNetwork gmlNetwork = new CyNetwork(newRoot,newNodeAttributes,newEdgeAttributes);
  cyWindow.getNetwork().setNewGraphFrom(gmlNetwork,false);
  GraphView newView = cyWindow.getView();
  cyWindow.getMainFrame().setVisible(false);
  SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter(newView,node2Species,homologyPairSet);
  layouter.doLayout();
  gmlNetwork.setNeedsLayout(false);
  cyWindow.getMainFrame().setVisible(true);
    
    

  //move all the nodes over an amount proportional to their species number
  Iterator nodeViewIt = newView.getNodeViewsIterator();
  while (nodeViewIt.hasNext()) {
    NodeView nodeView = (NodeView)nodeViewIt.next();
    int species = ((Integer)node2Species.get(nodeView.getNode())).intValue();
    nodeView.setXPosition(nodeView.getXPosition()+OFFSET*species);
  } // end of while ()
    
    
  //make sure all the nodes have their position updated
  nodeViewIt = newView.getNodeViewsIterator();
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
	Edge homologyEdge = newRoot.getEdge(newRoot.createEdge(outerNode,innerNode,false));
	String homologyName = ""+outerNode+" (hm) "+innerNode;
	homologyEdge.setIdentifier(homologyName);
	newEdgeAttributes.addNameMapping(homologyName,homologyEdge);
	newEdgeAttributes.add("interaction",homologyName,"hm");
	newPerspective.restoreEdge(homologyEdge);
      }
    }
  }
    

  if(parser.applyColor()){
    System.out.println("Color option not implemeneted");
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

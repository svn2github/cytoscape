package csplugins.ucsd.rmkelley.DualLayout;
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

import cytoscape.plugin.AbstractPlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.util.GinyFactory;
import cytoscape.data.Semantics;
import cytoscape.view.CyWindow;
import cytoscape.view.GraphViewController;
import cytoscape.data.readers.GMLTree;
/**
 * This is a plugin to separate a compatability graph into two
 * separate graphs, one for each species. It tries to lay the graphs
 * out such that homologous nodes are in a similar position in each graph.
 * In order to achieve this, it uses a force-directed layout, where the relevant
 * forces are repulsion between nodes, attraction between nodes connected by edge
 * and psuedo-attraction between homologous nodes (node will actuall be attracted to
 * that is "offset" away from the real node.
 */
public class DualLayout extends AbstractPlugin{

  CyWindow cyWindow;
  DualLayoutCommandLineParser parser;
  public static String NEW_TITLE = "Split Graph";
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
  public DualLayout(CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    cyWindow.getCyMenus().getOperationsMenu().add( new DualLayoutAction() );
    parser = new DualLayoutCommandLineParser(cyWindow.getCytoscapeObj().getConfiguration().getArgs());
    if(parser.run() && !cyWindow.getWindowTitle().endsWith(NEW_TITLE)){
      Thread t = new DualLayoutTask(cyWindow,parser); 
      t.start();
    }
  }

  /**
   * This class gets attached to the menu item.
   */
  public class DualLayoutAction extends AbstractAction {

    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public DualLayoutAction() {super("Dual Layout");}

    /**
     * Gives a description of this plugin.
     */
    public String describe() {
      StringBuffer sb = new StringBuffer();
      sb.append("Split a compatability graph and try to lay it out");
      return sb.toString();
    }

    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {

      //inform listeners that we're doing an operation on the network
      Thread t = new DualLayoutTask(cyWindow,parser); 
      t.start();
    }
  }
}

class DualLayoutTask extends Thread{
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



  public DualLayoutTask(CyWindow cyWindow,DualLayoutCommandLineParser parser){
    this.cyWindow = cyWindow;
    this.parser = parser;
  }
  public void run(){

    //get the graph view object from the window.
    GraphView graphView = cyWindow.getView();
    //get the network object; this contains the graph
    CyNetwork network = cyWindow.getNetwork();
    //can't continue if either of these is null
    if (graphView == null || network == null) {return;}


    String callerID = "DualLayout.actionPerformed";
    network.beginActivity(callerID);
    //this is the graph structure; it should never be null,
    GraphPerspective graphPerspective = network.getGraphPerspective();


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

    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
    Iterator compatNodeIt = graphPerspective.nodesList().iterator();
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
      //have to remember the homologies here, map from
      //a node to a vector of associated nodes, will probably
      //hold off on adding the edges until after everything is layed
      //out to keep it simple
      /*Vector homologousNodes;
	if(node2NodeVec.get(leftNode) == null){
	node2NodeVec.put(leftNode,new Vector());
	}
	homologousNodes = (Vector)node2NodeVec.get(leftNode);
	homologousNodes.add(rightNode);
	
	if(node2NodeVec.get(rightNode) == null){
	node2NodeVec.put(rightNode,new Vector());
	}
	homologousNodes = (Vector)node2NodeVec.get(rightNode);
	homologousNodes.add(leftNode);
      */
      //Add in the homology edges here
      /*if(!newRoot.edgeExists(leftNode,rightNode)){
	Edge homologyEdge = newRoot.getEdge(newRoot.createEdge(leftNode,rightNode,false));
	String homologyName = ""+leftNode+" (hm) "+rightNode;
	homologyEdge.setIdentifier(homologyName);
	newEdgeAttributes.addNameMapping(homologyName,homologyEdge);
	newEdgeAttributes.add("interaction",homologyName,"hm");
	}*/
    }

    
    //for each edge in the compatability graph, split it into two edges
    //and add each of these edges to the new root graph 
    Iterator compatEdgeIt = graphPerspective.edgesList().iterator();
    GraphObjAttributes compatEdgeAttributes = network.getEdgeAttributes();
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
    CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(), new CyNetwork(newRoot,newNodeAttributes,newEdgeAttributes), DualLayout.NEW_TITLE);
    GraphView newView = newWindow.getView();
    HashSet leftNodeSet = new HashSet(left_name2node.values());
    HashSet rightNodeSet = new HashSet(right_name2node.values());
    SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter(newView,leftNodeSet,rightNodeSet,homologyPairSet);
    layouter.doLayout();
    /*
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
    */


    //initialize positions randomly
    /*
    initializePositions(newView,leftNodeViews);
    initializePositions(newView,rightNodeViews);
    
    double leftMovement = CUTOFF + 1;
    double rightMovement = CUTOFF + 1;
    double offset = 500;
    int iterations = 0;
    while(leftMovement > CUTOFF || rightMovement > CUTOFF){
      //keep moving until there is not much movement going on
      rightMovement = advancePositions(newView,leftNodeViews,node2NodeVec,true,offset);
      leftMovement = advancePositions(newView,rightNodeViews,node2NodeVec,false,offset);
      iterations++;
      if(iterations > MAX_ITERATIONS){
	increment -= COOLING_DECREMENT;
	System.out.println(""+iterations);
      }
    }
    */


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

    //now that the graph has been layed out, we can add in edges between homologous nodes
    /*
    if(parser.addEdges()){
      Iterator leftIt = left_name2node.values().iterator();
      GraphPerspective newPerspective = newWindow.getView().getGraphPerspective();	
      while(leftIt.hasNext()){
	Node leftNode = (Node)leftIt.next();
	Iterator rightIt = ((Vector)node2NodeVec.get(leftNode)).iterator();
	while(rightIt.hasNext()){
	  Node rightNode = (Node)rightIt.next();
	  Edge homologyEdge = newRoot.getEdge(newRoot.createEdge(leftNode,rightNode,false));
	  String homologyName = ""+leftNode+" (hm) "+rightNode;
	  homologyEdge.setIdentifier(homologyName);
	  newEdgeAttributes.addNameMapping(homologyName,homologyEdge);
	  newEdgeAttributes.add("interaction",homologyName,"hm");
	  newPerspective.restoreEdge(homologyEdge);
	}
      }
    }
    */
    
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
	GMLTree result = new GMLTree(newWindow);
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
    newWindow.showWindow();
    network.endActivity(callerID);
  }

  /*  private void initializePositions(GraphView graphView, Vector nodeViews){
    Iterator viewIt = nodeViews.iterator();
    Random rnd = new Random();
    while(viewIt.hasNext()){
      NodeView v = (NodeView)viewIt.next();
      v.setXPosition(500*rnd.nextDouble());
      v.setYPosition(500*rnd.nextDouble());
    }}
  */


  /**
   * Relaxation step. Moves all nodes a smidge. Returns the maximum amount a ndoe has moved.
   */
  public double advancePositions(GraphView graphView, Vector nodeViews,HashMap node2NodeVec, boolean left,double offset) {
    double maxMovement = 0;	
    for (Iterator iter = nodeViews.iterator();iter.hasNext();){
      NodeView v = ( NodeView ) iter.next();
      double xForce = 0;
      double yForce = 0;

      double xSpring = 0;
      double ySpring = 0;
      double xRepulsion = 0;
      double yRepulsion = 0;
      double xHomology = 0;
      double yHomology = 0;

      double thisX = v.getXPosition();

      double thisY = v.getYPosition();

      //here we are going to calculate the offset to use in determining
      //the spring force between homologous nodes
      if(left){
	offset = -offset;
      }

      int[] adjacent_nodes;
      // Get the spring force between all of its adjacent vertices.
      adjacent_nodes = graphView.getGraphPerspective().neighborsArray( v.getGraphPerspectiveIndex() );
      for( int i = 0; i < adjacent_nodes.length; ++i ) {
	NodeView adjacent_node_view = graphView.getNodeView( adjacent_nodes[i] );

	double xdiff = adjacent_node_view.getXPosition() - thisX;
	double ydiff = adjacent_node_view.getYPosition() - thisY;	

	if(xdiff == 0){
	  xdiff = SMALL_DISTANCE;
	}
	if(ydiff == 0){
	  ydiff = SMALL_DISTANCE;
	}

	double distance = Point2D.distance( 0, 0, xdiff,ydiff );
	distance -= SPRING_LENGTH;
	distance = Math.max(distance,0);

	double force = SPRING_STIFFNESS*distance;
	//calculate the magnitude of the x component
	double spring = force/Math.sqrt( ((ydiff*ydiff)/(xdiff*xdiff))+1);
	//calculate the direction of the x component
	if(xdiff < 0){
	  spring = -spring;
	}
	xSpring += spring;

	//calculate the magnitude of the y component
	spring = force/Math.sqrt(((xdiff*xdiff)/(ydiff*ydiff))+1);
	//calculate the direction of the y component
	if(ydiff < 0){
	  spring = -spring;
	}
	ySpring += spring;


      }


      // Get the electrical repulsion between all vertices,
      // including those that are not adjacent.
      for(Iterator ite = nodeViews.iterator();ite.hasNext(); ) {
	NodeView other_v = ( NodeView )ite.next();

	if( v == other_v )
	  continue;

	double xdiff = other_v.getXPosition()-thisX;
	double ydiff = other_v.getYPosition()-thisY;
	if(xdiff == 0){
	  xdiff = SMALL_DISTANCE;
	}
	if(ydiff == 0){
	  ydiff = SMALL_DISTANCE;
	}

	double distance = Point2D.distance( 0,0, xdiff, ydiff );
	double force = this.electricalRepulsion/(distance*distance);

	double repulsion = force/Math.sqrt(((ydiff*ydiff)/(xdiff*xdiff))+1);
	if(xdiff > 0){
	  repulsion = -repulsion;
	}
	xRepulsion += repulsion;

	repulsion = force/Math.sqrt(((xdiff*xdiff)/(ydiff*ydiff))+1);
	if(ydiff > 0){
	  repulsion = -repulsion;
	}
	yRepulsion += repulsion;
      }

      //if(!left){
      //Calculate the homology attraction force, here we calculate
      //an attractive force between each node and all of the nodes it has 
      //a homology with
      for(Iterator ite = ((Vector)node2NodeVec.get(v.getNode())).iterator();ite.hasNext();){
	NodeView other_v = graphView.getNodeView((Node)ite.next());
	double xdiff = other_v.getXPosition()-thisX;
	double ydiff = other_v.getYPosition()-thisY;
	if(xdiff == 0){
	  xdiff = SMALL_DISTANCE;
	}
	if(ydiff == 0){
	  ydiff = SMALL_DISTANCE;
	}

	double distance = Point2D.distance( 0,0, xdiff,ydiff );
	distance -= HOMOLOGY_LENGTH;
	distance = Math.max(distance,0);
	double force = HOMOLOGY_STIFFNESS*distance;

	//calculate the magnitude of the xcomponent
	double homology = force/Math.sqrt(((ydiff*ydiff)/(xdiff*xdiff))+1);
	//calculate the direction of the x component
	if(xdiff < 0){
	  homology = -homology;
	}
	xHomology += homology;

	//calculate the magnitude of the ycomponent
	homology = force/Math.sqrt(((xdiff*xdiff)/(ydiff*ydiff))+1);
	//calculate the direction of the y component
	if(ydiff < 0){
	  homology = -homology;
	}
	yHomology += homology;
      }
      //}
      // Combine the two to produce the total force exerted on the vertex.
      xForce = (xSpring + xRepulsion + xHomology);
      yForce = (ySpring + yRepulsion + yHomology);

      // Move the vertex in the direction of the force 
      double xadj =  ( xForce * this.increment );
      double yadj =  ( yForce * this.increment );
      xadj = muffleDistance(xadj); 
      yadj = muffleDistance(yadj);

      maxMovement = Math.max(maxMovement,Point2D.distance(0,0,xadj,yadj));

      double newX = thisX + xadj;
      double newY = thisY + yadj;

      v.setXPosition(newX);
      v.setYPosition(newY);

    }
    return maxMovement;
  }

  private double muffleDistance(double adj){
    if(adj < 0){
      return Math.max(adj,-MAX_DISTANCE);
    }
    return Math.min(adj,MAX_DISTANCE);
  }
  //split the name of a node in the compatability graph into the names of
  //the component nodes
  /*private String [] split(String s,String split){
    String [] result = new String [2];
    int index = s.indexOf(split);
    result[0] = s.substring(0,index);
    result[1] = s.substring(index + 1,s.length());
    return result;
    }*/
}


package csplugins.bmtest;

import java.util.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import cytoscape.AbstractPlugin;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraph;
//import metaNodeViewer.model.TopLevelMetaNodeModel;

public class BMTest extends AbstractPlugin {
    
  CyWindow cyWindow;
  
  /**
   * Constructor.
   */
  public BMTest (CyWindow cyWindow) {
    this.cyWindow = cyWindow;
    cyWindow.getCyMenus().getOperationsMenu().add(new AddNodesAction());
    cyWindow.getCyMenus().getOperationsMenu().add(new AddEdgesAction());
    cyWindow.getCyMenus().getOperationsMenu().add(new HideSelectedNodesAction());
    cyWindow.getCyMenus().getOperationsMenu().add(new HideSelectedNodesEdgesAction());
    cyWindow.getCyMenus().getOperationsMenu().add(new RestoreHiddenNodesAction());
    //cyWindow.getCyMenus().getOperationsMenu().add(new CollapseSelectedNodes());
    //cyWindow.getCyMenus().getOperationsMenu().add(new UncollapseSelectedNodes());
    cyWindow.getCyMenus().getOperationsMenu().add(new CreateMetaNode());
  }//BMTest

  public void createMetaNode (){
     
    //System.err.println("------------------------------------------------------");
    //System.err.println("In createMetaNode()");
    
    GraphView graphView = cyWindow.getView();
    if(graphView.getSelectedNodes().size() == 0){
      JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                    "Please select one or more nodes.");
      return;
    }
    CyNetwork cyNetwork = cyWindow.getNetwork();
    GraphPerspective mainGP = cyNetwork.getGraphPerspective();
    java.util.List selectedNVlist = graphView.getSelectedNodes();
    Iterator it = selectedNVlist.iterator();
    java.util.List selectedNodeIndices = new ArrayList();
    while(it.hasNext()){
      NodeView nodeView = (NodeView)it.next();
      int rgNodeIndex = mainGP.getRootGraphNodeIndex(nodeView.getGraphPerspectiveIndex());
      selectedNodeIndices.add(new Integer(rgNodeIndex));
      System.out.println("Selected node index = "+rgNodeIndex);
    }//while it
    int [] nodeIndices = new int [selectedNodeIndices.size()];
    for(int i = 0; i < selectedNodeIndices.size(); i++){
      nodeIndices[i] = ((Integer)selectedNodeIndices.get(i)).intValue();
    }//for i
    RootGraph rootGraph = mainGP.getRootGraph();
    int[] edgeIndices = mainGP.getConnectingEdgeIndicesArray(nodeIndices);
    for(int i = 0; i < edgeIndices.length; i++){
      if(edgeIndices[i] > 0){
        int rootEdgeIndex = mainGP.getRootGraphEdgeIndex(edgeIndices[i]);
        edgeIndices[i] = rootEdgeIndex;
      }
      System.out.println("Connecting edge index = " + edgeIndices[i]);
    }
    int rgParentNodeIndex = rootGraph.createNode(nodeIndices, edgeIndices);
    System.out.println("The newly created meta-node has index " + rgParentNodeIndex);
    // Check that the creation of the meta-node worked
    
    int [] childrenNodeIndices = rootGraph.getNodeMetaChildIndicesArray(rgParentNodeIndex);
    if(childrenNodeIndices.length != selectedNodeIndices.size()){
      System.out.println("ERROR: The number of children nodes of the meta-node is " + 
                         childrenNodeIndices.length + ", but the number of selected nodes was " +
                         selectedNodeIndices.size());
    }
    int [] childrenEdgeIndices = rootGraph.getEdgeMetaChildIndicesArray(rgParentNodeIndex);
    if(childrenEdgeIndices.length != edgeIndices.length){
      System.out.println("ERROR: The number of children edges of the meta-node is " + 
                         childrenEdgeIndices.length + 
                         ", but the number of edges between children is " + edgeIndices.length); 
    }
       
  }// createMetaNode method
  
  public class CreateMetaNode extends AbstractAction{
    public CreateMetaNode (){super("Create meta-node from selected nodes");}
    
    public void actionPerformed (ActionEvent event){
      BMTest.this.createMetaNode();
    }//actionPerformed
    
  }//class CreateMetaNode

  public class UncollapseSelectedNodes extends AbstractAction {
    public UncollapseSelectedNodes (){super("Uncollapse Selected Nodes");}
    public void actionPerformed (ActionEvent event){
      
      System.out.println("-------------------------------------------------------");
      System.out.println("In UncollapseSelectedNodes.actionPerformed()");
      GraphView graphView = cyWindow.getView();
      //put up a dialog if there are no selected nodes
      if (graphView.getSelectedNodes().size() == 0) {
        JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                      "Please select one or more nodes.");
        return;
      }
      
      CyNetwork cyNetwork = cyWindow.getNetwork();
      GraphPerspective mainGraphPerspective = cyNetwork.getGraphPerspective();
      // Get the selected nodes that are parent nodes
      java.util.List selectedNodeViewsList = graphView.getSelectedNodes();
      HashSet parentNodeViews = new HashSet();
      Iterator it = selectedNodeViewsList.iterator();
      while(it.hasNext()){
        NodeView nodeView = (NodeView)it.next();
        Node node = nodeView.getNode();
        int gpNodeIndex = nodeView.getGraphPerspectiveIndex();
        int [] childrenNodeIndices = mainGraphPerspective.getNodeMetaChildIndicesArray(gpNodeIndex);
        System.out.println("...done");
        if(childrenNodeIndices != null && childrenNodeIndices.length > 0){
          parentNodeViews.add(nodeView);
          System.out.println("Node " + node + " is a parent node.");
        }else{
          System.out.println("Node " + node + " is NOT a parent node.");
        }
      }//while it
      // Uncollapse
      String callerID = "UncollapseSelectedNodes.actionPerformed";
      cyNetwork.beginActivity(callerID);
      it = parentNodeViews.iterator();
      while(it.hasNext()){
        NodeView pNodeView = (NodeView)it.next();
        Node pNode = pNodeView.getNode();
        GraphPerspective childGP = pNode.getGraphPerspective();
        // Unhide the nodes and edges
        System.out.println("About to unhide nodes and edges in mainGraphPerspective...");
        java.util.List nodesList = childGP.nodesList();
        java.util.List edgesList = childGP.edgesList();
        System.err.println("-!-!-!-!");
        mainGraphPerspective.restoreNodes(nodesList);
        System.err.println("-!-!-!-!");
        // This call seems to fire two events, the first one hides the nodes connected
        // to the edges (told Rowan)
        mainGraphPerspective.restoreEdges(edgesList);
        System.err.println("-!-!-!-!");
        System.out.println("...done unhiding nodes and edges in mainGraphPerspective.");
        // Hide the parent node
        // THIS CRASHES. Rowan seems to know what the problem is, so I will wait for him.
        System.out.println("About to hide the parent node in mainGraphPerspective" + pNode + " ...");
        mainGraphPerspective.hideNode(pNode);
        System.out.println("...done hiding parent node");
      }//while it

      cyNetwork.endActivity(callerID);
      // Apply vizmaps
      cyWindow.redrawGraph();
    }//actionPerformed
  }//UncollapseSelectedNodes

  public class CollapseSelectedNodes extends AbstractAction {
    public CollapseSelectedNodes (){super("Collapse Selected Nodes");}
    public void actionPerformed (ActionEvent event){
      
      System.out.println("-------------------------------------------------------");
      System.out.println("In CollapseSelectedNodes.actionPerformed()");
      GraphView graphView = cyWindow.getView();
      //put up a dialog if there are no selected nodes
      if (graphView.getSelectedNodes().size() == 0) {
        JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                      "Please select one or more nodes.");
        return;
      }
      CyNetwork cyNetwork = cyWindow.getNetwork();
      GraphPerspective graphPerspective = cyNetwork.getGraphPerspective();
      java.util.List selectedNodesList = graphView.getSelectedNodes();
      int [] childNodesIndices = new int[selectedNodesList.size()];
      int [] childNodesRootIndices = new int[selectedNodesList.size()];
      Iterator it = selectedNodesList.iterator();
      int i = 0;
      while(it.hasNext()){
        NodeView nodeView = (NodeView)it.next();
        childNodesIndices[i] = nodeView.getGraphPerspectiveIndex();
        System.out.println("selected node graph perspective index = " + childNodesIndices[i]);
        childNodesRootIndices[i] = graphPerspective.getRootGraphNodeIndex(childNodesIndices[i]);
        System.out.println("selected node root index = " + childNodesRootIndices[i]);
        i++;
      }// for it
      System.out.println("Creating childGraphPerspective with " + childNodesIndices.length +
                         " nodes...");
      String callerID = "CollapseSelectedNodes.actionPerformed";
      cyNetwork.beginActivity(callerID);
      // Create the graphPerspective for the child nodes
      GraphPerspective childGraphPerspective = 
        graphPerspective.createGraphPerspective(childNodesIndices);
      System.out.println("Created the childGraphPerspective, which has " + 
                         childGraphPerspective.getNodeCount() + " nodes and " +
                         childGraphPerspective.getEdgeCount() + " edges.");

      // Create the parent node in the RootGraph
      RootGraph rootGraph = graphPerspective.getRootGraph();
      System.out.println("Creating the parentNode in root graph...");
      // THIS CRASHES:
      //int rgParentNodeIndex = rootGraph.createNode(childGraphPerspective);
      // THIS CRASHES TOO:
      //int rgParentNodeIndex = rootGraph.createNode(childNodesRootIndices, null);
      int rgParentNodeIndex = rootGraph.createNode();
      // THIS TOO:
      Node parentNode = rootGraph.getNode(rgParentNodeIndex);
      System.out.println("Calling parentNode.setGraphPerspective()...");
      parentNode.setGraphPerspective(childGraphPerspective);
      System.out.println("Created the parent node in root graph, with index = " + rgParentNodeIndex +
                         " and graphPerspective = " + parentNode.getGraphPerspective());
      
      // Restore the parent node in graphPerspective
      System.out.println("About to restore parentNode in graphPerspective...");
      if(graphPerspective.restoreNode(parentNode) == null){
        System.out.println("ERROR: could not restore node, graphPerspective thinks it was not hidden");
      }else{
        System.out.println("...done restoring parentNode.");
        // Set some visual atts so that I can see it on the screen
        GraphObjAttributes nodeAtts = cyNetwork.getNodeAttributes();
        nodeAtts.addNameMapping("PARENT_NODE", parentNode);
      }
      
      // Now do the collapsing
      // Assume that all I got for collapsing was the parentNode (for testing)
      GraphPerspective childGP = parentNode.getGraphPerspective();
      // Hide all nodes and edges in graphPerspective that are in childGP
      System.out.println("About to hide in graphPerspective nodes and edges in childGP...");
      java.util.List edgeList = childGP.edgesList();
      java.util.List nodeList = childGP.nodesList();
      graphPerspective.hideEdges(edgeList);
      graphPerspective.hideNodes(nodeList);
      System.out.println("...done hiding nodes and edges.");
      cyNetwork.endActivity(callerID);
      // Apply vizmaps
      cyWindow.redrawGraph();
    }//actionPerformed
  }//CollapseSelectedNodes
    
  public class AddEdgesAction extends AbstractAction {
    public AddEdgesAction() {super("Add clique edges between selected nodes");}

    public void actionPerformed (ActionEvent event){

      System.out.println("-------------------------------------------------------");
      System.err.println("In AddEdgesAction.actionPerformed()");
      
      GraphView graphView = cyWindow.getView();
      CyNetwork network = cyWindow.getNetwork();
      if(graphView == null || network == null){
        System.err.println("Oops! The graphView or the network is null");
        return;
      }
      
      String callerID = "AddEdgesAction.actionPerformed";
      network.beginActivity(callerID);
      
      //this is the graph structure; it should never be null,
      GraphPerspective graphPerspective = network.getGraphPerspective();
      if (graphPerspective == null){
        System.err.println("In " + callerID + ":");
        System.err.println("Unexpected null graph perspective in network");
        network.endActivity(callerID);
        return;
      }
      //and the view should be a view on this structure
      if(graphView.getGraphPerspective() != graphPerspective){
        System.err.println("In " + callerID + ":");
        System.err.println("graph view is not a view on network's graph perspective");
        network.endActivity(callerID);
        return;
      }

    
      RootGraph rootGraph = graphPerspective.getRootGraph();
      java.util.List nodeList = graphView.getSelectedNodes();
      NodeView [] selectedNodes = (NodeView[])nodeList.toArray(new NodeView[nodeList.size()]);
      for(int i = 0; i < selectedNodes.length; i++){
        Node sourceNode = selectedNodes[i].getNode();
        System.out.println("Selected node index in graphPerspective is " + 
                           selectedNodes[i].getGraphPerspectiveIndex());
        for(int j = i+1; j < selectedNodes.length; j++){
          Node targetNode = selectedNodes[j].getNode();
          int rrEdgeIndex = rootGraph.createEdge(sourceNode, targetNode, false); // not directed
          System.out.println("Created edge in root graph, root index = " + rrEdgeIndex);
          System.out.println("About to restore edge in graph perspective...");
          int gpEdgeIndex = graphPerspective.restoreEdge(rrEdgeIndex);
          System.out.println("Restored edge in graphPerspective, gp index = " + gpEdgeIndex);
        }//for j
      }//for i

      network.endActivity(callerID);
    }//actionEvent
  }//class AddEdgesAction
  
  /**
   * This class gets attached to the menu item.
   */
  public class AddNodesAction extends AbstractAction {
    
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public AddNodesAction () {super("Add new 100 Nodes");}
    
    /**
     * Gives a description of this plugin.
     */
    public String describe () {
      return "Adds nodes.";
    }
    
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed (ActionEvent ae) {

      System.out.println("-------------------------------------------------------");
      System.err.println("In AddNodesAction.actionPerformed()");
      
      GraphView graphView = cyWindow.getView();
      CyNetwork network = cyWindow.getNetwork();
      if(graphView == null || network == null){
        System.err.println("Oops! The graphView or the network is null");
        return;
      }
      //inform listeners that we're doing an operation on the network
      String callerID = "AddNodesAction.actionPerformed";
      network.beginActivity(callerID);
      
      //this is the graph structure; it should never be null,
      GraphPerspective graphPerspective = network.getGraphPerspective();
      if (graphPerspective == null){
        System.err.println("In " + callerID + ":");
        System.err.println("Unexpected null graph perspective in network");
        network.endActivity(callerID);
        return;
      }
      //and the view should be a view on this structure
      if(graphView.getGraphPerspective() != graphPerspective){
        System.err.println("In " + callerID + ":");
        System.err.println("graph view is not a view on network's graph perspective");
        network.endActivity(callerID);
        return;
      }
      
      RootGraph rootGraph = graphPerspective.getRootGraph();
      GraphObjAttributes nodeAtt = network.getNodeAttributes();
      System.out.println("Creating nodes...");
      // Create a bunch of nodes
      for(int i = 0; i < 100; i++){
        int rrIndex = rootGraph.createNode();
        //NOTE: THIS DOES NOT WORK!: (it should now)
        Node newNode = rootGraph.getNode(rrIndex);
        Node gpNode = graphPerspective.restoreNode(newNode);
        int gpIndex = graphPerspective.getIndex(gpNode);
        //int gpIndex = graphPerspective.restoreNode(rrIndex);
        //Node newNode = graphPerspective.getNode(gpIndex);
        if(newNode == null){
          System.out.println("The node that was just restored in graphPerspective with root index " +
                             rrIndex + " and perspective index " + gpIndex + " is null");
          
        }else{
          String canonicalName = Integer.toString(rrIndex);
          nodeAtt.addNameMapping(canonicalName, newNode);
          nodeAtt.set("commonName", canonicalName, canonicalName);
        }
        //BUG: Same index, gpIndex should not be negative.
        System.out.println("rootGraph node index = " + rrIndex + 
                           " graphPerspective node index = " + gpIndex);
      }//for i
      System.out.println("...done creating nodes.");

      //and tell listeners that we're done
      network.endActivity(callerID);
    }
  }// AddNodesAction

  Node [] hiddenSelectedNodes;
  Edge [] hiddenEdges;

   /**
   * This class gets attached to the menu item.
   */
  public class HideSelectedNodesAction extends AbstractAction {
    
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public HideSelectedNodesAction () {super("Hide Selected Nodes and connecting edges");}
    
    /**
     * Gives a description of this plugin.
     */
    public String describe () {
      return "Hides nodes.";
    }
    
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed (ActionEvent ae) {

      System.out.println("-------------------------------------------------------");
      System.err.println("In HideSelectedNodesAction.actionPerformed()");
      
      GraphView graphView = cyWindow.getView();
      CyNetwork network = cyWindow.getNetwork();
      if(graphView == null || network == null){
        System.err.println("Oops! The graphView or the network is null");
        return;
      }

      //put up a dialog if there are no selected nodes
      if (graphView.getSelectedNodes().size() == 0) {
        JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                      "Please select one or more nodes.");
      }
      
      //inform listeners that we're doing an operation on the network
      String callerID = "HideSelectedNodesAction.actionPerformed";
      network.beginActivity(callerID);
      
      //this is the graph structure; it should never be null,
      GraphPerspective graphPerspective = network.getGraphPerspective();
      if (graphPerspective == null){
        System.err.println("In " + callerID + ":");
        System.err.println("Unexpected null graph perspective in network");
        network.endActivity(callerID);
        return;
      }
      //and the view should be a view on this structure
      if(graphView.getGraphPerspective() != graphPerspective){
        System.err.println("In " + callerID + ":");
        System.err.println("graph view is not a view on network's graph perspective");
        network.endActivity(callerID);
        return;
      }

      System.out.println("Hiding selected nodes...");
      java.util.List nodeList = graphView.getSelectedNodes();
      NodeView [] selectedNodes = (NodeView[])nodeList.toArray(new NodeView[nodeList.size()]);
      int numHidden = 0;
      Set hiddenNodesSet = new HashSet();
      Set hiddenEdgesSet = new HashSet();
      // Hide selected nodes using hideNode(Node)
      for(int i = 0; i < selectedNodes.length; i++){
        boolean error = false;
        Node node = selectedNodes[i].getNode();
        System.out.println("Hiding node [" + node + "]...");
        java.util.List list = 
          graphPerspective.getAdjacentEdgesList(node,true,true,true); 
        if(graphPerspective.hideNode(node) == null){
          System.out.println("graphPerspective.hideNode("+node+") returned null");
          error = true;
        }else{
          numHidden++;
          hiddenNodesSet.add(node);
          // Also remember the connected edges that are hidden
          //java.util.List list = 
          //graphPerspective.getAdjacentEdgesList(node,true,true,true); 
          hiddenEdgesSet.addAll(list);
        }
        // see if it hid it
        // NOTE: This throws an exception:
        //if(graphPerspective.containsNode(node)){
        //System.out.println("... ERROR could not hide!!!");
        //error = true;
        //}else{
        //System.out.println("...done hiding.");
        //numHidden++;
        //}
        
        // Try hiding the node using hideNode(node_index)
        if(error){
          int gpNodeIndex = selectedNodes[i].getGraphPerspectiveIndex();
          System.out.println("Trying to hide with hideNode(" + gpNodeIndex + ")...");
          if(graphPerspective.hideNode(gpNodeIndex) == 0){
            System.out.println("...ERROR graphPerspective.hideNode("+gpNodeIndex+") returns 0");
          }else{
            System.out.println("...done hiding.");
            numHidden++;
          }
        }// if error
        
      }//for i
      System.out.println("...done, hid " + numHidden + 
                         " nodes from " + selectedNodes.length + " selected nodes, and " +
                         hiddenEdgesSet.size() + " connecting edges.");
      
      hiddenSelectedNodes = (Node[])hiddenNodesSet.toArray(new Node[hiddenNodesSet.size()]);
      hiddenEdges = (Edge[])hiddenEdgesSet.toArray(new Edge[hiddenEdgesSet.size()]);
      //and tell listeners that we're done
      network.endActivity(callerID);
    }
  }//HideSelectedNodesAction

   /**
   * This class gets attached to the menu item.
   */
  public class HideSelectedNodesEdgesAction extends AbstractAction {
    
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public HideSelectedNodesEdgesAction () {super("Hide connecting edges of selected nodes");}
    
    /**
     * Gives a description of this plugin.
     */
    public String describe () {
      return "Hides edges that connect selected nodes.";
    }
    
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed (ActionEvent ae) {
      
      System.out.println("-------------------------------------------------------");
      System.err.println("In HideSelectedNodesEdgesAction.actionPerformed()");
      
      GraphView graphView = cyWindow.getView();
      CyNetwork network = cyWindow.getNetwork();
      if(graphView == null || network == null){
        System.err.println("Oops! The graphView or the network is null");
        return;
      }

      //put up a dialog if there are no selected nodes
      if (graphView.getSelectedNodes().size() == 0) {
        JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                      "Please select one or more nodes.");
      }
      
      //inform listeners that we're doing an operation on the network
      String callerID = "HideSelectedNodesEdgesAction.actionPerformed";
      network.beginActivity(callerID);
      
      //this is the graph structure; it should never be null,
      GraphPerspective graphPerspective = network.getGraphPerspective();
      if (graphPerspective == null){
        System.err.println("In " + callerID + ":");
        System.err.println("Unexpected null graph perspective in network");
        network.endActivity(callerID);
        return;
      }
      //and the view should be a view on this structure
      if(graphView.getGraphPerspective() != graphPerspective){
        System.err.println("In " + callerID + ":");
        System.err.println("graph view is not a view on network's graph perspective");
        network.endActivity(callerID);
        return;
      }

      System.out.println("Hiding connecting edges...");
      java.util.List nodeList = graphView.getSelectedNodes();
      NodeView [] selectedNodes = (NodeView[])nodeList.toArray(new NodeView[nodeList.size()]);
      int numHidden = 0;
      Set hiddenEdgesSet = new HashSet();
      
      for(int i = 0; i < selectedNodes.length; i++){
        boolean error = false;
        Node node = selectedNodes[i].getNode();
        System.out.println("Selected node = " + node);
        java.util.List list = 
          graphPerspective.getAdjacentEdgesList(node,true,true,true); 
        Edge [] edges = (Edge[])list.toArray(new Edge[list.size()]);
        for(int j = 0; j < edges.length; j++){
          System.out.println("Hiding edge " + edges[j] + "...");
          graphPerspective.hideEdge(edges[j]);
          System.out.println("..done hiding");
          hiddenEdgesSet.add(edges[j]);
          numHidden++;
        }//for j
      }//for i
      System.out.println("...done, hid " + numHidden + " connecting edges.");
      
      hiddenEdges = (Edge[])hiddenEdgesSet.toArray(new Edge[hiddenEdgesSet.size()]);
      //and tell listeners that we're done
      network.endActivity(callerID);
    }
  }//HideSelectedNodesEdgesAction

  public class RestoreHiddenNodesAction extends AbstractAction {
     
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public RestoreHiddenNodesAction () {super("Unhide prev hidden nodes/edges");}
    
    /**
     * Gives a description of this plugin.
     */
    public String describe () {
      return "Unhides nodes.";
    }
    
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed (ActionEvent ae) {
      if(hiddenSelectedNodes == null && hiddenEdges == null){
        return;
      }
      System.out.println("-------------------------------------------------------");
      System.out.println("In RestoreHiddenNodesAction.actionPerformed()");
      
      GraphPerspective graphPerspective = cyWindow.getNetwork().getGraphPerspective();
      CyNetwork cyNetwork = cyWindow.getNetwork();
      String callerID = "RestoreHiddenNodesAction.actionPerformed";
      cyNetwork.beginActivity(callerID);
      for(int i = 0; hiddenSelectedNodes != null && i < hiddenSelectedNodes.length; i++){
        Node node = hiddenSelectedNodes[i];
        System.out.println("Restoring node " + node + "...");
        graphPerspective.restoreNode(node);
        System.out.println("..done");
      }//for i

      // Also restore the edges
      for(int i = 0; hiddenEdges != null && i < hiddenEdges.length; i++){
        Edge edge = hiddenEdges[i];
        //NOTE: This does not seem to fire an event:
        System.out.println("Restoring edge " + edge + "...");
        if(graphPerspective.restoreEdge(edge) == null){
          System.out.println("...ERROR graphPerspective.restoreEdge("+edge+"returned null");
        }
        System.out.println("...done");
      }//for i
      cyNetwork.endActivity(callerID);
      hiddenSelectedNodes = null;
      hiddenEdges = null;
    }//actionPerformed
  }//RestoreHiddenNodesAction
    

}//class BMTest


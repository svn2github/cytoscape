/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
/**
 * @author Iliana Avila-Campillo
 * @version %I%, %G%
 * @since 2.0
 */

package cytoscape.view;

import java.util.*;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.Edge;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.EdgeView;
import giny.view.NodeView;

/**
 * A basic <code>GraphViewHandler</code> that simply reflects <code>GraphPerspective</code>
 * changes on a given <code>GraphView</code>
 */

public class BasicGraphViewHandler implements GraphViewHandler {

  /**
   * Constructor
   */
  public BasicGraphViewHandler (){}//BasicGraphViewHandler
  
  /**
   * Handles the event as desired by updating the given <code>giny.view.GraphView</code>.
   *
   * @param event the event to handle
   * @param graph_view the <code>giny.view.GraphView</code> that views the 
   * <code>giny.model.GraphPerspective</code> that generated the event and that should
   * be updated as necessary
   */
  public void handleGraphPerspectiveEvent (GraphPerspectiveChangeEvent event, GraphView graph_view){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.handleGraphPerspectiveEvent().");
    
    int numTypes = 0; // An event may have more than one type
    
    // Node Events:
    if(event.isNodesHiddenType()){
      //TODO: Remove
      //System.out.println("isNodesHiddenType == " + event.isNodesHiddenType());
      // THIS CALL CRASHES:
      //hideGraphViewNodes(graph_view, event.getHiddenNodes());
      hideGraphViewNodes(graph_view, event.getHiddenNodeIndices());
      numTypes++;
    }

    if(event.isNodesRestoredType()){
      //TODO: Remove
      //System.out.println("isNodesRestoredType == " + event.isNodesRestoredType());
      restoreGraphViewNodes(graph_view, event.getRestoredNodeIndices(), true);
      numTypes++;
    }
    
    // A GraphPerspective cannot have selected graph objects (Rowan told me)
    //if(event.isNodesSelectedType()){
    //selectGraphViewNodes(graph_view, event.getSelectedNodes());
    //numTypes++;
    //}
    
    // Same as above
    //if(event.isNodesUnselectedType()){
    //unselectGraphViewNodes(graph_view, event.getUnselectedNodes());
    //numTypes++;
    //}
    
    // Edge events:
    if(event.isEdgesHiddenType()){
      //TODO: Remove
      //System.out.println("isEdgesHiddenType == " + event.isEdgesHiddenType());
      hideGraphViewEdges(graph_view, event.getHiddenEdgeIndices());
      numTypes++;
    }
    
    if(event.isEdgesRestoredType()){
      //TODO: Remove
      //System.out.println("isEdgesRestoredType == " + event.isEdgesRestoredType());
      restoreGraphViewEdges(graph_view, event.getRestoredEdgeIndices());
      numTypes++;
    }
    
    // A GraphPerspective cannot have selected graph objects (Rowan told me)
    //if(event.isEdgesSelectedType()){
    //selectGraphViewEdges(graph_view, event.getSelectedEdges());
    //numTypes++;
    //}
    
    // Same as above
    //if(event.isEdgesUnselectedType()){
    //unselectGraphViewEdges(graph_view, event.getUnselectedEdges());
    //numTypes++;
    //}
    
    if(numTypes == 0){
      //System.err.println("In BasicGraphViewHandler.handleGraphPerspectiveEvent, "
      //+ "unrecognized event type");
      return;
    }
    
    graph_view.updateView();
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.handleGraphPerspectiveEvent()." +
    //" numTypes caught = " + numTypes);
  }//handleGraphPerspectiveEvent

  /**
   * It hides the edges in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be hidden
   * @param edges the edges that will be hidden in <code>graph_view</code>
   * @return an array of edges that were hidden
   */
  //TESTED: Gets an exception because the edges array has references to null.
  //USE INSTEAD: hideGraphViewEdges(GraphView, int [])
  static public Edge []  hideGraphViewEdges (GraphView graph_view,
                                             Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.hideGraphViewEdges()");
    Set hiddenEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      // Gets an exception here:
      //TODO: Remove
      //System.out.println("About to call graph_view.getEdgeView("+edges[i]+"), i = " + i);
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      //TODO: Remove
      //System.out.println("Done calling graph_view.getEdgeView()");
      if(edgeView != null && graph_view.hideGraphObject(edgeView)){
        hiddenEdges.add(edges[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.hideGraphViewEdges()," + "num hidden edges = " + hiddenEdges.size());
    return (Edge[])hiddenEdges.toArray(new Edge[hiddenEdges.size()]);
  }//hideGraphViewEdges

   /**
   * It hides the edges in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be hidden
   * @param edge_indices the indices of the edges that will be hidden in <code>graph_view</code>
   * @return an array of edge indices that were hidden
   */
  // TESTED
  // NOTE: USE THIS INSTEAD OF hideGraphViewEdges (GraphView,Edge[])
  static public int []  hideGraphViewEdges (GraphView graph_view,
                                            int [] edge_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.hideGraphViewEdges()");
    Set hiddenEdges = new HashSet();
    for(int i = 0; i < edge_indices.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edge_indices[i]);
      if(edgeView != null && graph_view.hideGraphObject(edgeView)){
        hiddenEdges.add(new Integer(edge_indices[i]));
      }
    }//for i
    Integer [] IntArray = (Integer[])hiddenEdges.toArray(new Integer[hiddenEdges.size()]);
    int [] indicesArray = new int [hiddenEdges.size()];
    for(int i = 0; i < IntArray.length; i++){
      indicesArray[i] = IntArray[i].intValue();
    }
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.hideGraphViewEdges()," + "num hidden edges = " + indicesArray.length);
    return indicesArray;
  }//hideGraphViewEdges
  
  /**
   * It restores the edges in the array in the given <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be restored
   * @param edges the edges that will be restored
   * @return an array of edges that were restored
   */
  // TESTED
  static public Edge [] restoreGraphViewEdges (GraphView graph_view,
                                               Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewEdges()");
    Set restoredEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      boolean restored = false;
      if(edgeView == null){
        // This means that the restored edge had not been viewed before
        // by graph_view
        edgeView = graph_view.addEdgeView(edges[i].getRootGraphIndex());
        if(edgeView != null){
          restored = true;
        }
      }else{
        // This means that the restored edge had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show it
        restored = graph_view.showGraphObject(edgeView);
      }
      if(restored){
        restoredEdges.add(edgeView.getEdge());
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewEdges(), "+"num restored edges = " + restoredEdges.size() );
    return (Edge[])restoredEdges.toArray(new Edge[restoredEdges.size()]);
  }//restoreGraphViewEdges

  /**
   * It restores the edges with the given indices in the given <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be restored
   * @param edge_indices the indices of the edges that will be restored
   * @return an array of indices of edges that were restored
   */
  // TODO: What if a connected node is not in the graph view or graph perspective?
  static public int [] restoreGraphViewEdges (GraphView graph_view,
                                               int [] edge_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewEdges()");
    List restoredEdgeIndices = new ArrayList();
    for(int i = 0; i < edge_indices.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edge_indices[i]);
      boolean restored = false;
      if(edgeView == null){
        // This means that the restored edge had not been viewed before
        // by graph_view
        edgeView = graph_view.addEdgeView(edge_indices[i]);
        if(edgeView != null){
          restored = true;
        }
      }else{
        // This means that the restored edge had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show it
        restored = graph_view.showGraphObject(edgeView);
      }
      if(restored){
        restoredEdgeIndices.add(new Integer(edge_indices[i]));
      }
    }//for i
    
    int [] restoredEdgeIndicesArray = new int[restoredEdgeIndices.size()];
    for(int i = 0; i < restoredEdgeIndicesArray.length; i++){
      restoredEdgeIndicesArray[i] = ((Integer)restoredEdgeIndices.get(i)).intValue();
    }// for i
    
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewEdges(), "+"num restored edges = " + restoredEdgeIndices.size() );
    return restoredEdgeIndicesArray;
  }//restoreGraphViewEdges

  /**
   * It selects the edges in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be selected
   * @param edges the edges in <code>graph_view</code> that will be selected
   * @return the edges that were selected
   */
  static public Edge [] selectGraphViewEdges (GraphView graph_view,
                                              Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.selectGraphViewEdges()");
    Set selectedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      if(edgeView != null){
        edgeView.setSelected(true);
        selectedEdges.add(edges[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.selectGraphViewEdges()," +"num selected edges = " + selectedEdges.size());
    return (Edge[])selectedEdges.toArray(new Edge[selectedEdges.size()]);
  }//selectGraphViewEdges

  /**
   * It unselects the edges in the array in the given  <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which edges will be unselected
   * @param edges the edges that will be unselected in <code>graph_view</code>
   * @return an array of edges that were unselected
   */
  static public Edge[] unselectGraphViewEdges (GraphView graph_view,
                                               Edge [] edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.unselectGraphViewEdges()");
    Set unselectedEdges = new HashSet();
    for(int i = 0; i < edges.length; i++){
      EdgeView edgeView = graph_view.getEdgeView(edges[i]);
      if(edgeView != null){
        edgeView.setSelected(false);
        unselectedEdges.add(edges[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.unselectGraphViewEdges()," +"num unselected edges = " + unselectedEdges.size());
    return (Edge[])unselectedEdges.toArray(new Edge[unselectedEdges.size()]);
  }//unselectGraphViewEdges

  /**
   * It hides the nodes in the array in the given <code>giny.view.GraphView</code> object,
   * it also hides the connected edges to these nodes (an edge without a connecting node makes
   * no mathematical sense).
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be hidden
   * @param nodes the nodes that will be hidden in <code>graph_view</code>
   * @return an array of nodes that were hidden
   */
  // NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
  // and this hiding fires a hideEdgesEvent, so hideGraphViewEdges will get called on those
  // edges and we don't need to hide them in this method
  // TESTED
  static public Node[] hideGraphViewNodes (GraphView graph_view,
                                           Node [] nodes){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.hideGraphViewNodes()");
    Set hiddenNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      //TODO: Remove
      //System.out.println("About to call graph_view.getNodeView("+nodes[i]+"), i = " + i);
      // CRASHED HERE:
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null && graph_view.hideGraphObject(nodeView)){
        hiddenNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.hideGraphViewNodes(), " +"num hidden nodes = " + hiddenNodes.size());
    return (Node[])hiddenNodes.toArray(new Node[hiddenNodes.size()]);
  }//hideGraphViewNodes

   /**
   * It hides the nodes with the given indices that are contained in the given 
   * <code>giny.view.GraphView</code> object, it also hides the connected edges to 
   * these nodes (an edge without a connecting node makes no mathematical sense).
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be hidden
   * @param node_indices the indices of the nodes that will be hidden in <code>graph_view</code>
   * @return an array of indices of nodes that were hidden
   */
  // NOTE: GINY automatically hides the edges connected to the nodes in the GraphPerspective
  // and this hiding fires a hideEdgesEvent, so hideGraphViewEdges will get called on those
  // edges and we don't need to hide them in this method
  static public int[] hideGraphViewNodes (GraphView graph_view,
                                          int [] node_indices){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.hideGraphViewNodes()");
    List hiddenNodesIndices = new ArrayList();
    for(int i = 0; i < node_indices.length; i++){
      //TODO: Remove
      //System.out.println("About to call graph_view.getNodeView("+node_indices[i]+"), i = " + i);
      NodeView nodeView = graph_view.getNodeView(node_indices[i]);
      if(nodeView != null && graph_view.hideGraphObject(nodeView)){
        hiddenNodesIndices.add(new Integer(node_indices[i]));
      }
    }//for i
    int [] hiddenNodesIndicesArray = new int [hiddenNodesIndices.size()];
    for(int i = 0; i < hiddenNodesIndices.size(); i++){
      hiddenNodesIndicesArray[i] = ((Integer)hiddenNodesIndices.get(i)).intValue();
    }
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.hideGraphViewNodes(), " +"num hidden nodes = " + hiddenNodesIndicesArray.length);

    return hiddenNodesIndicesArray;
  }//hideGraphViewNodes
  
  /**
   * It restores the nodes in the array in the given <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be restored
   * @param nodes the nodes that will be restored in <code>graph_view</code>
   * @param restore_connected_edges whether or not the connected edges to the restored nodes
   * should also be restored or not (for now this argument is ignored)
   * @return an array of nodes that were restored
   */
  //TODO: Depending on restore_connected_edges, restore connected edges or not.
  //TESTED
  static public Node[] restoreGraphViewNodes (GraphView graph_view,
                                              Node [] nodes,
                                              boolean restore_connected_edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewNodes()");
    Set restoredNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      boolean restored = false;
      if(nodeView == null){
        // This means that the nodes that were restored had never been viewed by
        // the graph_view, so we need to create a new NodeView.
        nodeView = graph_view.addNodeView(nodes[i].getRootGraphIndex());
        if(nodeView != null){
          restored = true;
        }
      }else{
        // This means that the nodes that were restored had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show them
        restored = graph_view.showGraphObject(nodeView);
      }
      if(restored){
        positionToBarycenter(nodeView);
        restoredNodes.add(nodeView.getNode());
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewNodes()." +"Total restored nodes == " + restoredNodes.size());
    return (Node[])restoredNodes.toArray(new Node[restoredNodes.size()]);
  }//restoreGraphViewNodes

  /**
   * It restores the nodes with the given indices in the given <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be restored
   * @param node_indices the incides of the nodes that will be restored in <code>graph_view</code>
   * @param restore_connected_edges whether or not the connected edges to the restored nodes
   * should also be restored or not (for now this argument is ignored)
   * @return an array of indices of the nodes that were restored
   */
  //TODO: Depending on restore_connected_edges, restore connected edges or not.
  static public int[] restoreGraphViewNodes (GraphView graph_view,
                                              int [] node_indices,
                                              boolean restore_connected_edges){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.restoreGraphViewNodes()");
    List restoredNodeIndices = new ArrayList();
    for(int i = 0; i < node_indices.length; i++){
      
      NodeView nodeView = graph_view.getNodeView(node_indices[i]);
      boolean restored = false;
      if(nodeView == null){
        // This means that the nodes that were restored had never been viewed by
        // the graph_view, so we need to create a new NodeView.
        nodeView = graph_view.addNodeView(node_indices[i]);
        if(nodeView != null){
          restored = true;
        }
      }else{
        // This means that the nodes that were restored had been viewed by the graph_view
        // before, so all we need to do is tell the graph_view to re-show them
        restored = graph_view.showGraphObject(nodeView);
      }
      if(restored){
        restoredNodeIndices.add(new Integer(node_indices[i]));
        positionToBarycenter(nodeView);
        //TODO: Remove
        //System.err.println("NodeView for node index " + node_indices[i] + " was added to graph_view");
      }else{
        //TODO: Remove
        //System.err.println("ERROR: NodeView for node index " + node_indices[i] +" was NOT added to graph_view");
      }
    }//for i
    int [] restoredNodeIndicesArray = new int [restoredNodeIndices.size()];
    for(int i = 0; i < restoredNodeIndicesArray.length; i++){
      restoredNodeIndicesArray[i] = ((Integer)restoredNodeIndices.get(i)).intValue();
    }//for i
    
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.restoreGraphViewNodes()." +"Showed in graph_view/Restored in GP == " + restoredNodeIndicesArray.length +"/" + node_indices.length);
    return restoredNodeIndicesArray;
  }//restoreGraphViewNodes


  /**
   * It selects the nodes in the array in the given <code>giny.view.GraphView</code> object.
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be selected
   * @param nodes the nodes in <code>graph_view</code> that will be selected
   * @return the nodes that were selected
   */
  static public Node [] selectGraphViewNodes (GraphView graph_view,
                                              Node [] nodes){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.selectGraphViewNodes()"); 
    Set selectedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null){
        nodeView.setSelected(true);
        selectedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.selectGraphViewNodes(),"+"num selected nodes = " + selectedNodes.size()); 
    return (Node[])selectedNodes.toArray(new Node[selectedNodes.size()]);
  }//selectGraphViewNodes

  /**
   * It unselects the nodes in the array in the given  <code>giny.view.GraphView</code> object
   *
   * @param graph_view the <code>giny.view.GraphView</code> object in which nodes will be unselected
   * @param nodes the nodes that will be unselected in <code>graph_view</code>
   * @return an array of nodes that were unselected
   */
  static public Node[] unselectGraphViewNodes (GraphView graph_view,
                                               Node [] nodes){
    //TODO: Remove
    //System.out.println("In BasicGraphViewHandler.unselectGraphViewNodes()");
    Set unselectedNodes = new HashSet();
    for(int i = 0; i < nodes.length; i++){
      NodeView nodeView = graph_view.getNodeView(nodes[i]);
      if(nodeView != null){
        nodeView.setSelected(false);
        unselectedNodes.add(nodes[i]);
      }
    }//for i
    //TODO: Remove
    //System.out.println("Leaving BasicGraphViewHandler.unselectGraphViewNodes()," +"num unselected nodes = " + unselectedNodes.size());
    return (Node[])unselectedNodes.toArray(new Node[unselectedNodes.size()]);
  }//unselectGraphViewNodes

  /**
   * If the node that node_view represents is a meta-node, then it 
   * positions it at the barycenter of its viewable children nodes.
   *
   * @param node_view the <code>giny.view.NodeView</code> that will be positioned
   * to the barycenter of its children
   */
  static public void positionToBarycenter (NodeView node_view){
    Node node = node_view.getNode();
    int rootIndex = node.getRootGraphIndex();
    GraphView graphView = node_view.getGraphView();
    GraphPerspective gp = graphView.getGraphPerspective();
    
    int [] childrenNodeIndices = gp.getNodeMetaChildIndicesArray(rootIndex);
    if(childrenNodeIndices == null || childrenNodeIndices.length == 0){return;}
    
    GraphPerspective childGP = node.getGraphPerspective();
    if(childGP == null || childGP.getNodeCount() == 0){
      throw new IllegalStateException("Node " + node.getIdentifier() + " has a non-empty array " +
                                  " of children-node indices, but, it has no child GraphPerspective");
    }
    List childrenNodeList = childGP.nodesList();
    Iterator it = childrenNodeList.iterator();
    double x = 0.0;
    double y = 0.0;
    double viewableChildren = 0;
    while(it.hasNext()){
      Node childNode = (Node)it.next();
      if(gp.containsNode(childNode, false)){
        NodeView childNV = graphView.getNodeView(childNode.getRootGraphIndex());
        if(childNV != null){
          x += childNV.getXPosition();
          y += childNV.getYPosition();
          viewableChildren++;
        }
      }
    }//while it
    if(viewableChildren != 0){
      x /= viewableChildren;
      y /= viewableChildren;
      node_view.setXPosition(x);
      node_view.setYPosition(y);
    }
  }//positionToBarycenter

}//classs BasicGraphViewHandler

/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */

package metaNodeViewer.model;
import java.util.*;
import giny.model.*;

/**
 * This class makes the necessary modifications to a <code>giny.model.GraphPerspective</code>
 * so that it's top meta-node level is viewed.
 * A <code>giny.model.GraphPerspective</code> can have nodes that contain inside them another
 * <code>giny.model.GraphPerspective</code>. We refer to these nodes as "meta-nodes". The graph
 * can be viewed as a collection of trees (formally, a forest) each of which has levels.
 * The first level consists of the nodes that have no children. The second level consists of
 * the nodes that have children with 0 levels below them. The third level consists of nodes that have 
 * children with 1 level below them, and so on, until the last level. This model
 * only includes in the <code>GraphPerspective</code> the root nodes, and the edges of the root's 
 * children are connected to the root itself.
 */

public class TopLevelMetaNodeModel{
  
  /**
   * Modifies the given <code>giny.model.GraphPerspective</code> so that it's model
   * fits a top level model.
   * <p>
   * It adds to the <code>giny.model.RootGraph</code> new edges that are connected to 
   * meta-nodes.
   * These edges are infered from the meta-nodes' children edges. After adding edges, it
   * hides the nodes and edges that are in <code>graph_perspective</code> and that are
   * the meta-nodes' children.
   * </p>
   *
   * @param graph_perspective the <code>giny.model.GraphPerspective</code> to modify
   * @return true if the graph was modified, false otherwise (if for example, the graph
   * has no meta-nodes)
   * @see #switch(GraphPerspective)
   */
  static public boolean applyModel (GraphPerspective graph_perspective){
    Node [] metaNodes = getMetaNodes(graph_perspective);
    boolean modified = false;
    
    //TODO: Remove
    System.err.println("In TopLevelMetaNodeModel.applyModel(GraphPerspective), num meta-nodes is " +
                       metaNodes.length);
    
    for(int i = 0; i < metaNodes.length; i++){
      //TODO: Remove
      //System.err.println("metaNode = " + metaNodes[i]);
      modified = modified || applyModel(graph_perspective, metaNodes[i]);
    }//for metaNodes
    //TODO: Remove
    System.err.println("Leaving TopLevelMetaNodeModel.applyModel(GraphPerspective), modified == " +
                       modified);
    return modified;
  }//applyModel

  /**
   * Transforms the given meta-node to the top level model.
   *
   * @param graph_perspective the <code>giny.model.GraphPerspective</code> in which 
   * the meta-node resides and that will be modified to meet this model
   * @param meta_node the <code>giny.model.Node</code> that has a <code>GraphPerspective</code>
   * inside it and that will be modified to meet this model
   * @return true iff <code>graph_perspective</code> was modified
   */
  static public boolean applyModel (GraphPerspective graph_perspective,
                                    Node meta_node){
    //TODO: Remove
    System.err.println("In TopLevelMetaNodeModel.applyModel(GraphPerspective," + meta_node.getRootGraphIndex() + ")");
    
    //TODO: Check that the given Node is a meta-node
    
    int numNewEdges = 0;
    int numHiddenGraphObjs = 0;
    GraphPerspective childGP = meta_node.getGraphPerspective();
    
    //TODO: Remove
    // System.err.print("Restoring meta_node in graph_perspective...");
    //System.err.flush();
    
    // Unhide the meta-node if hidden in graph_perspective
    Node restoredMetaNode = graph_perspective.restoreNode(meta_node);
    
    //System.err.println("...done");
    
    // Add edges to the meta-node //
    //TODO: Remove
    //System.err.print("Adding edges to meta_node...");
    //System.err.flush();
    RootGraph rootGraph = graph_perspective.getRootGraph();
    List childrenNodes = childGP.nodesList();
    Iterator nodesit = childrenNodes.iterator();
    List edgesToRestore = new ArrayList();
    while(nodesit.hasNext()){
      Node node = (Node)nodesit.next();
      // Undirected edges
      List undirectedEdgesList = graph_perspective.getAdjacentEdgesList(node,true,false,false);
      //TODO: Remove
      //System.err.println("Node w/index " + node.getRootGraphIndex() + " has " +
      //                 undirectedEdgesList.size() + " undirected edges");
      Edge [] undirectedEdges = 
        (Edge[])undirectedEdgesList.toArray(new Edge[undirectedEdgesList.size()]);
      for(int j = 0; j < undirectedEdges.length; j++){
        Edge edge = undirectedEdges[j];
        if(childGP.containsEdge(edge, false)){
          // The edge is inside
          //TODO: Remove
          //System.err.println("childGP contains edge w/index " + edge.getRootGraphIndex());
          continue;
        }
        Node otherNode = null;
        if(edge.getTarget() == node){
          otherNode = edge.getSource();
        }else if(edge.getSource() == node){
          otherNode = edge.getTarget();
        }
        //TODO: Remove
        //System.err.println("otherNode index == " + otherNode.getRootGraphIndex());
        // Create an undirected edge from the metanode to otherNode
        int edgeIndex = rootGraph.createEdge(meta_node,otherNode,false);
        System.err.println("Edge w/index " + edgeIndex + " was created in rootgraph");
        //System.err.println("Restoring new edge in graph_perspective");
        //graph_perspective.restoreEdge(edgeIndex);
        edgesToRestore.add(new Integer(edgeIndex));
        numNewEdges++;
      }//for undirected edges

      // Outgoing edges
      List outgoingEdgesList = graph_perspective.getAdjacentEdgesList(node, false, false, true);
      //TODO: Remove
      //System.err.println("Node w/index " + node.getRootGraphIndex() + " has " + 
      //                 outgoingEdgesList.size() + " outgoing edges");
      Edge [] outgoingEdges = (Edge[])outgoingEdgesList.toArray(new Edge[outgoingEdgesList.size()]);
      for(int j = 0; j < outgoingEdges.length; j++){
        Edge edge = outgoingEdges[j];
        //System.err.println("Looking at edge w/index " + edge.getRootGraphIndex());
        if(childGP.containsEdge(edge, false) || undirectedEdgesList.contains(edge)){
          // This edge is inside, or it was already added
          //TODO: Remove
          //System.err.println("Edge w/index " + edge.getRootGraphIndex() + " is in childGP");
          continue;
        }
        //System.err.println("About to call edge.getTarget()...");
        Node toNode = edge.getTarget();
        //TODO: Remove
        //System.err.println("Edge w/index " + edge.getRootGraphIndex() +
        //                 " has target node w/index " + toNode.getRootGraphIndex());
        // Create a directed edge from the meta-node to the toNode
        // TODO: Remove
        int edgeIndex = rootGraph.createEdge(meta_node,toNode, true);
        System.err.println("Edge w/index " + edgeIndex + " created in rootGraph");
        numNewEdges++;
        //TODO : Remove
        //System.err.println("Restoring edge in graph_perspective...");
        //graph_perspective.restoreEdge(edgeIndex);
        edgesToRestore.add(new Integer(edgeIndex));
        //System.err.println("...done");
      }//for outgoing edges
      
      //System.err.println("Done with outgoing edges, numNewEdges == " + numNewEdges);
      // Ingoing edges
      List ingoingEdgesList = graph_perspective.getAdjacentEdgesList(node,false,true,false);
      //TODO: Remove
      //System.err.println("Node w/index " + node.getRootGraphIndex() + " has " +
      //                 ingoingEdgesList.size() + " ingoing edges");
      Edge [] ingoingEdges = (Edge[])ingoingEdgesList.toArray(new Edge[ingoingEdgesList.size()]);
      for(int j = 0; j < ingoingEdges.length; j++){
        Edge edge = ingoingEdges[j];
        if(childGP.containsEdge(edge, false) || undirectedEdgesList.contains(edge)){
          // The edge is inside 
          //TODO: Remove
          //System.err.println("childGP contains edge w/root index" + edge.getRootGraphIndex());
          continue;
        }
        Node fromNode = edge.getSource();
        // Create a directed edge from the fromNode to the meta-node
        int edgeIndex = rootGraph.createEdge(fromNode, meta_node, true);
        System.err.println("Edge w/index " + edgeIndex + " created in rootGraph");
        //System.err.println("Restoring new edge in graph_perspective");
        //graph_perspective.restoreEdge(edgeIndex);
        edgesToRestore.add(new Integer(edgeIndex));
        numNewEdges++;
      }//for ingoing edges
      //System.err.println("Done with ingoing edges, numNewEdges == " + numNewEdges);
    }//nodesit
    
    if(edgesToRestore.size() > 0){
      int [] newEdgeIndices = new int [edgesToRestore.size()];
      for(int i = 0; i < newEdgeIndices.length; i++){
        newEdgeIndices[i] = ((Integer)edgesToRestore.get(i)).intValue();
      }
      // One event gets fired
      int [] restoredEdges = graph_perspective.restoreEdges(newEdgeIndices);
      System.err.println("Restored " + restoredEdges.length + " in graph_perspective");
    }
    
    //TODO: Remove
    //System.err.println("...done");
    // Done adding edges to metanode //

    System.err.println("Done adding edges to metanode, total numNewEdges == " + numNewEdges);

    // Hide the nodes and edges that are in childGP
    //TODO: Remove
    //System.err.print("Hiding edges in graph_perspective...");
    //System.err.flush();
    List hiddenEdges = graph_perspective.hideEdges(childGP.edgesList());
    //TODO: Remove
    //System.err.println("...done");
    //System.err.print("Hiding nodes in graph_perspective...");
    //System.err.flush();
    List hiddenNodes = graph_perspective.hideNodes(childrenNodes);
    //TODO: Remove
    //System.err.println("...done");
    numHiddenGraphObjs += hiddenEdges.size();
    numHiddenGraphObjs += hiddenNodes.size();
  
    if(numNewEdges > 0 || numHiddenGraphObjs > 0 || restoredMetaNode != null){
      return true;
    }
    return false;
  }//applyMode
  
  /**
   * @return an array of <code>giny.model.Node</code> objects that are in
   * <code>graph_perspective</code>'s <code>RootGraph</code> and whose
   * children nodes are contained in <code>graph_perspective</code>, it
   * also includes meta-nodes that are contained in <code>graph_perspective</code>
   * and whose children nodes are contained in <code>RootGraph</code>.
   */
  static public Node[] getMetaNodes (GraphPerspective graph_perspective){
    
    RootGraph rootGraph = graph_perspective.getRootGraph();
    Iterator it = rootGraph.nodesIterator();
    HashSet metaNodeList = new HashSet();
    while(it.hasNext()){
      Node rootNode = (Node)it.next();
      int rootNodeIndex = rootNode.getRootGraphIndex();
      if(graph_perspective.containsNode(rootNode, false)){
        // Since the children nodes are hidden in the graph_perspective, we need to use rootGraph
        int [] childrenIndices = rootGraph.getNodeMetaChildIndicesArray(rootNodeIndex);
        if(childrenIndices == null || childrenIndices.length == 0){
          continue;
        }
        // This is a meta-node that is in the graph_perspective
        metaNodeList.add(rootNode);
      }else{
        // See if the children nodes of the meta_node are contained in graph_perspective
        int [] childrenIndices = rootGraph.getNodeMetaChildIndicesArray(rootNodeIndex);
        if(childrenIndices == null || childrenIndices.length == 0){
          continue;
        }
        int i;
        for(i = 0; i < childrenIndices.length; i++){
          Node childRootNode = rootGraph.getNode(childrenIndices[i]);
          if(!graph_perspective.containsNode(childRootNode, false)){
            break;
          }
        }//for i
        if(i == childrenIndices.length){
          metaNodeList.add(rootNode);
        }
      }//Else if the rootNode is not contained in the graph_perspective
    }//While it, for each node in the rootGraph
    
    return (Node[])metaNodeList.toArray(new Node[metaNodeList.size()]);
  }//getMetaNodes

  /**
   * This method brings back the given <code>giny.model.GraphPerspective</code> to the state
   * it was in before the previous call to {@link #applyModel(GraphPerspective)} 
   * iff <code>recurse</code> is false, if <code>recurse</code> is true, then it brings back
   * <code>graph_perspective</code> to its original state before any calls to <code>applyModel</code>
   * were made.
   * <p>
   * It removes from <code>giny.model.RootGraph</code> the edges connected to meta-nodes, and
   * unhides the meta-nodes children nodes and edges in <code>graph_perspective</code>.
   * </p>
   *
   * @param graph_perspective the <code>giny.model.GraphPerspective</code> to be modified
   * @param recurse whether meta-nodes inside meta-nodes should also be undone
   * @return true if the graph was modified, false otherwise
   * @see #undoModel(GraphPerspective, Node, boolean)
   * @see #switch()
   */
  static public boolean undoModel (GraphPerspective graph_perspective,
                                   boolean recurse){
    
    Node [] metaNodes = getMetaNodes(graph_perspective);
    boolean modified = false;
    
    //TODO: Remove
    System.err.println("In undoModel(GraphPerspective, boolean), total num meta-nodes == "+metaNodes.length);
    
    for(int i = 0; i < metaNodes.length; i++){
      modified = modified || undoModel(graph_perspective, metaNodes[i], recurse);
    }//for metanodes
    return modified;
  }//undoModel

  /**
   * It undos a call to {@link #applyModel(GraphPerspective, Node)} or 
   * {@link #applyModel(GraphPerspective)} for the given meta-node.
   *
   * @param graph_perspective the <code>GraphPerspective</code> in which <code>meta_node</code>
   * resides and that will be modified
   * @param meta_node the <code>Node</code> that contains a <code>GraphPerspective</code> inside
   * it and that was previously modified to feet this model
   * @param recurse whether or not the meta-nodes inside <code>meta_node</code> should also
   * be undone
   * @return true iff <code>graph_perspective</code> was modified
   */
  static public boolean undoModel (GraphPerspective graph_perspective,
                                   Node meta_node,
                                   boolean recurse){
    
    int rootIndex = meta_node.getRootGraphIndex();
    RootGraph rootGraph = graph_perspective.getRootGraph();
    int [] childrenIndices = rootGraph.getNodeMetaChildIndicesArray(rootIndex);
    if(childrenIndices == null || childrenIndices.length == 0){
      // This is not a metanode
      //TODO: Remove
      //System.err.println("Node " + meta_node.getIdentifier() + " is not a meta-node");
      return false;
    }
  
    GraphPerspective childGP = meta_node.getGraphPerspective();
    if(childGP == null || childGP.getNodeCount() == 0){
      throw new IllegalStateException("Node " + meta_node.getIdentifier() + " has a non-empty array " +
                                   " of children-node indices, but, it has no child GraphPerspective");
    }
    
    // Remove the edges connected to the metanode
    // NOTE: Sometimes returns an empty list, even though the node does have edges
    List edgesList = graph_perspective.getAdjacentEdgesList(meta_node,true, true, true);
    //TODO: Remove
    System.err.println("Got " + edgesList.size()+" adjacent edges to meta_node in graph_perspective");
    List removedEdges = rootGraph.removeEdges(edgesList);
    //TODO: Remove
    System.err.println("Removed " + removedEdges.size() + " edges from rootGraph");
    // Unhide the metanode's children nodes and edges (this also restores incident edges)
    List restoredNodes = graph_perspective.restoreNodes(childGP.nodesList(), true);
        
    // Hide the meta-node, leave it alone in rootGraph since it was there to begin with
    Node hiddenNode = graph_perspective.hideNode(meta_node);
    
    boolean recursivelyChanged = false;
    // Recurse
    if(recurse){
      Iterator it = restoredNodes.iterator();
      while(it.hasNext()){
        Node childNode = (Node)it.next();
        recursivelyChanged = recursivelyChanged || undoModel(graph_perspective,childNode,recurse);
      }//while it
    }

    if( (removedEdges.size() + restoredNodes.size()) > 0 || hiddenNode != null || recursivelyChanged){
      return true;
    }
    return false;
  }//undoModel
  
  /**
   * Switches the given <code>giny.model.GraphPerspective</code> from its current model state
   * to the opposite state. 
   * Before calling this method one call to {@link #applyModel(GraphPerspective)}
   * must have been made, otherwise it will have no effect.
   * <p>
   * If the graph's model is the top view model, then it will
   * switch the graph back to its non-model state, and, if the graph is in the non-model
   * state, then it will be given the top level model. Use this method after calling 
   * {@link #applyModel(GraphPerspective)}, and instead of {@link #applyModel(GraphPerspective)} 
   * and {@link #undoModel(GraphPerspective)} if you expect to switch between the two states 
   * several times, since this method does not permanently remove edges from the 
   * <code>giny.model.RootGraph</code>, it only hides them in the
   * <code>giny.model.GraphPerspective</code> so that they can be unhidden when switching again.
   * </p>
   *
   * @param graph_perspective the <code>giny.model.GraphPerspective</code> to be switched
   * @return true iff <code>graph_perspective</code> changed
   * @see #applyModel(GraphPerspective)
   */
  static public boolean switchState (GraphPerspective graph_perspective){
    Node [] metaNodes = getMetaNodes(graph_perspective);
    boolean modified = false;
    for(int i = 0; i < metaNodes.length; i++){
      modified = modified || switchState(graph_perspective, metaNodes[i]);
    }//for meta-nodes
    return modified;
  }//switchState
  
  /**
   * Switches the given <code>giny.model.Node</code> from its current model state to the
   * opposite state iff it is a meta-node (it contains a <code>giny.model.GraphPerspective</code>
   * inside it). A call to {@link #applyModel(GraphPerspective graph_perspective)} or 
   * {@link #applyModel(GraphPerspective graph_perspective, Node meta_node)} 
   * must have been made before this call.
   *
   * @param graph_perspective the <code>GraphPerspective</code> that will be modified
   * @param meta_node the <code>giny.model.Node</code> that will be switched
   * @return true iff <code>graph_perspective</code> was modified, false otherwise
   * @see #switchState(GraphPerspective graph_perspective)
   */
  static public boolean switchState (GraphPerspective graph_perspective,
                                     Node meta_node){
    int rootIndex = meta_node.getRootGraphIndex();
    int [] childrenIndices = graph_perspective.getNodeMetaChildIndicesArray(rootIndex);
    if(childrenIndices == null){
      // The "meta_node" is not a meta-node
      return false;
    }
    
    GraphPerspective childGP = meta_node.getGraphPerspective();
    
    boolean modifiedGraph = false;
    // If the meta_node is in graph_perspective, assume that the state
    // is top level model, otherwise, assume it is in non-model state
    if(graph_perspective.containsNode(meta_node)){
      // The node is in top level mode
      // Unhide the children
      List restoredNodes = graph_perspective.restoreNodes(childGP.nodesList());
      List restoredEdges = graph_perspective.restoreEdges(childGP.edgesList());
      // Hide the meta_node and its edges
      Node hiddenNode = graph_perspective.hideNode(meta_node);
      if( ( restoredNodes.size() + restoredEdges.size() ) > 0 ||
          hiddenNode != null){
        modifiedGraph = true;
      }
    }else{
      // Switch to top level model
      // Unhide the meta-node
      Node restoredNode = graph_perspective.restoreNode(meta_node);
      //TODO: Remove
      if(restoredNode == null){
        System.err.println("Oops! graph_perspective.restoreNode(meta_node) returned null");
      }
      // Unhide the meta-node's edges
      RootGraph rootGraph = graph_perspective.getRootGraph();
      int metaNodeIndex = rootGraph.getIndex(meta_node);
      int [] edgeIndices = rootGraph.getAdjacentEdgeIndicesArray(metaNodeIndex,
                                                                 true,
                                                                 true,
                                                                 true);
      // The indices are root indices, get the actual edges
      // TODO: Ask Rowan if this is the best way
      List edgeList = new ArrayList();
      for(int i = 0; i < edgeIndices.length; i++){
        edgeList.add(rootGraph.getEdge(edgeIndices[i]));
      }//for i
      List restoredEdges = graph_perspective.restoreEdges(edgeList);
      
      // Hide the meta-node's children in graph_perspective
      List hiddenEdges = graph_perspective.hideEdges(childGP.edgesList());
      List hiddenNodes = graph_perspective.hideNodes(childGP.nodesList());
    
      if(restoredNode != null ||
         (restoredEdges.size() + hiddenEdges.size() + hiddenNodes.size() ) > 0){
        modifiedGraph = true;
      }
    }
    
    return modifiedGraph;
  }//switchState
  
}//class TopLevelMetaNodeModel

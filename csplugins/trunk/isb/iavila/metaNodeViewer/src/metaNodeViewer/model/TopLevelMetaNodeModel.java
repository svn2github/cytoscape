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
    int [] metaNodeRootIndices = getMetaNodeIndices(graph_perspective);
    boolean modified = false;
    
    //TODO: Remove
    System.err.println("In TopLevelMetaNodeModel.applyModel(GraphPerspective), num meta-nodes is " +
                       metaNodeRootIndices.length);
    
    for(int i = 0; i < metaNodeRootIndices.length; i++){
      //TODO: Remove
      System.err.println("metaNode index = " + metaNodeRootIndices[i]);
      modified = modified || applyModel(graph_perspective, metaNodeRootIndices[i]);
    }//for metaNodes
    //TODO: Remove
    System.err.println("Leaving TopLevelMetaNodeModel.applyModel(GraphPerspective), modified == " +
                       modified);
    return modified;
  }//applyModel

  /**
   * Transforms the meta-node with the given RootGraph index to the top level model.
   *
   * @param graph_perspective the <code>giny.model.GraphPerspective</code> in which 
   * the meta-node resides and that will be modified to meet this model
   * @param meta_node_root_index the <code>RootGraph</code> index of the meta-node
   * @return true iff <code>graph_perspective</code> was modified
   */
  static public boolean applyModel (GraphPerspective graph_perspective,
                                    int meta_node_root_index){
    //TODO: Remove
    //System.err.println("In TopLevelMetaNodeModel.applyModel(GraphPerspective,"+meta_node_root_index);
    
    //TODO: Check that the given Node is a meta-node
           
    //TODO: Remove
    //System.err.print("Restoring meta_node in graph_perspective...");
    //System.err.flush();
    // Unhide the meta-node if hidden in graph_perspective
    int restoredMetaNodeRootIndex = graph_perspective.restoreNode(meta_node_root_index);
    //System.err.println("...done, restored node has index " + restoredMetaNodeRootIndex);
    
    // Add edges to the meta-node //
    //TODO: Remove
    //System.err.print("Adding edges to meta_node...");
    //System.err.flush();
    int numNewEdges = 0;
    int numHiddenGraphObjs = 0;
    RootGraph rootGraph = graph_perspective.getRootGraph();
    List edgeRootIndicesToRestore = new ArrayList();
    List seenChildEdgeIndices = new ArrayList();
    // These are GraphPerspective indices:
    int [] childrenNodeIndices = graph_perspective.getNodeMetaChildIndicesArray(meta_node_root_index); 
    for(int child_i = 0; child_i < childrenNodeIndices.length; child_i++){
      int childNodeRootIndex = graph_perspective.getRootGraphNodeIndex(childrenNodeIndices[child_i]);
      // Undirected edges (these are GraphPerspective indices)
      int[] undirectedEdgeIndices = 
        graph_perspective.getAdjacentEdgeIndicesArray(childNodeRootIndex,true, false, false);
      //TODO: Remove
      //System.err.println("Child node "+childNodeRootIndex+" has "+undirectedEdgeIndices.length+" undirected edges");
      for(int edge_i = 0; edge_i < undirectedEdgeIndices.length; edge_i++){
        int childEdgeIndex = undirectedEdgeIndices[edge_i];
        if(// THIS CRASHES:
           //graph_perspective.isEdgeMetaChild(
           //                                meta_node_root_index,
           //                                childEdgeIndex
           //                                )
           rootGraph.isEdgeMetaChild(
                                     meta_node_root_index,
                                     graph_perspective.getRootGraphEdgeIndex(childEdgeIndex)
                                     )
           
           ){
          // The edge is a child of the meta-node, so it should be hidden in graph_perspective
          //TODO: Remove
          //System.err.println("Child edge = " + undirectedEdgeIndices[edge_i]);
          continue;
        }
        int otherNodeRootIndex = 0;
        int targetRootIndex = 
         graph_perspective.getRootGraphNodeIndex(graph_perspective.getEdgeTargetIndex(childEdgeIndex));
        int sourceRootIndex = 
         graph_perspective.getRootGraphNodeIndex(graph_perspective.getEdgeSourceIndex(childEdgeIndex));
        if(targetRootIndex == childNodeRootIndex){
          otherNodeRootIndex = sourceRootIndex;
        }else if(sourceRootIndex == childNodeRootIndex){
          otherNodeRootIndex = targetRootIndex;
        }
        //TODO: Remove
        //System.err.println("otherNode == " + otherNodeRootIndex);
        // Create an undirected edge from the meta-node to other node
        int newEdgeRootIndex = 
          rootGraph.createEdge(meta_node_root_index,
                               otherNodeRootIndex,
                               false);
        //System.err.println("Edge w/index " + newEdgeRootIndex + " was created in rootgraph");
        edgeRootIndicesToRestore.add(new Integer(newEdgeRootIndex));
        seenChildEdgeIndices.add(new Integer(childEdgeIndex));
        numNewEdges++;
      }//for undirected edges

      // Outgoing edges (GraphPerspective indices)
      int [] outgoingEdgeIndices = 
        graph_perspective.getAdjacentEdgeIndicesArray(childNodeRootIndex,false,false,true);
      //TODO: Remove
      //System.err.println("Child node "+childNodeRootIndex+" has "+outgoingEdgeIndices.length+" outgoing edges");
      for(int edge_i = 0; edge_i < outgoingEdgeIndices.length; edge_i++){
        int childEdgeIndex = outgoingEdgeIndices[edge_i];
        Integer childEdgeIndexInteger = new Integer(childEdgeIndex);
        if(// THIS CRASHES:
           //graph_perspective.isEdgeMetaChild(
           //                                meta_node_root_index,
           //                                childEdgeIndex
           //                                )
           rootGraph.isEdgeMetaChild(
                                     meta_node_root_index,
                                     graph_perspective.getRootGraphEdgeIndex(childEdgeIndex)
                                     )
           
           ||
           seenChildEdgeIndices.contains(childEdgeIndexInteger)
           ){
          // This edge is a child of meta-node, or it was already added
          //TODO: Remove
          //System.err.println("Child edge = " + childEdgeIndex);
          continue;
        }
        int toNodeRootIndex =           
         graph_perspective.getRootGraphNodeIndex(graph_perspective.getEdgeTargetIndex(childEdgeIndex));
        //TODO: Remove
        //System.err.println("Child edge "+childEdgeIndex+" has target node " + toNodeRootIndex);
        // Create a directed edge from the meta-node to the toNodeRoot
        // TODO: Remove
        int newEdgeRootIndex = 
          rootGraph.createEdge(meta_node_root_index,
                               toNodeRootIndex, 
                               true);
        //System.err.println("New edge " + newEdgeRootIndex + " created in rootGraph");
        numNewEdges++;
        edgeRootIndicesToRestore.add(new Integer(newEdgeRootIndex));
        seenChildEdgeIndices.add(childEdgeIndexInteger);
      }//for outgoing edges
      
      //System.err.println("Done with outgoing edges, numNewEdges == " + numNewEdges);
      // Ingoing edges
      int [] ingoingEdgeIndices = 
         graph_perspective.getAdjacentEdgeIndicesArray(childNodeRootIndex,false,true,false);
      //TODO: Remove
      //System.err.println("Child node "+childNodeRootIndex+" has "+ingoingEdgeIndices.length+" ingoing edges");
      for(int edge_i = 0; edge_i < ingoingEdgeIndices.length; edge_i++){
        int childEdgeIndex = ingoingEdgeIndices[edge_i];
        Integer childEdgeIndexInteger = new Integer(childEdgeIndex);
        if(// THIS CRASHES:
           //graph_perspective.isEdgeMetaChild(
           //                                meta_node_root_index,
           //                                childEdgeIndex
           //                               )
           rootGraph.isEdgeMetaChild(
                                     meta_node_root_index,
                                     graph_perspective.getRootGraphEdgeIndex(childEdgeIndex)
                                     )
           ||
           seenChildEdgeIndices.contains(childEdgeIndexInteger)
           ){
          // This edge is a child of meta-node, or it was already added
          //TODO: Remove
          //System.err.println("Child edge = " + childEdgeIndex);
          continue;
        }
        int fromNodeRootIndex =
         graph_perspective.getRootGraphNodeIndex(graph_perspective.getEdgeSourceIndex(childEdgeIndex));
        // Create a directed edge from the from-node to the meta-node
        int newEdgeRootIndex = 
          rootGraph.createEdge(fromNodeRootIndex, 
                               meta_node_root_index, 
                               true);
        //System.err.println("New edge " + newEdgeRootIndex + " created in rootGraph");
        edgeRootIndicesToRestore.add(new Integer(newEdgeRootIndex));
        numNewEdges++;
      }//for ingoing edges
      //System.err.println("Done with ingoing edges, numNewEdges == " + numNewEdges);
    }//nodesit
    
    if(edgeRootIndicesToRestore.size() > 0){
      int [] newEdgeRootIndices = new int [edgeRootIndicesToRestore.size()];
      for(int i = 0; i < newEdgeRootIndices.length; i++){
        newEdgeRootIndices[i] = ((Integer)edgeRootIndicesToRestore.get(i)).intValue();
      }
      // One event gets fired
      int [] restoredEdges = graph_perspective.restoreEdges(newEdgeRootIndices);
      //System.err.println("Restored " + restoredEdges.length + " in graph_perspective");
    }//if there are new edges
    
    //TODO: Remove
    //System.err.println("...done");
    // Done adding edges to metanode //

    //System.err.println("Done adding edges to metanode, total numNewEdges == " + numNewEdges);

    // Hide the child nodes and edges
    //TODO: Remove
    //System.err.print("Hiding nodes in graph_perspective...");
    //System.err.flush();
    int [] hiddenNodes = graph_perspective.hideNodes(childrenNodeIndices);
    //TODO: Remove
    //System.err.println("...done, hid " + hiddenNodes.length + " nodes:");
    //for(int i = 0; i < hiddenNodes.length; i++){
      //System.err.println("Hidden node index = " + hiddenNodes[i]);
    //}
    numHiddenGraphObjs += hiddenNodes.length;
    //TODO: Remove, this happen to be RootGraph indices
    //int [] graphPerspectiveNodeIndices = graph_perspective.getNodeIndicesArray();
    //System.out.println("After removing the children nodes, these are the graph_perspective nodes:");
    //for(int i = 0; i < graphPerspectiveNodeIndices.length; i++){
    //System.out.println("Index = " + graphPerspectiveNodeIndices[i] + " " + 
    //                   graph_perspective.getNodeIndex(graphPerspectiveNodeIndices[i]));
    //}
  
    if(numNewEdges > 0 || numHiddenGraphObjs > 0 || restoredMetaNodeRootIndex != 0){
      return true;
    }
    return false;
  }//applyMode
  
  /**
   * @return an array of <code>giny.model.RootGraph</code> indices of nodes
   * that are meta-nodes. A meta-node is defined as a node that has children
   * nodes. meta-nodes that are in graph_perspective and whose children are
   * hidden in it, are included, as well as meta-nodes that are hidden in 
   * graph_perspective but whose children are not.
   */
  static public int [] getMetaNodeIndices (GraphPerspective graph_perspective){
    RootGraph rootGraph = graph_perspective.getRootGraph();
    int [] rgNodeIndices = rootGraph.getNodeIndicesArray();
    ArrayList metaNodeIndices = new ArrayList();
    for(int i = 0; i < rgNodeIndices.length; i++){
      int rootNodeIndex = rgNodeIndices[i];
      if(graph_perspective.getNodeIndex(rootNodeIndex) > 0){
        // Since the children nodes are hidden in the graph_perspective, we need to use rootGraph
        int [] childrenIndices = rootGraph.getNodeMetaChildIndicesArray(rootNodeIndex);
        if(childrenIndices == null || childrenIndices.length == 0){
          continue;
        }
        // This is a meta-node that is in the graph_perspective
        metaNodeIndices.add(new Integer(rootNodeIndex));
      }else{
        // See if the children nodes of the meta_node are contained in graph_perspective
        int [] childrenIndices = rootGraph.getNodeMetaChildIndicesArray(rootNodeIndex);
        if(childrenIndices == null || childrenIndices.length == 0){
          continue;
        }
        int j;
        for(j = 0; j < childrenIndices.length; j++){
          int gpNodeIndex = graph_perspective.getNodeIndex(childrenIndices[j]);
          if(gpNodeIndex == 0){
            break;
          }
        }//for j
        if(j == childrenIndices.length){
          metaNodeIndices.add(new Integer(rootNodeIndex));
        }
      }//Else if the rootNode is not contained in the graph_perspective
    }//For each node index in the rootGraph
    int [] indicesArray = new int [metaNodeIndices.size()];
    for(int i = 0; i < indicesArray.length; i++){
      indicesArray[i] = ((Integer)metaNodeIndices.get(i)).intValue();
    }
    return indicesArray;
  }//getMetaNodeIndices
  
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
    
    int [] metaNodeRootIndices = getMetaNodeIndices(graph_perspective);
    boolean modified = false;
    
    //TODO: Remove
    System.err.println("In undoModel(GraphPerspective, boolean), total num meta-nodes == "+metaNodeRootIndices.length);
    
    for(int i = 0; i < metaNodeRootIndices.length; i++){
      modified = modified || undoModel(graph_perspective, metaNodeRootIndices[i], recurse);
    }//for metanodes
    return modified;
  }//undoModel

  /**
   * It undos a call to {@link #applyModel(GraphPerspective, int)} or 
   * {@link #applyModel(GraphPerspective)} for the meta-node with the given index.
   *
   * @param graph_perspective the <code>GraphPerspective</code> in which the meta-node
   * resides and that will be modified
   * @param meta_node_root_index the <code>RootGraph</code> index of the meta-node
   * @param recurse whether or not the meta-nodes inside the meta-node should also
   * be undone
   * @return true iff <code>graph_perspective</code> was modified
   */
  static public boolean undoModel (GraphPerspective graph_perspective,
                                   int meta_node_root_index,
                                   boolean recurse){
    
    RootGraph rootGraph = graph_perspective.getRootGraph();
    int [] childrenIndices = rootGraph.getNodeMetaChildIndicesArray(meta_node_root_index);
    if(childrenIndices == null || childrenIndices.length == 0){
      // This is not a metanode
      //TODO: Remove
      //System.err.println("Node " + meta_node_root_index + " is not a meta-node");
      return false;
    }
    
    //TODO: Remove
    System.err.println("undoModel("+graph_perspective+","+meta_node_root_index+","+recurse+")");
    
    
    // Remove the edges connected to the metanode
    int [] edgeIndices = graph_perspective.getAdjacentEdgeIndicesArray(meta_node_root_index,
                                                                       true,true,true
                                                                       );
    for(int i = 0; i < edgeIndices.length; i++){
      edgeIndices[i] = graph_perspective.getRootGraphEdgeIndex(edgeIndices[i]);
    }// for i
    //TEST
    //graph_perspective.hideEdges(edgeIndices);
    //--
    //TODO: Remove
    System.err.println("Got "+edgeIndices.length+" adjacent edges to meta-node in graph_perspective");
    int[] removedEdgeIndices = rootGraph.removeEdges(edgeIndices);
    //TODO: Remove
    System.err.println("Removed "+removedEdgeIndices.length+" edges from rootGraph");
    // Unhide the metanode's children nodes and edges (this also restores incident edges)
    int[] restoredNodeIndices = graph_perspective.restoreNodes(childrenIndices, true);
    // TEST:
    // int [] rootGraphNodeIndices = rootGraph.getNodeIndicesArray();
//     ArrayList edgeIndicesToRestore = new ArrayList();
//     for(int i = 0; i < childrenIndices.length; i++){
//       for(int j = 0; j < rootGraphNodeIndices.length; j++){
//         int[] betweenEdges = rootGraph.getEdgeIndicesArray(childrenIndices[i],rootGraphNodeIndices[j],true);
//         if(betweenEdges != null && betweenEdges.length > 0){
//           for(int k = 0; k < betweenEdges.length; k++){
//             edgeIndicesToRestore.add(new Integer(betweenEdges[k]));
//           }// for k
//         }// if
//       }// for j
//     }// for i
//     if(edgeIndicesToRestore.size() == 0){
//       System.err.println("NO EDGES BETWEEN CHILDREN NODES.");
//     }
        
//     int [] edgeIndicesToRestoreArray = new int [edgeIndicesToRestore.size()];
//     for(int i = 0; i < edgeIndicesToRestoreArray.length; i++){
//       edgeIndicesToRestoreArray[i] = ((Integer)edgeIndicesToRestore.get(i)).intValue();
//     }
//     graph_perspective.restoreEdges(edgeIndicesToRestoreArray);
    
    //----
    // Hide the meta-node, leave it alone in rootGraph since it was there to begin with
    int hiddenNodeIndex = graph_perspective.hideNode(meta_node_root_index);
    
    boolean recursivelyChanged = false;
    // Recurse
    if(recurse){
      for(int i = 0; i < restoredNodeIndices.length; i++){
        recursivelyChanged = recursivelyChanged || undoModel(graph_perspective,
                                                             restoredNodeIndices[i],
                                                             recurse);
      }//for i
    }

    if( (removedEdgeIndices.length + restoredNodeIndices.length) > 0 
        || hiddenNodeIndex != 0 
        || recursivelyChanged){
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

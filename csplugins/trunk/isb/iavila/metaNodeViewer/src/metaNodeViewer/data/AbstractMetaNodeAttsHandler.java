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
 * Assigns attributes and their values to meta-nodes:
 * 1. Common name = member with highest intra-connections
 * 2. Area = proportional to number of members
 * 3. Edges = same as member edges
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer.data;

import java.util.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cern.colt.map.*;
import giny.model.*;
import giny.view.*;

//implements MetaNodeAttributesHandler {
public class AbstractMetaNodeAttsHandler extends SimpleMetaNodeAttributesHandler {
  public static final boolean DEBUG = true;
  
  /**
   * Constructor.
   */
  public AbstractMetaNodeAttsHandler (){}//AbstractMetaNodeAttsHandler
  
  /**
   * Assigns a canonical name and a common name to the given node. The common name
   * is the same as the child node with highest number of connections to other
   * children. The canonical name is "MetaNode_<abs(node root index)>".
   *
   * @param cy_net the CyNetwork that contains the GraphObjAttributes for nodes and
   * the RootGraph that contains the given node
   * @param metanode_root_index the RootGraph index of the meta-node for which a name
   * will be set
   * @return the name, or null if something went wrong
   */
  public String assignName (CyNetwork cy_net, int metanode_root_index){
    // Check arguments
    if(cy_net == null){
      return null;
    }
    int metaNodeRindex = metanode_root_index;
    if(metaNodeRindex > 0){
      // Not a RootGraph index
      metaNodeRindex = cy_net.getRootGraphNodeIndex(metanode_root_index);
    }
    if(metaNodeRindex == 0){
      // The node is hidden
      throw new IllegalArgumentException ("metanode_root_index = " + metanode_root_index);
    }
    
    String uniqueName = createMetaNodeUI(metaNodeRindex);
    String alias = createMetaNodeAlias(cy_net,metaNodeRindex);
    Node node = Cytoscape.getRootGraph().getNode(metaNodeRindex);
    if(DEBUG){
      System.err.println("Node for root index " + metaNodeRindex + " is " + node);
    }
    Cytoscape.getNodeNetworkData().addNameMapping(uniqueName,node); 
    cy_net.setNodeAttributeValue(node,Semantics.COMMON_NAME,alias);
    if(DEBUG){
      System.err.println("meta-node " + metaNodeRindex + " canonical name = " + uniqueName 
                        + " common name = " + alias);
    }
    return uniqueName;
  }//assignName

  /**
   * @return a String with the form "MetaNode_<abs(metanode_root_index)>".
   */
  protected String createMetaNodeUI (int metanode_root_index){
    return "MetaNode_" + Integer.toString( (metanode_root_index*-1) );
  }//createMetaNodeUI

  /**
   * @return the alias (common-name) for the given meta-node
   */
  protected String createMetaNodeAlias (CyNetwork cy_net, int metanode_root_index){
    OpenIntIntHashMap intradegrees = countIntraDegrees(cy_net,metanode_root_index);
    // TODO: I know the 3rd arg. is true, becasue otherwise there would have been an
    // exception thrown in countIntraDegrees. FIX THIS.
    SortedSet sortedNodes = new TreeSet(new IntraDegreeComparator(cy_net,intradegrees,true));
    RootGraph rootGraph = cy_net.getRootGraph();
    int[] children = rootGraph.getNodeMetaChildIndicesArray(metanode_root_index);
    for(int i = 0; i < children.length; i++){
      // Maybe I need to get the network index...
      CyNode node = (CyNode)cy_net.getNode(children[i]);
      sortedNodes.add(node);
      cy_net.setNodeAttributeValue(node,
                                    "intra degree", 
                                    new Integer(intradegrees.get(children[i]))); 
    }//for i
    CyNode highestNode = (CyNode)sortedNodes.first();
    String alias = (String)cy_net.getNodeAttributeValue(highestNode,Semantics.COMMON_NAME);
    if(alias == null){
      alias = (String)cy_net.getNodeAttributeValue(highestNode,Semantics.CANONICAL_NAME);
    }
    return alias;
  }//createMetaNodeAlias
  
  /**
   * For each child node of meta-node with index metanode_root_index it counts
   * how many edges it has to other children of that metanode, and puts it in a
   * map where the key is the child-node's <code>RootGraph</code> index, and the
   * value is its intra-degree.
   *
   * @return a <code>cern.colt.map.OpenIntIntHashMap</code>, keys are <code>RootGraph</code>
   * indices, and values are intra-degrees
   */
  protected OpenIntIntHashMap countIntraDegrees (CyNetwork network, int metanode_root_index){
    if(DEBUG){
      System.err.println("---------- countIntraDegrees(network,"
                         +metanode_root_index+") ----------");
    }

    OpenIntIntHashMap nodeToDegree = new OpenIntIntHashMap();
    RootGraph rootGraph = network.getRootGraph();
    int[] children = rootGraph.getNodeMetaChildIndicesArray(metanode_root_index);
    if(children == null || children.length == 0){
      // No children
      if(DEBUG){
        System.err.println("AbstractMetaNodeAttsHandler.countIntraDegrees(): metanode " +
                           metanode_root_index + " has no children.");
      }
      return nodeToDegree;
    }
    // We have two situations:
    // 1. The children nodes are in network
    // 2. The children nodes are not in the network, but they are in RootGraph
    // We need to take two different approaches. If the children are in network, then
    // count the edges between them that are in network. If the children are not in network
    // then count the edges between them that are in RootGraph.
    boolean childrenAreInNetwork = nodesAreInNetwork(network,children);
    int[] connectingEdges = null; 
    if(childrenAreInNetwork){
      connectingEdges = network.getConnectingEdgeIndicesArray(children);
    }else{
      // TODO: Take care of this later, RootGraph does not have getConnectingEdgeIndicesArray
      // method (talk to Rowan???)
      throw new IllegalStateException ("The children of metanode " + 
                                       metanode_root_index + 
                                       " are not in network, not implemented solution.");
    }
    if(connectingEdges == null || connectingEdges.length == 0){
      // No edges between the nodes
      if(DEBUG){
        System.err.println("The children of " + metanode_root_index + 
                           " have no edges between them.");
      }
      return nodeToDegree;
    }
    
    for(int i = 0; i < connectingEdges.length; i++){
      int edgeIndex = connectingEdges[i];
      boolean directed = network.isEdgeDirected(edgeIndex);
      int sourceIndex = network.getEdgeSourceIndex(edgeIndex);
      int targetIndex = network.getEdgeTargetIndex(edgeIndex);
      sourceIndex = network.getRootGraphNodeIndex(sourceIndex);
      targetIndex = network.getRootGraphNodeIndex(targetIndex);
      //if(DEBUG){
      //System.err.println("sourceIndex = " + sourceIndex + " targetIndex = " + targetIndex);
      //}
      int degree = nodeToDegree.get(sourceIndex);
      nodeToDegree.put(sourceIndex,degree+1);
      degree = nodeToDegree.get(targetIndex);
      nodeToDegree.put(targetIndex,degree+1);
    }//for i
    //if(DEBUG){
    //for(int i = 0; i < children.length; i++){
    //  int rindex = network.getRootGraphNodeIndex(children[i]);
    //  int degree = nodeToDegree.get(rindex);
    //  System.err.println("rindex = " + rindex + " degree = " + degree);
    //}
    //}
    return nodeToDegree;
  }//countIntraDegrees

  /**
   * @returns true if *all* of the nodes in the array are contained in the network,
   * false otherwise
   */
  protected boolean nodesAreInNetwork (CyNetwork network, int [] nodes){
    for(int i = 0; i < nodes.length; i++){
      if(nodes[i] < 0){
        if(network.getNodeIndex(nodes[i]) == 0){
          return false;
        }
      }else if(nodes[i] > network.getNodeCount()){
        return false;
      }else if (nodes[i] == 0){
        return false;
      }
    }//for i
    
    return true;
  }//nodesAreInNetwork
  
  /**
   * Sets the node and edge attributes of the meta-node with the given RootGraph index
   * and assigns a unique name to it in the GraphObjAttributes for nodes
   *
   * @param cy_network the CyNetwork that contains the GraphPerspective that contains
   * the children nodes of the meta-node, and that contains the GraphObjAttributes that 
   * contain the node and edge attributes
   * @param metanode_root_index the RootGraph index of the meta-node for which attributes
   * will be set
   * @param children_nodes_root_indeces the RootGraph indices of the children nodes from
   * which node attributes will be transfered to the meta-node
   * @param meta_edge_to_child_edge maps a meta-edge (edge connected to the meta-node)
   * RootGraph index to a child edge (edge that connects a child of the meta-node to another
   * node) RootGraph index, so that classes implementing this interface know which child edge
   * corresponds to which meta-edge
   * @return true if all went well, false if there was an error
   */
  public boolean setAttributes (CyNetwork cy_network, 
                                int metanode_root_index,
                                int [] children_nodes_root_indices,
                                AbstractIntIntMap meta_edge_to_child_edge){
    boolean nodesOk = setNodeAttributes(cy_network,
                                        metanode_root_index,
                                        children_nodes_root_indices);
    boolean edgesOk = setEdgeAttributes(cy_network,
                                        metanode_root_index,
                                        meta_edge_to_child_edge);
    return nodesOk && edgesOk;
  }//setAttributes
  
  /**
   * Sets the node attributes of the meta-node with the given RootGraph index 
   * and assigns a unique name to it in the GraphObjAttributes for nodes
   *
   * @param cy_network the CyNetwork that contains the GraphPerspective that contains
   * the children nodes of the meta-node, and that contains the GraphObjAttributes that 
   * contain the node and edge attributes
   * @param metanode_root_index the RootGraph index of the meta-node for which attributes
   * will be set
   * @param children_nodes_root_indeces the RootGraph indices of the children nodes from
   * which node attributes will be transfered to the meta-node
   * @return true if all went well, false if there was an error
   */
  public boolean setNodeAttributes (CyNetwork cy_network, 
                                    int metanode_root_index,
                                    int [] children_nodes_root_indices){
    
    if(children_nodes_root_indices == null || cy_network == null){
      return false;
    }
    // Set the 'nodeType' attribute to 'metaNode'
    Node node = Cytoscape.getRootGraph().getNode(metanode_root_index);
    cy_network.setNodeAttributeValue(node,
                                     "nodeType",
                                     "metaNode");
    
    // Set the node-height and node-width attributes so that the area
    // of the meta-node is proportional to the number of members within it
    double area = 0;
    CyNetworkView netView = Cytoscape.getNetworkView(cy_network.getIdentifier());
    if(netView == null){
      if(DEBUG){
        System.err.println("AbstractMetaNodeAttsHandler.setNodeAttributes(): netView is null");
      }
      return false;
    }
    for(int i = 0; i < children_nodes_root_indices.length; i++){
      NodeView nodeView = netView.getNodeView(children_nodes_root_indices[i]);
      if(nodeView == null){
        if(DEBUG){
          System.err.println("Node with index " + children_nodes_root_indices[i] +
                             " does not have a NodeView");
        }
        continue;
      }
      double height = nodeView.getHeight();
      double width = nodeView.getWidth();
      area = area + height * width;
    }//for i
    if(area == 0){
      // none of the nodes have a view?
      if(DEBUG){
        System.err.println("The area is 0.");
      }
      return false;
    }
    // NOTE: This assumes a circular shape for meta-nodes.
    double diameter = 2 * Math.sqrt(area/Math.PI);
    cy_network.setNodeAttributeValue(node,
                                     NodeAppearanceCalculator.nodeWidthBypass,
                                     new Double(diameter));
    cy_network.setNodeAttributeValue(node,
                                     NodeAppearanceCalculator.nodeHeightBypass,
                                     new Double(diameter));
    return true;
  }//setNodeAttributes

  /**
   * Sets the edge attributes of the meta-node with the given RootGraph index and assigns
   * to them unique names in the GraphObjAttributes for edges
   *
   * @param cy_network the CyNetwork that contains the GraphPerspective that contains
   * the children nodes of the meta-node, and the GraphObjAttributes that contain the edge
   * attributes
   * @param metanode_root_index the RootGraph index of the meta-node for which node attributes
   * will be set
   * @param meta_edge_to_child_edge maps a meta-edge (edge connected to the meta-node)
   * RootGraph index to a child edge (edge that connects a child of the meta-node to another
   * node) RootGraph index, so that classes implementing this interface know which child edge
   * corresponds to which meta-edge
   * @return true if all went well, false if there was an error
   */
  //public boolean setEdgeAttributes (CyNetwork cy_network, 
  //                                int metanode_root_index,
  //                                AbstractIntIntMap meta_edge_to_child_edge){
    
  //return true;
  //}//setEdgeAttributes

  /**
   * Removes the Node object identified by the given RootGraph index from the
   * GraphObjAttributes for nodes contained in the given CyNetwork as well as
   * its meta-edges from the GraphObjAttributes for edges contained in CyNetwork
   *
   * @param cy_network the CyNetwork that contains the GraphObjAttributes for nodes
   * and edges from which the meta-node and meta-edges will be removed
   * @param metanode_root_index the RootGraph index of the meta-node that will be
   * removed from the GraphObjAttributes
   * @param meta_edge_root_indices the RootGraph indices of the edges connected
   * to the meta-node that should be removed from the GraphObjAttributes for edges
   * @return true if all went well, false otherwise
   */
  public boolean removeFromAttributes (CyNetwork cy_network,
                                       int metanode_root_index,
                                       int [] meta_edge_root_indices){
    return false;
  }//removeFromAttributes

  /**
   * Remove from the GraphObjAttributes for edges in CyNetwork the edges in
   * the given array, but don't change the node attributes for metanode_root_index
   *
   * @param cy_network the CyNetwork that contains the GraphObjAttributes for
   * edges from which the  meta-edges will be removed
   * @param metanode_root_index the RootGraph index of the meta-node to which the
   * meta-edges are connected
   * @param meta_edge_root_indices the RootGraph indices of the edges connected
   * to the meta-node that should be removed from the GraphObjAttributes for edges
   * @return true if all went well, false otherwise
   */
  public boolean removeMetaEdgesFromAttributes (CyNetwork cy_network,
                                                int metanode_root_index,
                                                int [] meta_edge_root_indices){
    return false;
  }//removeMetaEdgesFromAttributes

  /**
   * Class used to compare CyNodes and sort them.
   */
  class IntraDegreeComparator implements Comparator{
    
    CyNetwork network;
    OpenIntIntHashMap intraDegrees;
    boolean nodesAreInNetwork;
    
    public IntraDegreeComparator (CyNetwork cy_net, 
                                  OpenIntIntHashMap intra_degrees,
                                  boolean nodes_in_network){
      this.network = cy_net;
      this.intraDegrees = intra_degrees;
      this.nodesAreInNetwork = nodes_in_network;
    }//IntraDegreeComparator

    public int compare (Object object1, Object object2){
    
      if(object1 == object2){
        return 0;
      }
      
      CyNode node1, node2;
      String canonical1, canonical2;
      int rindex1, rindex2;
      RootGraph rootGraph = this.network.getRootGraph();
      if(object1 instanceof CyNode && object2 instanceof CyNode){
        node1 = (CyNode)object1;
        node2 = (CyNode)object2;
        canonical1 = (String)this.network.getNodeAttributeValue(node1,Semantics.CANONICAL_NAME);
        canonical2 = (String)this.network.getNodeAttributeValue(node2,Semantics.CANONICAL_NAME);
        rindex1 = rootGraph.getIndex(node1);
        rindex2 = rootGraph.getIndex(node2);
        if(rindex1 == rindex2){
          //System.out.println((CyNode)object1 + " and " + (CyNode)object2 + 
          //" are equal because they have the same RootGraph index");
          return 0;
        }
      }else{
        throw new IllegalStateException("The given objects are not instances of CyNode.");
        //return -11;
      }
            
      int degree1 = this.intraDegrees.get(rindex1);
      int degree2 = this.intraDegrees.get(rindex2);
      
      if(degree1 > degree2){
        return -1;
      }

      if(degree1 < degree2){
        return 1;
      }
      
      // The intra-degrees are the same.
      // First tie breaker: If one of these is a metabolite, 
      // and the other is a protein with a common name, then the protein wins.
      Object o1 = this.network.getNodeAttributeValue(node1,"nodeType");
      Object o2 = this.network.getNodeAttributeValue(node2,"nodeType");
      String node1MType = null;
      String node2MType = null;
      
      if(o1 != null && o2 != null){
        node1MType = (String)o1;
        node2MType = (String)o2;
        if(node1MType.equals("metabolite") && node2MType.equals("protein")){
          String commonName = null ;
          Object cn =  this.network.getNodeAttributeValue(node2,Semantics.COMMON_NAME);
          if(cn != null){
            commonName = (String)cn;
          }
          if( commonName != null && !commonName.equals(canonical2) ){
            // the second node is a protein and has a common name, so it wins
            return 1;
          }else{
            // the second node is a protein but does not have a common name, so it looses
            return -1;
          }
        }else if(node1MType.equals("protein") && node2MType.equals("metabolite")){
          String commonName = null;
          Object cn =  this.network.getNodeAttributeValue(node1,Semantics.COMMON_NAME);
          if(cn != null){
            commonName = (String)cn;
          }
          if( commonName != null && !commonName.equals(canonical1) ){
            // the first node is a protein and has a common name, so it wins
            return -1;
          }else{
            // the first node is a protein but does not have a common name, so it looses
            return 1;
          }
        }
      }// First tie breaker
      
      // From this point on we know that it is not the case that one of the nodes is a 
      // protein and the other one is a metabolite.
      
      // Third tie breaker: which has highest overall degree
      
      if(this.nodesAreInNetwork){
        if(this.network.getDegree(node1) > this.network.getDegree(node2)){
          return -1;
        }
        
        if(this.network.getDegree(node1) < this.network.getDegree(node2)){
          return 1;
        } 
      }else{
        if(rootGraph.getDegree(node1) > rootGraph.getDegree(node2)){
          return -1;
        }

        if(rootGraph.getDegree(node1) < rootGraph.getDegree(node2)){
          return 1;
        }
        
      }// End of overall degree test
      
      // The overall degrees are the same
      // Fourth tie breaker: which one has a common name (it is not the case that 
      // one is a protein and the other one a metabolite)
      String commonName1 = 
        (String)this.network.getNodeAttributeValue(node1, Semantics.COMMON_NAME);
      String commonName2 = 
        (String)this.network.getNodeAttributeValue(node2, Semantics.COMMON_NAME);
      
      boolean firstIsCommonName = (commonName1 != null && !commonName1.equals(canonical1));
      boolean secondIsCommonName = (commonName2 != null && !commonName2.equals(canonical2));
        
      if(firstIsCommonName && !secondIsCommonName){
        return -1;
      }else if(!firstIsCommonName && secondIsCommonName){
        return 1;
      }else{
        // Either neither has a common name, or they both have common names.
        // return the one that is lexicographically first
        if(commonName1 == null){
          commonName1 = canonical1;
        }
        if(commonName2 == null){
          commonName2 = canonical2;
        }
        int compareValue = commonName1.compareTo(commonName2);
        
        return compareValue;
      }
    }//compare
    
  }//internal class IntraDegreeComparator
  
}//AbstractMetaNodeAttsHandler

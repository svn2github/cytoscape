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

//TODO: Implement removeFromAttributes()
//TODO: Imeplemnt removeMetaEdgesFromAttributes()

package metaNodeViewer.data;

import java.util.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cern.colt.map.*;
import giny.model.*;
import giny.view.*;

/**
 * Specialized version of SimpleMetaNodeAttributesHandler.<br>
 * 
 * 1. Common name = member with highest intra-connections<br>
 * 2. Area = proportional to number of members<br>
 * 3. Edges = same as member edges<br>
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version 1.0
 */
public class AbstractMetaNodeAttsHandler extends SimpleMetaNodeAttributesHandler {
  public static final boolean DEBUG = false;
  
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
  	int [] children = cy_net.getRootGraph().getNodeMetaChildIndicesArray(metanode_root_index);
  	if(children == null || children.length == 0){
  		throw new IllegalArgumentException("Node with index [" + metanode_root_index + "] has no children");
  	}
    SortedSet sortedNodes = IntraDegreeComparator.sortNodes(cy_net, children);
    CyNode highestNode = (CyNode)sortedNodes.first();
    String alias = (String)cy_net.getNodeAttributeValue(highestNode,Semantics.COMMON_NAME);
    if(alias == null){
      alias = (String)cy_net.getNodeAttributeValue(highestNode,Semantics.CANONICAL_NAME);
    }
    return alias;
  }//createMetaNodeAlias
  
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
  
}//AbstractMetaNodeAttsHandler

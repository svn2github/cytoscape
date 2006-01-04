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

package metaNodeViewer.data;
import java.util.ArrayList;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cern.colt.map.AbstractIntIntMap;

/**
 * An interface for classes that handle the node and edge attributes of meta-nodes.
 * Since there are many different ways of transfering node and edge attributes
 * from children nodes and edges to their parent nodes, we need an interface that then 
 * can be implemented by classes so that they can handle this operation as they desire.
 */

public interface MetaNodeAttributesHandler {

  /**
   * Creates a unique name for the Node with the given index, and adds
   * a Node-name mapping to GraphObjAttributes for nodes (contained in the given
   * CyNetwork).
   *
   * @param cy_net the CyNetwork that contains the GraphObjAttributes for nodes and
   * the RootGraph that contains the given node
   * @param metanode_root_index the RootGraph index of the meta-node for which a name
   * will be set
   * @return the name, or null if something went wrong
   */
  public String assignName(CyNetwork cy_net, CyNode node);

  /**
   * Sets the node and edge attributes of the meta-node with the given RootGraph index
   * and assigns a unique name to it in the GraphObjAttributes for nodes
   *
   * @param cy_network the CyNetwork that contains the children nodes of the meta-node, 
   * and that contains the GraphObjAttributes that contain the node and edge attributes
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
  public boolean setAttributes(CyNetwork cy_network, CyNode node, ArrayList children, AbstractIntIntMap meta_edge_to_child_edge);
  
  /**
   * Sets the node attributes of the meta-node with the given RootGraph index 
   * and assigns a unique name to it in the GraphObjAttributes for nodes
   *
   * @param cy_network the CyNetwork that contains the GraphPerspective that contains
   * the children nodes of the meta-node, and that contains the GraphObjAttributes that 
   * contain the node and edge attributes
   * @param node the node for which node attributes will be set
   * @param children an ArrayList of CyNodes that are chilren nodes of the given node
   * @return true if all went well, false if there was an error
   */
  public boolean setNodeAttributes(CyNetwork cy_network, CyNode node, ArrayList children);

  /**
   * Sets the edge attributes of the meta-node with the given RootGraph index and assigns
   * to them unique names in the GraphObjAttributes for edges
   *
   * @param cy_network the CyNetwork that contains the GraphPerspective that contains
   * the children nodes of the meta-node, and the GraphObjAttributes that contain the edge
   * attributes
   * @param node the node for which edge attributes will be set
   * @param meta_edge_to_child_edge maps a meta-edge (edge connected to the meta-node)
   * RootGraph index to a child edge (edge that connects a child of the meta-node to another
   * node) RootGraph index, so that classes implementing this interface know which child edge
   * corresponds to which meta-edge
   * @return true if all went well, false if there was an error
   */
 
  public boolean setEdgeAttributes(CyNetwork cy_network, CyNode node, AbstractIntIntMap meta_edge_to_child_edge);

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
                                       CyNode node,
                                      ArrayList meta_edge_list);

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
                                              CyNode node,
                                              ArrayList meta_edge_list);
}//MetaNodeAttributesHandler

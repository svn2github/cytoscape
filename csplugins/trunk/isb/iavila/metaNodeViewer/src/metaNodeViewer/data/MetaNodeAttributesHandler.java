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
   * Creates a unique name for the CyNode and registers this name and node into Cytoscape
   *
   * @param cy_net the CyNetwork where the node is contained
   * @param node the node
   * @return the name, or null if something went wrong
   */
  public String assignName(CyNetwork cy_net, CyNode node);

  /**
   * Sets the node and edge attributes of the meta-node
   *
   * @param cy_network the CyNetwork that contains the children nodes of the meta-node
   * @param node the meta-node
   * @param children an array of CyNodes that are the children nodes of "node"
   * @param meta_edge_to_child_edge maps a meta-edge (edge connected to the meta-node)
   * to a child edge (edge that connects a child of the meta-node to another
   * node) so that classes implementing this interface know which child edge
   * corresponds to which meta-edge
   * @return true if all went well, false if there was an error
   */
  public boolean setAttributes(CyNetwork cy_network, CyNode node, ArrayList children, AbstractIntIntMap meta_edge_to_child_edge);
  
  /**
   * Sets the node attributes of the meta-node
   * 
   * @param cy_network the CyNetwork that contains the children nodes of "node"
   * @param node the node for which node attributes will be set
   * @param children an ArrayList of CyNodes that are chilren nodes of the given node
   * @return true if all went well, false if there was an error
   */
  public boolean setNodeAttributes(CyNetwork cy_network, CyNode node, ArrayList children);

  /**
   * Sets the edge attributes of the meta-node
   *
   * @param cy_network the CyNetwork that contains the children nodes of "node"
   * @param node the node for which edge attributes will be set
   * @param meta_edge_to_child_edge maps a meta-edge (edge connected to the meta-node)
   * to a child edge (edge that connects a child of the meta-node to another
   * node) so that classes implementing this interface know which child edge
   * corresponds to which meta-edge
   * @return true if all went well, false if there was an error
   */
 
  public boolean setEdgeAttributes(CyNetwork cy_network, CyNode node, AbstractIntIntMap meta_edge_to_child_edge);

  /**
   * Removes all attributes created for the given meta-node.
   * 
   * @param cy_network were the children for the node live
   * @param node the meta-node
   * @param meta_edge_root_indices the edges connected
   * to the meta-node whose attributes should alse be removed
   * @return true if all went well, false otherwise
   */
  public boolean removeFromAttributes (CyNetwork cy_network,
                                       CyNode node,
                                      ArrayList meta_edge_list);

  /**
   * Remove attributes for the gicen meta-edges in CyNetwork, but leave the attributes for the meta-node
   * as they are.
   *
   * @param cy_network the CyNetwork where the meta-edges are
   * @param node the meta-node
   * @param meta_edge_list a list of CyEdges for which the attributes should be removed
   * @return true if all went well, false otherwise
   */
  public boolean removeMetaEdgesFromAttributes (CyNetwork cy_network,
                                              CyNode node,
                                              ArrayList meta_edge_list);
}//MetaNodeAttributesHandler

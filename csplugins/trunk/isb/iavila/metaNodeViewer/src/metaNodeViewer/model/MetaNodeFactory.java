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
package metaNodeViewer.model;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import java.util.*;
import metaNodeViewer.data.*;
/**
 * A class with easy to use static methods for creating meta-nodes for CyNetworks. Its most
 * important task is keeping track of which meta-nodes belong to which CyNetworks. If meta-nodes
 * are created by not using this factory, and then attempts are made to collapse/expand them, unexpected
 * errors/results may unsue.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @since 2.0
 */
public class MetaNodeFactory {
	/**
	 * The key to obtain a cern.colt.list.IntArrayList that contains RootGraph indices of meta-nodes for a
	 * given CyNetwork through <code>CyNetwork.getClientData(String key)</code>
	 * This is necessary because all CyNetworks belong to the same RootGraph which contains the meta-nodes, so
	 * we need to know which meta-nodes belong to which CyNetworks.
	 */
	public static final String METANODES_IN_NETWORK = "metaNodeViewer.model.GPMetaNodeFactory.metaNodeRindices";
	private static final GPMetaNodeFactory gpMetaNodeFactory = new GPMetaNodeFactory();
	
  /**
   * Creates a meta-node within the CyNetwork's RootGraph, a default name is given to the 
   * meta-node if getAssignDefaultNames() is true. 
   * Note that the new meta-node is not contained in cy_network, but, after calling this method,
   * it is recorded that the new meta-node belongs to cy_network.
   *
   * @param cy_network the CyNetwork for which the meta-node will be created
   * @param children_node_indices the indices of the nodes that will be
   * the children of the created meta-node and that should be in cy_network
   * @return the RootGraph index of the newly created meta-node, or zero if
   * none created.
   */
  public static CyNode createMetaNode (CyNetwork cy_network, ArrayList children){
  	return MetaNodeFactory.gpMetaNodeFactory.createMetaNode(cy_network, children);
  }//createMetaNode

  /**
   * Creates a meta-node within the CyNetwork's RootGraph, a name is given to the 
   * meta-node (if getAssignDefaultNames() is true) by invoking attributes_handler.assigName().
   * Note that the new meta-node is not contained in cy_network, but, after calling this method,
   * it is recorded that the new meta-node belongs to cy_network.
   *
   * @param cy_network the CyNetwork for which a meta-node will be created
   * @param children_node_indices the indices of the nodes that will be
   * the children of the created meta-node and that should be in cy_network
   * @param attributes_handler the MetaNodeAttributesHandler to be used to name the new node (if getAssignDefaultNames() is true)
   * @return the RootGraph index of the newly created meta-node, or zero if none created.
   */
  public static CyNode createMetaNode (CyNetwork cy_network, ArrayList children, MetaNodeAttributesHandler attributes_handler){
  	return MetaNodeFactory.gpMetaNodeFactory.createMetaNode(cy_network, children, attributes_handler);
  }//createMetaNode
  
  /**
   * Sets whether or not a default name for newly created meta-nodes should be given
   * and added to the node attributes.
   */
  public static void assignDefaultNames (boolean assign){
  	MetaNodeFactory.gpMetaNodeFactory.assignDefaultNames(assign);
  }//assignDefaultNames

  /**
   * Whether or not default names are being assigned to newly created meta-nodes
   */
  public static boolean getAssignDefaultNames (){
  	return MetaNodeFactory.gpMetaNodeFactory.getAssignDefaultNames();
  }//getAssign

  /**
   * Clears this Factory (useful if a new RootGraph is loaded, but this is not supposed to happen in Cytoscape, so don't use it for now).
   */
  public static void clear (){
  	MetaNodeFactory.gpMetaNodeFactory.clear();
  }//clear
  
}//MetaNodeFactory

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
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer.model;
import cytoscape.CyNetwork;
import giny.model.GraphPerspective;
import metaNodeViewer.data.*;

public class MetaNodeFactory {
	/**
	 * The key to obtain a cern.colt.list.IntArrayList that contains RootGraph indices of meta-nodes for a
	 * given CyNetwork through <code>CyNetwork.getClientData(String key)</code>
	 */
	public static final String METANODES_IN_NETWORK = "metaNodeViewer.model.GPMetaNodeFactory.metaNodeRindices";
	private static final GPMetaNodeFactory gpMetaNodeFactory = new GPMetaNodeFactory();
	
  /**
   * Creates a MetaNode in the given CyNetwork with the given children
   *
   * @param cy_network the CyNetwork in which MetaNodes will be created
   * @param children_node_indices the indices of the nodes that will be
   * the children of the created meta-node
   * @return the RootGraph index of the newly created meta-node, or zero if
   * none created.
   */
  public static int createMetaNode (CyNetwork cy_network, int [] children_node_indices){
  	return MetaNodeFactory.gpMetaNodeFactory.createMetaNode(cy_network, children_node_indices);
  }//createMetaNode

  /**
   * Creates a MetaNode in the given CyNetwork with the given children
   *
   * @param cy_network the CyNetwork in which MetaNodes will be created
   * @param children_node_indices the indices of the nodes that will be
   * the children of the created meta-node
   * @param attributes_handler the MetaNodeAttributesHandler to be used to name the new node (if getAssignDefaultNames() is true)
   * @return the RootGraph index of the newly created meta-node, or zero if
   * none created.
   */
  public static int createMetaNode (CyNetwork cy_network, int [] children_node_indices, MetaNodeAttributesHandler attributes_handler){
  	return MetaNodeFactory.gpMetaNodeFactory.createMetaNode(cy_network, children_node_indices, attributes_handler);
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
   * Clears this Factory.
   */
  public static void clear (){
  	MetaNodeFactory.gpMetaNodeFactory.clear();
  }//clear
  
}//MetaNodeFactory

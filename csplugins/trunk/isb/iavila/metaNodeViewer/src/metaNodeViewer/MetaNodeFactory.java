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
package metaNodeViewer;
import cytoscape.CyNetwork;
import giny.model.GraphPerspective;

public interface MetaNodeFactory {
  
  /**
   * Creates a MetaNode in the given CyNetwork with the given children
   *
   * @param cy_network the CyNetwork in which MetaNodes will be created
   * @param children_node_indices the indices of the nodes that will be
   * the children of the created meta-node
   * @return the RootGraph index of the newly created meta-node, or zero if
   * none created.
   */
  public int createMetaNode (CyNetwork cy_network,
                              int [] children_node_indices);

  /**
   * Sets whether or not a default name for newly created meta-nodes should be given
   * and added to the node attributes.
   */
  public void assignDefaultNames (boolean assign);

  /**
   * Whether or not default names are being assigned to newly created meta-nodes
   */
  public boolean getAssignDefaultNames ();

  /**
   * Clears this Factory.
   */
  public void clear ();

  /**
   * @return the RootGraph indices of the nodes that are parent nodes of nodes
   * in the given graph and that were created using this factory
   */
  public int [] getParentNodesInNet (GraphPerspective graphPerspective);
    
}//MetaNodeFactory

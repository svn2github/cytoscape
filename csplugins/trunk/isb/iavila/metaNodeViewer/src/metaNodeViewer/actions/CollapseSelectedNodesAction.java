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
package metaNodeViewer.actions;
import java.util.*;
import metaNodeViewer.model.AbstractMetaNodeModeler;
import metaNodeViewer.model.MetaNodeFactory;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cern.colt.list.IntArrayList;
import cytoscape.*;
import giny.view.*;
import giny.model.*;

/**
 * Only accessible in metaNodeViewer.actions package.
 * Use action factory to get an instance of this class.
 */
public class CollapseSelectedNodesAction extends AbstractAction {
  
  protected AbstractMetaNodeModeler abstractingModeler;
  protected boolean collapseExistentParents;
    
  /**
   * Use action factory instead
   *
   * @param abstracting_modeler the AbstractMetaNodeModeler used to collapse nodes
   * @param collapse_existent_parents if the selected nodes already have meta-parents, and
   * if collapse_existent_parents is true, don't create new parent meta-nodes, simply collapse
   * the existent parents
   */
  protected CollapseSelectedNodesAction (AbstractMetaNodeModeler abstracting_modeler,
                                         boolean collapse_existent_parents,
                                         String title){
    super(title);
    this.abstractingModeler = abstracting_modeler;
    this.collapseExistentParents = collapse_existent_parents;
  }//CollapseSelectedNodesAction

  /**
   * Sets whether or not the existent parents of the selected nodes should be collapsed instead
   * of creating new meta-nodes for them
   */
  public void setCollapseExistentParents (boolean collapse_existent_parents){
    this.collapseExistentParents = collapse_existent_parents;
  }//setCollapseExistentParents

  /**
   * Sets whether or not default names should be assigned to newly created metanodes
   */
  public void setAssignDefaultNames (boolean assign){
    MetaNodeFactory.assignDefaultNames(assign);
  }//setAssignDefaultNames


  /**
   * Implements AbstractAction.actionPerformed by calling <code>collapseSelectedNodes</code>
   */
  public void actionPerformed (ActionEvent e){
    collapseSelectedNodes(this.abstractingModeler, 
                          this.collapseExistentParents);
  }//actionPerformed
  
  
  /**
   * Collapses into a single node a set of selected nodes in the current CyNetwork.
   */
  public static void collapseSelectedNodes (AbstractMetaNodeModeler abstractModeler,
                                            boolean collapse_existent_parents){
    
  	GraphView graphView = Cytoscape.getCurrentNetworkView();
    // Pop-up a dialog if there are no selected nodes and return
    if(graphView.getSelectedNodes().size() == 0) {
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                    "Please select one or more nodes.");
      return;
    }
    
    // Get the selected nodes' RootGraph indices
    CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
    java.util.List selectedNVlist = graphView.getSelectedNodes();
    Iterator it = selectedNVlist.iterator();
    IntArrayList selectedNodeIndices = new IntArrayList();
    while(it.hasNext()){
      NodeView nodeView = (NodeView)it.next();
      int rgNodeIndex = cyNetwork.getRootGraphNodeIndex(nodeView.getGraphPerspectiveIndex());
      selectedNodeIndices.add(rgNodeIndex);
    }//while it
    selectedNodeIndices.trimToSize();
    int [] nodeIndices = selectedNodeIndices.elements();

    // If collapse_existent_parents is true, then find parents for the selected nodes
    // and collapse them
    // NOTE: This is tricky if we have multiple GraphPerspectives, since
    // they share the same RootGraph. Use the fact that MetaNodeFactory stores for each network
    // the meta-nodes that were created for it.
    if(collapse_existent_parents){
      RootGraph rootGraph = cyNetwork.getRootGraph();
      IntArrayList parentRootGraphIndices = new IntArrayList();
      IntArrayList metaNodesForNetwork = (IntArrayList)cyNetwork.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
      if(metaNodesForNetwork != null){
      	for(int i = 0; i < nodeIndices.length; i++){
      		int [] parents = rootGraph.getNodeMetaParentIndicesArray(nodeIndices[i]);
      		if(parents.length == 1 && metaNodesForNetwork.contains(parents[0])){
      			parentRootGraphIndices.add(parents[0]);
      		}else if(parents.length > 1){
      			// TODO: Think about this better. What to do when a node has more than one parent???
      			// Maybe pop-up window asking which parent should be collapsed, give the option of collapsing the last one created...???
      			for(int j = 0; j < parents.length; j++){
      				if( metaNodesForNetwork.contains(parents[j]) ){
      					parentRootGraphIndices.add(parents[j]);
      				}
      			}//for j
      		}
      	}//for i
      }// metaNodesForNetwork != null
      
      // Collapse parents sequentially
      parentRootGraphIndices.trimToSize();
      if(parentRootGraphIndices.size() == 0){
        // Tell the user there are no parents and exit
        JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                      "The selected nodes have no existent parent nodes.");
        return;
      }
      int [] parents = parentRootGraphIndices.elements();      
      for(int i = 0; i < parents.length; i++){
        abstractModeler.applyModel(cyNetwork,parents[i]);
      }//for i
      return;
    }// if collapse_existent_parents

    // Create a meta-node for the selected nodes
    int rgParentNodeIndex = MetaNodeFactory.createMetaNode(cyNetwork, nodeIndices);
    if(rgParentNodeIndex == 0){
      // Something went wrong, alert user, and exit
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                    "An internal error was encountered while collapsing.",
                                    "Internal Error",
                                    JOptionPane.ERROR_MESSAGE
                                    );
      return;
    }
    // Finally, collapse it
    abstractModeler.applyModel(cyNetwork,rgParentNodeIndex);
  }//collapseSelectedNodes
  
}//class CollapseSelectedNodesAction


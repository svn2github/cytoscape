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
import metaNodeViewer.MetaNodeFactory;
import metaNodeViewer.GPMetaNodeFactory;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cern.colt.list.IntArrayList;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.*;
import cytoscape.view.CyWindow;
import giny.view.*;
import giny.model.*;
import cern.colt.list.IntArrayList;

/**
 * Only accessible in metaNodeViewer.actions package.
 * Use action factory to get an instance of this class.
 */
public class CollapseSelectedNodesAction 
  extends AbstractAction {
  
  protected CyWindow cyWindow;
  protected AbstractMetaNodeModeler abstractingModeler;
  protected MetaNodeFactory mnFactory;
  protected boolean collapseExistentParents;
    
  /**
   * Use action factory instead
   *
   * @param cy_window where the CyNetwork lives
   * @param abstracting_modeler the AbstractMetaNodeModeler used to collapse nodes
   * @param mn_factory the MetaNodeFactory used to create meta nodes from selected nodes
   * @param collapse_existent_parents if the selected nodes already have meta-parents, and
   * if collapse_existent_parents is true, don't create new parent meta-nodes, simply collapse
   * the existent parents
   */
  public CollapseSelectedNodesAction (CyWindow cy_window,
                                      AbstractMetaNodeModeler abstracting_modeler,
                                      MetaNodeFactory mn_factory,
                                      boolean collapse_existent_parents,
                                      String title){
    super(title);
    this.cyWindow = cy_window;
    this.mnFactory = mn_factory;
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
    this.mnFactory.assignDefaultNames(assign);
  }//setAssignDefaultNames


  /**
   * Implements AbstractAction.actionPerformed by calling <code>collapseSelectedNodes</code>
   */
  public void actionPerformed (ActionEvent e){
    collapseSelectedNodes(this.cyWindow, 
                          this.abstractingModeler, 
                          this.mnFactory,
                          this.collapseExistentParents);
  }//actionPerformed
  
  
  /**
   * Collapses into a single node a set of currently selected nodes.
   */
  public static void collapseSelectedNodes (CyWindow cyWindow,
                                            AbstractMetaNodeModeler abstractModeler,
                                            MetaNodeFactory mnFactory,
                                            boolean collapse_existent_parents){
    GraphView graphView = cyWindow.getView();
    // Pop-up a dialog if there are no selected nodes and return
    if(graphView.getSelectedNodes().size() == 0) {
      JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                    "Please select one or more nodes.");
      return;
    }
    
    // Get the selected nodes' RootGraph indices
    CyNetwork cyNetwork = cyWindow.getNetwork();
    GraphPerspective mainGP = cyNetwork.getGraphPerspective();
    java.util.List selectedNVlist = graphView.getSelectedNodes();
    Iterator it = selectedNVlist.iterator();
    IntArrayList selectedNodeIndices = new IntArrayList();
    while(it.hasNext()){
      NodeView nodeView = (NodeView)it.next();
      int rgNodeIndex = mainGP.getRootGraphNodeIndex(nodeView.getGraphPerspectiveIndex());
      selectedNodeIndices.add(rgNodeIndex);
    }//while it
    selectedNodeIndices.trimToSize();
    int [] nodeIndices = selectedNodeIndices.elements();

    // If collapse_existent_parents is true, then find parents for the selected nodes
    // and collapse them
    // NOTE (TODO) : This is tricky if we have multiple GraphPerspectives, since
    // they share the same RootGraph. The MetaNodeFactory (or some other class) needs
    // to keep track of which meta-nodes in RootGraph belong to which GraphPerspectives
    // For now do this:
    if(collapse_existent_parents){
      RootGraph rootGraph = mainGP.getRootGraph();
      IntArrayList parentRootGraphIndices = new IntArrayList();
      for(int i = 0; i < nodeIndices.length; i++){
        int [] parents = rootGraph.getNodeMetaParentIndicesArray(nodeIndices[i]);
        if(parents.length == 1){
          parentRootGraphIndices.add(parents[0]);
        }else if(parents.length > 1){
          // TODO: Somehow handle the case when there is more than one parent
          // for now, just add the first parent
          parentRootGraphIndices.add(parents[0]);
        }
      }//for i
      
      // Collapse each parent sequentially
      parentRootGraphIndices.trimToSize();
      if(parentRootGraphIndices.size() == 0){
        // Tell the user there are no parents and exit
        JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                      "The selected nodes have no existent parent nodes.");
        return;
      }
      int [] parents = parentRootGraphIndices.elements();      
      for(int i = 0; i < parents.length; i++){
        abstractModeler.applyModel(mainGP,parents[i]);
      }//for i
      return;
    }

    // Create a meta-node for the selected nodes
    int rgParentNodeIndex = mnFactory.createMetaNode(cyNetwork, nodeIndices);
    if(rgParentNodeIndex == 0){
      // Something went wrong, alert user, and exit
      JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                    "An internal error was encountered while collapsing.",
                                    "Internal Error",
                                    JOptionPane.ERROR_MESSAGE
                                    );
      return;
    }
    // Finally, collapse it
    abstractModeler.applyModel(mainGP,rgParentNodeIndex);
  }//collapseSelectedNodes
  
}//class CollapseSelectedNodesAction


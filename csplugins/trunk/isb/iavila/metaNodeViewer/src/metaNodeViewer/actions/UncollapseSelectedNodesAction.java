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
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cern.colt.list.IntArrayList;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.*;
import cytoscape.view.CyWindow;
import giny.view.*;
import giny.model.*;

/**
 * Only accessible in metaNodeViewer.actions package.
 * Use action factory to get an instance of this class.
 */
public class UncollapseSelectedNodesAction 
  extends AbstractAction {
  
  protected CyWindow cyWindow;
  protected AbstractMetaNodeModeler abstractingModeler;
  protected boolean recursive;
  protected boolean temporaryUncollapse;
  
  /**
   * Use action factory instead
   */
  public UncollapseSelectedNodesAction (CyWindow cy_window,
                                        AbstractMetaNodeModeler abstracting_modeler,
                                        boolean recursive_uncollapse,
                                        boolean temporary_uncollapse,
                                        String title){
    super(title);
    this.cyWindow = cy_window;
    this.abstractingModeler = abstracting_modeler;
    this.recursive = recursive_uncollapse;
    this.temporaryUncollapse = temporary_uncollapse;
  }//UncollapseSelectedNodesAction

  /**
   * Sets whether or not this uncollapse should be recursive (uncollapse to the bottom level
   * of the hierarchy).
   */
  public void setRecursiveUncollapse (boolean is_recursive){
    this.recursive = is_recursive;
  }//setRecursiveUncollapse
  
  /**
   * Sets whether or not this uncollapse is temporary or not. If it is temporary, then the
   * meta-node will be remembered for a subsequent collapse operation.
   */
  public void setTemporaryUncollapse (boolean is_temporary){
    this.temporaryUncollapse = is_temporary;
  }//setTemporaryUncollapse

  /**
   * Implements AbstractAction.actionPerformed by calling <code>uncollapseSelectedNodes</code>
   */
  public void actionPerformed (ActionEvent e){
    uncollapseSelectedNodes(this.cyWindow, 
                            this.abstractingModeler, 
                            this.recursive, 
                            this.temporaryUncollapse);
  }//actionPerformed
  
  public static void uncollapseSelectedNodes (CyWindow cyWindow,
                                              AbstractMetaNodeModeler abstractModeler,
                                              boolean recursive,
                                              boolean temporary){
    GraphView graphView = cyWindow.getView();
    // Pop-up a dialog if there are no selected nodes and return
    if(graphView.getSelectedNodes().size() == 0) {
      JOptionPane.showMessageDialog(cyWindow.getMainFrame(),
                                    "Please select one or more nodes.");
      return;
    }
    
    // Get the RootGraph indices of the selected nodes
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
    
    // Finally, uncollapse each node (if it is not a metanode, nothing happens)
    for(int i = 0; i < nodeIndices.length; i++){
      abstractModeler.undoModel(mainGP,nodeIndices[i],recursive,temporary);
    }//for i
  }//uncollapseSelectedNodes
  
}//class UncollapseSelectedNodesAction

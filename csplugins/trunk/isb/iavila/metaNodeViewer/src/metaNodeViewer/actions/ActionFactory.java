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
import cytoscape.view.CyWindow;
import cytoscape.data.*;
import javax.swing.AbstractAction;
import giny.model.*;

/**
 * This class creates and returns actions for applying meta-node models to a graph
 */

public class ActionFactory {
  
  // In theory, there should only be one RootGraph and one AbstractMetaNodeModeler for it
  private static AbstractMetaNodeModeler abstractingModeler;
  // Only one meta node factory as well
  private static MetaNodeFactory mnFactory;
  private static final String COLLAPSE_SELECTED_TITLE = "Collapse selected";
  private static final String UNCOLLAPSE_SELECTED_R_TITLE = "Uncollapse selected recursively";
  private static final String UNCOLLAPSE_SELECTED_TITLE = "Uncollapse selected";
  
  /**
   * Creates the AbstractMetaNodeModeler and a listener for CyNetwork that replaces
   * AbstractMetaNodeModeler's RootGraph when a new RootGraph is loaded.
   */
  private static void initialize (CyWindow cy_window){
    RootGraph rootGraph = cy_window.getNetwork().getRootGraph();
    ActionFactory.abstractingModeler = new AbstractMetaNodeModeler(rootGraph);
    cy_window.getNetwork().addCyNetworkListener(
                                                new CyNetworkListener(){
                                                  public void onCyNetworkEvent (CyNetworkEvent e){
                                                    if(e.getType() == 
                                                       CyNetworkEvent.GRAPH_REPLACED){
                                                      RootGraph r = e.getNetwork().getRootGraph();
                                                      ActionFactory.abstractingModeler.setRootGraph(r);
                                                    }//if
                                                  }//onCyNetworkEvent
                                                }//end anonymous class
                                                );
    ActionFactory.mnFactory = new GPMetaNodeFactory();
  }//intialize
  
  /**
   * @param cy_window the CyWindow that contains the CyNetwork that contains the GraphPerspective
   * @param collapse_existent_parents  whether or not the existent parents of the selected nodes 
   * should be collapsed instead of creating new meta-nodes for them
   * @return an AbstractAction that collapses into one single node selected nodes and edges
   * in the GraphPerspective contained in the given CyWindow
   */
  public static AbstractAction createCollapseSelectedNodesAction 
    (
     CyWindow cy_window,
     boolean collapse_existent_parents
     ){
    // Get the RootGraph and create or update abstractingModeler
    RootGraph rootGraph = cy_window.getNetwork().getRootGraph();
    if(ActionFactory.abstractingModeler == null || ActionFactory.mnFactory == null){
      initialize(cy_window);
    }else if(ActionFactory.abstractingModeler.getRootGraph() != rootGraph){
      // Currently, Cytoscape only supports ONE RootGraph. If we get here,
      // that means that the RootGraph for Cytoscape changed
      ActionFactory.abstractingModeler.setRootGraph(rootGraph);
    }
    // Create the action and return it
    return new CollapseSelectedNodesAction(cy_window,
                                           ActionFactory.abstractingModeler,
                                           ActionFactory.mnFactory,
                                           collapse_existent_parents,
                                           ActionFactory.COLLAPSE_SELECTED_TITLE);
    
  }//getCollapseSelectedNodesAction
  
  /**
   * @param cy_window the CyWindow that contains the RootGraph (and its GraphPerspective)
   * in which nodes will be selected for uncollapsing
   * @param recursive whether or not the selected nodes with children should be uncollapse
   * all the way down to their childless descendants or not
   * @return an AbstractAction that uncollapses selected nodes
   */
  public static AbstractAction createUncollapseSelectedNodesAction 
    (
     CyWindow cy_window,
     boolean recursive,
     boolean temporary
     ){
    // Get the RootGraph and create or update abstractingModeler
    RootGraph rootGraph = cy_window.getNetwork().getRootGraph();
    if(ActionFactory.abstractingModeler == null){
      initialize(cy_window);
    }else if(ActionFactory.abstractingModeler.getRootGraph() != rootGraph){
      // Currently, Cytoscape only supports ONE RootGraph. If we get here,
      // that means that the RootGraph for Cytoscape changed
      ActionFactory.abstractingModeler.setRootGraph(rootGraph);
    }
    // Create the action and return it
    return new UncollapseSelectedNodesAction(cy_window,
                                             ActionFactory.abstractingModeler,
                                             recursive,
                                             temporary,
                                             (recursive)?ActionFactory.UNCOLLAPSE_SELECTED_R_TITLE
                                             :ActionFactory.UNCOLLAPSE_SELECTED_TITLE);
    
  }//getUncollapseSelectedNodesAction

}//class ActionFactory

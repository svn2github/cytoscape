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
import metaNodeViewer.model.*;
import metaNodeViewer.data.MetaNodeAttributesHandler;
import cytoscape.*;
import javax.swing.AbstractAction;
import giny.model.*;

/**
 * This class creates and returns actions for applying meta-node models to a graph
 */

public class ActionFactory {
  
  private static AbstractMetaNodeModeler abstractingModeler;
  private static MetaNodeAttributesHandler defaultAttributesHandler = MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER;
  private static final String COLLAPSE_SELECTED_TITLE = "Collapse selected";
  private static final String UNCOLLAPSE_SELECTED_R_TITLE = "Uncollapse selected recursively";
  private static final String UNCOLLAPSE_SELECTED_TITLE = "Uncollapse selected";
  
  private static void initialize (){
  	ActionFactory.abstractingModeler = MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler();
  	ActionFactory.defaultAttributesHandler = MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER;
  }//initialize
  
  /**
   * Sets the default MetaNodeAttributesHandler that should be used to transfer node and edge
   * attributes from children nodes and edges to meta-nodes. 
   * If none is set, MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER will be used.
   *
   * @param MetaNodeAttributesHandler the handler
   */
  public static void setMetaNodeAttributesHandler (MetaNodeAttributesHandler handler){
    ActionFactory.defaultAttributesHandler = handler;
    if(ActionFactory.abstractingModeler == null){
    	ActionFactory.abstractingModeler = MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler();
    }
    ActionFactory.abstractingModeler.setDefaultAttributesHandler(ActionFactory.defaultAttributesHandler);
  }//setMetaNodeAttributesHandler

  
  /**
   * @param collapse_existent_parents  whether or not the existent parents of the selected nodes 
   * should be collapsed instead of creating new meta-nodes for them
   * @return an AbstractAction that collapses into one single node selected nodes and edges
   * in the GraphPerspective contained in the given CyWindow
   */
  public static AbstractAction createCollapseSelectedNodesAction (boolean collapse_existent_parents){
    // Get the RootGraph and create or update abstractingModeler
    RootGraph rootGraph = Cytoscape.getRootGraph();
    if(ActionFactory.abstractingModeler == null || 
       ActionFactory.defaultAttributesHandler == null){
      initialize();
    }else if(ActionFactory.abstractingModeler.getRootGraph() != rootGraph){
      // Currently, Cytoscape only supports ONE RootGraph. If we get here,
      // that means that the RootGraph for Cytoscape changed
      ActionFactory.abstractingModeler.setRootGraph(rootGraph);
    }
    // Create the action and return it
    return new CollapseSelectedNodesAction(ActionFactory.abstractingModeler,
                                           collapse_existent_parents,
                                           ActionFactory.COLLAPSE_SELECTED_TITLE);
    
  }//getCollapseSelectedNodesAction
  
  /**
   * in which nodes will be selected for uncollapsing
   * @param recursive whether or not the selected nodes with children should be uncollapse
   * all the way down to their childless descendants or not
   * @return an AbstractAction that uncollapses selected nodes
   */
  public static AbstractAction createUncollapseSelectedNodesAction (boolean recursive, boolean temporary){
    // Get the RootGraph and create or update abstractingModeler
    RootGraph rootGraph = Cytoscape.getRootGraph();
    if(ActionFactory.abstractingModeler == null){
      initialize();
    }else if(ActionFactory.abstractingModeler.getRootGraph() != rootGraph){
      // Currently, Cytoscape only supports ONE RootGraph. If we get here,
      // that means that the RootGraph for Cytoscape changed
      ActionFactory.abstractingModeler.setRootGraph(rootGraph);
    }
    // Create the action and return it
    return new UncollapseSelectedNodesAction(ActionFactory.abstractingModeler,
                                             recursive,
                                             temporary,
                                             (recursive)?ActionFactory.UNCOLLAPSE_SELECTED_R_TITLE
                                             :ActionFactory.UNCOLLAPSE_SELECTED_TITLE);
    
  }//getUncollapseSelectedNodesAction

}//class ActionFactory

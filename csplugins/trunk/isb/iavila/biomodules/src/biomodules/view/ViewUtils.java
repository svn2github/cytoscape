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
 * A class with class members and methods that facilitate viewing biomodules.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
package biomodules.view;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import giny.model.*;
import metaNodeViewer.model.*;
import metaNodeViewer.GPMetaNodeFactory;
import metaNodeViewer.data.*;
import metaNodeViewer.view.VisualStyleFactory;

public class ViewUtils {

  public static MetaNodeAttributesHandler attributesHandler = 
    new AbstractMetaNodeAttsHandler();
  
  public static GPMetaNodeFactory metaNodeFactory = 
    new GPMetaNodeFactory(attributesHandler);
  
  public static AbstractMetaNodeModeler abstractModeler = 
    new AbstractMetaNodeModeler(null,attributesHandler);

  
  /**
   * @return an array of RootGraph indices for the newly created meta-nodes, null
   * if something went wront (null arguments for example)
   */
  public static int [] abstractBiomodules (CyNetwork network, 
                                           CyNode [][] biomodules){
    
    long startTime = System.currentTimeMillis();
    System.err.println("Abstracting biomodules...");

    RootGraph rootGraph = network.getRootGraph();
    
    if(ViewUtils.abstractModeler.getRootGraph() == null ||
       ViewUtils.abstractModeler.getRootGraph() != rootGraph){
      ViewUtils.abstractModeler.setRootGraph(rootGraph);
    }
    
    if(biomodules == null){
      //Nothing to visualize
      return null;
    }
    int [] metaNodeIndices = new int[biomodules.length];
    for(int i = 0; i < biomodules.length; i++){
      int [] nodeIndices = new int[biomodules[i].length]; // the children of the mentanode
      for(int j = 0; j < biomodules[i].length; j++){
        CyNode node = biomodules[i][j];
        int index = network.getIndex(node);
        if(index == 0){
          // The node is hidden, don't know what to do!
          System.err.println("CyNode " + node + " is hidden.");
          continue;
        }
        nodeIndices[j] = index;
      }//for j
      metaNodeIndices[i] = ViewUtils.metaNodeFactory.createMetaNode(network,nodeIndices);
      ViewUtils.abstractModeler.applyModel(network,metaNodeIndices[i],nodeIndices);
    }//for i
    
    long secs = (System.currentTimeMillis() - startTime)/1000;
    System.err.println("Done creating meta-nodes for biomodules, time = " + secs + ".");
   
    // Apply vizmapper
    CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
    VisualMappingManager vizmapper = cyDesktop.getVizMapManager();
    VisualStyle abstractMetaNodeVS = 
      vizmapper.getCalculatorCatalog().getVisualStyle(VisualStyleFactory.ABSTRACT_METANODE_VS);
    if(abstractMetaNodeVS == null){
      abstractMetaNodeVS = VisualStyleFactory.createAbstractMetaNodeVisualStyle(network);
    }
    String netID = network.getIdentifier();
    CyNetworkView netView = Cytoscape.getNetworkView(netID);
    if(netView != null){
      netView.applyVizmapper(abstractMetaNodeVS);
    }
    
    return metaNodeIndices;
  }//abstractBiomodules

  /**
   * Removes the given list of meta-nodes from the network and restores
   * their children.
   *
   * @param network the <code>CyNetwork</code> from which meta-nodes will be removed
   * @param meta_node_rindices the <code>RootGraph</code> indices of the meta-nodes
   * to be removed
   * @param recursive if there are > 1 levels of meta-node hierarchy, whether or not
   * to remove all the levels (if it is known that there is only 1 level, setting this
   * to false significantly improves performance)
   */
  public static void removeMetaNodes (CyNetwork network, 
                                      int [] meta_node_rindices,
                                      boolean recursive){
    
    long startTime = System.currentTimeMillis();
    System.err.println("Removing meta-nodes...");
    
    if(network == null || meta_node_rindices == null || meta_node_rindices.length == 0){
      System.err.println("...nothing to remove.");
      return;
    }
    
    for(int i = 0; i < meta_node_rindices.length; i++){
      // Check that the index is a RootGraph index
      int rindex = meta_node_rindices[i];
      if(rindex > 0){
        // Not a root-graph index
        rindex = network.getRootGraphNodeIndex(rindex); 
      }
      if(rindex == 0){
        // We are in trouble
        System.err.println("Skipping index = 0.");
        continue;
      }
      boolean temporary = false;
      boolean ok = ViewUtils.abstractModeler.undoModel(network,rindex,recursive,temporary); 
      if(!ok){
        System.err.println("Could not remove meta-node " + rindex);
      }
    }//for i
    
    long secs = (System.currentTimeMillis() - startTime)/1000;
    System.err.println("...done removing meta-nodes, time = " + secs);
  }//removeMetaNodes

}//ViewUtils

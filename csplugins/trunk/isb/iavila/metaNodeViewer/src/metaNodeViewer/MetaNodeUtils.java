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
package metaNodeViewer;

import cern.colt.list.IntArrayList;
import giny.model.RootGraph;
import metaNodeViewer.data.MetaNodeAttributesHandler;
import metaNodeViewer.model.AbstractMetaNodeModeler;
import metaNodeViewer.model.MetaNodeFactory;
import metaNodeViewer.model.MetaNodeModelerFactory;
import cytoscape.CyNetwork;
import cytoscape.CyNode;

/**
 * Class with easy to call static methods for meta-node operations.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @since 2.0
 */

// TODO: In method removeAbstractedMetaNodes() there is a call to RootGraph.removeNode(index) that throws an exception at the level
// of ColtRootGraph. FIX THIS.

public class MetaNodeUtils {
	  public static final AbstractMetaNodeModeler abstractModeler = MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler();
	  
	  
	  /**
	   * For each row in children, it creates a meta-node with the children in that row, and abstracts
	   * the children to a meta-node in the network.
	   *  
	   * @param network the CyNetwork where the nodes to be abstracted to meta-nodes reside
	   * @param children a 2D array of CyNodes where each row contains the members of a meta-node
	   * @param attributes_handler the MetaNodeAttributesHanlder to name and assign attributes to the meta-nodes
	   * @return an array of RootGraph indices for the newly created meta-nodes, null
	   * if something went wrong (null arguments for example)
	   * 
	   * The order of the indices in the returned array corresponds to the order of the meta-nodes' 
	   * children in the given CyNode[][]. For example, meta node with index 'i' in the 
	   * returned array is the parent of nodes in row children[i].
	   */
	  public static int [] abstractToMetaNodes (CyNetwork network, 
	                                            CyNode [][] children,
												MetaNodeAttributesHandler attributes_handler){
	  	MetaNodeUtils.abstractModeler.setNetworkAttributesHandler(network, attributes_handler);
	  	return abstractToMetaNodes(network,children);
	  }//abstractToMetaNodes
	  
	  /**
	   * For each row in children, it creates a meta-node with the children in that row, and abstracts
	   * the children to a meta-node in the network.
	   * 
	   * @param network the CyNetwork where the nodes to be abstracted to meta-nodes reside
	   * @param children a 2D array of CyNodes where each row contains the members of a meta-node
	   * @return an array of RootGraph indices for the newly created meta-nodes, null
	   * if something went wrong (null arguments for example)
	   * 
	   * The order of the indices in the returned array corresponds to the order of the meta-nodes' 
	   * children in the given CyNode[][]. For example, meta node with index 'i' in the 
	   * returned array is the parent of nodes in row children[i].
	   */
	  public static int [] abstractToMetaNodes (CyNetwork network, 
	                                            CyNode [][] children){
	    
	    long startTime = System.currentTimeMillis();
	    System.err.println("Abstracting biomodules...");
	    
	    if(children == null){
	        //Nothing to visualize
	        return null;
	    }
	    
	    RootGraph rootGraph = network.getRootGraph();
	    if(MetaNodeUtils.abstractModeler.getRootGraph() == null ||
	       MetaNodeUtils.abstractModeler.getRootGraph() != rootGraph){
	    	// Theoretically, should never have to do this since there is only one RootGraph during a Cytoscape session
	       MetaNodeUtils.abstractModeler.setRootGraph(rootGraph);
	    }
	    
	    int [] metaNodeIndices = new int[children.length];
	    for(int i = 0; i < children.length; i++){
	      int [] nodeIndices = new int[children[i].length]; // the children of the meta-node
	      for(int j = 0; j < children[i].length; j++){
	        CyNode node = children[i][j];
	        int index = network.getIndex(node);
	        if(index == 0){
	          // TODO: The node is hidden, don't know what to do!
	          System.err.println("CyNode " + node + " is hidden.");
	          continue;
	        }
	        nodeIndices[j] = index;
	      }//for j
	      MetaNodeAttributesHandler attsHandler = MetaNodeUtils.abstractModeler.getNetworkAttributesHandler(network);
	      metaNodeIndices[i] = MetaNodeFactory.createMetaNode(network,nodeIndices,attsHandler);
	     MetaNodeUtils.abstractModeler.applyModel(network,metaNodeIndices[i],nodeIndices);
	    }//for i
	    
	    long secs = (System.currentTimeMillis() - startTime)/1000;
	    System.err.println("Done creating meta-nodes for biomodules, time = " + secs + ".");
	 
	    return metaNodeIndices;
	  }//abstractToMetaNodes

	  /**
	   * Permanently removes the given list of meta-nodes from the network and restores
	   * their children.
	   *
	   * @param network the <code>CyNetwork</code> from which meta-nodes will be removed
	   * @param meta_node_rindices the <code>RootGraph</code> indices of the meta-nodes
	   * to be removed
	   * @param recursive if there are > 1 levels of meta-node hierarchy, whether or not
	   * to remove all the levels (if it is known that there is only 1 level, setting this
	   * to false significantly improves performance)
	   * @return the number of removed meta-nodes
	   */
	  public static int removeAbstractedMetaNodes (CyNetwork network, 
	                                                int [] meta_node_rindices,
	                                                boolean recursive){
	    
	    long startTime = System.currentTimeMillis();
	    System.err.println("Removing meta-nodes...");
	    
	    if(network == null || meta_node_rindices == null || meta_node_rindices.length == 0){
	      System.err.println("...nothing to remove.");
	      return 0;
	    }
	    int numRemoved = 0;
	    for(int i = 0; i < meta_node_rindices.length; i++){
	      // Check that the index is a RootGraph index
	      int rindex = meta_node_rindices[i];
	      if(rindex > 0){
	        // Not a root-graph index
	        rindex = network.getRootGraphNodeIndex(rindex); 
	      }
	      if(rindex == 0){
	        // We are in trouble
	        System.err.println("Skipping, index == 0.");
	        continue;
	      }
	      boolean temporary = false; // == don't remember these meta-nodes
	      boolean ok = 
          MetaNodeUtils.abstractModeler.undoModel(network,rindex,recursive,temporary); 
	      
        if(!ok){
	        System.err.println("Could not remove meta-node " + rindex);
	      }else{
	      	// Also, remove the meta-node from the RootGraph
	      	RootGraph rootGraph = network.getRootGraph();
	      	// TODO: This throws an exception!!!!!!
	      	// Talked to Rowan, he says he knows what it is.
	      	rootGraph.removeNode(rindex);
	      	numRemoved++;
	      }
	    }//for i
	    
	    long secs = (System.currentTimeMillis() - startTime)/1000;
	    System.err.println("...done removing meta-nodes, time = " + secs);
	    return numRemoved;
	  }//removeMetaNodes
	  
	  /**
	   * Uncollapses a list of meta-nodes in a CyNetwork.
	   * 
	   * @param cy_network the CyNetwork whithin which the meta-nodes to be uncollapsed reside
	   * @param node_rindices the RootGraph indices of the meta-nodes to be uncollapsed
	   * @param recursive whether meta-nodes inside meta-nodes should be uncollapsed
	   * @param temporary whether this operation is temporary, or not, if it is not, then the meta-nodes will be removed permanently
	   * @return the number of uncollapsed meta-nodes
	   */
	  public static int uncollapseNodes (CyNetwork cy_network, 
                                       int [] node_rindices, 
                                       boolean recursive, 
                                       boolean temporary){
	  	
	  	// Uncollapse each node (if it is not a metanode, nothing happens)
	  	int numUncollapsed = 0;
	  	if(temporary){
	  		for(int i = 0; i < node_rindices.length; i++){
	  			// This only uncollapses the meta-nodes, but they are kept in the RootGraph
	  			boolean b = MetaNodeUtils.abstractModeler.undoModel(cy_network,node_rindices[i],recursive,true);
	  			if(b){
	  				numUncollapsed++;
	  			}
	  		}//for i
	  	}else{
	  		// Permanently removes the meta-nodes from CyNetwork and RootGraph:
	  		numUncollapsed = MetaNodeUtils.removeAbstractedMetaNodes(cy_network,node_rindices,recursive);
	  	}
	  	return numUncollapsed;
	  }//uncollapseSelectedNodes
	  
	  /**
	   * Collapses into a meta-node(s) a set of given nodes in a CyNetwork.
	   * 
	   * @param cy_network the CyNetwork whithin which the nodes to be collapse reside
	   * @param node_rindices the RootGraph indices of the nodes to be collapsed
	   * @param collapse_existent_parents whether or not the existent meta-node parents of the selected nodes should be collapsed instead
	   * of creating new meta-nodes for them
	   * @param collapse_recursively whether or not the top-level meta-node parents of the selected nodes should be found and collapsed, ignored if
	   * collapse_existent_parents is false
     * @param multiple_edges whether or not multiple edges between meta-nodes and other nodes
     * should be created
	   * @return the number of collapsed meta-nodes, or -1 if something went wrong
	   */
	  public static int collapseNodes (CyNetwork cy_network, int [] node_rindices, 
                                     boolean collapse_existent_parents,
                                     boolean collapse_recursively,
                                     boolean multiple_edges){
	    
	    // If collapse_existent_parents is true, then find parents for the selected nodes
	    // and collapse them
	    // NOTE: This is tricky if we have multiple GraphPerspectives, since
	    // they share the same RootGraph, 
      // use the fact that MetaNodeFactory stores for each network
	    // the meta-nodes that were created for it.
      MetaNodeUtils.abstractModeler.setMultipleEdges(multiple_edges);
	    if(collapse_existent_parents){
	    	int [] parents = findParentMetaNodes(cy_network, node_rindices, collapse_recursively);
	      // Collapse parents sequentially
	      if(parents == null || parents.length == 0){
	        return 0;
	      }
	      int numCollapsed = 0;
	      for(int i = 0; i < parents.length; i++){
	        boolean collapsed = MetaNodeUtils.abstractModeler.applyModel(cy_network,parents[i]);
	        if(collapsed){
	        	numCollapsed++;
	        }
	      }//for i
	      return numCollapsed;
	    }// if collapse_existent_parents

	    // Create a meta-node for the selected nodes
	    int rgParentNodeIndex = MetaNodeFactory.createMetaNode(cy_network, node_rindices);
	    if(rgParentNodeIndex == 0){
	      // Something went wrong, return -1
	      return -1;
	    }
	    // Finally, collapse it
	    MetaNodeUtils.abstractModeler.applyModel(cy_network,rgParentNodeIndex);
	    return 1;
	  }//collapseNodes
	  
	  /**
	   * Finds the parent meta-nodes of the given array of nodes in the CyNetwork and returns their <code>RootGraph</code> indices.
	   * 
	   * @param cy_net the CyNetwork within which to look for parent meta-nodes
	   * @param children_rindices the <code>RootGraph</code> indices of the nodes for which to find parent nodes
	   * @param find_top_parents whether or not to find parent meta-nodes that don't have any parents themselves
	   * @return an array of <code>RootGraph</code> indices of the parent meta-nodes
	   */
	  public static int [] findParentMetaNodes (CyNetwork cy_net, int [] children_rindices, boolean find_top_parents){
	  	RootGraph rootGraph = cy_net.getRootGraph();
	    IntArrayList parentRootGraphIndices = new IntArrayList();
	    IntArrayList metaNodesForNetwork = (IntArrayList)cy_net.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
	    if(metaNodesForNetwork != null){
	    	for(int i = 0; i < children_rindices.length; i++){
	    		int [] parents = rootGraph.getNodeMetaParentIndicesArray(children_rindices[i]);
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
	    parentRootGraphIndices.trimToSize();
	    
	    if(find_top_parents){
	    	if(parentRootGraphIndices.size() > 0){
	    		int [] ancestors = findParentMetaNodes(cy_net, parentRootGraphIndices.elements(), find_top_parents);
	    		if(ancestors.length == 0){
	    			return parentRootGraphIndices.elements();
	    		}else{
	    			return ancestors;
	    		}
	    	}
	    }// if find_top_parents
	  	return parentRootGraphIndices.elements();
	  }//findParentMetaNodes
	  
}//MetaNodeUtils

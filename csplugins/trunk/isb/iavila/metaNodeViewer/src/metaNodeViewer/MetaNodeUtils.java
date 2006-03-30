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

import java.util.*;
import giny.model.RootGraph;
import metaNodeViewer.data.MetaNodeAttributesHandler;
import metaNodeViewer.model.AbstractMetaNodeModeler;
import metaNodeViewer.model.MetaNodeFactory;
import metaNodeViewer.model.MetaNodeModelerFactory;
import cytoscape.*;
import cytoscape.view.*;
import giny.view.*;

/**
 * Class with easy to use static methods for metanode operations.<p>
 * Most classes using the MetaNodeViewer plugin <b>programatically</b> will call methods in this class.<br>
 * Advanced uses of the plugin require more familiarity with the rest of the classes in this package.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @since 2.3
 */

public class MetaNodeUtils {
    
    /**
     * The object that collapses and expands metanodes, not recommended to use unless you know what you are doing
     */
	  public static final AbstractMetaNodeModeler abstractModeler = 
          MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler();
      
	  
	/**
     * Creates a CyNode that represents the given subnetwork in the given network
     *   
     * @param network the CyNetwork for which the metanode will be created
     * @param subnetwork the CyNetwork that the metanode will represent
     * @param attributes_handler the object that transfers subnetwork node and edge attributes to the metanode's attributes
     * @return a CyNode that represents the given subnetwork, or null if there was an error
	 */
	  public static CyNode createMetaNode (CyNetwork network, 
	                                           CyNetwork subnetwork,
	                                           MetaNodeAttributesHandler attributes_handler){
	  	MetaNodeUtils.abstractModeler.setNetworkAttributesHandler(network, attributes_handler);
	  	return createMetaNode(network,subnetwork);
	  }//abstractToMetaNodes
	  
        /**
         * Creates a CyNode that represents the given subnetwork in the given network
         *   
         * @param network the CyNetwork for which the metanode will be created
         * @param subnetwork the CyNetwork that the metanode will represent
         * @return a CyNode that represents the given subnetwork, or null if there was an error
         */
	  public static CyNode createMetaNode (CyNetwork network, CyNetwork subnetwork){
	    
	    if(subnetwork == null){
	        //Nothing to visualize
	        return null;
	    }
	    
	    RootGraph rootGraph = network.getRootGraph();
	    if(MetaNodeUtils.abstractModeler.getRootGraph() == null ||
	       MetaNodeUtils.abstractModeler.getRootGraph() != rootGraph){
	    	// Theoretically, should never have to do this since there is only one RootGraph during a Cytoscape session
	       MetaNodeUtils.abstractModeler.setRootGraph(rootGraph);
	    }
	    
        //Cytoscape.getDesktop().getGraphViewController().stopListening();

	    Iterator it = subnetwork.nodesIterator();
	    ArrayList childrenArray = new ArrayList(); // the children of the metanode
	    while(it.hasNext()){
	        CyNode node = (CyNode)it.next();
	        childrenArray.add(node);
	    }//for j
	    MetaNodeAttributesHandler attsHandler = MetaNodeUtils.abstractModeler.getNetworkAttributesHandler(network);
	    CyNode metaNode = MetaNodeFactory.createMetaNode(network,childrenArray,attsHandler);
        
	    return metaNode;
	  }//abstractToMetaNodes

	  /**
	   * Expands and then permanently removes the given metanode from the network
	   *
	   * @param network the <code>CyNetwork</code> from which metanodes will be removed
	   * @param metaNode the metanode to remove (must have been created through <code>createMetaNode</code> methods in this class)
	   * @param recursive if there are > 1 levels of metanode hierarchy, whether or not
	   * to remove all the levels (if it is known that there is only 1 level, setting this
	   * to false significantly improves performance)
	   * @return true if the metanode was successfully removed, false otherwise
	   */
	  public static boolean removeMetaNode (CyNetwork network, CyNode metaNode, boolean recursive){
	      boolean removed = false;
	      if(network == null || metaNode == null){
	          return removed;
	      }
	      
          boolean temporary = false; // == don't remember the metanode
	    
          CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
          ArrayList childrenNodes = new ArrayList();
          double xPos = 0;
          double yPos = 0;
          if(netView != null){
              childrenNodes = getNodesInSubnetwork(metaNode,recursive);
              NodeView metaNodeView = netView.getNodeView(metaNode);
              xPos = metaNodeView.getXPosition();
              yPos = metaNodeView.getYPosition();
          }
           
          boolean ok = MetaNodeUtils.abstractModeler.undoModel(network,metaNode,recursive,temporary);
          // Also, remove the metanode from the RootGraph
          RootGraph rootGraph = network.getRootGraph();
          CyNode removedNode = (CyNode)rootGraph.removeNode(metaNode);
          removed = ok && (removedNode != null);
          // Cytoscape no longer remembers the locations of nodes after they are removed. So, we need to lay them out in some way...
          if(childrenNodes.size() > 0) layoutNodesInAStack(netView,childrenNodes,xPos,yPos);
          
          return removed;
	  }//removeMetaNodes
	  
	  /**
	   * Expands the metanode (hides the metanode and restores its subnetwork in the given network), the children nodes
       * of the metanode are layed out as a stack
	   * 
	   * @param cy_network the CyNetwork whithin which the metanode resides and in which it will be expanded
	   * @param metaNode the CyNode to expand (must have been created through <code>createMetaNode</code> methods in this class)
	   * @param recursive whether metanodes inside the given metanode should be expanded
	   * @return true if the metanode was successfully expanded, false otherwise
	   */
	  public static boolean expandMetaNode (CyNetwork network, CyNode metaNode, boolean recursive){
	  	// Uncollapse each node (if it is not a metanode, nothing happens)
	      boolean expanded = false;
	      if(network == null || metaNode == null) return expanded;
          
	      CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
	      ArrayList childrenNodes = new ArrayList();
	      double xPos = 0;
	      double yPos = 0;
	      if(netView != null){
	          childrenNodes = getNodesInSubnetwork(metaNode,recursive);
	          NodeView metaNodeView = netView.getNodeView(metaNode);
	          xPos = metaNodeView.getXPosition();
	          yPos = metaNodeView.getYPosition();
	      }
	      // This only uncollapses the metanodes, but they are kept in the RootGraph
	      expanded = MetaNodeUtils.abstractModeler.undoModel(network,metaNode,recursive,true);
	      // Cytoscape no longer remembers the locations of nodes after they are removed. So, we need to lay them out in some way...
	      if(childrenNodes.size() > 0) layoutNodesInAStack(netView,childrenNodes,xPos,yPos);
	      
          return expanded;
	  }//uncollapseSelectedNodes
	  
	  /**
       * Collapses the given metanode (hides its subnetwork and displays the metanode with edges connected to other
       * nodes in the network)
       * 
       * @param cy_network the CyNetwork in which the given metanode should be collapsed in
       * @param metaNode the metanode to collapse (must have been created through <code>createMetaNode</code> methods in this class)
       * @param show_multiple_edges if true, then multiple edges between the metanode and anotother node are created to
       * represent the metanode's subnetwork connections to that node, if false, only one edge is created to represent these
       * connections
       * @return true if successfully collapsed, false otherwise
	   */
	  public static boolean collapseMetaNode (CyNetwork network, CyNode metaNode, boolean show_multiple_edges){
	      
	      boolean collapsed = false;
          if(network == null || metaNode == null) return collapsed;
          MetaNodeUtils.abstractModeler.setMultipleEdges(show_multiple_edges);
          collapsed = MetaNodeUtils.abstractModeler.applyModel(network,metaNode);
	    
	      return collapsed;
	  }//collapseNodes
	  
	  /**
	   * Finds the metanodes that contain the given children nodes in their subnetworks<p>
       * If <code>find_top_containers</code> is true, 
	   *
       * @param cy_net the CyNetwork within which to look for parent metanodes
	   * @param children an array of CyNodes for which parent nodes are to be found
	   * @param find_top_containers if true, then the top-level
	   * @return an array of CyNode parent nodes
	   */
	  public static ArrayList findContainingMetaNodes (CyNetwork cy_net, ArrayList children, boolean find_top_containers){
	  	
	      RootGraph rootGraph = cy_net.getRootGraph();
	      ArrayList parentNodes = new ArrayList();
	      ArrayList metaNodesForNetwork = (ArrayList)cy_net.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
	      int [] childrenRindices = new int [children.size()];
	      for(int i = 0; i < children.size(); i++) childrenRindices[i] = ((CyNode)children.get(i)).getRootGraphIndex();
	      if(metaNodesForNetwork != null){
	          for(int i = 0; i < children.size(); i++){
	              int [] parents = rootGraph.getNodeMetaParentIndicesArray(childrenRindices[i]);
	              if(parents.length == 1 && metaNodesForNetwork.contains(rootGraph.getNode(parents[0]))){
	                  parentNodes.add(rootGraph.getNode(parents[0]));
	              }else if(parents.length > 1){
	                  // TODO: Think about this better. What to do when a node has more than one parent???
	                  // Maybe pop-up window asking which parent should be collapsed, give the option of collapsing the last one created...???
	                  for(int j = 0; j < parents.length; j++){
	                      if( metaNodesForNetwork.contains(rootGraph.getNode(parents[j])) ){
	                          parentNodes.add(rootGraph.getNode(parents[j]));
	                      }
	                  }//for j
	              }
	          }//for i
	      }// metaNodesForNetwork != null
	    
	    if(find_top_containers){
        	    	if(parentNodes.size() > 0){
        	    		ArrayList ancestors = findContainingMetaNodes(cy_net, parentNodes, find_top_containers);
        	    		if(ancestors.size() == 0){
        	    			return parentNodes;
        	    		}else{
        	    			return ancestors;
        	    		}
        	    	}
	    }// if find_top_parents
	  	
        return parentNodes;
	  
      }//findParentMetaNodes
      
      /**
       * Finds the children nodes (nodes in the subnetwork that the given meta-node represents) of the meta-node.
       * 
       * @param metaNode the CyNode that represents a subnetwork
       * @param get_lowest_level_nodes if true, then the returned array contains nodes that are descendants of the metanode and
       * have no children themselves, if false, the immediate children nodes are returned
       * @return an ArrayList of children CyNodes
       */
      public static ArrayList getNodesInSubnetwork (CyNode metaNode, boolean get_lowest_level_nodes){
          ArrayList childrenNodes = new ArrayList();
          Iterator it = metaNode.getGraphPerspective().nodesIterator();
          while(it.hasNext()){
              CyNode childNode = (CyNode)it.next();
              if(get_lowest_level_nodes && childNode.getGraphPerspective().getNodeCount() > 0) {
                  childrenNodes.addAll(getNodesInSubnetwork(childNode, get_lowest_level_nodes));
              }else{
                  childrenNodes.add(childNode);
              }
          }
          
          return childrenNodes;
      }
      
      //------------ temporary methods ---------------- //
      // These should go somewhere else. Like a GinyLayoutUtils class.
      public static void layoutNodesInAStack (CyNetworkView network_view, Collection nodes, double x_position, double y_start_position){
          
          Iterator it = nodes.iterator();
          double yPosition = y_start_position;
          while(it.hasNext()){
              CyNode node = (CyNode)it.next();
              NodeView nodeView = network_view.getNodeView(node);
              nodeView.setXPosition(x_position);
              nodeView.setYPosition(yPosition);
              yPosition += nodeView.getHeight() * 2;
          }
          
      }
	  
}//MetaNodeUtils

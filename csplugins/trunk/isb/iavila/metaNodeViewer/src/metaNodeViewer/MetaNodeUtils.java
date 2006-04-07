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
import cytoscape.layout.*;
import giny.view.*;

/**
 * Class with easy to use static methods for metanode operations.<p>
 * A metanode is a normal Cytoscape node (CyNode) that represents a network (we call it child-network of the metanode) within another network.
 * After metanodes are created (using create methods in this class), they can be "collapsed" or "expanded". The "collapse" operation consists on replacing the child-network by its parent metanode. The "expand" operation consists on the
 * opposite, that is, replacing the metanode by its child-network. When collapsing a metanode, edges between its children nodes and other nodes are transfered to the metanode. 
 * These operations modify the <b>model</b> of a network. Since network views are synchronized to the network model, the existing views of a network
 * will automatically reflect the model modifications made by methods in this class.<br><br>
 * Most classes using the MetaNodeViewer plugin <b>programatically</b> will call methods in this class.<br>
 * Advanced uses of the plugin require more familiarity with the rest of the classes in this package.<br>
 * Order of calls to use this class go something like this:<br>
 * <PRE>
 * // create the metanode
 * CyNode metaNode = MetaNodeUtils.createMetaNode(network,childNetwork);
 * // collapse the metanode, creating multiple edges
 * MetaNodeUtils.collapseMetaNode(network,metaNode,true);
 * // expand the metanode, not recursive
 * MetaNodeUtils.expandMetaNode(network, metaNode, false);
 * // when I am sure that I will no longer use the metaNode, remove it
 * MetaNodeUtils.removeMetaNode(network,metaNode,false);
 * </PRE>
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @since 2.3
 */

public class MetaNodeUtils {
    
    /**
     * The object that modifies the network model to collapse and expand metanodes, not recommended to use unless you know what you are doing
     */
	  public static final AbstractMetaNodeModeler abstractModeler = 
          MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler();
      
	  
	/**
     * Creates a metanode that represents the given child-network in the given network<p>
     * This method does not collapse the created metanode. It only internally stores the given information in data-structures.
     * 
     * @param network the CyNetwork in which the metanode will represent the child-network
     * @param child_network the CyNetwork that the metanode will represent
     * @param attributes_handler the object that transfers chhild-network node and edge attributes to the metanode's attributes
     * @return a CyNode that represents the given child-network, or null if there was an error
	 */
	  public static CyNode createMetaNode (CyNetwork network, 
	                                           CyNetwork child_network,
	                                           MetaNodeAttributesHandler attributes_handler){
	  	MetaNodeUtils.abstractModeler.setNetworkAttributesHandler(network, attributes_handler);
	  	return createMetaNode(network,child_network);
	  }
	  
      /**
       * Creates a metanode that represents the given child-network in the given network<p>
       * This method does not collapse the created metanode. It only internally stores the given information in data-structures.
       * 
       * @param network the CyNetwork in which the metanode will represent the child-network
       * @param child_network the CyNetwork that the metanode will represent
       * @param attributes_handler the object that transfers chhild-network node and edge attributes to the metanode's attributes
       * @return a CyNode that represents the given child-network, or null if there was an error
       */
	  public static CyNode createMetaNode (CyNetwork network, CyNetwork child_network){
	    
	    if(child_network == null){
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

	    Iterator it = child_network.nodesIterator();
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
       * Sets a network as a child-network for an existing CyNode that after this call becomes a metanode
       * 
       * @param node the CyNode for which to set the child-network
       * @param network the CyNetwork in which the metanode will represent the child-network
       * @param child_network the CyNetwork that the metanode will represent
       */
      public static void setChildNetwork (CyNode node, CyNetwork network, CyNetwork child_network){
          if(network == null || child_network == null || node == null) return;
          RootGraph rootGraph = network.getRootGraph();
          
          if(MetaNodeUtils.abstractModeler.getRootGraph() == null ||
           MetaNodeUtils.abstractModeler.getRootGraph() != rootGraph){
              // Theoretically, should never have to do this since there is only one RootGraph during a Cytoscape session
              MetaNodeUtils.abstractModeler.setRootGraph(rootGraph);
          }
          
          Iterator it = child_network.nodesIterator();
          ArrayList childrenArray = new ArrayList(); // the children of the metanode
          while(it.hasNext()){
            CyNode child = (CyNode)it.next();
            childrenArray.add(child);
          }//for j
          MetaNodeFactory.convertToMetaNode(node,network,childrenArray);
      }

	  /**
	   * Expands and then permanently removes the given metanode from the network
       * <p>
       * Call this method if you are sure that the given metanode will not be collapsed in the future
	   *
	   * @param network the <code>CyNetwork</code> from which the metanode will be removed
	   * @param meta_node the metanode to remove (must have been created through methods in this class)
	   * @param recursive if there are > 1 levels of metanode hierarchy within the metanode to remove, whether or not
	   * to remove all the levels (if it is known that there is only 1 level, setting this
	   * to false significantly improves performance)
	   * @return true if the metanode was successfully removed, false otherwise
	   */
	  public static boolean removeMetaNode (CyNetwork network, CyNode meta_node, boolean recursive){
	      boolean removed = false;
	      if(network == null || meta_node == null){
	          return removed;
	      }
	      
          boolean temporary = false; // == don't remember the metanode
	    
          CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
          List childrenNodes = new ArrayList();
          double xPos = 0;
          double yPos = 0;
          if(netView != null){
              if(recursive)
                  childrenNodes = getBottomLevelChildren(meta_node);
              else
                  childrenNodes = getChildren(meta_node);
              NodeView metaNodeView = netView.getNodeView(meta_node);
              xPos = metaNodeView.getXPosition();
              yPos = metaNodeView.getYPosition();
          }
           
          boolean ok = MetaNodeUtils.abstractModeler.undoModel(network,meta_node,recursive,temporary);
          // Also, remove the metanode from the RootGraph
          RootGraph rootGraph = network.getRootGraph();
          CyNode removedNode = (CyNode)rootGraph.removeNode(meta_node);
          removed = ok && (removedNode != null);
          // Cytoscape no longer remembers the locations of nodes after they are removed. So, we need to lay them out in some way...
          if(childrenNodes.size() > 0) LayoutUtils.layoutNodesInAStack(netView,childrenNodes,xPos,yPos);
          
          return removed;
	  }//removeMetaNodes
	  
	  /**
	   * Expands the metanode in the given network
	   * <p>
       * If the given network has a network view, then the children nodes are layed out in a stack after the metanode is expanded
       * 
	   * @param network the CyNetwork in which the metanode is contained and in which it will be expanded
	   * @param meta_node the CyNode to expand
	   * @param recursive whether metanodes inside the given metanode should be expanded recursively
	   * @return true if the metanode was successfully expanded, false otherwise
	   */
	  public static boolean expandMetaNode (CyNetwork network, CyNode meta_node, boolean recursive){
	  	// Uncollapse each node (if it is not a metanode, nothing happens)
	      boolean expanded = false;
	      if(network == null || meta_node == null) return expanded;
          
	      CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
	      List childrenNodes = new ArrayList();
	      double xPos = 0;
	      double yPos = 0;
	      if(netView != null){
              if(recursive)
                  childrenNodes = getBottomLevelChildren(meta_node);
              else
                  childrenNodes = getChildren(meta_node);
	          NodeView metaNodeView = netView.getNodeView(meta_node);
	          xPos = metaNodeView.getXPosition();
	          yPos = metaNodeView.getYPosition();
	      }
	      // This only uncollapses the metanodes, but they are kept in the RootGraph
	      expanded = MetaNodeUtils.abstractModeler.undoModel(network,meta_node,recursive,true);
	      // Cytoscape no longer remembers the locations of nodes after they are removed. So, we need to lay them out in some way...
	      if(childrenNodes.size() > 0) LayoutUtils.layoutNodesInAStack(netView,childrenNodes,xPos,yPos);
	      
          return expanded;
	  }//uncollapseSelectedNodes
	  
	  /**
       * Collapses the given metanode in the given network
       * 
       * @param network the CyNetwork in which the given metanode should be collapsed in
       * @param meta_node the metanode to collapse
       * @param create_multiple_edges if true, then multiple edges between the metanode and anotother node are created to
       * represent the metanode's child-network connections to that node, if false, only one edge is created to represent these
       * connections
       * @return true if successfully collapsed, false otherwise
	   */
	  public static boolean collapseMetaNode (CyNetwork network, CyNode meta_node, boolean create_multiple_edges){
	      
	      boolean collapsed = false;
          if(network == null || meta_node == null) return collapsed;
          MetaNodeUtils.abstractModeler.setMultipleEdges(create_multiple_edges);
          collapsed = MetaNodeUtils.abstractModeler.applyModel(network,meta_node);
	    
	      return collapsed;
	  }//collapseNodes
	  
	  /**
	   * Finds and returns the immediate parents of the given child node
	   *
       * @param network the CyNetwork within which to look for parent metanodes
	   * @param child the CyNode to look for
	   * @return an array of CyNode parent nodes
	   */
	  public static List getParents (CyNetwork network, CyNode child){
	      RootGraph rootGraph = network.getRootGraph();
	      ArrayList parentNodes = new ArrayList();
	      ArrayList metaNodesForNetwork = (ArrayList)network.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
	      if(metaNodesForNetwork != null){
	          int [] parents = rootGraph.getNodeMetaParentIndicesArray(child.getRootGraphIndex());
	          for(int j = 0; j < parents.length; j++){
	              if( metaNodesForNetwork.contains(rootGraph.getNode(parents[j])) ){
	                  parentNodes.add(rootGraph.getNode(parents[j]));
	              }
	          }//for j
	      }// metaNodesForNetwork != null
        return parentNodes;
      }
      
      /**
       * Returns true if the given node has parent metanodes in the given network, false otherwise
       * 
       * @param network the network in which to look for parent nodes
       * @param node the CyNode for which to look for parents
       * @return true if the node has at least one parent metanode in the network, false otherwise
       */
      public static boolean hasParents (CyNetwork network, CyNode node){
          RootGraph rootGraph = network.getRootGraph();
          ArrayList metaNodesForNetwork = (ArrayList)network.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
          if(metaNodesForNetwork != null){
              int [] parents = rootGraph.getNodeMetaParentIndicesArray(node.getRootGraphIndex());
              for(int j = 0; j < parents.length; j++){
                  if( metaNodesForNetwork.contains(rootGraph.getNode(parents[j])) ){
                     return true;
                  }
              }//for j
              
          }// metaNodesForNetwork != null
        
        return false;
      }
      
      /**
       * Finds and returns the top level parents of the given child node
       * 
       * @param network the network in which to look for parent nodes
       * @param child the CyNode for which to look for parents
       * @return a List of parent CyNodes
       */
      public static List getTopLevelParents (CyNetwork network, CyNode child){   
          ArrayList topParents = new ArrayList();
          Iterator it = getParents(network, child).iterator();
          while(it.hasNext()){
              CyNode parentNode = (CyNode)it.next();
              if(hasParents(network,parentNode)) topParents.addAll(getTopLevelParents(network,parentNode));
              else topParents.add(parentNode);
          }
          return topParents;
      }
      
      /**
       * Finds the immediate children nodes of the meta-node and returns them
       * 
       * @param metaNode the CyNode that represents a subnetwork
       * @return an ArrayList of children CyNodes
       */
      public static List getChildren (CyNode meta_node){
          ArrayList childrenNodes = new ArrayList();
          Iterator it = meta_node.getGraphPerspective().nodesIterator();
          while(it.hasNext()){
              CyNode childNode = (CyNode)it.next();
              childrenNodes.add(childNode);   
          }
          return childrenNodes;
      }
      
      /**
       * Returns the descendant nodes of the given metanode that are not meta-nodes themselves
       *
       * @param meta_node  the CyNode for which to return bottom level descendant nodes
       * @return a List of CyNodes
       */
      public static List getBottomLevelChildren (CyNode meta_node){
          ArrayList descendants = new ArrayList();
          Iterator it = meta_node.getGraphPerspective().nodesIterator();
          while(it.hasNext()){
              CyNode childNode = (CyNode)it.next();
              if(isMetaNode(childNode)) descendants.addAll(getBottomLevelChildren(childNode));
              else descendants.add(childNode);   
          }
          return descendants;
      }
      
      /**
       * Returns true if the given node is a metanode, false otherwise
       * 
       * @param meta_node the CyNode to test
       * @return true if the given node has children nodes, false otherwise
       */
      public static boolean isMetaNode (CyNode meta_node){
          return meta_node.getGraphPerspective().getNodeCount() > 0;
      }
      
      /**
       * Returns all CyNodes that are metanodes in the given network, whether they are collapsed or expanded
       * 
       * @param network the CyNetwork in which to look for metanodes
       * @return a List of CyNodes that are metanodes in the given network
       */
      public static List getAllMetaNodes (CyNetwork network){
          List metaNodesForNetwork = (ArrayList)network.getClientData(MetaNodeFactory.METANODES_IN_NETWORK); 
          return metaNodesForNetwork;
      }
      
      
	  
}//MetaNodeUtils

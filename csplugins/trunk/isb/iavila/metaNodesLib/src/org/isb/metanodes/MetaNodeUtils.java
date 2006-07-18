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
package org.isb.metanodes;

import java.util.*;
import giny.model.RootGraph;
import org.isb.metanodes.data.MetaNodeAttributesHandler;
import org.isb.metanodes.model.AbstractMetaNodeModeler;
import org.isb.metanodes.model.MetaNodeFactory;
import org.isb.metanodes.model.MetaNodeModelerFactory;
import cytoscape.*;
import cytoscape.data.*;

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
 * // collapse the metanode, creating multiple edges and no edges representing meta-relationships (read below)
 * MetaNodeUtils.collapseMetaNode(network,metaNode,true,false);
 * // expand the metanode, not recursive
 * MetaNodeUtils.expandMetaNode(network, metaNode, false);
 * // when I am sure that I will no longer use the metaNode, remove it
 * MetaNodeUtils.removeMetaNode(network,metaNode,false);
 * </PRE>
 * Additionally, meta-nodes can optionally have "meta-relationship edges". These edges can be of two types:<br>
 * <UL>
 * <LI>"Shared child" edges: if two metaNodes share a child node, there will be an edge between them
 * <LI>"Child of" edges: if a child node has two metaNode parents, and one of them is collapsed, and
 * the other one expanded, the child node will be visible and have an edge to the collapsed parent metaNode
 * </UL>
 * This option can be given as an argument when calling the collapsing method in this class.
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
     * This method does not collapse the created metanode. It only internally stores the given information in data-structures.<br>
     * Note that all connecting edges between nodes in the child-network that are not in the child-network, but are in the <code>network</code>
     * paremeter, are also considered child edges of the metanode and addded to its child-network automatically. This means you don't have to find connecting
     * edges between the children nodes in <code>network</code> to create the metanode.
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
       * This method does not collapse the created metanode. It only internally stores the given information in data-structures.<br>
       * Note that all connecting edges between nodes in the child-network that are not in the child-network, but are in the <code>network</code>
       * paremeter, are also considered child edges of the metanode and addded to its child-network automatically. This means you don't have to find connecting
       * edges between the children nodes in <code>network</code> to create the metanode.
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
	    //System.err.println("-------- MetaNodeUtils.createMetaNode, attsHandler.getNodeLabelAttribute = " + attsHandler.getNodeLabelAttribute());
	    CyNode metaNode = MetaNodeFactory.createMetaNode(network,childrenArray,attsHandler);
        
	    return metaNode;
	  }//abstractToMetaNodes
      
      /**
       * Sets a network as a child-network for an existing CyNode that after this call becomes a metanode
       * <p>
       * If the CyNode is already a metanode, it will be first removed from the given network<br>
       * Note that all connecting edges between nodes in the child-network that are not in the child-network, but are in the <code>network</code>
       * paremeter, are also considered child edges of the metanode and addded to its child-network automatically. This means you don't have to find connecting
       * edges between the children nodes in <code>network</code> to create the metanode.<br>
       * <b>Important note:</b> <code>child_network</code> should not contain the <code>node</code> given as a parameter.
       * 
       * @param node the CyNode for which to set the child-network
       * @param network the CyNetwork in which the metanode will represent the child-network
       * @param child_network the CyNetwork that the metanode will represent
       * @throws IllegalArgumentException if <code>child_network</code> contains the input node that will become a metanode (a metanode cannot be its own parent)
       */
      public static void setChildNetwork (CyNode node, CyNetwork network, CyNetwork child_network) throws IllegalArgumentException{
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
          
          if(isMetaNode(node)) removeMetaNode(network,node,false);
          MetaNodeFactory.convertToMetaNode(node,network,childrenArray);
      }
      
      /**
       * Returns the child-network of the given metanode, or null if the node is not a metanode
       * 
       * @param meta_node the metaNode for which to return the child-network
       * @return a CyNetwork that is the child-network of the CyNode, null if the CyNode is not a meta-noded
       */
      public static CyNetwork getChildNetwork (CyNode meta_node){
          CyNetwork childNet = (CyNetwork)meta_node.getGraphPerspective();
          if(childNet.getNodeCount() == 0) return null;
          return childNet;
      }

	  /**
	   * Expands and then permanently removes the given metanode from the network
       * <p>
       * Call this method if you are sure that the given metanode will not be collapsed in the future<br>
       * Note that this method DOES NOT remove the metanode's child-network from the given <code>network</code>.
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
	      if(network == null || meta_node == null || !isMetaNode(meta_node)){
	          return removed;
	      }
	      
          boolean temporary = false; // == don't remember the metanode
	    
/*
          CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
          List childrenNodes = new ArrayList();
          double xPos = 0;
          double yPos = 0;
          boolean hasNodeView = true;
          if(netView != null){
              if(recursive)
                  childrenNodes = getLeafChildren(meta_node);
              else
                  childrenNodes = getChildren(meta_node);
              NodeView metaNodeView = netView.getNodeView(meta_node);
              if(metaNodeView != null){
            	  xPos = metaNodeView.getXPosition();
            	  yPos = metaNodeView.getYPosition();
            	  
              }else{
            	  hasNodeView = false;
              }
          }
*/
           
          boolean ok = MetaNodeUtils.abstractModeler.undoModel(network,meta_node,recursive,temporary);
          // Also, remove the metanode from the RootGraph
          RootGraph rootGraph = network.getRootGraph();
          CyNode removedNode = (CyNode)rootGraph.removeNode(meta_node);
          removed = ok && (removedNode != null);
          // Cytoscape no longer remembers the locations of nodes after they are removed. So, we need to lay them out in some way...
          // if(hasNodeView && childrenNodes.size() > 0) LayoutUtils.layoutNodesInAStack(netView,childrenNodes,xPos,yPos);
          
          return removed;
	  }//removeMetaNodes
	  
	  /**
	   * Expands the metanode in the given network
       * 
	   * @param network the CyNetwork in which the metanode is contained and in which it will be expanded
	   * @param meta_node the CyNode to expand
	   * @param recursive whether metanodes inside the given metanode should be expanded recursively
	   * @return true if the metanode was successfully expanded, false otherwise
	   */
	  public static boolean expandMetaNode (CyNetwork network, CyNode meta_node, boolean recursive){
	  	// Uncollapse each node (if it is not a metanode, nothing happens)
	      boolean expanded = false;
	      if(network == null || meta_node == null || !isMetaNode(meta_node)) return expanded;
          
/*
	      CyNetworkView netView = Cytoscape.getNetworkView(network.getIdentifier());
	      List childrenNodes = new ArrayList();
	      double xPos = 0;
	      double yPos = 0;
	      boolean hasNodeView = true;
	      if(netView != null){
              if(recursive)
                  childrenNodes = getLeafChildren(meta_node);
              else
                  childrenNodes = getChildren(meta_node);
	          NodeView metaNodeView = netView.getNodeView(meta_node);
	          if(metaNodeView != null){
	          	xPos = metaNodeView.getXPosition();
	          	yPos = metaNodeView.getYPosition();
	          }else{
	        	hasNodeView = false;
	          }
	      }
*/
	      // This only uncollapses the metanodes, but they are kept in the RootGraph
	      expanded = MetaNodeUtils.abstractModeler.undoModel(network,meta_node,recursive,true);
	      // Cytoscape no longer remembers the locations of nodes after they are removed. So, we need to lay them out in some way...
	      // if(hasNodeView && childrenNodes.size() > 0) LayoutUtils.layoutNodesInAStack(netView,childrenNodes,xPos,yPos);
	      
          return expanded;
	  }//uncollapseSelectedNodes
	  
	  /**
       * Collapses the given metanode in the given network
       * <p>
       * The <code>create_multiple_edges</code> parameter only takes effect the FIRST TIME this method is called for a metanode.<br>
       * Subsequent collapse operations on the metanode will ignore the value of this parameter, and use the value that was given the first time.
       * 
       * @param network the CyNetwork in which the given metanode should be collapsed in
       * @param meta_node the metanode to collapse
       * @param create_multiple_edges if true, then multiple edges between the metanode and another node are created to
       * represent the metanode's child-network connections to that node, if false, only one edge is created to represent these
       * connections
       * @param create_meta_relationship_edges if true, then edges between meta-nodes that share a child ("sharedChild" edges) and edges
       * between meta-nodes and their children ("childOf" edges) are created
       * @return true if successfully collapsed, false otherwise
	   */
	  public static boolean collapseMetaNode (CyNetwork network, CyNode meta_node, boolean create_multiple_edges, boolean create_meta_relationship_edges){
	      
	      boolean collapsed = false;
          if(network == null || meta_node == null || !isMetaNode(meta_node)) return collapsed;
          MetaNodeUtils.abstractModeler.setMultipleEdges(create_multiple_edges);
          MetaNodeUtils.abstractModeler.setCreateMetaRelationshipEdges(create_meta_relationship_edges);
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
	      List metaNodesForNetwork = (ArrayList)getAllMetaNodes(network);
	      if(metaNodesForNetwork != null){
	          int [] parents = rootGraph.getNodeMetaParentIndicesArray(child.getRootGraphIndex());
	          for(int j = 0; j < parents.length; j++){
	              if( metaNodesForNetwork.contains(rootGraph.getNode(parents[j])) ){
	                  parentNodes.add(rootGraph.getNode(parents[j]));
	              }
	          }//for j
	      }// metaNodeIDsForNetwork != null
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
          List metaNodesForNetwork = (ArrayList)getAllMetaNodes(network);
          if(metaNodesForNetwork != null){
              int [] parents = rootGraph.getNodeMetaParentIndicesArray(node.getRootGraphIndex());
              for(int j = 0; j < parents.length; j++){
	          if( metaNodesForNetwork.contains(rootGraph.getNode(parents[j])) ){
                     return true;
                  }
              }//for j
              
          }// metaNodeIDsForNetwork != null
        
        return false;
      }
      
      /**
       * Finds and returns the top level parents of the given child node
       * 
       * @param network the network in which to look for parent nodes
       * @param child the CyNode for which to look for parents
       * @return a List of parent CyNodes
       */
      public static List getRootParents (CyNetwork network, CyNode child){   
          ArrayList topParents = new ArrayList();
          Iterator it = getParents(network, child).iterator();
          while(it.hasNext()){
              CyNode parentNode = (CyNode)it.next();
              if(hasParents(network,parentNode)) topParents.addAll(getRootParents(network,parentNode));
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
      public static List getLeafChildren (CyNode meta_node){
          ArrayList descendants = new ArrayList();
          Iterator it = meta_node.getGraphPerspective().nodesIterator();
          while(it.hasNext()){
              CyNode childNode = (CyNode)it.next();
              if(isMetaNode(childNode)) descendants.addAll(getLeafChildren(childNode));
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
       * Returns true if the given metanode is collapsed in the given network, false if it is expanded,
       * or if it is not a metanode at all
       * 
       * @param network the CyNetwork in which the metanode is collapsed
       * @param meta_node the CyNode to test
       * @return true if the node is a metanodes and is collapsed, false otherwise
       */
      public static boolean isCollapsed (CyNetwork network, CyNode meta_node){
          // The node needs to be in the network
          return isMetaNode(meta_node) && network.containsNode(meta_node);
          
      }
      
      /**
       * Returns all CyNodes that are metanodes in the given network, whether they are collapsed or expanded
       * 
       * @param network the CyNetwork in which to look for metanodes
       * @return a List of CyNodes that are metanodes in the given network
       */
      public static List getAllMetaNodes (CyNetwork network){
          RootGraph rootGraph = network.getRootGraph();
          Iterator it = getAllMetaNodeIDs(network).iterator();
          ArrayList metaNodesForNetwork = new ArrayList();
          while (it.hasNext()) {
              metaNodesForNetwork.add((CyNode)rootGraph.getNode(((Integer)it.next()).intValue()));
          }
          return metaNodesForNetwork;
      }

      /**
       * Returns indices of nodes that are metanodes in the given network, whether they are collapsed or expanded
       *
       * @param network the CyNetwork in which to look for metanodes
       * @return a List of indices for CyNodes that are metanodes in the given network
       */
      public static List getAllMetaNodeIDs (CyNetwork network){
          CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
          ArrayList metaNodeIDsForNetwork = (ArrayList)netAttributes.getAttributeList(network.getIdentifier(),MetaNodeFactory.METANODES_IN_NETWORK); 
          if (metaNodeIDsForNetwork == null) {
              metaNodeIDsForNetwork = new ArrayList();
          }
          return metaNodeIDsForNetwork;
      }

}//MetaNodeUtils

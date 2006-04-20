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
package org.isb.metanodes.model;

import org.isb.metanodes.data.*;
import giny.model.*;
import cytoscape.*;

import java.util.*;

/**
 * Creates meta-nodes for a given CyNetwork and keeps track
 * of which meta-nodes belong to which network (since all CyNetworks share a RootGraoh).
 * The newly created meta-nodes are only contained within the RootGraph, and not in the CyNetwork.
 * Use metaNodeViewer.model.MetaNodeFactory instead, since it has easy to use static
 * methods to create meta-nodes.
 * Named GPMetaNodeFactory because when it was written, Cytoscape only had GraphPerspectives, not CyNetworks.
 */

public class GPMetaNodeFactory {

	protected static final boolean DEBUG = true;

	/**
	 * Specifies whether or not this class should assign a name to newly created
	 * meta-nodes
	 */
	protected boolean assignDefaultName;

	/**
	 * The MetaNodeAttributesHandler that names newly created meta-nodes
	 */
	protected MetaNodeAttributesHandler attributesHandler;

	/**
	 * Constructor. Calls
	 * <code>this(MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER, true)</code>.
	 */
	public GPMetaNodeFactory() {
		this(MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER, true);
	}// GPMetaNodeFactory

	/**
	 * Constructor.
	 * 
	 * @param attributes_handler
	 *            the MetaNodeAttributesHandler that should be used to name
	 *            meta-nodes and to add a [name-->Node] mapping as an attribute,
	 *            calls GPMetaNodeFactory(attributes_handler,true)
	 */
	public GPMetaNodeFactory(MetaNodeAttributesHandler attributes_handler) {
		this(attributes_handler, true);
	}// GPMetaNodeFactory

	/**
	 * Constructor.
	 * 
	 * @param attributes_handler
	 *            the MetaNodeAttributesHandler that should be used to name
	 *            meta-nodes and add a [name-->Node] mapping into
	 *            GraphObjAttributes for nodes
	 * @param assign_names
	 *            whether or not this class should assign new names to newly
	 *            created nodes and set this names in the node object attributes
	 */
	public GPMetaNodeFactory(MetaNodeAttributesHandler attributes_handler,
			boolean assign_names) {
		this.assignDefaultName = assign_names;
		this.attributesHandler = attributes_handler;
	}// GPMetaNodeFactory

	/**
	 * Sets whether or not a default name for newly created meta-nodes should be
	 * given
	 */
	public void assignDefaultNames(boolean assign) {
		this.assignDefaultName = assign;
	}// assignDefaultNames

	/**
	 * Whether or not default names are being assigned to newly created
	 * meta-nodes
	 */
	public boolean getAssignDefaultNames() {
		return this.assignDefaultName;
	}// getAssignDefaultNames

	/**
	 * Creates a meta-node within CyNetwork's RootGraph, a default name is given
	 * to the meta-node by this.attributesHandler (if getAssignDefaultNames() is
	 * true). Note that the new meta-node is not contained in cy_net, but, after
	 * calling this method, it is recorded that the new meta-node belongs to
	 * cy_net.
	 * 
	 * @param cy_net
	 *            he CyNetwork for which a meta-node will be created
	 * @param children
	 *            the meta-node's children CyNodes that should be
	 *            in cy_net
	 * @return the new CyNode
	 */
	public CyNode createMetaNode(CyNetwork cy_net, ArrayList children) {
		return createMetaNode(cy_net, children,
				this.attributesHandler);
	}// createMetaNode

	/**
	 * Creates a meta-node within CyNetwork's RootGraph, a default name is given
	 * to the meta-node by the given MetaNodeAttributesHandler (if
	 * getAssignDefaultNames() is true). Note that the new meta-node is not
	 * contained in cy_net, but, after calling this method, it is recorded that
	 * the new meta-node belongs to cy_net.
	 * 
	 * @param cy_net
	 *            the CyNetwork for which a meta-node will be created
	 * @param children an array of CyNodes that are the children of the meta-node
	 * @param attributes_handler
	 *            the MetaNodeAttributesHandler to be used to assign a name to
	 *            the new meta-node (if getAssignDefaultNames() is true)
	 * @return the newly created meta-node, or null if
	 *         something went wrong
	 */
	public CyNode createMetaNode(CyNetwork cy_net, ArrayList children,
			MetaNodeAttributesHandler attributes_handler) {

		if (children == null || cy_net == null
				|| children.size() == 0) {
			if (DEBUG) {
				System.err
						.println("GPMetaNodeFactory.createMetaNode(CyNetwork="
								+ cy_net + ", children="
								+ children
								+ ") : wrong input, returning 0");
			}
			return null;
		}// check args

        int [] childrenNodeIndices = new int[children.size()];
        for(int i = 0; i < children.size(); i++) childrenNodeIndices[i] = ( (CyNode)children.get(i) ).getRootGraphIndex();
        
		// Get the RootGraph indices of the edges that connect to the selected nodes
		RootGraph rootGraph = cy_net.getRootGraph();
		int[] edgeIndices = cy_net
				.getConnectingEdgeIndicesArray(childrenNodeIndices);
		if (edgeIndices != null) {
			for (int i = 0; i < edgeIndices.length; i++) {
				if (edgeIndices[i] > 0) {
					int rootEdgeIndex = cy_net.getRootGraphEdgeIndex(edgeIndices[i]);
					edgeIndices[i] = rootEdgeIndex;
				}// if rootEdgeIndex > 0
			}// for i
		}// if edgeIndices != null
		
		// Create a node in RootGraph that contains inside it the selected nodes
		// and their connected edges
		// TODO: Maybe move this to createNode(Node[],Edge[])???? (12.11.05)
        int rgParentNodeIndex = rootGraph.createNode(childrenNodeIndices, edgeIndices);
        CyNode metaNode = (CyNode)rootGraph.getNode(rgParentNodeIndex);
        if(metaNode == null) throw new IllegalStateException("CyNode from RootGraph with index = " + rgParentNodeIndex + " is null!!!");
        
		// Remember that this RootGraph node belongs to cyNetwork
		ArrayList rootNodes = (ArrayList) cy_net.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
		if (rootNodes == null) {
			rootNodes = new ArrayList();
			cy_net.putClientData(MetaNodeFactory.METANODES_IN_NETWORK,rootNodes);
		}
		rootNodes.add(metaNode);

		// Assign a default name if necessary
		if (getAssignDefaultNames()) {
			if (attributes_handler.assignName(cy_net,metaNode) == null) {
				// Failed to assign a default name, but the node has been
				// created, so just
				// print a debug statement
				if (DEBUG) {
					System.err
							.println("Alert! GPMetaNodeFactory.createMetaNode: No assigned name for new node");
				}
			}// if(!assignName)
		}// if(getAssignDefaultNames())

		return metaNode;
	}// createMetaNode
    
    /**
     * Converts the given node into a metanode
     * 
     * @param cy_net the CyNetwork in which the node will be a metanode
     * @param node the node that will become a metanode
     * @param children the nodes that will be children nodes of the metanode
     * @throws IllegalArgumentException if the List of children contains the node that will become a metanode
     */
    public void makeMetaNode (CyNetwork cy_net, CyNode node, List children ) throws IllegalArgumentException{
        if (children == null || cy_net == null
                || children.size() == 0) {
            if (DEBUG)
                System.err
                        .println("GPMetaNodeFactory.createMetaNode(CyNetwork="
                                + cy_net + ", children="
                                + children
                                + ") : wrong input, returning 0");
            
        }// check args

        Iterator it = children.iterator();
        while(it.hasNext()){
            CyNode child = (CyNode)it.next();
            if(child == node)
                    throw new IllegalArgumentException("A node cannot be its own parent metanode. Node:" + node);
        }
        
        List edges = cy_net.getConnectingEdges(children);
        
        // Create a network and set it for the node
        CyNetwork childnet = Cytoscape.getRootGraph().createNetwork(children,edges);
        node.setGraphPerspective(childnet);
        
        // Remember that this RootGraph node belongs to cyNetwork
        ArrayList rootNodes = (ArrayList) cy_net.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
        if (rootNodes == null) {
            rootNodes = new ArrayList();
            cy_net.putClientData(MetaNodeFactory.METANODES_IN_NETWORK,rootNodes);
        }
        rootNodes.add(node);
        
    }

	/**
	 * Creates a new name of the form MetaNode_<root_node_index> and adds
	 * an object-name mapping in the node attributes contained in CyNetwork for
	 * the given node.
	 * 
	 * @return false if it failed to assign a name, or true if successful
	 */
	protected boolean assignDefaultName(CyNetwork cy_net, CyNode node) {
		return (this.attributesHandler.assignName(cy_net, node) != null);
	}// assignDefaultName

	/**
	 * Clears the Factory, right now it does nothing...
	 */
	public void clear() {
		// ?
	}// clear

}// GPMetaNodeFactory

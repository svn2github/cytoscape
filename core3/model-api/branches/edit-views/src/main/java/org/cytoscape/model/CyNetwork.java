
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package org.cytoscape.model;

import java.util.List;
import java.util.Map;


/**
 * CyNetwork is the primary class for algorithm writing.  All algorithms should take a 
 * CyNetwork as input, and do their best to only use the API of CyNetwork.  CyNetwork
 * provides a straightforward interface to a simple graph.  For an interface to a
 * more complicated subgraph, see {@link org.cytoscape.model.subnetwork.CyRootNetwork} 
 * and {@link org.cytoscape.model.subnetwork.CySubNetwork}, both
 * of which inherit from CyNetwork.
 *
 * The CyNetwork interface provides most of the methods a plugin would need to create
 * a graph, traverse it, get and set attributes, and inquire about the existence of
 * nodes and edges.
 */
public interface CyNetwork extends Identifiable, GraphObject {
	/**
	 * The "USER" CyDataTable is created by default for CyNetworks, CyNodes, and
	 * CyEdges.  Other CyDataTables may also be associated -- see {@link CyDataTable}
	 * for more information.  The table should be referenced using this constant:
	 * <code>CyNetwork.DEFAULT_ATTRS</code>.
	 */
	String DEFAULT_ATTRS = "USER";

	/**
	 * The "HIDDEN" CyDataTable is created by default for CyNetworks, CyNodes, and
	 * CyEdges.  Other CyDataTables may also be associated -- see {@link CyDataTable}
	 * for more information.
	 */
	String HIDDEN_ATTRS = "HIDDEN";

	/**
	 * Returns an EditProxy object, which can be used to collect a
	 * large number of edits. These edits can be merged back into
	 * the original object (i.e. this object) by calling
	 * .mergeEdits() on the EditProxy object.
	 *
	 * Note to implementors: if you don't care about batching
	 * events in this way, simply implement this method with
	 * "return this;" and the mergeEdits() method with "return;".
	 *
	 * @return an edit proxy that may or may not batch edits
	 */
	CyNetwork getEditProxy();

	/**
	 * If this instance is an EditProxy of another CyNetwork and
	 * there are pending edits, batch those edits and commit them
	 * to the parent CyNetwork. Otherwise, do nothing.
	 *
	 */
	void mergeEdits();


	/**
	 * This method is used to create and add a node to this network.
	 *
	 * @return the created node
	 */
	CyNode addNode();

	/**
	 * Remove a node from the network and delete the node (if it only exists in 
	 * this network).  See {@link org.cytoscape.model.subnetwork.CyRootNetwork} 
	 * for information about having the same node in two networks.
	 *
	 * @param node the node to be deleted
	 * @return true if the node was successfully deleted
	 */
	boolean removeNode(CyNode node);

	/**
	 * This method is used to create and add an edge to this network.
	 *
	 * @param source the source (or start) of the edge
	 * @param target the target (or end) of the edge
	 * @param isDirected if 'true' this is a directed edge
	 * @return the created edge
	 */
	CyEdge addEdge(CyNode source, CyNode target, boolean isDirected);

	/**
	 * Remove an edge from the network and delete the edge (if it only exists in
	 * this network).  See {@link org.cytoscape.model.subnetwork.CyRootNetwork} 
	 * for information about having the same edge in two networks.
	 *
	 * @param edge the edge to be deleted
	 * @return true if the edge was successfully deleted
	 */
	boolean removeEdge(CyEdge edge);

	/**
	 * Return the number of nodes in this network.
	 *
	 * @return the number of nodes
	 */
	int getNodeCount();

	/**
	 * Return the number of edges in this network.
	 *
	 * @return the number of edges
	 */
	int getEdgeCount();

	/**
	 * Return a list of the nodes in this network.
	 *
	 * @return the node list
	 */
	List<CyNode> getNodeList();

	/**
	 * Return a list of the edges in this network.
	 *
	 * @return the edge list
	 */
	List<CyEdge> getEdgeList();

	/**
	 * Determine if this CyNetwork contains a particular node.
	 *
	 * @param node the node to check
	 * @return true if this network contains the node
	 */
	boolean containsNode(CyNode node);

	/**
	 * Determine if this CyNetwork contains a particular edge.
	 *
	 * @param edge the edge to check
	 * @return true if this network contains the edge
	 */
	boolean containsEdge(CyEdge edge);

	/**
	 * Determine if this CyNetwork contains an edge between
	 * two nodes.  Note that if the edge is directed, the
	 * source and targets must match.
	 *
	 * @param  from the source of the edge
	 * @param  to the target of the edge
	 * @return true if this network contains the edge
	 */
	boolean containsEdge(CyNode from, CyNode to);

	/**
	 * Return the CyNode that has the index.
	 *
	 * @param index the index of the CyNode to get
	 * @return the associated CyNode or null if there is no
	 * node with that index in this network.
	 */
	CyNode getNode(int index);

	/**
	 * Return the CyEdge that has the index.
	 *
	 * @param index the index of the CyEdge to get
	 * @return the associated CyEdge or null if there is no
	 * edge with that index in this network.
	 */
	CyEdge getEdge(int index);

	/**
	 * Get the list of nodes that neighbor this node where the
	 * definition of "neighbor" is a node that is connected to this
	 * node by the passed edgeType.  The {@link CyEdge.Type} enum is
	 * used to determine whether the list includes undirected, directed,
	 * incoming, or outgoing edges.
	 *
	 * @param node the node whose neighbors we're looking for
	 * @param edgeType the directionality of the edges we're interested in
	 * @return the list of nodes that neighbor this node
	 */
	List<CyNode> getNeighborList(CyNode node, CyEdge.Type edgeType);

	/**
	 * Get the list of edges that connect to this node. The {@link CyEdge.Type} enum is
	 * used to determine whether the list includes undirected, directed,
	 * incoming, or outgoing edges.
	 *
	 * @param node the node whose edges we're looking for
	 * @param edgeType the directionality of the edges we're interested in
	 * @return the list of edges that are adjacent to this one
	 */
	List<CyEdge> getAdjacentEdgeList(CyNode node, CyEdge.Type edgeType);

	/**
	 * Get the list of edges that connect two nodes.  The {@link CyEdge.Type} enum is
	 * used to determine whether the list includes undirected, directed,
	 * incoming, or outgoing edges.
	 *
	 * @param source the source node
	 * @param target the target node
	 * @param edgeType the directionality of the edges we're interested in
	 * @return the list of edges that include source and target and directed edges.
	 */
	List<CyEdge> getConnectingEdgeList(CyNode source, CyNode target, CyEdge.Type edgeType);

	/**
	 * Return the map of {@link CyDataTable}s for this network.  The map is indexed by the
	 * name of the data table (e.g. "USER")
	 *
	 * @return the map of {@link CyDataTable}s for this network
	 */
	Map<String,CyDataTable> getNetworkCyDataTables();

	/**
	 * Return the map of {@link CyDataTable}s for nodes in this network.  The map is indexed by the
	 * name of the data table (e.g. "USER")
	 *
	 * @return the map of {@link CyDataTable}s for nodes in this network
	 */
	Map<String,CyDataTable> getNodeCyDataTables();

	/**
	 * Return the map of {@link CyDataTable}s for edges in this network.  The map is indexed by the
	 * name of the data table (e.g. "USER")
	 *
	 * @return the map of {@link CyDataTable}s for edges in this network
	 */
	Map<String,CyDataTable> getEdgeCyDataTables();
}


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
package org.cytoscape.model.internal;


import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.SUIDFactory;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.model.subnetwork.CyRootNetwork;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of CySubNetwork that is largely a passthrough to
 * {@link ArrayGraph}.
 */
class ArraySubGraph implements CySubNetwork {
	private final int internalId;
	private final long internalSUID;
	private final ArrayGraph parent;
	private int internalNodeCount;
	private int internalEdgeCount;
	private NodePointer inFirstNode;
	private Set<CyNode> nodeSet;
	private Set<CyEdge> edgeSet;

	ArraySubGraph(final ArrayGraph par, final int inId) {
		assert(par!=null);
		parent = par;
		internalId = inId;

		internalSUID = SUIDFactory.getNextSUID();
		
		nodeSet = new HashSet<CyNode>();
		edgeSet = new HashSet<CyEdge>();

		internalNodeCount = 0; 
		internalEdgeCount = 0; 
	}

	private void updateNode(final CyNode n) {
		final NodePointer node = parent.getNodePointer(n);
		node.expandTo(internalId);

		inFirstNode = node.insert(inFirstNode, internalId);
	}

	private void updateEdge(final CyEdge edge) {
		final NodePointer source = parent.getNodePointer(edge.getSource());
		source.expandTo(internalId);

		final NodePointer target = parent.getNodePointer(edge.getTarget());
		target.expandTo(internalId);

		final EdgePointer e = parent.getEdgePointer(edge);
		e.expandTo(internalId);

		e.insert(internalId);
	}

	/**
	 * {@inheritDoc}
	 */
	public CyRootNetwork getRootNetwork() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public long getSUID() {
		return internalSUID;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNode addNode() {
		//System.out.println("base addNode null");
		final CyNode ret; 
		synchronized (this) {
			ret = parent.nodeAdd(this);
			updateNode(ret);
			internalNodeCount++;
			nodeSet.add(ret);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge addEdge(final CyNode source, final CyNode target, final boolean isDirected) {
		// important that it's edgeAdd and not addEdge
		final CyEdge ret; 
		synchronized (this) {
			ret = parent.edgeAdd(source, target, isDirected,this); 
			updateEdge(ret);
			internalEdgeCount++;
			edgeSet.add(ret);
		}

		return ret;
	}


	/**
	 * {@inheritDoc}
	 */
	public synchronized int getNodeCount() {
		return internalNodeCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized int getEdgeCount() {
		return internalEdgeCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized List<CyNode> getNodeList() {
		return parent.getNodeList(inFirstNode, internalId, internalNodeCount);
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized List<CyEdge> getEdgeList() {
		return parent.getEdgeList(inFirstNode, internalId, internalEdgeCount);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsNode(final CyNode node) {
		return parent.containsNode(node) && nodeSet.contains(node);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyEdge edge) {
		return parent.containsEdge(edge) && edgeSet.contains(edge);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyNode from, final CyNode to) {
		return containsNode(from) && containsNode(to) && parent.containsEdge(from, to, internalId);
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNode getNode(final int index) {
		return parent.getNode(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge getEdge(final int index) {
		return parent.getEdge(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNeighborList(final CyNode node, final CyEdge.Type edgeType) {
		return parent.getNeighborList(node, edgeType, internalId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getAdjacentEdgeList(final CyNode node, final CyEdge.Type edgeType) {
		return parent.getAdjacentEdgeList(node, edgeType, internalId);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getConnectingEdgeList(final CyNode source, final CyNode target,
	                                          final CyEdge.Type edgeType) {
		return parent.getConnectingEdgeList(source, target, edgeType, internalId);
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String,CyDataTable> getNetworkCyDataTables() {
		return parent.getNetworkCyDataTables();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String,CyDataTable> getNodeCyDataTables() {
		return parent.getNodeCyDataTables();
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<String,CyDataTable> getEdgeCyDataTables() {
		return parent.getEdgeCyDataTables();
	}
	
	public Map<String, CyDataTable> getCyDataTables(String graphObjectType) {
		return parent.getCyDataTables(graphObjectType);
	}

	/**
	 * {@inheritDoc}
	 */
	public CyRow getCyRow(final String namespace) {
		return parent.getCyRow(namespace);
	}

	/**
	 * {@inheritDoc}
	 */
	public CyRow attrs() {
		return parent.attrs();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addNode(final CyNode node) {
		if (node == null)
			throw new NullPointerException("node is null");
		
		synchronized (this) {
			if (containsNode(node))
				return false;	

			if (!parent.containsNode(node))
				throw new IllegalArgumentException("node is not contained in parent network!");

			// add node 
			internalNodeCount++;
			nodeSet.add(node);
			updateNode(node);
		}

		return true;
	}

	public boolean addEdge(final CyEdge edge) {
		if (edge == null)
			throw new NullPointerException("edge is null");
		
		synchronized (this) {
			if (containsEdge(edge))
				return false;

			if (!parent.containsEdge(edge))
				throw new IllegalArgumentException("edge is not contained in parent network!");

			if (!containsNode(edge.getSource()))
				throw new IllegalArgumentException("source node of edge is not contained in network!");

			if (!containsNode(edge.getTarget()))
				throw new IllegalArgumentException("target node of edge is not contained in network!");

			// add edge 
			internalEdgeCount++;
			edgeSet.add(edge);
			updateEdge(edge);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeNode(final CyNode n) {
		//System.out.println("removeNode " + internalId);

		synchronized (this) {
		if (!containsNode(n))
			return false;

		//System.out.println("  attempting removeNode " + internalId);
		// remove adjacent edges
		final List<CyEdge> edges = getAdjacentEdgeList(n, CyEdge.Type.ANY);

		for (final CyEdge e : edges)
			removeEdge(e);

		final NodePointer node = parent.getNodePointer(n);
		inFirstNode = node.remove(inFirstNode,internalId);

		internalNodeCount--;
		nodeSet.remove(n);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(final CyEdge edge) {
		//System.out.println("removeEdge " + internalId);
		synchronized (this) {
		if (!containsEdge(edge))
			return false;

		final EdgePointer e = parent.getEdgePointer(edge);

		e.remove(internalId);

		internalEdgeCount--;
		edgeSet.remove(edge);
		}

		return true;
	}
}

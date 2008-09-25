
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

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.DynamicGraphFactory;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.AboutToRemoveEdgeEvent;
import org.cytoscape.model.events.AboutToRemoveEdgeListener;
import org.cytoscape.model.events.AboutToRemoveNodeEvent;
import org.cytoscape.model.events.AboutToRemoveNodeListener;
import org.cytoscape.model.events.AddedEdgeEvent;
import org.cytoscape.model.events.AddedEdgeListener;
import org.cytoscape.model.events.AddedNodeEvent;
import org.cytoscape.model.events.AddedNodeListener;
import org.cytoscape.model.events.RemovedEdgeEvent;
import org.cytoscape.model.events.RemovedEdgeListener;
import org.cytoscape.model.events.RemovedNodeEvent;
import org.cytoscape.model.events.RemovedNodeListener;
import org.cytoscape.model.events.internal.EdgeEvent;
import org.cytoscape.model.events.internal.NodeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * DOCUMENT ME!
  */
public class CyNetworkImpl implements CyNetwork {
	private static final int OUT = 0;
	private static final int IN = 1;
	private static final int UN = 2;
	private final long suid;
	private final DynamicGraph dg;
	private final ArrayList<CyNode> nodeList;
	private final ArrayList<CyEdge> edgeList;
	private final AtomicInteger nodeCount;
	private final AtomicInteger edgeCount;
	private final Map<String, CyDataTable> netAttrMgr;
	private final Map<String, CyDataTable> nodeAttrMgr;
	private final Map<String, CyDataTable> edgeAttrMgr;
	private final CyEventHelper eventHelper;

	/**
	 * Creates a new CyNetworkImpl object.
	 *
	 * @param eh  DOCUMENT ME!
	 */
	public CyNetworkImpl(final CyEventHelper eh) {
		suid = IdFactory.getNextSUID();
		dg = DynamicGraphFactory.instantiateDynamicGraph();
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeCount = new AtomicInteger(0);
		edgeCount = new AtomicInteger(0);

		netAttrMgr = new HashMap<String, CyDataTable>();
		netAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " network", true));

		nodeAttrMgr = new HashMap<String, CyDataTable>();
		nodeAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " node", true));

		edgeAttrMgr = new HashMap<String, CyDataTable>();
		edgeAttrMgr.put(CyNetwork.DEFAULT_ATTRS, new CyDataTableImpl(null, suid + " edge", true));

		eventHelper = eh;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public long getSUID() {
		return suid;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyNode addNode() {
		final CyNode newNode;

		synchronized (this) {
			final int newNodeInd = dg.nodeCreate();
			newNode = new CyNodeImpl(this, newNodeInd, nodeAttrMgr);

			if (newNodeInd == nodeList.size())
				nodeList.add(newNode);
			else if ((newNodeInd < nodeList.size()) && (newNodeInd >= 0))
				nodeList.set(newNodeInd, newNode);
			else
				throw new IllegalStateException("bad new int index: " + newNodeInd + " max size: "
				                                + nodeList.size());

			nodeCount.incrementAndGet();
		}

		eventHelper.fireSynchronousEvent((AddedNodeEvent) new NodeEvent(newNode, this),
		                                 AddedNodeListener.class);

		return newNode;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean removeNode(final CyNode node) {
		eventHelper.fireSynchronousEvent((AboutToRemoveNodeEvent) new NodeEvent(node, this),
		                                 AboutToRemoveNodeListener.class);

		boolean rem = false;

		synchronized (this) {
			if (!containsNode(node))
				return false;

			final List<CyEdge> edgesToRemove = getAdjacentEdgeList(node, CyEdge.Type.ANY);

			for (CyEdge etr : edgesToRemove) {
				final boolean removeSuccess = removeEdge(etr);

				if (!removeSuccess)
					throw new IllegalStateException("couldn't remove edge in preparation for node removal: "
					                                + etr);
			}

			final int remInd = node.getIndex();
			rem = dg.nodeRemove(remInd);

			if (rem) {
				nodeList.set(remInd, null);
				nodeCount.decrementAndGet();
			}
		}

		eventHelper.fireSynchronousEvent((RemovedNodeEvent) new NodeEvent(null, this),
		                                 RemovedNodeListener.class);

		return rem;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 * @param isDirected DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyEdge addEdge(final CyNode source, final CyNode target, final boolean isDirected) {
		final CyEdge newEdge;

		synchronized (this) {
			if (!containsNode(source) || !containsNode(target))
				throw new IllegalArgumentException("invalid input nodes");

			final int newEdgeInd = dg.edgeCreate(source.getIndex(), target.getIndex(), isDirected);

			newEdge = new CyEdgeImpl(source, target, isDirected, newEdgeInd, edgeAttrMgr);

			if (newEdgeInd == edgeList.size())
				edgeList.add(newEdge);
			else if ((newEdgeInd < edgeList.size()) && (newEdgeInd > 0))
				edgeList.set(newEdgeInd, newEdge);
			else
				throw new IllegalStateException("bad new int index: " + newEdgeInd + " max size: "
				                                + edgeList.size());

			edgeCount.incrementAndGet();
		}

		eventHelper.fireSynchronousEvent((AddedEdgeEvent) new EdgeEvent(newEdge, this),
		                                 AddedEdgeListener.class);

		return newEdge;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean removeEdge(final CyEdge edge) {
		eventHelper.fireSynchronousEvent((AboutToRemoveEdgeEvent) new EdgeEvent(edge, this),
		                                 AboutToRemoveEdgeListener.class);

		boolean rem = false;

		synchronized (this) {
			if (!containsEdge(edge))
				return false;

			final int remInd = edge.getIndex();
			rem = dg.edgeRemove(remInd);

			if (rem) {
				edgeList.set(remInd, null);
				edgeCount.decrementAndGet();
			}
		}

		eventHelper.fireSynchronousEvent((RemovedEdgeEvent) new EdgeEvent(null, this),
		                                 RemovedEdgeListener.class);

		return rem;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNodeCount() {
		return nodeCount.get();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeCount() {
		return edgeCount.get();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyNode> getNodeList() {
		final ArrayList<CyNode> nl = new ArrayList<CyNode>();
		final IntEnumerator it = dg.nodes();

		while (it.numRemaining() > 0) {
			final CyNode n = nodeList.get(it.nextInt());

			if (n == null)
				throw new IllegalStateException("Iterator and List out of sync");

			nl.add(n);
		}

		return nl;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyEdge> getEdgeList() {
		final ArrayList<CyEdge> el = new ArrayList<CyEdge>();
		final IntEnumerator it = dg.edges();

		while (it.numRemaining() > 0) {
			final CyEdge e = edgeList.get(it.nextInt());

			if (e == null)
				throw new IllegalStateException("Iterator and List out of sync");

			el.add(e);
		}

		return el;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean containsNode(final CyNode node) {
		if (node == null)
			return false;

		return dg.nodeExists(node.getIndex());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean containsEdge(final CyEdge edge) {
		if (edge == null)
			return false;

		return dg.edgeType(edge.getIndex()) != -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean containsEdge(final CyNode from, final CyNode to) {
		if (!containsNode(from) || !containsNode(to))
			return false;

		final IntIterator it = dg.edgesConnecting(from.getIndex(), to.getIndex(), true, true, true);

		if (it == null)
			return false;

		return it.hasNext();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param edgeType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyNode> getNeighborList(final CyNode node, final CyEdge.Type edgeType) {
		if (!containsNode(node))
			throw new IllegalArgumentException("Node not contained in this network");

		final boolean[] et = convertEdgeType(edgeType);
		final ArrayList<CyNode> nodes = new ArrayList<CyNode>();
		final IntEnumerator it = dg.edgesAdjacent(node.getIndex(), et[OUT], et[IN], et[UN]);

		while ((it != null) && (it.numRemaining() > 0)) {
			final int edgeInd = it.nextInt();
			final int neighbor = node.getIndex() ^ dg.edgeSource(edgeInd) ^ dg.edgeTarget(edgeInd);

			if ((neighbor < 0) || (neighbor >= nodeList.size()))
				throw new IllegalStateException("bad neighbor");

			final CyNode n = nodeList.get(neighbor);

			if (n == null)
				throw new IllegalStateException("null neighbor");

			nodes.add(n);
		}

		return nodes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param edgeType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyEdge> getAdjacentEdgeList(final CyNode node, final CyEdge.Type edgeType) {
		if (!containsNode(node))
			throw new IllegalArgumentException("Nodes not contained in this network");

		final boolean[] et = convertEdgeType(edgeType);
		final ArrayList<CyEdge> edges = new ArrayList<CyEdge>();
		final IntEnumerator it = dg.edgesAdjacent(node.getIndex(), et[OUT], et[IN], et[UN]);

		while ((it != null) && (it.numRemaining() > 0)) {
			final int edgeInd = it.nextInt();
			final CyEdge e = edgeList.get(edgeInd);

			if (e == null)
				throw new IllegalStateException("Iterator and List out of sync");

			edges.add(e);
		}

		return edges;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 * @param edgeType DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<CyEdge> getConnectingEdgeList(final CyNode source, final CyNode target,
	                                          final CyEdge.Type edgeType) {
		if (!containsNode(source) || !containsNode(target))
			throw new IllegalArgumentException("Nodes not contained in this network");

		final boolean[] et = convertEdgeType(edgeType);
		final ArrayList<CyEdge> edges = new ArrayList<CyEdge>();
		final IntIterator it = dg.edgesConnecting(source.getIndex(), target.getIndex(), et[OUT],
		                                          et[IN], et[UN]);

		while ((it != null) && it.hasNext()) {
			final int edgeInd = it.nextInt();
			final CyEdge e = edgeList.get(edgeInd);

			if (e == null)
				throw new IllegalStateException("Iterator and List out of sync");

			edges.add(e);
		}

		return edges;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param index DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyNode getNode(final int index) {
		if ((index < 0) || (index >= nodeList.size()))
			return null;
		else

			return nodeList.get(index);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param index DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyEdge getEdge(final int index) {
		if ((index < 0) || (index >= edgeList.size()))
			return null;
		else

			return edgeList.get(index);
	}

	private boolean[] convertEdgeType(final CyEdge.Type e) {
		if (e == CyEdge.Type.UNDIRECTED)
			return new boolean[] { false, false, true };
		else if (e == CyEdge.Type.DIRECTED)
			return new boolean[] { true, true, false };
		else if (e == CyEdge.Type.INCOMING)
			return new boolean[] { false, true, false };
		else if (e == CyEdge.Type.OUTGOING)
			return new boolean[] { true, false, false };
		else // ANY


			return new boolean[] { true, true, true };
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param namespace DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyRow getCyRow(final String namespace) {
		if (namespace == null)
			throw new NullPointerException("namespace is null");

		final CyDataTable mgr = netAttrMgr.get(namespace);

		if (mgr == null)
			throw new NullPointerException("attribute manager is null for namespace: " + namespace);

		return mgr.getRow(suid);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyRow attrs() {
		return getCyRow(CyNetwork.DEFAULT_ATTRS);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<String, CyDataTable> getNetworkCyDataTables() {
		return netAttrMgr;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<String, CyDataTable> getNodeCyDataTables() {
		return nodeAttrMgr;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<String, CyDataTable> getEdgeCyDataTables() {
		return edgeAttrMgr;
	}
}

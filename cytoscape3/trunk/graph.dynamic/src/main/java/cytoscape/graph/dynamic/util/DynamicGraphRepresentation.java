
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntStack;


public final class DynamicGraphRepresentation implements DynamicGraph, java.io.Externalizable {
	private static final long serialVersionUID = 44615510L;
	private int m_nodeCount;
	private int m_maxNode;
	private int m_edgeCount;
	private int m_maxEdge;
	private Node m_firstNode;
	private final NodeArray m_nodes;
	private final NodeDepot m_nodeDepot;
	private final EdgeArray m_edges;
	private final EdgeDepot m_edgeDepot;

	// Use this as a bag of integers in various operations.  Don't forget to
	// empty() it before using it.
	private final IntStack m_stack;

	/**
	 * Creates a new DynamicGraphRepresentation object.
	 */
	public DynamicGraphRepresentation() // Must be public for Externalizable.
	 {
		m_nodeCount = 0;
		m_firstNode = null;
		m_maxNode = -1;
		m_edgeCount = 0;
		m_maxEdge = -1;
		m_nodes = new NodeArray();
		m_edges = new EdgeArray();
		m_edgeDepot = new EdgeDepot();
		m_nodeDepot = new NodeDepot();
		m_stack = new IntStack();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final IntEnumerator nodes() {
		final int nodeCount = m_nodeCount;
		final Node firstNode = m_firstNode;

		return new IntEnumerator() {
				private int numRemaining = nodeCount;
				private Node node = firstNode;

				public final int numRemaining() {
					return numRemaining;
				}

				public final int nextInt() {
					final int returnThis = node.nodeId;
					node = node.nextNode;
					numRemaining--;

					return returnThis;
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final IntEnumerator edges() {
		final int edgeCount = m_edgeCount;
		final Node firstNode = m_firstNode;

		return new IntEnumerator() {
				private int numRemaining = edgeCount;
				private Node node = firstNode;
				private Edge edge = null;

				public final int numRemaining() {
					return numRemaining;
				}

				public final int nextInt() {
					final int returnThis;

					if (edge != null) {
						returnThis = edge.edgeId;
					} else {
						for (edge = node.firstOutEdge; edge == null;
						     node = node.nextNode, edge = node.firstOutEdge) {
						}

						node = node.nextNode;
						returnThis = edge.edgeId;
					}

					edge = edge.nextOutEdge;
					numRemaining--;

					return returnThis;
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final int nodeCreate() {
		final Node n = m_nodeDepot.getNode();
		final int returnThis;

		if (n.nodeId < 0) {
			returnThis = (n.nodeId = ++m_maxNode);
		} else {
			returnThis = n.nodeId;
		}

		m_nodes.setNodeAtIndex(n, returnThis);
		m_nodeCount++;
		n.nextNode = m_firstNode;

		if (m_firstNode != null) {
			m_firstNode.prevNode = n;
		}

		m_firstNode = n;
		n.outDegree = 0;
		n.inDegree = 0;
		n.undDegree = 0;
		n.selfEdges = 0;

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final boolean nodeRemove(final int node) {
		final IntEnumerator edges = edgesAdjacent(node, true, true, true);

		if (edges == null) {
			return false;
		}

		m_stack.empty();

		while (edges.numRemaining() > 0)
			m_stack.push(edges.nextInt());

		while (m_stack.size() > 0)
			edgeRemove(m_stack.pop());

		final Node n = m_nodes.getNodeAtIndex(node);

		if (n.prevNode != null) {
			n.prevNode.nextNode = n.nextNode;
		} else {
			m_firstNode = n.nextNode;
		}

		if (n.nextNode != null) {
			n.nextNode.prevNode = n.prevNode;
		}

		m_nodes.setNodeAtIndex(null, node);
		n.prevNode = null;
		n.firstOutEdge = null;
		n.firstInEdge = null;
		m_nodeDepot.recycleNode(n);
		m_nodeCount--;

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param sourceNode DOCUMENT ME!
	 * @param targetNode DOCUMENT ME!
	 * @param directed DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final int edgeCreate(final int sourceNode, final int targetNode, final boolean directed) {
		if ((sourceNode < 0) || (sourceNode == Integer.MAX_VALUE)) {
			return -1;
		}

		final Node source = m_nodes.getNodeAtIndex(sourceNode);

		if ((targetNode < 0) || (targetNode == Integer.MAX_VALUE)) {
			return -1;
		}

		final Node target = m_nodes.getNodeAtIndex(targetNode);

		if ((source == null) || (target == null)) {
			return -1;
		}

		final Edge e = m_edgeDepot.getEdge();
		final int returnThis;

		if (e.edgeId < 0) {
			returnThis = (e.edgeId = ++m_maxEdge);
		} else {
			returnThis = e.edgeId;
		}

		m_edges.setEdgeAtIndex(e, returnThis);
		m_edgeCount++;

		if (directed) {
			source.outDegree++;
			target.inDegree++;
		} else {
			source.undDegree++;
			target.undDegree++;
		}

		if (source == target) { // Self-edge.

			if (directed) {
				source.selfEdges++;
			} else {
				source.undDegree--;
			}
		}

		e.nextOutEdge = source.firstOutEdge;

		if (source.firstOutEdge != null) {
			source.firstOutEdge.prevOutEdge = e;
		}

		source.firstOutEdge = e;
		e.nextInEdge = target.firstInEdge;

		if (target.firstInEdge != null) {
			target.firstInEdge.prevInEdge = e;
		}

		target.firstInEdge = e;
		e.directed = directed;
		e.sourceNode = sourceNode;
		e.targetNode = targetNode;

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final boolean edgeRemove(final int edge) {
		if ((edge < 0) || (edge == Integer.MAX_VALUE)) {
			return false;
		}

		final Edge e = m_edges.getEdgeAtIndex(edge);

		if (e == null) {
			return false;
		}

		final Node source = m_nodes.getNodeAtIndex(e.sourceNode);
		final Node target = m_nodes.getNodeAtIndex(e.targetNode);

		if (e.prevOutEdge != null) {
			e.prevOutEdge.nextOutEdge = e.nextOutEdge;
		} else {
			source.firstOutEdge = e.nextOutEdge;
		}

		if (e.nextOutEdge != null) {
			e.nextOutEdge.prevOutEdge = e.prevOutEdge;
		}

		if (e.prevInEdge != null) {
			e.prevInEdge.nextInEdge = e.nextInEdge;
		} else {
			target.firstInEdge = e.nextInEdge;
		}

		if (e.nextInEdge != null) {
			e.nextInEdge.prevInEdge = e.prevInEdge;
		}

		if (e.directed) {
			source.outDegree--;
			target.inDegree--;
		} else {
			source.undDegree--;
			target.undDegree--;
		}

		if (source == target) { // Self-edge.

			if (e.directed) {
				source.selfEdges--;
			} else {
				source.undDegree++;
			}
		}

		m_edges.setEdgeAtIndex(null, edge);
		e.prevOutEdge = null;
		e.nextInEdge = null;
		e.prevInEdge = null;
		m_edgeDepot.recycleEdge(e);
		m_edgeCount--;

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final boolean nodeExists(final int node) {
		if ((node < 0) || (node == Integer.MAX_VALUE)) {
			return false;
		}

		return m_nodes.getNodeAtIndex(node) != null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final byte edgeType(final int edge) {
		if ((edge < 0) || (edge == Integer.MAX_VALUE)) {
			return -1;
		}

		final Edge e = m_edges.getEdgeAtIndex(edge);

		if (e == null) {
			return -1;
		}

		if (e.directed) {
			return DIRECTED_EDGE;
		}

		return UNDIRECTED_EDGE;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final int edgeSource(final int edge) {
		if ((edge < 0) || (edge == Integer.MAX_VALUE)) {
			return -1;
		}

		final Edge e = m_edges.getEdgeAtIndex(edge);

		if (e == null) {
			return -1;
		}

		return e.sourceNode;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final int edgeTarget(final int edge) {
		if ((edge < 0) || (edge == Integer.MAX_VALUE)) {
			return -1;
		}

		final Edge e = m_edges.getEdgeAtIndex(edge);

		if (e == null) {
			return -1;
		}

		return e.targetNode;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param outgoing DOCUMENT ME!
	 * @param incoming DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final IntEnumerator edgesAdjacent(final int node, final boolean outgoing,
	                                         final boolean incoming, final boolean undirected) {
		if ((node < 0) || (node == Integer.MAX_VALUE)) {
			return null;
		}

		final Node n = m_nodes.getNodeAtIndex(node);

		if (n == null) {
			return null;
		}

		final Edge[] edgeLists;

		if (undirected || (outgoing && incoming)) {
			edgeLists = new Edge[] { n.firstOutEdge, n.firstInEdge };
		} else if (outgoing) { // Cannot also be incoming.
			edgeLists = new Edge[] { n.firstOutEdge, null };
		} else if (incoming) { // Cannot also be outgoing.
			edgeLists = new Edge[] { null, n.firstInEdge };
		} else { // All boolean input parameters are false.
			edgeLists = new Edge[] { null, null };
		}

		int tentativeEdgeCount = 0;

		if (outgoing) {
			tentativeEdgeCount += n.outDegree;
		}

		if (incoming) {
			tentativeEdgeCount += n.inDegree;
		}

		if (undirected) {
			tentativeEdgeCount += n.undDegree;
		}

		if (outgoing && incoming) {
			tentativeEdgeCount -= n.selfEdges;
		}

		final int edgeCount = tentativeEdgeCount;

		return new IntEnumerator() {
				private int numRemaining = edgeCount;
				private int edgeListIndex = -1;
				private Edge edge = null;

				public final int numRemaining() {
					return numRemaining;
				}

				public final int nextInt() {
					while (edge == null)
						edge = edgeLists[++edgeListIndex];

					int returnThis = -1;

					if (edgeListIndex == 0) {
						while ((edge != null)
						       && !((outgoing && edge.directed) || (undirected && !edge.directed))) {
							edge = edge.nextOutEdge;

							if (edge == null) {
								edge = edgeLists[++edgeListIndex];

								break;
							}
						}

						if ((edge != null) && (edgeListIndex == 0)) {
							returnThis = edge.edgeId;
							edge = edge.nextOutEdge;
						}
					}

					if (edgeListIndex == 1) {
						while (((edge.sourceNode == edge.targetNode)
						       && ((outgoing && edge.directed) || (undirected && !edge.directed)))
						       || !((incoming && edge.directed) || (undirected && !edge.directed))) {
							edge = edge.nextInEdge;
						}

						returnThis = edge.edgeId;
						edge = edge.nextInEdge;
					}

					numRemaining--;

					return returnThis;
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node0 DOCUMENT ME!
	 * @param node1 DOCUMENT ME!
	 * @param outgoing DOCUMENT ME!
	 * @param incoming DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public final IntIterator edgesConnecting(final int node0, final int node1,
	                                         final boolean outgoing, final boolean incoming,
	                                         final boolean undirected) {
		final IntEnumerator node0Adj = edgesAdjacent(node0, outgoing, incoming, undirected);
		final IntEnumerator node1Adj = edgesAdjacent(node1, incoming, outgoing, undirected);

		if ((node0Adj == null) || (node1Adj == null)) {
			return null;
		}

		final DynamicGraph graph = this;
		final IntEnumerator theAdj;
		final int nodeZero;
		final int nodeOne;

		if (node0Adj.numRemaining() <= node1Adj.numRemaining()) {
			theAdj = node0Adj;
			nodeZero = node0;
			nodeOne = node1;
		} else {
			theAdj = node1Adj;
			nodeZero = node1;
			nodeOne = node0;
		}

		return new IntIterator() {
				private int nextEdge = -1;

				private void ensureComputeNext() {
					if (nextEdge != -1) {
						return;
					}

					while (theAdj.numRemaining() > 0) {
						final int edge = theAdj.nextInt();

						if (nodeOne == (nodeZero ^ graph.edgeSource(edge) ^ graph.edgeTarget(edge))) {
							nextEdge = edge;

							return;
						}
					}

					nextEdge = -2;
				}

				public final boolean hasNext() {
					ensureComputeNext();

					if (nextEdge < 0) {
						return false;
					} else {
						return true;
					}
				}

				public final int nextInt() {
					ensureComputeNext();

					final int returnThis = nextEdge;
					nextEdge = -1;

					return returnThis;
				}
			};
	}

	// Externalizable methods.
	/**
	 * DOCUMENT ME!
	 *
	 * @param out DOCUMENT ME!
	 *
	 */
	public final void writeExternal(final java.io.ObjectOutput out) throws java.io.IOException {
		out.writeInt(m_nodeCount);
		out.writeInt(m_maxNode);
		out.writeInt(m_edgeCount);
		out.writeInt(m_maxEdge);

		for (Node currNode = m_nodeDepot.m_head.nextNode; currNode != null;
		     currNode = currNode.nextNode)
			out.writeInt(currNode.nodeId);

		out.writeInt(-1);

		for (Edge currEdge = m_edgeDepot.m_head.nextOutEdge; currEdge != null;
		     currEdge = currEdge.nextOutEdge)
			out.writeInt(currEdge.edgeId);

		out.writeInt(-1);

		{ // m_edges.

			final Edge[] arr = m_edges.m_edgeArr;
			final int arrLen = arr.length;
			out.writeInt(arrLen);

			for (int i = 0; i < arrLen; i++) {
				final Edge edge = arr[i];

				if (edge == null) {
					out.writeInt(-1);

					continue;
				}

				out.writeInt(edge.sourceNode);
				out.writeInt(edge.targetNode);
				out.writeBoolean(edge.directed);
			}

			for (int i = 0; i < arrLen; i++) {
				final Edge edge = arr[i];

				if (edge == null) {
					continue;
				}

				out.writeInt((edge.nextOutEdge == null) ? (-1) : edge.nextOutEdge.edgeId);
				out.writeInt((edge.prevOutEdge == null) ? (-1) : edge.prevOutEdge.edgeId);
				out.writeInt((edge.nextInEdge == null) ? (-1) : edge.nextInEdge.edgeId);
				out.writeInt((edge.prevInEdge == null) ? (-1) : edge.prevInEdge.edgeId);
			}
		}

		{ // m_nodes.

			final Node[] arr = m_nodes.m_nodeArr;
			final int arrLen = arr.length;
			out.writeInt(arrLen);

			for (int i = 0; i < arrLen; i++) {
				final Node node = arr[i];

				if (node == null) {
					out.writeInt(-1);

					continue;
				}

				out.writeInt(node.outDegree);
				out.writeInt(node.inDegree);
				out.writeInt(node.undDegree);
				out.writeInt(node.selfEdges);
			}

			for (int i = 0; i < arrLen; i++) {
				final Node node = arr[i];

				if (node == null) {
					continue;
				}

				out.writeInt((node.nextNode == null) ? (-1) : node.nextNode.nodeId);
				out.writeInt((node.prevNode == null) ? (-1) : node.prevNode.nodeId);
				out.writeInt((node.firstOutEdge == null) ? (-1) : node.firstOutEdge.edgeId);
				out.writeInt((node.firstInEdge == null) ? (-1) : node.firstInEdge.edgeId);
			}
		}

		if (m_firstNode == null) {
			out.writeInt(-1);
		} else {
			out.writeInt(m_firstNode.nodeId);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param in DOCUMENT ME!
	 *
	 */
	public final void readExternal(final java.io.ObjectInput in) throws java.io.IOException {
		m_nodeCount = in.readInt();
		m_maxNode = in.readInt();
		m_edgeCount = in.readInt();
		m_maxEdge = in.readInt();

		{ // m_nodeDepot.

			Node currNode = m_nodeDepot.m_head;

			while (true) {
				final int id = in.readInt();

				if (id < 0) {
					break;
				}

				currNode.nextNode = new Node();
				currNode = currNode.nextNode;
				currNode.nodeId = id;
			}
		}

		{ // m_edgeDepot.

			Edge currEdge = m_edgeDepot.m_head;

			while (true) {
				final int id = in.readInt();

				if (id < 0) {
					break;
				}

				currEdge.nextOutEdge = new Edge();
				currEdge = currEdge.nextOutEdge;
				currEdge.edgeId = id;
			}
		}

		{ // m_edges.

			final int arrLen = in.readInt();
			final Edge[] arr = (m_edges.m_edgeArr = new Edge[arrLen]);

			for (int i = 0; i < arrLen; i++) {
				final int source = in.readInt();

				if (source < 0) {
					continue;
				}

				final Edge edge = (arr[i] = new Edge());
				edge.edgeId = i;
				edge.sourceNode = source;
				edge.targetNode = in.readInt();
				edge.directed = in.readBoolean();
			}

			for (int i = 0; i < arrLen; i++) {
				final Edge edge = arr[i];

				if (edge == null) {
					continue;
				}

				final int nextOutEdge = in.readInt();
				final int prevOutEdge = in.readInt();
				final int nextInEdge = in.readInt();
				final int prevInEdge = in.readInt();

				if (nextOutEdge >= 0) {
					edge.nextOutEdge = arr[nextOutEdge];
				}

				if (prevOutEdge >= 0) {
					edge.prevOutEdge = arr[prevOutEdge];
				}

				if (nextInEdge >= 0) {
					edge.nextInEdge = arr[nextInEdge];
				}

				if (prevInEdge >= 0) {
					edge.prevInEdge = arr[prevInEdge];
				}
			}
		}

		{ // m_nodes.

			final int arrLen = in.readInt();
			final Node[] arr = (m_nodes.m_nodeArr = new Node[arrLen]);

			for (int i = 0; i < arrLen; i++) {
				final int outDeg = in.readInt();

				if (outDeg < 0) {
					continue;
				}

				final Node node = (arr[i] = new Node());
				node.nodeId = i;
				node.outDegree = outDeg;
				node.inDegree = in.readInt();
				node.undDegree = in.readInt();
				node.selfEdges = in.readInt();
			}

			final Edge[] edgeArr = m_edges.m_edgeArr;

			for (int i = 0; i < arrLen; i++) {
				final Node node = arr[i];

				if (node == null) {
					continue;
				}

				final int nextNode = in.readInt();
				final int prevNode = in.readInt();
				final int firstOutEdge = in.readInt();
				final int firstInEdge = in.readInt();

				if (nextNode >= 0) {
					node.nextNode = arr[nextNode];
				}

				if (prevNode >= 0) {
					node.prevNode = arr[prevNode];
				}

				if (firstOutEdge >= 0) {
					node.firstOutEdge = edgeArr[firstOutEdge];
				}

				if (firstInEdge >= 0) {
					node.firstInEdge = edgeArr[firstInEdge];
				}
			}
		}

		{ // m_firstNode.

			final int firstNode = in.readInt();

			if (firstNode >= 0) {
				m_firstNode = m_nodes.m_nodeArr[firstNode];
			}
		}
	}

}

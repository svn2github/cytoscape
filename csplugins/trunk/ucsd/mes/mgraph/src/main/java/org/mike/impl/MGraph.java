
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

package org.mike.impl;

import org.mike.CyEdge;
import org.mike.CyNetwork;
import org.mike.CyNode;
import org.mike.EdgeType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A linked list implementation of a {@link CyNetwork}. 
 */
public class MGraph implements CyNetwork {
	private int nodeCount;
	private int edgeCount;
	private NodePointer firstNode;
	private final List<NodePointer> nodePointers;
	private final List<EdgePointer> edgePointers;

	/**
	 * Creates a new MGraph object.
	 */
	public MGraph() {
		nodeCount = 0;
		edgeCount = 0;
		firstNode = null;
		nodePointers = new ArrayList<NodePointer>();
		edgePointers = new ArrayList<EdgePointer>();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNodeCount() {
		return nodeCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getEdgeCount() {
		return edgeCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge getEdge(final int e) {
		if ((e >= 0) && (e < edgePointers.size()))
			return edgePointers.get(e).cyEdge;
		else

			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNode getNode(final int n) {
		if ((n >= 0) && (n < nodePointers.size()))
			return nodePointers.get(n).cyNode;
		else

			return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNodeList() {
		final List<CyNode> ret = new ArrayList<CyNode>(nodeCount);
		int numRemaining = nodeCount;
		NodePointer node = firstNode;

		while (numRemaining > 0) {
			final CyNode toAdd = node.cyNode;
			node = node.nextNode;
			ret.add(toAdd);
			numRemaining--;
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getEdgeList() {
		final List<CyEdge> ret = new ArrayList<CyEdge>(edgeCount);
		int numRemaining = edgeCount;
		NodePointer node = firstNode;
		EdgePointer edge = null;

		while (numRemaining > 0) {
			final CyEdge retEdge;

			if (edge != null) {
				retEdge = edge.cyEdge;
			} else {
				for (edge = node.firstOutEdge; edge == null;
				     node = node.nextNode, edge = node.firstOutEdge) {
				}

				node = node.nextNode;
				retEdge = edge.cyEdge;
			}

			edge = edge.nextOutEdge;
			numRemaining--;

			ret.add(retEdge);
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyNode> getNeighborList(final CyNode n, final EdgeType e) {
		if (!containsNode(n))
			throw new IllegalArgumentException("this node is not contained in the network");

		final NodePointer np = getNodePointer(n);
		final List<CyNode> ret = new ArrayList<CyNode>(countEdges(np, e));
		final Iterator<EdgePointer> it = edgesAdjacent(np, e);

		while (it.hasNext()) {
			final EdgePointer edge = it.next();
			final int neighborIndex = np.index ^ edge.source.index ^ edge.target.index;
			ret.add(getNode(neighborIndex));
		}

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getAdjacentEdgeList(final CyNode n, final EdgeType e) {
		if (!containsNode(n))
			throw new IllegalArgumentException("this node is not contained in the network");

		final NodePointer np = getNodePointer(n);
		final List<CyEdge> ret = new ArrayList<CyEdge>(countEdges(np, e));
		final Iterator<EdgePointer> it = edgesAdjacent(np, e);

		while (it.hasNext())
			ret.add(it.next().cyEdge);

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<CyEdge> getConnectingEdgeList(final CyNode src, final CyNode trg, final EdgeType e) {
		if (!containsNode(src))
			throw new IllegalArgumentException("source node is not contained in the network");

		if (!containsNode(trg))
			throw new IllegalArgumentException("target node is not contained in the network");

		final NodePointer srcP = getNodePointer(src);
		final NodePointer trgP = getNodePointer(trg);

		final List<CyEdge> ret = new ArrayList<CyEdge>(Math.min(countEdges(srcP, e), countEdges(trgP, e)));
		final Iterator<EdgePointer> it = edgesConnecting(srcP, trgP, e);

		while (it.hasNext())
			ret.add(it.next().cyEdge);

		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyNode addNode() {
		final NodePointer n;

		synchronized (this) {
			n = new NodePointer(nodePointers.size(), this, firstNode);
			nodePointers.add(n);
			nodeCount++;

			if (firstNode != null)
				firstNode.prevNode = n;

			firstNode = n;
		}

		return n.cyNode;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeNode(final CyNode n) {
		if (!containsNode(n))
			return false;

		// remove adjacent edges
		final List<CyEdge> edges = getAdjacentEdgeList(n, EdgeType.ANY_EDGE);

		for (final CyEdge e : edges)
			removeEdge(e);

		final NodePointer node = getNodePointer(n);

		// now clean up node
		if (node.prevNode != null)
			node.prevNode.nextNode = node.nextNode;
		else
			firstNode = node.nextNode;

		if (node.nextNode != null)
			node.nextNode.prevNode = node.prevNode;

		nodePointers.set(node.index, null);

		node.prevNode = null;
		node.firstOutEdge = null;
		node.firstInEdge = null;
		nodeCount--;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public CyEdge addEdge(final CyNode s, final CyNode t, final boolean directed) {
		final EdgePointer e;

		synchronized (this) {
			if (!containsNode(s))
				throw new IllegalArgumentException("source node is not a member of this network");

			if (!containsNode(t))
				throw new IllegalArgumentException("target node is not a member of this network");

			final NodePointer source = getNodePointer(s);
			final NodePointer target = getNodePointer(t);

			e = new EdgePointer(source, target, directed, edgePointers.size());

			edgePointers.add(e);

			edgeCount++;

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
		}

		return e.cyEdge;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeEdge(final CyEdge edge) {
		if (!containsEdge(edge))
			return false;

		final EdgePointer e = getEdgePointer(edge);

		final NodePointer source = nodePointers.get(e.source.index);
		final NodePointer target = nodePointers.get(e.target.index);

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

		edgePointers.set(e.index, null);
		e.prevOutEdge = null;
		e.nextInEdge = null;
		e.prevInEdge = null;
		edgeCount--;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsNode(final CyNode node) {
		if (node == null)
			return false;
			//throw new NullPointerException("node is null");

		final int ind = node.getIndex();

		if (ind < 0)
			return false;
			//throw new IllegalArgumentException("node index less than zero");

		if (ind >= nodePointers.size())
			return false;

		final NodePointer thisNode = nodePointers.get(ind);

		return ((thisNode != null) && thisNode.cyNode.equals(node));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyEdge edge) {
		if (edge == null)
			return false;

		//throw new NullPointerException("edge is null");
		final int ind = edge.getIndex();

		if (ind < 0)
			return false;

		//throw new IllegalArgumentException("edge index less than zero");
		if (ind >= edgePointers.size())
			return false;

		final EdgePointer thisEdge = edgePointers.get(ind);

		return ((thisEdge != null) && thisEdge.cyEdge.equals(edge));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsEdge(final CyNode n1, final CyNode n2) {
		if (!containsNode(n1))
			return false;

		if (!containsNode(n2))
			return false;

		final Iterator<EdgePointer> it = edgesConnecting(getNodePointer(n1), getNodePointer(n2), 
		                                                 EdgeType.ANY_EDGE);

		return it.hasNext();
	}

	private Iterator<EdgePointer> edgesAdjacent(final NodePointer n, final EdgeType edgeType) {

		assert(n!=null);

		final EdgePointer[] edgeLists;

		boolean inc = false;
		boolean out = false;
		boolean und = false;

		if ((edgeType == EdgeType.UNDIRECTED_EDGE) || (edgeType == EdgeType.ANY_EDGE))
			und = true;

		if ((edgeType == EdgeType.DIRECTED_EDGE) || (edgeType == EdgeType.ANY_EDGE)
		    || (edgeType == EdgeType.INCOMING_EDGE))
			inc = true;

		if ((edgeType == EdgeType.DIRECTED_EDGE) || (edgeType == EdgeType.ANY_EDGE)
		    || (edgeType == EdgeType.OUTGOING_EDGE))
			out = true;

		final boolean incoming = inc;
		final boolean outgoing = out;
		final boolean undirected = und;

		if (undirected || (outgoing && incoming)) {
			edgeLists = new EdgePointer[] { n.firstOutEdge, n.firstInEdge };
		} else if (outgoing) { // Cannot also be incoming.
			edgeLists = new EdgePointer[] { n.firstOutEdge, null };
		} else if (incoming) { // Cannot also be outgoing.
			edgeLists = new EdgePointer[] { null, n.firstInEdge };
		} else { // All boolean input parameters are false.
			edgeLists = new EdgePointer[] { null, null };
		}

		final int edgeCount = countEdges(n, edgeType);

		return new Iterator<EdgePointer>() {
				private int numRemaining = edgeCount;
				private int edgeListIndex = -1;
				private EdgePointer edge;

				public boolean hasNext() {
					return numRemaining > 0;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}

				public EdgePointer next() {
					while (edge == null)
						edge = edgeLists[++edgeListIndex];

					int returnIndex = -1;

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
							returnIndex = edge.index;
							edge = edge.nextOutEdge;
						}
					}

					if (edgeListIndex == 1) {
						while (((edge.source.index == edge.target.index)
						       && ((outgoing && edge.directed) || (undirected && !edge.directed)))
						       || !((incoming && edge.directed) || (undirected && !edge.directed))) {
							edge = edge.nextInEdge;
						}

						returnIndex = edge.index;
						edge = edge.nextInEdge;
					}

					numRemaining--;

					return edgePointers.get(returnIndex);
				}
			};
	}

	private Iterator<EdgePointer> edgesConnecting(final NodePointer node0, final NodePointer node1,
	                                              final EdgeType et) {
		assert(node0!=null);
		assert(node1!=null);

		final Iterator<EdgePointer> theAdj;
		final int nodeZero;
		final int nodeOne;

		// choose the smaller iterator
		if (countEdges(node0, et) <= countEdges(node1, et)) {
			theAdj = edgesAdjacent(node0, et);
			nodeZero = node0.index;
			nodeOne = node1.index;
		} else {
			theAdj = edgesAdjacent(node1, et);
			nodeZero = node1.index;
			nodeOne = node0.index;
		}

		return new Iterator<EdgePointer>() {
				private int nextEdge = -1;

				private void ensureComputeNext() {
					if (nextEdge != -1) {
						return;
					}

					while (theAdj.hasNext()) {
						final EdgePointer e = theAdj.next();
						final int edge = e.index;

						if (nodeOne == (nodeZero ^ e.source.index ^ e.target.index)) {
							nextEdge = edge;

							return;
						}
					}

					nextEdge = -2;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}

				public boolean hasNext() {
					ensureComputeNext();

					return (nextEdge >= 0);
				}

				public EdgePointer next() {
					ensureComputeNext();

					final int returnIndex = nextEdge;
					nextEdge = -1;

					return edgePointers.get(returnIndex);
				}
			};
	}

	private int countEdges(final NodePointer n, final EdgeType edgeType) {
		assert(n!=null);
		boolean undirected = false;
		boolean incoming = false;
		boolean outgoing = false;

		if ((edgeType == EdgeType.UNDIRECTED_EDGE) || (edgeType == EdgeType.ANY_EDGE))
			undirected = true;

		if ((edgeType == EdgeType.DIRECTED_EDGE) || (edgeType == EdgeType.ANY_EDGE)
		    || (edgeType == EdgeType.INCOMING_EDGE))
			incoming = true;

		if ((edgeType == EdgeType.DIRECTED_EDGE) || (edgeType == EdgeType.ANY_EDGE)
		    || (edgeType == EdgeType.OUTGOING_EDGE))
			outgoing = true;

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

		return tentativeEdgeCount;
	}

	private EdgePointer getEdgePointer(final CyEdge edge) {
		assert(edge != null);
		assert(edge.getIndex()>=0);
		assert(edge.getIndex()<edgePointers.size());

		return edgePointers.get(edge.getIndex());
	}

	private NodePointer getNodePointer(final CyNode node) {
		assert(node != null);
		assert(node.getIndex()>=0);
		assert(node.getIndex()<nodePointers.size());

		return nodePointers.get(node.getIndex());
	}
}

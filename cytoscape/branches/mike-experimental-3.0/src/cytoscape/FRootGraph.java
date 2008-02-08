
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

package cytoscape;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;

import cytoscape.util.intr.ArrayIntIterator;
import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntStack;
import cytoscape.util.intr.MinIntHeap;

import cytoscape.Edge;
import cytoscape.GraphPerspective;
import cytoscape.Node;
import cytoscape.RootGraph;
import cytoscape.RootGraphChangeEvent;
import cytoscape.RootGraphChangeListener;
import cytoscape.data.SelectEventListener;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.NoSuchElementException;

// This implementation of cytoscape is safe to use with a single thread only.
class FRootGraph implements RootGraph, DynamicGraph {
	////////////////////////////////////
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public IntEnumerator nodes() {
		return m_graph.nodes();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public IntEnumerator edges() {
		return m_graph.edges();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int nodeCreate() {
		return ~createNode();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean nodeRemove(int node) {
		return removeNode(~node) != 0;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param sourceNode DOCUMENT ME!
	 * @param targetNode DOCUMENT ME!
	 * @param directed DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int edgeCreate(int sourceNode, int targetNode, boolean directed) {
		return ~createEdge(~sourceNode, ~targetNode, directed);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean edgeRemove(int edge) {
		return removeEdge(~edge) != 0;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean nodeExists(int node) {
		return m_graph.nodeExists(node);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte edgeType(int edge) {
		return m_graph.edgeType(edge);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int edgeSource(int edge) {
		return m_graph.edgeSource(edge);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int edgeTarget(int edge) {
		return m_graph.edgeTarget(edge);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param outgoing DOCUMENT ME!
	 * @param incoming DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public IntEnumerator edgesAdjacent(int node, boolean outgoing, boolean incoming,
	                                   boolean undirected) {
		return m_graph.edgesAdjacent(node, outgoing, incoming, undirected);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node0 DOCUMENT ME!
	 * @param node1 DOCUMENT ME!
	 * @param outgoing DOCUMENT ME!
	 * @param incoming DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public IntIterator edgesConnecting(int node0, int node1, boolean outgoing, boolean incoming,
	                                   boolean undirected) {
		return m_graph.edgesConnecting(node0, node1, outgoing, incoming, undirected);
	}

	//////////////////////////////////
	// END: Implements DynamicGraph //
	//////////////////////////////////

	// Not specified by cytoscape.RootGraph.  GraphPerspective implementation
	// in this package relies on this method.
	// ATTENTION!  Before making this method public you need to change the
	// event implementations to return copied arrays in their methods instead
	// of always returning the same array reference.  Also you need to enable
	// create node and create edge events - currently only remove node and
	// remove edge events are fired.
	void addRootGraphChangeListener(RootGraphChangeListener listener) { // This method is not thread safe; synchronize on an object to make it so.
		m_lis = RootGraphChangeListenerChain.add(m_lis, listener);
	}

	// Not specified by cytoscape.RootGraph.  GraphPerspective implementation
	// in this package relies on this method.
	// ATTENTION!  Before making this method public you need to change the
	// event implementations to return copied arrays in their methods instead
	// of always returning the same array reference.  Also you need to enable
	// create node and create edge events - currently only remove node and
	// remove edge events are fired.
	void removeRootGraphChangeListener(RootGraphChangeListener listener) { // This method is not thread safe; synchronize on an object to make it so.
		m_lis = RootGraphChangeListenerChain.remove(m_lis, listener);
	}

	/**
	public CyNetwork createNetwork(Collection nodes, Collection edges) {
		Node[] node = (Node[]) nodes.toArray(new Node[] {  });
		Edge[] edge = (Edge[]) edges.toArray(new Edge[] {  });

		return createNetwork(node, edge);
	}
	 */

	/**
	 * Creates a new Network
	public CyNetwork createNetwork(Node[] nodes, Edge[] edges) {
		final Node[] nodeArr = ((nodes != null) ? nodes : new Node[0]);
		final Edge[] edgeArr = ((edges != null) ? edges : new Edge[0]);
		final RootGraph root = this;

		try {
			return new FingCyNetwork(this,
			                         new IntIterator() {
					private int index = 0;

					public boolean hasNext() {
						return index < nodeArr.length;
					}

					public int nextInt() {
						if ((nodeArr[index] == null) || (nodeArr[index].getRootGraph() != root))
							throw new IllegalArgumentException();

						return nodeArr[index++].getRootGraphIndex();
					}
				},
			                         new IntIterator() {
					private int index = 0;

					public boolean hasNext() {
						return index < edgeArr.length;
					}

					public int nextInt() {
						if ((edgeArr[index] == null) || (edgeArr[index].getRootGraph() != root))
							throw new IllegalArgumentException();

						return edgeArr[index++].getRootGraphIndex();
					}
				});
		} catch (IllegalArgumentException exc) {
			return null;
		}
	}
	 */

	/**
	 * Uses Code copied from ColtRootGraph to create a new Network.
	public CyNetwork createNetwork(int[] nodeInx, int[] edgeInx) {
		if (nodeInx == null)
			nodeInx = new int[0];

		if (edgeInx == null)
			edgeInx = new int[0];

		try {
			return new FingCyNetwork(this, new ArrayIntIterator(nodeInx, 0, nodeInx.length),
			                         new ArrayIntIterator(edgeInx, 0, edgeInx.length));
		} catch (IllegalArgumentException exc) {
			return null;
		}
	}
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 * @param edges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphPerspective createGraphPerspective(Collection<Node> nodes, Collection<Edge> edges) {
		return createGraphPerspective(nodes.toArray(new Node[nodes.size()]), 
		                              edges.toArray(new Edge[edges.size()]));
	}

	public GraphPerspective createGraphPerspective(Node[] nodes, Edge[] edges) {
		final Node[] nodeArr = ((nodes != null) ? nodes : new Node[0]);
		final Edge[] edgeArr = ((edges != null) ? edges : new Edge[0]);
		final RootGraph root = this;

		try {
			return new FGraphPerspective(this,
			                             new IntIterator() {
					private int index = 0;

					public boolean hasNext() {
						return index < nodeArr.length;
					}

					public int nextInt() {
						if ((nodeArr[index] == null) || (nodeArr[index].getRootGraph() != root))
							throw new IllegalArgumentException();

						return nodeArr[index++].getRootGraphIndex();
					}
				},
			                             new IntIterator() {
					private int index = 0;

					public boolean hasNext() {
						return index < edgeArr.length;
					}

					public int nextInt() {
						if ((edgeArr[index] == null) || (edgeArr[index].getRootGraph() != root))
							throw new IllegalArgumentException();

						return edgeArr[index++].getRootGraphIndex();
					}
				});
		} catch (IllegalArgumentException exc) {
			return null;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphPerspective createGraphPerspective(int[] nodeInx, int[] edgeInx) {
		if (nodeInx == null)
			nodeInx = new int[0];

		if (edgeInx == null)
			edgeInx = new int[0];

		try {
			return new FGraphPerspective(this, new ArrayIntIterator(nodeInx, 0, nodeInx.length),
			                                   new ArrayIntIterator(edgeInx, 0, edgeInx.length));
		} catch (IllegalArgumentException exc) {
			return null;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNodeCount() {
		return m_graph.nodes().numRemaining();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeCount() {
		return m_graph.edges().numRemaining();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator<Node> nodesIterator() {
		final IntEnumerator nodes = m_graph.nodes();
		final FRootGraph rootGraph = this;

		return new Iterator<Node>() {
				public void remove() {
					throw new UnsupportedOperationException();
				}

				public boolean hasNext() {
					return nodes.numRemaining() > 0;
				}

				public Node next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return rootGraph.getNode(~(nodes.nextInt()));
				}
			};
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Node> nodesList() {
		final int nodeCount = getNodeCount();
		final java.util.ArrayList<Node> returnThis = new java.util.ArrayList<Node>(nodeCount);
		Iterator<Node> iter = nodesIterator();

		for (int i = 0; i < nodeCount; i++)
			returnThis.add(iter.next());

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getNodeIndicesArray() {
		IntEnumerator nodes = m_graph.nodes();
		final int[] returnThis = new int[nodes.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = ~(nodes.nextInt());

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator<Edge> edgesIterator() {
		final IntEnumerator edges = m_graph.edges();
		final FRootGraph rootGraph = this;

		return new Iterator<Edge>() {
				public void remove() {
					throw new UnsupportedOperationException();
				}

				public boolean hasNext() {
					return edges.numRemaining() > 0;
				}

				public Edge next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return rootGraph.getEdge(~(edges.nextInt()));
				}
			};
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Edge> edgesList() {
		final int edgeCount = getEdgeCount();
		final java.util.ArrayList<Edge> returnThis = new java.util.ArrayList<Edge>(edgeCount);
		Iterator<Edge> iter = edgesIterator();

		for (int i = 0; i < edgeCount; i++)
			returnThis.add(iter.next());

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray() {
		IntEnumerator edges = m_graph.edges();
		final int[] returnThis = new int[edges.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = ~(edges.nextInt());

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Node removeNode(Node node) {
		if ((node.getRootGraph() == this) && (removeNode(node.getRootGraphIndex()) != 0))
			return node;
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int removeNode(final int nodeInx) {
		final int nativeNodeInx = ~nodeInx;

		if (!m_graph.nodeExists(nativeNodeInx))
			return 0;

		final IntEnumerator nativeEdgeEnum = m_graph.edgesAdjacent(nativeNodeInx, true, true, true);
		final Edge[] removedEdgeArr = new Edge[nativeEdgeEnum.numRemaining()];

		for (int i = 0; i < removedEdgeArr.length; i++)
			removedEdgeArr[i] = m_edges.getEdgeAtIndex(nativeEdgeEnum.nextInt());

		for (int i = 0; i < removedEdgeArr.length; i++) {
			final int nativeEdgeInx = ~(removedEdgeArr[i].getRootGraphIndex());
			m_graph.edgeRemove(nativeEdgeInx);

			final Edge removedEdge = m_edges.getEdgeAtIndex(nativeEdgeInx);
			m_edges.setEdgeAtIndex(null, nativeEdgeInx);
			m_edgeDepot.recycleEdge(removedEdge);
		}

		final Node removedNode = m_nodes.getNodeAtIndex(nativeNodeInx);
		m_graph.nodeRemove(nativeNodeInx);
		m_nodes.setNodeAtIndex(null, nativeNodeInx);
		m_nodeDepot.recycleNode(removedNode);

		if (removedEdgeArr.length > 0)
			m_lis.rootGraphChanged(new RootGraphEdgesRemovedEvent(this, removedEdgeArr));

		m_lis.rootGraphChanged(new RootGraphNodesRemovedEvent(this, new Node[] { removedNode }));

		return nodeInx;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Node> removeNodes(java.util.List<Node> nodes) {
		final java.util.ArrayList<Node> returnThis = new java.util.ArrayList<Node>();

		for (int i = 0; i < nodes.size(); i++)
			if (removeNode((Node) nodes.get(i)) != null)
				returnThis.add(nodes.get(i));

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeIndices DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] removeNodes(int[] nodeIndices) {
		final int[] returnThis = new int[nodeIndices.length];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = removeNode(nodeIndices[i]);

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createNode() {
		final int nativeNodeInx = m_graph.nodeCreate();
		final int returnThis = ~nativeNodeInx;
		Node newNode = m_nodeDepot.getNode(this, returnThis, null);
		m_nodes.setNodeAtIndex(newNode, nativeNodeInx);

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 * @param edges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createNode(Node[] nodes, Edge[] edges) {
		final GraphPerspective persp = createGraphPerspective(nodes, edges);

		if (persp == null)
			return 0;

		return createNode(persp);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param perspective DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createNode(GraphPerspective perspective) {
		// Casting to check that we aren't going to get garbage nodes and edges.
		if (((FGraphPerspective) perspective).getRootGraph() != this)
			return 0;

		final int returnThis = createNode();
		final int nativeParentNodeInx = ~returnThis;
		final int[] perspEdgeInxArr = perspective.getEdgeIndicesArray();
		final int[] perspNodeInxArr = perspective.getNodeIndicesArray();

		if ((perspEdgeInxArr.length == 0) && (perspNodeInxArr.length == 0))
			return returnThis;

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeIndices DOCUMENT ME!
	 * @param edgeIndices DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createNode(int[] nodeIndices, int[] edgeIndices) {
		final GraphPerspective persp = createGraphPerspective(nodeIndices, edgeIndices);

		if (persp == null)
			return 0;

		return createNode(persp);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Edge removeEdge(Edge edge) {
		if ((edge.getRootGraph() == this) && (removeEdge(edge.getRootGraphIndex()) != 0))
			return edge;
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int removeEdge(final int edgeInx) {
		final int nativeEdgeInx = ~edgeInx;

		if (m_graph.edgeType(nativeEdgeInx) < 0)
			return 0;
		m_graph.edgeRemove(nativeEdgeInx);

		final Edge removedEdge = m_edges.getEdgeAtIndex(nativeEdgeInx);
		m_edges.setEdgeAtIndex(null, nativeEdgeInx);
		m_edgeDepot.recycleEdge(removedEdge);
		m_lis.rootGraphChanged(new RootGraphEdgesRemovedEvent(this, new Edge[] { removedEdge }));

		return edgeInx;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Edge> removeEdges(java.util.List<Edge> edges) {
		final java.util.ArrayList<Edge> returnThis = new java.util.ArrayList<Edge>();

		for (int i = 0; i < edges.size(); i++)
			if (removeEdge((Edge) edges.get(i)) != null)
				returnThis.add(edges.get(i));

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeIndices DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] removeEdges(int[] edgeIndices) {
		final int[] returnThis = new int[edgeIndices.length];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = removeEdge(edgeIndices[i]);

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createEdge(Node source, Node target) {
		return createEdge(source, target, source.getRootGraphIndex() != target.getRootGraphIndex());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param target DOCUMENT ME!
	 * @param directed DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createEdge(Node source, Node target, boolean directed) {
		if ((source.getRootGraph() == this) && (target.getRootGraph() == this))
			return createEdge(source.getRootGraphIndex(), target.getRootGraphIndex(), directed);
		else

			return 0;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param sourceNodeIndex DOCUMENT ME!
	 * @param targetNodeIndex DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createEdge(int sourceNodeIndex, int targetNodeIndex) {
		return createEdge(sourceNodeIndex, targetNodeIndex, sourceNodeIndex != targetNodeIndex);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param sourceNodeIndex DOCUMENT ME!
	 * @param targetNodeIndex DOCUMENT ME!
	 * @param directed DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int createEdge(int sourceNodeIndex, int targetNodeIndex, boolean directed) {
		final int nativeEdgeInx = m_graph.edgeCreate(~sourceNodeIndex, ~targetNodeIndex, directed);

		if (nativeEdgeInx < 0)
			return 0;

		final int returnThis = ~nativeEdgeInx;
		Edge newEdge = m_edgeDepot.getEdge(this, returnThis, null);
		m_edges.setEdgeAtIndex(newEdge, nativeEdgeInx);

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean containsNode(Node node) {
		return (node.getRootGraph() == this) && (getNode(node.getRootGraphIndex()) != null);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean containsEdge(Edge edge) {
		return (edge.getRootGraph() == this) && (getEdge(edge.getRootGraphIndex()) != null);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Node> neighborsList(Node node) {
		if (node.getRootGraph() == this) {
			final int nodeIndex = node.getRootGraphIndex();
			int[] adjacentEdgeIndices = getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);

			if (adjacentEdgeIndices == null)
				return null;

			m_hash.empty();

			final IntHash neighbors = m_hash;

			for (int i = 0; i < adjacentEdgeIndices.length; i++) {
				int neighborIndex = (nodeIndex ^ getEdgeSourceIndex(adjacentEdgeIndices[i])
				                    ^ getEdgeTargetIndex(adjacentEdgeIndices[i]));
				neighbors.put(~neighborIndex);
			}

			IntEnumerator enumx = neighbors.elements();
			java.util.ArrayList<Node> list = new java.util.ArrayList<Node>(enumx.numRemaining());

			while (enumx.numRemaining() > 0)
				list.add(getNode(~(enumx.nextInt())));

			return list;
		} else {
			return null;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param a DOCUMENT ME!
	 * @param b DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isNeighbor(Node a, Node b) {
		if ((a.getRootGraph() == this) && (b.getRootGraph() == this))
			return isNeighbor(a.getRootGraphIndex(), b.getRootGraphIndex());
		else

			return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInxA DOCUMENT ME!
	 * @param nodeInxB DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isNeighbor(int nodeInxA, int nodeInxB) {
		final IntIterator connectingEdges = m_graph.edgesConnecting(~nodeInxA, ~nodeInxB, true,
		                                                            true, true);

		if (connectingEdges == null)
			return false;

		return connectingEdges.hasNext();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean edgeExists(Node from, Node to) {
		if ((from.getRootGraph() == this) && (to.getRootGraph() == this))
			return edgeExists(from.getRootGraphIndex(), to.getRootGraphIndex());
		else

			return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean edgeExists(int fromNodeInx, int toNodeInx) {
		final IntIterator connectingEdges = m_graph.edgesConnecting(~fromNodeInx, ~toNodeInx, true,
		                                                            false, true);

		if (connectingEdges == null)
			return false;

		return connectingEdges.hasNext();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeCount(Node from, Node to, boolean countUndirectedEdges) {
		if ((from.getRootGraph() == this) && (to.getRootGraph() == this))
			return getEdgeCount(from.getRootGraphIndex(), to.getRootGraphIndex(),
			                    countUndirectedEdges);
		else

			return -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeCount(int fromNodeInx, int toNodeInx, boolean countUndirectedEdges) {
		final int[] connEdges = getEdgeIndicesArray(fromNodeInx, toNodeInx, countUndirectedEdges);

		if (connEdges == null)
			return -1;
		else

			return connEdges.length;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 * @param incomingDirected DOCUMENT ME!
	 * @param outgoingDirected DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getAdjacentEdgeIndicesArray(int nodeInx, boolean undirected,
	                                         boolean incomingDirected, boolean outgoingDirected) {
		final IntEnumerator adj = m_graph.edgesAdjacent(~nodeInx, outgoingDirected,
		                                                incomingDirected, undirected);

		if (adj == null)
			return null;

		final int[] returnThis = new int[adj.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = ~adj.nextInt();

		return returnThis;
	}

	private final IntHash m_hash2 = new IntHash();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getConnectingEdgeIndicesArray(int[] nodeInx) {
		m_hash2.empty();

		final IntHash nodeBucket = m_hash2;

		for (int i = 0; i < nodeInx.length; i++) {
			final int positiveNodeIndex = ~nodeInx[i];

			if (m_graph.nodeExists(positiveNodeIndex))
				nodeBucket.put(positiveNodeIndex);
			else

				return null;
		}

		m_hash.empty();

		final IntHash edgeBucket = m_hash;
		final IntEnumerator nodeIter = nodeBucket.elements();

		while (nodeIter.numRemaining() > 0) {
			final int thePositiveNode = nodeIter.nextInt();
			final IntEnumerator edgeIter = m_graph.edgesAdjacent(thePositiveNode, true, false, true);

			while (edgeIter.numRemaining() > 0) {
				final int candidateEdge = edgeIter.nextInt();
				final int otherEdgeNode = (thePositiveNode ^ m_graph.edgeSource(candidateEdge)
				                          ^ m_graph.edgeTarget(candidateEdge));

				if (otherEdgeNode == nodeBucket.get(otherEdgeNode))
					edgeBucket.put(candidateEdge);
			}
		}

		final IntEnumerator returnEdges = edgeBucket.elements();
		final int[] returnThis = new int[returnEdges.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = ~(returnEdges.nextInt());

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param undirectedEdges DOCUMENT ME!
	 * @param bothDirections DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray(int fromNodeInx, int toNodeInx, boolean undirectedEdges,
	                                 boolean bothDirections) {
		final IntIterator connectingEdges = m_graph.edgesConnecting(~fromNodeInx, ~toNodeInx, true,
		                                                            bothDirections, undirectedEdges);

		if (connectingEdges == null)
			return null;

		m_heap.empty();

		final MinIntHeap edgeBucket = m_heap;

		while (connectingEdges.hasNext())
			edgeBucket.toss(~connectingEdges.nextInt());

		final int[] returnThis = new int[edgeBucket.size()];
		edgeBucket.copyInto(returnThis, 0);

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Edge> edgesList(Node from, Node to) {
		if ((from.getRootGraph() == this) && (to.getRootGraph() == this))
			return edgesList(from.getRootGraphIndex(), to.getRootGraphIndex(), true);
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param includeUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public java.util.List<Edge> edgesList(int fromNodeInx, int toNodeInx, boolean includeUndirectedEdges) {
		final int[] edgeInx = getEdgeIndicesArray(fromNodeInx, toNodeInx, includeUndirectedEdges);

		if (edgeInx == null)
			return null;

		java.util.ArrayList<Edge> returnList = new java.util.ArrayList<Edge>(edgeInx.length);

		for (int i = 0; i < edgeInx.length; i++)
			returnList.add(getEdge(edgeInx[i]));

		return returnList;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param includeUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray(int fromNodeInx, int toNodeInx, boolean includeUndirectedEdges) {
		return getEdgeIndicesArray(fromNodeInx, toNodeInx, includeUndirectedEdges, false);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getInDegree(Node node) {
		if (node.getRootGraph() == this)
			return getInDegree(node.getRootGraphIndex());
		else

			return -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getInDegree(int nodeInx) {
		return getInDegree(nodeInx, true);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getInDegree(Node node, boolean countUndirectedEdges) {
		if (node.getRootGraph() == this)
			return getInDegree(node.getRootGraphIndex(), countUndirectedEdges);
		else

			return -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getInDegree(int nodeInx, boolean countUndirectedEdges) {
		final IntEnumerator adj = m_graph.edgesAdjacent(~nodeInx, false, true, countUndirectedEdges);

		if (adj == null)
			return -1;

		return adj.numRemaining();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getOutDegree(Node node) {
		if (node.getRootGraph() == this)
			return getOutDegree(node.getRootGraphIndex());
		else

			return -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getOutDegree(int nodeInx) {
		return getOutDegree(nodeInx, true);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getOutDegree(Node node, boolean countUndirectedEdges) {
		if (node.getRootGraph() == this)
			return getOutDegree(node.getRootGraphIndex(), countUndirectedEdges);
		else

			return -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getOutDegree(int nodeInx, boolean countUndirectedEdges) {
		final IntEnumerator adj = m_graph.edgesAdjacent(~nodeInx, true, false, countUndirectedEdges);

		if (adj == null)
			return -1;

		return adj.numRemaining();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getDegree(Node node) {
		if (node.getRootGraph() == this)
			return getDegree(node.getRootGraphIndex());
		else

			return -1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getDegree(int nodeInx) {
		final IntEnumerator adj = m_graph.edgesAdjacent(~nodeInx, true, true, true);

		if (adj == null)
			return -1;

		return adj.numRemaining();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIndex(Node node) {
		if (node.getRootGraph() == this)
			return node.getRootGraphIndex();
		else

			return 0;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Node getNode(int nodeInx) {
		if ((nodeInx < 0) && (nodeInx != 0x80000000))
			return m_nodes.getNodeAtIndex(~nodeInx);
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIndex(Edge edge) {
		if (edge.getRootGraph() == this)
			return edge.getRootGraphIndex();
		else

			return 0;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Edge getEdge(int edgeInx) {
		if ((edgeInx < 0) && (edgeInx != 0x80000000))
			return m_edges.getEdgeAtIndex(~edgeInx);
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeSourceIndex(int edgeInx) {
		return ~(m_graph.edgeSource(~edgeInx));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEdgeTargetIndex(int edgeInx) {
		return ~(m_graph.edgeTarget(~edgeInx));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isEdgeDirected(int edgeInx) {
		return m_graph.edgeType(~edgeInx) == 1;
	}


	private final IntStack m_stack = new IntStack();
	// The relationship between indices (both node and edge) in this
	// RootGraph and in the DynamicGraph is "flip the bits":
	// rootGraphIndex == ~(dynamicGraphIndex)
	private final DynamicGraph m_graph = DynamicGraphFactory.instantiateDynamicGraph();

	// For the most part, there will always be a listener registered with this
	// RootGraph (all GraphPerspectives will have registered listeners).  So,
	// instead of checking for null, just keep a permanent listener.
	private RootGraphChangeListener m_lis = new RootGraphChangeListener() {
		public void rootGraphChanged(RootGraphChangeEvent event) {
		}
	};

	// This hash is re-used by many methods.  Make sure to empty() it before
	// using it.  You can use it as a bag of integers or to filter integer
	// duplicates.  You don't need to empty() it after usage.
	private final IntHash m_hash = new IntHash();

	// This heap is re-used by several methods.  It's used primarily as a bucket
	// of integers; sorting with this heap is [probably] not done at all.
	// Make sure to empty() it before using it.
	private final MinIntHeap m_heap = new MinIntHeap();

	// This is our "node factory" and "node recyclery".
	private final FingNodeDepot m_nodeDepot;

	// This is our "edge factory" and "edge recyclery".
	private final FingEdgeDepot m_edgeDepot;

	// This is our index-to-node mapping.
	private final NodeArray m_nodes = new NodeArray();

	// This is our index-to-edge mapping.
	private final EdgeArray m_edges = new EdgeArray();

	Map<String,Integer> node_name_index_map;
	Map<String,Integer> edge_name_index_map;


	// Package visible constructor.
	FRootGraph() {
		this(new NodeDepository(), new EdgeDepository());
	}

	// Package visible constructor.
	FRootGraph(FingNodeDepot nodeDepot, FingEdgeDepot edgeDepot) {
		if (nodeDepot == null)
			throw new NullPointerException("nodeDepot is null");

		m_nodeDepot = nodeDepot;

		if (edgeDepot == null)
			throw new NullPointerException("edgeDepot is null");

		m_edgeDepot = edgeDepot;

		node_name_index_map = new HashMap<String,Integer>(); 
		edge_name_index_map = new HashMap<String,Integer>();
	}


/*
  File: CytoscapeFingRootGraph.java
*/




	/**
	 *  DOCUMENT ME!
	 *
	 * @param identifier DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Node getNode(String identifier) {
		if (node_name_index_map.containsKey(identifier))
			return getNode(node_name_index_map.get(identifier));
		else
			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param identifier DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Edge getEdge(String identifier) {
		if (edge_name_index_map.containsKey(identifier))
			return getEdge(edge_name_index_map.get(identifier));
		else
			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param identifier DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 */
	public void setNodeIdentifier(String identifier, int index) {
		if (index == 0 && node_name_index_map.containsKey(identifier)) {
			node_name_index_map.remove(identifier);
		} else {
			node_name_index_map.put(identifier, index);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param identifier DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 */
	public void setEdgeIdentifier(String identifier, int index) {
		if (index == 0 && edge_name_index_map.containsKey(identifier)) {
			edge_name_index_map.remove(identifier);
		} else {
			edge_name_index_map.put(identifier, index);
		}
	}
}

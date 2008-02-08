
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

import cytoscape.graph.fixed.FixedGraph;

import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.MinIntHeap;

import giny.filter.Filter;

import cytoscape.Edge;
import cytoscape.GraphPerspective;
import cytoscape.GraphPerspectiveChangeListener;
import cytoscape.Node;
import cytoscape.RootGraph;
import cytoscape.RootGraphChangeEvent;
import cytoscape.RootGraphChangeListener;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectFilter;
import cytoscape.data.SelectEventListener;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;


// Package visible class.
class FGraphPerspective implements GraphPerspective, FixedGraph {

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public IntEnumerator nodes() {
		final IntEnumerator nativeNodes = m_graph.nodes();

		return new IntEnumerator() {
				public int numRemaining() {
					return nativeNodes.numRemaining();
				}

				public int nextInt() {
					return ~m_nativeToRootNodeInxMap.getIntAtIndex(nativeNodes.nextInt());
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public IntEnumerator edges() {
		final IntEnumerator nativeEdges = m_graph.edges();

		return new IntEnumerator() {
				public int numRemaining() {
					return nativeEdges.numRemaining();
				}

				public int nextInt() {
					return ~m_nativeToRootEdgeInxMap.getIntAtIndex(nativeEdges.nextInt());
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean nodeExists(final int node) {
		if (node < 0) {
			return false;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(node);

		return m_graph.nodeExists(nativeNodeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public byte edgeType(final int edge) {
		if (edge < 0) {
			return -1;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(edge);

		return m_graph.edgeType(nativeEdgeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int edgeSource(final int edge) {
		if (edge < 0) {
			return -1;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(edge);
		final int nativeSource = m_graph.edgeSource(nativeEdgeInx);

		if (nativeSource < 0) {
			return -1;
		}

		return ~m_nativeToRootNodeInxMap.getIntAtIndex(nativeSource);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int edgeTarget(final int edge) {
		if (edge < 0) {
			return -1;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(edge);
		final int nativeTarget = m_graph.edgeTarget(nativeEdgeInx);

		if (nativeTarget < 0) {
			return -1;
		}

		return ~m_nativeToRootNodeInxMap.getIntAtIndex(nativeTarget);
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
	public IntEnumerator edgesAdjacent(final int node, boolean outgoing, boolean incoming,
	                                   boolean undirected) {
		if (node < 0) {
			return null;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(node);
		final IntEnumerator nativeEdges = m_graph.edgesAdjacent(nativeNodeInx, outgoing, incoming,
		                                                        undirected);

		if (nativeEdges == null) {
			return null;
		}

		return new IntEnumerator() {
				public int numRemaining() {
					return nativeEdges.numRemaining();
				}

				public int nextInt() {
					return ~m_nativeToRootEdgeInxMap.getIntAtIndex(nativeEdges.nextInt());
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
	public IntIterator edgesConnecting(final int node0, final int node1, boolean outgoing,
	                                   boolean incoming, boolean undirected) {
		if ((node0 < 0) || (node1 < 0)) {
			return null;
		}

		final int nativeNode0Inx = m_rootToNativeNodeInxMap.get(node0);
		final int nativeNode1Inx = m_rootToNativeNodeInxMap.get(node1);
		final IntIterator nativeEdges = m_graph.edgesConnecting(nativeNode0Inx, nativeNode1Inx,
		                                                        outgoing, incoming, undirected);

		if (nativeEdges == null) {
			return null;
		}

		return new IntIterator() {
				public boolean hasNext() {
					return nativeEdges.hasNext();
				}

				public int nextInt() {
					return ~m_nativeToRootEdgeInxMap.getIntAtIndex(nativeEdges.nextInt());
				}
			};
	}

	/////////////////////////////////
	/**
	 * DOCUMENT ME!
	 *
	 * @param listener DOCUMENT ME!
	 */
	public void addGraphPerspectiveChangeListener(GraphPerspectiveChangeListener listener) {
		// This method is not thread safe; synchronize on an object to make it so.
		m_lis[0] = GraphPerspectiveChangeListenerChain.add(m_lis[0], listener);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param listener DOCUMENT ME!
	 */
	public void removeGraphPerspectiveChangeListener(GraphPerspectiveChangeListener listener) {
		// This method is not thread safe; synchronize on an object to make it so.
		m_lis[0] = GraphPerspectiveChangeListenerChain.remove(m_lis[0], listener);
	}

	// The object returned shares the same RootGraph with this object.
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object clone() {
		final IntEnumerator nativeNodes = m_graph.nodes();
		final IntIterator rootGraphNodeInx = new IntIterator() {
			public boolean hasNext() {
				return nativeNodes.numRemaining() > 0;
			}

			public int nextInt() {
				return m_nativeToRootNodeInxMap.getIntAtIndex(nativeNodes.nextInt());
			}
		};

		final IntEnumerator nativeEdges = m_graph.edges();
		final IntIterator rootGraphEdgeInx = new IntIterator() {
			public boolean hasNext() {
				return nativeEdges.numRemaining() > 0;
			}

			public int nextInt() {
				return m_nativeToRootEdgeInxMap.getIntAtIndex(nativeEdges.nextInt());
			}
		};

		return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public RootGraph getRootGraph() {
		return m_root;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getNodeCount() {
		return m_graph.nodes().numRemaining();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getEdgeCount() {
		return m_graph.edges().numRemaining();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Iterator<Node> nodesIterator() {
		final IntEnumerator nodes = m_graph.nodes();

		return new Iterator<Node>() {
				public void remove() {
					throw new UnsupportedOperationException();
				}

				public boolean hasNext() {
					return nodes.numRemaining() > 0;
				}

				public Node next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}

					return m_root.getNode(m_nativeToRootNodeInxMap.getIntAtIndex(nodes.nextInt()));
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Node> nodesList() {
		final int nodeCount = getNodeCount();
		final ArrayList<Node> returnThis = new ArrayList<Node>(nodeCount);
		Iterator<Node> iter = nodesIterator();

		for (int i = 0; i < nodeCount; i++)
			returnThis.add(iter.next());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] getNodeIndicesArray() {
		IntEnumerator nodes = m_graph.nodes();
		final int[] returnThis = new int[nodes.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootNodeInxMap.getIntAtIndex(nodes.nextInt());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Iterator<Edge> edgesIterator() {
		final IntEnumerator edges = m_graph.edges();

		return new Iterator<Edge>() {
				public void remove() {
					throw new UnsupportedOperationException();
				}

				public boolean hasNext() {
					return edges.numRemaining() > 0;
				}

				public Edge next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}

					return m_root.getEdge(m_nativeToRootEdgeInxMap.getIntAtIndex(edges.nextInt()));
				}
			};
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> edgesList() {
		final int edgeCount = getEdgeCount();
		final ArrayList<Edge> returnThis = new ArrayList<Edge>(edgeCount);
		Iterator<Edge> iter = edgesIterator();

		for (int i = 0; i < edgeCount; i++)
			returnThis.add(iter.next());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray() {
		IntEnumerator edges = m_graph.edges();
		final int[] returnThis = new int[edges.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(edges.nextInt());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphFromNodeInx DOCUMENT ME!
	 * @param rootGraphToNodeInx DOCUMENT ME!
	 * @param undirectedEdges DOCUMENT ME!
	 * @param bothDirections DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray(int rootGraphFromNodeInx, int rootGraphToNodeInx,
	                                 boolean undirectedEdges, boolean bothDirections) {
		if (!((rootGraphFromNodeInx < 0) && (rootGraphToNodeInx < 0))) {
			return null;
		}

		final int nativeFromNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphFromNodeInx);
		final int nativeToNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphToNodeInx);
		final IntIterator connectingEdges = m_graph.edgesConnecting(nativeFromNodeInx,
		                                                            nativeToNodeInx, true,
		                                                            bothDirections, undirectedEdges);

		if (connectingEdges == null) {
			return null;
		}

		m_heap.empty();

		final MinIntHeap edgeBucket = m_heap;

		while (connectingEdges.hasNext())
			edgeBucket.toss(m_nativeToRootEdgeInxMap.getIntAtIndex(connectingEdges.nextInt()));

		final int[] returnThis = new int[edgeBucket.size()];
		edgeBucket.copyInto(returnThis, 0);

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Node hideNode(Node node) {
		if ((node.getRootGraph() == m_root) && (hideNode(node.getRootGraphIndex()) != 0)) {
			return node;
		} else {
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int hideNode(int rootGraphNodeInx) {
		return m_weeder.hideNode(this, rootGraphNodeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Node> hideNodes(List<Node> nodes) {
		final ArrayList<Node> returnThis = new ArrayList<Node>();

		for (int i = 0; i < nodes.size(); i++)
			if (hideNode((Node) nodes.get(i)) != null) {
				returnThis.add(nodes.get(i));
			}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] hideNodes(int[] rootGraphNodeInx) {
		return m_weeder.hideNodes(this, rootGraphNodeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Node restoreNode(Node node) {
		if ((node.getRootGraph() == m_root) && (restoreNode(node.getRootGraphIndex()) != 0)) {
			return node;
		} else {
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int restoreNode(int rootGraphNodeInx) {
		final int returnThis;

		if (_restoreNode(rootGraphNodeInx) != 0) {
			returnThis = rootGraphNodeInx;
		} else {
			returnThis = 0;
		}

		if (returnThis != 0) {
			final GraphPerspectiveChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphPerspectiveChanged(new GraphPerspectiveNodesRestoredEvent(this,
				                                                                        new int[] {
				                                                                            rootGraphNodeInx
				                                                                        }));
			}
		}

		return returnThis;
	}

	// Returns 0 if unsuccessful; returns the complement of the native node
	// index if successful.  Complement is '~', i.e., it's a negative value.
	private int _restoreNode(final int rootGraphNodeInx) {
		if (!(rootGraphNodeInx < 0)) {
			return 0;
		}

		int nativeNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);

		if ((m_root.getNode(rootGraphNodeInx) == null)
		    || !((nativeNodeInx < 0) || (nativeNodeInx == Integer.MAX_VALUE))) {
			return 0;
		}

		nativeNodeInx = m_graph.nodeCreate();
		m_rootToNativeNodeInxMap.put(~rootGraphNodeInx, nativeNodeInx);
		m_nativeToRootNodeInxMap.setIntAtIndex(rootGraphNodeInx, nativeNodeInx);

		return ~nativeNodeInx;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Node> restoreNodes(List<Node> nodes) {
		final ArrayList<Node> returnThis = new ArrayList<Node>();

		for (int i = 0; i < nodes.size(); i++)
			if (restoreNode((Node) nodes.get(i)) != null) {
				returnThis.add(nodes.get(i));
			}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 * @param restoreIncidentEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Node> restoreNodes(List<Node> nodes, boolean restoreIncidentEdges) {
		final List<Node> returnThis = restoreNodes(nodes);
		final int[] restoredNodeInx = new int[returnThis.size()];

		for (int i = 0; i < restoredNodeInx.length; i++)
			restoredNodeInx[i] = ((Node) returnThis.get(i)).getRootGraphIndex();

		final int[] connectingEdgeInx = m_root.getConnectingEdgeIndicesArray(restoredNodeInx);
		restoreEdges(connectingEdgeInx);

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 * @param restoreIncidentEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] restoreNodes(int[] rootGraphNodeInx, boolean restoreIncidentEdges) {
		final int[] returnThis = restoreNodes(rootGraphNodeInx);
		final int[] connectingEdgeInx = m_root.getConnectingEdgeIndicesArray(returnThis);
		restoreEdges(connectingEdgeInx);

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] restoreNodes(int[] rootGraphNodeInx) {
		m_heap.empty();

		final MinIntHeap successes = m_heap;
		final int[] returnThis = new int[rootGraphNodeInx.length];

		for (int i = 0; i < rootGraphNodeInx.length; i++)
			if (_restoreNode(rootGraphNodeInx[i]) != 0) {
				returnThis[i] = rootGraphNodeInx[i];
				successes.toss(returnThis[i]);
			}

		if (successes.size() > 0) {
			final GraphPerspectiveChangeListener listener = m_lis[0];

			if (listener != null) {
				final int[] successArr = new int[successes.size()];
				successes.copyInto(successArr, 0);
				listener.graphPerspectiveChanged(new GraphPerspectiveNodesRestoredEvent(this,
				                                                                        successArr));
			}
		}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Edge hideEdge(Edge edge) {
		if ((edge.getRootGraph() == m_root) && (hideEdge(edge.getRootGraphIndex()) != 0)) {
			return edge;
		} else {
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int hideEdge(int rootGraphEdgeInx) {
		return m_weeder.hideEdge(this, rootGraphEdgeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> hideEdges(List<Edge> edges) {
		final ArrayList<Edge> returnThis = new ArrayList<Edge>();

		for (int i = 0; i < edges.size(); i++)
			if (hideEdge((Edge) edges.get(i)) != null) {
				returnThis.add(edges.get(i));
			}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] hideEdges(int[] rootGraphEdgeInx) {
		return m_weeder.hideEdges(this, rootGraphEdgeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Edge restoreEdge(Edge edge) {
		if ((edge.getRootGraph() == m_root) && (restoreEdge(edge.getRootGraphIndex()) != 0)) {
			return edge;
		} else {
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int restoreEdge(int rootGraphEdgeInx) {
		final int returnThis = _restoreEdge(rootGraphEdgeInx);

		if (returnThis != 0) {
			final GraphPerspectiveChangeListener listener = m_lis[0];

			if (listener != null) {
				listener.graphPerspectiveChanged(new GraphPerspectiveEdgesRestoredEvent(this,
				                                                                        new int[] {
				                                                                            rootGraphEdgeInx
				                                                                        }));
			}
		}

		return returnThis;
	}

	// Use this only from _restoreEdge(int).  The heap will never grow
	// to more than the default size; it won't take up lots of memory.
	private final MinIntHeap m_heap__restoreEdge = new MinIntHeap();

	// Returns 0 if unsuccessful; otherwise returns the root index of edge.
	private int _restoreEdge(final int rootGraphEdgeInx) {
		if (!(rootGraphEdgeInx < 0)) {
			return 0;
		}

		int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);

		if ((m_root.getEdge(rootGraphEdgeInx) == null)
		    || !((nativeEdgeInx < 0) || (nativeEdgeInx == Integer.MAX_VALUE))) {
			return 0;
		}

		final int rootGraphSourceNodeInx = m_root.getEdgeSourceIndex(rootGraphEdgeInx);
		final int rootGraphTargetNodeInx = m_root.getEdgeTargetIndex(rootGraphEdgeInx);
		int nativeSourceNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphSourceNodeInx);
		int nativeTargetNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphTargetNodeInx);
		m_heap__restoreEdge.empty();

		final MinIntHeap restoredNodeRootInx = m_heap__restoreEdge;

		if ((nativeSourceNodeInx < 0) || (nativeSourceNodeInx == Integer.MAX_VALUE)) {
			nativeSourceNodeInx = ~(_restoreNode(rootGraphSourceNodeInx));
			restoredNodeRootInx.toss(rootGraphSourceNodeInx);

			if (rootGraphSourceNodeInx == rootGraphTargetNodeInx) {
				nativeTargetNodeInx = nativeSourceNodeInx;
			}
		}

		if ((nativeTargetNodeInx < 0) || (nativeTargetNodeInx == Integer.MAX_VALUE)) {
			nativeTargetNodeInx = ~(_restoreNode(rootGraphTargetNodeInx));
			restoredNodeRootInx.toss(rootGraphTargetNodeInx);
		}

		if (restoredNodeRootInx.size() > 0) {
			final GraphPerspectiveChangeListener listener = m_lis[0];

			if (listener != null) {
				final int[] restoredNodesArr = new int[restoredNodeRootInx.size()];
				restoredNodeRootInx.copyInto(restoredNodesArr, 0);
				listener.graphPerspectiveChanged(new GraphPerspectiveNodesRestoredEvent(this,
				                                                                        restoredNodesArr));
			}
		}

		nativeEdgeInx = m_graph.edgeCreate(nativeSourceNodeInx, nativeTargetNodeInx,
		                                   m_root.isEdgeDirected(rootGraphEdgeInx));
		m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx, nativeEdgeInx);
		m_nativeToRootEdgeInxMap.setIntAtIndex(rootGraphEdgeInx, nativeEdgeInx);

		return rootGraphEdgeInx;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> restoreEdges(List<Edge> edges) {
		final ArrayList<Edge> returnThis = new ArrayList<Edge>();

		for (int i = 0; i < edges.size(); i++)
			if (restoreEdge((Edge) edges.get(i)) != null) {
				returnThis.add(edges.get(i));
			}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] restoreEdges(int[] rootGraphEdgeInx) {
		m_heap.empty();

		final MinIntHeap successes = m_heap;
		final int[] returnThis = new int[rootGraphEdgeInx.length];

		for (int i = 0; i < rootGraphEdgeInx.length; i++) {
			returnThis[i] = _restoreEdge(rootGraphEdgeInx[i]);

			if (returnThis[i] != 0) {
				successes.toss(returnThis[i]);
			}
		}

		if (successes.size() > 0) {
			final GraphPerspectiveChangeListener listener = m_lis[0];

			if (listener != null) {
				final int[] successArr = new int[successes.size()];
				successes.copyInto(successArr, 0);
				listener.graphPerspectiveChanged(new GraphPerspectiveEdgesRestoredEvent(this,
				                                                                        successArr));
			}
		}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean containsNode(Node node) {
		int nativeInx;

		return (node.getRootGraph() == m_root)
		       && ((nativeInx = m_rootToNativeNodeInxMap.get(~(node.getRootGraphIndex()))) >= 0)
		       && (nativeInx != Integer.MAX_VALUE);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param recurse DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean containsNode(Node node, boolean recurse) {
		if (node.getRootGraph() != m_root) {
			return false;
		}

		final int nativeInx = m_rootToNativeNodeInxMap.get(~(node.getRootGraphIndex()));

		if ((nativeInx >= 0) && (nativeInx != Integer.MAX_VALUE)) {
			return true;
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean containsEdge(Edge edge) {
		int nativeInx;

		return (edge.getRootGraph() == m_root)
		       && ((nativeInx = m_rootToNativeEdgeInxMap.get(~(edge.getRootGraphIndex()))) >= 0)
		       && (nativeInx != Integer.MAX_VALUE);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 * @param recurse DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean containsEdge(Edge edge, boolean recurse) {
		if (edge.getRootGraph() != m_root) {
			return false;
		}

		final int nativeInx = m_rootToNativeEdgeInxMap.get(~(edge.getRootGraphIndex()));

		if ((nativeInx >= 0) && (nativeInx != Integer.MAX_VALUE)) {
			return true;
		}
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param persp DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphPerspective join(GraphPerspective persp) {
		final FGraphPerspective thisPersp = this;

		if (!(persp instanceof FGraphPerspective)) {
			return null;
		}

		final FGraphPerspective otherPersp = (FGraphPerspective) persp;

		if (otherPersp.m_root != thisPersp.m_root) {
			return null;
		}

		final IntEnumerator thisNativeNodes = thisPersp.m_graph.nodes();
		final IntEnumerator otherNativeNodes = otherPersp.m_graph.nodes();
		final IntIterator rootGraphNodeInx = new IntIterator() {
			public boolean hasNext() {
				return (thisNativeNodes.numRemaining() > 0)
				       || (otherNativeNodes.numRemaining() > 0);
			}

			public int nextInt() {
				if (thisNativeNodes.numRemaining() > 0) {
					return thisPersp.m_nativeToRootNodeInxMap.getIntAtIndex(thisNativeNodes.nextInt());
				} else {
					return otherPersp.m_nativeToRootNodeInxMap.getIntAtIndex(otherNativeNodes
					                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       .nextInt());
				}
			}
		};

		final IntEnumerator thisNativeEdges = thisPersp.m_graph.edges();
		final IntEnumerator otherNativeEdges = otherPersp.m_graph.edges();
		final IntIterator rootGraphEdgeInx = new IntIterator() {
			public boolean hasNext() {
				return (thisNativeEdges.numRemaining() > 0)
				       || (otherNativeEdges.numRemaining() > 0);
			}

			public int nextInt() {
				if (thisNativeEdges.numRemaining() > 0) {
					return thisPersp.m_nativeToRootEdgeInxMap.getIntAtIndex(thisNativeEdges.nextInt());
				} else {
					return otherPersp.m_nativeToRootEdgeInxMap.getIntAtIndex(otherNativeEdges.nextInt());
				}
			}
		};

		return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 * @param edges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphPerspective createGraphPerspective(Node[] nodes, Edge[] edges) {
		for (int i = 0; i < nodes.length; i++)
			if (!containsNode(nodes[i])) {
				return null;
			}

		for (int i = 0; i < edges.length; i++)
			if (!containsEdge(edges[i])) {
				return null;
			}

		return m_root.createGraphPerspective(nodes, edges);
	}

	public GraphPerspective createGraphPerspective(Collection<Node> nodes, Collection<Edge> edges) {
		return createGraphPerspective( nodes.toArray(new Node[nodes.size()]), 
		                               edges.toArray(new Edge[edges.size()]));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphPerspective createGraphPerspective(int[] rootGraphNodeInx, int[] rootGraphEdgeInx) {
		for (int i = 0; i < rootGraphNodeInx.length; i++) {
			final int rootGraphNodeIndex = rootGraphNodeInx[i];

			if (!(rootGraphNodeIndex < 0)) {
				return null;
			}

			final int nativeNodeIndex = m_rootToNativeNodeInxMap.get(~rootGraphNodeIndex);

			if ((nativeNodeIndex < 0) || (nativeNodeIndex == Integer.MAX_VALUE)) {
				return null;
			}
		}

		for (int i = 0; i < rootGraphEdgeInx.length; i++) {
			final int rootGraphEdgeIndex = rootGraphEdgeInx[i];

			if (!(rootGraphEdgeIndex < 0)) {
				return null;
			}

			final int nativeEdgeIndex = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeIndex);

			if ((nativeEdgeIndex < 0) || (nativeEdgeIndex == Integer.MAX_VALUE)) {
				return null;
			}
		}

		return m_root.createGraphPerspective(rootGraphNodeInx, rootGraphEdgeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param filter DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphPerspective createGraphPerspective(final Filter filter) {
		m_heap.empty();

		final MinIntHeap nodeInxBucket = m_heap;
		final Iterator<Node> nodesIter = nodesIterator();

		while (nodesIter.hasNext()) {
			final Node nodeCandidate = (Node) (nodesIter.next());

			if (filter.passesFilter(nodeCandidate)) {
				nodeInxBucket.toss(nodeCandidate.getRootGraphIndex());
			}
		}

		final int[] nodeInxArr = new int[nodeInxBucket.size()];
		nodeInxBucket.copyInto(nodeInxArr, 0);
		m_heap.empty();

		final MinIntHeap edgeInxBucket = m_heap;
		final Iterator edgesIter = edgesIterator();

		while (edgesIter.hasNext()) {
			final Edge edgeCandidate = (Edge) (edgesIter.next());

			if (filter.passesFilter(edgeCandidate)) {
				edgeInxBucket.toss(edgeCandidate.getRootGraphIndex());
			}
		}

		final int[] edgeInxArr = new int[edgeInxBucket.size()];
		edgeInxBucket.copyInto(edgeInxArr, 0);

		return m_root.createGraphPerspective(nodeInxArr, edgeInxArr);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Node> neighborsList(Node node) {
		if (node.getRootGraph() == m_root) {
			final int[] neighInx = neighborsArray(node.getRootGraphIndex());

			if (neighInx == null) {
				return null;
			}

			final ArrayList<Node> returnThis = new ArrayList<Node>(neighInx.length);

			for (int i = 0; i < neighInx.length; i++)
				returnThis.add(getNode(neighInx[i]));

			return returnThis;
		} else {
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeIndex DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] neighborsArray(final int nodeIndex) {
		int[] adjacentEdgeIndices = getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);

		if (adjacentEdgeIndices == null) {
			return null;
		}

		m_hash.empty();

		final IntHash neighbors = m_hash;

		for (int i = 0; i < adjacentEdgeIndices.length; i++) {
			int neighborIndex = (nodeIndex ^ getEdgeSourceIndex(adjacentEdgeIndices[i])
			                    ^ getEdgeTargetIndex(adjacentEdgeIndices[i]));
			neighbors.put(~neighborIndex);
		}

		IntEnumerator enumx = neighbors.elements();
		final int[] returnThis = new int[enumx.numRemaining()];
		int index = -1;

		while (enumx.numRemaining() > 0)
			returnThis[++index] = ~(enumx.nextInt());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param a DOCUMENT ME!
	 * @param b DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isNeighbor(Node a, Node b) {
		if ((a.getRootGraph() == m_root) && (b.getRootGraph() == m_root)) {
			return isNeighbor(a.getRootGraphIndex(), b.getRootGraphIndex());
		} else {
			return false;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInxA DOCUMENT ME!
	 * @param nodeInxB DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isNeighbor(final int nodeInxA, final int nodeInxB) {
		if (!((nodeInxA < 0) && (nodeInxB < 0))) {
			return false;
		}

		final int nativeNodeA = m_rootToNativeNodeInxMap.get(~nodeInxA);
		final int nativeNodeB = m_rootToNativeNodeInxMap.get(~nodeInxB);
		final IntIterator nativeConnEdgeIter = m_graph.edgesConnecting(nativeNodeA, nativeNodeB,
		                                                               true, true, true);

		if (nativeConnEdgeIter == null) {
			return false;
		}

		return nativeConnEdgeIter.hasNext();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean edgeExists(Node from, Node to) {
		if ((from.getRootGraph() == m_root) && (to.getRootGraph() == m_root)) {
			return edgeExists(from.getRootGraphIndex(), to.getRootGraphIndex());
		} else {
			return false;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean edgeExists(final int fromNodeInx, final int toNodeInx) {
		if (!((fromNodeInx < 0) && (toNodeInx < 0))) {
			return false;
		}

		final int nativeFromNode = m_rootToNativeNodeInxMap.get(~fromNodeInx);
		final int nativeToNode = m_rootToNativeNodeInxMap.get(~toNodeInx);
		final IntIterator nativeConnEdgeIter = m_graph.edgesConnecting(nativeFromNode,
		                                                               nativeToNode, true, false,
		                                                               true);

		if (nativeConnEdgeIter == null) {
			return false;
		}

		return nativeConnEdgeIter.hasNext();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getEdgeCount(Node from, Node to, boolean countUndirectedEdges) {
		if ((from.getRootGraph() == m_root) && (to.getRootGraph() == m_root)) {
			return getEdgeCount(from.getRootGraphIndex(), to.getRootGraphIndex(),
			                    countUndirectedEdges);
		} else {
			return -1;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getEdgeCount(int fromNodeInx, int toNodeInx, boolean countUndirectedEdges) {
		final int[] edgeIndicesArray = getEdgeIndicesArray(fromNodeInx, toNodeInx,
		                                                   countUndirectedEdges);

		if (edgeIndicesArray == null) {
			return -1;
		}

		return edgeIndicesArray.length;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param from DOCUMENT ME!
	 * @param to DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> edgesList(Node from, Node to) {
		if ((from.getRootGraph() == m_root) && (to.getRootGraph() == m_root)) {
			return edgesList(from.getRootGraphIndex(), to.getRootGraphIndex(), true);
		} else {
			return null;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param includeUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> edgesList(int fromNodeInx, int toNodeInx, boolean includeUndirectedEdges) {
		final int[] edgeInx = getEdgeIndicesArray(fromNodeInx, toNodeInx, includeUndirectedEdges);

		if (edgeInx == null) {
			return null;
		}

		ArrayList<Edge> returnList = new ArrayList<Edge>(edgeInx.length);

		for (int i = 0; i < edgeInx.length; i++)
			returnList.add(getEdge(edgeInx[i]));

		return returnList;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param fromNodeInx DOCUMENT ME!
	 * @param toNodeInx DOCUMENT ME!
	 * @param includeUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] getEdgeIndicesArray(int fromNodeInx, int toNodeInx, boolean includeUndirectedEdges) {
		return getEdgeIndicesArray(fromNodeInx, toNodeInx, includeUndirectedEdges, false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getInDegree(Node node) {
		if (node.getRootGraph() == m_root) {
			return getInDegree(node.getRootGraphIndex());
		} else {
			return -1;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getInDegree(int nodeInx) {
		return getInDegree(nodeInx, true);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getInDegree(Node node, boolean countUndirectedEdges) {
		if (node.getRootGraph() == m_root) {
			return getInDegree(node.getRootGraphIndex(), countUndirectedEdges);
		} else {
			return -1;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getInDegree(final int nodeInx, boolean countUndirectedEdges) {
		if (!(nodeInx < 0)) {
			return -1;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx);
		final IntEnumerator adj = m_graph.edgesAdjacent(nativeNodeInx, false, true,
		                                                countUndirectedEdges);

		if (adj == null) {
			return -1;
		}

		return adj.numRemaining();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getOutDegree(Node node) {
		if (node.getRootGraph() == m_root) {
			return getOutDegree(node.getRootGraphIndex());
		} else {
			return -1;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getOutDegree(int nodeInx) {
		return getOutDegree(nodeInx, true);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getOutDegree(Node node, boolean countUndirectedEdges) {
		if (node.getRootGraph() == m_root) {
			return getOutDegree(node.getRootGraphIndex(), countUndirectedEdges);
		} else {
			return -1;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param countUndirectedEdges DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getOutDegree(int nodeInx, boolean countUndirectedEdges) {
		if (!(nodeInx < 0)) {
			return -1;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx);
		final IntEnumerator adj = m_graph.edgesAdjacent(nativeNodeInx, true, false,
		                                                countUndirectedEdges);

		if (adj == null) {
			return -1;
		}

		return adj.numRemaining();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getDegree(Node node) {
		if (node.getRootGraph() == m_root) {
			return getDegree(node.getRootGraphIndex());
		} else {
			return -1;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getDegree(final int nodeInx) {
		if (!(nodeInx < 0)) {
			return -1;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx);
		final IntEnumerator adj = m_graph.edgesAdjacent(nativeNodeInx, true, true, true);

		if (adj == null) {
			return -1;
		}

		return adj.numRemaining();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getIndex(Node node) {
		if ((node.getRootGraph() == m_root)
		    && (getRootGraphNodeIndex(node.getRootGraphIndex()) == node.getRootGraphIndex())) {
			return node.getRootGraphIndex();
		} else {
			return 0;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getRootGraphNodeIndex(int rootGraphNodeInx) {
		if (!(rootGraphNodeInx < 0)) {
			return 0;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);

		if ((nativeNodeInx < 0) || (nativeNodeInx == Integer.MAX_VALUE)) {
			return 0;
		}

		return rootGraphNodeInx;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphNodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Node getNode(int rootGraphNodeInx) {
		return m_root.getNode(getRootGraphNodeIndex(rootGraphNodeInx));
	}

	// Package visible.
	boolean containsNode(int rootGraphNodeInx) {
		if (rootGraphNodeInx >= 0) {
			return false;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);

		return ((nativeNodeInx >= 0) && (nativeNodeInx != Integer.MAX_VALUE));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getIndex(Edge edge) {
		if ((edge.getRootGraph() == m_root)
		    && (getRootGraphEdgeIndex(edge.getRootGraphIndex()) == edge.getRootGraphIndex())) {
			return edge.getRootGraphIndex();
		} else {
			return 0;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getRootGraphEdgeIndex(int rootGraphEdgeInx) {
		if (!(rootGraphEdgeInx < 0)) {
			return 0;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);

		if ((nativeEdgeInx < 0) || (nativeEdgeInx == Integer.MAX_VALUE)) {
			return 0;
		}

		return rootGraphEdgeInx;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rootGraphEdgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Edge getEdge(int rootGraphEdgeInx) {
		return m_root.getEdge(getRootGraphEdgeIndex(rootGraphEdgeInx));
	}

	// Package visible.
	boolean containsEdge(int rootGraphEdgeInx) {
		if (rootGraphEdgeInx >= 0) {
			return false;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);

		return ((nativeEdgeInx >= 0) && (nativeEdgeInx != Integer.MAX_VALUE));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getEdgeSourceIndex(int edgeInx) {
		if (!(edgeInx < 0)) {
			return 0;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~edgeInx);
		final int nativeSrcNodeInx = m_graph.edgeSource(nativeEdgeInx);

		if (nativeSrcNodeInx < 0) {
			return 0;
		}

		return m_nativeToRootNodeInxMap.getIntAtIndex(nativeSrcNodeInx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getEdgeTargetIndex(int edgeInx) {
		if (!(edgeInx < 0)) {
			return 0;
		}

		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~edgeInx);
		final int nativeTrgNodeInx = m_graph.edgeTarget(nativeEdgeInx);

		if (nativeTrgNodeInx < 0) {
			return 0;
		}

		return m_nativeToRootNodeInxMap.getIntAtIndex(nativeTrgNodeInx);
	}

	// Throws IllegalArgumentException
	/**
	 * DOCUMENT ME!
	 *
	 * @param edgeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isEdgeDirected(int edgeInx) {
		if (!(edgeInx < 0)) {
			throw new IllegalArgumentException("edge index is not negative");
		}

		return m_graph.edgeType(m_rootToNativeEdgeInxMap.get(~edgeInx)) == 1;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 * @param incoming DOCUMENT ME!
	 * @param outgoing DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> getAdjacentEdgesList(Node node, boolean undirected, boolean incoming,
	                                           boolean outgoing) {
		if (node.getRootGraph() != m_root) {
			return null;
		}

		final int[] adjEdgeInx = getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), undirected,
		                                                     incoming, outgoing);

		if (adjEdgeInx == null) {
			return null;
		}

		final ArrayList<Edge> returnThis = new ArrayList<Edge>(adjEdgeInx.length);

		for (int i = 0; i < adjEdgeInx.length; i++)
			returnThis.add(getEdge(adjEdgeInx[i]));

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 * @param undirected DOCUMENT ME!
	 * @param incomingDirected DOCUMENT ME!
	 * @param outgoingDirected DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] getAdjacentEdgeIndicesArray(int nodeInx, boolean undirected,
	                                         boolean incomingDirected, boolean outgoingDirected) {
		if (!(nodeInx < 0)) {
			return null;
		}

		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx);
		final IntEnumerator adj = m_graph.edgesAdjacent(nativeNodeInx, outgoingDirected,
		                                                incomingDirected, undirected);

		if (adj == null) {
			return null;
		}

		final int[] returnThis = new int[adj.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(adj.nextInt());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodes DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<Edge> getConnectingEdges(List<Node> nodes) {
		m_heap.empty();

		final MinIntHeap nodeInxBucket = m_heap;

		for (int i = 0; i < nodes.size(); i++) {
			Node node = (Node) (nodes.get(i));

			if (node.getRootGraph() == m_root) {
				nodeInxBucket.toss(node.getRootGraphIndex());
			} else {
				return null;
			}
		}

		final int[] nodeInxArr = new int[nodeInxBucket.size()];
		nodeInxBucket.copyInto(nodeInxArr, 0);

		final int[] connEdgeInxArr = getConnectingEdgeIndicesArray(nodeInxArr);

		if (connEdgeInxArr == null) {
			return null;
		}

		final ArrayList<Edge> returnThis = new ArrayList<Edge>(connEdgeInxArr.length);

		for (int i = 0; i < connEdgeInxArr.length; i++)
			returnThis.add(getEdge(connEdgeInxArr[i]));

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int[] getConnectingEdgeIndicesArray(int[] nodeInx) {
		final IntHash nativeNodeBucket = new IntHash();

		for (int i = 0; i < nodeInx.length; i++) {
			if (!(nodeInx[i] < 0)) {
				return null;
			}

			final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx[i]);

			if (m_graph.nodeExists(nativeNodeInx)) {
				nativeNodeBucket.put(nativeNodeInx);
			} else {
				return null;
			}
		}

		m_hash.empty();

		final IntHash nativeEdgeBucket = m_hash;
		final IntEnumerator nativeNodeEnum = nativeNodeBucket.elements();

		while (nativeNodeEnum.numRemaining() > 0) {
			final int nativeNodeIndex = nativeNodeEnum.nextInt();
			final IntEnumerator nativeAdjEdgeEnum = m_graph.edgesAdjacent(nativeNodeIndex, true,
			                                                              false, true);

			while (nativeAdjEdgeEnum.numRemaining() > 0) {
				final int nativeCandidateEdge = nativeAdjEdgeEnum.nextInt();
				final int nativeOtherEdgeNode = (nativeNodeIndex
				                                ^ m_graph.edgeSource(nativeCandidateEdge)
				                                ^ m_graph.edgeTarget(nativeCandidateEdge));

				if (nativeOtherEdgeNode == nativeNodeBucket.get(nativeOtherEdgeNode)) {
					nativeEdgeBucket.put(nativeCandidateEdge);
				}
			}
		}

		final IntEnumerator nativeReturnEdges = nativeEdgeBucket.elements();
		final int[] returnThis = new int[nativeReturnEdges.numRemaining()];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(nativeReturnEdges.nextInt());

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nodeInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphPerspective createGraphPerspective(int[] nodeInx) {
		return createGraphPerspective(nodeInx, getConnectingEdgeIndicesArray(nodeInx));
	}

	/**
	 * DOCUMENT ME!
	 */
	public void finalize() {
		m_root.removeRootGraphChangeListener(m_changeSniffer);
	}

	// Nodes and edges in this graph are called "native indices" throughout
	// this class.
	private final DynamicGraph m_graph;
	private final FRootGraph m_root;

	// This is an array of length 1 - we need an array as an extra reference
	// to a reference because some other inner classes need to know what the
	// current listener is.
	private final GraphPerspectiveChangeListener[] m_lis;

	// RootGraph indices are negative in these arrays.
	private final IntArray m_nativeToRootNodeInxMap;
	private final IntArray m_nativeToRootEdgeInxMap;

	// RootGraph indices are ~ (complements) of the real RootGraph indices
	// in these hashtables.
	private final IntIntHash m_rootToNativeNodeInxMap;
	private final IntIntHash m_rootToNativeEdgeInxMap;

	// This is a utilitarian heap that is used as a bucket of ints.
	// Don't forget to empty() it before using it.
	private final MinIntHeap m_heap;

	// This is a utilitarian hash that is used as a collision detecting
	// bucket of ints.  Don't forget to empty() it before using it.
	private final IntHash m_hash;
	private final GraphWeeder m_weeder;

	// We need to remove this listener from the RootGraph during finalize().
	private final RootGraphChangeSniffer m_changeSniffer;

	// Package visible constructor.  rootGraphNodeInx
	// need not contain all endpoint nodes corresponding to edges in
	// rootGraphEdgeInx - this is calculated automatically by this constructor.
	// If any index does not correspond to an existing node or edge, an
	// IllegalArgumentException is thrown.  The indices lists need not be
	// non-repeating - the logic in this constructor handles duplicate
	// filtering.
	FGraphPerspective(FRootGraph root, IntIterator rootGraphNodeInx, IntIterator rootGraphEdgeInx)
	    throws IllegalArgumentException // If any index is not in RootGraph.
	 {
		m_graph = DynamicGraphFactory.instantiateDynamicGraph();
		m_root = root;
		m_lis = new GraphPerspectiveChangeListener[1];
		m_nativeToRootNodeInxMap = new IntArray();
		m_nativeToRootEdgeInxMap = new IntArray();
		m_rootToNativeNodeInxMap = new IntIntHash();
		m_rootToNativeEdgeInxMap = new IntIntHash();
		m_heap = new MinIntHeap();
		m_hash = new IntHash();
		m_weeder = new GraphWeeder(m_root, m_graph, m_nativeToRootNodeInxMap,
		                           m_nativeToRootEdgeInxMap, m_rootToNativeNodeInxMap,
		                           m_rootToNativeEdgeInxMap, m_lis, m_heap);
		m_changeSniffer = new RootGraphChangeSniffer(m_weeder);

		while (rootGraphNodeInx.hasNext()) {
			final int rootNodeInx = rootGraphNodeInx.nextInt();

			if (m_root.getNode(rootNodeInx) != null) {
				if (m_rootToNativeNodeInxMap.get(~rootNodeInx) >= 0) {
					continue;
				}

				final int nativeNodeInx = m_graph.nodeCreate();
				m_rootToNativeNodeInxMap.put(~rootNodeInx, nativeNodeInx);
				m_nativeToRootNodeInxMap.setIntAtIndex(rootNodeInx, nativeNodeInx);
			} else {
				throw new IllegalArgumentException("node with index " + rootNodeInx
				                                   + " not in RootGraph");
			}
		}

		while (rootGraphEdgeInx.hasNext()) {
			final int rootEdgeInx = rootGraphEdgeInx.nextInt();

			if (m_root.getEdge(rootEdgeInx) != null) {
				if (m_rootToNativeEdgeInxMap.get(~rootEdgeInx) >= 0) {
					continue;
				}

				final int rootSrcInx = m_root.getEdgeSourceIndex(rootEdgeInx);
				final int rootTrgInx = m_root.getEdgeTargetIndex(rootEdgeInx);
				final boolean edgeDirected = m_root.isEdgeDirected(rootEdgeInx);
				int nativeSrcInx = m_rootToNativeNodeInxMap.get(~rootSrcInx);

				if (nativeSrcInx < 0) {
					nativeSrcInx = m_graph.nodeCreate();
					m_rootToNativeNodeInxMap.put(~rootSrcInx, nativeSrcInx);
					m_nativeToRootNodeInxMap.setIntAtIndex(rootSrcInx, nativeSrcInx);
				}

				int nativeTrgInx = m_rootToNativeNodeInxMap.get(~rootTrgInx);

				if (nativeTrgInx < 0) {
					nativeTrgInx = m_graph.nodeCreate();
					m_rootToNativeNodeInxMap.put(~rootTrgInx, nativeTrgInx);
					m_nativeToRootNodeInxMap.setIntAtIndex(rootTrgInx, nativeTrgInx);
				}

				final int nativeEdgeInx = m_graph.edgeCreate(nativeSrcInx, nativeTrgInx,
				                                             edgeDirected);
				m_rootToNativeEdgeInxMap.put(~rootEdgeInx, nativeEdgeInx);
				m_nativeToRootEdgeInxMap.setIntAtIndex(rootEdgeInx, nativeEdgeInx);
			} else {
				throw new IllegalArgumentException("edge with index " + rootEdgeInx
				                                   + " not in RootGraph");
			}
		}

		m_root.addRootGraphChangeListener(m_changeSniffer);
		Integer i = new Integer(uid_counter++);
		System.err.println("TTTTTTTTTTTTTTTTTtt setting identifier to: " + i);
		identifier = i.toString();
		selectFilter = new SelectFilter(this);
	}

	// Cannot have any recursize reference to a FGraphPerspective in this
	// object instance - we want to allow garbage collection of unused
	// GraphPerspective objects.
	private final static class RootGraphChangeSniffer implements RootGraphChangeListener {
		private final GraphWeeder m_weeder;

		private RootGraphChangeSniffer(GraphWeeder weeder) {
			m_weeder = weeder;
		}

		public final void rootGraphChanged(RootGraphChangeEvent evt) {
			if ((evt.getType() & RootGraphChangeEvent.NODES_REMOVED_TYPE) != 0) {
				m_weeder.hideNodes(evt.getSource(), evt.getRemovedNodes());
			}

			if ((evt.getType() & RootGraphChangeEvent.EDGES_REMOVED_TYPE) != 0) {
				m_weeder.hideEdges(evt.getSource(), evt.getRemovedEdges());
			}
		}
	}

	// An instance of this class cannot have any recursive reference to a
	// FGraphPerspective object.  The idea behind this class is to allow
	// garbage collection of unused GraphPerspective objects.  This class
	// is used by the RootGraphChangeSniffer to remove nodes/edges from
	// a GraphPerspective; this class is also used by this GraphPerspective
	// implementation itself.
	private final static class GraphWeeder {
		private final RootGraph m_root;
		private final DynamicGraph m_graph;
		private final IntArray m_nativeToRootNodeInxMap;
		private final IntArray m_nativeToRootEdgeInxMap;
		private final IntIntHash m_rootToNativeNodeInxMap;
		private final IntIntHash m_rootToNativeEdgeInxMap;

		// This is an array of length 1 - we need an array as an extra reference
		// to a reference because the surrounding GraphPerspective will be
		// modifying the entry at index 0 in this array.
		private final GraphPerspectiveChangeListener[] m_lis;

		// This is a utilitarian heap that is used as a bucket of ints.
		// Don't forget to empty() it before using it.
		private final MinIntHeap m_heap;

		private GraphWeeder(RootGraph root, DynamicGraph graph, IntArray nativeToRootNodeInxMap,
		                    IntArray nativeToRootEdgeInxMap, IntIntHash rootToNativeNodeInxMap,
		                    IntIntHash rootToNativeEdgeInxMap,
		                    GraphPerspectiveChangeListener[] listener, MinIntHeap heap) {
			m_root = root;
			m_graph = graph;
			m_nativeToRootNodeInxMap = nativeToRootNodeInxMap;
			m_nativeToRootEdgeInxMap = nativeToRootEdgeInxMap;
			m_rootToNativeNodeInxMap = rootToNativeNodeInxMap;
			m_rootToNativeEdgeInxMap = rootToNativeEdgeInxMap;
			m_lis = listener;
			m_heap = heap;
		}

		// RootGraphChangeSniffer is not to call this method.  We rely on
		// the specified node still existing in the RootGraph in this method.
		private final int hideNode(GraphPerspective source, int rootGraphNodeInx) {
			// first see if we can hide the node
			final int returnThis = canHideNode(rootGraphNodeInx);

			// then notify everyone that we will be hiding nodes
			if (returnThis != 0) {
				final GraphPerspectiveChangeListener listener = m_lis[0];

				if (listener != null) {
					final Node removedNode = m_root.getNode(rootGraphNodeInx);
					listener.graphPerspectiveChanged(new GraphPerspectiveNodesHiddenEvent(source,
					                                                                      new Node[] {
					                                                                          removedNode
					                                                                      }));
				}

				// now actually hide it
				actuallyHideNode(source, rootGraphNodeInx);
			}

			return returnThis;
		}

		// Don't call this method from outside this inner class.
		// Returns 0 if and only if hiding this node will fail. 
		// Otherwise returns the root node index.
		private int canHideNode(final int rootGraphNodeInx) {
			if (!(rootGraphNodeInx < 0)) {
				return 0;
			}

			final int nativeNodeIndex = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);

			if (nativeNodeIndex < 0) {
				return 0;
			}

			final IntEnumerator nativeEdgeInxEnum = m_graph.edgesAdjacent(nativeNodeIndex, true,
			                                                              true, true);

			if (nativeEdgeInxEnum == null) {
				return 0;
			}

			return rootGraphNodeInx;
		}

		//
		// This method should ONLY be called AFTER
		// canHideNode(index) has been called!!!!
		//
		// If this method fails, it will throw an exception.   
		// Otherwise, assume everything is copacetic.
		private void actuallyHideNode(Object source, final int rootGraphNodeInx) {
			final int nativeNodeIndex = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);
			final IntEnumerator nativeEdgeInxEnum = m_graph.edgesAdjacent(nativeNodeIndex, true,
			                                                              true, true);

			if (nativeEdgeInxEnum.numRemaining() > 0) {
				final Edge[] edgeRemoveArr = new Edge[nativeEdgeInxEnum.numRemaining()];

				for (int i = 0; i < edgeRemoveArr.length; i++) {
					final int rootGraphEdgeInx = m_nativeToRootEdgeInxMap.getIntAtIndex(nativeEdgeInxEnum
					                                                                    .nextInt());
					// The edge returned by the RootGraph won't be null even if this
					// hideNode operation is triggered by a node being removed from
					// the underlying RootGraph - this is because when a node is removed
					// from an underlying RootGraph, all touching edges to that node are
					// removed first from that RootGraph, and corresponding edge removal
					// events are fired before the node removal event is fired.
					edgeRemoveArr[i] = m_root.getEdge(rootGraphEdgeInx);
				}

				hideEdges(source, edgeRemoveArr);
			}

			// nativeNodeIndex tested for validity with adjacentEdges() above.
			if (m_graph.nodeRemove(nativeNodeIndex)) {
				m_rootToNativeNodeInxMap.put(~rootGraphNodeInx, Integer.MAX_VALUE);
				m_nativeToRootNodeInxMap.setIntAtIndex(0, nativeNodeIndex);
			} else {
				throw new IllegalStateException("internal error - node didn't exist, its adjacent edges did");
			}
		}

		// This heap is to be used directly only by
		// hideNodes(GraphPerspective, int[]) and by hideNodes(Object, Node[]).
		private final MinIntHeap m_heap_hideNodes = new MinIntHeap();

		// RootGraphChangeSniffer is not to call this method.  We rely on
		// the specified nodes still existing in the RootGraph in this method.
		private final int[] hideNodes(GraphPerspective source, int[] rootNodeInx) {
			// We can't use m_heap here because it's potentially used by every
			// actuallHideNode() during hiding of edges.
			m_heap_hideNodes.empty();

			final MinIntHeap successes = m_heap_hideNodes;
			final int[] returnThis = new int[rootNodeInx.length];

			// check which nodes we can actually hide
			for (int i = 0; i < rootNodeInx.length; i++) {
				returnThis[i] = canHideNode(rootNodeInx[i]);

				if (returnThis[i] != 0) {
					successes.toss(i);
				}
			}

			// notify everyone of the node we're about to hide
			if (successes.size() > 0) {
				final GraphPerspectiveChangeListener listener = m_lis[0];

				if (listener != null) {
					final Node[] successArr = new Node[successes.size()];
					final IntEnumerator enumx = successes.elements();
					int index = -1;

					while (enumx.numRemaining() > 0)
						successArr[++index] = m_root.getNode(rootNodeInx[enumx.nextInt()]);

					listener.graphPerspectiveChanged(new GraphPerspectiveNodesHiddenEvent(source,
					                                                                      successArr));
				}
			}

			// now actually hide nodes
			final IntEnumerator successEnum = successes.elements();

			while (successEnum.numRemaining() > 0)
				actuallyHideNode(source, rootNodeInx[successEnum.nextInt()]);

			return returnThis;
		}

		// Entries in the nodes array may not be null.
		// This method is to be called by RootGraphChangeSniffer.  It may also
		// be called by others - therefore don't assume that the nodes to be
		// hidden here don't have any adjacent edges.
		private final void hideNodes(Object source, Node[] nodes) {
			// We can't use m_heap here because it's potentially used by every
			// actuallyHideNode() during hiding of edges.
			m_heap_hideNodes.empty();

			final MinIntHeap successes = m_heap_hideNodes;

			// check to see if we can hide the nodes
			for (int i = 0; i < nodes.length; i++) {
				if (canHideNode(nodes[i].getRootGraphIndex()) != 0) {
					successes.toss(i);
				}
			}

			// notify everyone of the nodes about to be hidden
			if (successes.size() > 0) {
				final GraphPerspectiveChangeListener listener = m_lis[0];

				if (listener != null) {
					final Node[] successArr = new Node[successes.size()];
					final IntEnumerator enumx = successes.elements();
					int index = -1;

					while (enumx.numRemaining() > 0)
						successArr[++index] = nodes[enumx.nextInt()];

					listener.graphPerspectiveChanged(new GraphPerspectiveNodesHiddenEvent(source,
					                                                                      successArr));
				}

				// now actually hide the nodes
				final IntEnumerator successEnum = successes.elements();

				while (successEnum.numRemaining() > 0)
					actuallyHideNode(source, nodes[successEnum.nextInt()].getRootGraphIndex());
			}
		}

		// RootGraphChangeSniffer is not to call this method.  We rely on
		// the specified edge still existing in the RootGraph in this method.
		private final int hideEdge(GraphPerspective source, int rootGraphEdgeInx) {
			// see if we can hide the edge
			final int returnThis = canHideEdge(rootGraphEdgeInx);

			if (returnThis != 0) {
				final GraphPerspectiveChangeListener listener = m_lis[0];

				// notify listeners of edge about to be hidden
				if (listener != null) {
					final Edge removedEdge = m_root.getEdge(rootGraphEdgeInx);
					listener.graphPerspectiveChanged(new GraphPerspectiveEdgesHiddenEvent(source,
					                                                                      new Edge[] {
					                                                                          removedEdge
					                                                                      }));
				}

				// hide the edge
				actuallyHideEdge(rootGraphEdgeInx);
			}

			return returnThis;
		}

		// Don't call this method from outside this inner class.
		// Returns 0 if and only if we're actually able to hide an edge. 
		// Otherwise returns the root edge index.
		private int canHideEdge(int rootGraphEdgeInx) {
			if (!(rootGraphEdgeInx < 0)) {
				return 0;
			}

			final int nativeEdgeIndex = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);

			if ((nativeEdgeIndex < 0) || (nativeEdgeIndex == Integer.MAX_VALUE)) {
				return 0;
			}

			return rootGraphEdgeInx;
		}

		//
		// This method should ONLY be called AFTER
		// canHideEdge(index) has been called!!!!
		//
		// If this method fails, it will throw an exception.   
		// Otherwise, assume everything is copacetic.
		private void actuallyHideEdge(int rootGraphEdgeInx) {
			final int nativeEdgeIndex = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);

			if (m_graph.edgeRemove(nativeEdgeIndex)) {
				m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx, Integer.MAX_VALUE);
				m_nativeToRootEdgeInxMap.setIntAtIndex(0, nativeEdgeIndex);
			} else {
				throw new IllegalStateException("internal error - couldn't hide edge: "
				                                + rootGraphEdgeInx);
			}
		}

		// RootGraphChangeSniffer is not to call this method.  We rely on
		// the specified edges still existing in the RootGraph in this method.
		private final int[] hideEdges(GraphPerspective source, int[] rootEdgeInx) {
			m_heap.empty();

			final MinIntHeap successes = m_heap;
			final int[] returnThis = new int[rootEdgeInx.length];

			// see if we can hide the edge
			for (int i = 0; i < rootEdgeInx.length; i++) {
				returnThis[i] = canHideEdge(rootEdgeInx[i]);

				if (returnThis[i] != 0) {
					successes.toss(i);
				}
			}

			if (successes.size() > 0) {
				final GraphPerspectiveChangeListener listener = m_lis[0];

				// notify listeners of the edges about to be hidden
				if (listener != null) {
					final Edge[] successArr = new Edge[successes.size()];
					final IntEnumerator enumx = successes.elements();
					int index = -1;

					while (enumx.numRemaining() > 0)
						successArr[++index] = m_root.getEdge(rootEdgeInx[enumx.nextInt()]);

					listener.graphPerspectiveChanged(new GraphPerspectiveEdgesHiddenEvent(source,
					                                                                      successArr));
				}

				// actually hide edges
				final IntEnumerator successEnum = successes.elements();

				while (successEnum.numRemaining() > 0)
					actuallyHideEdge(rootEdgeInx[successEnum.nextInt()]);
			}

			return returnThis;
		}

		// Entries in the edges array may not be null.
		// This method is to be called by RootGraphChangeSniffer.
		private final void hideEdges(Object source, Edge[] edges) {
			m_heap.empty();

			final MinIntHeap successes = m_heap;

			// check to see if we can hide edges
			for (int i = 0; i < edges.length; i++) {
				if (edges[i] == null)
					continue;

				if (canHideEdge(edges[i].getRootGraphIndex()) != 0)
					successes.toss(i);
			}

			if (successes.size() > 0) {
				final GraphPerspectiveChangeListener listener = m_lis[0];

				// notify listeners 
				if (listener != null) {
					final Edge[] successArr = new Edge[successes.size()];
					final IntEnumerator enumx = successes.elements();
					int index = -1;

					while (enumx.numRemaining() > 0)
						successArr[++index] = edges[enumx.nextInt()];

					listener.graphPerspectiveChanged(new GraphPerspectiveEdgesHiddenEvent(source,
					                                                                      successArr));
				}

				// now actually hide edges
				final IntEnumerator successEnum = successes.elements();

				while (successEnum.numRemaining() > 0)
					actuallyHideEdge(edges[successEnum.nextInt()].getRootGraphIndex());
			}
		}
	}


/*
  File: FingCyNetwork.java
*/


	private static int uid_counter = 0;
	private String identifier;
	protected String title;

	/**
	 * The default object to set the selected state of nodes and edges
	 */
	protected final SelectFilter selectFilter;

	/**
	 * Can Change
	 */
	public String getTitle() {
		if (title == null)
			return identifier;

		return title;
	}

	/**
	 * Can Change
	 * Throws a PropertyChangeEvent if the title has changed with a CyNetworkTitleChange object that contains the network id and the name.
	 */
	public void setTitle(String new_id) {
		if (title == null) {
			title = new_id;
		} else if (!title.equals(new_id)) { // new title is different from the old one
			CyNetworkTitleChange OldTitle = new CyNetworkTitleChange(this.getIdentifier(), title);
			title = new_id;
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_TITLE_MODIFIED, OldTitle, new CyNetworkTitleChange(this.getIdentifier(), new_id) );
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param new_id DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String setIdentifier(String new_id) {
		identifier = new_id;

		return identifier;
	}

	/**
	 * Appends all of the nodes and edges in teh given Network to
	 * this Network
	 */
	public void appendNetwork(GraphPerspective network) {
		int[] nodes = network.getNodeIndicesArray();
		int[] edges = network.getEdgeIndicesArray();
		restoreNodes(nodes);
		restoreEdges(edges);
	}

	/**
	  * Sets the selected state of all nodes in this CyNetwork to true
	  */
	public void selectAllNodes() {
		this.selectFilter.selectAllNodes();
	}

	/**
	 * Sets the selected state of all edges in this CyNetwork to true
	 */
	public void selectAllEdges() {
		this.selectFilter.selectAllEdges();
	}

	/**
	 * Sets the selected state of all nodes in this CyNetwork to false
	 */
	public void unselectAllNodes() {
		this.selectFilter.unselectAllNodes();
	}

	/**
	 * Sets the selected state of all edges in this CyNetwork to false
	 */
	public void unselectAllEdges() {
		this.selectFilter.unselectAllEdges();
	}

	/**
	 * Sets the selected state of a collection of nodes.
	 *
	 * @param nodes a Collection of Nodes
	 * @param selected_state the desired selection state for the nodes
	 */
	public void setSelectedNodeState(Collection nodes, boolean selected_state) {
		this.selectFilter.setSelectedNodes(nodes, selected_state);
	}

	/**
	 * Sets the selected state of a node.
	 *
	 * @param nodes a Node
	 * @param selected_state the desired selection state for the node
	 */
	public void setSelectedNodeState(Node node, boolean selected_state) {
		this.selectFilter.setSelected(node, selected_state);
	}

	/**
	 * Sets the selected state of a collection of edges.
	 *
	 * @param edges a Collection of Edges
	 * @param selected_state the desired selection state for the edges
	 */
	public void setSelectedEdgeState(Collection edges, boolean selected_state) {
		this.selectFilter.setSelectedEdges(edges, selected_state);
	}

	/**
	 * Sets the selected state of an edge.
	 *
	 * @param edges an Edge
	 * @param selected_state the desired selection state for the edge
	 */
	public void setSelectedEdgeState(Edge edge, boolean selected_state) {
		this.selectFilter.setSelected(edge, selected_state);
	}

	/**
	 * Returns the selected state of the given node.
	 *
	 * @param node the node
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected(Node node) {
		return this.selectFilter.isSelected(node);
	}

	/**
	 * Returns the selected state of the given edge.
	 *
	 * @param edge the edge
	 * @return true if selected, false otherwise
	 */
	public boolean isSelected(Edge edge) {
		return this.selectFilter.isSelected(edge);
	}

	/**
	 * Returns the set of selected nodes in this CyNetwork
	 *
	 * @return a Set of selected nodes
	 */
	public Set getSelectedNodes() {
		return this.selectFilter.getSelectedNodes();
	}

	/**
	 * Returns the set of selected edges in this CyNetwork
	 *
	 * @return a Set of selected edges
	 */
	public Set getSelectedEdges() {
		return this.selectFilter.getSelectedEdges();
	}

	/**
	 * Adds a listener for SelectEvents to this CyNetwork
	 *
	 * @param listener
	 */
	public void addSelectEventListener(SelectEventListener listener) {
		this.selectFilter.addSelectEventListener(listener);
	}

	/**
	 * Removes a listener for SelectEvents from this CyNetwork
	 * @param listener
	 */
	public void removeSelectEventListener(SelectEventListener listener) {
		this.selectFilter.removeSelectEventListener(listener);
	}

	/**
	 *
	 * @return SelectFilter
	 */
	public SelectFilter getSelectFilter() {
		return this.selectFilter;
	}

	//----------------------------------------//
	// Data Access Methods
	//----------------------------------------//

	//--------------------//
	// Member Data

	// get

	/**
	 * Return the requested Attribute for the given Node
	 * @param node the given CyNode
	 * @param attribute the name of the requested attribute
	 * @return the value for the give node, for the given attribute
	 */
	public Object getNodeAttributeValue(Node node, String attribute) {
		return getNodeAttributeValue(node.getRootGraphIndex(), attribute);
	}

	/**
	 * Return the requested Attribute for the given Node
	 */
	public Object getNodeAttributeValue(int node, String attribute) {
		final String canonName = getNode(node).getIdentifier();
		final CyAttributes attrs = Cytoscape.getNodeAttributes();
		final byte cyType = attrs.getType(attribute);

		if (cyType == CyAttributes.TYPE_BOOLEAN) {
			return attrs.getBooleanAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_FLOATING) {
			return attrs.getDoubleAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_INTEGER) {
			return attrs.getIntegerAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_STRING) {
			return attrs.getStringAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_LIST) {
			return attrs.getListAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_MAP) {
			return attrs.getMapAttribute(canonName, attribute);
		} else {
			return null;
		}
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public Object getEdgeAttributeValue(Edge edge, String attribute) {
		return getEdgeAttributeValue(edge.getRootGraphIndex(), attribute);
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public Object getEdgeAttributeValue(int edge, String attribute) {
		final String canonName = getEdge(edge).getIdentifier();
		final CyAttributes attrs = Cytoscape.getEdgeAttributes();
		final byte cyType = attrs.getType(attribute);

		if (cyType == CyAttributes.TYPE_BOOLEAN) {
			return attrs.getBooleanAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_FLOATING) {
			return attrs.getDoubleAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_INTEGER) {
			return attrs.getIntegerAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_STRING) {
			return attrs.getStringAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_LIST) {
			return attrs.getListAttribute(canonName, attribute);
		} else if (cyType == CyAttributes.TYPE_SIMPLE_MAP) {
			return attrs.getMapAttribute(canonName, attribute);
		} else {
			return null;
		}
	}

	/**
	 * Return all availble Attributes for the Nodes in this CyNetwork
	 */
	public String[] getNodeAttributesList() {
		return Cytoscape.getNodeAttributes().getAttributeNames();
	}

	/**
	 * Return all available Attributes for the given Nodes
	 */
	public String[] getNodeAttributesList(Node[] nodes) {
		return Cytoscape.getNodeAttributes().getAttributeNames();
	}

	/**
	 * Return all availble Attributes for the Edges in this CyNetwork
	 */
	public String[] getEdgeAttributesList() {
		return Cytoscape.getEdgeAttributes().getAttributeNames();
	}

	/**
	 * Return all available Attributes for the given Edges
	 */
	public String[] getNodeAttributesList(Edge[] edges) {
		return Cytoscape.getEdgeAttributes().getAttributeNames();
	}

	/**
	* Return the requested Attribute for the given Node
	* @param node the given CyNode
	* @param attribute the name of the requested attribute
	* @param value the value to be set
	* @return if it overwrites a previous value
	*/
	public boolean setNodeAttributeValue(Node node, String attribute, Object value) {
		return setNodeAttributeValue(node.getRootGraphIndex(), attribute, value);
	}

	/**
	 * Return the requested Attribute for the given Node
	 */
	public boolean setNodeAttributeValue(int node, String attribute, Object value) {
		final String canonName = getNode(node).getIdentifier();
		final CyAttributes attrs = Cytoscape.getNodeAttributes();

		if (value instanceof Boolean) {
			attrs.setAttribute(canonName, attribute, (Boolean) value);

			return true;
		} else if (value instanceof Integer) {
			attrs.setAttribute(canonName, attribute, (Integer) value);

			return true;
		} else if (value instanceof Double) {
			attrs.setAttribute(canonName, attribute, (Double) value);

			return true;
		} else if (value instanceof String) {
			attrs.setAttribute(canonName, attribute, (String) value);

			return true;
		} else if (value instanceof List) {
			attrs.setListAttribute(canonName, attribute, (List) value);

			return true;
		} else if (value instanceof Map) {
			attrs.setMapAttribute(canonName, attribute, (Map) value);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public boolean setEdgeAttributeValue(Edge edge, String attribute, Object value) {
		return setEdgeAttributeValue(edge.getRootGraphIndex(), attribute, value);
	}

	/**
	 * Return the requested Attribute for the given Edge
	 */
	public boolean setEdgeAttributeValue(int edge, String attribute, Object value) {
		final String canonName = getEdge(edge).getIdentifier();
		final CyAttributes attrs = Cytoscape.getEdgeAttributes();

		if (value instanceof Boolean) {
			attrs.setAttribute(canonName, attribute, (Boolean) value);

			return true;
		} else if (value instanceof Integer) {
			attrs.setAttribute(canonName, attribute, (Integer) value);

			return true;
		} else if (value instanceof Double) {
			attrs.setAttribute(canonName, attribute, (Double) value);

			return true;
		} else if (value instanceof String) {
			attrs.setAttribute(canonName, attribute, (String) value);

			return true;
		} else if (value instanceof List) {
			attrs.setListAttribute(canonName, attribute, (List) value);

			return true;
		} else if (value instanceof Map) {
			attrs.setMapAttribute(canonName, attribute, (Map) value);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes the attribute with the given name from node attributes
	 */
	public void deleteNodeAttribute(String attribute) {
		Cytoscape.getNodeAttributes().deleteAttribute(attribute);
	}

	/**
	 * Deletes the attribute with the given name from edge attributes
	 */
	public void deleteEdgeAttribute(String attribute) {
		Cytoscape.getEdgeAttributes().deleteAttribute(attribute);
	}

	//----------------------------------------//
	// Implements Network
	//----------------------------------------//

	//----------------------------------------//
	// Node and Edge creation/deletion
	//----------------------------------------//

	//--------------------//
	// Nodes

	/**
	 * This method will create a new node.
	 * @return the Cytoscape index of the created node
	 */
	public int createNode() {
		return restoreNode(Cytoscape.getRootGraph().createNode());
	}

	/**
	 * Add a node to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this node
	 */
	public int addNode(int cytoscape_node) {
		return restoreNode(cytoscape_node);
	}

	/**
	 * Add a node to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this node
	 */
	public Node addNode(Node cytoscape_node) {
		return (Node) restoreNode(cytoscape_node);
	}

	/**
	 * Adds a node to this Network, by looking it up via the
	 * given attribute and value
	 * @return the Network Index of this node
	 */
	public int addNode(String attribute, Object value) {
		return 0;
	}

	/**
	 * This will remove this node from the Network. However,
	 * unless forced, it will remain in Cytoscape to be possibly
	 * resused by another Network in the future.
	 * @param force force this node to be removed from all Networks
	 * @return true if the node is still present in Cytoscape
	 *          ( i.e. in another Network )
	 */
	public boolean removeNode(int node_index, boolean force) {
		hideNode(node_index);

		return true;
	}

	//--------------------//
	// Edges

	/**
	 * This method will create a new edge.
	 * @param source the source node
	 * @param target the target node
	 * @param directed weather the edge should be directed
	 * @return the Cytoscape index of the created edge
	 */
	public int createEdge(int source, int target, boolean directed) {
		return restoreEdge(Cytoscape.getRootGraph().createEdge(source, target, directed));
	}

	/**
	 * Add a edge to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this edge
	 */
	public int addEdge(int cytoscape_edge) {
		return restoreEdge(cytoscape_edge);
	}

	/**
	 * Add a edge to this Network that already exists in
	 * Cytoscape
	 * @return the Network Index of this edge
	 */
	public Edge addEdge(Edge cytoscape_edge) {
		return (Edge) restoreEdge(cytoscape_edge);
	}

	/**
	 * Adds a edge to this Network, by looking it up via the
	 * given attribute and value
	 * @return the Network Index of this edge
	 */
	public int addEdge(String attribute, Object value) {
		return 0;
	}

	/**
	 * This will remove this edge from the Network. However,
	 * unless forced, it will remain in Cytoscape to be possibly
	 * resused by another Network in the future.
	 * @param force force this edge to be removed from all Networks
	 * @return true if the edge is still present in Cytoscape
	 *          ( i.e. in another Network )
	 */
	public boolean removeEdge(int edge_index, boolean force) {
		hideEdge(edge_index);

		return true;
	}

}

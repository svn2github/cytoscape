
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

package fing.model.test;

import fing.model.FingRootGraphFactory;

import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *
 */
public final class AllGraphPerspectiveMethodsTest {
	// No constructor.
	private AllGraphPerspectiveMethodsTest() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static final void main(String[] args) {
		final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
		final GraphPerspective persp = root.createGraphPerspective((int[]) null, (int[]) null);
		int[] nodeInx = new int[5];

		for (int i = 0; i < (nodeInx.length - 1); i++)
			nodeInx[i] = root.createNode();

		int[] edgeInx = new int[7];
		edgeInx[0] = root.createEdge(nodeInx[0], nodeInx[1], true);
		edgeInx[1] = root.createEdge(nodeInx[1], nodeInx[2], false);
		edgeInx[2] = root.createEdge(nodeInx[2], nodeInx[0], true);
		edgeInx[3] = root.createEdge(nodeInx[2], nodeInx[2], true);
		edgeInx[4] = root.createEdge(nodeInx[1], nodeInx[1], false);
		edgeInx[5] = root.createEdge(nodeInx[1], nodeInx[0], true);
		edgeInx[6] = root.createEdge(nodeInx[3], nodeInx[2], true);
		nodeInx[nodeInx.length - 1] = root.createNode(null, new int[] { edgeInx[6], edgeInx[2] });

		for (int i = 0; i < nodeInx.length; i++)
			if (persp.restoreNode(nodeInx[i]) != nodeInx[i])
				throw new IllegalStateException("unable to restore node");

		for (int i = 0; i < edgeInx.length; i++)
			if (persp.restoreEdge(edgeInx[i]) != edgeInx[i])
				throw new IllegalStateException("unable to restore edge");

		RootGraph root2 = FingRootGraphFactory.instantiateRootGraph();
		root2.createNode();
		root2.createEdge(((Node) root2.nodesIterator().next()).getRootGraphIndex(),
		                 ((Node) root2.nodesIterator().next()).getRootGraphIndex());

		final Node root2Node = (Node) root2.nodesIterator().next();
		final Edge root2Edge = (Edge) root2.edgesIterator().next();
		final Node nodeNotInPersp = root.getNode(root.createNode());
		final Edge edge1NotInPersp = root.getEdge(root.createEdge(nodeInx[1], nodeInx[3], true));
		final Edge edge2NotInPersp = root.getEdge(root.createEdge(nodeInx[2],
		                                                          nodeNotInPersp.getRootGraphIndex(),
		                                                          false));
		int[] rootNodeInx = root.getNodeIndicesArray();
		int minNodeInx = 0;

		for (int i = 0; i < rootNodeInx.length; i++)
			minNodeInx = Math.min(minNodeInx, rootNodeInx[i]);

		int[] rootEdgeInx = root.getEdgeIndicesArray();
		int minEdgeInx = 0;

		for (int i = 0; i < rootEdgeInx.length; i++)
			minEdgeInx = Math.min(minEdgeInx, rootEdgeInx[i]);

		// Restore and hide nodes and edges.
		Node node2NotInPersp = root.getNode(root.createNode());
		Edge edge3NotInPersp = root.getEdge(root.createEdge(nodeInx[1], nodeInx[2], true));
		persp.restoreNode(node2NotInPersp);
		persp.restoreEdge(edge3NotInPersp);
		persp.hideNode(node2NotInPersp);
		persp.hideEdge(edge3NotInPersp);

		// Not testing GraphPerspectiveChangeListener methods.

		// clone().
		final GraphPerspective persp2 = (GraphPerspective) persp.clone();

		if ((persp2.getNodeCount() != persp.getNodeCount())
		    || (persp2.getEdgeCount() != persp.getEdgeCount()))
			throw new IllegalStateException("clone has different topology");

		int[] edgeInxArr = persp2.getEdgeIndicesArray();

		for (int i = 0; i < edgeInxArr.length; i++)
			if (persp2.hideEdge(edgeInxArr[i]) != edgeInxArr[i])
				throw new IllegalStateException("cannot hide edge in clone");

		if (persp2.getEdgeCount() != 0)
			throw new IllegalStateException("some edges in clone remaining");

		if (persp2.getNodeCount() != persp.getNodeCount())
			throw new IllegalStateException("node counts should still be the same");

		int[] nodeInxArr = persp2.getNodeIndicesArray();

		for (int i = 0; i < nodeInxArr.length; i++)
			if (persp2.hideNode(nodeInxArr[i]) != nodeInxArr[i])
				throw new IllegalStateException("cannot hide node in clone");

		if ((persp2.getNodeCount() != 0) || (persp2.getEdgeCount() != 0))
			throw new IllegalStateException("nodes or edges remaining");

		// getRootGraph().
		if ((persp.getRootGraph() != root) || (persp2.getRootGraph() != root))
			throw new IllegalStateException("incorrect RootGraph");

		// getNodeCount().
		if (persp.getNodeCount() != 5)
			throw new IllegalStateException("wrong number of nodes");

		// getEdgeCount().
		if (persp.getEdgeCount() != 7)
			throw new IllegalStateException("wrong number of edges");

		// nodesIterator().
		Iterator nodesIter = persp.nodesIterator();
		Node[] twoNodes = new Node[] { (Node) nodesIter.next(), (Node) nodesIter.next() };

		// nodesList().
		List nodesList = persp.nodesList();

		if (nodesList.size() != 5)
			throw new IllegalStateException("incorrect node List size");

		for (int i = 0; i < nodesList.size(); i++) {
			Node n = (Node) nodesList.get(i);
		}

		// getNodeIndicesArray().
		int[] nodeIndicesArray = persp.getNodeIndicesArray();

		if (nodeIndicesArray.length != nodesList.size())
			throw new IllegalStateException("size of nodes List and length of node indices array don't match");

		for (int j = 0; j < nodeInx.length; j++) {
			for (int i = 0;; i++) {
				if (nodeIndicesArray[i] == nodeInx[j])
					break;
			}
		}

		// edgesIterator().
		Iterator edgesIter = persp.edgesIterator();
		Edge[] twoEdges = new Edge[] { (Edge) edgesIter.next(), (Edge) edgesIter.next() };

		// edgesList().
		List edgesList = persp.edgesList();

		if (edgesList.size() != 7)
			throw new IllegalStateException("incorrect edge List size");

		for (int i = 0; i < edgesList.size(); i++) {
			Edge e = (Edge) edgesList.get(i);
		}

		// getEdgeIndicesArray().
		int[] edgeIndicesArray = persp.getEdgeIndicesArray();

		if (edgeIndicesArray.length != edgesList.size())
			throw new IllegalStateException("size of edges List and length of edge indices array don't match");

		for (int j = 0; j < edgeInx.length; j++) {
			for (int i = 0;; i++) {
				if (edgeIndicesArray[i] == edgeInx[j])
					break;
			}
		}

		// getEdgeIndicesArray(int, int, boolean, boolean).
		int[] connEdges;
		connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[0], false, true);

		if (connEdges.length != 2)
			throw new IllegalStateException("not 2 connecting edges");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[5])
				break;

		connEdges = persp.getEdgeIndicesArray(nodeInx[0], nodeInx[3], true, true);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[2], false, true);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = persp.getEdgeIndicesArray(nodeInx[2], nodeInx[1], true, false);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[1])
				break;

		connEdges = persp.getEdgeIndicesArray(nodeInx[2], nodeInx[2], false, false);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[3])
				break;

		connEdges = persp.getEdgeIndicesArray(nodeInx[2], nodeInx[2], true, true);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[3])
				break;

		connEdges = persp.getEdgeIndicesArray(nodeInx[2], nodeInx[3], false, false);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = persp.getEdgeIndicesArray(nodeInx[3], nodeInx[2], false, false);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[6])
				break;

		connEdges = persp.getEdgeIndicesArray(nodeInx[4], nodeInx[0], true, true);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = persp.getEdgeIndicesArray(99, 0, true, true);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		connEdges = persp.getEdgeIndicesArray(nodeInx[0], minNodeInx - 1, true, false);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		if ((persp.getEdgeIndicesArray(Integer.MAX_VALUE, Integer.MIN_VALUE, true, false) != null)
		    || (persp.getEdgeIndicesArray(Integer.MIN_VALUE, Integer.MAX_VALUE, false, false) != null)
		    || (persp.getEdgeIndicesArray(nodeInx[0], nodeNotInPersp.getRootGraphIndex(), false,
		                                  true) != null))
			throw new IllegalStateException("not null");

		// hide/restore mothods are tested elsewhere.

		// containsNode(Node).
		if (!persp.containsNode(twoNodes[1]))
			throw new IllegalStateException("GraphPersp does not contain node");

		if (persp.containsNode(root2Node))
			throw new IllegalStateException("GraphPersp contains node from other");

		if (persp.containsNode(nodeNotInPersp)
		    || !persp.getRootGraph().containsNode(nodeNotInPersp))
			throw new IllegalStateException("GraphPerspective contains node");

		// containsNode(Node, boolean).
		if (persp.containsNode(nodeNotInPersp, false))
			throw new IllegalStateException("GraphPersp should not contain node");

		if (!persp.containsNode(nodeNotInPersp, true))
			throw new IllegalStateException("GraphPersp should contain node recur.");

		// containsEdge(Edge).
		if (!persp.containsEdge(twoEdges[1]))
			throw new IllegalStateException("GraphPersp does not contain edge");

		if (persp.containsEdge(root2Edge))
			throw new IllegalStateException("GraphPersp contains edge from other");

		if (persp.containsEdge(edge1NotInPersp) || persp.containsEdge(edge2NotInPersp)
		    || !(persp.getRootGraph().containsEdge(edge1NotInPersp)
		    && persp.getRootGraph().containsEdge(edge2NotInPersp)))
			throw new IllegalStateException("GraphPerspective contains edge");

		// containsEdge(Edge, boolean).
		if (persp.containsEdge(edge1NotInPersp, false))
			throw new IllegalStateException("GraphPersp should not contains edge");

		if (!persp.containsEdge(edge1NotInPersp, true))
			throw new IllegalStateException("GraphPersp should contain edge recur.");

		// join(GraphPerspective).


		// neighborsList(Node).
		List neighList = persp.neighborsList(persp.getNode(nodeInx[0]));

		if (neighList.size() != 2)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = persp.neighborsList(persp.getNode(nodeInx[1]));

		if (neighList.size() != 3)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = persp.neighborsList(persp.getNode(nodeInx[2]));

		if (neighList.size() != 4)
			throw new IllegalStateException("wrong number of neighbors");

		int[] neighInx = new int[neighList.size()];

		for (int i = 0; i < neighList.size(); i++) {
			Node node = (Node) neighList.get(i);
			int nodeIndex = node.getRootGraphIndex();

			if (persp.getNode(nodeIndex) == null)
				throw new IllegalStateException("bad node in neighbors");

			if (nodeIndex == 0)
				throw new IllegalStateException("node index is 0");

			int index = -1;

			while (true) {
				if (neighInx[++index] != 0) {
					if (neighInx[index] == nodeIndex)
						throw new IllegalStateException("duplicate neighbor");
					else

						continue;
				} else {
					neighInx[index] = nodeIndex;

					break;
				}
			}
		}

		neighList = persp.neighborsList(persp.getNode(nodeInx[3]));

		if (neighList.size() != 1)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = persp.neighborsList(persp.getNode(nodeInx[4]));

		if (neighList.size() != 0)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = persp.neighborsList(root2Node);

		if (neighList != null)
			throw new IllegalStateException("neighbors List isn't null");

		neighList = persp.neighborsList(nodeNotInPersp);

		if (neighList != null)
			throw new IllegalStateException("neighbors List isn't null");

		// neighborsArray(int).
		neighInx = persp.neighborsArray(nodeInx[1]);

		if (neighInx.length != 3)
			throw new IllegalStateException("wrong number of neighbors");

		for (int i = 0;; i++)
			if (neighInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (neighInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (neighInx[i] == nodeInx[2])
				break;

		neighInx = persp.neighborsArray(nodeInx[4]);

		if (neighInx.length != 0)
			throw new IllegalStateException("wrong number of neighbors");

		if ((persp.neighborsArray(nodeNotInPersp.getRootGraphIndex()) != null)
		    || (persp.neighborsArray(Integer.MIN_VALUE) != null)
		    || (persp.neighborsArray(Integer.MAX_VALUE) != null)
		    || (persp.neighborsArray(0) != null) || (persp.neighborsArray(1) != null)
		    || (persp.neighborsArray(minNodeInx - 1) != null))
			throw new IllegalStateException("expected null");

		// isNeighbor(Node, Node).
		if (persp.isNeighbor(persp.getNode(nodeInx[4]), persp.getNode(nodeInx[4])))
			throw new IllegalStateException("node with no edges is its own neigh");

		if (persp.isNeighbor(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[1])))
			throw new IllegalStateException("nodes are neighbors");

		if (!persp.isNeighbor(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[0])))
			throw new IllegalStateException("nodes are not neighbors");

		if (persp.isNeighbor(root2Node, persp.getNode(nodeInx[2])))
			throw new IllegalStateException("nodes from another graph is neighbor");

		if (persp.isNeighbor(persp.getNode(nodeInx[0]), nodeNotInPersp))
			throw new IllegalStateException("neighbor with node not in GraphPersp");

		// isNeighbor(int, int).
		if (persp.isNeighbor(nodeInx[1], nodeInx[3]) || !root.isNeighbor(nodeInx[1], nodeInx[3]))
			throw new IllegalStateException("bad neighbors");

		if (!persp.isNeighbor(nodeInx[1], nodeInx[1]))
			throw new IllegalStateException("node with self edge not neigbhor");

		if (persp.isNeighbor(nodeInx[0], nodeInx[0]))
			throw new IllegalStateException("node with no self edge is neighbor");

		if (persp.isNeighbor(98, 99))
			throw new IllegalStateException("positive nodes are neighbors");

		if (!persp.isNeighbor(nodeInx[3], nodeInx[2]))
			throw new IllegalStateException("nodes are not neighbors");

		if (!persp.isNeighbor(nodeInx[1], nodeInx[2]))
			throw new IllegalStateException("nodes are not neighbors");

		if (persp.isNeighbor(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || persp.isNeighbor(Integer.MIN_VALUE, Integer.MAX_VALUE)
		    || persp.isNeighbor(minNodeInx - 1, nodeInx[0]) || persp.isNeighbor(0, 1))
			throw new IllegalStateException("extreme neighbors");

		// edgeExists(Node, Node).
		if (persp.edgeExists(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[1])))
			throw new IllegalStateException("edge exists");

		if (persp.edgeExists(persp.getNode(nodeInx[0]), root2Node))
			throw new IllegalStateException("edge exists with node of other graph");

		if (!persp.edgeExists(persp.getNode(nodeInx[0]), persp.getNode(nodeInx[1])))
			throw new IllegalStateException("edge does not exist");

		if (!persp.edgeExists(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[2])))
			throw new IllegalStateException("edge does not exist");

		if (persp.edgeExists(persp.getNode(nodeInx[2]), nodeNotInPersp)
		    || !root.edgeExists(persp.getNode(nodeInx[2]), nodeNotInPersp))
			throw new IllegalStateException("bad edgeExists");

		// edgeExists(int, int).
		if (persp.edgeExists(nodeInx[1], nodeInx[3]))
			throw new IllegalStateException("edge exists in RootGraph, not persp");

		if (persp.edgeExists(minNodeInx - 1, nodeInx[1]))
			throw new IllegalStateException("bad edgeExists()");

		if (persp.edgeExists(0, 0))
			throw new IllegalStateException("0 -> 0");

		if (persp.edgeExists(nodeInx[2], nodeInx[3]))
			throw new IllegalStateException("edge exists in opposite direction");

		if (persp.edgeExists(nodeInx[4], nodeInx[2]))
			throw new IllegalStateException("edge exists on node with no edge");

		if (persp.edgeExists(nodeInx[0], nodeInx[0]))
			throw new IllegalStateException("self-edge exists");

		if (persp.edgeExists(98, 99))
			throw new IllegalStateException("edge exists between positive nodes");

		if (!persp.edgeExists(nodeInx[1], nodeInx[1]))
			throw new IllegalStateException("self-edge does not exist [undirected]");

		if (!persp.edgeExists(nodeInx[2], nodeInx[2]))
			throw new IllegalStateException("self-edge does not exist [directed]");

		if (!persp.edgeExists(nodeInx[3], nodeInx[2]))
			throw new IllegalStateException("edge does not exist");

		if (!persp.edgeExists(nodeInx[1], nodeInx[0]))
			throw new IllegalStateException("edge does not exist");

		if (persp.edgeExists(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || persp.edgeExists(Integer.MIN_VALUE, Integer.MAX_VALUE))
			throw new IllegalStateException("MIN_VALUE and MAX_VALUE edge exists");

		// getEdgeCount(Node, Node, boolean).
		if (persp.getEdgeCount(persp.getNode(nodeInx[0]), persp.getNode(nodeInx[1]), true) != 1)
			throw new IllegalStateException("wrong number in edge count");

		if ((persp.getEdgeCount(persp.getNode(nodeInx[0]), root2Node, true) != -1)
		    || (persp.getEdgeCount(persp.getNode(nodeInx[1]), nodeNotInPersp, false) != -1))
			throw new IllegalStateException("edge count not -1");

		if (persp.getEdgeCount(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[1]), false) != 0)
			throw new IllegalStateException("edge count not 0");

		// getEdgeCount(int, int, boolean).
		if (persp.getEdgeCount(nodeInx[3], nodeInx[2], true) != 1)
			throw new IllegalStateException("edge count not 1");

		if (persp.getEdgeCount(nodeInx[2], nodeInx[3], true) != 0)
			throw new IllegalStateException("edge count not 0");

		if (persp.getEdgeCount(nodeInx[1], nodeInx[2], false) != 0)
			throw new IllegalStateException("edge count not 0");

		if (persp.getEdgeCount(nodeInx[1], nodeInx[1], true) != 1)
			throw new IllegalStateException("edge count not 1 for und. self edge");

		if (persp.getEdgeCount(99, 98, true) != -1)
			throw new IllegalStateException("edge count not -1");

		if ((persp.getEdgeCount(Integer.MAX_VALUE, Integer.MIN_VALUE, false) != -1)
		    || (persp.getEdgeCount(Integer.MIN_VALUE, Integer.MAX_VALUE, true) != -1)
		    || (persp.getEdgeCount(nodeInx[0], minNodeInx - 1, true) != -1))
			throw new IllegalStateException("edge count not -1");

		if ((persp.getEdgeCount(nodeInx[1], nodeInx[3], true) != 0)
		    || !(root.getEdgeCount(nodeInx[1], nodeInx[3], true) > 0))
			throw new IllegalStateException("bad edge count between 2 nodes");

		// edgesList(Node, Node).
		edgesList = persp.edgesList(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[1]));

		if (edgesList.size() != 0)
			throw new IllegalStateException("edges List not of size 0");

		edgesList = persp.edgesList(persp.getNode(nodeInx[0]), root2Node);

		if (edgesList != null)
			throw new IllegalStateException("not null");

		edgesList = persp.edgesList(persp.getNode(nodeInx[0]), persp.getNode(nodeInx[1]));

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[0])
			throw new IllegalStateException("wrong edge");

		edgesList = persp.edgesList(persp.getNode(nodeInx[2]), persp.getNode(nodeInx[1]));

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[1])
			throw new IllegalStateException("wrong edge");

		edgesList = persp.edgesList(persp.getNode(nodeInx[2]), persp.getNode(nodeInx[2]));

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[3])
			throw new IllegalStateException("wrong edge");

		edgesList = persp.edgesList(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[4]));

		if (edgesList.size() != 0)
			throw new IllegalStateException("edges List not of size 0");

		edgesList = persp.edgesList(persp.getNode(nodeInx[3]), nodeNotInPersp);

		if (edgesList != null)
			throw new IllegalStateException("expected null");

		// edgesList(int, int, boolean).
		edgesList = persp.edgesList(nodeInx[2], nodeInx[0], true);

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[2])
			throw new IllegalStateException("wrong edge");

		edgesList = persp.edgesList(nodeInx[4], nodeInx[3], false);

		if (edgesList.size() != 0)
			throw new IllegalStateException("edges List not of size 0");

		edgesList = persp.edgesList(99, minNodeInx - 1, true);

		if (edgesList != null)
			throw new IllegalStateException("not null");

		if ((persp.edgesList(Integer.MAX_VALUE, Integer.MIN_VALUE, true) != null)
		    || (persp.edgesList(Integer.MIN_VALUE, Integer.MAX_VALUE, false) != null))
			throw new IllegalStateException("not null");

		// getEdgeIndicesArray(int, int, boolean).
		connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[1], false);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[1], true);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[4])
				break;

		connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[0], true);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[5])
				break;

		connEdges = persp.getEdgeIndicesArray(minNodeInx - 1, nodeInx[2], true);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		connEdges = persp.getEdgeIndicesArray(nodeInx[1], 99, true);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		if ((persp.getEdgeIndicesArray(Integer.MAX_VALUE, Integer.MIN_VALUE, true) != null)
		    || (persp.getEdgeIndicesArray(Integer.MIN_VALUE, Integer.MAX_VALUE, false) != null))
			throw new IllegalStateException("not null");

		// getInDegree(Node).
		if ((persp.getInDegree(root2Node) != -1) || (persp.getInDegree(nodeNotInPersp) != -1))
			throw new IllegalStateException("not in degree -1 for other node");

		if (persp.getInDegree(persp.getNode(nodeInx[2])) != 3)
			throw new IllegalStateException("not in degree 3 for node");

		if (persp.getInDegree(persp.getNode(nodeInx[3])) != 0)
			throw new IllegalStateException("not in degree 0 for node");

		if (persp.getInDegree(persp.getNode(nodeInx[4])) != 0)
			throw new IllegalStateException("not in degree 0 for node");

		if (persp.getInDegree(persp.getNode(nodeInx[0])) != 2)
			throw new IllegalStateException("not in degree 2 for node");

		if (persp.getInDegree(persp.getNode(nodeInx[1])) != 3)
			throw new IllegalStateException("not in degree 3 for node");

		// getInDegree(int).
		if ((persp.getInDegree(minNodeInx - 1) != -1) || (persp.getInDegree(0) != -1)
		    || (persp.getInDegree(99) != -1))
			throw new IllegalStateException("not in degree -1");

		if (persp.getInDegree(nodeInx[1]) != 3)
			throw new IllegalStateException("not in degree 3");

		if (persp.getInDegree(nodeInx[0]) != 2)
			throw new IllegalStateException("not in degree 2");

		if (persp.getInDegree(Integer.MAX_VALUE) != -1)
			throw new IllegalStateException("not in degree -1");

		// getInDegree(Node, boolean).
		if ((persp.getInDegree(root2Node, true) != -1)
		    || (persp.getInDegree(nodeNotInPersp, false) != -1))
			throw new IllegalStateException("not in degree -1 for other node");

		if (persp.getInDegree(persp.getNode(nodeInx[1]), false) != 1)
			throw new IllegalStateException("not in degree 1");

		if (persp.getInDegree(persp.getNode(nodeInx[2]), false) != 2)
			throw new IllegalStateException("not in degree 2");

		if (persp.getInDegree(persp.getNode(nodeInx[2]), true) != 3)
			throw new IllegalStateException("not in degree 3");

		// getInDegree(int, boolean).
		if ((persp.getInDegree(minNodeInx - 1, false) != -1) || (persp.getInDegree(0, true) != -1)
		    || (persp.getInDegree(99, false) != -1))
			throw new IllegalStateException("not in degree -1");

		if (persp.getInDegree(nodeInx[0], true) != 2)
			throw new IllegalStateException("not in degree 2");

		if (persp.getInDegree(nodeInx[0], false) != 2)
			throw new IllegalStateException("not in degree 2");

		if (persp.getInDegree(nodeInx[4], true) != 0)
			throw new IllegalStateException("not in degree 0");

		if (persp.getInDegree(nodeInx[1], false) != 1)
			throw new IllegalStateException("not in degree 1");

		if (persp.getInDegree(Integer.MIN_VALUE, true) != -1)
			throw new IllegalStateException("not in degree -1");

		// getOutDegree(Node).
		if ((persp.getOutDegree(root2Node) != -1) || (persp.getOutDegree(nodeNotInPersp) != -1))
			throw new IllegalStateException("not out degree -1");

		if (persp.getOutDegree(persp.getNode(nodeInx[2])) != 3)
			throw new IllegalStateException("not out degree 3");

		if (persp.getOutDegree(persp.getNode(nodeInx[1])) != 3)
			throw new IllegalStateException("not out degree 3");

		if (persp.getOutDegree(persp.getNode(nodeInx[4])) != 0)
			throw new IllegalStateException("not out degree 0");

		// getOutDegree(int).
		if ((persp.getOutDegree(minNodeInx - 1) != -1) || (persp.getOutDegree(0) != -1)
		    || (persp.getOutDegree(101) != -1))
			throw new IllegalStateException("not out degree -1");

		if (persp.getOutDegree(nodeInx[3]) != 1)
			throw new IllegalStateException("not out degree 1");

		if (persp.getOutDegree(nodeInx[0]) != 1)
			throw new IllegalStateException("not out degree 1");

		if (persp.getOutDegree(Integer.MIN_VALUE) != -1)
			throw new IllegalStateException("not out degree -1");

		// getOutDegree(Node, boolean).
		if ((persp.getOutDegree(root2Node, false) != -1)
		    || (persp.getOutDegree(nodeNotInPersp, true) != -1))
			throw new IllegalStateException("not out degree -1");

		if (persp.getOutDegree(persp.getNode(nodeInx[1]), false) != 1)
			throw new IllegalStateException("not out degree 1");

		if (persp.getOutDegree(persp.getNode(nodeInx[1]), true) != 3)
			throw new IllegalStateException("not out degree 1");

		if ((persp.getOutDegree(persp.getNode(nodeInx[0]), false) != 1)
		    || (persp.getOutDegree(persp.getNode(nodeInx[0]), true) != 1))
			throw new IllegalStateException("not out degree 1");

		// getOutDegree(int, boolean).
		if ((persp.getOutDegree(minNodeInx - 3, false) != -1)
		    || (persp.getOutDegree(0, true) != -1) || (persp.getOutDegree(2, false) != -1))
			throw new IllegalStateException("not out degree -1");

		if ((persp.getOutDegree(nodeInx[2], false) != 2)
		    || (persp.getOutDegree(nodeInx[2], true) != 3))
			throw new IllegalStateException("not correct out degree");

		if ((persp.getOutDegree(nodeInx[3], false) != 1)
		    || (persp.getOutDegree(nodeInx[3], true) != 1))
			throw new IllegalStateException("not out degree 1");

		if ((persp.getOutDegree(nodeInx[4], true) != 0)
		    || (persp.getOutDegree(nodeInx[4], false) != 0))
			throw new IllegalStateException("not out degree 0");

		if (persp.getOutDegree(Integer.MAX_VALUE, false) != -1)
			throw new IllegalStateException("not out degree -1");

		// getDegree(Node).
		if ((persp.getDegree(root2Node) != -1) || (persp.getDegree(nodeNotInPersp) != -1))
			throw new IllegalStateException("not degree -1 for other node");

		if (persp.getDegree(persp.getNode(nodeInx[0])) != 3)
			throw new IllegalStateException("not degree 3");

		if (persp.getDegree(persp.getNode(nodeInx[1])) != 4)
			throw new IllegalStateException("not degree 4");

		// getDegree(int).
		if ((persp.getDegree(minNodeInx - 2) != -1) || (persp.getDegree(0) != -1)
		    || (persp.getDegree(13) != -1))
			throw new IllegalStateException("not degree -1");

		if (persp.getDegree(nodeInx[2]) != 4)
			throw new IllegalStateException("not degree 4");

		if (persp.getDegree(nodeInx[3]) != 1)
			throw new IllegalStateException("not degree 1");

		if (persp.getDegree(nodeInx[4]) != 0)
			throw new IllegalStateException("not degree 0");

		if ((persp.getDegree(Integer.MAX_VALUE) != -1)
		    || (persp.getDegree(Integer.MIN_VALUE) != -1))
			throw new IllegalStateException("not degree -1");

		// getIndex(Node).
		if ((persp.getIndex(root2Node) != 0) || (persp.getIndex(nodeNotInPersp) != 0))
			throw new IllegalStateException("index not 0");

		if (persp.getIndex(persp.getNode(nodeInx[2])) != nodeInx[2])
			throw new IllegalStateException("wrong node index");

		// getRootGraphNodeIndex(int).
		if ((persp.getRootGraphNodeIndex(nodeInx[3]) != nodeInx[3])
		    || (persp.getRootGraphNodeIndex(nodeNotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getRootGraphNodeIndex(minNodeInx - 1) != 0)
		    || (persp.getRootGraphNodeIndex(Integer.MIN_VALUE) != 0)
		    || (persp.getRootGraphNodeIndex(1) != 0))
			throw new IllegalStateException("bad getRootGraphNodeIndex(int)");

		// getNode(int).
		if ((persp.getNode(minNodeInx - 1) != null) || (persp.getNode(0) != null)
		    || (persp.getNode(23) != null)
		    || (persp.getNode(nodeNotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("not null");

		if ((persp.getNode(Integer.MAX_VALUE) != null)
		    || (persp.getNode(Integer.MIN_VALUE) != null))
			throw new IllegalStateException("not null");

		// getIndex(Edge).
		if ((persp.getIndex(persp.getEdge(edgeInx[0])) != edgeInx[0])
		    || (persp.getIndex(persp.getEdge(edgeInx[2])) != edgeInx[2]))
			throw new IllegalStateException("wrong getIndex(Edge)");

		if ((persp.getIndex(root2Edge) != 0) || (persp.getIndex(edge1NotInPersp) != 0))
			throw new IllegalStateException("getIndex(Edge) should have been 0");

		// getRootGraphEdgeIndex(int).
		if ((persp.getRootGraphEdgeIndex(edgeInx[4]) != edgeInx[4])
		    || (persp.getRootGraphEdgeIndex(edge2NotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getRootGraphEdgeIndex(minEdgeInx - 1) != 0)
		    || (persp.getRootGraphEdgeIndex(Integer.MAX_VALUE) != 0)
		    || (persp.getRootGraphEdgeIndex(1) != 0))
			throw new IllegalStateException("bad getRootGraphEdgeIndex(int)");

		// getEdge(int).
		if ((persp.getEdge(minEdgeInx - 1) != null) || (persp.getEdge(0) != null)
		    || (persp.getEdge(23) != null)
		    || (persp.getEdge(edge1NotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("not null in getEdge(int)");

		if ((persp.getEdge(Integer.MAX_VALUE) != null)
		    || (persp.getEdge(Integer.MIN_VALUE) != null))
			throw new IllegalStateException("not null in getEdge(int)");

		// getEdgeSourceIndex(int).
		if ((persp.getEdgeSourceIndex(edgeInx[0]) != nodeInx[0])
		    || (persp.getEdgeSourceIndex(edgeInx[1]) != nodeInx[1])
		    || (persp.getEdgeSourceIndex(edgeInx[2]) != nodeInx[2])
		    || (persp.getEdgeSourceIndex(edgeInx[3]) != nodeInx[2])
		    || (persp.getEdgeSourceIndex(edgeInx[4]) != nodeInx[1])
		    || (persp.getEdgeSourceIndex(edgeInx[5]) != nodeInx[1])
		    || (persp.getEdgeSourceIndex(edgeInx[6]) != nodeInx[3]))
			throw new IllegalStateException("wrong edge source");

		if ((persp.getEdgeSourceIndex(minEdgeInx - 1) != 0) || (persp.getEdgeSourceIndex(0) != 0)
		    || (persp.getEdgeSourceIndex(23) != 0)
		    || (persp.getEdgeSourceIndex(edge1NotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getEdgeSourceIndex(edge2NotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getEdgeSourceIndex(Integer.MAX_VALUE) != 0)
		    || (persp.getEdgeSourceIndex(Integer.MIN_VALUE) != 0))
			throw new IllegalStateException("should have returned 0 as source node");

		// getEdgeTargetIndex(int).
		if ((persp.getEdgeTargetIndex(edgeInx[0]) != nodeInx[1])
		    || (persp.getEdgeTargetIndex(edgeInx[1]) != nodeInx[2])
		    || (persp.getEdgeTargetIndex(edgeInx[2]) != nodeInx[0])
		    || (persp.getEdgeTargetIndex(edgeInx[3]) != nodeInx[2])
		    || (persp.getEdgeTargetIndex(edgeInx[4]) != nodeInx[1])
		    || (persp.getEdgeTargetIndex(edgeInx[5]) != nodeInx[0])
		    || (persp.getEdgeTargetIndex(edgeInx[6]) != nodeInx[2]))
			throw new IllegalStateException("wrong edge target");

		if ((persp.getEdgeTargetIndex(minEdgeInx - 1) != 0) || (persp.getEdgeTargetIndex(0) != 0)
		    || (persp.getEdgeTargetIndex(22) != 0)
		    || (persp.getEdgeTargetIndex(edge1NotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getEdgeTargetIndex(edge2NotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getEdgeTargetIndex(Integer.MAX_VALUE) != 0)
		    || (persp.getEdgeTargetIndex(Integer.MIN_VALUE) != 0))
			throw new IllegalStateException("should have returned 0 as target node");

		// isEdgeDirected(int).
		if (persp.isEdgeDirected(edgeInx[1]) || persp.isEdgeDirected(edgeInx[4]))
			throw new IllegalStateException("edge is not directed");

		if (!(persp.isEdgeDirected(edgeInx[0]) && persp.isEdgeDirected(edgeInx[2])
		    && persp.isEdgeDirected(edgeInx[3]) && persp.isEdgeDirected(edgeInx[5])
		    && persp.isEdgeDirected(edgeInx[6])))
			throw new IllegalStateException("edge is directed");

		// getAdjacentEdgesList(Node, boolean, boolean, boolean).
		List adjacentEdges = persp.getAdjacentEdgesList(persp.getNode(nodeInx[2]), true, true, true);

		if (adjacentEdges.size() != 4)
			throw new IllegalStateException("expected 4 adjacent edges");

		for (int i = 0;; i++)
			if (((Edge) adjacentEdges.get(i)).getRootGraphIndex() == edgeInx[1])
				break;

		for (int i = 0;; i++)
			if (((Edge) adjacentEdges.get(i)).getRootGraphIndex() == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (((Edge) adjacentEdges.get(i)).getRootGraphIndex() == edgeInx[3])
				break;

		for (int i = 0;; i++)
			if (((Edge) adjacentEdges.get(i)).getRootGraphIndex() == edgeInx[6])
				break;

		if ((persp.getAdjacentEdgesList(root2Node, false, false, false) != null)
		    || (persp.getAdjacentEdgesList(nodeNotInPersp, true, false, true) != null))
			throw new IllegalStateException("expected null adjacent edges list");

		adjacentEdges = persp.getAdjacentEdgesList(persp.getNode(nodeInx[2]), false, false, false);

		if (adjacentEdges.size() != 0)
			throw new IllegalStateException("should have been 0 adjacent edges");

		// getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean).
		int[] adjacentEdgeInx = persp.getAdjacentEdgeIndicesArray(nodeInx[0], false, true, false);

		if (adjacentEdgeInx.length != 2)
			throw new IllegalStateException("should have been 2 adjacent edges");

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[5])
				break;

		adjacentEdgeInx = persp.getAdjacentEdgeIndicesArray(nodeInx[1], true, true, false);

		if (adjacentEdgeInx.length != 3)
			throw new IllegalStateException("should have been 3 adjacent edges");

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[1])
				break;

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[4])
				break;

		adjacentEdgeInx = persp.getAdjacentEdgeIndicesArray(nodeInx[3], true, false, true);

		if (adjacentEdgeInx.length != 1)
			throw new IllegalStateException("should have been 1 adjacent edge");

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[6])
				break;

		adjacentEdgeInx = persp.getAdjacentEdgeIndicesArray(nodeInx[4], false, false, true);

		if (adjacentEdgeInx.length != 0)
			throw new IllegalStateException("should have been 0 adjacent edges");

		adjacentEdgeInx = persp.getAdjacentEdgeIndicesArray(nodeInx[0], true, false, false);

		if (adjacentEdgeInx.length != 0)
			throw new IllegalStateException("should have been 0 adjacent edges");

		adjacentEdgeInx = persp.getAdjacentEdgeIndicesArray(nodeInx[1], false, false, true);

		if (adjacentEdgeInx.length != 1)
			throw new IllegalStateException("should have been 1 adjacent edge");

		for (int i = 0;; i++)
			if (adjacentEdgeInx[i] == edgeInx[5])
				break;

		if ((persp.getAdjacentEdgeIndicesArray(0, true, false, true) != null)
		    || (persp.getAdjacentEdgeIndicesArray(Integer.MIN_VALUE, true, true, true) != null)
		    || (persp.getAdjacentEdgeIndicesArray(nodeNotInPersp.getRootGraphIndex(), false, true,
		                                          true) != null)
		    || (persp.getAdjacentEdgeIndicesArray(minNodeInx - 1, false, true, true) != null))
			throw new IllegalStateException("expected null adjacent edge inx arr");

		// getConnectingEdges(List).
		ArrayList nodeInputList = new ArrayList(3);
		nodeInputList.add(0, persp.getNode(nodeInx[0]));
		nodeInputList.add(1, persp.getNode(nodeInx[3]));
		nodeInputList.add(2, persp.getNode(nodeInx[2]));

		List connectingEdgesList = persp.getConnectingEdges(nodeInputList);

		if (connectingEdgesList.size() != 3)
			throw new IllegalStateException("expected 3 connecting edges");

		for (int i = 0;; i++)
			if (((Edge) connectingEdgesList.get(i)).getRootGraphIndex() == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (((Edge) connectingEdgesList.get(i)).getRootGraphIndex() == edgeInx[3])
				break;

		for (int i = 0;; i++)
			if (((Edge) connectingEdgesList.get(i)).getRootGraphIndex() == edgeInx[6])
				break;

		nodeInputList = new ArrayList(3);
		nodeInputList.add(0, persp.getNode(nodeInx[2]));
		nodeInputList.add(1, persp.getNode(nodeInx[0]));
		nodeInputList.add(2, persp.getNode(nodeInx[1]));
		connectingEdgesList = persp.getConnectingEdges(nodeInputList);

		if (connectingEdgesList.size() != 6)
			throw new IllegalStateException("expected 6 connecting nodes");

		for (int i = 0; i < connectingEdgesList.size(); i++)
			if (((Edge) connectingEdgesList.get(i)).getRootGraphIndex() == edgeInx[6])
				throw new IllegalStateException("wrong connecting edge");

		nodeInputList = new ArrayList(2);
		nodeInputList.add(0, persp.getNode(nodeInx[0]));
		nodeInputList.add(1, nodeNotInPersp);

		if (persp.getConnectingEdges(nodeInputList) != null)
			throw new IllegalStateException("expected null connecting edges");

		nodeInputList = new ArrayList(2);
		nodeInputList.add(0, root2Node);
		nodeInputList.add(1, persp.getNode(nodeInx[1]));

		if (persp.getConnectingEdges(nodeInputList) != null)
			throw new IllegalStateException("expected null connecting edges");

		// getConnectingEdgeIndicesArray(int[]).
		int[] nodeInputInxArr = new int[4];
		nodeInputInxArr[0] = nodeInx[0];
		nodeInputInxArr[1] = nodeInx[2];
		nodeInputInxArr[2] = nodeInx[3];
		nodeInputInxArr[3] = nodeInx[4];

		int[] connectingEdgeInx = persp.getConnectingEdgeIndicesArray(nodeInputInxArr);

		if (connectingEdgeInx.length != 3)
			throw new IllegalStateException("expected 3 connecting edges");

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[3])
				break;

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[6])
				break;

		nodeInputInxArr = new int[2];
		nodeInputInxArr[0] = nodeInx[0];
		nodeInputInxArr[1] = nodeInx[1];
		connectingEdgeInx = persp.getConnectingEdgeIndicesArray(nodeInputInxArr);

		if (connectingEdgeInx.length != 3)
			throw new IllegalStateException("expected 3 connecting edges");

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[4])
				break;

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[5])
				break;

		nodeInputInxArr = new int[2];
		nodeInputInxArr[0] = nodeInx[4];
		nodeInputInxArr[1] = nodeInx[2];
		connectingEdgeInx = persp.getConnectingEdgeIndicesArray(nodeInputInxArr);

		if (connectingEdgeInx.length != 1)
			throw new IllegalStateException("expected 1 connecting edge");

		for (int i = 0;; i++)
			if (connectingEdgeInx[i] == edgeInx[3])
				break;

		nodeInputInxArr = new int[3];
		nodeInputInxArr[0] = nodeInx[4];
		nodeInputInxArr[1] = nodeInx[3];
		nodeInputInxArr[2] = nodeInx[0];
		connectingEdgeInx = persp.getConnectingEdgeIndicesArray(nodeInputInxArr);

		if (connectingEdgeInx.length != 0)
			throw new IllegalStateException("expected no connecting edges");

		if ((persp.getConnectingEdgeIndicesArray(new int[100]) != null)
		    || (persp.getConnectingEdgeIndicesArray(new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE }) != null)
		    || (persp.getConnectingEdgeIndicesArray(new int[] { minNodeInx - 1, nodeInx[1] }) != null)
		    || (persp.getConnectingEdgeIndicesArray(new int[] {
		                                                nodeInx[1],
		                                                nodeNotInPersp.getRootGraphIndex()
		                                            }) != null))
			throw new IllegalStateException("expected null connecting edge inx");

	}
}

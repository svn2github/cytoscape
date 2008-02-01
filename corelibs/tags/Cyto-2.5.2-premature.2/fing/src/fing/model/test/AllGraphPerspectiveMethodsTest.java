
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

		if (!(root.addNodeMetaChild(nodeInx[0], nodeInx[1])
		    && root.addNodeMetaChild(nodeInx[0], nodeInx[4])
		    && root.addNodeMetaChild(nodeInx[3], nodeInx[1])
		    && root.addNodeMetaChild(nodeInx[4], nodeInx[4])
		    && root.addEdgeMetaChild(nodeInx[3], edgeInx[6])
		    && root.addEdgeMetaChild(nodeInx[3], edgeInx[0])
		    && root.addEdgeMetaChild(nodeInx[0], edgeInx[4])))
			throw new IllegalStateException("unable to create meta relationship");

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
		root.addNodeMetaChild(nodeInx[2], nodeNotInPersp.getRootGraphIndex());
		root.addNodeMetaChild(nodeNotInPersp.getRootGraphIndex(), nodeInx[4]);
		root.addEdgeMetaChild(nodeInx[3], edge1NotInPersp.getRootGraphIndex());

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

		// createGraphPerspective(Node[], Edge[]).

		// createGraphPerspective(int[], int[]).

		// createGraphPerspective(Filter).

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

		// getNodeIndex(int).
		if ((persp.getNodeIndex(nodeInx[1]) != nodeInx[1])
		    || (persp.getNodeIndex(nodeNotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getNodeIndex(minNodeInx - 1) != 0)
		    || (persp.getNodeIndex(Integer.MAX_VALUE) != 0) || (persp.getNodeIndex(1) != 0))
			throw new IllegalStateException("bad getNodeIndex(int)");

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

		// getEdgeIndex(int).
		if ((persp.getEdgeIndex(edgeInx[3]) != edgeInx[3])
		    || (persp.getEdgeIndex(edge1NotInPersp.getRootGraphIndex()) != 0)
		    || (persp.getEdgeIndex(minEdgeInx - 1) != 0)
		    || (persp.getEdgeIndex(Integer.MIN_VALUE) != 0) || (persp.getEdgeIndex(1) != 0))
			throw new IllegalStateException("bad getEdgeIndex(int)");

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

		// isMetaParent(Node, Node).
		if (!(persp.isMetaParent(persp.getNode(nodeInx[4]), persp.getNode(nodeInx[4]))
		    && persp.isMetaParent(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[0]))))
			throw new IllegalStateException("expected meta-relationship");

		if (persp.isMetaParent(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[2]))
		    || persp.isMetaParent(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[4])))
			throw new IllegalStateException("did not expect meta-relationship");

		if (persp.isMetaParent(root2Node, persp.getNode(nodeInx[3]))
		    || persp.isMetaParent(persp.getNode(nodeInx[0]), root2Node)
		    || persp.isMetaParent(nodeNotInPersp, persp.getNode(nodeInx[2]))
		    || persp.isMetaParent(persp.getNode(nodeInx[4]), nodeNotInPersp))
			throw new IllegalStateException("meta-relationship only in RootGraph");

		// isNodeMetaParent(int, int).
		if (!(persp.isNodeMetaParent(nodeInx[2], nodeInx[3])
		    && persp.isNodeMetaParent(nodeInx[2], nodeInx[4])
		    && persp.isNodeMetaParent(nodeInx[3], nodeInx[3])))
			throw new IllegalStateException("expected meta-relationship");

		if (persp.isNodeMetaParent(nodeInx[4], nodeInx[3])
		    || persp.isNodeMetaParent(nodeInx[1], nodeInx[1]))
			throw new IllegalStateException("did not expect meta-relationship");

		if (persp.isNodeMetaParent(0, nodeInx[1])
		    || persp.isNodeMetaParent(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || persp.isNodeMetaParent(Integer.MIN_VALUE, Integer.MAX_VALUE)
		    || persp.isNodeMetaParent(1, 2) || persp.isNodeMetaParent(minNodeInx - 1, nodeInx[1])
		    || persp.isNodeMetaParent(nodeNotInPersp.getRootGraphIndex(), nodeInx[4])
		    || persp.isNodeMetaParent(nodeInx[2], nodeNotInPersp.getRootGraphIndex()))
			throw new IllegalStateException("meta-relationship totally invalid");

		// metaParentsList(Node).
		List parentsList = persp.metaParentsList(persp.getNode(nodeInx[1]));

		if (parentsList.size() != 2)
			throw new IllegalStateException("wrong number of parents");

		for (int i = 0;; i++)
			if (((Node) parentsList.get(i)).getRootGraphIndex() == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (((Node) parentsList.get(i)).getRootGraphIndex() == nodeInx[3])
				break;

		if ((persp.metaParentsList(nodeNotInPersp) != null)
		    || (persp.metaParentsList(root2Node) != null))
			throw new IllegalStateException("expected null parents list");

		// nodeMetaParentsList(int).
		parentsList = persp.nodeMetaParentsList(nodeInx[4]);

		if (parentsList.size() != 2)
			throw new IllegalStateException("wrong number of parents");

		for (int i = 0;; i++)
			if (((Node) parentsList.get(i)).getRootGraphIndex() == nodeInx[4])
				break;

		for (int i = 0;; i++)
			if (((Node) parentsList.get(i)).getRootGraphIndex() == nodeInx[0])
				break;

		if ((persp.nodeMetaParentsList(0) != null) || (persp.nodeMetaParentsList(1) != null)
		    || (persp.nodeMetaParentsList(Integer.MAX_VALUE) != null)
		    || (persp.nodeMetaParentsList(Integer.MIN_VALUE) != null)
		    || (persp.nodeMetaParentsList(minNodeInx - 1) != null)
		    || (persp.nodeMetaParentsList(nodeNotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("expected null meta parents list");

		// getNodeMetaParentIndicesArray(int).
		int[] parentInx = persp.getNodeMetaParentIndicesArray(nodeInx[3]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parents");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		parentInx = persp.getNodeMetaParentIndicesArray(nodeInx[0]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parents");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		parentInx = persp.getNodeMetaParentIndicesArray(nodeInx[2]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parents");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		if ((persp.getNodeMetaParentIndicesArray(0) != null)
		    || (persp.getNodeMetaParentIndicesArray(1) != null)
		    || (persp.getNodeMetaParentIndicesArray(minNodeInx - 1) != null)
		    || (persp.getNodeMetaParentIndicesArray(Integer.MIN_VALUE) != null)
		    || (persp.getNodeMetaParentIndicesArray(Integer.MAX_VALUE) != null)
		    || (persp.getNodeMetaParentIndicesArray(nodeNotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("expected null for meta-parents");

		// isMetaChild(Node, Node).
		if (!(persp.isMetaChild(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[0]))
		    && persp.isMetaChild(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[2]))))
			throw new IllegalStateException("expected meta-child relationship");

		if (persp.isMetaChild(persp.getNode(nodeInx[1]), persp.getNode(nodeInx[4]))
		    || persp.isMetaChild(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[4])))
			throw new IllegalStateException("did not expect meta-child");

		if (persp.isMetaChild(nodeNotInPersp, persp.getNode(nodeInx[2]))
		    || persp.isMetaChild(persp.getNode(nodeInx[2]), nodeNotInPersp)
		    || persp.isMetaChild(persp.getNode(nodeInx[4]), nodeNotInPersp)
		    || persp.isMetaChild(nodeNotInPersp, persp.getNode(nodeInx[4]))
		    || persp.isMetaChild(root2Node, persp.getNode(nodeInx[0]))
		    || persp.isMetaChild(persp.getNode(nodeInx[0]), root2Node))
			throw new IllegalStateException("totally wrong meta-relationship");

		// isNodeMetaChild(int, int).
		if (!(persp.isNodeMetaChild(nodeInx[0], nodeInx[4])
		    && persp.isNodeMetaChild(nodeInx[4], nodeInx[2])
		    && persp.isNodeMetaChild(nodeInx[4], nodeInx[4])
		    && persp.isNodeMetaChild(nodeInx[4], nodeInx[3])
		    && persp.isNodeMetaChild(nodeInx[3], nodeInx[0])))
			throw new IllegalStateException("expected meta-relationship");

		if (persp.isNodeMetaChild(nodeInx[2], nodeInx[2])
		    || persp.isNodeMetaChild(nodeInx[1], nodeInx[3])
		    || persp.isNodeMetaChild(nodeInx[0], nodeInx[3])
		    || persp.isNodeMetaChild(nodeInx[3], nodeInx[4]))
			throw new IllegalStateException("unexpected meta-relationship");

		if (persp.isNodeMetaChild(0, 1)
		    || persp.isNodeMetaChild(nodeNotInPersp.getRootGraphIndex(), nodeInx[2])
		    || persp.isNodeMetaChild(nodeInx[2], nodeNotInPersp.getRootGraphIndex())
		    || persp.isNodeMetaChild(Integer.MIN_VALUE, nodeInx[3]))
			throw new IllegalStateException("totally wrong meta-relationship");

		// nodeMetaChildrenList(Node).
		List nodeChildrenList = persp.nodeMetaChildrenList(persp.getNode(nodeInx[4]));

		if (nodeChildrenList.size() != 4)
			throw new IllegalStateException("wrong number of children nodes");

		for (int i = 0;; i++)
			if (((Node) nodeChildrenList.get(i)).getRootGraphIndex() == nodeInx[4])
				break;

		for (int i = 0;; i++)
			if (((Node) nodeChildrenList.get(i)).getRootGraphIndex() == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (((Node) nodeChildrenList.get(i)).getRootGraphIndex() == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (((Node) nodeChildrenList.get(i)).getRootGraphIndex() == nodeInx[3])
				break;

		if ((persp.nodeMetaChildrenList(root2Node) != null)
		    || (persp.nodeMetaChildrenList(nodeNotInPersp) != null))
			throw new IllegalStateException("expected null node children list");

		// nodeMetaChildrenList(int).
		nodeChildrenList = persp.nodeMetaChildrenList(nodeInx[0]);

		if (nodeChildrenList.size() != 2)
			throw new IllegalStateException("expected 2 children nodes");

		for (int i = 0;; i++)
			if (((Node) nodeChildrenList.get(i)).getRootGraphIndex() == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (((Node) nodeChildrenList.get(i)).getRootGraphIndex() == nodeInx[4])
				break;

		nodeChildrenList = persp.nodeMetaChildrenList(nodeInx[1]);

		if (nodeChildrenList.size() != 0)
			throw new IllegalStateException("expected 0 children nodes");

		if ((persp.nodeMetaChildrenList(0) != null) || (persp.nodeMetaChildrenList(1) != null)
		    || (persp.nodeMetaChildrenList(minNodeInx - 1) != null)
		    || (persp.nodeMetaChildrenList(nodeNotInPersp.getRootGraphIndex()) != null)
		    || (persp.nodeMetaChildrenList(Integer.MIN_VALUE) != null)
		    || (persp.nodeMetaChildrenList(Integer.MAX_VALUE) != null)
		    || (persp.nodeMetaChildrenList(Integer.MIN_VALUE + 1) != null))
			throw new IllegalStateException("expected null node children list");

		// getNodeMetaChildIndicesArray(int).
		int[] childNodeInx = persp.getNodeMetaChildIndicesArray(nodeInx[2]);

		if (childNodeInx.length != 0)
			throw new IllegalStateException("expected 0 child nodes");

		childNodeInx = persp.getNodeMetaChildIndicesArray(nodeInx[3]);

		if (childNodeInx.length != 4)
			throw new IllegalStateException("expected 4 child nodes");

		for (int i = 0;; i++)
			if (childNodeInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (childNodeInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (childNodeInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (childNodeInx[i] == nodeInx[2])
				break;

		if ((persp.getNodeMetaChildIndicesArray(0) != null)
		    || (persp.getNodeMetaChildIndicesArray(23) != null)
		    || (persp.getNodeMetaChildIndicesArray(minNodeInx - 1) != null)
		    || (persp.getNodeMetaChildIndicesArray(Integer.MAX_VALUE) != null)
		    || (persp.getNodeMetaChildIndicesArray(Integer.MIN_VALUE) != null)
		    || (persp.getNodeMetaChildIndicesArray(nodeNotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("expected null children nodes");

		// isMetaParent(Edge, Node).
		if (!(persp.isMetaParent(persp.getEdge(edgeInx[6]), persp.getNode(nodeInx[3]))
		    && persp.isMetaParent(persp.getEdge(edgeInx[2]), persp.getNode(nodeInx[4]))
		    && persp.isMetaParent(persp.getEdge(edgeInx[0]), persp.getNode(nodeInx[3]))))
			throw new IllegalStateException("expected meta-edge relationship");

		if (persp.isMetaParent(persp.getEdge(edgeInx[6]), persp.getNode(nodeInx[0]))
		    || persp.isMetaParent(persp.getEdge(edgeInx[1]), persp.getNode(nodeInx[4]))
		    || persp.isMetaParent(persp.getEdge(edgeInx[3]), persp.getNode(nodeInx[1])))
			throw new IllegalStateException("unexpected meta-edge relationship");

		if (persp.isMetaParent(edge1NotInPersp, persp.getNode(nodeInx[3]))
		    || persp.isMetaParent(edge2NotInPersp, persp.getNode(nodeInx[4]))
		    || persp.isMetaParent(root2Edge, persp.getNode(nodeInx[0]))
		    || persp.isMetaParent(persp.getEdge(edgeInx[1]), nodeNotInPersp))
			throw new IllegalStateException("totally wrong meta-edge relationship");

		// isEdgeMetaParent(int, int).
		if (!(persp.isEdgeMetaParent(edgeInx[4], nodeInx[0])
		    && persp.isEdgeMetaParent(edgeInx[6], nodeInx[4])))
			throw new IllegalStateException("missing edge meta-relationship");

		if (persp.isEdgeMetaParent(edgeInx[4], nodeInx[1])
		    || persp.isEdgeMetaParent(edgeInx[2], nodeInx[0])
		    || persp.isEdgeMetaParent(edgeInx[1], nodeInx[4]))
			throw new IllegalStateException("unexpected edge meta-relationship");

		if (persp.isEdgeMetaParent(0, 1)
		    || persp.isEdgeMetaParent(edge1NotInPersp.getRootGraphIndex(), nodeInx[0])
		    || persp.isEdgeMetaParent(Integer.MIN_VALUE, Integer.MAX_VALUE))
			throw new IllegalStateException("totally wrong edge meta-relationship");

		// metaParentsList(Edge).
		parentsList = persp.metaParentsList(persp.getEdge(edgeInx[5]));

		if (parentsList.size() != 0)
			throw new IllegalStateException("edge has no parents");

		parentsList = persp.metaParentsList(persp.getEdge(edgeInx[2]));

		if (parentsList.size() != 1)
			throw new IllegalStateException("wrong number of edge parents");

		for (int i = 0;; i++)
			if (((Node) parentsList.get(i)).getRootGraphIndex() == nodeInx[4])
				break;

		if ((persp.metaParentsList(root2Edge) != null)
		    || (persp.metaParentsList(edge1NotInPersp) != null)
		    || (persp.metaParentsList(edge2NotInPersp) != null))
			throw new IllegalStateException("expected null edge parents list");

		// edgeMetaParentsList(int).
		parentsList = persp.edgeMetaParentsList(edgeInx[0]);

		if (parentsList.size() != 1)
			throw new IllegalStateException("wrong number of edge parents");

		for (int i = 0;; i++)
			if (((Node) parentsList.get(i)).getRootGraphIndex() == nodeInx[3])
				break;

		parentsList = persp.edgeMetaParentsList(edgeInx[1]);

		if (parentsList.size() != 0)
			throw new IllegalStateException("expected 0 edge parents");

		if ((persp.edgeMetaParentsList(0) != null) || (persp.edgeMetaParentsList(1) != null)
		    || (persp.edgeMetaParentsList(minEdgeInx - 1) != null)
		    || (persp.edgeMetaParentsList(Integer.MIN_VALUE + 1) != null)
		    || (persp.edgeMetaParentsList(Integer.MIN_VALUE) != null)
		    || (persp.edgeMetaParentsList(edge1NotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("totally wrong edge meta parents");

		// getEdgeMetaParentIndicesArray(int).
		parentInx = persp.getEdgeMetaParentIndicesArray(edgeInx[3]);

		if (parentInx.length != 0)
			throw new IllegalStateException("expected 0 edge parents");

		parentInx = persp.getEdgeMetaParentIndicesArray(edgeInx[4]);

		if (parentInx.length != 1)
			throw new IllegalStateException("expected 1 edge parent");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[0])
				break;

		parentInx = persp.getEdgeMetaParentIndicesArray(edgeInx[6]);

		if (parentInx.length != 2)
			throw new IllegalStateException("expected 2 edge parent");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		if ((persp.getEdgeMetaParentIndicesArray(0) != null)
		    || (persp.getEdgeMetaParentIndicesArray(2) != null)
		    || (persp.getEdgeMetaParentIndicesArray(Integer.MAX_VALUE) != null)
		    || (persp.getEdgeMetaParentIndicesArray(Integer.MIN_VALUE) != null)
		    || (persp.getEdgeMetaParentIndicesArray(minEdgeInx - 1) != null)
		    || (persp.getEdgeMetaParentIndicesArray(edge2NotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("totally wrong edge meta parents");

		// isMetaChild(Node, Edge).
		if (!(persp.isMetaChild(persp.getNode(nodeInx[4]), persp.getEdge(edgeInx[6]))
		    || persp.isMetaChild(persp.getNode(nodeInx[0]), persp.getEdge(edgeInx[4]))))
			throw new IllegalStateException("missing edge meta-relationship");

		if (persp.isMetaChild(persp.getNode(nodeInx[2]), persp.getEdge(edgeInx[6]))
		    || persp.isMetaChild(persp.getNode(nodeInx[4]), persp.getEdge(edgeInx[0])))
			throw new IllegalStateException("wrong edge meta-relationship");

		if (persp.isMetaChild(root2Node, root2Edge)
		    || persp.isMetaChild(persp.getNode(nodeInx[3]), edge1NotInPersp))
			throw new IllegalStateException("totally wrong edge meta-relationship");

		// isEdgeMetaChild(int, int).
		if (!(persp.isEdgeMetaChild(nodeInx[4], edgeInx[2])
		    && persp.isEdgeMetaChild(nodeInx[3], edgeInx[0])))
			throw new IllegalStateException("missing edge meta-relationship");

		if (persp.isEdgeMetaChild(nodeInx[3], edgeInx[2])
		    || persp.isEdgeMetaChild(nodeInx[0], edgeInx[2])
		    || persp.isEdgeMetaChild(nodeInx[0], edgeInx[5]))
			throw new IllegalStateException("wrong edge meta-relationship");

		if (persp.isEdgeMetaChild(0, 0)
		    || persp.isEdgeMetaChild(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || persp.isEdgeMetaChild(minNodeInx - 1, minEdgeInx - 1)
		    || persp.isEdgeMetaChild(nodeNotInPersp.getRootGraphIndex(), edgeInx[1]))
			throw new IllegalStateException("totally wrong edge meta-relationship");

		// edgeMetaChildrenList(Node).
		List edgeChildrenList = persp.edgeMetaChildrenList(persp.getNode(nodeInx[4]));

		if (edgeChildrenList.size() != 2)
			throw new IllegalStateException("expected 2 edge children");

		for (int i = 0;; i++)
			if (((Edge) edgeChildrenList.get(i)).getRootGraphIndex() == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (((Edge) edgeChildrenList.get(i)).getRootGraphIndex() == edgeInx[6])
				break;

		edgeChildrenList = persp.edgeMetaChildrenList(persp.getNode(nodeInx[0]));

		if (edgeChildrenList.size() != 1)
			throw new IllegalStateException("expected 1 edge child");

		for (int i = 0;; i++)
			if (((Edge) edgeChildrenList.get(i)).getRootGraphIndex() == edgeInx[4])
				break;

		if ((persp.edgeMetaChildrenList(root2Node) != null)
		    || (persp.edgeMetaChildrenList(nodeNotInPersp) != null))
			throw new IllegalStateException("expected null as edge children list");

		// edgeMetaChildRenList(int).
		edgeChildrenList = persp.edgeMetaChildrenList(nodeInx[1]);

		if (edgeChildrenList.size() != 0)
			throw new IllegalStateException("expected 0 edge children");

		edgeChildrenList = persp.edgeMetaChildrenList(nodeInx[3]);

		if (edgeChildrenList.size() != 2)
			throw new IllegalStateException("expected 2 edge children");

		for (int i = 0;; i++)
			if (((Edge) edgeChildrenList.get(i)).getRootGraphIndex() == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (((Edge) edgeChildrenList.get(i)).getRootGraphIndex() == edgeInx[6])
				break;

		if ((persp.edgeMetaChildrenList(0) != null)
		    || (persp.edgeMetaChildrenList(nodeNotInPersp.getRootGraphIndex()) != null)
		    || (persp.edgeMetaChildrenList(23) != null)
		    || (persp.edgeMetaChildrenList(Integer.MAX_VALUE) != null)
		    || (persp.edgeMetaChildrenList(Integer.MIN_VALUE) != null)
		    || (persp.edgeMetaChildrenList(minNodeInx - 1) != null))
			throw new IllegalStateException("totally wrong edge children list");

		// getEdgeMetaChildIndicesArray(int).
		int[] childEdgeInx = persp.getEdgeMetaChildIndicesArray(nodeInx[2]);

		if (childEdgeInx.length != 0)
			throw new IllegalStateException("expected no edge children");

		childEdgeInx = persp.getEdgeMetaChildIndicesArray(nodeInx[4]);

		if (childEdgeInx.length != 2)
			throw new IllegalStateException("expected 2 edge children");

		for (int i = 0;; i++)
			if (childEdgeInx[i] == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (childEdgeInx[i] == edgeInx[6])
				break;

		if ((persp.getEdgeMetaChildIndicesArray(0) != null)
		    || (persp.getEdgeMetaChildIndicesArray(23) != null)
		    || (persp.getEdgeMetaChildIndicesArray(Integer.MAX_VALUE) != null)
		    || (persp.getEdgeMetaChildIndicesArray(Integer.MIN_VALUE) != null)
		    || (persp.getEdgeMetaChildIndicesArray(minNodeInx - 1) != null)
		    || (persp.getEdgeMetaChildIndicesArray(nodeNotInPersp.getRootGraphIndex()) != null))
			throw new IllegalStateException("expected null edge children");

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

		// getConnectingNodeIndicesArray(int[]).
		int[] edgeInputInxArr = new int[3];
		edgeInputInxArr[0] = edgeInx[0];
		edgeInputInxArr[1] = edgeInx[2];
		edgeInputInxArr[2] = edgeInx[4];

		int[] connectingNodeInx = persp.getConnectingNodeIndicesArray(edgeInputInxArr);

		if (connectingNodeInx.length != 3)
			throw new IllegalStateException("expected 3 connecting nodes");

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[2])
				break;

		edgeInputInxArr = new int[2];
		edgeInputInxArr[0] = edgeInx[6];
		edgeInputInxArr[1] = edgeInx[1];
		connectingNodeInx = persp.getConnectingNodeIndicesArray(edgeInputInxArr);

		if (connectingNodeInx.length != 3)
			throw new IllegalStateException("expected 3 connecting nodes");

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[3])
				break;

		edgeInputInxArr = new int[1];
		edgeInputInxArr[0] = edgeInx[3];
		connectingNodeInx = persp.getConnectingNodeIndicesArray(edgeInputInxArr);

		if (connectingNodeInx.length != 1)
			throw new IllegalStateException("expected one connecting node");

		for (int i = 0;; i++)
			if (connectingNodeInx[i] == nodeInx[2])
				break;

		if ((persp.getConnectingNodeIndicesArray(new int[76]) != null)
		    || (persp.getConnectingNodeIndicesArray(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE }) != null)
		    || (persp.getConnectingNodeIndicesArray(new int[] { minEdgeInx - 1, edgeInx[0] }) != null)
		    || (persp.getConnectingNodeIndicesArray(new int[] {
		                                                edgeInx[2],
		                                                edge1NotInPersp.getRootGraphIndex()
		                                            }) != null))
			throw new IllegalStateException("expected null connecting node inx");

		// createGraphPerspective(int[]).
	}
}

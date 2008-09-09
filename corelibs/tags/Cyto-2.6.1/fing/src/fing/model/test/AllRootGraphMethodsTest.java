
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
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;
import java.util.List;


/**
 *
 */
public final class AllRootGraphMethodsTest {
	// No constructor.
	private AllRootGraphMethodsTest() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static final void main(String[] args) {
		final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
		runTest(root);
	}

	// Package visible so other classes can call this.
	static final void runTest(RootGraph root) {
		// Don't change this!  Any change here implies re-reading all the test
		// code below and making appropriate changes there.
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
		// Meta-node definitions.
		nodeInx[nodeInx.length - 1] = root.createNode(null, new int[] { edgeInx[6], edgeInx[2] }); // Should have nodes
		                                                                                           // nodeInx[0], nodeInx[2], and nodeInx[3].

		if (!(root.addNodeMetaChild(nodeInx[0], nodeInx[1])
		    && root.addNodeMetaChild(nodeInx[0], nodeInx[4])
		    && root.addNodeMetaChild(nodeInx[3], nodeInx[1])
		    && root.addNodeMetaChild(nodeInx[4], nodeInx[4])
		    && root.addEdgeMetaChild(nodeInx[3], edgeInx[6])
		    && root.addEdgeMetaChild(nodeInx[3], edgeInx[0])
		    && root.addEdgeMetaChild(nodeInx[0], edgeInx[4])))
			throw new IllegalStateException("unable to create meta relationship");

		for (int i = 0; i < nodeInx.length; i++)
			if (nodeInx[i] >= 0)
				throw new IllegalStateException("non-negative node");

		for (int i = 0; i < edgeInx.length; i++)
			if (edgeInx[i] >= 0)
				throw new IllegalStateException("non-negative edge");

		// Test add/remove nodes and edges before other tests.
		// We leave the graph with the same topology after these tests as
		// existed before these tests.
		if (root.removeNode(nodeInx[1]) != nodeInx[1])
			throw new IllegalStateException("removal of node failed");

		if ((root.removeEdge(edgeInx[0]) != 0) || (root.removeEdge(edgeInx[1]) != 0)
		    || (root.removeEdge(edgeInx[4]) != 0) || (root.removeEdge(edgeInx[5]) != 0))
			throw new IllegalStateException("removal failure failed - edge");

		if (root.removeNode(nodeInx[1]) != 0)
			throw new IllegalStateException("removal failure failed - node");

		if (root.removeEdge(edgeInx[6]) != edgeInx[6])
			throw new IllegalStateException("removal of edge failed");

		if (root.removeEdge(edgeInx[6]) != 0)
			throw new IllegalStateException("removal failure failed - edge");

		if ((root.removeNode(0) != 0) || (root.removeNode(Integer.MAX_VALUE) != 0)
		    || (root.removeNode(Integer.MIN_VALUE) != 0))
			throw new IllegalStateException("removal failure failed - node");

		if ((root.removeEdge(0) != 0) || (root.removeEdge(Integer.MAX_VALUE) != 0)
		    || (root.removeNode(Integer.MIN_VALUE) != 0))
			throw new IllegalStateException("removal failure failed - edge");

		if ((root.createEdge(Integer.MAX_VALUE, Integer.MAX_VALUE, true) != 0)
		    || (root.createEdge(Integer.MIN_VALUE, Integer.MIN_VALUE, false) != 0)
		    || (root.createEdge(0, 0, true) != 0))
			throw new IllegalStateException("creation failure failed - edge");

		int deleteThisNode0 = root.createNode();
		nodeInx[1] = root.createNode();

		if (root.removeNode(deleteThisNode0) != deleteThisNode0)
			throw new IllegalStateException("cannot delete node");

		int deleteThisEdge;

		if ((deleteThisEdge = root.createEdge(nodeInx[4], nodeInx[1], true)) >= 0)
			throw new IllegalStateException("had problems creating edge");

		edgeInx[0] = root.createEdge(nodeInx[0], nodeInx[1], true);
		edgeInx[1] = root.createEdge(nodeInx[1], nodeInx[2], false);
		edgeInx[4] = root.createEdge(nodeInx[1], nodeInx[1], false);
		edgeInx[5] = root.createEdge(nodeInx[1], nodeInx[0], true);
		edgeInx[6] = root.createEdge(nodeInx[3], nodeInx[2], true);

		if (root.removeEdge(deleteThisEdge) != deleteThisEdge)
			throw new IllegalStateException("cannot delete edge");

		int deleteThisNode = root.createNode();
		int deleteThisEdge1;

		if ((deleteThisEdge1 = root.createEdge(nodeInx[0], nodeInx[1], false)) >= 0)
			throw new IllegalStateException("could not create edge");

		int deleteThisEdge2;

		if ((deleteThisEdge2 = root.createEdge(nodeInx[4], nodeInx[2], false)) >= 0)
			throw new IllegalStateException("could not create edge");

		if (root.createEdge(deleteThisNode, nodeInx[1], true) >= 0)
			throw new IllegalStateException("could not create edge");

		if (root.removeNode(deleteThisNode) != deleteThisNode)
			throw new IllegalStateException("could not delete node");

		if (root.removeEdge(deleteThisEdge1) != deleteThisEdge1)
			throw new IllegalStateException("could not delete edge");

		if (root.removeEdge(deleteThisEdge2) != deleteThisEdge2)
			throw new IllegalStateException("could not delete edge");

		// Meta-nodes.  First restore what was there originally.
		if (!(root.addEdgeMetaChild(nodeInx[3], edgeInx[0])))
			throw new IllegalStateException("errors during restoration");

		if (!(root.addEdgeMetaChild(nodeInx[0], edgeInx[4])))
			throw new IllegalStateException("errors during restoration");

		if (!(root.addEdgeMetaChild(nodeInx[4], edgeInx[6])
		    && root.addEdgeMetaChild(nodeInx[3], edgeInx[6])))
			throw new IllegalStateException("errors during restoration");

		if (root.addEdgeMetaChild(nodeInx[3], edgeInx[0])
		    || root.addEdgeMetaChild(nodeInx[4], edgeInx[2])
		    || root.addNodeMetaChild(nodeInx[3], nodeInx[1])
		    || root.addEdgeMetaChild(0, edgeInx[0]) || root.addEdgeMetaChild(nodeInx[1], 0)
		    || root.addEdgeMetaChild(99, 98)
		    || root.addEdgeMetaChild(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || root.addNodeMetaChild(0, nodeInx[2]) || root.addNodeMetaChild(nodeInx[2], 0)
		    || root.addNodeMetaChild(87, 23)
		    || root.addNodeMetaChild(Integer.MIN_VALUE, Integer.MAX_VALUE))
			throw new IllegalStateException("failure of failure during creation");

		// Meta-nodes.  Create and remove extra meta-relationships.
		if (!root.addNodeMetaChild(nodeInx[2], nodeInx[1]))
			throw new IllegalStateException("failed to create relationship");

		if (root.addNodeMetaChild(nodeInx[2], nodeInx[1])
		    || root.addNodeMetaChild(nodeInx[4], nodeInx[0]))
			throw new IllegalStateException("was able to create duplicate meta");

		if (!(root.addEdgeMetaChild(nodeInx[4], edgeInx[5])
		    && root.addNodeMetaChild(nodeInx[2], nodeInx[2])))
			throw new IllegalStateException("failed to create meta relationship");

		if (root.addNodeMetaChild(nodeInx[2], nodeInx[2])
		    || root.addEdgeMetaChild(nodeInx[4], edgeInx[5])
		    || root.addEdgeMetaChild(nodeInx[4], edgeInx[6]))
			throw new IllegalStateException("was able to create duplicate meta");

		if (!(root.removeNodeMetaChild(nodeInx[2], nodeInx[1])
		    && root.removeNodeMetaChild(nodeInx[4], nodeInx[1])
		    && root.removeNodeMetaChild(nodeInx[2], nodeInx[2])))
			throw new IllegalStateException("could not delete meta relationship");

		if (root.removeEdgeMetaChild(nodeInx[4], edgeInx[5]))
			throw new IllegalStateException("edge child was supposed to go away");

		if (root.removeNodeMetaChild(nodeInx[2], nodeInx[3])
		    || root.removeEdgeMetaChild(nodeInx[4], edgeInx[0])
		    || root.removeNodeMetaChild(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || root.removeEdgeMetaChild(Integer.MIN_VALUE, Integer.MAX_VALUE)
		    || root.removeNodeMetaChild(0, 88) || root.removeEdgeMetaChild(88, 0))
			throw new IllegalStateException("deleted non-existent meta");

		for (int i = 0; i < nodeInx.length; i++)
			if (nodeInx[i] >= 0)
				throw new IllegalStateException("non-negative node");

		for (int i = 0; i < edgeInx.length; i++)
			if (edgeInx[i] >= 0)
				throw new IllegalStateException("non-negative edge");

		// nodesIterator() and edgesIterator().
		Iterator nodesIter = root.nodesIterator();
		Iterator edgesIter = root.edgesIterator();
		Node[] twoNodes = new Node[] { (Node) nodesIter.next(), (Node) nodesIter.next() };
		Edge[] twoEdges = new Edge[] { (Edge) edgesIter.next(), (Edge) edgesIter.next() };

		// createGraphPerspective(Node[], Edge[].
		if (root.createGraphPerspective(twoNodes, null).getNodeCount() != 2)
			throw new IllegalStateException("GraphPerspective does not have two nodes");

		if (root.createGraphPerspective(null, twoEdges).getEdgeCount() != 2)
			throw new IllegalStateException("GraphPerspective does not have two edges");

		if (root.createGraphPerspective(twoNodes, twoEdges).getNodeCount() < 2)
			throw new IllegalStateException("GraphPerspective has less than two nodes");

		if (root.createGraphPerspective(twoNodes, twoEdges).getEdgeCount() < 2)
			throw new IllegalStateException("GraphPerspective has less than two edges");

		if (root.createGraphPerspective((Node[]) null, (Edge[]) null) == null)
			throw new IllegalStateException("GraphPerspective is null");

		if (root.createGraphPerspective(new Node[0], new Edge[0]) == null)
			throw new IllegalStateException("GraphPerspective is null");

		RootGraph root2 = FingRootGraphFactory.instantiateRootGraph();
		root2.createNode();
		root2.createEdge(((Node) root2.nodesIterator().next()).getRootGraphIndex(),
		                 ((Node) root2.nodesIterator().next()).getRootGraphIndex());

		Node root2Node = (Node) root2.nodesIterator().next();
		Edge root2Edge = (Edge) root2.edgesIterator().next();

		if (root.createGraphPerspective(new Node[] { root2Node }, null) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		if (root.createGraphPerspective(null, new Edge[] { root2Edge }) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		if (root.createGraphPerspective(new Node[] { twoNodes[0], root2Node },
		                                new Edge[] { twoEdges[0], root2Edge }) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		// createGraphPerspective(int[], int[]).
		int[] twoNodeInx = new int[] {
		                       twoNodes[0].getRootGraphIndex(), twoNodes[1].getRootGraphIndex()
		                   };
		int[] twoEdgeInx = new int[] {
		                       twoEdges[0].getRootGraphIndex(), twoEdges[1].getRootGraphIndex()
		                   };

		if (root.createGraphPerspective(twoNodeInx, null).getNodeCount() != 2)
			throw new IllegalStateException("GraphPerspective does not have two nodes");

		if (root.createGraphPerspective(null, twoEdgeInx).getEdgeCount() != 2)
			throw new IllegalStateException("GraphPerspective does not have two edges");

		if (root.createGraphPerspective(twoNodeInx, twoEdgeInx).getNodeCount() < 2)
			throw new IllegalStateException("GraphPerspective has less than two nodes");

		if (root.createGraphPerspective(twoNodeInx, twoEdgeInx).getEdgeCount() < 2)
			throw new IllegalStateException("GraphPerspective has less than two edges");

		if (root.createGraphPerspective((int[]) null, (int[]) null) == null)
			throw new IllegalStateException("GraphPerspective is null");

		if (root.createGraphPerspective(new int[0], new int[0]) == null)
			throw new IllegalStateException("GraphPerspective is null");

		if (root.createGraphPerspective(new int[] { 0 }, null) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		if (root.createGraphPerspective(null, new int[] { 0 }) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		if (root.createGraphPerspective(new int[] { twoNodeInx[0], 0 },
		                                new int[] { twoEdgeInx[0], 9999 }) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		if (root.createGraphPerspective(new int[] { 1 }, null) != null)
			throw new IllegalStateException("GraphPerspective is not null");

		if ((root.createGraphPerspective(new int[] { Integer.MAX_VALUE }, null) != null)
		    || (root.createGraphPerspective(new int[] { Integer.MIN_VALUE }, null) != null)
		    || (root.createGraphPerspective(null, new int[] { Integer.MAX_VALUE }) != null)
		    || (root.createGraphPerspective(null, new int[] { Integer.MIN_VALUE }) != null))
			throw new IllegalStateException("GraphPerspective is not null");

		// getNodeCount() and getEdgeCount().
		if ((root.getNodeCount() != 5) || (root.getEdgeCount() != 7))
			throw new IllegalStateException("incorrect nodes or edges count");

		// nodesList().
		List nodesList = root.nodesList();

		if (nodesList.size() != 5)
			throw new IllegalStateException("incorrect node List size");

		for (int i = 0; i < nodesList.size(); i++) {
			Node n = (Node) nodesList.get(i);
		}

		// getNodeIndicesArray().
		int[] nodeIndicesArray = root.getNodeIndicesArray();

		if (nodeIndicesArray.length != nodesList.size())
			throw new IllegalStateException("size of nodes List and length of node indices array don't match");

		if (root.createGraphPerspective(nodeIndicesArray, null) == null)
			throw new IllegalStateException("GraphPerspective is null");

		// edgesList().
		List edgesList = root.edgesList();

		if (edgesList.size() != 7)
			throw new IllegalStateException("incorrect edge List size");

		for (int i = 0; i < edgesList.size(); i++) {
			Edge e = (Edge) edgesList.get(i);
		}

		// getEdgeIndicesArray().
		int[] edgeIndicesArray = root.getEdgeIndicesArray();

		if (edgeIndicesArray.length != edgesList.size())
			throw new IllegalStateException("size of edges List and length of edge indices array don't match");

		if (root.createGraphPerspective(null, edgeIndicesArray) == null)
			throw new IllegalStateException("GraphPerspective is null");

		// Create and remove node/edge functionality is tested in other code.

		// containsNode(Node).
		if (!root.containsNode(twoNodes[1]))
			throw new IllegalStateException("RootGraph does not contain node");

		if (root.containsNode(root2Node))
			throw new IllegalStateException("RootGraph contains node from other");

		// containsEdge(Edge).
		if (!root.containsEdge(twoEdges[1]))
			throw new IllegalStateException("RootGraph does not contain edge");

		if (root.containsEdge(root2Edge))
			throw new IllegalStateException("RootGraph contains edge from other");

		// neighborsList(Node).
		List neighList = root.neighborsList(root.getNode(nodeInx[0]));

		if (neighList.size() != 2)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = root.neighborsList(root.getNode(nodeInx[1]));

		if (neighList.size() != 3)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = root.neighborsList(root.getNode(nodeInx[2]));

		if (neighList.size() != 4)
			throw new IllegalStateException("wrong number of neighbors");

		int[] neighInx = new int[neighList.size()];

		for (int i = 0; i < neighList.size(); i++) {
			Node node = (Node) neighList.get(i);
			int nodeIndex = node.getRootGraphIndex();

			if (root.getNode(nodeIndex) == null)
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

		neighList = root.neighborsList(root.getNode(nodeInx[3]));

		if (neighList.size() != 1)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = root.neighborsList(root.getNode(nodeInx[4]));

		if (neighList.size() != 0)
			throw new IllegalStateException("wrong number of neighbors");

		neighList = root.neighborsList(root2Node);

		if (neighList != null)
			throw new IllegalStateException("neighbors List isn't null");

		// isNeighbor(Node, Node).
		if (root.isNeighbor(root.getNode(nodeInx[4]), root.getNode(nodeInx[4])))
			throw new IllegalStateException("node with no edges is its own neigh");

		if (root.isNeighbor(root.getNode(nodeInx[3]), root.getNode(nodeInx[1])))
			throw new IllegalStateException("nodes are neighbors");

		if (!root.isNeighbor(root.getNode(nodeInx[1]), root.getNode(nodeInx[0])))
			throw new IllegalStateException("nodes are not neighbors");

		if (root.isNeighbor(root2Node, root.getNode(nodeInx[2])))
			throw new IllegalStateException("nodes from another graph is neighbor");

		// isNeighbor(int, int).
		if (!root.isNeighbor(nodeInx[1], nodeInx[1]))
			throw new IllegalStateException("node with self edge not neigbhor");

		if (root.isNeighbor(nodeInx[0], nodeInx[0]))
			throw new IllegalStateException("node with no self edge is neighbor");

		if (root.isNeighbor(98, 99))
			throw new IllegalStateException("positive nodes are neighbors");

		if (!root.isNeighbor(nodeInx[3], nodeInx[2]))
			throw new IllegalStateException("nodes are not neighbors");

		if (!root.isNeighbor(nodeInx[1], nodeInx[2]))
			throw new IllegalStateException("nodes are not neighbors");

		if (root.isNeighbor(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || root.isNeighbor(Integer.MIN_VALUE, Integer.MAX_VALUE))
			throw new IllegalStateException("MIN_VALUE and MAX_VALUE neighbors");

		// edgeExists(Node, Node).
		if (root.edgeExists(root.getNode(nodeInx[3]), root.getNode(nodeInx[1])))
			throw new IllegalStateException("edge exists");

		if (root.edgeExists(root.getNode(nodeInx[0]), root2Node))
			throw new IllegalStateException("edge exists with node of other graph");

		if (!root.edgeExists(root.getNode(nodeInx[0]), root.getNode(nodeInx[1])))
			throw new IllegalStateException("edge does not exist");

		if (!root.edgeExists(root.getNode(nodeInx[1]), root.getNode(nodeInx[2])))
			throw new IllegalStateException("edge does not exist");

		// edgeExists(int, int).
		if (root.edgeExists(0, 0))
			throw new IllegalStateException("0 -> 0");

		if (root.edgeExists(nodeInx[2], nodeInx[3]))
			throw new IllegalStateException("edge exists in opposite direction");

		if (root.edgeExists(nodeInx[4], nodeInx[2]))
			throw new IllegalStateException("edge exists on node with no edge");

		if (root.edgeExists(nodeInx[0], nodeInx[0]))
			throw new IllegalStateException("self-edge exists");

		if (root.edgeExists(98, 99))
			throw new IllegalStateException("edge exists between positive nodes");

		if (!root.edgeExists(nodeInx[1], nodeInx[1]))
			throw new IllegalStateException("self-edge does not exist [undirected]");

		if (!root.edgeExists(nodeInx[2], nodeInx[2]))
			throw new IllegalStateException("self-edge does not exist [directed]");

		if (!root.edgeExists(nodeInx[3], nodeInx[2]))
			throw new IllegalStateException("edge does not exist");

		if (!root.edgeExists(nodeInx[1], nodeInx[0]))
			throw new IllegalStateException("edge does not exist");

		if (root.edgeExists(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || root.edgeExists(Integer.MIN_VALUE, Integer.MAX_VALUE))
			throw new IllegalStateException("MIN_VALUE and MAX_VALUE edge exists");

		// getEdgeCount(Node, Node, boolean).
		if (root.getEdgeCount(root.getNode(nodeInx[0]), root.getNode(nodeInx[1]), true) != 1)
			throw new IllegalStateException("wrong number in edge count");

		if (root.getEdgeCount(root.getNode(nodeInx[0]), root2Node, true) != -1)
			throw new IllegalStateException("edge count not -1");

		if (root.getEdgeCount(root.getNode(nodeInx[1]), root.getNode(nodeInx[1]), false) != 0)
			throw new IllegalStateException("edge count not 0");

		// getEdgeCount(int, int, boolean).
		if (root.getEdgeCount(nodeInx[3], nodeInx[2], true) != 1)
			throw new IllegalStateException("edge count not 1");

		if (root.getEdgeCount(nodeInx[2], nodeInx[3], true) != 0)
			throw new IllegalStateException("edge count not 0");

		if (root.getEdgeCount(nodeInx[1], nodeInx[2], false) != 0)
			throw new IllegalStateException("edge count not 0");

		if (root.getEdgeCount(nodeInx[1], nodeInx[1], true) != 1)
			throw new IllegalStateException("edge count not 1 for und. self edge");

		if (root.getEdgeCount(99, 98, true) != -1)
			throw new IllegalStateException("edge count not -1");

		if ((root.getEdgeCount(Integer.MAX_VALUE, Integer.MIN_VALUE, false) != -1)
		    || (root.getEdgeCount(Integer.MIN_VALUE, Integer.MAX_VALUE, true) != -1))
			throw new IllegalStateException("edge count not -1");

		// getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean).
		int[] adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[0], true, true, true);

		if (adjEdges.length != 3)
			throw new IllegalStateException("not 3 adj.");

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[5])
				break;

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[2])
				break;

		adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[4], true, true, true);

		if (adjEdges.length != 0)
			throw new IllegalStateException("not 0 adj.");

		adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[2], true, false, true);

		if (adjEdges.length != 3)
			throw new IllegalStateException("not 3 adj.");

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[1])
				break;

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[3])
				break;

		adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[2], true, true, true);

		if (adjEdges.length != 4)
			throw new IllegalStateException("not 4 adj.");

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[6])
				break;

		adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[1], true, false, false);

		if (adjEdges.length != 2)
			throw new IllegalStateException("not 2 adj.");

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[1])
				break;

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[4])
				break;

		adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[1], false, true, true);

		if (adjEdges.length != 2)
			throw new IllegalStateException("not 2 adj.");

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (adjEdges[i] == edgeInx[5])
				break;

		adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[3], true, true, false);

		if (adjEdges.length != 0)
			throw new IllegalStateException("not 0 adj.");

		int minEdgeInx = 0;

		for (int i = 0; i < edgeInx.length; i++)
			minEdgeInx = Math.min(minEdgeInx, edgeInx[i]);

		adjEdges = root.getAdjacentEdgeIndicesArray(99, true, true, true);

		if (adjEdges != null)
			throw new IllegalStateException("not null");

		adjEdges = root.getAdjacentEdgeIndicesArray(minEdgeInx - 1, true, true, true);

		if (adjEdges != null)
			throw new IllegalStateException("not null");

		adjEdges = root.getAdjacentEdgeIndicesArray(0, true, true, true);

		if (adjEdges != null)
			throw new IllegalStateException("not null");

		if ((root.getAdjacentEdgeIndicesArray(Integer.MAX_VALUE, true, true, false) != null)
		    || (root.getAdjacentEdgeIndicesArray(Integer.MIN_VALUE, false, true, true) != null))
			throw new IllegalStateException("not null");

		// getConnectingEdgeIndicesArray(int[]).
		int[] connEdges = root.getConnectingEdgeIndicesArray(nodeInx);

		if (connEdges.length != edgeInx.length)
			throw new IllegalStateException("edge arrays not same length");

		for (int i = 0; i < edgeInx.length; i++)
			for (int j = 0;; j++)
				if (connEdges[j] == edgeInx[i])
					break;

		int[] someNodes = new int[] { nodeInx[0], nodeInx[2], nodeInx[3] };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges.length != 3)
			throw new IllegalStateException("not 3 connecting edges");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[3])
				break;

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[6])
				break;

		someNodes = new int[] { nodeInx[1], nodeInx[4], nodeInx[0], nodeInx[3] };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges.length != 3)
			throw new IllegalStateException("not 3 connecting edges");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[4])
				break;

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[5])
				break;

		someNodes = new int[] { nodeInx[2] };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[3])
				break;

		someNodes = new int[] { nodeInx[4], nodeInx[3], nodeInx[0] };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		someNodes = new int[] { nodeInx[0], nodeInx[1], nodeInx[2], nodeInx[3] };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges.length != edgeInx.length)
			throw new IllegalStateException("edge arrays not same length");

		for (int i = 0; i < edgeInx.length; i++)
			for (int j = 0;; j++)
				if (connEdges[j] == edgeInx[i])
					break;

		int minNodeInx = 0;

		for (int i = 0; i < nodeInx.length; i++)
			minNodeInx = Math.min(minNodeInx, nodeInx[i]);

		someNodes = new int[] { 99 };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		someNodes = new int[] { nodeInx[0], nodeInx[1], minNodeInx - 1, nodeInx[2] };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		someNodes = new int[] { nodeInx[4], 0 };
		connEdges = root.getConnectingEdgeIndicesArray(someNodes);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		if ((root.getConnectingEdgeIndicesArray(new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE }) != null)
		    || (root.getConnectingEdgeIndicesArray(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE }) != null))
			throw new IllegalStateException("not null");

		// getConnectingNodeIndicesArray(int[]).
		int[] connNodes = root.getConnectingNodeIndicesArray(edgeInx);

		if (connNodes.length != 4)
			throw new IllegalStateException("not 4 connecting nodes");

		for (int i = 0; i < nodeInx.length; i++)
			if (i != 4)
				for (int j = 0;; j++)
					if (connNodes[j] == nodeInx[i])
						break;

		int[] someEdges = new int[] { edgeInx[0], edgeInx[3], edgeInx[5] };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes.length != 3)
			throw new IllegalStateException("not 3 connecting nodes");

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[2])
				break;

		someEdges = new int[] { edgeInx[6] };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes.length != 2)
			throw new IllegalStateException("not 2 connecting nodes");

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[3])
				break;

		someEdges = new int[] { edgeInx[4], edgeInx[3] };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes.length != 2)
			throw new IllegalStateException("not 2 connecting nodes");

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[2])
				break;

		someEdges = new int[] { edgeInx[5], edgeInx[6] };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes.length != 4)
			throw new IllegalStateException("not 4 connecting nodes");

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (connNodes[i] == nodeInx[3])
				break;

		someEdges = new int[] { 99 };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes != null)
			throw new IllegalStateException("not null");

		someEdges = new int[] { minEdgeInx - 1 };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes != null)
			throw new IllegalStateException("not null");

		someEdges = new int[] { edgeInx[0], 0, edgeInx[1] };
		connNodes = root.getConnectingNodeIndicesArray(someEdges);

		if (connNodes != null)
			throw new IllegalStateException("not null");

		if ((root.getConnectingNodeIndicesArray(new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE }) != null)
		    || (root.getConnectingNodeIndicesArray(new int[] { Integer.MIN_VALUE, Integer.MAX_VALUE }) != null))
			throw new IllegalStateException("not null");

		// getEdgeIndicesArray(int, int, boolean, boolean).
		connEdges = root.getEdgeIndicesArray(nodeInx[1], nodeInx[0], false, true);

		if (connEdges.length != 2)
			throw new IllegalStateException("not 2 connecting edges");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[5])
				break;

		connEdges = root.getEdgeIndicesArray(nodeInx[0], nodeInx[3], true, true);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = root.getEdgeIndicesArray(nodeInx[1], nodeInx[2], false, true);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = root.getEdgeIndicesArray(nodeInx[2], nodeInx[1], true, false);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[1])
				break;

		connEdges = root.getEdgeIndicesArray(nodeInx[2], nodeInx[2], false, false);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[3])
				break;

		connEdges = root.getEdgeIndicesArray(nodeInx[2], nodeInx[2], true, true);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[3])
				break;

		connEdges = root.getEdgeIndicesArray(nodeInx[2], nodeInx[3], false, false);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = root.getEdgeIndicesArray(nodeInx[3], nodeInx[2], false, false);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[6])
				break;

		connEdges = root.getEdgeIndicesArray(nodeInx[4], nodeInx[0], true, true);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = root.getEdgeIndicesArray(99, 0, true, true);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		connEdges = root.getEdgeIndicesArray(nodeInx[0], minNodeInx - 1, true, false);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		if ((root.getEdgeIndicesArray(Integer.MAX_VALUE, Integer.MIN_VALUE, true, false) != null)
		    || (root.getEdgeIndicesArray(Integer.MIN_VALUE, Integer.MAX_VALUE, false, false) != null))
			throw new IllegalStateException("not null");

		// edgesList(Node, Node).
		edgesList = root.edgesList(root.getNode(nodeInx[3]), root.getNode(nodeInx[1]));

		if (edgesList.size() != 0)
			throw new IllegalStateException("edges List not of size 0");

		edgesList = root.edgesList(root.getNode(nodeInx[0]), root2Node);

		if (edgesList != null)
			throw new IllegalStateException("not null");

		edgesList = root.edgesList(root.getNode(nodeInx[0]), root.getNode(nodeInx[1]));

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[0])
			throw new IllegalStateException("wrong edge");

		edgesList = root.edgesList(root.getNode(nodeInx[2]), root.getNode(nodeInx[1]));

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[1])
			throw new IllegalStateException("wrong edge");

		edgesList = root.edgesList(root.getNode(nodeInx[2]), root.getNode(nodeInx[2]));

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[3])
			throw new IllegalStateException("wrong edge");

		edgesList = root.edgesList(root.getNode(nodeInx[1]), root.getNode(nodeInx[4]));

		if (edgesList.size() != 0)
			throw new IllegalStateException("edges List not of size 0");

		// edgesList(int, int, boolean).
		edgesList = root.edgesList(nodeInx[2], nodeInx[0], true);

		if (edgesList.size() != 1)
			throw new IllegalStateException("edges List not of size 1");

		if (((Edge) edgesList.get(0)).getRootGraphIndex() != edgeInx[2])
			throw new IllegalStateException("wrong edge");

		edgesList = root.edgesList(nodeInx[4], nodeInx[3], false);

		if (edgesList.size() != 0)
			throw new IllegalStateException("edges List not of size 0");

		edgesList = root.edgesList(99, minNodeInx - 1, true);

		if (edgesList != null)
			throw new IllegalStateException("not null");

		if ((root.edgesList(Integer.MAX_VALUE, Integer.MIN_VALUE, true) != null)
		    || (root.edgesList(Integer.MIN_VALUE, Integer.MAX_VALUE, false) != null))
			throw new IllegalStateException("not null");

		// getEdgeIndicesArray(int, int, boolean).
		connEdges = root.getEdgeIndicesArray(nodeInx[1], nodeInx[1], false);

		if (connEdges.length != 0)
			throw new IllegalStateException("not 0 connecting edges");

		connEdges = root.getEdgeIndicesArray(nodeInx[1], nodeInx[1], true);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[4])
				break;

		connEdges = root.getEdgeIndicesArray(nodeInx[1], nodeInx[0], true);

		if (connEdges.length != 1)
			throw new IllegalStateException("not 1 connecting edge");

		for (int i = 0;; i++)
			if (connEdges[i] == edgeInx[5])
				break;

		connEdges = root.getEdgeIndicesArray(minNodeInx - 1, nodeInx[2], true);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		connEdges = root.getEdgeIndicesArray(nodeInx[1], 99, true);

		if (connEdges != null)
			throw new IllegalStateException("not null");

		if ((root.getEdgeIndicesArray(Integer.MAX_VALUE, Integer.MIN_VALUE, true) != null)
		    || (root.getEdgeIndicesArray(Integer.MIN_VALUE, Integer.MAX_VALUE, false) != null))
			throw new IllegalStateException("not null");

		// getInDegree(Node).
		if (root.getInDegree(root2Node) != -1)
			throw new IllegalStateException("not in degree -1 for other node");

		if (root.getInDegree(root.getNode(nodeInx[2])) != 3)
			throw new IllegalStateException("not in degree 3 for node");

		if (root.getInDegree(root.getNode(nodeInx[3])) != 0)
			throw new IllegalStateException("not in degree 0 for node");

		if (root.getInDegree(root.getNode(nodeInx[4])) != 0)
			throw new IllegalStateException("not in degree 0 for node");

		if (root.getInDegree(root.getNode(nodeInx[0])) != 2)
			throw new IllegalStateException("not in degree 2 for node");

		if (root.getInDegree(root.getNode(nodeInx[1])) != 3)
			throw new IllegalStateException("not in degree 3 for node");

		// getInDegree(int).
		if ((root.getInDegree(minNodeInx - 1) != -1) || (root.getInDegree(0) != -1)
		    || (root.getInDegree(99) != -1))
			throw new IllegalStateException("not in degree -1");

		if (root.getInDegree(nodeInx[1]) != 3)
			throw new IllegalStateException("not in degree 3");

		if (root.getInDegree(nodeInx[0]) != 2)
			throw new IllegalStateException("not in degree 2");

		if (root.getInDegree(Integer.MAX_VALUE) != -1)
			throw new IllegalStateException("not in degree -1");

		// getInDegree(Node, boolean).
		if (root.getInDegree(root2Node, true) != -1)
			throw new IllegalStateException("not in degree -1 for other node");

		if (root.getInDegree(root.getNode(nodeInx[1]), false) != 1)
			throw new IllegalStateException("not in degree 1");

		if (root.getInDegree(root.getNode(nodeInx[2]), false) != 2)
			throw new IllegalStateException("not in degree 2");

		if (root.getInDegree(root.getNode(nodeInx[2]), true) != 3)
			throw new IllegalStateException("not in degree 3");

		// getInDegree(int, boolean).
		if ((root.getInDegree(minNodeInx - 1, false) != -1) || (root.getInDegree(0, true) != -1)
		    || (root.getInDegree(99, false) != -1))
			throw new IllegalStateException("not in degree -1");

		if (root.getInDegree(nodeInx[0], true) != 2)
			throw new IllegalStateException("not in degree 2");

		if (root.getInDegree(nodeInx[0], false) != 2)
			throw new IllegalStateException("not in degree 2");

		if (root.getInDegree(nodeInx[4], true) != 0)
			throw new IllegalStateException("not in degree 0");

		if (root.getInDegree(nodeInx[1], false) != 1)
			throw new IllegalStateException("not in degree 1");

		if (root.getInDegree(Integer.MIN_VALUE, true) != -1)
			throw new IllegalStateException("not in degree -1");

		// getOutDegree(Node).
		if (root.getOutDegree(root2Node) != -1)
			throw new IllegalStateException("not out degree -1");

		if (root.getOutDegree(root.getNode(nodeInx[2])) != 3)
			throw new IllegalStateException("not out degree 3");

		if (root.getOutDegree(root.getNode(nodeInx[1])) != 3)
			throw new IllegalStateException("not out degree 3");

		if (root.getOutDegree(root.getNode(nodeInx[4])) != 0)
			throw new IllegalStateException("not out degree 0");

		// getOutDegree(int).
		if ((root.getOutDegree(minNodeInx - 1) != -1) || (root.getOutDegree(0) != -1)
		    || (root.getOutDegree(101) != -1))
			throw new IllegalStateException("not out degree -1");

		if (root.getOutDegree(nodeInx[3]) != 1)
			throw new IllegalStateException("not out degree 1");

		if (root.getOutDegree(nodeInx[0]) != 1)
			throw new IllegalStateException("not out degree 1");

		if (root.getOutDegree(Integer.MIN_VALUE) != -1)
			throw new IllegalStateException("not out degree -1");

		// getOutDegree(Node, boolean).
		if (root.getOutDegree(root2Node, false) != -1)
			throw new IllegalStateException("not out degree -1");

		if (root.getOutDegree(root.getNode(nodeInx[1]), false) != 1)
			throw new IllegalStateException("not out degree 1");

		if (root.getOutDegree(root.getNode(nodeInx[1]), true) != 3)
			throw new IllegalStateException("not out degree 1");

		if ((root.getOutDegree(root.getNode(nodeInx[0]), false) != 1)
		    || (root.getOutDegree(root.getNode(nodeInx[0]), true) != 1))
			throw new IllegalStateException("not out degree 1");

		// getOutDegree(int, boolean).
		if ((root.getOutDegree(minNodeInx - 3, false) != -1) || (root.getOutDegree(0, true) != -1)
		    || (root.getOutDegree(2, false) != -1))
			throw new IllegalStateException("not out degree -1");

		if ((root.getOutDegree(nodeInx[2], false) != 2)
		    || (root.getOutDegree(nodeInx[2], true) != 3))
			throw new IllegalStateException("not correct out degree");

		if ((root.getOutDegree(nodeInx[3], false) != 1)
		    || (root.getOutDegree(nodeInx[3], true) != 1))
			throw new IllegalStateException("not out degree 1");

		if ((root.getOutDegree(nodeInx[4], true) != 0)
		    || (root.getOutDegree(nodeInx[4], false) != 0))
			throw new IllegalStateException("not out degree 0");

		if (root.getOutDegree(Integer.MAX_VALUE, false) != -1)
			throw new IllegalStateException("not out degree -1");

		// getDegree(Node).
		if (root.getDegree(root2Node) != -1)
			throw new IllegalStateException("not degree -1 for other node");

		if (root.getDegree(root.getNode(nodeInx[0])) != 3)
			throw new IllegalStateException("not degree 3");

		if (root.getDegree(root.getNode(nodeInx[1])) != 4)
			throw new IllegalStateException("not degree 4");

		// getDegree(int).
		if ((root.getDegree(minNodeInx - 2) != -1) || (root.getDegree(0) != -1)
		    || (root.getDegree(13) != -1))
			throw new IllegalStateException("not degree -1");

		if (root.getDegree(nodeInx[2]) != 4)
			throw new IllegalStateException("not degree 4");

		if (root.getDegree(nodeInx[3]) != 1)
			throw new IllegalStateException("not degree 1");

		if (root.getDegree(nodeInx[4]) != 0)
			throw new IllegalStateException("not degree 0");

		if ((root.getDegree(Integer.MAX_VALUE) != -1) || (root.getDegree(Integer.MIN_VALUE) != -1))
			throw new IllegalStateException("not degree -1");

		// getIndex(Node).
		if (root.getIndex(root2Node) != 0)
			throw new IllegalStateException("index not 0");

		if (root.getIndex(root.getNode(nodeInx[2])) != nodeInx[2])
			throw new IllegalStateException("wrong node index");

		// getNode(int).
		if ((root.getNode(minNodeInx - 1) != null) || (root.getNode(0) != null)
		    || (root.getNode(23) != null))
			throw new IllegalStateException("not null");

		if ((root.getNode(Integer.MAX_VALUE) != null) || (root.getNode(Integer.MIN_VALUE) != null))
			throw new IllegalStateException("not null");

		// getIndex(Edge).
		if (root.getIndex(root2Edge) != 0)
			throw new IllegalStateException("index not 0");

		if (root.getIndex(root.getEdge(edgeInx[3])) != edgeInx[3])
			throw new IllegalStateException("wrong edge index");

		// getEdge(int).
		if ((root.getEdge(minEdgeInx - 1) != null) || (root.getEdge(0) != null)
		    || (root.getEdge(37) != null))
			throw new IllegalStateException("not null");

		if ((root.getEdge(Integer.MIN_VALUE) != null) || (root.getEdge(Integer.MAX_VALUE) != null))
			throw new IllegalStateException("not null");

		// getEdgeSourceIndex(int).
		if ((root.getEdgeSourceIndex(minEdgeInx - 1) != 0) || (root.getEdgeSourceIndex(0) != 0)
		    || (root.getEdgeSourceIndex(97) != 0))
			throw new IllegalStateException("edge source not 0");

		if ((root.getEdgeSourceIndex(edgeInx[3]) != nodeInx[2])
		    || (root.getEdgeSourceIndex(edgeInx[6]) != nodeInx[3])
		    || (root.getEdgeSourceIndex(edgeInx[1]) != nodeInx[1])
		    || (root.getEdgeSourceIndex(edgeInx[4]) != nodeInx[1]))
			throw new IllegalStateException("wrong edge source node");

		if ((root.getEdgeSourceIndex(Integer.MAX_VALUE) != 0)
		    || (root.getEdgeSourceIndex(Integer.MIN_VALUE) != 0))
			throw new IllegalStateException("edge source not 0");

		// getEdgeTargetIndex(int).
		if ((root.getEdgeTargetIndex(minEdgeInx - 1) != 0) || (root.getEdgeTargetIndex(0) != 0)
		    || (root.getEdgeTargetIndex(93) != 0))
			throw new IllegalStateException("wrong edge target node");

		if ((root.getEdgeTargetIndex(edgeInx[2]) != nodeInx[0])
		    || (root.getEdgeTargetIndex(edgeInx[0]) != nodeInx[1])
		    || (root.getEdgeTargetIndex(edgeInx[5]) != nodeInx[0]))
			throw new IllegalStateException("wrong edge target node");

		if ((root.getEdgeTargetIndex(Integer.MAX_VALUE) != 0)
		    || (root.getEdgeTargetIndex(Integer.MIN_VALUE) != 0))
			throw new IllegalStateException("edge target not 0");

		// isEdgeDirected(int).
		if ((!root.isEdgeDirected(edgeInx[0])) || root.isEdgeDirected(edgeInx[1])
		    || root.isEdgeDirected(edgeInx[4]) || (!root.isEdgeDirected(edgeInx[5])))
			throw new IllegalStateException("wrong edge directedness");

		// isMetaParent(Node, Node).
		if (root.isMetaParent(root.getNode(nodeInx[2]), root.getNode(nodeInx[1])))
			throw new IllegalStateException("reported wrong meta child node");

		if (root.isMetaParent(root.getNode(nodeInx[1]), root.getNode(nodeInx[4])))
			throw new IllegalStateException("reported wrong meta child node");

		if (root.isMetaParent(root.getNode(nodeInx[4]), root.getNode(nodeInx[3])))
			throw new IllegalStateException("reported wrong meta child node");

		if (root.isMetaParent(root2Node, root.getNode(nodeInx[1])))
			throw new IllegalStateException("reported wrong meta child node");

		if (root.isMetaParent(root.getNode(nodeInx[3]), root2Node))
			throw new IllegalStateException("reported wrong meta child node");

		if (root.isMetaParent(root.getNode(nodeInx[2]), root.getNode(nodeInx[2])))
			throw new IllegalStateException("reported wrong meta child node");

		if (!(root.isMetaParent(root.getNode(nodeInx[4]), root.getNode(nodeInx[4]))))
			throw new IllegalStateException("missing a meta relationship");

		if (!(root.isMetaParent(root.getNode(nodeInx[4]), root.getNode(nodeInx[0]))))
			throw new IllegalStateException("missing a meta relationship");

		if (!(root.isMetaParent(root.getNode(nodeInx[1]), root.getNode(nodeInx[0]))))
			throw new IllegalStateException("missing a meta relationship");

		// isNodeMetaParent(int, int).
		if (root.isNodeMetaParent(nodeInx[4], nodeInx[1])
		    || root.isNodeMetaParent(nodeInx[1], nodeInx[1]) || root.isNodeMetaParent(99, 0)
		    || root.isNodeMetaParent(nodeInx[4], minNodeInx - 1)
		    || root.isNodeMetaParent(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || root.isNodeMetaParent(Integer.MIN_VALUE, 0))
			throw new IllegalStateException("reported wrong meta child node");

		if (!(root.isNodeMetaParent(nodeInx[3], nodeInx[3])))
			throw new IllegalStateException("missing a meta relationship");

		if (!(root.isNodeMetaParent(nodeInx[2], nodeInx[3])))
			throw new IllegalStateException("missing a meta relationship");

		if (!(root.isNodeMetaParent(nodeInx[0], nodeInx[4])))
			throw new IllegalStateException("missing a meta relationship");

		if (!(root.isNodeMetaParent(nodeInx[4], nodeInx[0])))
			throw new IllegalStateException("missing a meta relationship");

		if (!(root.isNodeMetaParent(nodeInx[0], nodeInx[3])))
			throw new IllegalStateException("missing a meta relationship");

		// metaParentsList(Node).
		if (root.metaParentsList(root2Node) != null)
			throw new IllegalStateException("expected null for other node");

		nodesList = root.metaParentsList(root.getNode(nodeInx[0]));

		if (nodesList.size() != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		for (int i = 0; i < nodesList.size(); i++) {
			Node foo = (Node) (nodesList.get(i));
		}

		nodesList = root.metaParentsList(root.getNode(nodeInx[1]));

		if (nodesList.size() != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		// nodeMetaParentsList(int).
		if ((root.nodeMetaParentsList(0) != null)
		    || (root.nodeMetaParentsList(Integer.MAX_VALUE) != null)
		    || (root.nodeMetaParentsList(Integer.MIN_VALUE) != null)
		    || (root.nodeMetaParentsList(99) != null)
		    || (root.nodeMetaParentsList(minNodeInx - 1) != null))
			throw new IllegalStateException("expected null for meta parents");

		nodesList = root.nodeMetaParentsList(nodeInx[2]);

		if (nodesList.size() != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		nodesList = root.nodeMetaParentsList(nodeInx[3]);

		if (nodesList.size() != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		nodesList = root.nodeMetaParentsList(nodeInx[4]);

		if (nodesList.size() != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		// getNodeMetaParentIndicesArray(int).
		if ((root.getNodeMetaParentIndicesArray(0) != null)
		    || (root.getNodeMetaParentIndicesArray(Integer.MAX_VALUE) != null)
		    || (root.getNodeMetaParentIndicesArray(Integer.MIN_VALUE) != null)
		    || (root.getNodeMetaParentIndicesArray(99) != null)
		    || (root.getNodeMetaParentIndicesArray(minNodeInx - 1) != null))
			throw new IllegalStateException("expected null for meta parents");

		int[] parentInx = root.getNodeMetaParentIndicesArray(nodeInx[0]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		parentInx = root.getNodeMetaParentIndicesArray(nodeInx[1]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		parentInx = root.getNodeMetaParentIndicesArray(nodeInx[2]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		parentInx = root.getNodeMetaParentIndicesArray(nodeInx[3]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		parentInx = root.getNodeMetaParentIndicesArray(nodeInx[4]);

		if (parentInx.length != 2)
			throw new IllegalStateException("wrong number of parent nodes");

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (parentInx[i] == nodeInx[4])
				break;

		// isMetaChild(Node, Node).
		if (root.isMetaChild(root2Node, root2Node)
		    || root.isMetaChild(root.getNode(nodeInx[1]), root.getNode(nodeInx[2]))
		    || root.isMetaChild(root.getNode(nodeInx[2]), root.getNode(nodeInx[1])))
			throw new IllegalStateException("unexpected meta child");

		if (!(root.isMetaChild(root.getNode(nodeInx[4]), root.getNode(nodeInx[4]))
		    && root.isMetaChild(root.getNode(nodeInx[3]), root.getNode(nodeInx[2]))
		    && root.isMetaChild(root.getNode(nodeInx[0]), root.getNode(nodeInx[1]))))
			throw new IllegalStateException("missing meta relationship");

		// isNodeMetaChild(int, int).
		if (root.isNodeMetaChild(0, 0) || root.isNodeMetaChild(Integer.MIN_VALUE, 87)
		    || root.isNodeMetaChild(nodeInx[0], Integer.MAX_VALUE)
		    || root.isNodeMetaChild(minNodeInx - 1, nodeInx[1])
		    || root.isNodeMetaChild(nodeInx[0], nodeInx[3])
		    || root.isNodeMetaChild(nodeInx[1], nodeInx[0]))
			throw new IllegalStateException("unexpected meta relationship");

		if (!(root.isNodeMetaChild(nodeInx[0], nodeInx[1])
		    && root.isNodeMetaChild(nodeInx[3], nodeInx[3])
		    && root.isNodeMetaChild(nodeInx[0], nodeInx[4])))
			throw new IllegalStateException("missing meta relationship");

		// isNodeMetaChild(int, int, boolean).
		if (root.isNodeMetaChild(0, 0, true)
		    || root.isNodeMetaChild(Integer.MIN_VALUE, Integer.MAX_VALUE, true)
		    || root.isNodeMetaChild(Integer.MAX_VALUE, Integer.MIN_VALUE, true)
		    || root.isNodeMetaChild(minNodeInx - 1, nodeInx[1], true)
		    || root.isNodeMetaChild(nodeInx[0], nodeInx[3], false)
		    || root.isNodeMetaChild(nodeInx[2], nodeInx[4], true)
		    || root.isNodeMetaChild(nodeInx[1], nodeInx[0], true))
			throw new IllegalStateException("unexpected recursive meta child");

		if (!(root.isNodeMetaChild(nodeInx[0], nodeInx[3], true)
		    && root.isNodeMetaChild(nodeInx[0], nodeInx[0], true)
		    && root.isNodeMetaChild(nodeInx[0], nodeInx[2], true)
		    && root.isNodeMetaChild(nodeInx[4], nodeInx[1], true)
		    && root.isNodeMetaChild(nodeInx[3], nodeInx[4], true)))
			throw new IllegalStateException("missing recursive meta child");

		// nodeMetaChildrenList(Node).
		if (root.nodeMetaChildrenList(root2Node) != null)
			throw new IllegalStateException("expected null for other node children");

		nodesList = root.nodeMetaChildrenList(root.getNode(nodeInx[2]));

		if (nodesList.size() != 0)
			throw new IllegalStateException("expected no children");

		nodesList = root.nodeMetaChildrenList(root.getNode(nodeInx[4]));

		if (nodesList.size() != 4)
			throw new IllegalStateException("wrong number of children");

		for (int i = 0; i < nodesList.size(); i++) {
			Node foo = (Node) nodesList.get(i);
		}

		// nodeMetaChildrenList(int).
		if ((root.nodeMetaChildrenList(0) != null)
		    || (root.nodeMetaChildrenList(Integer.MAX_VALUE) != null)
		    || (root.nodeMetaChildrenList(Integer.MIN_VALUE) != null)
		    || (root.nodeMetaChildrenList(minNodeInx - 1) != null)
		    || (root.nodeMetaChildrenList(77) != null))
			throw new IllegalStateException("expected null children list");

		nodesList = root.nodeMetaChildrenList(nodeInx[3]);

		if (nodesList.size() != 4)
			throw new IllegalStateException("wrong number of children");

		int[] nodeChildInx = new int[nodesList.size()];

		for (int i = 0; i < nodesList.size(); i++) {
			Node foo = (Node) nodesList.get(i);
			nodeChildInx[i] = foo.getRootGraphIndex();
		}

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[3])
				break;

		// getNodeMetaChildIndicesArray(int).
		if ((root.getNodeMetaChildIndicesArray(0) != null)
		    || (root.getNodeMetaChildIndicesArray(Integer.MAX_VALUE) != null)
		    || (root.getNodeMetaChildIndicesArray(Integer.MIN_VALUE) != null)
		    || (root.getNodeMetaChildIndicesArray(minNodeInx - 1) != null)
		    || (root.getNodeMetaChildIndicesArray(66) != null))
			throw new IllegalStateException("expected null children array");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[0]);

		if (nodeChildInx.length != 2)
			throw new IllegalStateException("wrong number of children in array");

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[4])
				break;

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[1]);

		if (nodeChildInx.length != 0)
			throw new IllegalStateException("wrong number of children in array");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[4]);

		if (nodeChildInx.length != 4)
			throw new IllegalStateException("wrong number of children in array");

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[4])
				break;

		// getNodeMetaChildIndicesArray(int, boolean).
		if ((root.getNodeMetaChildIndicesArray(0, true) != null)
		    || (root.getNodeMetaChildIndicesArray(Integer.MAX_VALUE, true) != null)
		    || (root.getNodeMetaChildIndicesArray(Integer.MIN_VALUE, true) != null)
		    || (root.getNodeMetaChildIndicesArray(minNodeInx - 1, true) != null)
		    || (root.getNodeMetaChildIndicesArray(66, true) != null))
			throw new IllegalStateException("expected null children array");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[0], true);

		if (nodeChildInx.length != 5)
			throw new IllegalStateException("wrong # recursive children");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[1], true);

		if (nodeChildInx.length != 0)
			throw new IllegalStateException("wrong # recursive children");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[2], true);

		if (nodeChildInx.length != 0)
			throw new IllegalStateException("wrong # recursive children");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[3], true);

		if (nodeChildInx.length != 5)
			throw new IllegalStateException("wrong # recursive children");

		nodeChildInx = root.getNodeMetaChildIndicesArray(nodeInx[4], true);

		if (nodeChildInx.length != 5)
			throw new IllegalStateException("wrong # recursive children");

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[0])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[2])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[4])
				break;

		// getChildlessMetaDescendants(int).
		if ((root.getChildlessMetaDescendants(0) != null)
		    || (root.getChildlessMetaDescendants(Integer.MAX_VALUE) != null)
		    || (root.getChildlessMetaDescendants(Integer.MIN_VALUE) != null)
		    || (root.getChildlessMetaDescendants(minNodeInx - 1) != null)
		    || (root.getChildlessMetaDescendants(12) != null))
			throw new IllegalStateException("expected null childless descendants");

		if (root.getChildlessMetaDescendants(nodeInx[1]).length != 0)
			throw new IllegalStateException("expected no childless descendants");

		nodeChildInx = root.getChildlessMetaDescendants(nodeInx[0]);

		if (nodeChildInx.length != 2)
			throw new IllegalStateException("wrong number of childless descendants");

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[1])
				break;

		for (int i = 0;; i++)
			if (nodeChildInx[i] == nodeInx[2])
				break;

		// isMetaParent(Edge, Node).
		if (root.isMetaParent(root2Edge, root2Node)
		    || root.isMetaParent(root.getEdge(edgeInx[1]), root.getNode(nodeInx[1]))
		    || root.isMetaParent(root.getEdge(edgeInx[6]), root.getNode(nodeInx[2])))
			throw new IllegalStateException("wrong edge meta relationships");

		if (root.isMetaParent(root.getEdge(edgeInx[1]), root.getNode(nodeInx[3])))
			throw new IllegalStateException("wrong edge meta relationships");

		if (!(root.isMetaParent(root.getEdge(edgeInx[0]), root.getNode(nodeInx[3]))
		    && root.isMetaParent(root.getEdge(edgeInx[2]), root.getNode(nodeInx[4]))))
			throw new IllegalStateException("missing edge meta relationship");

		// isEdgeMetaParent(int, int).
		if (root.isEdgeMetaParent(0, 0)
		    || root.isEdgeMetaParent(Integer.MAX_VALUE, Integer.MIN_VALUE)
		    || root.isEdgeMetaParent(Integer.MIN_VALUE, Integer.MAX_VALUE)
		    || root.isEdgeMetaParent(1, 2) || root.isEdgeMetaParent(minEdgeInx - 1, nodeInx[0]))
			throw new IllegalStateException("wrong edge meta relationship");

		if (!(root.isEdgeMetaParent(edgeInx[6], nodeInx[3])
		    && root.isEdgeMetaParent(edgeInx[6], nodeInx[4])))
			throw new IllegalStateException("missing edge meta relationship");

		// metaParentsList(Edge).
		if (root.metaParentsList(root2Edge) != null)
			throw new IllegalStateException("expected null edge parents");

		nodesList = root.metaParentsList(root.getEdge(edgeInx[6]));

		if (nodesList.size() != 2)
			throw new IllegalStateException("wrong number of edge parents");

		int[] metaParents = new int[nodesList.size()];

		for (int i = 0; i < nodesList.size(); i++) {
			Node foo = (Node) nodesList.get(i);
			metaParents[i] = foo.getRootGraphIndex();
		}

		for (int i = 0;; i++)
			if (metaParents[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (metaParents[i] == nodeInx[4])
				break;

		// edgeMetaParentsList(int).
		if ((root.edgeMetaParentsList(0) != null)
		    || (root.edgeMetaParentsList(Integer.MAX_VALUE) != null)
		    || (root.edgeMetaParentsList(Integer.MIN_VALUE) != null)
		    || (root.edgeMetaParentsList(1) != null)
		    || (root.edgeMetaParentsList(minEdgeInx - 1) != null))
			throw new IllegalStateException("expected null meta parents array");

		nodesList = root.edgeMetaParentsList(edgeInx[2]);

		if ((nodesList.size() != 1)
		    || (((Node) nodesList.get(0)).getRootGraphIndex() != nodeInx[4]))
			throw new IllegalStateException("wrong edge meta parents");

		// getEdgeMetaParentIndicesArray(int).
		if ((root.getEdgeMetaParentIndicesArray(0) != null)
		    || (root.getEdgeMetaParentIndicesArray(Integer.MAX_VALUE) != null)
		    || (root.getEdgeMetaParentIndicesArray(Integer.MIN_VALUE) != null)
		    || (root.getEdgeMetaParentIndicesArray(minEdgeInx - 1) != null)
		    || (root.getEdgeMetaParentIndicesArray(66) != null))
			throw new IllegalStateException("expected null parent array");

		int[] edgeParentInx = root.getEdgeMetaParentIndicesArray(edgeInx[0]);

		if (edgeParentInx.length != 1)
			throw new IllegalStateException("wrong number of edge parents");

		for (int i = 0;; i++)
			if (edgeParentInx[i] == nodeInx[3])
				break;

		edgeParentInx = root.getEdgeMetaParentIndicesArray(edgeInx[1]);

		if (edgeParentInx.length != 0)
			throw new IllegalStateException("wrong number of edge parents");

		edgeParentInx = root.getEdgeMetaParentIndicesArray(edgeInx[2]);

		if (edgeParentInx.length != 1)
			throw new IllegalStateException("wrong number of edge parents");

		for (int i = 0;; i++)
			if (edgeParentInx[i] == nodeInx[4])
				break;

		edgeParentInx = root.getEdgeMetaParentIndicesArray(edgeInx[4]);

		if (edgeParentInx.length != 1)
			throw new IllegalStateException("wrong number of edge parents");

		for (int i = 0;; i++)
			if (edgeParentInx[i] == nodeInx[0])
				break;

		edgeParentInx = root.getEdgeMetaParentIndicesArray(edgeInx[5]);

		if (edgeParentInx.length != 0)
			throw new IllegalStateException("wrong number of edge parents");

		edgeParentInx = root.getEdgeMetaParentIndicesArray(edgeInx[6]);

		if (edgeParentInx.length != 2)
			throw new IllegalStateException("wrong number of edge parents");

		for (int i = 0;; i++)
			if (edgeParentInx[i] == nodeInx[3])
				break;

		for (int i = 0;; i++)
			if (edgeParentInx[i] == nodeInx[4])
				break;

		// isMetaChild(Node, Edge).
		if (root.isMetaChild(root2Node, root2Edge)
		    || root.isMetaChild(root2Node, root.getEdge(edgeInx[0])))
			throw new IllegalStateException("child with other graph's elements");

		if (root.isMetaChild(root.getNode(nodeInx[1]), root.getEdge(edgeInx[2]))
		    || root.isMetaChild(root.getNode(nodeInx[4]), root.getEdge(edgeInx[4])))
			throw new IllegalStateException("reported wrong meta relationship");

		if (!(root.isMetaChild(root.getNode(nodeInx[4]), root.getEdge(edgeInx[6]))
		    && root.isMetaChild(root.getNode(nodeInx[0]), root.getEdge(edgeInx[4]))))
			throw new IllegalStateException("missing meta relationship");

		// isEdgeMetaChild(int, int).
		if (root.isEdgeMetaChild(0, 0)
		    || root.isEdgeMetaChild(Integer.MIN_VALUE, Integer.MAX_VALUE)
		    || root.isEdgeMetaChild(minNodeInx - 1, minEdgeInx - 1) || root.isEdgeMetaChild(98, 99)
		    || root.isEdgeMetaChild(nodeInx[3], edgeInx[4])
		    || root.isEdgeMetaChild(nodeInx[0], edgeInx[3])
		    || root.isEdgeMetaChild(nodeInx[2], edgeInx[1]))
			throw new IllegalStateException("reported wrong meta relationship");

		if (!(root.isEdgeMetaChild(nodeInx[4], edgeInx[2])
		    && root.isEdgeMetaChild(nodeInx[3], edgeInx[6])
		    && root.isEdgeMetaChild(nodeInx[3], edgeInx[0])))
			throw new IllegalStateException("missing meta relationship");

		// edgeMetaChildrenList(Node).
		if (root.edgeMetaChildrenList(root2Node) != null)
			throw new IllegalStateException("expected null return value");

		edgesList = root.edgeMetaChildrenList(root.getNode(nodeInx[3]));

		if (edgesList.size() != 2)
			throw new IllegalStateException("wrong number of edge children");

		for (int i = 0;; i++)
			if (((Edge) edgesList.get(i)).getRootGraphIndex() == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (((Edge) edgesList.get(i)).getRootGraphIndex() == edgeInx[6])
				break;

		// edgeMetaChildrenList(int).
		if ((root.edgeMetaChildrenList(0) != null) || (root.edgeMetaChildrenList(1) != null)
		    || (root.edgeMetaChildrenList(Integer.MAX_VALUE) != null)
		    || (root.edgeMetaChildrenList(Integer.MIN_VALUE) != null)
		    || (root.edgeMetaChildrenList(minNodeInx - 1) != null))
			throw new IllegalStateException("expected null return value");

		edgesList = root.edgeMetaChildrenList(nodeInx[1]);

		if (edgesList.size() != 0)
			throw new IllegalStateException("wrong number of edge children");

		edgesList = root.edgeMetaChildrenList(nodeInx[0]);

		if (edgesList.size() != 1)
			throw new IllegalStateException("wrong number of edge children");

		for (int i = 0;; i++)
			if (((Edge) edgesList.get(i)).getRootGraphIndex() == edgeInx[4])
				break;

		// getEdgeMetaChildIndicesArray(int).
		if ((root.getEdgeMetaChildIndicesArray(0) != null)
		    || (root.getEdgeMetaChildIndicesArray(1) != null)
		    || (root.getEdgeMetaChildIndicesArray(Integer.MAX_VALUE) != null)
		    || (root.getEdgeMetaChildIndicesArray(Integer.MIN_VALUE) != null)
		    || (root.getEdgeMetaChildIndicesArray(minNodeInx - 1) != null))
			throw new IllegalStateException("expected null return value");

		int[] edgeChildInx = root.getEdgeMetaChildIndicesArray(nodeInx[0]);

		if (edgeChildInx.length != 1)
			throw new IllegalStateException("wrong number of edge children");

		for (int i = 0;; i++)
			if (edgeChildInx[i] == edgeInx[4])
				break;

		edgeChildInx = root.getEdgeMetaChildIndicesArray(nodeInx[1]);

		if (edgeChildInx.length != 0)
			throw new IllegalStateException("wrong number of edge children");

		edgeChildInx = root.getEdgeMetaChildIndicesArray(nodeInx[2]);

		if (edgeChildInx.length != 0)
			throw new IllegalStateException("wrong number of edge children");

		edgeChildInx = root.getEdgeMetaChildIndicesArray(nodeInx[3]);

		if (edgeChildInx.length != 2)
			throw new IllegalStateException("wrong number of edge children");

		for (int i = 0;; i++)
			if (edgeChildInx[i] == edgeInx[0])
				break;

		for (int i = 0;; i++)
			if (edgeChildInx[i] == edgeInx[6])
				break;

		edgeChildInx = root.getEdgeMetaChildIndicesArray(nodeInx[4]);

		if (edgeChildInx.length != 2)
			throw new IllegalStateException("wrong number of edge children");

		for (int i = 0;; i++)
			if (edgeChildInx[i] == edgeInx[2])
				break;

		for (int i = 0;; i++)
			if (edgeChildInx[i] == edgeInx[6])
				break;

		// Finally we remove all nodes and edges.
		if (root.removeEdge(edgeInx[2]) != edgeInx[2])
			throw new IllegalStateException("failed to remove edge");

		if (root.removeEdge(edgeInx[4]) != edgeInx[4])
			throw new IllegalStateException("failed to remove edge");

		for (int i = 0; i < nodeInx.length; i++)
			if (root.removeNode(nodeInx[i]) != nodeInx[i])
				throw new IllegalStateException("failed to remove node");

		for (int i = 0; i < edgeInx.length; i++)
			if (root.removeEdge(edgeInx[i]) != 0)
				throw new IllegalStateException("edge should already be removed");
	}
}

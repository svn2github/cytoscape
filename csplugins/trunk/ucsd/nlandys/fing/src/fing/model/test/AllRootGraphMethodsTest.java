package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.Edge;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;

public final class AllRootGraphMethodsTest
{

  // No constructor.
  private AllRootGraphMethodsTest() { }

  public static final void main(String[] args)
    throws ClassNotFoundException, InstantiationException,
           IllegalAccessException
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();

    // Don't change this!  Any change here implies re-reading all the test
    // code below and making appropriate changes there.
    int[] nodeInx = new int[5];
    for (int i = 0; i < nodeInx.length; i++) nodeInx[i] = root.createNode();
    int[] edgeInx = new int[7];
    edgeInx[0] = root.createEdge(nodeInx[0], nodeInx[1], true);
    edgeInx[1] = root.createEdge(nodeInx[1], nodeInx[2], false);
    edgeInx[2] = root.createEdge(nodeInx[2], nodeInx[0], true);
    edgeInx[3] = root.createEdge(nodeInx[2], nodeInx[2], true);
    edgeInx[4] = root.createEdge(nodeInx[1], nodeInx[1], false);
    edgeInx[5] = root.createEdge(nodeInx[1], nodeInx[0], true);
    edgeInx[6] = root.createEdge(nodeInx[3], nodeInx[2], true);

    // nodesIterator() and edgesIterator().
    Iterator nodesIter = root.nodesIterator();
    Iterator edgesIter = root.edgesIterator();
    Node[] twoNodes = new Node[] { (Node) nodesIter.next(),
                                   (Node) nodesIter.next() };
    Edge[] twoEdges = new Edge[] { (Edge) edgesIter.next(),
                                   (Edge) edgesIter.next() };

    // createGraphPerspective(Node[], Edge[].
    if (root.createGraphPerspective(twoNodes, null).getNodeCount() != 2)
      throw new IllegalStateException
        ("GraphPerspective does not have two nodes");
    if (root.createGraphPerspective(null, twoEdges).getEdgeCount() != 2)
      throw new IllegalStateException
        ("GraphPerspective does not have two edges");
    if (root.createGraphPerspective(twoNodes, twoEdges).getNodeCount() < 2)
      throw new IllegalStateException
        ("GraphPerspective has less than two nodes");
    if (root.createGraphPerspective(twoNodes, twoEdges).getEdgeCount() < 2)
      throw new IllegalStateException
        ("GraphPerspective has less than two edges");
    if (root.createGraphPerspective((Node[]) null, (Edge[]) null) == null)
      throw new IllegalStateException("GraphPerspective is null");
    if (root.createGraphPerspective(new Node[0], new Edge[0]) == null)
      throw new IllegalStateException("GraphPerspective is null");
    RootGraph root2 = FingRootGraphFactory.instantiateRootGraph();
    root2.createNode();
    root2.createEdge
      (((Node) root2.nodesIterator().next()).getRootGraphIndex(),
       ((Node) root2.nodesIterator().next()).getRootGraphIndex());
    Node root2Node = (Node) root2.nodesIterator().next();
    Edge root2Edge = (Edge) root2.edgesIterator().next();
    if (root.createGraphPerspective(new Node[] { root2Node }, null) != null)
      throw new IllegalStateException("GraphPerspective is not null");
    if (root.createGraphPerspective(null, new Edge[] { root2Edge }) != null)
      throw new IllegalStateException("GraphPerspective is not null");
    if (root.createGraphPerspective(new Node[] { twoNodes[0], root2Node },
                                    new Edge[] { twoEdges[0], root2Edge })
        != null)
      throw new IllegalStateException("GraphPerspective is not null");

    // createGraphPerspective(int[], int[]).
    int[] twoNodeInx = new int[] { twoNodes[0].getRootGraphIndex(),
                                   twoNodes[1].getRootGraphIndex() };
    int[] twoEdgeInx = new int[] { twoEdges[0].getRootGraphIndex(),
                                   twoEdges[1].getRootGraphIndex() };
    if (root.createGraphPerspective(twoNodeInx, null).getNodeCount() != 2)
      throw new IllegalStateException
        ("GraphPerspective does not have two nodes");
    if (root.createGraphPerspective(null, twoEdgeInx).getEdgeCount() != 2)
      throw new IllegalStateException
        ("GraphPerspective does not have two edges");
    if (root.createGraphPerspective(twoNodeInx, twoEdgeInx).getNodeCount() < 2)
      throw new IllegalStateException
        ("GraphPerspective has less than two nodes");
    if (root.createGraphPerspective(twoNodeInx, twoEdgeInx).getEdgeCount() < 2)
      throw new IllegalStateException
        ("GraphPerspective has less than two edges");
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

    // getNodeCount() and getEdgeCount().
    if (root.getNodeCount() != 5 || root.getEdgeCount() != 7)
      throw new IllegalStateException("incorrect nodes or edges count");

    // nodesList().
    java.util.List nodesList = root.nodesList();
    if (nodesList.size() != 5)
      throw new IllegalStateException("incorrect node List size");
    for (int i = 0; i < nodesList.size(); i++) {
      Node n = (Node) nodesList.get(i); }

    // getNodeIndicesArray().
    int[] nodeIndicesArray = root.getNodeIndicesArray();
    if (nodeIndicesArray.length != nodesList.size())
      throw new IllegalStateException
        ("size of nodes List and length of node indices array don't match");
    if (root.createGraphPerspective(nodeIndicesArray, null) == null)
      throw new IllegalStateException("GraphPerspective is null");

    // edgesList().
    java.util.List edgesList = root.edgesList();
    if (edgesList.size() != 7)
      throw new IllegalStateException("incorrect edge List size");
    for (int i = 0; i < edgesList.size(); i++) {
      Edge e = (Edge) edgesList.get(i); }

    // getEdgeIndicesArray().
    int[] edgeIndicesArray = root.getEdgeIndicesArray();
    if (edgeIndicesArray.length != edgesList.size())
      throw new IllegalStateException
        ("size of edges List and length of edge indices array don't match");
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
    java.util.List neighList = root.neighborsList(root.getNode(nodeInx[0]));
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
      if (nodeIndex == 0) throw new IllegalStateException("node index is 0");
      int index = -1;
      while (true) {
        if (neighInx[++index] != 0) {
          if (neighInx[index] == nodeIndex)
            throw new IllegalStateException("duplicate neighbor");
          else continue; }
        else { neighInx[index] = nodeIndex; break; } } }
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
    if (root.edgeExists(0, 0)) throw new IllegalStateException("0 -> 0");
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

    // getEdgeCount(Node, Node, boolean).
    if (root.getEdgeCount(root.getNode(nodeInx[0]), root.getNode(nodeInx[1]),
                          true) != 1)
      throw new IllegalStateException("wrong number in edge count");
    if (root.getEdgeCount(root.getNode(nodeInx[0]), root2Node, true) != -1)
      throw new IllegalStateException("edge count not -1");
    if (root.getEdgeCount(root.getNode(nodeInx[1]), root.getNode(nodeInx[1]),
                          false) != 0)
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

    // getAdjacentEdgeIndicesArray(int, boolean, boolean, boolean).
    int[] adjEdges = root.getAdjacentEdgeIndicesArray
      (nodeInx[0], true, true, true);
    if (adjEdges.length != 3) throw new IllegalStateException("not 3 adj.");
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[0]) break;
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[5]) break;
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[2]) break;
    adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[4], true, true, true);
    if (adjEdges.length != 0) throw new IllegalStateException("not 0 adj.");
    adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[2], true, false, true);
    if (adjEdges.length != 3) throw new IllegalStateException("not 3 adj.");
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[1]) break;
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[2]) break;
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[3]) break;
    adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[2], true, true, true);
    if (adjEdges.length != 4) throw new IllegalStateException("not 4 adj.");
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[6]) break;
    adjEdges = root.getAdjacentEdgeIndicesArray
      (nodeInx[1], true, false, false);
    if (adjEdges.length != 2) throw new IllegalStateException("not 2 adj.");
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[1]) break;
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[4]) break;
    adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[1], false, true, true);
    if (adjEdges.length != 2) throw new IllegalStateException("not 2 adj.");
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[0]) break;
    for (int i = 0;; i++) if (adjEdges[i] == edgeInx[5]) break;
    adjEdges = root.getAdjacentEdgeIndicesArray(nodeInx[3], true, true, false);
    if (adjEdges.length != 0) throw new IllegalStateException("not 0 adj.");
    int minEdgeInx = 0;
    for (int i = 0; i < edgeInx.length; i++)
      minEdgeInx = Math.min(minEdgeInx, edgeInx[i]);
    adjEdges = root.getAdjacentEdgeIndicesArray(99, true, true, true);
    if (adjEdges != null) throw new IllegalStateException("not null");
    adjEdges = root.getAdjacentEdgeIndicesArray
      (minEdgeInx - 1, true, true, true);
    if (adjEdges != null) throw new IllegalStateException("not null");
    adjEdges = root.getAdjacentEdgeIndicesArray(0, true, true, true);
    if (adjEdges != null) throw new IllegalStateException("not null");

    // getConnectingEdgeIndicesArray(int[]).
    int[] connEdges = root.getConnectingEdgeIndicesArray(nodeInx);
    if (connEdges.length != edgeInx.length)
      throw new IllegalStateException("edge arrays not same length");
    for (int i = 0; i < edgeInx.length; i++)
      for (int j = 0;; j++) if (connEdges[j] == edgeInx[i]) break;
    int[] someNodes = new int[] { nodeInx[0], nodeInx[2], nodeInx[3] };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges.length != 3)
      throw new IllegalStateException("not 3 connecting edges");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[2]) break;
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[3]) break;
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[6]) break;
    someNodes = new int[] { nodeInx[1], nodeInx[4], nodeInx[0], nodeInx[3] };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges.length != 3)
      throw new IllegalStateException("not 3 connecting edges");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[0]) break;
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[4]) break;
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[5]) break;
    someNodes = new int[] { nodeInx[2] };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges.length != 1)
      throw new IllegalStateException("not 1 connecting edge");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[3]) break;
    someNodes = new int[] { nodeInx[4], nodeInx[3], nodeInx[0] };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges.length != 0)
      throw new IllegalStateException("not 0 connecting edges");
    someNodes = new int[] { nodeInx[0], nodeInx[1], nodeInx[2], nodeInx[3] };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges.length != edgeInx.length)
      throw new IllegalStateException("edge arrays not same length");
    for (int i = 0; i < edgeInx.length; i++)
      for (int j = 0;; j++) if (connEdges[j] == edgeInx[i]) break;
    int minNodeInx = 0;
    for (int i = 0; i < nodeInx.length; i++)
      minNodeInx = Math.min(minNodeInx, nodeInx[i]);
    someNodes = new int[] { 99 };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges != null) throw new IllegalStateException("not null");
    someNodes = new int[] { nodeInx[0], nodeInx[1],
                            minNodeInx - 1, nodeInx[2] };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges != null) throw new IllegalStateException("not null");
    someNodes = new int[] { nodeInx[4], 0 };
    connEdges = root.getConnectingEdgeIndicesArray(someNodes);
    if (connEdges != null) throw new IllegalStateException("not null");

    // getConnectingNodeIndicesArray(int[]).
    int[] connNodes = root.getConnectingNodeIndicesArray(edgeInx);
    if (connNodes.length != 4)
      throw new IllegalStateException("not 4 connecting nodes");
    for (int i = 0; i < nodeInx.length; i++)
      if (i != 4)
        for (int j = 0;; j++) if (connNodes[j] == nodeInx[i]) break;
    int[] someEdges = new int[] { edgeInx[0], edgeInx[3], edgeInx[5] };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes.length != 3)
      throw new IllegalStateException("not 3 connecting nodes");
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[0]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[1]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[2]) break;
    someEdges = new int[] { edgeInx[6] };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes.length != 2)
      throw new IllegalStateException("not 2 connecting nodes");
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[2]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[3]) break;
    someEdges = new int[] { edgeInx[4], edgeInx[3] };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes.length != 2)
      throw new IllegalStateException("not 2 connecting nodes");
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[1]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[2]) break;
    someEdges = new int[] { edgeInx[5], edgeInx[6] };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes.length != 4)
      throw new IllegalStateException("not 4 connecting nodes");
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[0]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[1]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[2]) break;
    for (int i = 0;; i++) if (connNodes[i] == nodeInx[3]) break;
    someEdges = new int[] { 99 };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes != null) throw new IllegalStateException("not null");
    someEdges = new int[] { minEdgeInx - 1 };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes != null) throw new IllegalStateException("not null");
    someEdges = new int[] { edgeInx[0], 0, edgeInx[1] };
    connNodes = root.getConnectingNodeIndicesArray(someEdges);
    if (connNodes != null) throw new IllegalStateException("not null");
  }

}

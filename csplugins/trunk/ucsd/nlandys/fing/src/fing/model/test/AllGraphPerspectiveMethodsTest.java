package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;
import java.util.List;

public final class AllGraphPerspectiveMethodsTest
{

  // No constructor.
  private AllGraphPerspectiveMethodsTest() { }

  public static final void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final GraphPerspective persp =
      root.createGraphPerspective((int[]) null, (int[]) null);
    int[] nodeInx = new int[5];
    for (int i = 0; i < nodeInx.length - 1; i++)
      nodeInx[i] = root.createNode();
    int[] edgeInx = new int[7];
    edgeInx[0] = root.createEdge(nodeInx[0], nodeInx[1], true);
    edgeInx[1] = root.createEdge(nodeInx[1], nodeInx[2], false);
    edgeInx[2] = root.createEdge(nodeInx[2], nodeInx[0], true);
    edgeInx[3] = root.createEdge(nodeInx[2], nodeInx[2], true);
    edgeInx[4] = root.createEdge(nodeInx[1], nodeInx[1], false);
    edgeInx[5] = root.createEdge(nodeInx[1], nodeInx[0], true);
    edgeInx[6] = root.createEdge(nodeInx[3], nodeInx[2], true);
    nodeInx[nodeInx.length - 1] = root.createNode
      (null, new int[] { edgeInx[6], edgeInx[2] });
    if (!(root.addNodeMetaChild(nodeInx[0], nodeInx[1]) &&
          root.addNodeMetaChild(nodeInx[0], nodeInx[4]) &&
          root.addNodeMetaChild(nodeInx[3], nodeInx[1]) &&
          root.addNodeMetaChild(nodeInx[4], nodeInx[4]) &&
          root.addEdgeMetaChild(nodeInx[3], edgeInx[6]) &&
          root.addEdgeMetaChild(nodeInx[3], edgeInx[0]) &&
          root.addEdgeMetaChild(nodeInx[0], edgeInx[4])))
      throw new IllegalStateException("unable to create meta relationship");
    for (int i = 0; i < nodeInx.length; i++)
      if (persp.restoreNode(nodeInx[i]) != nodeInx[i])
        throw new IllegalStateException("unable to restore node");
    for (int i = 0; i < edgeInx.length; i++)
      if (persp.restoreEdge(edgeInx[i]) != edgeInx[i])
        throw new IllegalStateException("unable to restore edge");
    RootGraph root2 = FingRootGraphFactory.instantiateRootGraph();
    root2.createNode();
    root2.createEdge
      (((Node) root2.nodesIterator().next()).getRootGraphIndex(),
       ((Node) root2.nodesIterator().next()).getRootGraphIndex());
    final Node root2Node = (Node) root2.nodesIterator().next();
    final Edge root2Edge = (Edge) root2.edgesIterator().next();
    final Node nodeNotInPersp = root.getNode(root.createNode());
    final Edge edge1NotInPersp = root.getEdge
      (root.createEdge(nodeInx[1], nodeInx[3], true));
    final Edge edge2NotInPersp = root.getEdge
      (root.createEdge(nodeInx[2], nodeNotInPersp.getRootGraphIndex(), false));
    root.addNodeMetaChild(nodeInx[2], nodeNotInPersp.getRootGraphIndex());
    root.addEdgeMetaChild(nodeInx[3], edge1NotInPersp.getRootGraphIndex());
    int[] rootNodeInx = root.getNodeIndicesArray();
    int minNodeInx = 0;
    for (int i = 0; i < rootNodeInx.length; i++)
      minNodeInx = Math.min(minNodeInx, rootNodeInx[i]);
    int[] rootEdgeInx = root.getEdgeIndicesArray();
    int minEdgeInx = 0;
    for (int i = 0; i < rootEdgeInx.length; i++)
      minEdgeInx = Math.min(minEdgeInx, rootEdgeInx[i]);

    // Not testing GraphPerspectiveChangeListener methods.

    // clone().
    final GraphPerspective persp2 = (GraphPerspective) persp.clone();
    if (persp2.getNodeCount() != persp.getNodeCount() ||
        persp2.getEdgeCount() != persp.getEdgeCount())
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
    if (persp2.getNodeCount() != 0 || persp2.getEdgeCount() != 0)
      throw new IllegalStateException("nodes or edges remaining");

    // getRootGraph().
    if (persp.getRootGraph() != root || persp2.getRootGraph() != root)
      throw new IllegalStateException("incorrect RootGraph");

    // getNodeCount().
    if (persp.getNodeCount() != 5)
      throw new IllegalStateException("wrong number of nodes");

    // getEdgeCount().
    if (persp.getEdgeCount() != 7)
      throw new IllegalStateException("wrong number of edges");

    // nodesIterator().
    Iterator nodesIter = persp.nodesIterator();
    Node[] twoNodes = new Node[] { (Node) nodesIter.next(),
                                   (Node) nodesIter.next() };

    // nodesList().
    List nodesList = persp.nodesList();
    if (nodesList.size() != 5)
      throw new IllegalStateException("incorrect node List size");
    for (int i = 0; i < nodesList.size(); i++) {
      Node n = (Node) nodesList.get(i); }

    // getNodeIndicesArray().
    int[] nodeIndicesArray = persp.getNodeIndicesArray();
    if (nodeIndicesArray.length != nodesList.size() + 1)
      throw new IllegalStateException
        ("size of nodes List and length of node indices array don't match");
    if (nodeIndicesArray[0] != 0)
      throw new IllegalStateException("expected 0 at index 0");
    for (int j = 0; j < nodeInx.length; j++) {
      for (int i = 1;; i++) { if (nodeIndicesArray[i] == nodeInx[j]) break; } }

    // edgesIterator().
    Iterator edgesIter = persp.edgesIterator();
    Edge[] twoEdges = new Edge[] { (Edge) edgesIter.next(),
                                   (Edge) edgesIter.next() };

    // edgesList().
    List edgesList = persp.edgesList();
    if (edgesList.size() != 7)
      throw new IllegalStateException("incorrect edge List size");
    for (int i = 0; i < edgesList.size(); i++) {
      Edge e = (Edge) edgesList.get(i); }

    // getEdgeIndicesArray().
    int[] edgeIndicesArray = persp.getEdgeIndicesArray();
    if (edgeIndicesArray.length != edgesList.size() + 1)
      throw new IllegalStateException
        ("size of edges List and length of edge indices array don't match");
    for (int j = 0; j < edgeInx.length; j++) {
      for (int i = 1;; i++) { if (edgeIndicesArray[i] == edgeInx[j]) break; } }

    // getEdgeIndicesArray(int, int, boolean, boolean).
    int[] connEdges;
    connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[0], false, true);
    if (connEdges.length != 2)
      throw new IllegalStateException("not 2 connecting edges");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[0]) break;
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[5]) break;
    connEdges = persp.getEdgeIndicesArray(nodeInx[0], nodeInx[3], true, true);
    if (connEdges.length != 0)
      throw new IllegalStateException("not 0 connecting edges");
    connEdges = persp.getEdgeIndicesArray(nodeInx[1], nodeInx[2], false, true);
    if (connEdges.length != 0)
      throw new IllegalStateException("not 0 connecting edges");
    connEdges = persp.getEdgeIndicesArray(nodeInx[2], nodeInx[1], true, false);
    if (connEdges.length != 1)
      throw new IllegalStateException("not 1 connecting edge");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[1]) break;
    connEdges =
      persp.getEdgeIndicesArray(nodeInx[2], nodeInx[2], false, false);
    if (connEdges.length != 1)
      throw new IllegalStateException("not 1 connecting edge");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[3]) break;
    connEdges = persp.getEdgeIndicesArray(nodeInx[2], nodeInx[2], true, true);
    if (connEdges.length != 1)
      throw new IllegalStateException("not 1 connecting edge");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[3]) break;
    connEdges =
      persp.getEdgeIndicesArray(nodeInx[2], nodeInx[3], false, false);
    if (connEdges.length != 0)
      throw new IllegalStateException("not 0 connecting edges");
    connEdges =
      persp.getEdgeIndicesArray(nodeInx[3], nodeInx[2], false, false);
    if (connEdges.length != 1)
      throw new IllegalStateException("not 1 connecting edge");
    for (int i = 0;; i++) if (connEdges[i] == edgeInx[6]) break;
    connEdges = persp.getEdgeIndicesArray(nodeInx[4], nodeInx[0], true, true);
    if (connEdges.length != 0)
      throw new IllegalStateException("not 0 connecting edges");
    connEdges = persp.getEdgeIndicesArray(99, 0, true, true);
    if (connEdges != null) throw new IllegalStateException("not null");
    connEdges = persp.getEdgeIndicesArray(nodeInx[0], minNodeInx - 1,
                                         true, false);
    if (connEdges != null) throw new IllegalStateException("not null");
    if (persp.getEdgeIndicesArray(Integer.MAX_VALUE, Integer.MIN_VALUE,
                                 true, false) != null ||
        persp.getEdgeIndicesArray(Integer.MIN_VALUE, Integer.MAX_VALUE,
                                 false, false) != null ||
        persp.getEdgeIndicesArray(nodeInx[0],
                                  nodeNotInPersp.getRootGraphIndex(),
                                  false, true) != null)
      throw new IllegalStateException("not null");

    // hide/restore mothods are tested elsewhere.

    // containsNode(Node).
    if (!persp.containsNode(twoNodes[1]))
      throw new IllegalStateException("GraphPersp does not contain node");
    if (persp.containsNode(root2Node))
      throw new IllegalStateException("GraphPersp contains node from other");
    if (persp.containsNode(nodeNotInPersp) ||
        !persp.getRootGraph().containsNode(nodeNotInPersp))
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
    if (persp.containsEdge(edge1NotInPersp) ||
        persp.containsEdge(edge2NotInPersp) ||
        !(persp.getRootGraph().containsEdge(edge1NotInPersp) &&
          persp.getRootGraph().containsEdge(edge2NotInPersp)))
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
      if (nodeIndex == 0) throw new IllegalStateException("node index is 0");
      int index = -1;
      while (true) {
        if (neighInx[++index] != 0) {
          if (neighInx[index] == nodeIndex)
            throw new IllegalStateException("duplicate neighbor");
          else continue; }
        else { neighInx[index] = nodeIndex; break; } } }
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
    for (int i = 0;; i++) if (neighInx[i] == nodeInx[0]) break;
    for (int i = 0;; i++) if (neighInx[i] == nodeInx[1]) break;
    for (int i = 0;; i++) if (neighInx[i] == nodeInx[2]) break;
    neighInx = persp.neighborsArray(nodeInx[4]);
    if (neighInx.length != 0)
      throw new IllegalStateException("wrong number of neighbors");
    if (persp.neighborsArray(nodeNotInPersp.getRootGraphIndex()) != null ||
        persp.neighborsArray(Integer.MIN_VALUE) != null ||
        persp.neighborsArray(Integer.MAX_VALUE) != null ||
        persp.neighborsArray(0) != null ||
        persp.neighborsArray(1) != null ||
        persp.neighborsArray(minNodeInx - 1) != null)
      throw new IllegalStateException("expected null");

    // isNeighbor(Node, Node).
    if (persp.isNeighbor(persp.getNode(nodeInx[4]), persp.getNode(nodeInx[4])))
      throw new IllegalStateException("node with no edges is its own neigh");
    if (persp.isNeighbor(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[1])))
      throw new IllegalStateException("nodes are neighbors");
    if (!persp.isNeighbor(persp.getNode(nodeInx[1]),
                          persp.getNode(nodeInx[0])))
      throw new IllegalStateException("nodes are not neighbors");
    if (persp.isNeighbor(root2Node, persp.getNode(nodeInx[2])))
      throw new IllegalStateException("nodes from another graph is neighbor");
    if (persp.isNeighbor(persp.getNode(nodeInx[0]), nodeNotInPersp))
      throw new IllegalStateException("neighbor with node not in GraphPersp");

    // isNeighbor(int, int).
    if (persp.isNeighbor(nodeInx[1], nodeInx[3]) ||
        !root.isNeighbor(nodeInx[1], nodeInx[3]))
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
    if (persp.isNeighbor(Integer.MAX_VALUE, Integer.MIN_VALUE) ||
        persp.isNeighbor(Integer.MIN_VALUE, Integer.MAX_VALUE) ||
        persp.isNeighbor(minNodeInx - 1, nodeInx[0]) ||
        persp.isNeighbor(0, 1))
      throw new IllegalStateException("extreme neighbors");

    // edgeExists(Node, Node).
    if (persp.edgeExists(persp.getNode(nodeInx[3]), persp.getNode(nodeInx[1])))
      throw new IllegalStateException("edge exists");
    if (persp.edgeExists(persp.getNode(nodeInx[0]), root2Node))
      throw new IllegalStateException("edge exists with node of other graph");
    if (!persp.edgeExists(persp.getNode(nodeInx[0]),
                          persp.getNode(nodeInx[1])))
      throw new IllegalStateException("edge does not exist");
    if (!persp.edgeExists(persp.getNode(nodeInx[1]),
                          persp.getNode(nodeInx[2])))
      throw new IllegalStateException("edge does not exist");
    if (persp.edgeExists(persp.getNode(nodeInx[2]), nodeNotInPersp) ||
        !root.edgeExists(persp.getNode(nodeInx[2]), nodeNotInPersp))
      throw new IllegalStateException("bad edgeExists");

    // edgeExists(int, int).
    if (persp.edgeExists(nodeInx[1], nodeInx[3]))
      throw new IllegalStateException("edge exists in RootGraph, not persp");
    if (persp.edgeExists(minNodeInx - 1, nodeInx[1]))
      throw new IllegalStateException("bad edgeExists()");
    if (persp.edgeExists(0, 0)) throw new IllegalStateException("0 -> 0");
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
    if (persp.edgeExists(Integer.MAX_VALUE, Integer.MIN_VALUE) ||
        persp.edgeExists(Integer.MIN_VALUE, Integer.MAX_VALUE))
      throw new IllegalStateException("MIN_VALUE and MAX_VALUE edge exists");
  }

}

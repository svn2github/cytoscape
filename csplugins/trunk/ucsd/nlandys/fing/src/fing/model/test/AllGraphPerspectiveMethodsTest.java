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
  }

}

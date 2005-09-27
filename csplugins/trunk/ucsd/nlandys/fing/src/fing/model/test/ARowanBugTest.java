package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

public class ARowanBugTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int n1 = root.createNode();
    final int n2 = root.createNode();
    final int e1 = root.createEdge(n1, n1, /* directed */ true);
    final GraphPerspective persp =
      root.createGraphPerspective((int[]) null, (int[]) null);
    if (persp.restoreEdge(e1) == 0)
      throw new IllegalStateException("could not restore valid edge");
    final GraphPerspective persp2 =
      root.createGraphPerspective(null, new int[] { e1 });
    if (!(persp2.getNodeCount() == 1 && persp2.getEdgeCount() == 1))
      throw new IllegalStateException("bad counts in perspective");
  }

}

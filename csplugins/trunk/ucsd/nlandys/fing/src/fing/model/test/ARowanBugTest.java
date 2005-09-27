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
    final Node n1 = root.getNode(root.createNode());
    final Node n2 = root.getNode(root.createNode());
    final Edge e1 = root.getEdge(root.createEdge(n1, n1,
                                                 /* directed */ true));
    final GraphPerspective persp = root.createGraphPerspective
      ((int[]) null, (int[]) null);
    if (persp.restoreEdge(e1) == null)
      throw new IllegalStateException("could not restore valid edge");
  }

}

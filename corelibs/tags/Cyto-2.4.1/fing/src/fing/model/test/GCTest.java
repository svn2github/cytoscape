package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.GraphPerspective;
import giny.model.RootGraph;

public class GCTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int[] nodeInx = root.createNodes(10000);
    final int[] edgeInx = new int[100000];
    for (int i = 0; i < edgeInx.length; i++)
      edgeInx[i] = root.createEdge(nodeInx[i % nodeInx.length],
                                   nodeInx[(i * 3) % nodeInx.length]);
    System.out.println("RootGraph node count: " + root.getNodeCount());
    System.out.println("RootGraph edge count: " + root.getEdgeCount());
    System.out.println();
    for (int i = 0; i < 1000; i++) {
      GraphPerspective persp = root.createGraphPerspective(nodeInx, edgeInx);
      System.out.println("GraphPerspective node count: " +
                         persp.getNodeCount());
      System.out.println("GraphPerspective edge count: " +
                         persp.getEdgeCount());
      System.out.println(); }
  }

}

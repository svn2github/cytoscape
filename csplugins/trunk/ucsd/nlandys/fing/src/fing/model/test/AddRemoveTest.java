package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.GraphPerspective;
import giny.model.RootGraph;

public class AddRemoveTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    final int[] nodeInx = root.createNodes(10);
    final int[] edgeInx = new int[40];
    for (int i = 0; i < edgeInx.length; i++)
      edgeInx[i] = root.createEdge(nodeInx[i % nodeInx.length],
                                   nodeInx[(i * 3) % nodeInx.length]);
    final GraphPerspective persp1 =
      root.createGraphPerspective(nodeInx, edgeInx);
    final GraphPerspective persp2 =
      root.createGraphPerspective(nodeInx, edgeInx);
    printme(root, persp1, persp2);
    System.out.println("hiding edge " + edgeInx[2] + " in GraphPerspective 1");
    persp1.hideEdge(edgeInx[0]);
    printme(root, persp1, persp2);
    System.out.println("hiding node " + nodeInx[0] + " in GraphPerspective 1");
    persp1.hideNode(nodeInx[0]);
    printme(root, persp1, persp2);
    System.out.println("hiding node " + nodeInx[1] + " and edge " +
                       edgeInx[3] + " in GraphPerspective 2");
    persp2.hideNode(nodeInx[1]); persp2.hideEdge(edgeInx[3]);
    printme(root, persp1, persp2);
    System.out.println("removing node " + nodeInx[2] + " and edge " +
                       edgeInx[8] + " in RootGraph");
    root.removeNode(nodeInx[2]); root.removeEdge(edgeInx[8]);
    printme(root, persp1, persp2);
    System.out.println("removing all edges from RootGraph");
    for (int i = 0; i < edgeInx.length; i++)
      root.removeEdge(edgeInx[i]);
    printme(root, persp1, persp2);
    System.out.println("removing all nodes from RootGraph");
    for (int i = 0; i < nodeInx.length; i++)
      root.removeNode(nodeInx[i]);
    printme(root,persp1, persp2);
  }

  private static void printme(RootGraph root,
                              GraphPerspective persp1,
                              GraphPerspective persp2)
  {
    System.out.println("in RootGraph: " + root.getNodeCount() + " nodes and " +
                       root.getEdgeCount() + " edges");
    System.out.println("in GraphPerspective 1: " + persp1.getNodeCount() +
                       " nodes and " + persp1.getEdgeCount() + " edges");
    System.out.println("in GraphPerspective 2: " + persp2.getNodeCount() +
                       " nodes and " + persp2.getEdgeCount() + " edges");
    System.out.println();
  }

}

package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.RootGraph;

import java.util.Iterator;

public class AddRemoveTest
{

  public static void main(String[] args)
  {
    final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
    int[] nodeInx = root.createNodes(3);
    int[] edgeInx = new int[5];
    for (int i = 0; i < edgeInx.length; i++)
      edgeInx[i] = root.createEdge(nodeInx[i % nodeInx.length],
                                   nodeInx[(i * 2) % nodeInx.length]);
    final GraphPerspective persp1 =
      root.createGraphPerspective(nodeInx, edgeInx);
    final GraphPerspective persp2 =
      root.createGraphPerspective(nodeInx, edgeInx);
    printme(root, persp1, persp2);

    int hideEdge = edgeInx[2];
    System.out.println("hiding edge " + hideEdge + " in GraphPerspective 1");
    persp1.hideEdge(hideEdge);
    printme(root, persp1, persp2);

    int hideNode = nodeInx[0];
    System.out.println("hiding node " + hideNode + " in GraphPerspective 1");
    persp1.hideNode(hideNode);
    printme(root, persp1, persp2);

    hideNode = nodeInx[1];
    hideEdge = edgeInx[3];
    System.out.println("hiding node " + hideNode + " and edge " +
                       hideEdge + " in GraphPerspective 2");
    persp2.hideNode(hideNode); persp2.hideEdge(hideEdge);
    printme(root, persp1, persp2);

    int removeNode = nodeInx[2];
    int removeEdge = edgeInx[0];
    System.out.println("removing node " + removeNode + " and edge " +
                       removeEdge + " in RootGraph");
    root.removeNode(removeNode); root.removeEdge(removeEdge);
    printme(root, persp1, persp2);

    System.out.println("removing all edges from RootGraph");
    for (int i = 0; i < edgeInx.length; i++)
      root.removeEdge(edgeInx[i]);
    printme(root, persp1, persp2);

    System.out.println("removing all nodes from RootGraph");
    for (int i = 0; i < nodeInx.length; i++)
      root.removeNode(nodeInx[i]);
    printme(root,persp1, persp2);

    edgeInx = new int[100000];
    final int[] nodeNums = new int[] { 10000, 9998, 10001 };
    for (int foo = 0; foo < 100000; foo++) {
      boolean print = false;
      if (foo % 1 == 0)  print = true;
      if (print)
        System.out.println("at add/remove iteration " + foo + " of 100,000");
      System.out.println("creating nodes");
      nodeInx = root.createNodes(nodeNums[foo % nodeNums.length]);
      System.out.println("creating edges");
      for (int i = 0; i < edgeInx.length; i++)
        edgeInx[i] = root.createEdge(nodeInx[i % nodeInx.length],
                                     nodeInx[(i * 3) % nodeInx.length]);
      System.out.println("creating GraphPerspective");
      GraphPerspective persp = root.createGraphPerspective(nodeInx, edgeInx);
      if (print) {
        System.out.println("in RootGraph: " + root.getNodeCount() +
                           " nodes and " + root.getEdgeCount() + " edges");
        System.out.println("in GraphPerspective: " + persp.getNodeCount() +
                           " nodes and " + persp.getEdgeCount() + " edges");
        System.out.println(); }
      if (print) {
        System.out.println("removing all edges and nodes from RootGraph"); }
      System.out.println("removing edges");
      for (int i = 0; i < edgeInx.length; i++)
        root.removeEdge(edgeInx[i]);
      System.out.println("removing nodes");
      for (int i = 0; i < nodeInx.length; i++)
        root.removeNode(nodeInx[i]);
      if (print) {
        System.out.println("in RootGraph: " + root.getNodeCount() +
                           " nodes and " + root.getEdgeCount() + " edges");
        System.out.println("in GraphPerspective: " + persp.getNodeCount() +
                           " nodes and " + persp.getEdgeCount() + " edges");
        System.out.println(); } }
  }

  private static void printme(RootGraph root,
                              GraphPerspective persp1,
                              GraphPerspective persp2)
  {
    System.out.println("in RootGraph: " + root.getNodeCount() + " nodes and " +
                       root.getEdgeCount() + " edges");
    Iterator iter = root.nodesIterator();
    System.out.print("  nodes: ");
    while (iter.hasNext())
      System.out.print(((Node) iter.next()).getRootGraphIndex() + " ");
    System.out.println();
    iter = root.edgesIterator();
    System.out.print("  edges: ");
    while (iter.hasNext()) {
      Edge e = ((Edge) iter.next());
      System.out.print
        (e.getRootGraphIndex() + "(" + e.getSource().getRootGraphIndex() +
         "," + e.getTarget().getRootGraphIndex() + ") "); }
    System.out.println();

    System.out.println("in GraphPerspective 1: " + persp1.getNodeCount() +
                       " nodes and " + persp1.getEdgeCount() + " edges");
    iter = persp1.nodesIterator();
    System.out.print("  nodes: ");
    while (iter.hasNext())
      System.out.print(((Node) iter.next()).getRootGraphIndex() + " ");
    System.out.println();
    iter = persp1.edgesIterator();
    System.out.print("  edges: ");
    while (iter.hasNext())
      System.out.print(((Edge) iter.next()).getRootGraphIndex() + " ");
    System.out.println();

    System.out.println("in GraphPerspective 2: " + persp2.getNodeCount() +
                       " nodes and " + persp2.getEdgeCount() + " edges");
    iter = persp2.nodesIterator();
    System.out.print("  nodes: ");
    while (iter.hasNext())
      System.out.print(((Node) iter.next()).getRootGraphIndex() + " ");
    System.out.println();
    iter = persp2.edgesIterator();
    System.out.print("  edges: ");
    while (iter.hasNext())
      System.out.print(((Edge) iter.next()).getRootGraphIndex() + " ");
    System.out.println();

    System.out.println();
  }

}

package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphRepresentation;
import cytoscape.util.intr.IntEnumerator;

public class GraphTest
{

  public static void main(String[] args)
  {
    final DynamicGraph graph = new DynamicGraphRepresentation();
    System.out.println("Creating 10 nodes...");
    for (int i = 0; i < 10; i++) graph.createNode();
    IntEnumerator nodesEnum = graph.nodes();
    int index = -1;
    int[] nodes = new int[nodesEnum.numRemaining()];
    System.out.print("Here are the nodes: ");
    while (nodesEnum.numRemaining() > 0) {
      nodes[++index] = nodesEnum.nextInt();
      System.out.print(nodes[index] + " "); }
    System.out.println(); System.out.println();
    boolean[] edgesDir = new boolean[] { false, true, true, true, false,
                                         true, false, false, true, true,
                                         false, false, true, false, true };
    int[][] edgesDef = new int[][] { {2,5}, {0,8}, {4,1}, {9,0}, {9,0},
                                     {0,8}, {1,4}, {2,2}, {7,7}, {1,1},
                                     {3,1}, {7,2}, {1,0}, {8,5}, {4,9} };
    for (int i = 0; i < edgesDir.length; i++) {
      System.out.println
        ("Creating " + (edgesDir[i] ? "directed" : "undirected") +
         " edge from node " + nodes[edgesDef[i][0]] + " to node " +
         nodes[edgesDef[i][1]] + "...");
      graph.createEdge(nodes[edgesDef[i][0]],
                       nodes[edgesDef[i][1]], edgesDir[i]); }
    IntEnumerator edgesEnum = graph.edges();
    System.out.println();
    System.out.println("Here are the edges:");
    while (edgesEnum.numRemaining() > 0) {
      final int edge = edgesEnum.nextInt();
      System.out.println
        ((graph.isDirectedEdge(edge) == 1 ? "Directed" : "Undirected") +
         " edge " + edge + " with source " + graph.sourceNode(edge) +
         " and target " + graph.targetNode(edge) + "."); }
    System.out.println();
    System.out.println("All adjacent edges...");
    nodesEnum = graph.nodes();
    while (nodesEnum.numRemaining() > 0) {
      final int node = nodesEnum.nextInt();
      IntEnumerator adjEdges = graph.adjacentEdges(node, true, true, true);
      System.out.print("For node " + node + ": ");
      while (adjEdges.numRemaining() > 0) {
        final int edge = adjEdges.nextInt();
        System.out.print(edge + " "); }
      System.out.println(); }
    System.out.println();
    System.out.println("All undirected adjacent edges...");
    nodesEnum = graph.nodes();
    while (nodesEnum.numRemaining() > 0) {
      final int node = nodesEnum.nextInt();
      IntEnumerator adjEdges = graph.adjacentEdges(node, false, false, true);
      System.out.print("For node " + node + ": ");
      while (adjEdges.numRemaining() > 0) {
        final int edge = adjEdges.nextInt();
        System.out.print(edge + " "); }
      System.out.println(); }
    System.out.println();
    System.out.println
      ("All undirected and incoming adjacent edges...");
    nodesEnum = graph.nodes();
    while (nodesEnum.numRemaining() > 0) {
      final int node = nodesEnum.nextInt();
      IntEnumerator adjEdges = graph.adjacentEdges(node, false, true, true);
      System.out.print("For node " + node + ": ");
      while (adjEdges.numRemaining() > 0) {
        final int edge = adjEdges.nextInt();
        System.out.print(edge + " "); }
      System.out.println(); }
    System.out.println();
    System.out.println("All outgoing adjacent edges...");
    nodesEnum = graph.nodes();
    while (nodesEnum.numRemaining() > 0) {
      final int node = nodesEnum.nextInt();
      IntEnumerator adjEdges = graph.adjacentEdges(node, true, false, false);
      System.out.print("For node " + node + ": ");
      while (adjEdges.numRemaining() > 0) {
        final int edge = adjEdges.nextInt();
        System.out.print(edge + " "); }
      System.out.println(); }
    System.out.println();
    System.out.println("All outgoing and incoming adjacent edges...");
    nodesEnum = graph.nodes();
    while (nodesEnum.numRemaining() > 0) {
      final int node = nodesEnum.nextInt();
      IntEnumerator adjEdges = graph.adjacentEdges(node, true, true, false);
      System.out.print("For node " + node + ": ");
      while (adjEdges.numRemaining() > 0) {
        final int edge = adjEdges.nextInt();
        System.out.print(edge + " "); }
      System.out.println(); }
    System.out.println();
    for (int i = 0; i < nodes.length; i++)
      if (i % 3 == 0) {
        System.out.println("Removing node " + nodes[i] + "...");
        graph.removeNode(nodes[i]); }
    System.out.println();
    System.out.println("All adjacent edges...");
    nodesEnum = graph.nodes();
    while (nodesEnum.numRemaining() > 0) {
      final int node = nodesEnum.nextInt();
      IntEnumerator adjEdges = graph.adjacentEdges(node, true, true, true);
      System.out.print("For node " + node + ": ");
      while (adjEdges.numRemaining() > 0) {
        final int edge = adjEdges.nextInt();
        System.out.print(edge + " "); }
      System.out.println(); }
    System.out.println();    
  }

}

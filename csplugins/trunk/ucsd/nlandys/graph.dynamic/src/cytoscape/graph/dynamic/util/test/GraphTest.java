package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphRepresentation;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.MinIntHeap;

public class GraphTest
{

  public static void main(String[] args)
  {
    final DynamicGraph graph = new DynamicGraphRepresentation();
    final MinIntHeap heap = new MinIntHeap();
    System.out.println("Creating 10 nodes...");
    for (int i = 0; i < 10; i++) graph.createNode();
    IntEnumerator nodesEnum = graph.nodes();
    int index = -1;
    int[] nodes = new int[nodesEnum.numRemaining()];
    System.out.print("Here are the nodes: ");
    while (nodesEnum.numRemaining() > 0) {
      nodes[++index] = nodesEnum.nextInt();
      System.out.print(nodes[index] + " "); }
    System.out.println();
    boolean[] edgesDir = new boolean[] { false, true, true, true, false,
                                         true, false, false, true, true,
                                         false, false, true, false, true };
    int[][] edgesDef = new int[][] { {2,5}, {0,8}, {4,1}, {9,0}, {9,0},
                                     {0,8}, {1,4}, {2,2}, {7,7}, {8,1},
                                     {3,1}, {7,2}, {1,0}, {8,5}, {4,9} };
    for (int i = 0; i < edgesDir.length; i++) {
      System.out.println
        ("Creating " + (edgesDir[i] ? "directed" : "undirected") +
         " edge from node " + nodes[edgesDef[i][0]] + " to node " +
         nodes[edgesDef[i][1]] + "...");
      graph.createEdge(nodes[edgesDef[i][0]],
                       nodes[edgesDef[i][1]], edgesDir[i]); }
    IntEnumerator edgesEnum = graph.edges();
    System.out.println("Here are the edges:");
    while (edgesEnum.numRemaining() > 0) {
      final int edge = edgesEnum.nextInt();
      System.out.println
        ((graph.isDirectedEdge(edge) == 1 ? "Directed" : "Undirected") +
         " edge " + edge + " with source " + graph.sourceNode(edge) +
         " and target " + graph.targetNode(edge) + "."); }
  }

}

package fing.model.test;

import fing.model.FingRootGraphFactory;
import giny.model.RootGraph;
import java.io.IOException;
import java.io.InputStream;

public final class RootGraphPerformanceTest
{

  // No constructor.
  private RootGraphPerformanceTest() { }

  // Args:
  //   <numNodes> <numDirectedEdges> <numUndirectedEdges> [luna]
  public static final void main(String[] args)
    throws ClassNotFoundException, InstantiationException,
           IllegalAccessException, IOException
  {
    final RootGraph root = getRootGraph(args);
    final int[] nodes =
      createNodes(root, Integer.parseInt(args[0]));
    final int[] directedEdges =
      createEdges(root, Integer.parseInt(args[1]), System.in, nodes, true);
    final int[] undirectedEdges =
      createEdges(root, Integer.parseInt(args[2]), System.in, nodes, false);
    long millisBegin = System.currentTimeMillis();
    testAdjacentEdges(root, nodes);
    long millisEnd = System.currentTimeMillis();
    System.out.println("adjacent edges test took " +
                       (millisEnd - millisBegin) + " milliseconds");
    long millisBegin2 = System.currentTimeMillis();
    testConnectingEdges(root, nodes);
    long millisEnd2 = System.currentTimeMillis();
    System.out.println("connecting edges test took " +
                       (millisEnd2 - millisBegin2) + " milliseconds");
    long millisBegin3 = System.currentTimeMillis();
    testNodeNeighbors(root, nodes);
    long millisEnd3 = System.currentTimeMillis();
    System.out.println("node neighbors test took " +
                       (millisEnd3 - millisBegin3) + " milliseconds");
    long millisBegin4 = System.currentTimeMillis();
    testConnectingWeb(root, nodes);
    long millisEnd4 = System.currentTimeMillis();
    System.out.println("connecting web test took " +
                       (millisEnd4 - millisBegin4) + " milliseconds");
  }

  private static final RootGraph getRootGraph(String[] mainArgs)
    throws ClassNotFoundException, InstantiationException,
           IllegalAccessException {
    if (mainArgs.length > 3 && mainArgs[3].equalsIgnoreCase("luna"))
      return (RootGraph) Class.forName("luna.LunaRootGraph").newInstance();
    else return FingRootGraphFactory.instantiateRootGraph(); }

  private static final int[] createNodes(RootGraph root, int numNodes)
  {
    final int[] returnThis = new int[numNodes];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = root.createNode();
    return returnThis;
  }

  private static final int[] createEdges(RootGraph root, int numEdges,
                                         InputStream in, int[] nodes,
                                         boolean directed)
    throws IOException
  {
    final int[] returnThis = new int[numEdges];
    byte[] buff = new byte[8];
    int inx = 0;
    int off = 0;
    int read;
    while (inx < numEdges &&
           (read = in.read(buff, off, buff.length - off)) > 0) {
      off += read;
      if (off < buff.length) continue;
      else off = 0;
      long randomLong = assembleLong(buff);
      int randomInt1 = (int) ((randomLong >> 32) & 0x00000000ffffffff);
      int randomInt2 = (int) (randomLong & 0x00000000ffffffff);
      int node1 = Math.abs(randomInt1) % nodes.length;
      int node2 = Math.abs(randomInt2) % nodes.length;
      returnThis[inx++] =
        root.createEdge(nodes[node1], nodes[node2],
                        directed && (node1 != node2)); }
    if (inx < numEdges) throw new IOException("premature end of input");
    return returnThis;
  }

  private static final long assembleLong(byte[] eightConsecutiveBytes)
  {
    long firstByte =
      (((long) eightConsecutiveBytes[0]) & 0x00000000000000ff) << 56;
    long secondByte =
      (((long) eightConsecutiveBytes[1]) & 0x00000000000000ff) << 48;
    long thirdByte =
      (((long) eightConsecutiveBytes[2]) & 0x00000000000000ff) << 40;
    long fourthByte =
      (((long) eightConsecutiveBytes[3]) & 0x00000000000000ff) << 32;
    long fifthByte =
      (((long) eightConsecutiveBytes[4]) & 0x00000000000000ff) << 24;
    long sixthByte =
      (((long) eightConsecutiveBytes[5]) & 0x00000000000000ff) << 16;
    long seventhByte =
      (((long) eightConsecutiveBytes[6]) & 0x00000000000000ff) << 8;
    long eighthByte =
      (((long) eightConsecutiveBytes[7]) & 0x00000000000000ff);
    return firstByte | secondByte | thirdByte | fourthByte |
      fifthByte | sixthByte | seventhByte | eighthByte;
  }

  // This test actually fail with luna when calling several combinations:
  // 1. RootGraph.getAdjacentEdgeIndicesArray(true, false, false)
  // 2. RootGraph.getAdjacentEdgeIndicesArray(true, true, false)
  // 3. RootGraph.getAdjacentEdgeIndicesArray(false, true, true)
  // 4. RootGraph.getAdjacentEdgeIndicesArray(false, true, false)
  // -- throws a NullPointerException.
  public static final void testAdjacentEdges(RootGraph root, int[] nodes)
  {
    for (int i = 0; i < 4; i++) {
      boolean undirected;
      boolean incoming;
      boolean outgoing;
      if (i == 0) {
        undirected = true; incoming = true; outgoing = true; }
      else if (i == 1) {
        undirected = true; incoming = false; outgoing = true; }
      else if (i == 2) {
        undirected = false; incoming = false; outgoing = true; }
      else {
        undirected = false; incoming = false; outgoing = false; }
      for (int j = 0; j < nodes.length; j++) {
        root.getAdjacentEdgeIndicesArray
          (nodes[j], undirected, incoming, outgoing); } }
  }

  public static final void testConnectingEdges(RootGraph root, int[] nodes)
  {
    for (int i = 0; i < 4; i++) {
      boolean undirected;
      boolean bothDirections;
      if (i == 0) {
        undirected = true; bothDirections = true; }
      else if (i == 1) {
        undirected = true; bothDirections = false; }
      else if (i == 2) {
        undirected = false; bothDirections = true; }
      else {
        undirected = false; bothDirections = false; }
      for (int j = 1; j < nodes.length; j++) {
        root.getEdgeIndicesArray
          (nodes[j - 1], nodes[j], undirected, bothDirections); } }
  }

  public static final void testNodeNeighbors(RootGraph root, int[] nodes)
  {
    for (int j = 1; j < nodes.length; j++) {
      root.neighborsList(root.getNode(nodes[j])); }
  }

  public static final void testConnectingWeb(RootGraph root, int[] nodes)
  {
    final int[] nodesWeb = new int[nodes.length / 2];
    System.arraycopy(nodes, 0, nodesWeb, 0, nodesWeb.length);
    int numIters = Math.min(100, nodesWeb.length);
    for (int i = 0; i < numIters; i++) {
      root.getConnectingEdgeIndicesArray(nodesWeb);
      nodesWeb[i] = nodes[nodesWeb.length + i]; }
  }

}

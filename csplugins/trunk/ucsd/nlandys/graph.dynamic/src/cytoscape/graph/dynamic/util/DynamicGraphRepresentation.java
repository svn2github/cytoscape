package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;

class DynamicGraphRepresentation implements DynamicGraph
{

  private int m_nodeCount;
  private Node m_firstNode;
  private int m_edgeCount;
  private final NodeArray m_nodes;
  private final EdgeArray m_edges;
  private final IntStack m_freeNodes;
  private final IntStack m_freeEdges;
  private final EdgeDepot m_edgeDepot;
  private final NodeDepot m_nodeDepot;

  DynamicGraphRepresentation()
  {
    m_nodeCount = 0;
    m_firstNode = null;
    m_edgeCount = 0;
    m_nodes = new NodeArray();
    m_edges = new EdgeArray();
    m_freeNodes = new IntStack();
    m_freeEdges = new IntStack();
    m_edgeDepot = new EdgeDepot();
    m_nodeDepot = new NodeDepot();
  }

  public IntEnumerator nodes()
  {
    final int nodeCount = m_nodeCount;
    final Node firstNode = m_firstNode;
    return new IntEnumerator() {
        private int numRemaining = nodeCount;
        private Node node = firstNode;
        public int numRemaining() { return numRemaining; }
        public int nextInt() {
          final int returnThis = node.nodeId;
          node = node.nextNode;
          numRemaining--;
          return returnThis; } };
  }

  public IntEnumerator edges()
  {
    final int edgeCount = m_edgeCount;
    final Node firstNode = m_firstNode;
    return new IntEnumerator() {
        private int numRemaining = edgeCount;
        private Node node = firstNode;
        private Edge edge = null;
        public int numRemaining() { return numRemaining; }
        public int nextInt() {
          int returnThis;
          try { returnThis = edge.edgeId; }
          catch (NullPointerException e) {
            for (edge = node.firstOutEdge;
                 edge == null;
                 node = node.nextNode, edge = node.firstOutEdge) { }
            node = node.nextNode;
            returnThis = edge.edgeId; }
          edge = edge.nextOutEdge;
          numRemaining--;
          return returnThis; } };
  }

  public boolean removeNode(int node)
  {
    return false;
  }

  public int createNode()
  {
    return -1;
  }

  public boolean removeEdge(int edge)
  {
    final Edge e;
    try { e = m_edges.getEdgeAtIndex(edge); }
    catch (ArrayIndexOutOfBoundsException exc) {
      // edge is negative or Integer.MAX_VALUE.
      throw new IllegalArgumentException("edge is negative"); }
    if (e == null) return false;
    final Node source = m_nodes.getNodeAtIndex(e.sourceNode);
    final Node target = m_nodes.getNodeAtIndex(e.targetNode);
    try { e.prevOutEdge.nextOutEdge = e.nextOutEdge; }
    catch (NullPointerException exc) { source.firstOutEdge = e.nextOutEdge; }
    try { e.prevInEdge.nextInEdge = e.nextInEdge; }
    catch (NullPointerException exc) { target.firstInEdge = e.nextInEdge; }
    if (e.directed) { source.outDegree--; target.inDegree--; }
    else { source.undDegree--; target.undDegree--; }
    m_edges.setEdgeAtIndex(null, edge);
    m_freeEdges.push(edge);
    e.nextOutEdge = null; e.prevOutEdge = null;
    e.nextInEdge = null; e.prevInEdge = null;
    m_edgeDepot.recycleEdge(e);
    m_edgeCount--;
    return true;
  }

  public int createEdge(int sourceNode, int targetNode, boolean directed)
  {
    return -1;
  }

  public boolean containsNode(int node)
  {
    return false;
  }

  public boolean containsEdge(int edge)
  {
    return false;
  }

  public IntEnumerator adjacentEdges(int node, boolean undirected,
                                     boolean incoming, boolean outgoing)
  {
    return null;
  }

  public int sourceNode(int edge)
  {
    return -1;
  }

  public int targetNode(int edge)
  {
    return -1;
  }

  public byte isDirectedEdge(int edge)
  {
    return (byte) -1;
  }

}

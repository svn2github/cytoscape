package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;

class DynamicGraphRepresentation implements DynamicGraph
{

  private int m_nodeCount;
  private Node m_firstNode;
  private int m_edgeCount;
  private NodeArray m_nodes;
  private EdgeArray m_edges;
  private IntStack m_freeNodes;
  private IntStack m_freeEdges;
  private EdgeDepot m_edgeDepot;

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
          final int returnThis = node.m_nodeId;
          node = node.m_nextNode;
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
          try { returnThis = edge.m_edgeId; }
          catch (NullPointerException e) {
            for (edge = node.m_firstOutEdge;
                 edge == null;
                 node = node.m_nextNode, edge = node.m_firstOutEdge) { }
            node = node.m_nextNode;
            returnThis = edge.m_edgeId; }
          edge = edge.m_nextOutEdge;
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
    m_edges.setEdgeAtIndex(null, edge);
    m_freeEdges.push(edge);
    try { e.m_prevOutEdge.m_nextOutEdge = e.m_nextOutEdge; }
    catch (NullPointerException exc) { // e.m_prevOutEdge is null.
      m_nodes.getNodeAtIndex(e.m_sourceNode).m_firstOutEdge =
        e.m_nextOutEdge; }
    try { e.m_prevInEdge.m_nextInEdge = e.m_nextInEdge; }
    catch (NullPointerException exc) { // e.m_prevInEdge is null.
      m_nodes.getNodeAtIndex(e.m_targetNode).m_firstInEdge = e.m_nextInEdge; }
    e.m_nextOutEdge = null; e.m_prevOutEdge = null;
    e.m_nextInEdge = null; e.m_prevInEdge = null;
    m_edgeDepot.recycleEdge(e);
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

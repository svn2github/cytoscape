package cytoscape.graph.dynamic.util;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;

class DynamicGraphRepresentation implements DynamicGraph
{

  private int m_nodeCount;
  private Node m_firstNode;
  private int m_maxNode;
  private int m_edgeCount;
  private int m_maxEdge;
  private final NodeArray m_nodes;
  private final EdgeArray m_edges;
  private final IntStack m_freeNodes;
  private final IntStack m_freeEdges;
  private final EdgeDepot m_edgeDepot;
  private final NodeDepot m_nodeDepot;

  // Use this as a bag of integers in various operations.  Don't forget to
  // empty() it before using it.
  private final IntStack m_stack;

  DynamicGraphRepresentation()
  {
    m_nodeCount = 0;
    m_firstNode = null;
    m_maxNode = -1;
    m_edgeCount = 0;
    m_maxEdge = -1;
    m_nodes = new NodeArray();
    m_edges = new EdgeArray();
    m_freeNodes = new IntStack();
    m_freeEdges = new IntStack();
    m_edgeDepot = new EdgeDepot();
    m_nodeDepot = new NodeDepot();
    m_stack = new IntStack();
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
          catch (NullPointerException exc) {
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
    IntEnumerator edges = adjacentEdges(node, true, true, true);
    if (edges == null) return false;
    m_stack.empty();
    while (edges.numRemaining() > 0) m_stack.push(edges.nextInt());
    while (m_stack.size() > 0) removeEdge(m_stack.pop());
    final Node n = m_nodes.getNodeAtIndex(node);
    try { n.prevNode.nextNode = n.nextNode; }
    catch (NullPointerException exc) { m_firstNode = n.nextNode; }
    try { n.nextNode.prevNode = n.prevNode; }
    catch (NullPointerException exc) { }
    m_nodes.setNodeAtIndex(null, node);
    m_freeNodes.push(node);
    n.prevNode = null; n.firstOutEdge = null; n.firstInEdge = null;
    m_nodeDepot.recycleNode(n);
    m_nodeCount--;
    return true;
  }

  public int createNode()
  {
    m_nodeCount++;
    final Node n = m_nodeDepot.getNode();
    final int returnThis;
    if (m_freeNodes.size() > 0) returnThis = m_freeNodes.pop();
    else returnThis = ++m_maxNode;
    m_nodes.setNodeAtIndex(n, returnThis);
    n.nextNode = m_firstNode;
    try { m_firstNode.prevNode = n; } catch (NullPointerException exc) { }
    m_firstNode = n;
    n.nodeId = returnThis;
    n.outDegree = 0; n.inDegree = 0; n.undDegree = 0;
    return returnThis;
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
    try { e.nextOutEdge.prevOutEdge = e.prevOutEdge; }
    catch (NullPointerException exc) { }
    try { e.prevInEdge.nextInEdge = e.nextInEdge; }
    catch (NullPointerException exc) { target.firstInEdge = e.nextInEdge; }
    try { e.nextInEdge.prevInEdge = e.prevInEdge; }
    catch (NullPointerException exc) { }
    if (e.directed) { source.outDegree--; target.inDegree--; }
    else { source.undDegree--; target.undDegree--; }
    m_edges.setEdgeAtIndex(null, edge);
    m_freeEdges.push(edge);
    e.prevOutEdge = null; e.nextInEdge = null; e.prevInEdge = null;
    m_edgeDepot.recycleEdge(e);
    m_edgeCount--;
    return true;
  }

  public int createEdge(int sourceNode, int targetNode, boolean directed)
  {
    final Node source;
    final Node target;
    try { source = m_nodes.getNodeAtIndex(sourceNode); }
    catch (ArrayIndexOutOfBoundsException exc) {
      // sourceNode is negative or Integer.MAX_VALUE.
      throw new IllegalArgumentException("sourceNode is negative"); }
    try { target = m_nodes.getNodeAtIndex(targetNode); }
    catch (ArrayIndexOutOfBoundsException exc) {
      // targetNode is negative or Integer.MAX_VALUE.
      throw new IllegalArgumentException("targetNode is negative"); }
    if (source == null || target == null) return -1;
    m_edgeCount++;
    final Edge e = m_edgeDepot.getEdge();
    final int returnThis;
    if (m_freeEdges.size() > 0) returnThis = m_freeEdges.pop();
    else returnThis = ++m_maxEdge;
    m_edges.setEdgeAtIndex(e, returnThis);
    if (directed) { source.outDegree++; target.inDegree++; }
    else { source.undDegree++; target.undDegree++; }
    e.nextOutEdge = source.firstOutEdge;
    try { source.firstOutEdge.prevOutEdge = e; }
    catch (NullPointerException exc) { }
    source.firstOutEdge = e;
    e.nextInEdge = target.firstInEdge;
    try { target.firstInEdge.prevInEdge = e; }
    catch (NullPointerException exc) { }
    target.firstInEdge = e;
    e.edgeId = returnThis;
    e.directed = directed;
    e.sourceNode = sourceNode;
    e.targetNode = targetNode;
    return returnThis;
  }

  public boolean containsNode(int node)
  {
    try { return m_nodes.getNodeAtIndex(node) != null; }
    catch (ArrayIndexOutOfBoundsException exc) {
      // node is negative or Integer.MAX_VALUE.
      throw new IllegalArgumentException("node is negative"); }
  }

  public boolean containsEdge(int edge)
  {
    try { return m_edges.getEdgeAtIndex(edge) != null; }
    catch (ArrayIndexOutOfBoundsException exc) {
      // edge is negative or Integer.MAX_VALUE.
      throw new IllegalArgumentException("edge is negative"); }
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

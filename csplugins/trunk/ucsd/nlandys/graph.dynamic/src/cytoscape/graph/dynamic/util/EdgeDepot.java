package cytoscape.graph.dynamic.util;

final class EdgeDepot
{

  private final Edge m_head;

  EdgeDepot()
  {
    m_head = new Edge();
  }

  // Gimme an edge, darnit!
  // Don't forget to initialize the edge's member variables!
  Edge getEdge()
  {
    final Edge returnThis = m_head.nextOutEdge;
    if (returnThis == null) { return new Edge(); }
    m_head.nextOutEdge = returnThis.nextOutEdge;
    return returnThis;
  }

  // Deinitialize the object's members yourself if you need or want to.
  void recycleEdge(Edge edge)
  {
    edge.nextOutEdge = m_head.nextOutEdge;
    m_head.nextOutEdge = edge;
  }

}

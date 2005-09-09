package cytoscape.graph.dynamic.util;

// Package visible.
final class EdgeDepot implements java.io.Externalizable
{

  // Externalizable.  Docs say instantiated using public no-arg constructor.
  // No other references to the edges in this depot exist.
  public final void writeExternal(final java.io.ObjectOutput out)
    throws java.io.IOException {
    for (Edge currEdge = m_head.nextOutEdge; currEdge != null;
         currEdge = currEdge.nextOutEdge) out.writeInt(currEdge.edgeId);
    out.writeInt(-1); }
  public final void readExternal(final java.io.ObjectInput in)
    throws java.io.IOException {
    Edge currEdge = m_head;
    while (true) {
      final int id = in.readInt();
      if (id < 0) break;
      currEdge.nextOutEdge = new Edge();
      currEdge = currEdge.nextOutEdge;
      currEdge.edgeId = id; } }

  private final Edge m_head;

  EdgeDepot()
  {
    m_head = new Edge();
  }

  // Gimme an edge, darnit!
  // Don't forget to initialize the edge's member variables!
  // Edge.nextOutEdge is used internally and will point to some undefined
  // edge in the returned Edge.
  Edge getEdge()
  {
    final Edge returnThis = m_head.nextOutEdge;
    if (returnThis == null) { return new Edge(); }
    m_head.nextOutEdge = returnThis.nextOutEdge;
    return returnThis;
  }

  // edge.nextOutEdge is used internally and does not need to be deinitialized.
  void recycleEdge(Edge edge)
  {
    edge.nextOutEdge = m_head.nextOutEdge;
    m_head.nextOutEdge = edge;
  }

}

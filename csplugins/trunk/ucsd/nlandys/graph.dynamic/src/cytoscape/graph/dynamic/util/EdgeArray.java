package cytoscape.graph.dynamic.util;

// Package visible.
// Valid indices: [0, Integer.MAX_VALUE - 1].
final class EdgeArray implements java.io.Externalizable
{

  // Externalizable.
  public final void writeExternal(final java.io.ObjectOutput out)
    throws java.io.IOException {
    out.writeInt(m_edgeArr.length);
    for (int i = 0; i < m_edgeArr.length; i++) {
      final Edge edge = m_edgeArr[i];
      if (edge == null) { out.writeInt(-1); continue; }
      out.writeInt(edge.sourceNode);
      out.writeInt(edge.targetNode);
      out.writeBoolean(edge.directed); }
    for (int i = 0; i < m_edgeArr.length; i++) {
      final Edge edge = m_edgeArr[i];
      if (edge == null) continue;
      out.writeInt(edge.nextOutEdge == null ? -1 : edge.nextOutEdge.edgeId);
      out.writeInt(edge.prevOutEdge == null ? -1 : edge.prevOutEdge.edgeId);
      out.writeInt(edge.nextInEdge == null ? -1 : edge.nextInEdge.edgeId);
      out.writeInt(edge.prevInEdge == null ? -1 : edge.prevInEdge.edgeId); } }
  public final void readExternal(final java.io.ObjectInput in)
    throws java.io.IOException {
    m_edgeArr = new Edge[in.readInt()];
    for (int i = 0; i < m_edgeArr.length; i++) {
      final int sourceNode = in.readInt();
      if (sourceNode < 0) continue;
      final Edge edge = (m_edgeArr[i] = new Edge());
      edge.edgeId = i;
      edge.sourceNode = sourceNode;
      edge.targetNode = in.readInt();
      edge.directed = in.readBoolean(); }
    for (int i = 0; i < m_edgeArr.length; i++) {
      final Edge edge = m_edgeArr[i];
      if (edge == null) continue;
      final int nextOutEdge = in.readInt();
      final int prevOutEdge = in.readInt();
      final int nextInEdge = in.readInt();
      final int prevInEdge = in.readInt();
      if (nextOutEdge >= 0) edge.nextOutEdge = m_edgeArr[nextOutEdge];
      if (prevOutEdge >= 0) edge.prevOutEdge = m_edgeArr[prevOutEdge];
      if (nextInEdge >= 0) edge.nextInEdge = m_edgeArr[nextInEdge];
      if (prevInEdge >= 0) edge.prevInEdge = m_edgeArr[prevInEdge]; } }

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private Edge[] m_edgeArr;

  EdgeArray()
  {
    m_edgeArr = new Edge[INITIAL_CAPACITY];
  }

  // Understand that this method will not increase the size of the underlying
  // array, no matter what.
  // Throws ArrayIndexOutOfBoundsException if index is negative.
  // The package-level agreement for this class is that Integer.MAX_VALUE
  // will never be passed to this method.
  Edge getEdgeAtIndex(int index)
  {
    if (index >= m_edgeArr.length) return null;
    return m_edgeArr[index];
  }

  // Understand that this method will potentially increase the size of the
  // underlying array, but only if two conditions hold:
  //   1. edge is not null and
  //   2. index is greater than or equal to the length of the array.
  // Throws ArrayIndexOutOfBoundsException if index is negative.
  // The package-level agreement for this class is that Integer.MAX_VALUE
  // will never be passed to this method.
  void setEdgeAtIndex(Edge edge, int index)
  {
    if (index >= m_edgeArr.length && edge == null) return;
    try { m_edgeArr[index] = edge; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0) throw e;
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 Math.max(((long) m_edgeArr.length) * 2l + 1l,
                          ((long) index) + 1l + (long) INITIAL_CAPACITY));
      Edge[] newArr = new Edge[newArrSize];
      System.arraycopy(m_edgeArr, 0, newArr, 0, m_edgeArr.length);
      m_edgeArr = newArr;
      m_edgeArr[index] = edge;
    }
  }

}

package cytoscape.graph.dynamic.util;

final class EdgeDepot
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private Edge[] m_edgeStack;
  private int m_size;

  EdgeDepot()
  {
    m_edgeStack = new Edge[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme an edge, darnit!
  // Don't forget to initialize the edge's member variables!
  Edge getEdge()
  {
    if (m_size == 0) { return new Edge(); }
    else { return m_edgeStack[--m_size]; }
  }

  // Deinitialize the object's members yourself if you need or want to.
  // Don't recycle null; this error condition is not checked for.
  void recycleEdge(Edge edge)
  {
    try { m_edgeStack[m_size++] = edge; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_size--;
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_edgeStack.length) * 2l + 1l);
      if (newArrSize == m_edgeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      Edge[] newArr = new Edge[newArrSize];
      System.arraycopy(m_edgeStack, 0, newArr, 0, m_edgeStack.length);
      m_edgeStack = newArr;
      m_edgeStack[m_size++] = edge; }
  }

}

package fing.model;

final class EdgeDepository implements FingEdgeDepot
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private FingEdge[] m_edgeStack;
  private int m_size;

  EdgeDepository()
  {
    m_edgeStack = new FingEdge[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme an edge, darnit!
  // Don't forget to initialize the edge's member variables!
  public FingEdge getEdge()
  {
    if (m_size == 0) { return new FEdge(); }
    else { return m_edgeStack[--m_size]; }
  }

  // Deinitialize the object's members yourself if you need or want to.
  public void recycleEdge(FingEdge edge)
  {
    if (edge == null) return;
    try { m_edgeStack[m_size] = edge; m_size++; }
    catch (ArrayIndexOutOfBoundsException e) {
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_edgeStack.length) * 2l + 1l);
      if (newArrSize == m_edgeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      FingEdge[] newArr = new FingEdge[newArrSize];
      System.arraycopy(m_edgeStack, 0, newArr, 0, m_edgeStack.length);
      m_edgeStack = newArr;
      m_edgeStack[m_size++] = edge; }
  }

}

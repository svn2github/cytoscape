package fing.model;

class EdgeArray
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private FEdge[] m_edgeArr;

  EdgeArray()
  {
    m_edgeArr = new FEdge[INITIAL_CAPACITY];
  }

  FEdge getEdgeAtIndex(int index)
  {
    try { return m_edgeArr[index]; }
    catch (ArrayIndexOutOfBoundsException e) { return null; }
  }

  void setEdgeAtIndex(FEdge edge, int index)
  {
    try { m_edgeArr[index] = edge; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0) { throw e; }
      else {
        final int newArrSize = Math.max(m_edgeArr.length * 2 + 1,
                                        (index + 1) + INITIAL_CAPACITY);
        FNode[] newArr = new FNode[(newArrSize > 0) ? newArrSize :
                                   Integer.MAX_VALUE];
        System.arraycopy(m_edgeArr, 0, newArr, 0, m_edgeArr.length);
        m_edgeArr = newArr;
        m_edgeArr[index] = node; }
    }
  }

}

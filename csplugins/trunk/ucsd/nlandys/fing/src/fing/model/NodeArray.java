package fing.model;

class NodeArray
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private FNode[] m_nodeArr;

  NodeArray()
  {
    m_nodeArr = new FNode[INITIAL_CAPACITY];
  }

  FNode getNodeAtIndex(int index)
  {
    try { return m_nodeArr[index]; }
    catch (ArrayIndexOutOfBoundsException e) { return null; }
  }

  void setNodeAtIndex(FNode node, int index)
  {
    try { m_nodeArr[index] = node; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0) { throw e; }
      else {
        final int newArrSize = Math.max(m_nodeArr.length * 2 + 1,
                                        (index + 1) + INITIAL_CAPACITY);
        FNode[] newArr = new FNode[(newArrSize > 0) ? newArrSize :
                                   Integer.MAX_VALUE];
        System.arraycopy(m_nodeArr, 0, newArr, 0, m_nodeArr.length);
        m_nodeArr = newArr;
        m_nodeArr[index] = node; }
    }
  }

}

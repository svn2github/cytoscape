package fing.model;

// Valid indices: [0, Integer.MAX_VALUE - 1].
class NodeArray
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private FNode[] m_nodeArr;

  NodeArray()
  {
    m_nodeArr = new FNode[INITIAL_CAPACITY];
  }

  // Understand that this method will not increase the size of the underlying
  // array, no matter what.
  FNode getNodeAtIndex(int index)
  {
    try { return m_nodeArr[index]; }
    catch (ArrayIndexOutOfBoundsException e) {
      if (index < 0 || index == Integer.MAX_VALUE) { throw e; }
      return null; }
  }

  // Understand that this method will potentially increase the size of the
  // underlying array, but only if two conditions hold:
  //   1. node is not null and
  //   2. index is greater than or equal to the length of the array.
  void setNodeAtIndex(FNode node, int index)
  {
    try { m_nodeArr[index] = node; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0 || index == Integer.MAX_VALUE) { throw e; }
      else if (node == null) { return; }
      else {
        // We need to make sure that we at least double the array length
        // in order to ensure amortized constant time hits for ths function.
        final int newArrSize = (int)
          Math.min((long) Integer.MAX_VALUE,
                   Math.max(((long) m_nodeArr.length) * 2l + 1l,
                            ((long) index) + 1l + (long) INITIAL_CAPACITY));
        if (newArrSize == m_nodeArr.length)
          throw new IllegalStateException
            ("unable to allocate large enough array");
        FNode[] newArr = new FNode[newArrSize];
        System.arraycopy(m_nodeArr, 0, newArr, 0, m_nodeArr.length);
        m_nodeArr = newArr;
        m_nodeArr[index] = node; }
    }
  }

}

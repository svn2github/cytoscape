package fing.model;

import giny.model.Node;

// Valid indices: [0, Integer.MAX_VALUE - 1].
class NodeArray
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private Node[] m_nodeArr;

  NodeArray()
  {
    m_nodeArr = new Node[INITIAL_CAPACITY];
  }

  // Understand that this method will not increase the size of the underlying
  // array, no matter what.
  // Throws ArrayIndexOutOfBoundsException if index is negative or
  // Integer.MAX_VALUE.
  Node getNodeAtIndex(int index)
  {
    // Do pre-checking because try/catch with thrown exception causes huge
    // performance hit.
    if (index >= m_nodeArr.length && index != Integer.MAX_VALUE) return null;
    return m_nodeArr[index]; // Exception if Integer.MAX_VALUE or negative.
  }

  // Understand that this method will potentially increase the size of the
  // underlying array, but only if two conditions hold:
  //   1. node is not null and
  //   2. index is greater than or equal to the length of the array.
  // Throws ArrayIndexOutOfBoundsException if index is negative or
  // Integer.MAX_VALUE.
  void setNodeAtIndex(Node node, int index)
  {
    // Do pre-checking because try/catch with thrown exception causes huge
    // performance hit.
    if (index >= m_nodeArr.length && node == null &&
        index != Integer.MAX_VALUE) return;
    try { m_nodeArr[index] = node; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0 || index == Integer.MAX_VALUE) { throw e; }
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 Math.max(((long) m_nodeArr.length) * 2l + 1l,
                          ((long) index) + 1l + (long) INITIAL_CAPACITY));
      Node[] newArr = new Node[newArrSize];
      System.arraycopy(m_nodeArr, 0, newArr, 0, m_nodeArr.length);
      m_nodeArr = newArr;
      m_nodeArr[index] = node;
    }
  }

}

package fing.model;

final class NodeDepository
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private FNode[] m_nodeStack;
  private int m_size;

  NodeDepository()
  {
    m_nodeStack = new FNode[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme a node, darnit!
  // Don't forget to initialize the node's member variables!
  FNode getNode()
  {
    if (m_size == 0) { return new FNode(); }
    else { return m_nodeStack[--m_size]; }
  }

  // Deinitialize the object's members yourself if you need or want to.
  void recycleNode(FNode node)
  {
    if (node == null) return;
    try { m_nodeStack[m_size] = node; m_size++; }
    catch (ArrayIndexOutOfBoundsException e) {
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_nodeStack.length) * 2l + 1l);
      if (newArrSize == m_nodeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      FNode[] newArr = new FNode[newArrSize];
      System.arraycopy(m_nodeStack, 0, newArr, 0, m_nodeStack.length);
      m_nodeStack = newArr;
      m_nodeStack[m_size++] = node; }
  }

}

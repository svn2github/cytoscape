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
    if (m_size == m_nodeStack.length) {
      FNode[] newArr = new FNode[m_nodeStack * 2 + 1];
      System.arraycopy(m_nodeStack, 0, newArr, 0, m_nodeStack.length);
      m_nodeStack = newArr; }
    m_nodeStack[m_size++] = node;
  }

}

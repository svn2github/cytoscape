package cytoscape.graph.dynamic.util;

final class NodeDepot
{

  private final static int INITIAL_CAPACITY = 11; // Must be non-negative.

  private Node[] m_nodeStack;
  private int m_size;

  NodeDepot()
  {
    m_nodeStack = new Node[INITIAL_CAPACITY];
    m_size = 0;
  }

  // Gimme a node, darnit!
  // Don't forget to initialize the node's member variables!
  Node getNode()
  {
    if (m_size == 0) { return new Node(); }
    else { return m_nodeStack[--m_size]; }
  }

  // Deinitialize the object's members yourself if you need or want to.
  // Don't recycle null; this error condition is not checked for.
  void recycleNode(Node node)
  {
    try { m_nodeStack[m_size++] = node; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_size--;
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 ((long) m_nodeStack.length) * 2l + 1l);
      if (newArrSize == m_nodeStack.length)
        throw new IllegalStateException
          ("unable to allocate large enough array");
      Node[] newArr = new Node[newArrSize];
      System.arraycopy(m_nodeStack, 0, newArr, 0, m_nodeStack.length);
      m_nodeStack = newArr;
      m_nodeStack[m_size++] = node; }
  }

}

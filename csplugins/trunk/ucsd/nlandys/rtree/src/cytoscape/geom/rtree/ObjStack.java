package cytoscape.geom.rtree;

/**
 * A first-in, last-out container of objects; an object put onto the stack
 * may be null.  In the underlying implementation, the memory consumed by an
 * instance of this class may increase, but does never decrease.
 * NOTE: Right now this class is package visible but it is written in such
 * a way so as to be suitable to become public at some point, once a proper
 * package location for it is found.
 */
final class ObjStack
{

  // This must be a non-negative integer.
  private static final int DEFAULT_CAPACITY = 11;

  private Object[] m_stack;
  private int m_currentSize;

  /**
   * Creates a new stack of objects.
   */
  public ObjStack()
  {
    m_stack = new Object[DEFAULT_CAPACITY];
    m_currentSize = 0;
  }

  /**
   * Returns the number of objects that are currently on this stack.
   */
  public final int size()
  {
    return m_currentSize;
  }

  /**
   * Pushes a new object onto this stack.  A successive peek() or pop()
   * call will return the specified object.  An object pushed onto this
   * stack may be null.
   */
  public final void push(Object obj)
  {
    try { m_stack[m_currentSize++] = obj; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_currentSize--;
      checkSize();
      m_stack[m_currentSize++] = obj; }
  }

  /**
   * A non-mutating operation that retrieves the next object on this stack.<p>
   * It is considered an error to call this method if there are no objects
   * currently on this stack.  If size() returns zero immediately before this
   * method is called, the results of this operation are undefined.
   */
  public final Object peek()
  {
    return m_stack[m_currentSize - 1];
  }

  /**
   * Removes and returns the next object on this stack.<p>
   * It is considered an error to call this method if there are no objects
   * currently on this stack.  If size() returns zero immediately before this
   * method is called, the results of this operation are undefined.
   */
  public final Object pop()
  {
    try { return m_stack[--m_currentSize]; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_currentSize++;
      throw e; }
  }

  private final void checkSize()
  {
    if (m_currentSize < m_stack.length) return;
    final int newStackSize = (int) Math.min((long) Integer.MAX_VALUE,
                                            ((long) m_stack.length) * 2l + 1l);
    if (newStackSize == m_stack.length)
      throw new IllegalStateException("cannot allocate large enough array");
    final Object[] newStack = new Object[newStackSize];
    System.arraycopy(m_stack, 0, newStack, 0, m_stack.length);
    m_stack = newStack;
  }

}

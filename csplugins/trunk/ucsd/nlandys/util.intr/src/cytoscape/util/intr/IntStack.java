package cytoscape.util.intr;

/**
 * A first-in, last-out container of 32 bit integers.  In the underlying
 * implementation, the memory consumed by an instance of this class may
 * increase, but does never decrease.
 */
public final class IntStack
{

  // This must be a non-negative integer.
  private static final int DEFAULT_CAPACITY = 11;

  private int[] m_stack;
  private int m_currentSize;

  /**
   * Creates a new stack of integers.
   */
  public IntStack()
  {
    m_stack = new int[DEFAULT_CAPACITY];
    m_currentSize = 0;
  }

  /**
   * Removes all integers from this stack.  This operation has constant time
   * complexity.
   */
  public final void empty()
  {
    m_currentSize = 0;
  }

  /**
   * Returns the number of integers that are currently on this stack.
   */
  public final int size()
  {
    return m_currentSize;
  }

  /**
   * Pushes a new integer onto this stack.  A successive peek() or pop()
   * call will return the specified value.
   */
  public final void push(int value)
  {
    try { m_stack[m_currentSize++] = value; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_currentSize--;
      checkSize();
      m_stack[m_currentSize++] = value; }
  }

  /**
   * A non-mutating operation to retrieve the next integer on this stack.
   * It is considered an error to call this method if there are no integers
   * currently in this stack.  If size() returns zero immediately before this
   * method is called, the results of this operation are undefined.
   */
  public final int peek()
  {
    return m_stack[m_currentSize - 1];
  }

  /**
   * Removes and returns the next integer on this stack.<p>
   * It is considered an error to call this method if there are no integers
   * currently in this stack.  If size() returns zero immediately before this
   * method is called, the results of this operation are undefined.
   */
  public final int pop()
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
    final int[] newStack = new int[(int) newStackSize];
    System.arraycopy(m_stack, 0, newStack, 0, m_stack.length);
    m_stack = newStack;
  }

}

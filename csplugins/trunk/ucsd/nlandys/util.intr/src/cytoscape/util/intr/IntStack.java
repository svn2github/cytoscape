package cytoscape.util.intr;

public final class IntStack
{

  // This must be a non-negative integer.
  private static final int DEFAULT_CAPACITY = 11;

  private int[] m_stack;
  private int m_currentSize;

  public IntStack()
  {
    m_stack = new int[DEFAULT_CAPACITY];
    m_currentSize = 0;
  }

  public final void empty()
  {
    m_currentSize = 0;
  }

  public final int size()
  {
    return m_currentSize;
  }

  public final void push(int val)
  {
    try { m_stack[m_currentSize++] = val; }
    catch (ArrayIndexOutOfBoundsException e) {
      m_currentSize--;
      checkSize();
      m_stack[m_currentSize++] = val; }
  }

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

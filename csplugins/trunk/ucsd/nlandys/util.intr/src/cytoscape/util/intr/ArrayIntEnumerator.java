package cytoscape.util.intr;

/**
 * A utility class which conveniently converts an array of integers into
 * an IntEnumerator (an enumeration of integers).
 */
public final class ArrayIntEnumerator implements IntEnumerator
{

  private final int[] m_elements;
  private int m_index;
  private final int m_end;

  /**
   * No copy of the elements array is made.  The contents of the array
   * are never modified by this object.
   */
  public ArrayIntEnumerator(int[] elements, int beginIndex, int length)
  {
    if (beginIndex < 0)
      throw new IllegalArgumentException("beginIndex is less than zero");
    if (length < 0)
      throw new IllegalArgumentException("length is less than zero");
    if (((long) beginIndex) + (long) length > (long) elements.length)
      throw new IllegalArgumentException
        ("combination of beginIndex and length exceed length of array");
    m_elements = elements;
    m_index = beginIndex;
    m_end = beginIndex + length;
  }

  public final int numRemaining()
  {
    return m_end - m_index;
  }

  public final int nextInt()
  {
    return m_elements[m_index++];
  }

}

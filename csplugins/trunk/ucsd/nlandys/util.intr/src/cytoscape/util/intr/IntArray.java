package cytoscape.util.intr;

/**
 * A dynamically growing array of integers.
 * Valid indices of this array are in the range [0, Integer.MAX_VALUE - 1].<p>
 * In the underlying implementation, this dynamically growing array
 * increases in size to adapt to elements being added (the size of the
 * underlying data structure supporting this dynamically growing array
 * is invisible to the programmer).  In the underlying implementation, this
 * dynamic array never decreases in size.  Underlying size expansions are
 * implemented such that the operation of expanding in size is amortized over
 * the constant time complexity of inserting new elements into this
 * dynamic array.<p>
 * An instance of this class is serializable; however, serialized instances of
 * this class should not be stored in a persistent manner because the
 * serialization implemented in this class makes no attempt at handling
 * class versioning.
 */
public final class IntArray implements java.io.Serializable
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private int[] m_arr;

  public IntArray()
  {
    m_arr = new int[INITIAL_CAPACITY];
  }

  /**
   * Returns the value at specified index.
   * This method will not increase the size of the underlying array, no
   * matter what.  The value returned by this method will be 0 unless a
   * value at given index has been previously specified with
   * setIntAtIndex(int, int).
   *
   * @exception ArrayIndexOutOfBoundsException if index is negative or
   *   Integer.MAX_VALUE.
   */
  public final int getIntAtIndex(int index)
  {
    // Do pre-checking because try/catch with thrown exception causes huge
    // performance hit.
    if (index >= m_arr.length && index != Integer.MAX_VALUE) return 0;
    return m_arr[index]; // Exception if Integer.MAX_VALUE or negative.
  }

  /**
   * Sets the specified value at specified index.
   * This method will potentially increase the size of the underlying array,
   * but only if two conditions hold:
   *   1. value is not zero and
   *   2. index is greater than or equal to the length of the underlying
   *      array.
   * <p>
   * NOTE: Setting very large indices to non-zero values implies instantiating
   * a very large underlying data structure.
   *
   * @exception ArrayIndexOutOfBoundsException if index is negative or
   *   Integer.MAX_VALUE.
   */
  public final void setIntAtIndex(int value, int index)
  {
    // Do pre-checking because try/catch with thrown exception causes huge
    // performance hit.
    if (index >= m_arr.length && value == 0 && index != Integer.MAX_VALUE)
      return;
    try { m_arr[index] = value; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0 || index == Integer.MAX_VALUE) { throw e; }
      // We need to ensure amortized constant time hits.
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 Math.max(((long) m_arr.length) * 2l + 1l,
                          ((long) index) + 1l + (long) INITIAL_CAPACITY));
      int[] newArr = new int[newArrSize];
      System.arraycopy(m_arr, 0, newArr, 0, m_arr.length);
      m_arr = newArr;
      m_arr[index] = value;
    }
  }

}

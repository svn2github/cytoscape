package cytoscape.util.intr;

/**
 * A dynamically growing array of integers.
 * Valid indices: [0, Integer.MAX_VALUE - 1].
 */
public final class IntArray
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
   * @exception ArrayIndexOutOfBoundsException if index is negative or
   *   Integer.MAX_VALUE.
   */
  public final int getIntAtIndex(int index)
  {
    try { return m_arr[index]; }
    catch (ArrayIndexOutOfBoundsException e) {
      if (index < 0 || index == Integer.MAX_VALUE) { throw e; }
      return 0; }
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
   * @exception ArrayIndexOutOfBoundsException if index is negative or
   *   Integer.MAX_VALUE.
   */
  public final void setIntAtIndex(int value, int index)
  {
    try { m_arr[index] = value; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0 || index == Integer.MAX_VALUE) { throw e; }
      else if (value == 0) { return; }
      else {
        // We need to ensure amortized constant time hits.
        final int newArrSize = (int)
          Math.min((long) Integer.MAX_VALUE,
                   Math.max(((long) m_arr.length) * 2l + 1l,
                            ((long) index) + 1l + (long) INITIAL_CAPACITY));
        int[] newArr = new int[newArrSize];
        System.arraycopy(m_arr, 0, newArr, 0, m_arr.length);
        m_arr = newArr;
        m_arr[index] = value; }
    }
  }

}

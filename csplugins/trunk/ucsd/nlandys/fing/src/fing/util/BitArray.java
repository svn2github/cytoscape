package fing.util;

/**
 * This class represents a fixed-size array of boolean values.  A BitArray
 * of length N consumes 32 times less memory than a boolean[] of length N.
 */
public final class BitArray
{

  private final int[] m_arr;
  private final int m_length;

  /**
   * A new array has entries that are all false.
   */
  public BitArray(int length)
  {
    if (length <= 0)
      throw new IllegalArgumentException("length must be positive");
    m_arr = new int[((length - 1) / 32) + 1];
    m_length = length;
  }

  /**
   * Sets all entries in this array to the value specified.
   */
  public final void reset(boolean value)
  {
    if (value) for (int i = 0; i < m_arr.length; i++) m_arr[i] = 0;
    else for (int i = 0; i < m_arr.length; i++) m_arr[i] = 0xffffffff;
  }

  /**
   * Returns the value of this array at the specified index.<p>
   * If index is less than zero, results of this operation are undefined.
   * If index is greater than or equal to length(), results of this operation
   * are undefined.
   * @see #length()
   */
  public final boolean get(int index)
  {
    int majorIndex = index / 32;
    int minorIndex = index % 32;
    int mask = 0x80000000 >>> minorIndex;
    return (m_arr[majorIndex] & mask) != 0;
  }

  /**
   * Manipulates the array such that a successive call to get(index)
   * would return the boolean value specified.<p>
   * If index is less than zero, results ofthis operation are undefined.
   * If index is greater than or equal to length(), results of this
   * operation are undefined.
   * @see #get(int)
   * @see #length()
   */
  public final void put(int index, boolean value)
  {
    int majorIndex = index / 32;
    int minorIndex = index % 32;
    int mask = 0x80000000 >>> minorIndex;
    if (value) m_arr[majorIndex] |= mask;
    else m_arr[majorIndex] &= ~mask;
  }

  /**
   * Returns the length of this array, as specified at
   * construction time.  Valid indices for elements in this array are
   * between zero and length()-1, inclusive.
   */
  public final int length()
  {
    return m_length;
  }

  /**
   * Copies data from one array to another.<p>
   * IMPORTANT NOTE: This method will perform extremely well when
   * the difference between srcPos and destPos is a multiple of 32.
   * Otherwise, this method performs only well.
   * @param src the source array from which to copy information.
   * @param srcPos a contiguous block of data is copied from the source array
   *   starting at this index in the source array.
   * @param dest the array into which to copy data.
   * @param destPos the contiguous block of data is copied into the
   *   destination array starting at this index in the destination array.
   * @param length the number of contiguous elements to copy from the
   *   source array into the destination array.
   * @exception IndexOutOfBoundsException if copying would cause access of data
   *   outside array bounds.
   */
  public static final void arrayCopy(BitArray src, int srcPos,
                                     BitArray dest, int destPos, int length)
  {
    if (length < 0 || srcPos < 0 || destPos < 0 ||
        srcPos + length > src.length() || destPos + length > dest.length())
      throw new IndexOutOfBoundsException
        ("combination of input parameters cause access outside array bounds");
    if ((srcPos - destPos) % 32 != 0) // Even this case can be optimized
      for (int i = 0; i < length; i++) // by using shifts on integers;
        dest.put(destPos + i, src.get(srcPos + i)); // this is not optimized.
    else { // We can directly copy arrays of integers.
      int majorSrcBegin = srcPos / 32;
      int minorSrcBegin = srcPos % 32;
      int majorDestBegin = destPos / 32;
      int minorDestBegin = destPos % 32;
      int majorSrcEnd = (srcPos + length) / 32;
      int minorSrcEnd = (srcPos + length) % 32;
      int majorDestEnd = (destPos + length) / 32;
      int minorDestEnd = (destPos + length) % 32;
      if (minorSrcBegin != 0) {
        int mask = ~(0xffffffff >>> minorSrcBegin);
        if (majorSrcBegin == majorSrcEnd) mask |= (0xffffffff >>> minorSrcEnd);
        int bitsToCopy = src.m_arr[majorSrcBegin] & ~mask;
        dest.m_arr[majorDestBegin] &= mask;
        dest.m_arr[majorDestBegin] |= bitsToCopy;
        majorSrcBegin++;
        minorSrcBegin = 0;
        majorDestBegin++;
        minorDestBegin = 0; }
    }
  }

}

package fing.util;

/**
 * This class represents an array of boolean values, consuming a
 * factor of 32 less memory than an array of native Java boolean values.
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
   * Returns the value of this array at the specified index.
   * If index is less than zero, results of this operation are undefined.
   * If index is greater than or equal to length(), results of this operation
   * are undefined.
   */
  public final boolean get(int index)
  {
    int majorIndex = index / 32;
    int minorIndex = index % 32;
    int mask = 0x00000001 << minorIndex;
    return (m_arr[majorIndex] & mask) != 0;
  }

  /**
   * Manipulates the array such that a successive call to get(index)
   * would return the boolean value specified.
   */
  public final void put(int index, boolean value)
  {
    int majorIndex = index / 32;
    int minorIndex = index % 32;
    int mask = 0x00000001 << minorIndex;
    if (value) m_arr[majorIndex] |= mask;
    else m_arr[majorIndex] &= ~mask;
  }

  /**
   * Returns the length of this array, as specified at
   * construction time.  Valid indices for elements in this array are
   * between zero and length() minus one, inclusive.
   */
  public final int length()
  {
    return m_length;
  }

}

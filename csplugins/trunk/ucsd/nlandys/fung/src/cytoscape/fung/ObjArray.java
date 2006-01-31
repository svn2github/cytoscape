package cytoscape.fung;

// Package visible.
// Valid indices: [0, Integer.MAX_VALUE - 1].
final class ObjArray
{

  private final static int INITIAL_CAPACITY = 0; // Must be non-negative.

  private Object[] m_objArr;

  ObjArray()
  {
    m_objArr = new Object[INITIAL_CAPACITY];
  }

  // Understand that this method will not increase the size of the underlying
  // array, no matter what.
  // Throws ArrayIndexOutOfBoundsException if index is negative.
  // The package-level agreement for this class is that Integer.MAX_VALUE
  // will never be passed to this method.
  final Object getObjAtIndex(final int index)
  {
    if (index >= m_objArr.length) return null;
    return m_objArr[index];
  }

  // Understand that this method will potentially increase the size of the
  // underlying array, but only if two conditions hold:
  //   1. obj is not null and
  //   2. index is greater than or equal to the length of the array.
  // Throws ArrayIndexOutOfBoundsException if index is negative.
  // The package-level agreement for this class is that Integer.MAX_VALUE
  // will never be passed to this method.
  final void setObjAtIndex(final Object obj, final int index)
  {
    if (index >= m_objArr.length && obj == null) return;
    try { m_objArr[index] = obj; }
    catch (ArrayIndexOutOfBoundsException e)
    {
      if (index < 0) throw e;
      final int newArrSize = (int)
        Math.min((long) Integer.MAX_VALUE,
                 Math.max(((long) m_objArr.length) * 2l + 1l,
                          ((long) index) + 1l + (long) INITIAL_CAPACITY));
      final Object[] newArr = new Object[newArrSize];
      System.arraycopy(m_objArr, 0, newArr, 0, m_objArr.length);
      m_objArr = newArr;
      m_objArr[index] = obj;
    }
  }

}

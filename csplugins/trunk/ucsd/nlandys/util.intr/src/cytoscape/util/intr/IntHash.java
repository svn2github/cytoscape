package cytoscape.util.intr;

/**
 * An insert-only hashtable that has non-negative 32 bit integer keys;
 * no "payload" is stored in this hashtable.  An instance of this class
 * is well-suited for efficiently detecting collisions between integers,
 * removing duplicates from a list of integers, or determining the presence of
 * an integer in a list of integers.<p>
 * In the underlying implementation, this hashtable increases in size to adapt
 * to elements being added (the underlying size of the hashtable is invisible
 * to the programmer).  In the underlying implementation, this hashtable never
 * decreases in size.  As a hashtable increases in size,
 * it takes at most 4 times as much memory as it would take
 * to store the hashtable's elements in a perfectly-sized array.
 * Underlying size expansions are implemented such that the operation of
 * expanding in size is amortized over the contstant time complexity needed to
 * insert new elements.
 */
public final class IntHash
{

  private static final int[] PRIMES = { 11, 23, 47, 97, 197, 397, 797, 1597,
                                        3203, 6421, 12853, 25717, 51437,
                                        102877, 205759, 411527, 823117,
                                        1646237, 3292489, 6584983, 13169977,
                                        26339969, 52679969, 105359939,
                                        210719881, 421439783, 842879579,
                                        1685759167, Integer.MAX_VALUE };
  private static final int INITIAL_SIZE = PRIMES[0];
  private static final double THRESHOLD_FACTOR = 0.77;

  private int[] m_arr;
  private int m_elements;
  private int m_size;
  private int m_thresholdSize;

  /**
   * Create a new hashtable.
   */
  public IntHash()
  {
    m_arr = new int[INITIAL_SIZE];
    empty();
  }

  /**
   * Removes all elements from this hashtable.  This operation has
   * O(1) time complexity.
   */
  public final void empty()
  {
    m_elements = 0;
    m_size = INITIAL_SIZE;
    m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_size);
    for (int i = 0; i < m_size; i++) m_arr[i] = -1;
  }

  /**
   * Puts a new value into this hashtable if that value is not already in
   * this hashtable; otherwise does nothing.  Returns the input value if this
   * value was already in this hashtable; returns -1 if the input value was
   * not in this hashtable prior to this call.<p>
   * Only non-negative values can be passed to this method.
   * Behavior is undefined if negative values are passed to put(int).<p>
   * Insertions into the hashtable are performed in [amortized] time
   * complexity O(1).
   */
  public final int put(final int value)
  {
    checkSize();
    int incr = 0;
    int index;
    for (index = value % (((~value) >>> 31) * m_size);
         m_arr[index] >= 0 && m_arr[index] != value;
         index = (index + incr) % m_size) {
      // Caching increment, which is an expensive operation, at the expense
      // of having an if statement.  I don't want to compute the increment
      // before this 'for' loop in case we get an immediate hit.
      if (incr == 0) { incr = 1 + (value % (m_size - 1)); } }
    final int returnVal = m_arr[index];
    m_arr[index] = value;
    m_elements += (returnVal >>> 31);
    return returnVal;
  }

  /**
   * Determines whether or not the value specified is in this hashtable.
   * Returns the value specified if this value is in the hashtable, otherwise
   * returns -1.<p>
   * It is an error to pass negative values to this method.  Passing
   * negative values to this method will result in undefined behavior of
   * this hashtable.<p>
   * Searches in this hashtable are performed in [amortized] time
   * complexity O(1).
   */
  public final int get(final int value)
  {
    int incr = 0;
    int index;
    for (index = value % (((~value) >>> 31) * m_size);
         m_arr[index] >= 0 && m_arr[index] != value;
         index = (index + incr) % m_size) {
      // Caching increment, which is an expensive operation, at the expense
      // of having an if statement.  I don't want to compute the increment
      // before this 'for' loop in case we get an immediate hit.
      if (incr == 0) { incr = 1 + (value % (m_size - 1)); } }
    return m_arr[index];
  }

  /**
   * Returns an enumeration of elements in this hashtable, ordered
   * arbitrarily.<p>
   * The returned enumeration becomes "invalid" as soon as any other method
   * on this hashtable instance is called; calling methods on an invalid
   * enumeration will cause undefined behavior in the enumerator.  Actually,
   * one method will not invalidate this enumeration: the get(int) method.
   * The returned enumerator has absolutely no effect on the underlying
   * hashtable.<p>
   * This method returns a value in constant time.  The returned enumerator
   * returns successive elements in [amortized] time complexity O(1).
   */
  public final IntEnumerator elements()
  {
    final int[] array = m_arr;
    final int numElements = m_elements;
    return new IntEnumerator() {
        int elements = numElements;
        int index = -1;
        public int numRemaining() { return elements; }
        public int nextInt() {
          while (array[++index] < 0) { }
          elements--;
          return array[index]; } };
  }

  private int[] m_dump = null;

  private final void checkSize()
  {
    if (m_elements >= m_thresholdSize)
    {
      final int newSize;
      try {
        int primesInx = 0;
        while (m_size != PRIMES[primesInx++]) { }
        newSize = PRIMES[primesInx]; }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new IllegalStateException
          ("too many elements in this hashtable"); }
      if (m_arr.length < newSize) {
        final int[] newArr = new int[newSize];
        m_dump = m_arr;
        m_arr = newArr; }
      else {
        System.arraycopy(m_arr, 0, m_dump, 0, m_size); }
      for (int i = 0; i < newSize; i++) m_arr[i] = -1;
      m_size = newSize;
      m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_size);
      int incr;
      int newIndex;
      int oldIndex = -1;
      for (int i = 0; i < m_elements; i++) {
        while (m_dump[++oldIndex] < 0) { }
        incr = 0;
        for (newIndex = m_dump[oldIndex] % m_size;
             m_arr[newIndex] >= 0;
             newIndex = (newIndex + incr) % m_size) {
          // Caching increment, which is an expensive operation, at the expense
          // of having an if statement.  I don't want to compute the increment
          // before this 'for' loop in case we get an immediate hit.
          if (incr == 0) { incr = 1 + (m_dump[oldIndex] % (m_size - 1)); } }
        m_arr[newIndex] = m_dump[oldIndex]; }
    }
  }

}

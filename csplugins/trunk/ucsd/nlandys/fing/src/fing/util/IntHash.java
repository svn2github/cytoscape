package fing.util;

/**
 * An insert-only hashtable that has non-negative 32 bit integer elements;
 * no "satellite data" is stored in this hashtable.  An instance of this class
 * is well-suited for detecting collisions between integers or determining
 * the presence of an integer in a set of integers.<p>
 * In the underlying implementation, this hashtable increases in size to adapt
 * to elements being added (the underlying size of the hashtable is invisible
 * to the programmer).  In the underlying implementation, this hashtable never
 * decreases in size.  As a hashtable increases in size,
 * it takes at most 3 times as much memory as it would take
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
  private static final double THRESHOLD_FACTOR = 0.666;

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
   * Puts a new value into this hashtable if that value is not already in
   * this hashtable; otherwise does nothing.
   * Returns false if and only if this value is already in the hashtable.<p>
   * Only non-negative values can be passed to this method.
   * Behavior is undefined If negative values are passed to put(int).<p>
   * Insertions into the hashtable are performed in [amortized] constant time.
   */
  public final boolean put(int value)
  {
    checkSize();
    // Double hashing.
    // h(key, i) = (h1(key) + i*h2(key)) mod size.
    // h1(key) = key mod size.
    // h2(key) = 1 + (key mod (size - 1)).
    // Note that size is always prime.
    return false;
  }

  /**
   * Determines whether or not the value specified is in this hashtable.
   * Returns the value specified if this value is in the hashtable; otherwise
   * returns -1.<p>
   * It is an error to pass negative values to this method.  Passing
   * negative values to this method will result in undefined behavior of
   * this hashtable.
   */
  public final int get(final int value)
  {
    final int size = m_size;
    int index;
    // The one thing about ths 'for' loop that I don't like is that we're
    // calculating (1 + (value % (size - 1))) more than once potentially.
    for (index = value % size;
         m_arr[index] >= 0 && m_arr[index] != value;
         index = (index + (1 + (value % (size - 1)))) % size) { }
    return m_arr[index];
  }

  /**
   * Removes all elements from this hashtable.  This is a constant time
   * operation.
   */
  public final void empty()
  {
    m_elements = 0;
    m_size = INITIAL_SIZE;
    m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_size);
    for (int i = 0; i < m_size; i++) m_arr[i] = -1;
  }

  /**
   * Returns an enumeration of elements in this hashtable, ordered
   * arbitrarily.<p>
   * The returned enumeration becomes "invalid" as soon as any other method
   * on this hashtable instance is called; calling methods on an invalid
   * enumeration will cause undefined behavior in the enumerator.
   * The returned enumerator has absolutely no effect on the underlying
   * hashtable.<p>
   * This method returns a value in constant time.  The returned enumerator
   * returns successive elements in [amortized] constant time.
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

  private final void checkSize()
  {
    if (m_elements >= m_thresholdSize)
    {
      final int newSize;
      try {
        int primesInx = 0;
        while (m_arr.length != PRIMES[primesInx++]) { }
        newSize = PRIMES[primesInx]; }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new IllegalStateException
          ("too many elements in this hashtable"); }
      int[] dump = new int[m_elements];
      int index = -1;
      for (int i = 0; i < dump.length; i++) {
        while (m_arr[++index] < 0) { }
        dump[i] = m_arr[index];
        m_arr[index] = -1; }
      if (m_arr.length < newSize) {
        final int[] newArr = new int[newSize];
        m_arr = newArr; }
      for (int i = m_size; i < newSize; i++) m_arr[i] = -1;
      m_elements = 0;
      m_size = newSize;
      m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_size);
      for (int i = 0; i < dump.length; i++) put(dump[i]);
    }
  } 

}

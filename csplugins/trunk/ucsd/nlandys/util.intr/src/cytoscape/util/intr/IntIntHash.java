package cytoscape.util.intr;

/**
 * An insert-only hashtable that has non-negative 32 bit integer keys and
 * non-negative 32 bit integer values.<p>
 * In the underlying implementation, this hashtable increases in size to adapt
 * to key/value pairs being added (the underlying size of the hashtable is
 * invisible to the programmer).  In the underlying implementation, this
 * hashtable never decreases in size.  As a hashtable increases in size,
 * it takes at most four times as much memory as it would take
 * to store the hashtable's keys and values in a perfectly-sized array.
 * Underlying size expansions are implemented such that the operation of
 * expanding in size is amortized over the contstant time complexity needed to
 * insert new elements.
 */
public final class IntIntHash
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

  private int[] m_keys;
  private int[] m_vals;
  private int m_elements;
  private int m_size;
  private int m_thresholdSize;

  /**
   * Create a new hashtable.
   */
  public IntIntHash()
  {
    m_keys = new int[INITIAL_SIZE];
    m_vals = new int[INITIAL_SIZE];
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
    for (int i = 0; i < m_size; i++) { m_keys[i] = -1; m_vals[i] = -1; }
  }

  /**
   * Puts a new key/value pair into this hashtable, potentially overwriting
   * an existing value whose key is the same as the one specified.
   * Returns the old value associated with the specified key or -1 if no value
   * is associated with specified key at the time of this call.<p>
   * Only non-negative keys and values can be passed to this method.
   * Behavior is undefined if negative values are passed to put(int, int).<p>
   * Insertions into the hashtable are performed in [amortized] time
   * complexity O(1).
   */
  public final int put(final int key, final int value)
  {
    checkSize();
    int incr = 0;
    int index;
    for (index = key % (((~key) >>> 31) * m_size);
         m_keys[index] >= 0 && m_keys[index] != key;
         index = (index + incr) % m_size) {
      // Caching increment, which is an expensive operation, at the expense
      // of having an if statement.  I don't want to compute the increment
      // before this 'for' loop in case we get an immediate hit.
      if (incr == 0) { incr = 1 + (key % (m_size - 1)); } }
    final int returnVal = m_vals[index];
    // One if and only if value is non-negative.
    final int oneOrZero = (~value) >>> 31;
    // Make this throw ArrayIndexOutOfBoundException if value is negative.
    m_vals[(index * oneOrZero) + (oneOrZero - 1)] = value;
    m_keys[index] = key;
    m_elements += (returnVal >>> 31);
    return returnVal;
  }

  /**
   * Returns the value bound to the specified key or -1 if no value is
   * currently bound to the specified key.<p>
   * It is an error to pass negative keys to this method.  Passing
   * negative values to this method will result in undefined behavior of
   * this hashtable.<p>
   * Searches in this hashtable are performed in [amortized] time
   * complexity O(1).
   */
  public final int get(final int key)
  {
    int incr = 0;
    int index;
    for (index = key % (((~key) >>> 31) * m_size);
         m_keys[index] >= 0 && m_keys[index] != key;
         index = (index + incr) % m_size) {
      if (incr == 0) { incr = 1 + (key % (m_size - 1)); } }
    return m_vals[index];
  }

  /**
   * Returns an enumeration of keys in this hashtable, ordered
   * arbitrarily.<p>
   * The returned enumeration becomes "invalid" as soon as any other method
   * on this hashtable instance is called; calling methods on an invalid
   * enumeration will cause undefined behavior in the enumerator.  Actually,
   * one method will not invalidate this enumeration: the get(int) method.
   * The returned enumerator has absolutely no effect on the underlying
   * hashtable.<p>
   * This method returns a value in constant time.  The returned enumerator
   * returns successive keys in [amortized] time complexity O(1).
   */
  public final IntEnumerator keys()
  {
    return enumeration(m_keys);
  }

  /**
   * Returns an enumeration of values in this hashtable, ordered
   * arbitrarily.<p>
   * The returned enumeration becomes "invalid" as soon as any other method
   * on this hashtable instance is called; calling methods on an invalid
   * enumeration will cause undefined behavior in the enumerator.  Actually,
   * one method will not invalidate this enumeration: the get(int) method.
   * The returned enumerator has absolutely no effect on the underlying
   * hashtable.<p>
   * This method returns a value in constant time.  The returned enumerator
   * returns successive values in [amortized] time complexity O(1).
   */
  public final IntEnumerator values()
  {
    return enumeration(m_vals);
  }

  private final IntEnumerator enumeration(final int[] arr)
  {
    final int numElements = m_elements;
    return new IntEnumerator() {
        int elements = numElements;
        int index = -1;
        public int numRemaining() { return elements; }
        public int nextInt() {
          while (arr[++index] < 0) { }
          elements--;
          return arr[index]; } };
  }

  private int[] m_keyDump = null;
  private int[] m_valDump = null;

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
      if (m_keys.length < newSize) {
        final int[] newKeys = new int[newSize];
        final int[] newVals = new int[newSize];
        m_keyDump = m_keys; m_valDump = m_vals;
        m_keys = newKeys; m_vals = newVals; }
      else {
        System.arraycopy(m_keys, 0, m_keyDump, 0, m_size);
        System.arraycopy(m_vals, 0, m_valDump, 0, m_size); }
      for (int i = 0; i < newSize; i++) { m_keys[i] = -1; m_vals[i] = -1; }
      m_size = newSize;
      m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_size);
      int incr;
      int newIndex;
      int oldIndex = -1;
      for (int i = 0; i < m_elements; i++) {
        while (m_keyDump[++oldIndex] < 0) { }
        incr = 0;
        for (newIndex = m_keyDump[oldIndex] % m_size;
             m_keys[newIndex] >= 0;
             newIndex = (newIndex + incr) % m_size) {
          if (incr == 0) { incr = 1 + (m_keyDump[oldIndex] % (m_size - 1)); } }
        m_keys[newIndex] = m_keyDump[oldIndex];
        m_vals[newIndex] = m_valDump[oldIndex]; }
    }
  }

}

package cytoscape.util.intr;

/**
 * An insert-only hashtable that has non-negative 32 bit integer keys and
 * non-negative 32 bit integer values.<p>
 * In the underlying implementation, this hashtable increases in size to adapt
 * to key/value pairs being added (the underlying size of the hashtable is
 * invisible to the programmer).  In the underlying implementation, this
 * hashtable never decreases in size.  As a hashtable increases in size,
 * it takes at most 2.66 times as much memory as it would take
 * to store the hashtable's elements in a perfectly-sized array.
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
      // Caching increment, which is an expensive operation, at the expense
      // of having an if statement.  I don't want to compute the increment
      // before this 'for' loop in case we get an immediate hit.
      if (incr == 0) { incr = 1 + (key % (m_size - 1)); } }
    return m_vals[index];
  }

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
      int[] keyDump = new int[m_elements];
      int[] valDump = new int[m_elements];
      int index = -1;
      for (int i = 0; i < keyDump.length; i++) {
        while (m_keys[++index] < 0) { }
        keyDump[i] = m_keys[index];
        valDump[i] = m_vals[index];
        m_keys[index] = -1;
        m_vals[index] = -1; }
      if (m_keys.length < newSize) {
        final int[] newKeys = new int[newSize];
        final int[] newVals = new int[newSize];
        System.arraycopy(m_keys, 0, newKeys, 0, m_keys.length);
        System.arraycopy(m_vals, 0, newVals, 0, m_vals.length);
        m_keys = newKeys;
        m_vals = newVals; }
      for (int i = m_size; i < newSize; i++) {
        m_keys[i] = -1; m_vals[i] = -1; }
      m_size = newSize;
      m_thresholdSize = (int) (THRESHOLD_FACTOR * (double) m_size);
      int incr;
      for (int i = 0; i < keyDump.length; i++) {
        incr = 0;
        for (index = keyDump[i] % m_size;
             m_keys[index] >= 0 && m_keys[index] != keyDump[i];
             index = (index + incr) % m_size) {
          // Caching increment, which is an expensive operation, at the expense
          // of having an if statement.  I don't want to compute the increment
          // before this 'for' loop in case we get an immediate hit.
          if (incr == 0) { incr = 1 + (keyDump[i] % (m_size - 1)); } }
        m_keys[index] = keyDump[i];
        m_vals[index] = valDump[i]; }
    }
  }

}

package fing.util;

/**
 * A heap is an implementation of a priority queue.  A priority queue is a
 * special kind of queue where the next value on the queue is the least
 * element in the queue.  A heap can be used to order N elements in
 * O(n*log(N)) time complexity.  Because of some of the propeties of a heap,
 * heaps are especially good at returning the M least elements from a set
 * of N elements, where M < N.  In particular, if M is less than or equal to
 * N/log(N), then the first M least elements from a set of N elements can be
 * computed in O(N) time complexity (linear time).<p>
 * A heap can have two states: ordered and unordered.  The time complexity of
 * certain operations is dependent on the state of the heap.  Certain
 * operation will effect the state of the heap.  Please read
 * the documentation of each method to better understand the time
 * complexity of each operation and its relationship to the state of the
 * heap.
 */
public final class MinIntHeap
{

  // This must be a non-negative integer.
  private static final int DEFAULT_CAPACITY = 11;

  private int[] m_heap;
  private int m_currentSize;
  private boolean m_orderOK;

  /**
   * A new heap is ordered.
   */
  public MinIntHeap()
  {
    m_heap = new int[DEFAULT_CAPACITY + 1];
    m_heap[0] = Integer.MIN_VALUE;
    m_currentSize = 0;
    m_orderOK = true;
  }

  /**
   * This constructor tosses length elements from the array arr, beginning
   * at index beginIndex, onto a new heap.  The new heap will not be
   * ordered.<p>
   * A copy of the input array is made.  The input array is never modified.
   */
  public MinIntHeap(int[] arr, int beginIndex, int length)
  {
    m_heap = new int[length + DEFAULT_CAPACITY + 1];
    System.arraycopy(arr, 0, m_heap, 1, length);
    m_heap[0] = Integer.MIN_VALUE;
    m_currentSize = length;
    m_orderOK = false;
  }

  /**
   * Empties this heap of all elements.  The heap is ordered after this
   * operation.
   */
  public final void empty()
  {
    m_currentSize = 0;
    m_orderOK = true;
  }

  /**
   * Returns the number of elements currently in this heap.
   */
  public final int numElements()
  {
    return m_currentSize;
  }

  /**
   * Returns true if and only if the heap is currently ordered.
   */
  public final boolean isOrdered()
  {
    return m_orderOK;
  }

  /**
   * Tosses a new element onto the heap.  The heap will be become
   * unordered after this operation; this operation takes constant time.
   */
  public final void toss(int x)
  {
    checkSize(1);
    m_heap[++m_currentSize] = x;
    m_orderOK = false;
  }

  /**
   * Tosses a bunch of new elements onto the heap at once.  The heap will
   * become unordered after this operation; this operation takes
   * O(N) time where N is the number of elements being tossed onto the heap.
   * @param elements an array containing elements to be tossed onto this heap.
   * @param beginIndex the index in the elements array from which to start
   *   tossing elements onto this heap.
   * @param length the number of contiguous elements in the elements array
   *   to toss onto this heap.
   */
  public final void toss(int[] elements, int beginIndex, int length)
  {
    if (length < 0)
      throw new IllegalArgumentException("length cannot be negative");
    checkSize(length);
    System.arraycopy(elements, beginIndex,
                     m_heap, m_currentSize + 1, length);
    m_currentSize += length;
    m_orderOK = false;
  }

  /**
   * If this heap is ordered prior to calling this operation, adds
   * specified element to heap such that the heap will remain ordered after
   * this operation, taking O(log(N)) time where N is the number of
   * elements in this heap (average time is actually constant regardless of
   * size of heap).  If this heap is not ordered when this operation is called,
   * adds specified element to heap in constant time.
   */
  public final void insert(int x)
  {
    checkSize(1);
    m_heap[++m_currentSize] = x;
    if (m_orderOK) percolateUp(m_heap, m_currentSize);
  }

  /**
   * Returns the minimum element in this heap.  This is a constant time
   * operation if the heap is ordered.  If the heap is not ordered, this
   * operation will first order the entire heap.  The time complexity of
   * ordering an unordered heap is O(N) where N is the number of elements
   * in the heap.  This method leaves the heap in an ordered state.<p>
   * If there are no elements in this heap, results of this operation
   * are undefined.
   * @see #numElements()
   */
  public final int findMin()
  {
    if (!m_orderOK) { // Fix heap.
      for (int i = m_currentSize / 2; i > 0; i--)
        percolateDown(m_heap, i, m_currentSize);
      m_orderOK = true; }
    return m_heap[1];
  }

  /**
   * Deletes and returns the minimum element in this heap.  This operation
   * has time complexity O(log(N)) where N is the number of elements
   * currently in this heap, assuming that the heap is ordered.  If the
   * heap is not ordered at the time this operation is invoked, this
   * operation will first order the entire heap.  The time complexity of
   * ordering an unordered heap is O(N), where N is the number of elements
   * in the heap.  When this method returns, this heap is in an ordered
   * state.<p>
   * If there are no elements in this heap, results of this operation
   * are undefined.
   * @see #numElements()
   */
  public final int deleteMin()
  {
    if (!m_orderOK) { // Fix heap.
      for (int i = m_currentSize / 2; i > 0; i--)
        percolateDown(m_heap, i, m_currentSize);
      m_orderOK = true; }
    final int returnThis = m_heap[1];
    m_heap[1] = m_heap[m_currentSize--];
    percolateDown(m_heap, 1, m_currentSize);
    return returnThis;
  }

  private final void checkSize(int newElements)
  {
    if (m_currentSize < m_heap.length - newElements) return;
    final int[] newHeap = new int[Math.max(m_heap.length * 2 + 1,
                                           m_heap.length + newElements)];
    System.arraycopy(m_heap, 0, newHeap, 0, m_heap.length);
    m_heap = newHeap;
  }

  private static final void percolateUp(int[] heap,
                                        int childIndex)
  {
    for (int parentIndex = childIndex / 2;
         heap[childIndex] < heap[parentIndex];
         childIndex = parentIndex, parentIndex = parentIndex / 2)
      swap(heap, parentIndex, childIndex);
  }

  private static final void percolateDown(int[] heap,
                                          int parentIndex,
                                          int size)
  {
    for (int childIndex = parentIndex * 2; childIndex <= size;
         parentIndex = childIndex, childIndex = childIndex * 2) {
      if (childIndex + 1 <= size && heap[childIndex + 1] < heap[childIndex])
        childIndex++;
      if (heap[childIndex] < heap[parentIndex])
        swap(heap, parentIndex, childIndex);
      else break; }
  }

  private static final void swap(int[] arr, int index1, int index2)
  {
    int temp = arr[index1];
    arr[index1] = arr[index2];
    arr[index2] = temp;
  }

  /**
   * Returns an enumeration of elements in this heap, ordered such that
   * the least element is first in the returned enumeration.  Pruning of
   * duplicate elements is enabled by setting pruneDuplicates to true.<p>
   * If pruneDuplicates is false, this method returns in constant
   * time (unless this heap is unordered when this method is called, in
   * which case this method returns in O(N) time), and the returned enumeration
   * takes O(log(N)) time complexity to return each successive element.
   * If pruneDuplicates is true, this method takes O(N*log(N)) time
   * complexity to come up with the return value, and
   * the retuned enumeration takes constant time to return each successive
   * element.<p>
   * The returned enumeration becomes "invalid" as soon as any other method
   * on this heap instance is called; calling methods on an invalid enumeration
   * will cause undefined behavior in both the enumerator and in the underlying
   * heap.<p>
   * Calling this function automatically causes this heap to become
   * unordered.  No elements are added or removed from this heap as a
   * result of using the returned enumeration.
   * @see #elements()
   */
  public final IntEnumerator orderedElements(boolean pruneDuplicates)
  {
    final int[] heap = m_heap;
    final int size = m_currentSize;
    if (!m_orderOK) // Fix heap.
      for (int i = size / 2; i > 0; i--) percolateDown(heap, i, size);
    m_orderOK = false; // That's right - the heap becomes unordered.
    if (pruneDuplicates)
    {
      int dups = 0;
      int sizeIter = size;
      while (sizeIter > 1) { // Needs to be 1, not 0, for duplicates.
        swap(heap, 1, sizeIter);
        percolateDown(heap, 1, sizeIter - 1);
        if (heap[1] == heap[sizeIter]) dups++;
        sizeIter--; }
      final int numDuplicates = dups;
      return new IntEnumerator() {
          int m_index = size;
          int m_dups = numDuplicates;
          int m_prevValue = heap[m_index] + 1;
          public int numRemaining() { return m_index - m_dups; }
          public int nextInt() {
            while (heap[m_index] == m_prevValue) {
              m_dups--; m_index--; }
            m_prevValue = heap[m_index--];
            return m_prevValue; } };
    }
    else // Don't prune duplicates.  Do lazy computation.
    {
      return new IntEnumerator() {
          int m_size = size;
          public int numRemaining() { return m_size; }
          public int nextInt() {
            swap(heap, 1, m_size);
            percolateDown(heap, 1, m_size - 1);
            return heap[m_size--]; } };
    }
  }

  /**
   * Returns an enumeration over all the elements currently in this heap;
   * the order of elements in the returned enumeration is undefined.<p>
   * If other methods in this heap are called while enumerating through
   * the return value, behavior of the enumerator is undefined.<p>
   * This enumerator has no effect on the set of element in this heap.  This
   * enumeration has no effect on the ordered state of this heap.
   * @see #orderedElements(boolean)
   */
  public final IntEnumerator elements()
  {
    final int[] heap = m_heap;
    final int size = m_currentSize;
    return new IntEnumerator() {
        int index = 0;
        public int numRemaining() { return size - index; }
        public int nextInt() { return heap[++index]; } };
  }

}

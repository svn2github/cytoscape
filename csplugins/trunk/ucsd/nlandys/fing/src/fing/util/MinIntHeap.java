package fing.util;

/**
 * A heap can have two states: ordered and unordered.
 */
public final class MinIntHeap
{

  private int[] m_heap;
  private int m_currentSize;
  private boolean m_orderOK;
  private static final int DEFAULT_CAPACITY = 11;

  /**
   * A new heap is ordered.
   */
  public MinIntHeap()
  {
    m_heap = new int[DEFAULT_CAPACITY];
    m_heap[0] = Integer.MIN_VALUE;
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
   * Tosses a new element onto the heap.  The order of the heap will be
   * ruined after this operation; this operation takes constant time.
   */
  public final void toss(int x)
  {
    checkSize();
    m_heap[++m_currentSize] = x;
    m_orderOK = false;
  }

  /**
   * If this heap is ordered prior to calling this operation, adds
   * specified element to heap such that the heap will remain ordered after
   * this operation, taking O(log(n)) time where n is the number of
   * elements in this heap (average time is actually constant regardless of
   * size of heap).  If this heap is not ordered when this operation is called,
   * adds specified element to heap in constant time.
   */
  public final void insert(int x)
  {
    checkSize();
    m_heap[++m_currentSize] = x;
    if (m_orderOK) percolateUp(m_heap, m_currentSize);
  }

  /**
   * Returns the minimum element in this heap.  This is a constant time
   * operation if the heap is ordered.  If the heap is not ordered, this
   * operation will first order the entire heap.  The time complexity of
   * ordering an unordered heap is O(n) where n is the number of elements
   * in the heap.<p>
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
   * has time complexity O(log(n)) where n is the number of elements
   * currently in this heap, assuming that the heap is ordered.  If the
   * heap is not ordered at the time this operation is invoked, this
   * operation will first order the entire heap.  The time complexity of
   * ordering an unordered heap is O(n), where n is the number of elements
   * in the heap.<p>
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

  private final void checkSize()
  {
    if (m_currentSize < m_heap.length - 1) return;
    final int[] newHeap = new int[m_heap.length * 2 + 1];
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
   * Returns an iterator of elements in this heap, ordered such that
   * the least element is first in the returned iterator.
   * If both pruneDuplicates and reverseOrder are false,
   * this method returns in O(n) time (unless this heap is ordered when
   * this method is called, in which case this method returns in
   * constant time), and the returned iterator
   * takes O(log(n)) time complexity to return each successive element.
   * If either pruneDuplicates or reverseOrder is true, this method
   * takes O(n*log(n)) time complexity to come up with the return value;
   * the retuned iterator takes constant time to return each successive
   * element.<p>
   * The returned iterator becomes "invalid" as soon as any other method
   * on this heap instance is called; calling methods on an invalid iterator
   * will cause undefined behavior in both the iterator and in the underlying
   * heap.<p>
   * Calling this function automatically causes this heap to become
   * unordered.  No elements are added or removed from this heap as a
   * result of using the returned iterator.
   */
  public final IntIterator orderedElements(boolean pruneDuplicates,
                                           boolean reverseOrder)
  {
    if (pruneDuplicates || reverseOrder)
    {
      return null;
    }
    else // We can do lazy computations.
    {
      if (!m_orderOK) // Fix heap.
        for (int i = m_currentSize / 2; i > 0; i--)
          percolateDown(m_heap, i, m_currentSize);
      else m_orderOK = false;
      final int[] heap = m_heap;
      final int size = m_currentSize;
      return new IntIterator() {
          int index = 0;
          public int numRemaining() { return size - index; }
          public int nextInt()
          {
            
          } };
    }
  }

  /**
   * Returns an iteration over all the elements currently in this heap;
   * the order of elements in the retruned iteration is undefined.<p>
   * If other methods in this heap are called while iterating through
   * the return value, behavior of the iterator is undefined.
   * @see #orderedElements(boolean, boolean)
   */
  public final IntIterator elements()
  {
    final int[] heap = m_heap;
    final int size = m_currentSize;
    return new IntIterator() {
        int index = 0;
        public int numRemaining() { return size - index; }
        public int nextInt() { return heap[++index]; } };
  }

}

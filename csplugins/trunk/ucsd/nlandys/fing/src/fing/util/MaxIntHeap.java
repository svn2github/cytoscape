package fing.util;

public final class MaxIntHeap
{

  private int[] m_heap;
  private int m_currentSize;
  private boolean m_orderOK;
  private static final int DEFAULT_CAPACITY = 11;

  /**
   * A new heap is ordered.
   */
  public MaxIntHeap()
  {
    m_heap = new int[DEFAULT_CAPACITY];
    m_heap[0] = Integer.MAX_VALUE;
    m_currentSize = 0;
    m_orderOK = true;
  }

  /**
   * Tosses a new element onto the heap.  The order of the heap will be
   * ruined after this operation, but this operation only takes constant time.
   */
  public final void toss(int x)
  {
    checkSize();
    m_heap[++m_currentSize] = x;
    m_orderOK = false;
  }

  /**
   * Returns the number of elements currently in this heap.
   */
  public final int numElements()
  {
    return m_currentSize;
  }

  private final void checkSize()
  {
    if (m_currentSize < m_heap.length - 1) return;
    final int[] newHeap = new int[m_heap.length * 2 + 1];
    System.arraycopy(m_heap, 0, newHeap, 0, m_heap.length);
    m_heap = newHeap;
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
    if (m_orderOK) percolateUp(m_currentSize);
  }

  private final void percolateUp(int childIndex)
  {
    for (int parentIndex = childIndex / 2;
         m_heap[childIndex] > m_heap[parentIndex];
         childIndex = parentIndex, parentIndex = parentIndex / 2)
      swap(m_heap, parentIndex, childIndex);
  }

  /**
   * Returns the maximum element in this heap.  This is a constant time
   * operation if the heap is ordered.  If the heap is not ordered, this
   * operation will first order the entire heap.  The time complexity of
   * ordering an unordered heap is O(n) where n is the number of elements
   * in the heap.<p>
   * If there are no elements in this heap, results of this operation
   * are undefined.
   * @see #numElements()
   */
  public final int findMax()
  {
    if (!m_orderOK) fixHeap();
    return m_heap[((m_currentSize == 0) ? -1 : 1)];
  }

  /**
   * Deletes and returns the maximum element in this heap.  This operation
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
  public final int deleteMax()
  {
    final int returnThis = findMax();
    m_heap[1] = m_heap[m_currentSize--];
    percolateDown(1);
    return returnThis;
  }

  private final void percolateDown(int parentIndex)
  {
    for (int childIndex = parentIndex * 2; childIndex <= m_currentSize;
         parentIndex = childIndex, childIndex = childIndex * 2) {
      if (childIndex + 1 <= m_currentSize &&
          m_heap[childIndex + 1] > m_heap[childIndex])
        childIndex++;
      if (m_heap[childIndex] > m_heap[parentIndex])
        swap(m_heap, parentIndex, childIndex);
      else break; }
  }

  private final void fixHeap()
  {
    for (int i = m_currentSize / 2; i > 0; i--) percolateDown(i);
    m_orderOK = true;
  }

  private static final void swap(int[] arr, int index1, int index2)
  {
    int temp = arr[index1];
    arr[index1] = arr[index2];
    arr[index2] = temp;
  }

  public static final int ELEMENTS_ORDER_LEAST_FIRST = 1;
  public static final int ELEMENTS_ORDER_GREATEST_FIRST = 2;
  public static final int ELEMENTS_PRUNE_DUPLICATES = 4;

  /**
   * If both ELEMENTS_ORDER_LEAST_FIRST and ELEMENTS_ORDER_GREATEST_FIRST
   * are specified as flags, results of this operation are undefined.
   */
  public final IntIterator getElements(int flags)
  {
    final boolean orderLFirst = flags & ELEMENTS_ORDER_LEAST_FIRST != 0;
    final boolean orderGFirst = flags & ELEMENTS_ORDER_GREATEST_FIRST != 0;
    if (orderLFirst && orderGFirst)
      throw new IllegalStateException
        ("cannot order least first and greatest first simultaneously");
    final boolean pruneDups = flags & ELEMENTS_PRUNE_DUPLICATES
  }

}

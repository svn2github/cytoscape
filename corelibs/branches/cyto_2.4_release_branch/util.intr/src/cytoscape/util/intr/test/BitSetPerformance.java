package cytoscape.util.intr.test;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.MinIntHeap;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;

public class BitSetPerformance
{

  /**
   * This test is analagous to
   * cytoscape.intr.util.test.MinIntHeapPerformance, only
   * it uses a bit array instead of a heap.  So the purpose of this test
   * is to compare the performance between a heap and a bit array when using
   * these objects solely for the purpose of pruning duplicate integers from a
   * set of integers.
   */
  public static void main(String[] args) throws Exception
  {
    int N = Integer.parseInt(args[0]);
    boolean repeat = false;
    if (args.length > 1 && args[1].equalsIgnoreCase("repeat"))
      repeat = true;
    int[] elements = new int[N];
    InputStream in = System.in;
    byte[] buff = new byte[4];
    int inx = 0;
    int off = 0;
    int read;
    while (inx < N && (read = in.read(buff, off, buff.length - off)) > 0) {
      off += read;
      if (off < buff.length) continue;
      else off = 0;
      elements[inx++] = (0x7fffffff & assembleInt(buff)) % N; }
    if (inx < N) throw new IOException("premature end of input");

    // Lose reference to as much as we can.
    in = null;
    buff = null;

    // Load the classes we're going to use into the classloader.
    _THE_TEST_CASE_(new int[] { 0, 3, 4, 3, 9, 9, 1 });

    // Sleep, collect garbage, have a snack, etc.
    Thread.sleep(1000);

    // Warm up.
    for (int i = 0; i < 100; i++) { int foo = i * 4 / 8; }

    // Start timer.
    long millisBegin = System.currentTimeMillis();

    // Run the test.  Quick, stopwatch is ticking!
    int[] uniqueElements = _THE_TEST_CASE_(elements);

    // Stop timer.
    long millisEnd = System.currentTimeMillis();

    // Print the time taken to standard error.
    if (!repeat) System.err.println(millisEnd - millisBegin);

    // Sort the array.  We can use the same heap that we used as a bucket
    // to now sort the integers.
    IntEnumerator sortedElements = _THE_HEAP_.orderedElements(false);

    // Print sorted array to standard out.
    if (!repeat)
      while (sortedElements.numRemaining() > 0)
        System.out.println(sortedElements.nextInt());

    // Run repeated test if that's what the command line told us.
    if (repeat)
    {
      for (int i = 0; i < uniqueElements.length; i++) uniqueElements[i] = 0;
      millisBegin = System.currentTimeMillis();
      _REPEAT_TEST_CASE_(elements, uniqueElements);
      millisEnd = System.currentTimeMillis();
      System.err.println((millisEnd - millisBegin) + " (repeated test)");
      _THE_HEAP_.empty();
      _THE_HEAP_.toss(uniqueElements, 0, uniqueElements.length);
      while (_THE_HEAP_.size() > 0)
        System.out.println(_THE_HEAP_.deleteMin());
    }
  }

  private static final int assembleInt(byte[] fourConsecutiveBytes)
  {
    int firstByte = (((int) fourConsecutiveBytes[0]) & 0x000000ff) << 24;
    int secondByte = (((int) fourConsecutiveBytes[1]) & 0x000000ff) << 16;
    int thirdByte = (((int) fourConsecutiveBytes[2]) & 0x000000ff) << 8;
    int fourthByte = (((int) fourConsecutiveBytes[3]) & 0x000000ff) << 0;
    return firstByte | secondByte | thirdByte | fourthByte;
  }

  // Keep a reference to our data structure so that we can determine how
  // much memory was consumed by our algorithm (may be implemented in future).
  static BitSet _THE_BIT_ARRAY_ = null;
  // The heap is used only for the purpose of being a bucket of unique
  // integers; we add an integer to this bucket if and only the bit array
  // tells us that the value at index integer is false (and then we set the
  // bit in the array to true).
  static MinIntHeap _THE_HEAP_ = null;

  private static final int[] _THE_TEST_CASE_(final int[] elements)
  {
    // The dynamic range of the integers in this array happens to be the
    // same as the number of integers in this array based on our test case
    // definition.
    _THE_BIT_ARRAY_ = new BitSet(elements.length);
    _THE_HEAP_ = new MinIntHeap();
    for (int i = 0; i < elements.length; i++)
      if (!_THE_BIT_ARRAY_.get(elements[i])) {
        _THE_HEAP_.toss(elements[i]);
        _THE_BIT_ARRAY_.set(elements[i]); }
    final IntEnumerator iter = _THE_HEAP_.elements();
    final int[] returnThis = new int[iter.numRemaining()];
    final int numElements = returnThis.length;
    for (int i = 0; i < numElements; i++) returnThis[i] = iter.nextInt();
    return returnThis;
  }

  private static final void _REPEAT_TEST_CASE_(final int[] elements,
                                               final int[] output)
  {
    _THE_HEAP_.empty();
    _THE_BIT_ARRAY_.clear(0, elements.length);
    for (int i = 0; i < elements.length; i++)
      if (!_THE_BIT_ARRAY_.get(elements[i])) {
        _THE_HEAP_.toss(elements[i]);
        _THE_BIT_ARRAY_.set(elements[i]); }
    if (_THE_HEAP_.size() != output.length)
      throw new IllegalStateException("output array is incorrect size");

    final IntEnumerator iter = _THE_HEAP_.elements();
    final int numElements = output.length;
    for (int i = 0; i < numElements; i++) output[i] = iter.nextInt();
    
    //_THE_HEAP_.copyInto(output, 0);
  }

}

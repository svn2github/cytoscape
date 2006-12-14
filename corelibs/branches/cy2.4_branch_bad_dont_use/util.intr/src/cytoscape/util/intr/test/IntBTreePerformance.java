package cytoscape.util.intr.test;

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;
import cytoscape.util.intr.MinIntHeap;

import java.io.IOException;
import java.io.InputStream;

public class IntBTreePerformance
{

  /**
   * This test is analagous to
   * cytoscape.intr.util.test.MinIntHeapPerformance, only
   * it uses a B-tree instead of a heap.  So the purpose of this test
   * is to compare the performance between a heap and a B-tree when using
   * these objects solely for the purpose of pruning duplicate integers from a
   * set of integers.
   */
  public static void main(String[] args) throws Exception
  {
    int N = Integer.parseInt(args[0]);
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
    System.err.println(millisEnd - millisBegin);

    // Print sorted array to standard out.
    for (int i = 0; i < uniqueElements.length; i++)
      System.out.println(uniqueElements[i]);
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
  static IntBTree _THE_TREE_ = null;
  static IntStack _THE_STACK_ = null;

  private static final int[] _THE_TEST_CASE_(final int[] elements)
  {
    _THE_TREE_ = new IntBTree();
    _THE_STACK_ = new IntStack();
    for (int i = 0; i < elements.length; i++)
      _THE_TREE_.insert(elements[i]);
    final IntEnumerator iter =
      _THE_TREE_.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
    int prevVal = -1;
    if (iter.numRemaining() > 0) {
      prevVal = iter.nextInt();
      _THE_STACK_.push(prevVal); }
    int newVal;
    while (iter.numRemaining() > 0) {
      newVal = iter.nextInt();
      if (newVal == prevVal) continue;
      prevVal = newVal;
      _THE_STACK_.push(prevVal); }
    final int[] returnThis = new int[_THE_STACK_.size()];
    for (int i = _THE_STACK_.size(); i > 0;)
      returnThis[--i] = _THE_STACK_.pop();
    return returnThis;
  }

}

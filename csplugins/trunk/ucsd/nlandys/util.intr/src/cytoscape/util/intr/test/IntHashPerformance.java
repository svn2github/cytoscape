/*
  Copyright (c) 2005, Nerius Landys
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package cytoscape.util.intr.test;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class IntHashPerformance
{

  /**
   * This test is analagous to fing.util.test.MinIntHeapPerformance, only
   * it uses a hashtable instead of a heap.  So the purpose of this test
   * is to compare the performance between a heap and a hashtable when using
   * these object solely for the purpose of pruning duplicate integers from a
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
      elements[inx++] = Math.abs(assembleInt(buff)) % N; }
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

    // Print sorted array to standard out.
    if (!repeat) {
      ArrayList arrList = new ArrayList();
      for (int i = 0; i < uniqueElements.length; i++)
        arrList.add(new Integer(uniqueElements[i]));
      Collections.sort(arrList);
      for (int i = 0; i < arrList.size(); i++)
        System.out.println(((Integer) arrList.get(i)).intValue()); }

    // Run repeated test if that's what the command line told us.
    if (repeat)
    {
      for (int i = 0; i < uniqueElements.length; i++) uniqueElements[i] = 0;
      millisBegin = System.currentTimeMillis();
      _REPEAT_TEST_CASE_(elements, uniqueElements);
      millisEnd = System.currentTimeMillis();
      System.err.println((millisEnd - millisBegin) + " (repeated test)");
      ArrayList arrList = new ArrayList();
      for (int i = 0; i < uniqueElements.length; i++)
        arrList.add(new Integer(uniqueElements[i]));
      Collections.sort(arrList);
      for (int i = 0; i < arrList.size(); i++)
        System.out.println(((Integer) arrList.get(i)).intValue());
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
  static IntHash _THE_HASHTABLE_ = null;

  private static final int[] _THE_TEST_CASE_(int[] elements)
  {
    _THE_HASHTABLE_ = new IntHash();
    for (int i = 0; i < elements.length; i++) {
      _THE_HASHTABLE_.put(elements[i]); }
    final IntEnumerator iter = _THE_HASHTABLE_.elements();
    final int[] returnThis = new int[iter.numRemaining()];
    for (int i = 0; i < returnThis.length; i++)
      returnThis[i] = iter.nextInt();
    return returnThis;
  }

  private static final void _REPEAT_TEST_CASE_(final int[] elements,
                                               final int[] output)
  {
    _THE_HASHTABLE_.empty();
    for (int i = 0; i < elements.length; i++)
      _THE_HASHTABLE_.put(elements[i]);
    IntEnumerator iter = _THE_HASHTABLE_.elements();
    if (iter.numRemaining() != output.length)
      throw new IllegalStateException("output aray is incorrect size");
    for (int i = 0; i < output.length; i++)
      output[i] = iter.nextInt();
  }

}

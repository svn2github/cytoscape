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
import cytoscape.util.intr.MinIntHeap;

public class MinIntHeapTest
{

  public static void main(String[] args)
  {
    MinIntHeap heap = new MinIntHeap();
    System.out.println
      ("Instantiated new MinIntHeap.");
    final int[] arr = new int[] { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 };
    System.out.println
      ("Defined my int[] to be: { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 }.");
    for (int i = 0; i < arr.length; i++) heap.toss(arr[i]);
    System.out.println
      ("Tossed all elements of array onto heap.");
    IntEnumerator iter = heap.orderedElements(true);
    System.out.println
      ("Got an IntEnumerator by calling orderedElements(true) on heap.");
    System.out.println
      ("The enumerator's numRemaining() method returns " +
       iter.numRemaining() + ".");
    System.out.print("The enumerator looks like this: { ");
    while (iter.numRemaining() > 1)
      System.out.print(iter.nextInt() + ", ");
    System.out.println(iter.nextInt() + " }.");
    iter = heap.orderedElements(false);
    System.out.println
      ("Got an IntEnumerator by calling orderedElements(false) on heap.");
    System.out.println("The enumerator's numRemaining() method returns " +
                       iter.numRemaining() + ".");
    System.out.print("The enumerator looks like this: { ");
    while (iter.numRemaining() > 1)
      System.out.print(iter.nextInt() + ", ");
    System.out.println(iter.nextInt() + " }.");
  }

}

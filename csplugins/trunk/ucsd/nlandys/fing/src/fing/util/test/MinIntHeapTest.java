package fing.util.test;

import fing.util.IntIterator;
import fing.util.MinIntHeap;

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
    IntIterator iter = heap.orderedElements(true);
    System.out.println
      ("Got an IntIterator by calling orderedElements(true) on heap.");
    System.out.println
      ("The iterator's numRemaining() method returns " +
       iter.numRemaining() + ".");
    System.out.print("The iterator looks like this: { ");
    while (iter.numRemaining() > 1)
      System.out.print(iter.nextInt() + ", ");
    System.out.println(iter.nextInt() + " }.");
    iter = heap.orderedElements(false);
    System.out.println
      ("Got an IntIterator by calling orderedElements(false) on heap.");
    System.out.println("The iterator's numRemaining() method returns " +
                       iter.numRemaining() + ".");
    System.out.print("The iterator looks like this: { ");
    while (iter.numRemaining() > 1)
      System.out.print(iter.nextInt() + ", ");
    System.out.println(iter.nextInt() + " }.");
  }

}

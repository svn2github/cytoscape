package cytoscape.util.intr.test;

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;

public class IntBTreeTest
{

  public static void main(String[] args)
  {
    IntBTree tree = new IntBTree();
    System.out.println
      ("Instantiated new IntBTree.");
    final int[] arr = new int[] { 5, 8, 1, 3, 8, 3, 0, 9, 1, 2, 7, 3 };
    System.out.print
      ("Defined my int[] to be: { ");
    for (int i = 0; i < arr.length - 1; i++)
      System.out.print(arr[i] + ", ");
    System.out.println(arr[arr.length - 1] + " }.");
    for (int i = 0; i < arr.length; i++) tree.insert(arr[i]);
    System.out.println
      ("Inserted all elements of array into tree.");
    IntEnumerator iter = tree.searchRange(Integer.MIN_VALUE,
                                          Integer.MAX_VALUE);
    System.out.println
      ("Here are the ordered elements:");
    System.out.print
      ("  ");
    while (iter.numRemaining() > 0)
      System.out.print(iter.nextInt() + " ");
    System.out.println();
    final int[] countThese =
      new int[] { Integer.MIN_VALUE, -1, 0, 3, 4, 6, 7, 8, 9, 10,
                  99, Integer.MAX_VALUE };
    for (int i = 0; i < countThese.length; i++)
      System.out.println
        ("The count of integer " + countThese[i] + " is " +
         tree.count(countThese[i]) + ".");
  }

}

package cytoscape.util.intr.test;

import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;

public class IntBTreeTest
{

  public static void main(String[] args)
  {
    IntBTree tree = new IntBTree(3);
    System.out.println
      ("Instantiated new IntBTree.");
    final int[] arr = new int[]
      { 8, 1, 3, 8, 3, 0, 9, 1, 7, 3, 3, 0, 4, 3, 1, 3, 3 };
    System.out.print
      ("My int[] is: { ");
    for (int i = 0; i < arr.length - 1; i++)
      System.out.print(arr[i] + ", ");
    System.out.println(arr[arr.length - 1] + " }.");
    for (int i = 0; i < arr.length; i++) tree.insert(arr[i]);
    System.out.println
      ("Inserted all elements of array into tree.");
    IntEnumerator iter = tree.searchRange(Integer.MIN_VALUE,
                                          Integer.MAX_VALUE, false);
    System.out.println
      ("Here are the ordered elements:");
    System.out.print
      ("  ");
    while (iter.numRemaining() > 0)
      System.out.print(iter.nextInt() + " ");
    System.out.println(".");
    final int[] countThese =
      new int[] { Integer.MIN_VALUE, -1, 0, 1, 3, 4, 6, 7, 8, 9, 10,
                  99, Integer.MAX_VALUE };
    for (int i = 0; i < countThese.length; i++)
      System.out.println
        ("The count of integer " + countThese[i] + " is " +
         tree.count(countThese[i]) + ".");
    final int[] xMins = new int[]
      { Integer.MIN_VALUE, Integer.MIN_VALUE, -23, 1, 3, 2, 3, 8, 4, 4, 5, 6,
        -1, 11 };
    final int[] xMaxs = new int[]
      { Integer.MAX_VALUE, 3, 99, 2, Integer.MAX_VALUE, 6, 4, 8, 4, 8, 6, 6,
        -1, 11 };
    for (int i = 0; i < xMins.length; i++) {
      System.out.println("In range [" + xMins[i] + ", " + xMaxs[i] + "]:");
      System.out.print("  ascending: ");
      iter = tree.searchRange(xMins[i], xMaxs[i], false);
      while (iter.numRemaining() > 0)
        System.out.print(iter.nextInt() + " ");
      System.out.println(".");
      System.out.print("  descending: ");
      iter = tree.searchRange(xMins[i], xMaxs[i], true);
      while (iter.numRemaining() > 0)
        System.out.print(iter.nextInt() + " ");
      System.out.println("."); }
    System.out.println("Now going to delete some random entries.");
    final int[] delInts = new int[] { 8, -1, 3, 2, 9, 3, 0, 3 };
    for (int i = 0; i < delInts.length; i++) {
      if (tree.delete(delInts[i])) {
        System.out.println("Deletion of integer " + delInts[i] +
                           " successful.");
        iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        System.out.print("  Current ordered elements: ");
        while (iter.numRemaining() > 0) System.out.print(iter.nextInt() + " ");
        System.out.println("."); }
      else {
        System.out.println("Deletion of integer " + delInts[i] +
                           " unsuccessful."); } }
    iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
    System.out.println("Now going to delete remaining entries.");
    final int[] intsRemaining = new int[iter.numRemaining()];
    for (int i = 0; i < intsRemaining.length; i++)
      intsRemaining[i] = iter.nextInt();
    for (int i = 0; i < intsRemaining.length; i++) {
      final int delInt =
        intsRemaining[(i + (intsRemaining.length / 2)) % intsRemaining.length];
      if (tree.delete(delInt)) {
        System.out.println("Deletion of integer " + delInt + " successful.");
        iter = tree.searchRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        System.out.print("  Current ordered elements: ");
        while (iter.numRemaining() > 0) System.out.print(iter.nextInt() + " ");
        System.out.println("."); }
      else {
        System.out.println("Deletion of integer " + delInt +
                           " unsuccessful."); } }
  }

}

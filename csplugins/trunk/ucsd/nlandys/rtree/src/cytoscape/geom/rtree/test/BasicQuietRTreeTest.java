package cytoscape.geom.rtree.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.util.intr.IntBTree;
import cytoscape.util.intr.IntEnumerator;

public class BasicQuietRTreeTest
{

  public static void main(String[] args)
  {
    RTree tree = new RTree(3);

    { // BEGIN EMPTY TREE TESTS: We run our first tests when this tree empty.
      double[] extentsArr = new double[4];
      IntEnumerator iter = tree.queryOverlap
        (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
         Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, extentsArr, 0);
      if (iter.numRemaining() != 0)
        throw new IllegalStateException
          ("did not expect query to generate results");
      if (extentsArr[0] != Double.POSITIVE_INFINITY ||
          extentsArr[1] != Double.POSITIVE_INFINITY ||
          extentsArr[2] != Double.NEGATIVE_INFINITY ||
          extentsArr[3] != Double.NEGATIVE_INFINITY)
        throw new IllegalStateException
          ("expected query to return inverted infinite extents");

      if (tree.exists(0, extentsArr, 0))
        throw new IllegalStateException("did not expect there to be an entry");
    } // END EMPTY TREE TESTS.

    tree.insert(0, 0.0, 0.0, 1.0, 1.0);
    tree.insert(1, 2.0, 2.0, 3.0, 3.0);
    tree.insert(2, 0.5, 1.0, 1.5, 2.0);

    { // BEGIN ROOT LEAF TEST: Still before any split.
      double[] extentsArr = new double[5];
      for (int i = 0; i < 3; i++)
        if (!tree.exists(i, extentsArr, 0))
          throw new IllegalStateException("entry " + i + " does not exist");
      if (tree.exists(3, extentsArr, 0))
        throw new IllegalStateException("entry 3 exits");
      if (extentsArr[0] != 0.5 || extentsArr[1] != 1.0 ||
          extentsArr[2] != 1.5 || extentsArr[3] != 2.0)
        throw new IllegalStateException("entry's extents don't match");

      IntEnumerator iter = tree.queryOverlap
        (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
         Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, extentsArr, 0);
      if (iter.numRemaining() != 3)
        throw new IllegalStateException("expected query to generate 3 hits");
      IntBTree cache = new IntBTree();
      for (int i = 0; i < 3; i++) cache.insert(i);
      int foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 3) throw new IllegalStateException
                      ("iter claimed it had 3 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("iter returned wrong objKeys");
      if (extentsArr[0] != 0.0 || extentsArr[1] != 0.0 ||
          extentsArr[2] != 3.0 || extentsArr[3] != 3.0)
        throw new IllegalStateException("extents from query wrong");

      iter = tree.queryOverlap(1.25, 2.0, 2.1, 3.3, extentsArr, 1);
      if (iter.numRemaining() != 2)
        throw new IllegalStateException("exptected query to return 2 hits");
      cache.insert(1); cache.insert(2); foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 2) throw new IllegalStateException
                      ("iter claimed it had 2 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("iter returned wrong objKeys");
      if (extentsArr[1] != 0.5 || extentsArr[2] != 1.0 ||
          extentsArr[3] != 3.0 || extentsArr[4] != 3.0)
        throw new IllegalStateException("extents from query wrong");
    } // END ROOT LEAF TEST.

    tree.insert(3, 2.5, 0.5, 3.5, 1.5);

    { // BEGIN SIMPLE ROOT SPLIT TEST: Minimum # entries with a split.
      double[] extentsArr = new double[4];
      for (int i = 0; i < 4; i++)
        if (!tree.exists(i, extentsArr, 0))
          throw new IllegalStateException("entry " + i + " does not exist");
      if (tree.exists(4, extentsArr, 0))
        throw new IllegalStateException("entry 4 exists");
      if (extentsArr[0] != 2.5 || extentsArr[1] != 0.5 ||
          extentsArr[2] != 3.5 || extentsArr[3] != 1.5)
        throw new IllegalStateException("entry's extents incorrect");

      IntEnumerator iter = tree.queryOverlap
        (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
         Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, extentsArr, 0);
      if (iter.numRemaining() != 4)
        throw new IllegalStateException("expected query to generate 4 hits");
      IntBTree cache = new IntBTree();
      for (int i = 0; i < 4; i++) cache.insert(i);
      int foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 4) throw new IllegalStateException
                      ("iter claimed it had 3 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("iter returned wrong objKeys");
      if (extentsArr[0] != 0.0 || extentsArr[1] != 0.0 ||
          extentsArr[2] != 3.5 || extentsArr[3] != 3.0)
        throw new IllegalStateException("extents from query wrong");

      iter = tree.queryOverlap(2.0, 0.5, 2.2, 1.9, extentsArr, 0);
      if (iter.numRemaining() != 0)
        throw new IllegalStateException("expected query to generate 0 hits");
      if (extentsArr[0] != Double.POSITIVE_INFINITY ||
          extentsArr[1] != Double.POSITIVE_INFINITY ||
          extentsArr[2] != Double.NEGATIVE_INFINITY ||
          extentsArr[3] != Double.NEGATIVE_INFINITY)
        throw new IllegalStateException
          ("query extents - expected inverted infinite");

      iter = tree.queryOverlap
        (Double.NEGATIVE_INFINITY, 1.1, Double.POSITIVE_INFINITY, 1.2,
         extentsArr, 0);
      if (iter.numRemaining() != 2)
        throw new IllegalStateException("expected query to generate 2 hits");
      cache.insert(2); cache.insert(3); foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 2) throw new IllegalStateException
                      ("iter claimed it had 2 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("query returned wrong objKeys");
      if (extentsArr[0] != 0.5 || extentsArr[1] != 0.5 ||
          extentsArr[2] != 3.5 || extentsArr[3] != 2.0)
        throw new IllegalStateException("extents from query wrong");

      iter = tree.queryOverlap(1.0, 1.0, 1.0, 1.0, extentsArr, 0);
      if (iter.numRemaining() != 2)
        throw new IllegalStateException("expected query to generate 2 hits");
      cache.insert(0); cache.insert(2); foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 2) throw new IllegalStateException
                      ("iter claimed it had 2 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("query returned wrong objKeys");
      if (extentsArr[0] != 0.0 || extentsArr[1] != 0.0 ||
          extentsArr[2] != 1.5 || extentsArr[3] != 2.0)
        throw new IllegalStateException("extents from query wrong");
    } // END SIMPLE ROOT SPLIT TEST.

    { // BEGIN EXCEPTION HANDLING TEST.
      boolean exceptionCaught = false;
      try { tree.insert(0, 0.0, 0.0, 1.0, 1.0); }
      catch (IllegalStateException e) { exceptionCaught = true; }
      if (!exceptionCaught) throw new IllegalStateException
                              ("expected exception for duplicate objKey");

      exceptionCaught = false;
      try { tree.insert(-1, 0.0, 0.0, 1.0, 1.0); }
      catch (IllegalArgumentException e) { exceptionCaught = true; }
      if (!exceptionCaught) throw new IllegalStateException
                              ("expected exception for negative objKey");

      exceptionCaught = false;
      try { tree.insert(5, 1.0, 1.0, 0.0, 0.0); }
      catch (IllegalArgumentException e) { exceptionCaught = true; }
      if (!exceptionCaught) throw new IllegalStateException
                              ("expected exception for min > max");
    } // END EXCEPTION HANDLING TEST.

    tree.insert(4, 3.0, -0.25, 4.0, 0.75);
    tree.insert(5, -0.5, 2.5, 0.5, 3.5);
    tree.insert(6, 2.75, 2.25, 3.75, 3.25);
    tree.insert(7, 1.25, 1.75, 2.25, 2.75);
    tree.insert(8, 1.0, 6.0, 2.0, 7.0);
    tree.insert(9, -2.0, 1.0, -1.0, 2.0);

    { // BEGIN DEPTH THREE TEST.
      double[] extentsArr = new double[4];
      for (int i = 9; i >= 0; i--)
        if (!tree.exists(i, extentsArr, 0))
          throw new IllegalStateException("entry " + i + " does not exist");
      if (tree.exists(-1, extentsArr, 0) || tree.exists(10, extentsArr, 0))
        throw new IllegalStateException("bad entry exists");
      if (extentsArr[0] != 0.0 || extentsArr[1] != 0.0 ||
          extentsArr[2] != 1.0 || extentsArr[3] != 1.0)
        throw new IllegalStateException("entry's extents incorrect");

      IntEnumerator iter = tree.queryOverlap
        (Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
         Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, extentsArr, 0);
      if (iter.numRemaining() != 10)
        throw new IllegalStateException("expected query to generate 10 hits");
      IntBTree cache = new IntBTree();
      for (int i = 0; i < 10; i++) cache.insert(i);
      int foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 10) throw new IllegalStateException
                       ("iter claimed it had 10 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("iter returned wrong objKeys");
      if (extentsArr[0] != -2.0 || extentsArr[1] != -0.25 ||
          extentsArr[2] != 4.0 || extentsArr[3] != 7.0)
        throw new IllegalStateException("extents from query wrong");

      iter = tree.queryOverlap(1.0, 1.25, 3.0, 5.0, extentsArr, 0);
      if (iter.numRemaining() != 5)
        throw new IllegalStateException("expected query to generate 5 hits");
      cache.insert(1); cache.insert(2); cache.insert(3); cache.insert(6);
      cache.insert(7); foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 5) throw new IllegalStateException
                      ("iter claimed it had 5 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("query returned wrong objKeys");
      if (extentsArr[0] != 0.5 || extentsArr[1] != 0.5 ||
          extentsArr[2] != 3.75 || extentsArr[3] != 3.25)
        throw new IllegalStateException("extents from query wrong");

      iter = tree.queryOverlap(-1.5, 0.25, 0.25, 3.0, extentsArr, 0);
      if (iter.numRemaining() != 3)
        throw new IllegalStateException("expected query to generate 3 hits");
      cache.insert(0); cache.insert(5); cache.insert(9); foo = 0;
      while (iter.numRemaining() > 0) { cache.delete(iter.nextInt()); foo++; }
      if (foo != 3) throw new IllegalStateException
                      ("query claimed it had 3 elements but really didn't");
      if (cache.size() != 0) throw new IllegalStateException
                               ("query returned wrong objKeys");
      if (extentsArr[0] != -2.0 || extentsArr[1] != 0.0 ||
          extentsArr[2] != 1.0 || extentsArr[3] != 3.5)
        throw new IllegalStateException("extents from query wrong");
    } // END DEPTH THREE TEST.
  }

}

package cytoscape.geom.rtree.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;
import java.io.IOException;
import java.io.InputStream;

public class RTreeQueryPerformance
{

  /**
   * For given N, creates N rectangles whose centers are in the space
   * [0,1] X [0,1].  Each rectangle has width and height no greater
   * than 1/sqrt(N).  The location of the centers of the rectangles
   * and the choice of width and height for each rectangle is determined
   * by the input stream, which in most cases will be a randomly generated
   * stream of bytes.  Please see the actual code for an explanation of
   * how the input stream of bytes is converted into the rectangle
   * information.
   */
  public static void main(String[] args) throws Exception
  {
    final RTree tree;

    // Populate the tree with entries.
    {
      int branches = Integer.parseInt(args[0]);
      int N = Integer.parseInt(args[1]);
      tree = new RTree(branches);
      double sqrtN = Math.sqrt((double) N);
      InputStream in = System.in;
      byte[] buff = new byte[16];
      int inx = 0;
      int off = 0;
      int read;
      while (inx < N && (read = in.read(buff, off, buff.length - off)) > 0) {
        off += read;
        if (off < buff.length) continue;
        else off = 0;
        int nonnegative = 0x7fffffff & assembleInt(buff, 0);
        double centerX = ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & assembleInt(buff, 4);
        double centerY = ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & assembleInt(buff, 8);
        double width =
          (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
        nonnegative = 0x7fffffff & assembleInt(buff, 12);
        double height =
          (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
        tree.insert(inx,
                    centerX - (width / 2.0d),
                    centerY - (width / 2.0d),
                    centerX + (width / 2.0d),
                    centerY + (width / 2.0d));
        inx++; }
      if (inx < N) throw new IOException("premature end of input");
    }

    final IntStack[] pointQueries;

    // Test 121 Point queries.
    {
      pointQueries = new IntStack[121];
      for (int i = 0; i < pointQueries.length; i++)
        pointQueries[i] = new IntStack();
      for (int i = 0; i < 3; i++) { System.gc(); Thread.sleep(1000); }
      final long millisBegin = System.currentTimeMillis();
      int inx = 0;
      double currX = -0.1d;
      for (int i = 0; i < 11; i++) {
        currX += 0.1d;
        double currY = -0.1d;
        for (int j = 0; j < 11; j++) {
          currY += 0.1d;
          final IntEnumerator iter =
            tree.queryOverlap(currX, currY, currX, currY, null, 0);
          final IntStack stack = pointQueries[inx++];
          while (iter.numRemaining() > 0) stack.push(iter.nextInt()); } }
      final long millisEnd = System.currentTimeMillis();
      System.err.println("point queries took " + (millisEnd - millisBegin) +
                         " milliseconds");
    }

    final IntStack[] areaQueries;

    // Test 5 area queries - each area is 0.1 X 0.1.
    {
      areaQueries = new IntStack[5];
      for (int i = 0; i < areaQueries.length; i++)
        areaQueries[i] = new IntStack();
      for (int i = 0; i < 3; i++) { System.gc(); Thread.sleep(1000); }
      final long millisBegin = System.currentTimeMillis();
      for (int i = 0; i < 5; i++) {
        final IntEnumerator iter =
          tree.queryOverlap(((double) i) * 0.1d,
                            ((double) i) * 0.1d,
                            ((double) (i + 1)) * 0.1d,
                            ((double) (i + 1)) * 0.1d, null, 0);
        final IntStack stack = areaQueries[i];
        while (iter.numRemaining() > 0) stack.push(iter.nextInt()); }
      final long millisEnd = System.currentTimeMillis();
      System.err.println("area queries took " + (millisEnd - millisBegin) +
                         " milliseconds");
    }

    final int[] countQueries;

    // Test 5 count queries - each area is 0.6 X 0.6.
    {
      countQueries = new int[5];
      for (int i = 0; i < 3; i++) { System.gc(); Thread.sleep(1000); }
      final long millisBegin = System.currentTimeMillis();
      for (int i = 0; i < 5; i++) {
        final IntEnumerator iter =
          tree.queryOverlap(((double) i) * 0.1d,
                            ((double) i) * 0.1d,
                            ((double) (i + 6)) * 0.1d,
                            ((double) (i + 6)) * 0.1d, null, 0);
        countQueries[i] = iter.numRemaining(); }
      final long millisEnd = System.currentTimeMillis();
      System.err.println("count queries took " + (millisEnd - millisBegin) +
                         " milliseconds");
    }
  }

  private static int assembleInt(byte[] bytes, int offset)
  {
    int firstByte = (((int) bytes[offset]) & 0x000000ff) << 24;
    int secondByte = (((int) bytes[offset + 1]) & 0x000000ff) << 16;
    int thirdByte = (((int) bytes[offset + 2]) & 0x000000ff) << 8;
    int fourthByte = (((int) bytes[offset + 3]) & 0x000000ff) << 0;
    return firstByte | secondByte | thirdByte | fourthByte;
  }

}

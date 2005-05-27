package cytoscape.geom.rtree.test;

import cytoscape.geom.rtree.RTree;
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
    int N = Integer.parseInt(args[0]);
    double sqrtN = Math.sqrt((double) N);
    int[] randomData = new int[N * 4];
    InputStream in = System.in;
    byte[] buff = new byte[4];
    int inx = 0;
    int off = 0;
    int read;
    while (inx < randomData.length &&
           (read = in.read(buff, off, buff.length - off)) > 0) {
      off += read;
      if (off < buff.length) continue;
      else off = 0;
      randomData[inx++] = assembleInt(buff); }
    if (inx < randomData.length)
      throw new IOException("premature end of input");

    inx = 0;
    for (int i = 0; i < N; i++) {
      int nonnegative = 0x7fffffff & randomData[inx++];
      double centerX = ((double) nonnegative) / ((double) 0x7fffffff);
      nonnegative = 0x7fffffff & randomData[inx++];
      double centerY = ((double) nonnegative) / ((double) 0x7fffffff);
      nonnegative = 0x7fffffff & randomData[inx++];
      double width = (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
      nonnegative = 0x7fffffff & randomData[inx++];
      double height = (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
      System.out.println("centerX: " + centerX + "  centerY: " + centerY +
                         "  width: " + width + "  height: " + height);
    }

//     double maxDim = 1.0d / Math.sqrt((double) N);
//     final double[] xMins = new int[N];
//     final double[] yMins = new int[N];
//     final double[] xMaxs = new int[N];
//     final double[] yMaxs = new int[N];
  }

  private static final int assembleInt(byte[] fourConsecutiveBytes)
  {
    int firstByte = (((int) fourConsecutiveBytes[0]) & 0x000000ff) << 24;
    int secondByte = (((int) fourConsecutiveBytes[1]) & 0x000000ff) << 16;
    int thirdByte = (((int) fourConsecutiveBytes[2]) & 0x000000ff) << 8;
    int fourthByte = (((int) fourConsecutiveBytes[3]) & 0x000000ff) << 0;
    return firstByte | secondByte | thirdByte | fourthByte;
  }

}

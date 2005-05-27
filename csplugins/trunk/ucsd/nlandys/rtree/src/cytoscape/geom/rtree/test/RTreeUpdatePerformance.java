package cytoscape.geom.rtree.test;

import cytoscape.geom.rtree.RTree;
import java.io.IOException;
import java.io.InputStream;

public class RTreeUpdatePerformance
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
    final double[] data;

    // Populate the array with entries.
    {
      int branches = Integer.parseInt(args[0]);
      int N = Integer.parseInt(args[1]);
      data = new double[N * 4];
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
        data[inx * 4] = centerX - (width / 2.0d);
        data[(inx * 4) + 1] = centerY - (height / 2.0d);
        data[(inx * 4) + 2] = centerX + (width / 2.0d);
        data[(inx * 4) + 3] = centerY + (height / 2.0d);
        inx++; }
      if (inx < N) throw new IOException("premature end of input");
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

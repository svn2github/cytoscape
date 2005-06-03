package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.render.immed.GraphGraphics;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

public final class TestFastNodeRendering extends Frame
{

  public final static void main(String[] args) throws Exception
  {
    final RTree tree;
    final double[] extents;

    // Populate the tree with entries.
    {
      int N = Integer.parseInt(args[0]);
      tree = new RTree();
      extents = new double[N * 4]; // xMin1, yMin1, xMax1, yMax1, xMin2, ....
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
        extents[inx * 4] = centerX - (width / 2.0d);
        extents[(inx * 4) + 1] = centerY - (height / 2.0d);
        extents[(inx * 4) + 2] = centerX + (width / 2.0d);
        extents[(inx * 4) + 3] = centerY + (height / 2.0d);
        tree.insert(inx, extents[inx * 4], extents[(inx * 4) + 1],
                    extents[(inx * 4) + 2], extents[(inx * 4) + 3]);
        inx++; }
      if (inx < N) throw new IOException("premature end of input");
      for (inx = 0; inx < N; inx++) {
        // Re-insert every entry into tree for performance gain.
        tree.delete(inx);
        tree.insert(inx, extents[inx * 4], extents[(inx * 4) + 1],
                    extents[(inx * 4) + 2], extents[(inx * 4) + 3]); }
    }
  }

  public TestFastNodeRendering()
  {
    super();
  }

  public void paint(Graphics g)
  {
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

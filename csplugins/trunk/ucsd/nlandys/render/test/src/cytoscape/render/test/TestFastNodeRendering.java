package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
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

    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestFastNodeRendering(tree, extents);
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final RTree m_tree;
  private final double[] m_extents;
  private final int m_imgWidth = 600;
  private final int m_imgHeight = 480;
  private final BufferedImage m_img;
  private final GraphGraphics m_grafx;
  private final Color m_bgColor = Color.white;
  private final int m_nodeColor = 0x00000000;
  private double m_currXCenter = 0.5d;
  private double m_currYCenter = 0.5d;
  private double m_currScale = 5000.0d;

  public TestFastNodeRendering(RTree tree, double[] extents)
  {
    super();
    m_tree = tree;
    m_extents = extents;
    m_img = new BufferedImage
      (m_imgWidth, m_imgHeight, BufferedImage.TYPE_INT_ARGB);
    m_grafx = new GraphGraphics(m_img);
    updateNodeImage();
  }

  public void paint(final Graphics g)
  {
    final Insets insets = insets();
    updateNodeImage();
    g.setColor(m_bgColor);
    g.fillRect(insets.left, insets.top, m_imgWidth, m_imgHeight);
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  private final void updateNodeImage()
  {
    m_grafx.clear(m_currXCenter, m_currYCenter, m_currScale);
    final IntEnumerator iter = m_tree.queryOverlap
      (m_currXCenter - ((double) (m_imgWidth / 2)) / m_currScale,
       m_currYCenter - ((double) (m_imgHeight / 2)) / m_currScale,
       m_currXCenter + ((double) (m_imgWidth / 2)) / m_currScale,
       m_currYCenter + ((double) (m_imgHeight / 2)) / m_currScale,
       null, 0);
    while (iter.numRemaining() > 0) {
      final int inx = iter.nextInt();
      m_grafx.drawNodeLow(m_extents[inx * 4], m_extents[(inx * 4) + 1],
                          m_extents[(inx * 4) + 2], m_extents[(inx * 4) + 3],
                          m_nodeColor); }
  }

  public boolean isResizable() { return false; }

  private static int assembleInt(byte[] bytes, int offset)
  {
    int firstByte = (((int) bytes[offset]) & 0x000000ff) << 24;
    int secondByte = (((int) bytes[offset + 1]) & 0x000000ff) << 16;
    int thirdByte = (((int) bytes[offset + 2]) & 0x000000ff) << 8;
    int fourthByte = (((int) bytes[offset + 3]) & 0x000000ff) << 0;
    return firstByte | secondByte | thirdByte | fourthByte;
  }

}

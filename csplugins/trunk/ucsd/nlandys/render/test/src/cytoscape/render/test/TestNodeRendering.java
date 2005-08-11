package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.rtree.RTreeEntryEnumerator;
import cytoscape.render.immed.GraphGraphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class TestNodeRendering
  extends Frame implements MouseListener, MouseMotionListener
{

  public final static void main(String[] args) throws Exception
  {
    final RTree tree;
    final byte shape;
    final float borderWidth;
    final Color[] colors;

    // Populate the tree with entries.
    {
      int N = Integer.parseInt(args[0]);
      tree = new RTree();
      double sqrtN = Math.sqrt((double) N);
      if (args.length > 1) {
        shape = Byte.parseByte(args[1]);
        if (args.length > 2) {
          borderWidth = (float) (Float.parseFloat(args[2]) / sqrtN); }
        else {
          borderWidth = (float) (0.025d / sqrtN); } }
      else {
        shape = (byte) -1;
        borderWidth = 0.0f; }
      int inx = 0;
      Random r = new Random();
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        double centerX = ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & r.nextInt();
        double centerY = ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & r.nextInt();
        double width =
          (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
        if (shape >= 0) width = Math.max(width, 6.1d * borderWidth);
        nonnegative = 0x7fffffff & r.nextInt();
        double height =
          (((double) nonnegative) / ((double) 0x7fffffff)) / sqrtN;
        if (shape >= 0) height = Math.max(height, 6.1d * borderWidth);
        if (shape == GraphGraphics.SHAPE_ROUNDED_RECTANGLE &&
            !(Math.max(width, height) <= 2.0d * Math.min(width, height)))
          continue; // Very inefficient.  x2.
        float xMin = (float) (centerX - (width / 2.0d));
        float yMin = (float) (centerY - (height / 2.0d));
        float xMax = (float) (centerX + (width / 2.0d));
        float yMax = (float) (centerY + (height / 2.0d));
        if (xMin == xMax) xMax = (float) (1.0d / sqrtN + xMin);
        if (yMin == yMax) yMax = (float) (1.0d / sqrtN + yMin);
        tree.insert(inx, xMin, yMin, xMax, yMax);
        inx++; }

      colors = new Color[256];
      for (int i = 0; i < colors.length; i++) {
        colors[i] = new Color(0x00ffffff & r.nextInt()); }
    }

    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestNodeRendering(tree, shape, borderWidth, colors);
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final RTree m_tree;
  private final byte m_shape; // -1 if low detail.
  private final float m_borderWidth;
  private final Color[] m_colors;
  private final int m_imgWidth = 1024;
  private final int m_imgHeight = 768;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private final Color m_bgColor = Color.white;
  private final Color m_borderColor = Color.black;
  private final float[] m_extents = new float[4];
  private double m_currXCenter = 0.5d;
  private double m_currYCenter = 0.5d;
  private double m_currScale = 1000.0d;
  private int m_currMouseButton = 0; // 0: none; 1: left; 2: middle.
  private int m_lastXMousePos = 0;
  private int m_lastYMousePos = 0;

  public TestNodeRendering(RTree tree, byte shape,
                           float borderWidth, Color[] colors)
  {
    super();
    m_tree = tree;
    m_shape = shape;
    m_borderWidth = borderWidth;
    m_colors = colors;
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, m_bgColor, false);
    updateNodeImage();
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void paint(final Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  public void update(final Graphics g)
  {
    final Insets insets = insets();
    updateNodeImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private final void updateNodeImage()
  {
    m_grafx.clear(m_currXCenter, m_currYCenter, m_currScale);
    final RTreeEntryEnumerator iter = m_tree.queryOverlap
      ((float) (m_currXCenter - ((double) (m_imgWidth / 2)) / m_currScale),
       (float) (m_currYCenter - ((double) (m_imgHeight / 2)) / m_currScale),
       (float) (m_currXCenter + ((double) (m_imgWidth / 2)) / m_currScale),
       (float) (m_currYCenter + ((double) (m_imgHeight / 2)) / m_currScale),
       null, 0);
    if (m_shape < 0) {
      while (iter.numRemaining() > 0) {
        final int inx = iter.nextExtents(m_extents, 0);
        m_grafx.drawNodeLow(m_extents[0], m_extents[1],
                            m_extents[2], m_extents[3],
                            m_colors[inx & 0x000000ff]); } }
    else {
      while (iter.numRemaining() > 0) {
        final int inx = iter.nextExtents(m_extents, 0);
        m_grafx.drawNodeFull(m_shape, m_extents[0], m_extents[1],
                             m_extents[2], m_extents[3],
                             m_colors[inx & 0x000000ff],
                             m_borderWidth, m_borderColor); } }
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      m_currMouseButton = 1;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      m_currMouseButton = 2;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (m_currMouseButton == 1) m_currMouseButton = 0; }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 1) {
      double deltaX = e.getX() - m_lastXMousePos;
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      m_currXCenter -= deltaX / m_currScale;
      m_currYCenter += deltaY / m_currScale; // y orientations are opposite.
      repaint(); }
    else if (m_currMouseButton == 2) {
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      m_currScale *= Math.pow(2, -deltaY / 300.0d);
      repaint(); }
  }

  public void mouseMoved(MouseEvent e) {}

  public boolean isResizable() { return false; }

}

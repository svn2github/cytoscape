package cytoscape.render.test;

import cytoscape.render.immed.GraphGraphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

public final class TestRoundedRectangle extends Frame implements Runnable
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestRoundedRectangle();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final Object m_lock = new Object();
  private final int m_imgWidth = 600;
  private final int m_imgHeight = 480;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private final AffineTransform m_xform;
  private final double[] m_pts;
  private final float[] m_xsect1Coords;
  private final float[] m_xsect2Coords;

  // State member variables.
  private boolean m_halt = false;
  private boolean m_requestingUpdate = false;
  private boolean m_updating = false;
  private boolean m_firstPaint = true;
  private double m_theta = 0.0d;
  private double m_scale = 1.0d;

  public TestRoundedRectangle()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, Color.white, 0, true);
    m_xform = new AffineTransform();
    m_pts = new double[2];
    m_xsect1Coords = new float[2];
    m_xsect2Coords = new float[2];
    generateImage(m_theta, m_scale);
  }

  public void run()
  {
    long currTime;
    while (!m_halt) {
      currTime = System.currentTimeMillis();
      m_theta = ((double) currTime) / 1500.0d;
      m_scale = (Math.sin(((double) currTime) / 9300.0d) + 1.2d) / 2.0d;
      requestUpdate(); }
  }

  public void paint(Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
    if (m_firstPaint) {
      m_firstPaint = false;
      (new Thread(this)).start(); }
  }

  public void update(Graphics g)
  {
    synchronized (m_lock) {
      if ((!m_requestingUpdate) || m_halt) return;
      m_updating = true; }
    generateImage(m_theta, m_scale);
    paint(g);
    synchronized (m_lock) {
      m_requestingUpdate = false; m_updating = false; m_lock.notify(); }
  }

  /* This triggers an asynchronous call to update(Graphics); returns only
   * after update(Graphics) returns. */
  private void requestUpdate()
  {
    if (m_halt) return;
    m_requestingUpdate = true;
    super.repaint();
    synchronized (m_lock) {
      try {
        if (!m_requestingUpdate || (m_halt && !m_updating)) return;
        m_lock.wait(3000);
        if (m_updating) m_lock.wait(); }
      catch (InterruptedException e) {}
      if (!m_requestingUpdate || (m_halt && !m_updating)) return; }
    throw new RuntimeException("could not update(Graphics)");
  }

  private final void generateImage(double rotationTheta, double m_scale)
  {
    m_xform.setToRotation(rotationTheta);
    m_xform.scale(m_scale, m_scale);
    m_grafx.clear(0.0d, 0.0d, 1.0d);
    double xCenter1 = -180.0d;
    double yCenter1 = -175.0d;
    double width1Div2 = 100.0d;
    double height1Div2 = 50.000012d;
    m_pts[0] = xCenter1;
    m_pts[1] = yCenter1;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    xCenter1 = m_pts[0];
    yCenter1 = m_pts[1];
    double xMin1 = xCenter1 - width1Div2;
    double yMin1 = yCenter1 - height1Div2;
    double xMax1 = xCenter1 + width1Div2;
    double yMax1 = yCenter1 + height1Div2;
    double border = 6.0d;
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                         (float) xMin1, (float) yMin1,
                         (float) xMax1, (float) yMax1,
                         Color.green, (float) border, Color.black); 

    double xCenter2 = 200.0d;
    double yCenter2 = 100.0d;
    double width2Div2 = 50.0d;
    double height2Div2 = 35.0d;
    m_pts[0] = xCenter2;
    m_pts[1] = yCenter2;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    xCenter2 = m_pts[0];
    yCenter2 = m_pts[1];
    double xMin2 = xCenter2 - width2Div2;
    double yMin2 = yCenter2 - height2Div2;
    double xMax2 = xCenter2 + width2Div2;
    double yMax2 = yCenter2 + height2Div2;
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                         (float) xMin2, (float) yMin2,
                         (float) xMax2, (float) yMax2,
                         Color.red, (float) border, Color.black);
    double edgeThickness = 2.0d;
    if (m_grafx.computeEdgeIntersection
        (GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
         (float) xMin1, (float) yMin1, (float) xMax1, (float) yMax1,
         0.0f, (float) xCenter2, (float) yCenter2, m_xsect1Coords) &&
        m_grafx.computeEdgeIntersection
        (GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
         (float) xMin2, (float) yMin2, (float) xMax2, (float) yMax2,
         (float) (Math.sqrt(25.0d * 25.0d + 10.0d * 10.0d) - 25.0d),
         (float) xCenter1, (float) yCenter1, m_xsect2Coords) &&
        // If dot product of original line and new line is greater than zero,
        // which means that the line orientation has not flipped or
        // degenerated.
        (xCenter1 - xCenter2) *
        (((double) m_xsect1Coords[0]) - m_xsect2Coords[0]) +
        (yCenter1 - yCenter2) *
        (((double) m_xsect1Coords[1]) - m_xsect2Coords[1]) > 0.0d)
      m_grafx.drawEdgeFull(GraphGraphics.ARROW_DIAMOND, 20.0f, Color.blue,
                           GraphGraphics.ARROW_DELTA, 20.0f, Color.magenta,
                           m_xsect1Coords[0], m_xsect1Coords[1],
                           m_xsect2Coords[0], m_xsect2Coords[1],
                           (float) edgeThickness, Color.orange, 0.0f);
  }

  public boolean isResizable() { return false; }

}

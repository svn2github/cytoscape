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

public final class TestBalls extends Frame implements Runnable
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestBalls();
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
  private final float[] m_pts;
  private final float[] m_xsect1Coords;
  private final float[] m_xsect2Coords;

  // State member variables.
  private boolean m_halt = false;
  private boolean m_requestingUpdate = false;
  private boolean m_updating = false;
  private boolean m_firstPaint = true;
  private double m_theta = 0.0d;

  public TestBalls()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, Color.white, true);
    m_xform = new AffineTransform();
    m_pts = new float[2];
    m_xsect1Coords = new float[2];
    m_xsect2Coords = new float[2];
    generateImage(m_theta);
  }

  public void run()
  {
    long prevTime = System.currentTimeMillis();
    while (!m_halt) {
      final long elapsedTime = System.currentTimeMillis() - prevTime;
      m_theta += ((double) elapsedTime) / 2000.0d;
      requestUpdate();
      prevTime += elapsedTime; }
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
    generateImage(m_theta);
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

  private final void generateImage(double rotationTheta)
  {
    m_xform.setToRotation(rotationTheta);
    m_grafx.clear(0.0d, 0.0d, 1.0d);
    float xCenter1 = -180.0f;
    float yCenter1 = -175.0f;
    float width1Div2 = 100.0f;
    float height1Div2 = 35.0f;
    m_pts[0] = xCenter1;
    m_pts[1] = yCenter1;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    xCenter1 = m_pts[0];
    yCenter1 = m_pts[1];
    float xMin1 = xCenter1 - width1Div2;
    float yMin1 = yCenter1 - height1Div2;
    float xMax1 = xCenter1 + width1Div2;
    float yMax1 = yCenter1 + height1Div2;
    float border = 6.0f;
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ELLIPSE,
                         xMin1, yMin1, xMax1, yMax1,
                         Color.red, border, Color.black); 

    float offset = 10.0f;
    float xCenter2 = 200.0f;
    float yCenter2 = 100.0f;
    float width2Div2 = 60.0f;
    float height2Div2 = 55.0f;
    m_pts[0] = xCenter2;
    m_pts[1] = yCenter2;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    xCenter2 = m_pts[0];
    yCenter2 = m_pts[1];
    float xMin2 = xCenter2 - width2Div2;
    float yMin2 = yCenter2 - height2Div2;
    float xMax2 = xCenter2 + width2Div2;
    float yMax2 = yCenter2 + height2Div2;
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_VEE,
                         xMin2, yMin2, xMax2, yMax2,
                         Color.red, border, Color.black);
    float edgeThickness = 2.0f;
    float dashLength = 0.0f;
    if (m_grafx.computeEdgeIntersection
        (GraphGraphics.SHAPE_ELLIPSE, xMin1, yMin1, xMax1, yMax1, offset,
         xCenter2, yCenter2, m_xsect1Coords) &&
        m_grafx.computeEdgeIntersection
        (GraphGraphics.SHAPE_VEE, xMin2, yMin2, xMax2, yMax2, offset,
         xCenter1, yCenter1, m_xsect2Coords)) {
      m_grafx.drawEdgeFull(GraphGraphics.ARROW_DISC, offset * 2.0f, Color.blue,
                           GraphGraphics.ARROW_DISC, offset * 2.0f, Color.gray,
                           m_xsect1Coords[0], m_xsect1Coords[1],
                           m_xsect2Coords[0], m_xsect2Coords[1],
                           edgeThickness, Color.green, dashLength); }
  }

  public boolean isResizable() { return false; }

}

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
    generateImage(m_theta);
  }

  public void run()
  {
    long prevTime = System.currentTimeMillis();
    while (!m_halt) {
      final long elapsedTime = System.currentTimeMillis() - prevTime;
      m_theta += ((double) elapsedTime) / 4000.0d;
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
    float xCenter = -180.0f;
    float yCenter = -175.0f;
    float widthDiv2 = 100.0f;
    float heightDiv2 = 35.0f;
    m_pts[0] = xCenter; m_pts[1] = yCenter;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    float xMin = m_pts[0] - widthDiv2;
    float yMin = m_pts[1] - heightDiv2;
    float xMax = m_pts[0] + widthDiv2;
    float yMax = m_pts[1] + heightDiv2;
    float border = 10.0f;
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ELLIPSE,
                         xMin, yMin, xMax, yMax,
                         Color.red, border, Color.black); 

    final float[] xsectCoords = new float[2];
    float offset = 10.0f;
    float ptX = 200.0f;
    float ptY = 100.0f;
    float edgeThickness = 2.0f;
    float dashLength = 0.0f;
    m_pts[0] = ptX; m_pts[1] = ptY;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    ptX = m_pts[0]; ptY = m_pts[1];
    if (m_grafx.computeEdgeIntersection
        (GraphGraphics.SHAPE_ELLIPSE, xMin, yMin, xMax, yMax, offset,
         ptX, ptY, xsectCoords)) {
      m_grafx.drawEdgeFull(GraphGraphics.ARROW_NONE, 0.0f, null,
                           GraphGraphics.ARROW_DISC, offset * 2.0f, Color.blue,
                           ptX, ptY, xsectCoords[0], xsectCoords[1],
                           edgeThickness, Color.green, dashLength); }
    ptX = 200.0f;
    ptY = -80.0f;
    m_pts[0] = ptX; m_pts[1] = ptY;
    m_xform.transform(m_pts, 0, m_pts, 0, 1);
    ptX = m_pts[0]; ptY = m_pts[1];
    float deltaSize = offset * 2.0f;
    offset = 0.0f;
    if (m_grafx.computeEdgeIntersection
        (GraphGraphics.SHAPE_ELLIPSE, xMin, yMin, xMax, yMax, offset,
         ptX, ptY, xsectCoords)) {
      m_grafx.drawEdgeFull(GraphGraphics.ARROW_NONE, 0.0f, null,
                           GraphGraphics.ARROW_DELTA, deltaSize, Color.orange,
                           ptX, ptY, xsectCoords[0], xsectCoords[1],
                           edgeThickness, Color.green, dashLength); }
  }

  public boolean isResizable() { return false; }

}

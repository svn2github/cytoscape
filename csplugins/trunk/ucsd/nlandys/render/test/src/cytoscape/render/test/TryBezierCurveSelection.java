package cytoscape.render.test;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public final class TryBezierCurveSelection
  extends Frame implements MouseListener, MouseMotionListener
{

  public static final void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TryBezierCurveSelection();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 640;
  private final int m_imgHeight = 480;
  private final Image m_img;
  private final GeneralPath[] m_curves;
  private final GeneralPath[] m_xsectCurves;
  private int m_currMouseButton = 0; // 0: none; 1: left.
  private int m_initXMousePos;
  private int m_initYMousePos;
  private int m_lastXMousePos;
  private int m_lastYMousePos;

  public TryBezierCurveSelection()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_curves = new GeneralPath[3];
    m_xsectCurves = new GeneralPath[m_curves.length];
    for (int i = 0; i < m_curves.length; i++) {
      m_curves[i] = new GeneralPath();
      m_xsectCurves[i] = new GeneralPath(); }

    m_curves[0].moveTo(10.0f, 10.0f);
    m_curves[0].curveTo(300.0f, 10.0f, 310.0f, 20.0f, 310.0f, 320.0f);
    m_curves[0].curveTo(310.0f, 620.0f, 100.0f, 300.0f, 590.0f, 150.0f);

    m_curves[1].moveTo(10.0f, 20.0f);
    m_curves[1].lineTo(100.0f, 120.0f);
    m_curves[1].lineTo(110.0f, 220.0f);
    m_curves[1].lineTo(500.0f, 300.0f);

    m_curves[2].moveTo(10.0f, 30.0f);
    m_curves[2].lineTo(15.0f, 230.0f);
    m_curves[2].quadTo(20.0f, 430.0f, 100.0f, 300.0f);

    for (int i = 0; i < m_curves.length; i++) {
      foo(m_curves[i].getPathIterator(null), m_xsectCurves[i]); }
    updateImage();
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  private final void updateImage()
  {
    final Graphics2D g2d = (Graphics2D) m_img.getGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                         RenderingHints.VALUE_STROKE_PURE);
    g2d.setBackground(Color.white);
    g2d.clearRect(0, 0, m_imgWidth, m_imgHeight);
    for (int i = 0; i < m_curves.length; i++) {
      if (m_currMouseButton == 1 && m_xsectCurves[i].intersects
          (Math.min(m_initXMousePos, m_lastXMousePos),
           Math.min(m_initYMousePos, m_lastYMousePos),
           Math.abs(m_initXMousePos - m_lastXMousePos) + 1,
           Math.abs(m_initYMousePos - m_lastYMousePos) + 1)) {
        g2d.setColor(Color.red); }
      else {
        g2d.setColor(Color.black); }
      g2d.draw(m_curves[i]); }
    if (m_currMouseButton == 1) {
      g2d.setColor(new Color(0, 0, 255, 55));
      g2d.fillRect(Math.min(m_initXMousePos, m_lastXMousePos),
                   Math.min(m_initYMousePos, m_lastYMousePos),
                   Math.abs(m_initXMousePos - m_lastXMousePos) + 1,
                   Math.abs(m_initYMousePos - m_lastYMousePos) + 1); }
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
    updateImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  public boolean isResizable() { return false; }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      m_currMouseButton = 1;
      final Insets insets = insets();
      m_initXMousePos = e.getX() - insets.left;
      m_initYMousePos = e.getY() - insets.top;
      m_lastXMousePos = m_initXMousePos;
      m_lastYMousePos = m_initYMousePos;
      repaint(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (m_currMouseButton == 1) m_currMouseButton = 0; repaint(); }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 1) {
      final Insets insets = insets();
      m_lastXMousePos = e.getX() - insets.left;
      m_lastYMousePos = e.getY() - insets.top;
      repaint(); }
  }

  public void mouseMoved(MouseEvent e) {}

  private final static float[] s_floatTemp = new float[6];
  private final static float[] s_floatBuff = new float[100];
  private final static int[] s_segTypeBuff = new int[100];

  /*
   * This method sets returnPath to be the forwards and backwards traversal
   * of origPath (returnPath is set to be a closed loop with zero theoretical
   * area).  This method expects a single PathIterator.SEG_MOVETO from
   * origPath at the very beginning, and no further SEG_MOVETO's.  No
   * PathIterator.SEG_CLOSE is expected.  Only 32 bit floating point accuracy
   * is honored from origPath.
   */
  final static void foo(final PathIterator origPath,
                        final GeneralPath returnPath)
  {
    // First fill our buffers with the coordinates and segment types.
    int segs = 0;
    int offset = 0;
    if ((s_segTypeBuff[segs++] = origPath.currentSegment(s_floatTemp)) !=
        PathIterator.SEG_MOVETO) {
      throw new IllegalStateException
        ("expected a SEG_MOVETO at the beginning of origPath"); }
    for (int i = 0; i < 2; i++) s_floatBuff[offset++] = s_floatTemp[i];
    origPath.next();
    while (!origPath.isDone()) {
      final int segType = origPath.currentSegment(s_floatTemp);
      s_segTypeBuff[segs++] = segType;
      if (segType == PathIterator.SEG_MOVETO ||
          segType == PathIterator.SEG_CLOSE) {
        throw new IllegalStateException
          ("did not expect SEG_MOVETO or SEG_CLOSE"); }
      // This is a rare case where I rely on the actual constant values
      // to do a computation efficiently.
      final int coordCount = segType * 2;
      for (int i = 0; i < coordCount; i++) {
        s_floatBuff[offset++] = s_floatTemp[i]; }
      origPath.next(); }

    returnPath.reset();
    offset = 0;
    // Now add the forward path to returnPath.
    for (int i = 0; i < segs; i++) {
      switch (s_segTypeBuff[i]) {
      case PathIterator.SEG_MOVETO:
        returnPath.moveTo(s_floatBuff[offset++], s_floatBuff[offset++]);
        break;
      case PathIterator.SEG_LINETO:
        returnPath.lineTo(s_floatBuff[offset++], s_floatBuff[offset++]);
        break;
      case PathIterator.SEG_QUADTO:
        returnPath.quadTo(s_floatBuff[offset++], s_floatBuff[offset++],
                          s_floatBuff[offset++], s_floatBuff[offset++]);
        break;
      default: // PathIterator.SEG_CUBICTO.
        returnPath.curveTo(s_floatBuff[offset++], s_floatBuff[offset++],
                           s_floatBuff[offset++], s_floatBuff[offset++],
                           s_floatBuff[offset++], s_floatBuff[offset++]);
        break; } }
    // Now add the return path.
    for (int i = segs - 1; i > 0; i--) {
      switch (s_segTypeBuff[i]) {
      case PathIterator.SEG_LINETO:
        offset -= 2;
        returnPath.lineTo(s_floatBuff[offset - 2], s_floatBuff[offset - 1]);
        break;
      case PathIterator.SEG_QUADTO:
        offset -= 4;
        returnPath.quadTo(s_floatBuff[offset], s_floatBuff[offset + 1],
                          s_floatBuff[offset - 2], s_floatBuff[offset - 1]);
        break;
      default: // PathIterator.SEG_CUBICTO.
        offset -= 6;
        returnPath.curveTo(s_floatBuff[offset + 2], s_floatBuff[offset + 3],
                           s_floatBuff[offset], s_floatBuff[offset + 1],
                           s_floatBuff[offset - 2], s_floatBuff[offset - 1]);
        break; } }
    returnPath.closePath();
  }

}

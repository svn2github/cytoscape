package cytoscape.render.test;

import java.awt.Color;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

public final class TryThinPolygonFillingSpeed extends Frame
{

  private final static int FLAG_ANTIALIAS = 1;
  private final static int FLAG_TRANSFORM = 2;
  private final static int FLAG_DOUBLE_BUFFER = 4;

  public static final void main(String[] args) throws Exception
  {
    final int imgW = 600;
    final int imgH = 600;
    final float[] extents;
    final int flags;

    {
      int N = Integer.parseInt(args[0]);
      if (args.length > 1) flags = Integer.parseInt(args[1]);
      else flags = 0;
      extents = new float[N * 8];
      int inx = 0;
      Random r = new Random();
      double lineWidth = 1.0d;
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        double x0 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgW);
        nonnegative = 0x7fffffff & r.nextInt();
        double y0 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgH);
        nonnegative = 0x7fffffff & r.nextInt();
        double x1 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgW);
        nonnegative = 0x7fffffff & r.nextInt();
        double y1 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgH);
        double xOff = x0 - x1;
        double yOff = y0 - y1;
        double len = Math.sqrt(xOff * xOff + yOff * yOff);
        extents[inx * 8] = (float) (x0 + yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 1] = (float) (y0 - xOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 2] = (float) (x0 - yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 3] = (float) (y0 + xOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 4] = (float) (x1 - yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 5] = (float) (y1 + xOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 6] = (float) (x1 + yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 7] = (float) (y1 - xOff / (len * 2.0d) * lineWidth);
        inx++; }
    }

    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TryThinPolygonFillingSpeed(flags, imgW, imgH, extents);
          f.show();
          f.resize(imgW, imgH); } });

  }

  private final float[] m_extents;
  private final Image m_img;
  private final boolean m_antialias;
  private final GeneralPath m_poly2d;
  private final AffineTransform m_xform;
  private float m_offset = 0.0f;

  private TryThinPolygonFillingSpeed(int flags, int w, int h, float[] extents)
  {
    super();
    m_extents = extents;
    if ((flags & FLAG_DOUBLE_BUFFER) != 0) {
      addNotify();
      m_img = createImage(w, h); }
    else { m_img = null; }
    if ((flags & FLAG_ANTIALIAS) != 0) {
      m_antialias = true; }
    else { m_antialias = false; }
    if ((flags & FLAG_TRANSFORM) != 0) {
      m_xform = new AffineTransform();
      m_xform.setToScale(1.273d, 1.273d); }
    else { m_xform = null; }
    m_poly2d = new GeneralPath();
  }

  public void update(Graphics g) { paint(g); }

  public final void paint(Graphics g) {
    final Graphics2D g2;
    if (m_img != null) { g2 = (Graphics2D) m_img.getGraphics(); }
    else { g2 = (Graphics2D) g; }
    if (m_antialias) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON); }
    if (m_xform != null) { g2.transform(m_xform); }
    g2.setColor(Color.white);
    g2.fillRect(0, 0, 1000, 1000); // Whatever.
    g2.setColor(Color.black);
    for (int i = 0; i < m_extents.length; i += 8) {
      m_poly2d.reset();
      m_poly2d.moveTo(m_extents[i] + m_offset, m_extents[i + 1] + m_offset);
      m_poly2d.lineTo(m_extents[i + 2] + m_offset,
                      m_extents[i + 3] + m_offset);
      m_poly2d.lineTo(m_extents[i + 4] + m_offset,
                      m_extents[i + 5] + m_offset);
      m_poly2d.lineTo(m_extents[i + 6] + m_offset,
                      m_extents[i + 7] + m_offset);
      m_poly2d.closePath();
      g2.fill(m_poly2d); }
    if (m_img != null) {
      g.drawImage(m_img, 0, 0, null); }
    if (m_offset == 0.0f) { m_offset = 10.0f; }
    else { m_offset = 0.0f; }
    repaint(); }
  public boolean isResizable() { return false; }
  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
    return super.handleEvent(evt); }

}

package cytoscape.render.test;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class TryLineDrawingSpeed extends Frame
{

  private final static int FLAG_ANTIALIAS = 1;
  private final static int FLAG_TRANSFORM = 2;
  private final static int FLAG_DOUBLE_BUFFER = 4;
  private final static int FLAG_STROKE = 8;
  private final static int FLAG_LINE2D = 16;

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
      extents = new float[N * 4];
      int inx = 0;
      Random r = new Random();
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        int x0 = nonnegative % imgW;
        nonnegative = 0x7fffffff & r.nextInt();
        int y0 = nonnegative % imgH;
        nonnegative = 0x7fffffff & r.nextInt();
        int x1 = nonnegative % imgW;
        nonnegative = 0x7fffffff & r.nextInt();
        int y1 = nonnegative % imgH;
        extents[inx * 4] = (float) x0; // Add 0.5f later.
        extents[(inx * 4) + 1] = (float) y0;
        extents[(inx * 4) + 2] = (float) x1;
        extents[(inx * 4) + 3] = (float) y1;
        inx++; }
    }
    
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TryLineDrawingSpeed(flags, imgW, imgH, extents);
          f.show();
          f.resize(imgW, imgH); } });

  }

  private final boolean m_antialias;
  private final Image m_img;
  private final AffineTransform m_xform;
  private final Stroke m_stroke;
  private final float[] m_extents;
  private final Line2D.Float m_line2d;
  private float m_offset = 0.0f;

  private TryLineDrawingSpeed(int flags, int w, int h, float[] extents)
  {
    super();
    m_extents = extents;
    if ((flags & FLAG_DOUBLE_BUFFER) != 0) {
      addNotify();
      m_img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); }
    else { m_img = null; }
    if ((flags & FLAG_ANTIALIAS) != 0) {
      m_antialias = true; }
    else { m_antialias = false; }
    if ((flags & FLAG_TRANSFORM) != 0) {
      m_xform = new AffineTransform();
      m_xform.setToScale(1.273d, 1.273d); }
    else { m_xform = null; }
    if ((flags & FLAG_STROKE) != 0) {
      m_stroke = new BasicStroke(0.3f); }
    else { m_stroke = null; }
    if ((flags & FLAG_LINE2D) != 0) {
      m_line2d = new Line2D.Float(); }
    else { m_line2d = null; }
  }

  public void update(Graphics g) { paint(g); }

  public void paint(Graphics g) {
    final Graphics2D g2;
    if (m_img != null) { g2 = (Graphics2D) m_img.getGraphics(); }
    else { g2 = (Graphics2D) g; }
    Composite origComposite = g2.getComposite();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    g2.setColor(new Color(255, 255, 0, 0));
    // Stupid fucking Java2D antialiasing must be turned off for this to work.
    g2.fillRect(0, 0, 1000, 1000); // Whatever.
    g2.setComposite(origComposite);
    g2.setColor(Color.black);
    if (m_antialias) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON); }
    if (m_xform != null) { g2.transform(m_xform); }
    if (m_stroke != null) { g2.setStroke(m_stroke); }
    if (m_line2d == null) {
      for (int i = 0; i < m_extents.length;) {
        g2.drawLine((int) (m_extents[i++] + m_offset),
                    (int) (m_extents[i++] + m_offset),
                    (int) (m_extents[i++] + m_offset),
                    (int) (m_extents[i++] + m_offset)); } }
    else {
      for (int i = 0; i < m_extents.length;) {
        m_line2d.setLine(m_extents[i++] + m_offset,
                         m_extents[i++] + m_offset,
                         m_extents[i++] + m_offset,
                         m_extents[i++] + m_offset);
        g2.draw(m_line2d); } }
    if (m_img != null) {
      g.setColor(Color.blue);
      g.fillRect(0, 0, 1000, 1000);
      g.drawImage(m_img, 0, 0, null); }
    if (m_offset == 0.0f) { m_offset = 10.0f; }
    else { m_offset = 0.0f; }
    repaint(); }
  public boolean isResizable() { return false; }
  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
    return super.handleEvent(evt); }

}

package cytoscape.render.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Random;

public final class TryLineDrawingSpeed extends Frame
{

  private final static int FLAG_ANTIALIAS = 1;
  private final static int FLAG_TRANSFORM = 2;
  private final static int FLAG_DOUBLE_BUFFER = 4;
  private final static int FLAG_STROKE = 8;

  public static final void main(String[] args) throws Exception
  {
    final int imgW = 600;
    final int imgH = 600;
    final int[] extents;
    final int flags;

    {
      int N = Integer.parseInt(args[0]);
      if (args.length > 1) flags = Integer.parseInt(args[1]);
      else flags = 0;
      extents = new int[N * 4];
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
        extents[inx * 4] = x0;
        extents[(inx * 4) + 1] = y0;
        extents[(inx * 4) + 2] = x1;
        extents[(inx * 4) + 3] = y1;
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
  private final int[] m_extents;
  private int m_offset = 0;

  private TryLineDrawingSpeed(int flags, int w, int h, int[] extents)
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
      m_xform.setToScale(1.7639d, 1.7639d); }
    else { m_xform = null; }
    if ((flags & FLAG_STROKE) != 0) {
      m_stroke = new BasicStroke(0.23f); } // What about 1.0f?
    else { m_stroke = null; }
  }

  public void update(Graphics g) { paint(g); }

  public void paint(Graphics g) {
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
    if (m_stroke != null) { g2.setStroke(m_stroke); }
    for (int i = 0; i < m_extents.length;) {
      g2.drawLine(m_extents[i++] + m_offset, m_extents[i++] + m_offset,
                  m_extents[i++] + m_offset, m_extents[i++] + m_offset); }
    if (m_img != null) {
      g.drawImage(m_img, 0, 0, null); }
    if (m_offset == 0) { m_offset = 10; }
    else { m_offset = 0; }
    repaint(); }
  public boolean isResizable() { return false; }
  public boolean handleEvent(Event evt) {
    if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
    return super.handleEvent(evt); }

}

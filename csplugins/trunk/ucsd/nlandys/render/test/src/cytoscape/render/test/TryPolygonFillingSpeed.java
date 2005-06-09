package cytoscape.render.test;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class TryPolygonFillingSpeed extends Frame
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
      extents = new float[N * 4];
      int inx = 0;
      Random r = new Random();
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        int x0 = nonnegative % imgW;
        nonnegative = 0x7fffffff & r.nextInt();
        int y0 = nonnegative % imgH;
        nonnegative = 0x7fffffff & r.nextInt();
        int w = nonnegative % 5;
        nonnegative = 0x7fffffff & r.nextInt();
        int h = nonnegative % 5;
        extents[inx * 4] = (float) x0;
        extents[(inx * 4) + 1] = (float) y0;
        extents[(inx * 4) + 2] = (float) (x0 + w);
        extents[(inx * 4) + 3] = (float) (y0 + h);
        inx++; }
    }
    
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TryPolygonFillingSpeed(flags, imgW, imgH, extents);
          f.show();
          f.resize(imgW, imgH); } });

  }

  private final boolean m_antialias;
  private final Image m_img;
  private final AffineTransform m_xform;
  private final float[] m_extents;
  private final GeneralPath m_poly2d;
  private float m_offset = 0.0f;

  private TryPolygonFillingSpeed(int flags, int w, int h, float[] extents)
  {
    super();
    m_extents = extents;
    if ((flags & FLAG_DOUBLE_BUFFER) != 0) {
      m_img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); }
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

  public void paint(Graphics g) {
    final Graphics2D g2;
    if (m_img != null) { g2 = (Graphics2D) m_img.getGraphics(); }
    else { g2 = (Graphics2D) g; }
    Composite origComposite = g2.getComposite();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    g2.setColor(new Color(255, 255, 0, 0)); // Transparent yellow.
    // Stupid fucking Java2D antialiasing must be turned off for this to work.
    g2.fillRect(0, 0, 1000, 1000); // Whatever.
    g2.setComposite(origComposite);
    if (m_antialias) {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON); }
    if (m_xform != null) { g2.transform(m_xform); }
    for (int i = 0; i < m_extents.length; i += 4) {
      if (i % 8 == 0) g2.setColor(Color.black);
      else g2.setColor(Color.red);
      m_poly2d.reset();
      m_poly2d.moveTo(m_extents[i] + m_offset, m_extents[i + 1] + m_offset);
      m_poly2d.lineTo(m_extents[i + 2] + m_offset,
                      m_extents[i + 3] + m_offset);
      m_poly2d.lineTo(m_extents[i] + m_offset, m_extents[i + 3] + m_offset);
      m_poly2d.closePath();
      g2.fill(m_poly2d); }
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

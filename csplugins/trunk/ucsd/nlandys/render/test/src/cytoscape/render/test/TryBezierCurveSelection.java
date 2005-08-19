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
  private final GeneralPath m_curve1;
//   private final GeneralPath m_curve2;
//   private final GeneralPath m_curve3;
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
    m_curve1 = new GeneralPath();
    m_curve1.moveTo(10.0f, 10.0f);
    m_curve1.curveTo(300.0f, 10.0f, 310.0f, 20.0f, 310.0f, 320.0f);
    m_curve1.curveTo(310.0f, 620.0f, 100.0f, 300.0f, 590.0f, 150.0f);
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
    g2d.setColor(Color.black);
    g2d.draw(m_curve1);
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
      m_lastYMousePos = m_initYMousePos; }
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

}

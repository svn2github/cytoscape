package cytoscape.render.test;

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

public final class TestThoroughBidirectional
  extends Frame implements MouseListener, MouseMotionListener
{

  public final static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestThoroughBidirectional();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 600;
  private final int m_imgHeight = 480;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private double m_currXCenter = 0.0d;
  private double m_currYCenter = 0.0d;
  private double m_currScale = 1.0d;
  private int m_currMouseButton = 0; // 0: none; 1: left; 2: middle.
  private int m_lastXMousePos = 0;
  private int m_lastYMousePos = 0;

  public TestThoroughBidirectional()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, true);
    updateImage();
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
    updateImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private final void updateImage()
  {
    m_grafx.clear(Color.white, m_currXCenter, m_currYCenter, m_currScale);
    final double edgeThickness = 1.0d;
    final double arrow0Size = 1.0d;
    final double arrow1Size = 4.738d;
    m_grafx.drawEdgeFull
      (GraphGraphics.ARROW_DELTA,
       (float) (arrow0Size * 2.0d +
                edgeThickness * (Math.sqrt(17.0d) + 5.0d) / 4.0d),
       new Color(255, 0, 0, 63),
       GraphGraphics.ARROW_DELTA,
       (float) (arrow1Size * 2.0d +
                edgeThickness * (Math.sqrt(17.0d) + 5.0d) / 4.0d),
       new Color(255, 0, 0, 63),
       -100.0f, -100.0f, null, 30.0f, 40.0f,
       0.0f, Color.black, 0.0f);
    m_grafx.drawEdgeFull
      (GraphGraphics.ARROW_BIDIRECTIONAL, (float) arrow0Size, null,
       GraphGraphics.ARROW_BIDIRECTIONAL, (float) arrow1Size, null,
       -100.0f, -100.0f, null, 30.0f, 40.0f,
       (float) edgeThickness, new Color(0, 0, 0, 63), 0.0f);
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
      m_currYCenter += deltaY / m_currScale;
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

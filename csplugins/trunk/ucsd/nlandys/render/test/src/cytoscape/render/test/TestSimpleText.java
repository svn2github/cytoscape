package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.rtree.RTreeEntryEnumerator;
import cytoscape.render.immed.GraphGraphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class TestSimpleText
  extends Frame implements MouseListener, MouseMotionListener
{

  public final static void main(final String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestSimpleText(args.length > 0);
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

  public TestSimpleText(boolean textAsString)
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics
      (m_img, Color.white,
       textAsString ? GraphGraphics.FLAG_TEXT_AS_STRING : 0, true);
    updateNodeImage();
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
    updateNodeImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private final void updateNodeImage()
  {
    m_grafx.clear(m_currXCenter, m_currYCenter, m_currScale);
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                         -90.0f, -15.0f, -40.0f, 15.0f,
                         Color.yellow, 2.0f, Color.black);
    final Font font = new Font("Dialog", Font.PLAIN, 14);
    m_grafx.drawText(font, 1.0d, "TEKST", -65.0f, 0.0f, Color.black);
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                         -15.0f, -15.0f, 35.0f, 15.0f,
                         Color.magenta, 2.0f, Color.black);
    m_grafx.drawText(font, 1.0d, "ypqg", 10.0f, 0.0f, Color.black);
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_TRIANGLE,
                         0.0f, 90.0f, 120.0f, 170.0f,
                         Color.cyan, 2.0f, Color.black);
    m_grafx.drawText(font, 1.0d, "gooF", 60.0f, 130.0f, Color.black);
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
      m_currYCenter += deltaY / m_currScale; // y orientations are opposite.
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

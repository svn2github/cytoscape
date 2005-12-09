package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.render.immed.GraphGraphics;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SampleNetwork1
  extends Frame implements MouseListener, MouseMotionListener
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new SampleNetwork1();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }


  private final int m_imgWidth = 800;
  private final int m_imgHeight = 600;
  private final DynamicGraph m_graph;
  private final RTree m_rtree;
  private final Image m_img;
  private final GraphGraphics m_grafx;

  public SampleNetwork1()
  {
    super();
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
    m_rtree = new RTree();
    assembleGraph();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, true);
  }

  private void assembleGraph()
  {
    final int bnet = m_graph.nodeCreate();
    final int slip = m_graph.nodeCreate();
    final int bsdi = m_graph.nodeCreate();
    final int sun = m_graph.nodeCreate();
    final int svr4 = m_graph.nodeCreate();
    final int bmodem = m_graph.nodeCreate();
    final int tmodem = m_graph.nodeCreate();
    final int netb = m_graph.nodeCreate();
    final int tnet = m_graph.nodeCreate();
    final int aix = m_graph.nodeCreate();
    final int solaris = m_graph.nodeCreate();
    final int gemini = m_graph.nodeCreate();
    final int gateway = m_graph.nodeCreate();
    final int internet = m_graph.nodeCreate();
    m_rtree.insert(bnet, 33, 10, 100, 11);
    m_rtree.insert(slip, 5, 15, 17, 25);
    m_rtree.insert(bsdi, 37, 15, 49, 25);
    m_rtree.insert(sun, 61, 15, 73, 25);
    m_rtree.insert(svr4, 85, 15, 97, 25);
    m_rtree.insert(bmodem, 63, 33, 71, 39);
    m_rtree.insert(tmodem, 63, 50, 71, 56);
    m_graph.edgeCreate(
  }

  public void paint(Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  public void update(Graphics g)
  {
    final Insets insets = insets();
    updateImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private void updateImage()
  {
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
//     if (e.getButton() == MouseEvent.BUTTON3) {
//       m_currMouseButton = 3;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       m_currMouseButton = 2;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
//     if (e.getButton() == MouseEvent.BUTTON3) {
//       if (m_currMouseButton == 3) m_currMouseButton = 0; }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
//     if (m_currMouseButton == 3) {
//       double deltaX = e.getX() - m_lastXMousePos;
//       double deltaY = e.getY() - m_lastYMousePos;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY();
//       m_currXCenter -= deltaX / m_currScale;
//       m_currYCenter += deltaY / m_currScale; // y orientations are opposite.
//       repaint(); }
//     else if (m_currMouseButton == 2) {
//       double deltaY = e.getY() - m_lastYMousePos;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY();
//       m_currScale *= Math.pow(2, -deltaY / 300.0d);
//       repaint(); }
  }

  public void mouseMoved(MouseEvent e) {}

  public boolean isResizable() { return false; }

}

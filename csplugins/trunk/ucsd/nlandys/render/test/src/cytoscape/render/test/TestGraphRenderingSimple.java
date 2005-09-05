package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.EdgeDetails;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.render.stateful.NodeDetails;
import cytoscape.util.intr.IntHash;
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

public class TestGraphRenderingSimple
  extends Frame implements MouseListener, MouseMotionListener
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestGraphRenderingSimple();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 800;
  private final int m_imgHeight = 600;
  private final DynamicGraph m_graph;
  private final RTree m_rtree;
  private final GraphLOD m_lod;
  private final NodeDetails m_nodeDetails;
  private final EdgeDetails m_edgeDetails;
  private final IntHash m_hash;
  private final Image m_img;
  private final GraphGraphics m_grafx;

  public TestGraphRenderingSimple()
  {
    super();
    m_graph = DynamicGraphFactory.instantiateDynamicGraph();
    m_rtree = new RTree();
    assembleGraph();
    m_lod = new GraphLOD();
    m_nodeDetails = new NodeDetails() {
        private final Color m_fillColor = new Color(255, 0, 0, 127);
        public Color fillColor(int node) { return m_fillColor; } };
    m_edgeDetails = new EdgeDetails() {
        private final Color m_color = new Color(0, 0, 255, 127);
        public float thickness(int edge) { return 1.0f; }
        public Color color(int edge) { return m_color; } };
    m_hash = new IntHash();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, true);
    updateImage();
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  // This is called once by the constructor.  This simply populates
  // m_graph and m_rtree.
  private void assembleGraph()
  {
    final int node1 = m_graph.nodeCreate();
    final int node2 = m_graph.nodeCreate();
    final int node3 = m_graph.nodeCreate();
    final int node4 = m_graph.nodeCreate();
    final int node5 = m_graph.nodeCreate();
    m_rtree.insert(node1, -12, 250, 12, 270);
    m_rtree.insert(node2, 100, -200, 120, -176);
    m_rtree.insert(node3, -230, 100, -214, 130);
    m_rtree.insert(node4, 180, 110, 198, 132);
    m_rtree.insert(node5, -140, -220, -125, -200);
    m_graph.edgeCreate(node1, node2, true);
    m_graph.edgeCreate(node2, node3, true);
    m_graph.edgeCreate(node3, node4, true);
    m_graph.edgeCreate(node4, node5, true);
    m_graph.edgeCreate(node5, node1, true);
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
    GraphRenderer.renderGraph(m_graph, m_rtree, m_lod, m_nodeDetails,
                              m_edgeDetails, m_hash, m_grafx, Color.white,
                              0, 0, 1);
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
//     if (e.getButton() == MouseEvent.BUTTON1) {
//       m_currMouseButton = 1;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       m_currMouseButton = 2;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
//     if (e.getButton() == MouseEvent.BUTTON1) {
//       if (m_currMouseButton == 1) m_currMouseButton = 0; }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
//     if (m_currMouseButton == 1) {
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

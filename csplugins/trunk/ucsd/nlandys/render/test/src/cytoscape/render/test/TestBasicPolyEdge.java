package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.rtree.RTreeEntryEnumerator;
// import cytoscape.render.immed.EdgeAnchors;
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

public final class TestBasicPolyEdge
  extends Frame implements MouseListener, MouseMotionListener
{

  public final static void main(String[] args) throws Exception
  {
    final RTree tree = new RTree();
    tree.insert(0, -200.0f, -200.0f, -190.0f, -190.0f); // Begin point.
    tree.insert(1, -200.0f, 100.0f, -190.0f, 110.0f); // Anchor 1.
    tree.insert(2, 0.0f, 0.0f, 10.0f, 10.0f); // Anchor 2.
    tree.insert(3, 200.0f, 200.0f, 210.0f, 210.0f); // Anchor 3.
    tree.insert(4, 200.0f, -200.0f, 210.0f, -190.0f); // End point.
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestBasicPolyEdge(tree);
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 640;
  private final int m_imgHeight = 480;
  private final double[] m_ptBuff = new double[2];
  private final float[] m_floatBuff = new float[4];
  private final RTree m_tree;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private final boolean[] m_ptStates;
  private double m_currXCenter = 0.0d;
  private double m_currYCenter = 0.0d;
  private double m_currScale = 1.0d;
  private int m_currMouseButton = 0; // 0: none; 1: left; 2: middle; 3: right.
  private int m_lastXMousePos;
  private int m_lastYMousePos;

  /**
   * The objKeys in tree must start at zero and must be contiguous.
   */
  public TestBasicPolyEdge(RTree tree)
  {
    super();
    m_tree = tree;
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, true);
    updateImage();
    m_ptStates = new boolean[m_tree.size()];
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void paint(Graphics g)
  {
    Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  public void update(Graphics g)
  {
    Insets insets = insets();
    updateImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  public final void updateImage()
  {
    m_grafx.clear(Color.white, m_currXCenter, m_currYCenter, m_currScale);
    RTreeEntryEnumerator iter = m_tree.queryOverlap
      ((float) (m_currXCenter - ((double) (m_imgWidth / 2)) / m_currScale),
       (float) (m_currYCenter - ((double) (m_imgHeight / 2)) / m_currScale),
       (float) (m_currXCenter + ((double) (m_imgWidth / 2)) / m_currScale),
       (float) (m_currYCenter + ((double) (m_imgHeight / 2)) / m_currScale),
       null, 0);
    while (iter.numRemaining() > 0) {
      int inx = iter.nextExtents(m_floatBuff, 0);
      m_grafx.drawNodeFull(GraphGraphics.SHAPE_ELLIPSE,
                           m_floatBuff[0], m_floatBuff[1],
                           m_floatBuff[2], m_floatBuff[3],
                           m_ptStates[inx] ? Color.blue : Color.red,
                           0.7f, Color.black); }
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      m_currMouseButton = 1;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      m_ptBuff[0] = m_lastXMousePos;
      m_ptBuff[1] = m_lastYMousePos;
      m_grafx.xformImageToNodeCoords(m_ptBuff);
      RTreeEntryEnumerator candidates =
        m_tree.queryOverlap((float) m_ptBuff[0], (float) m_ptBuff[1],
                            (float) m_ptBuff[0], (float) m_ptBuff[1],
                            null, 0);
      boolean mustRender = false;
      while (candidates.numRemaining() > 0) {
        int candidate = candidates.nextExtents(m_floatBuff, 0);
        if (m_grafx.contains(GraphGraphics.SHAPE_ELLIPSE,
                             m_floatBuff[0], m_floatBuff[1],
                             m_floatBuff[2], m_floatBuff[3],
                             (float) m_ptBuff[0], (float) m_ptBuff[1])) {
          mustRender = true;
          m_ptStates[candidate] = true;
          break; } }
      if (mustRender) { repaint(); } }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       m_currMouseButton = 2;
//       m_lastXMousePos = e.getX();
//       m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1 && m_currMouseButton == 1) {
      boolean mustRender = false;
      for (int i = 0; i < m_ptStates.length; i++) {
        if (m_ptStates[i]) { // In our world at most one node is selected.
          m_ptStates[i] = false; mustRender = true; break; } }
      m_currMouseButton = 0;
      if (mustRender) { repaint(); } }
//     else if (e.getButton() == MouseEvent.BUTTON2) {
//       if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 1) {
      m_ptBuff[0] = m_lastXMousePos;
      m_ptBuff[1] = m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      int selected = -1;
      for (int i = 0; i < m_ptStates.length; i++) {
        if (m_ptStates[i]) { selected = i; break; } }
      if (selected >= 0) {
        m_grafx.xformImageToNodeCoords(m_ptBuff); // The previous point.
        double prevXCoord = m_ptBuff[0];
        double prevYCoord = m_ptBuff[1];
        m_ptBuff[0] = m_lastXMousePos;
        m_ptBuff[1] = m_lastYMousePos;
        m_grafx.xformImageToNodeCoords(m_ptBuff); // The current point.
        double deltaX = m_ptBuff[0] - prevXCoord;
        double deltaY = m_ptBuff[1] - prevYCoord;
        m_tree.exists(selected, m_floatBuff, 0);
        m_tree.delete(selected);
        m_tree.insert(selected,
                      (float) (deltaX + m_floatBuff[0]),
                      (float) (deltaY + m_floatBuff[1]),
                      (float) (deltaX + m_floatBuff[2]),
                      (float) (deltaY + m_floatBuff[3]));
        repaint(); } }
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

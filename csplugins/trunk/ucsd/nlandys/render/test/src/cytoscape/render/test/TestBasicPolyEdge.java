package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;

import org.freehep.graphicsio.pdf.PDFGraphics2D;

public final class TestBasicPolyEdge
  extends Frame implements KeyListener, MouseListener, MouseMotionListener
{

  public final static void main(String[] args) throws Exception
  {
    final RTree tree = new RTree(3);
    tree.insert(0, -200.0f, -200.0f, -180.0f, -180.0f);
    tree.insert(1, -200.0f, 100.0f, -180.0f, 120.0f);
    tree.insert(2, 0.0f, 0.0f, 20.0f, 20.0f);
    tree.insert(3, 200.0f, 200.0f, 220.0f, 220.0f);
    tree.insert(4, 50.0f, -50.0f, 70.0f, -30.0f);
//     tree.insert(5, 300.0f, -200.0f, 320.0f, -180.0f);
//     tree.insert(6, 0.0f, 200.0f, 20.0f, 220.0f);
//     tree.insert(7, 200.0f, -200.0f, 220.0f, -180.0f);
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestBasicPolyEdge(tree);
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 800;
  private final int m_imgHeight = 600;
  private final Color m_bgColor = Color.white;
  private final Color m_fillColor = new Color(255, 0, 0, 63);
  private final Color m_selectedFillColor = new Color(0, 0, 255, 63);
  private final Color m_borderColor = new Color(0, 0, 0, 63);
  private final Color m_textColor = Color.white;
  private final Color m_edgeSegmentColor = new Color(127, 127, 127, 127);
  private final Color m_selectedEdgeSegmentColor = new Color(127, 0, 0, 127);
  private final Color m_edgeArrowColor = new Color(0, 127, 0, 127);
  private final Color m_edgeSelectionWindowColor = new Color(0, 0, 255, 63);
  private final double[] m_ptBuff = new double[2];
  private final float[] m_floatBuff = new float[4];
  private final GeneralPath m_path2d = new GeneralPath();
  private final RTree m_tree;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private final Font m_font;
  private final boolean[] m_ptStates;
  private final String[] m_labels;
  private final float[] m_anchorsBuff;
  private double m_currXCenter = 0.0d;
  private double m_currYCenter = 0.0d;
  private double m_currScale = 1.0d;
  private int m_currMouseButton = 0; // 0: none; 1: left; 2: middle; 3: right.
  private int m_lastXMousePos;
  private int m_lastYMousePos;
  private int m_initXMousePos; // For edge selection window.
  private int m_initYMousePos; // For edge selection window.

  /**
   * The objKeys in tree must start at zero and must be contiguous.
   * The tree must contain at least 2 elements.
   */
  public TestBasicPolyEdge(RTree tree)
  {
    super();
    m_tree = tree;
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, true);
    m_font = new Font(null, Font.BOLD, 1);
    m_ptStates = new boolean[m_tree.size()];
    m_labels = new String[m_tree.size()];
    for (int i = 0; i < m_labels.length; i++) { m_labels[i] = "" + i; }
    m_anchorsBuff = new float[(m_tree.size() - 2) * 2];
    updateImage(m_grafx);
    addKeyListener(this);
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
    updateImage(m_grafx);
    if (m_currMouseButton == 1) {
      boolean anchorSelected = false;
      for (int i = 0; i < m_ptStates.length; i++) {
        if (m_ptStates[i]) { anchorSelected = true; break; } }
      if (!anchorSelected) { // Draw the edge selection window.
        Graphics imgGrafx = m_img.getGraphics();
        imgGrafx.setColor(m_edgeSelectionWindowColor);
        imgGrafx.fillRect(Math.min(m_initXMousePos, m_lastXMousePos),
                          Math.min(m_initYMousePos, m_lastYMousePos),
                          Math.abs(m_initXMousePos - m_lastXMousePos) + 1,
                          Math.abs(m_initYMousePos - m_lastYMousePos) + 1); } }
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private static final class MyAnchors implements EdgeAnchors
  {
    private final float[] anchorsBuff;
    private final int num;
    private MyAnchors(float[] anchorsBuff, int size) {
      this.num = size;
      this.anchorsBuff = anchorsBuff; }
    public int numAnchors() { return num; }
    public void getAnchor(int inx, float[] anchorArr, int offset) {
      anchorArr[offset] = anchorsBuff[inx * 2];
      anchorArr[offset + 1] = anchorsBuff[inx * 2 + 1]; }
  }

  private void updateImage(GraphGraphics grafx)
  {
    grafx.clear(m_bgColor, m_currXCenter, m_currYCenter, m_currScale);

    // Determine endpoints.
    m_tree.exists(0, m_floatBuff, 0);
    float x0 = (float)
      ((((double) m_floatBuff[0]) + m_floatBuff[2]) / 2.0d);
    float y0 = (float)
      ((((double) m_floatBuff[1]) + m_floatBuff[3]) / 2.0d);
    m_tree.exists(m_tree.size() - 1, m_floatBuff, 0);
    float x1 = (float)
      ((((double) m_floatBuff[0]) + m_floatBuff[2]) / 2.0d);
    float y1 = (float)
      ((((double) m_floatBuff[1]) + m_floatBuff[3]) / 2.0d);

    // Populate m_anchorsBuff.
    int offset = 0;
    for (int i = m_tree.size() - 2; i > 0; i--) {
      m_tree.exists(i, m_floatBuff, 0);
      m_anchorsBuff[offset++] = (float)
        ((((double) m_floatBuff[0]) + m_floatBuff[2]) / 2.0d);
      m_anchorsBuff[offset++] = (float)
        ((((double) m_floatBuff[1]) + m_floatBuff[3]) / 2.0d); }

    final byte arrow0 = GraphGraphics.ARROW_DIAMOND;
    final float arrow0Size = 15.0f;
    final byte arrow1 = GraphGraphics.ARROW_DELTA;
    final float arrow1Size = 15.0f;

    boolean edgeSelected = false;
    if (m_currMouseButton == 1) {
      boolean anchorSelected = false;
      for (int i = 0; i < m_ptStates.length; i++) {
        if (m_ptStates[i]) { anchorSelected = true; break; } }
      if ((!anchorSelected) &&
          grafx.getEdgePath
          (arrow0, arrow0Size, arrow1, arrow1Size,
           x0, y0, new MyAnchors(m_anchorsBuff, m_tree.size() - 2),
           x1, y1, m_path2d)) {
        TryBezierCurveSelection.foo(m_path2d.getPathIterator(null), m_path2d);
        m_ptBuff[0] = m_initXMousePos;
        m_ptBuff[1] = m_initYMousePos;
        grafx.xformImageToNodeCoords(m_ptBuff);
        double initX = m_ptBuff[0];
        double initY = m_ptBuff[1];
        m_ptBuff[0] = m_lastXMousePos;
        m_ptBuff[1] = m_lastYMousePos;
        grafx.xformImageToNodeCoords(m_ptBuff);
        double lastX = m_ptBuff[0];
        double lastY = m_ptBuff[1];
        if (m_path2d.intersects
            (Math.min(initX, lastX), Math.min(initY, lastY),
             Math.abs(initX - lastX) + 1.0d, Math.abs(initY - lastY) + 1.0d)) {
          edgeSelected = true; } } }

    grafx.drawEdgeFull
      (arrow0, arrow0Size, m_edgeArrowColor,
       arrow1, arrow1Size, m_edgeArrowColor,
       x0, y0, new MyAnchors(m_anchorsBuff, m_tree.size() - 2),
       x1, y1, 4.0f,
       edgeSelected ? m_selectedEdgeSegmentColor : m_edgeSegmentColor,
       0.0f);

    SpacialEntry2DEnumerator iter = m_tree.queryOverlap
      ((float) (m_currXCenter - ((double) (m_imgWidth / 2)) / m_currScale),
       (float) (m_currYCenter - ((double) (m_imgHeight / 2)) / m_currScale),
       (float) (m_currXCenter + ((double) (m_imgWidth / 2)) / m_currScale),
       (float) (m_currYCenter + ((double) (m_imgHeight / 2)) / m_currScale),
       null, 0, false);
    while (iter.numRemaining() > 0) {
      int inx = iter.nextExtents(m_floatBuff, 0);
      grafx.drawNodeFull
        (GraphGraphics.SHAPE_ELLIPSE,
         m_floatBuff[0], m_floatBuff[1], m_floatBuff[2], m_floatBuff[3],
         m_ptStates[inx] ? m_selectedFillColor : m_fillColor,
         1.6f, m_borderColor);
      grafx.drawTextFull
        (m_font, 14, m_labels[inx],
         (float) ((((double) m_floatBuff[0]) + m_floatBuff[2]) / 2.0d),
         (float) ((((double) m_floatBuff[1]) + m_floatBuff[3]) / 2.0d),
         m_textColor, true); }
  }

  public void keyTyped(KeyEvent e) { }

  public void keyPressed(KeyEvent e)
  {
    if (e.getKeyCode() == KeyEvent.VK_P) {
      PDFGraphics2D pdfGrafx =
        new PDFGraphics2D(System.out, new Dimension(m_imgWidth, m_imgHeight));
      Image img = new ImageImposter(pdfGrafx, m_imgWidth, m_imgHeight);
      GraphGraphics gg = new GraphGraphics(img, true);
      pdfGrafx.startExport();
      updateImage(gg);
      pdfGrafx.endExport(); }
  }

  public void keyReleased(KeyEvent e) {}

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      m_currMouseButton = 1;
      Insets insets = insets();
      m_lastXMousePos = e.getX() - insets.left;
      m_lastYMousePos = e.getY() - insets.top;
      m_ptBuff[0] = m_lastXMousePos;
      m_ptBuff[1] = m_lastYMousePos;
      m_grafx.xformImageToNodeCoords(m_ptBuff);
      // We have to "reverse" the order in which hits are returned; in
      // rendering, we do it back to front; in selection, we would like to
      // do it front to back.
      SpacialEntry2DEnumerator candidates =
        m_tree.queryOverlap((float) m_ptBuff[0], (float) m_ptBuff[1],
                            (float) m_ptBuff[0], (float) m_ptBuff[1],
                            null, 0, true);
      boolean anchorSelected = false;
      while (candidates.numRemaining() > 0) {
        int objKey = candidates.nextExtents(m_floatBuff, 0);
        if (m_grafx.nodeContains(GraphGraphics.SHAPE_ELLIPSE,
                                 m_floatBuff[0], m_floatBuff[1],
                                 m_floatBuff[2], m_floatBuff[3],
                                 (float) m_ptBuff[0], (float) m_ptBuff[1])) {
          anchorSelected = true;
          m_ptStates[objKey] = true;
          // Re-insert the entry into the R-tree so that just clicking on
          // the node has the same effect as dragging it a little bit.
          m_tree.delete(objKey);
          m_tree.insert(objKey, m_floatBuff[0], m_floatBuff[1],
                        m_floatBuff[2], m_floatBuff[3]);
          break; } }
      if (!anchorSelected) { // Start drawing the edge selection window.
        m_initXMousePos = m_lastXMousePos;
        m_initYMousePos = m_lastYMousePos; }
      repaint(); }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      m_currMouseButton = 2;
      m_lastXMousePos = e.getX(); // Ignore offset caused by insets.
      m_lastYMousePos = e.getY(); }
    else if (e.getButton() == MouseEvent.BUTTON3) {
      m_currMouseButton = 3;
      m_lastXMousePos = e.getX(); // Ignore offset caused by insets.
      m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1 && m_currMouseButton == 1) {
      for (int i = 0; i < m_ptStates.length; i++) {
        if (m_ptStates[i]) { // In our world at most one node is selected.
          m_ptStates[i] = false; break; } }
      m_currMouseButton = 0;
      repaint(); }
    else if (e.getButton() == MouseEvent.BUTTON2 &&
             m_currMouseButton == 2) {
      m_currMouseButton = 0; }
    else if (e.getButton() == MouseEvent.BUTTON3 &&
             m_currMouseButton == 3) {
      m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 1) {
      m_ptBuff[0] = m_lastXMousePos;
      m_ptBuff[1] = m_lastYMousePos;
      Insets insets = insets();
      m_lastXMousePos = e.getX() - insets.left;
      m_lastYMousePos = e.getY() - insets.top;
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
                      (float) (deltaY + m_floatBuff[3])); }
      else { } // We're dragging the edge selection window.
      repaint(); }
    else if (m_currMouseButton == 2) {
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX(); // Ignore offset caused by insets.
      m_lastYMousePos = e.getY();
      m_currScale *= Math.pow(2, -deltaY / 300.0d);
      repaint(); }
    else if (m_currMouseButton == 3) {
      double deltaX = e.getX() - m_lastXMousePos; // Ignore inset offsets.
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      m_currXCenter -= deltaX / m_currScale;
      m_currYCenter += deltaY / m_currScale; // Y orientations are opposite.
      repaint(); }
  }

  public void mouseMoved(MouseEvent e) {}

  public boolean isResizable() { return false; }

}

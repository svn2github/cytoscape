package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.geom.spacial.SpacialIndex2D;
import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.EdgeDetails;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.render.stateful.NodeDetails;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntObjHash;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class TestGraphRendering
  extends Frame implements MouseListener, MouseMotionListener
{

  public static void main(String[] args) throws Exception
  {
    final DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
    final RTree rtree = new RTree();
    final int N = Integer.parseInt(args[0]);
    final double maxDim = 10;
    final double minDim = 5.01;
    final double areaDim = Math.sqrt((double) N) * maxDim * 2;
    final Random r = new Random();
    for (int i = 0; i < N; i++) {
      final double centerX =
        ((double) r.nextInt()) / Integer.MAX_VALUE * (areaDim / 2.0d);
      final double centerY =
        ((double) r.nextInt()) / Integer.MAX_VALUE * (areaDim / 2.0d);
      final double width =
        ((double) (0x7fffffff & r.nextInt())) / Integer.MAX_VALUE *
        (maxDim - minDim) + minDim;
      final double height =
        ((double) (0x7fffffff & r.nextInt())) / Integer.MAX_VALUE *
        (maxDim - minDim) + minDim;
      final float xMin = (float) (centerX - (width / 2));
      final float yMin = (float) (centerY - (height / 2));
      final float xMax = (float) (centerX + (width / 2));
      final float yMax = (float) (centerY + (height / 2));
      rtree.insert(graph.nodeCreate(), xMin, yMin, xMax, yMax); }
    final float[] floatBuff = new float[4];
    final IntObjHash anchorsHash = new IntObjHash();
    for (int i = 0; i < N * 2; i++) {
      final int node = (r.nextInt() & 0x7fffffff) % N;
      rtree.exists(node, floatBuff, 0);
      final double xCenter = (((double) floatBuff[0]) + floatBuff[2]) / 2;
      final double yCenter = (((double) floatBuff[1]) + floatBuff[3]) / 2;
      final double radius = maxDim * 4;
      final SpacialEntry2DEnumerator spacialNeighbors =
        rtree.queryOverlap((float) (xCenter - radius),
                           (float) (yCenter - radius),
                           (float) (xCenter + radius),
                           (float) (yCenter + radius),
                           null, 0, false);
      final int numNeighbors = spacialNeighbors.numRemaining();
      if (numNeighbors > 0) {
        final int chosenEntry =
          (r.nextInt() & 0x7fffffff) % spacialNeighbors.numRemaining();
        for (int k = 0; k < chosenEntry; k++) {
          spacialNeighbors.nextInt(); }
        final int chosenObj = spacialNeighbors.nextInt();
        int numExistingSuchEdges = 0;
        final IntIterator iter =
          graph.edgesConnecting(node, chosenObj, true, true, true);
        while (iter.hasNext()) {
          iter.nextInt();
          numExistingSuchEdges++; }
        final int edge = graph.edgeCreate(node, chosenObj, true);
        if (numExistingSuchEdges > 0) {
          final double factor = (numExistingSuchEdges + 1) / 2 *
            (numExistingSuchEdges % 2 == 0 ? 1 : -1) * 0.1d;
          if (node != chosenObj) {
            final int node0 = Math.min(node, chosenObj);
            final int node1 = Math.max(node, chosenObj);
            rtree.exists(node0, floatBuff, 0);
            final double node0XCenter =
              (((double) floatBuff[0]) + floatBuff[2]) / 2;
            final double node0YCenter =
              (((double) floatBuff[1]) + floatBuff[3]) / 2;
            rtree.exists(node1, floatBuff, 0);
            final double node1XCenter =
              (((double) floatBuff[0]) + floatBuff[2]) / 2;
            final double node1YCenter =
              (((double) floatBuff[1]) + floatBuff[3]) / 2;
            final double dx = node1XCenter - node0XCenter;
            final double dy = node1YCenter - node0YCenter;
            final double midX = (node0XCenter + node1XCenter) / 2;
            final double midY = (node0YCenter + node1YCenter) / 2;
            final double anchorX = midX + factor * dy;
            final double anchorY = midY - factor * dx;
            anchorsHash.put
              (edge,
               new EdgeAnchors() {
                 public int numAnchors() { return 1; }
                 public void getAnchor(int inx, float[] arr, int off) {
                   arr[off] = (float) anchorX; 
                   arr[off + 1] = (float) anchorY; } }); }
          else { // node == chosenObj, i.e. self-edge.
            final float[] twoAnchors = new float[4];
            twoAnchors[0] = (float) (xCenter + factor * 10 * maxDim);
            twoAnchors[1] = (float) yCenter;
            twoAnchors[2] = (float) xCenter;
            twoAnchors[3] = (float) (yCenter + factor * 10 * maxDim);
            anchorsHash.put
              (edge,
               new EdgeAnchors() {
                 public int numAnchors() { return twoAnchors.length / 2; }
                 public void getAnchor(int inx, float[] arr, int off) {
                   arr[off] = twoAnchors[inx * 2];
                   arr[off + 1] = twoAnchors[inx * 2 + 1]; } }); } } } }

    final byte[] shapes = new byte[9];
    shapes[0] = GraphGraphics.SHAPE_RECTANGLE;
    shapes[1] = GraphGraphics.SHAPE_DIAMOND;
    shapes[2] = GraphGraphics.SHAPE_ELLIPSE;
    shapes[3] = GraphGraphics.SHAPE_HEXAGON;
    shapes[4] = GraphGraphics.SHAPE_OCTAGON;
    shapes[5] = GraphGraphics.SHAPE_PARALLELOGRAM;
    shapes[6] = GraphGraphics.SHAPE_ROUNDED_RECTANGLE;
    shapes[7] = GraphGraphics.SHAPE_TRIANGLE;
    shapes[8] = GraphGraphics.SHAPE_VEE;
    final Color[] colors = new Color[256];
    final Color[] colorsLow = new Color[256];
    for (int i = 0; i < colors.length; i++) {
      final int color = (0x00ffffff & r.nextInt()) | 0x7f000000;
      colors[i] = new Color(color, true);
      colorsLow[i] = new Color((colors[i].getRed() + 255) / 2,
                               (colors[i].getGreen() + 255) / 2,
                               (colors[i].getBlue() + 255) / 2,
                               255); }
    final GraphLOD lod;
    if (args.length > 1) {
      lod = new GraphLOD() {
          public byte renderEdges(int renderNodeCount,
                                  int totalNodeCount,
                                  int totalEdgeCount) {
            return 1; }
          public boolean detail(int renderNodeCount,
                                int renderEdgeCount) {
            return renderNodeCount < 600; }
          public boolean nodeBorders(int renderNodeCount,
                                     int renderEdgeCount) {
            return renderNodeCount < 250; }
          public boolean textAsShape(int renderNodeCount,
                                     int renderEdgeCount) {
            return renderNodeCount + renderEdgeCount < 30; }
          public boolean edgeArrows(int renderNodeCount,
                                    int renderEdgeCount) {
            return renderNodeCount < 250; } }; }
    else {
      lod = new GraphLOD() {
          public byte renderEdges(int renderNodeCount,
                                  int totalNodeCount,
                                  int totalEdgeCount) {
            if (renderNodeCount >= 30000) return -1;
            return 0; }
          public boolean textAsShape(int renderNodeCount,
                                     int renderEdgeCount) {
            return renderNodeCount + renderEdgeCount < 30; } }; }
    final NodeDetails nodeDetails = new NodeDetails() {
        private final float borderWidth = (float) (minDim / 12);
        private final Color borderColor = new Color(63, 63, 63, 127);
        private final Font font = new Font(null, Font.PLAIN, 1);
        private final double fontScaleFactor = minDim / 2;
        private final Color labelColor = new Color(0, 0, 0, 255);
        public Color colorLowDetail(int node) {
          return colorsLow[node % colorsLow.length]; }
        public byte shape(int node) { return shapes[node % shapes.length]; }
        public Paint fillPaint(int node) {
          return colors[node % colors.length]; }
        public float borderWidth(int node) { return borderWidth; }
        public Paint borderPaint(int node) { return borderColor; }
        public String label(int node) { return "" + node; }
        public Font font(int node) { return font; }
        public double fontScaleFactor(int node) { return fontScaleFactor; }
        public Paint labelPaint(int node) { return labelColor; } };
    final byte[] arrows = new byte[4];
    arrows[0] = GraphGraphics.ARROW_DELTA;
    arrows[1] = GraphGraphics.ARROW_DIAMOND;
    arrows[2] = GraphGraphics.ARROW_DISC;
    arrows[3] = GraphGraphics.ARROW_TEE;
    final EdgeDetails edgeDetails = new EdgeDetails() {
        private final float thickness = (float) (minDim / 18);
        private final Color colorLowDetail = new Color(127, 127, 255, 255);
        private final Color color = new Color(0, 0, 255, 127);
        private final Color arrowColor = new Color(0, 0, 0, 127);
        public Color colorLowDetail(int edge) { return colorLowDetail; }
        public byte sourceArrow(int edge) {
          return arrows[edge % arrows.length]; }
        public float sourceArrowSize(int edge) {
          return thickness * 4; }
        public Paint sourceArrowPaint(int edge) {
          return arrowColor; }
        public byte targetArrow(int edge) {
          return arrows[edge % arrows.length]; }
        public float targetArrowSize(int edge) {
          return thickness * 4; }
        public Paint targetArrowPaint(int edge) {
          return arrowColor; }
        public EdgeAnchors anchors(int edge) {
          return (EdgeAnchors) anchorsHash.get(edge); }
        public float thickness(int edge) { return thickness; }
        public Paint paint(int edge) { return color; } };
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestGraphRendering(graph, rtree, lod,
                                           nodeDetails, edgeDetails);
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 800;
  private final int m_imgHeight = 600;
  private final FixedGraph m_graph;
  private final SpacialIndex2D m_spacial;
  private final GraphLOD m_lod;
  private final NodeDetails m_nodeDetails;
  private final EdgeDetails m_edgeDetails;
  private final IntHash m_hash;
  private final Image m_img;
  private final GraphGraphics m_grafx;

  private double m_currXCenter = 0.0d;
  private double m_currYCenter = 0.0d;
  private double m_currScale = 1.0d;
  private int m_currMouseButton = 0; // 0: none; 2: middle; 3: right.
  private int m_lastXMousePos = 0;
  private int m_lastYMousePos = 0;

  public TestGraphRendering(FixedGraph graph,
                            SpacialIndex2D spacial,
                            GraphLOD lod,
                            NodeDetails nodeDetails,
                            EdgeDetails edgeDetails)
  {
    super();
    m_graph = graph;
    m_spacial = spacial;
    m_lod = lod;
    m_nodeDetails = nodeDetails;
    m_edgeDetails = edgeDetails;
    m_hash = new IntHash();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, false);
    updateImage();
    addMouseListener(this);
    addMouseMotionListener(this);
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
    GraphRenderer.renderGraph(m_graph, m_spacial, m_lod,
                              m_nodeDetails, m_edgeDetails, m_hash,
                              m_grafx, Color.white,
                              m_currXCenter, m_currYCenter, m_currScale);
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON3) {
      m_currMouseButton = 3;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      m_currMouseButton = 2;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON3) {
      if (m_currMouseButton == 3) m_currMouseButton = 0; }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 3) {
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

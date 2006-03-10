package ding.view;

import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntStack;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

class InnerCanvas extends Canvas implements MouseListener, MouseMotionListener
{

  final double[] m_ptBuff = new double[2];
  final float[] m_extentsBuff2 = new float[4];
  final float[] m_floatBuff1 = new float[2];
  final float[] m_floatBuff2 = new float[2];
  final Line2D.Float m_line = new Line2D.Float();
  final GeneralPath m_path = new GeneralPath();
  final GeneralPath m_path2 = new GeneralPath();
  final IntStack m_stack = new IntStack();
  final IntStack m_stack2 = new IntStack();
  final Object m_lock;
  DGraphView m_view;
  GraphLOD m_lod;
  final IntHash m_hash;
  Image m_img;
  GraphGraphics m_grafx;
  Paint m_bgPaint;
  double m_xCenter;
  double m_yCenter;
  double m_scaleFactor;
  private int m_lastRenderDetail = 0;
  private Rectangle m_selectionRect = null;

  InnerCanvas(Object lock, DGraphView view)
  {
    super();
    m_lock = lock;
    m_view = view;
    m_lod = new GraphLOD(); // Default LOD.
    m_hash = new IntHash();
    m_bgPaint = Color.white;
    m_xCenter = 0.0d;
    m_yCenter = 0.0d;
    m_scaleFactor = 1.0d;
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void reshape(int x, int y, int width, int height)
  {
    super.reshape(x, y, width, height);
    if (width > 0 && height > 0) {
      final Image img =
        new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      GraphGraphics grafx = new GraphGraphics(img, false);
      synchronized (m_lock) {
        m_img = img;
        m_grafx = grafx;
        m_lastRenderDetail =
          GraphRenderer.renderGraph((FixedGraph) m_view.m_drawPersp,
                                    m_view.m_spacial,
                                    m_lod,
                                    m_view.m_nodeDetails,
                                    m_view.m_edgeDetails,
                                    m_hash,
                                    m_grafx,
                                    m_bgPaint,
                                    m_xCenter,
                                    m_yCenter,
                                    m_scaleFactor); } }
  }

  public void update(Graphics g)
  {
    if (m_grafx == null) { return; }

    // This is the magical portion of code that transfers what is in the
    // visual data structures into what's on the image.
    synchronized (m_lock) {
      m_lastRenderDetail =
        GraphRenderer.renderGraph((FixedGraph) m_view.m_drawPersp,
                                  m_view.m_spacial,
                                  m_lod,
                                  m_view.m_nodeDetails,
                                  m_view.m_edgeDetails,
                                  m_hash,
                                  m_grafx,
                                  m_bgPaint,
                                  m_xCenter,
                                  m_yCenter,
                                  m_scaleFactor); }
    if (m_selectionRect != null) {
      final Graphics2D g2 = (Graphics2D) m_img.getGraphics();
      g2.setColor(Color.red);
      g2.draw(m_selectionRect); }
    g.drawImage(m_img, 0, 0, null);
  }

  public void paint(Graphics g)
  {
    if (m_img == null) { return; }

    // TODO: Figure out the SRC_OVER and whatnot.
    g.drawImage(m_img, 0, 0, null);
  }

  public void print(Graphics g)
  {
    final ImageImposter img = new ImageImposter(g, getWidth(), getHeight());
    synchronized (m_lock) {
      GraphRenderer.renderGraph((FixedGraph) m_view.m_drawPersp,
                                m_view.m_spacial,
                                m_lod,
                                m_view.m_nodeDetails,
                                m_view.m_edgeDetails,
                                m_hash,
                                new GraphGraphics(img, false),
                                m_bgPaint,
                                m_xCenter,
                                m_yCenter,
                                m_scaleFactor); }
  }

  private int m_currMouseButton = 0;
  private int m_lastXMousePos = 0;
  private int m_lastYMousePos = 0;
  private boolean m_button1NodeDrag = false;

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      m_currMouseButton = 1;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      int[] unselectedNodes = null;
      int[] unselectedEdges = null;
      int chosenNode = 0;
      int chosenEdge = 0;
      boolean chosenNodeSelected = false;
      boolean chosenEdgeSelected = false;
      synchronized (m_lock) {
        if (m_view.m_nodeSelection) {
          m_ptBuff[0] = m_lastXMousePos;
          m_ptBuff[1] = m_lastYMousePos;
          m_view.xformComponentToNodeCoords(m_ptBuff);
          m_stack.empty();
          m_view.getNodesIntersectingRectangle
            ((float) m_ptBuff[0], (float) m_ptBuff[1],
             (float) m_ptBuff[0], (float) m_ptBuff[1],
             (m_lastRenderDetail & GraphRenderer.LOD_HIGH_DETAIL) == 0,
             m_stack);
          chosenNode = (m_stack.size() > 0) ? m_stack.peek() : 0; }
        if (m_view.m_edgeSelection && chosenNode == 0) {
          computeEdgesIntersecting(m_lastXMousePos - 1, m_lastYMousePos - 1,
                                   m_lastXMousePos + 1, m_lastYMousePos + 1);
          chosenEdge = (m_stack2.size() > 0) ? m_stack2.peek() : 0; }
        if (!e.isShiftDown()) {
          // Unselect all nodes and edges.
          unselectedNodes = m_view.getSelectedNodeIndices();
          // Adding this line to speed things up from O(n*log(n)) to O(n).
          m_view.m_selectedNodes.empty();
          for (int i = 0; i < unselectedNodes.length; i++) {
            ((DNodeView) m_view.getNodeView(unselectedNodes[i])).
              unselectInternal(); }
          unselectedEdges = m_view.getSelectedEdgeIndices();
          // Adding this line to speed things up from O(n*log(n)) to O(n).
          m_view.m_selectedEdges.empty();
          for (int i = 0; i < unselectedEdges.length; i++) {
            ((DEdgeView) m_view.getEdgeView(unselectedEdges[i])).
              unselectInternal(); } }
        if (chosenNode != 0) {
          final boolean wasSelected =
            m_view.getNodeView(chosenNode).isSelected();
          if (wasSelected) {
            ((DNodeView) m_view.getNodeView(chosenNode)).unselectInternal();
            chosenNodeSelected = false; }
          else { // Was not selected.
            ((DNodeView) m_view.getNodeView(chosenNode)).selectInternal();
            chosenNodeSelected = true; }
          m_button1NodeDrag = true; }
        if (chosenEdge != 0) {
          final boolean wasSelected =
            m_view.getEdgeView(chosenEdge).isSelected();
          if (wasSelected) {
            ((DEdgeView) m_view.getEdgeView(chosenEdge)).unselectInternal();
            chosenEdgeSelected = false; }
          else { // Was not selected.
            ((DEdgeView) m_view.getEdgeView(chosenEdge)).selectInternal();
            chosenEdgeSelected = true; }
          m_button1NodeDrag = true; }
        if (chosenNode == 0 && chosenEdge == 0) {
          m_selectionRect =
            new Rectangle(m_lastXMousePos, m_lastYMousePos, 0, 0);
          m_button1NodeDrag = false; } }
      repaint();
      final GraphViewChangeListener listener = m_view.m_lis[0];
      if (listener != null) {
        if (unselectedNodes != null && unselectedNodes.length > 0) {
          listener.graphViewChanged
            (new GraphViewNodesUnselectedEvent(m_view, unselectedNodes)); }
        if (unselectedEdges != null && unselectedEdges.length > 0) {
          listener.graphViewChanged
            (new GraphViewEdgesUnselectedEvent(m_view, unselectedEdges)); }
        if (chosenNode != 0) {
          if (chosenNodeSelected) {
            listener.graphViewChanged
              (new GraphViewNodesSelectedEvent
               (m_view, new int[] { chosenNode })); }
          else {
            listener.graphViewChanged
              (new GraphViewNodesUnselectedEvent
               (m_view, new int[] { chosenNode })); } }
        if (chosenEdge != 0) {
          if (chosenEdgeSelected) {
            listener.graphViewChanged
              (new GraphViewEdgesSelectedEvent
               (m_view, new int[] { chosenEdge })); }
          else {
            listener.graphViewChanged
              (new GraphViewEdgesUnselectedEvent
               (m_view, new int[] { chosenEdge })); } } } }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      m_currMouseButton = 2;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
    else if (e.getButton() == MouseEvent.BUTTON3) {
      m_currMouseButton = 3;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (m_currMouseButton == 1) {
        m_currMouseButton = 0;
        if (m_selectionRect != null) {
          int[] selectedNodes = null;
          int[] selectedEdges = null;
          synchronized (m_lock) {
            if (m_view.m_nodeSelection || m_view.m_edgeSelection) {
              if (m_view.m_nodeSelection) {
                m_ptBuff[0] = m_selectionRect.x;
                m_ptBuff[1] = m_selectionRect.y + m_selectionRect.height;
                m_view.xformComponentToNodeCoords(m_ptBuff);
                final double xMin = m_ptBuff[0];
                final double yMin = m_ptBuff[1];
                m_ptBuff[0] = m_selectionRect.x + m_selectionRect.width;
                m_ptBuff[1] = m_selectionRect.y;
                m_view.xformComponentToNodeCoords(m_ptBuff);
                final double xMax = m_ptBuff[0];
                final double yMax = m_ptBuff[1];
                m_stack.empty();
                m_view.getNodesIntersectingRectangle
                  ((float) xMin, (float) yMin, (float) xMax, (float) yMax,
                   (m_lastRenderDetail & GraphRenderer.LOD_HIGH_DETAIL) == 0,
                   m_stack);
                selectedNodes = new int[m_stack.size()];
                final IntEnumerator nodes = m_stack.elements();
                for (int i = 0; i < selectedNodes.length; i++) {
                  selectedNodes[i] = nodes.nextInt(); }
                for (int i = 0; i < selectedNodes.length; i++) {
                  ((DNodeView) m_view.getNodeView(selectedNodes[i])).
                    selectInternal(); } }
              if (m_view.m_edgeSelection) {
                computeEdgesIntersecting
                  (m_selectionRect.x, m_selectionRect.y,
                   m_selectionRect.x + m_selectionRect.width,
                   m_selectionRect.y + m_selectionRect.height);
                selectedEdges = new int[m_stack2.size()];
                final IntEnumerator edges = m_stack2.elements();
                for (int i = 0; i < selectedEdges.length; i++) {
                  selectedEdges[i] = edges.nextInt(); }
                for (int i = 0; i < selectedEdges.length; i++) {
                  ((DEdgeView) m_view.getEdgeView(selectedEdges[i])).
                    selectInternal(); } } } }
          m_selectionRect = null;
          repaint();
          final GraphViewChangeListener listener = m_view.m_lis[0];
          if (listener != null) {
            if (selectedNodes != null && selectedNodes.length > 0) {
              listener.graphViewChanged
                (new GraphViewNodesSelectedEvent
                 (m_view, selectedNodes)); }
            if (selectedEdges != null && selectedEdges.length > 0) {
              listener.graphViewChanged
                (new GraphViewEdgesSelectedEvent
                 (m_view, selectedEdges)); } } } } }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      if (m_currMouseButton == 2) { m_currMouseButton = 0; } }
    else if (e.getButton() == MouseEvent.BUTTON3) {
      if (m_currMouseButton == 3) { m_currMouseButton = 0; } }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 1) {
      if (m_button1NodeDrag) {
        synchronized (m_lock) {
          m_ptBuff[0] = m_lastXMousePos;
          m_ptBuff[1] = m_lastYMousePos;
          m_view.xformComponentToNodeCoords(m_ptBuff);
          final double oldX = m_ptBuff[0];
          final double oldY = m_ptBuff[1];
          m_lastXMousePos = e.getX();
          m_lastYMousePos = e.getY();
          m_ptBuff[0] = m_lastXMousePos;
          m_ptBuff[1] = m_lastYMousePos;
          m_view.xformComponentToNodeCoords(m_ptBuff);
          final double newX = m_ptBuff[0];
          final double newY = m_ptBuff[1];
          final double deltaX = newX - oldX;
          final double deltaY = newY - oldY;
          // TODO: Optimize to not instantiate new array on every call.
          final int[] selectedNodes = m_view.getSelectedNodeIndices();
          for (int i = 0; i < selectedNodes.length; i++) {
            final NodeView nv = m_view.getNodeView(selectedNodes[i]);
            final double oldXPos = nv.getXPosition();
            final double oldYPos = nv.getYPosition();
            nv.setOffset(oldXPos + deltaX, oldYPos + deltaY); } }
        repaint(); }
      if (m_selectionRect != null) {
        final int x = Math.min(m_lastXMousePos, e.getX());
        final int y = Math.min(m_lastYMousePos, e.getY());
        final int w = Math.abs(m_lastXMousePos - e.getX());
        final int h = Math.abs(m_lastYMousePos - e.getY());
        m_selectionRect.setBounds(x, y, w, h);
        repaint(); } }
    else if (m_currMouseButton == 2) {
      double deltaY = e.getY() - m_lastYMousePos;
      synchronized (m_lock) {
        m_lastXMousePos = e.getX();
        m_lastYMousePos = e.getY();
        m_scaleFactor *= Math.pow(2, -deltaY / 300.0d); }
      repaint(); }
    else if (m_currMouseButton == 3) {
      double deltaX = e.getX() - m_lastXMousePos;
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      synchronized (m_lock) {
        m_xCenter -= deltaX / m_scaleFactor;
        m_yCenter += deltaY / m_scaleFactor;  } // y orientations are opposite.
      repaint(); }
  }

  public void mouseMoved(MouseEvent e)
  {
  }

  // Puts [last drawn] edges intersecting onto m_stack2; as RootGraph indices.
  // Depends on the state of several member variables, such as m_hash.
  // Clobbers m_stack and m_ptBuff.
  // The rectangle extents are in component coordinate space.
  // IMPORTANT: Code that calls this method should be holding m_lock.
  private final void computeEdgesIntersecting(final int xMini,
                                              final int yMini,
                                              final int xMaxi,
                                              final int yMaxi)
  {
    m_ptBuff[0] = xMini;
    m_ptBuff[1] = yMaxi;
    m_view.xformComponentToNodeCoords(m_ptBuff);
    final double xMin = m_ptBuff[0];
    final double yMin = m_ptBuff[1];
    m_ptBuff[0] = xMaxi;
    m_ptBuff[1] = yMini;
    m_view.xformComponentToNodeCoords(m_ptBuff);
    final double xMax = m_ptBuff[0];
    final double yMax = m_ptBuff[1];
    IntEnumerator edgeNodesEnum = m_hash.elements(); // Positive.
    m_stack.empty();
    final int edgeNodesCount = edgeNodesEnum.numRemaining();
    for (int i = 0; i < edgeNodesCount; i++) {
      m_stack.push(edgeNodesEnum.nextInt()); }
    m_hash.empty();
    edgeNodesEnum = m_stack.elements();
    m_stack2.empty();
    final FixedGraph graph = (FixedGraph) m_view.m_drawPersp;
    if ((m_lastRenderDetail &
         GraphRenderer.LOD_HIGH_DETAIL) == 0) {
      // We won't need to look up arrows and their sizes.
      for (int i = 0; i < edgeNodesCount; i++) {
        final int node = edgeNodesEnum.nextInt(); // Positive.
        m_view.m_spacial.exists(node, m_view.m_extentsBuff, 0);
        final float nodeX =
          (m_view.m_extentsBuff[0] + m_view.m_extentsBuff[2]) / 2;
        final float nodeY =
          (m_view.m_extentsBuff[1] + m_view.m_extentsBuff[3]) / 2;
        final IntEnumerator touchingEdges =
          graph.edgesAdjacent(node, true, true, true);
        while (touchingEdges.numRemaining() > 0) {
          final int edge = touchingEdges.nextInt(); // Positive.
          final int otherNode = // Positive.
            node ^ graph.edgeSource(edge) ^ graph.edgeTarget(edge);
          if (m_hash.get(otherNode) < 0) {
            m_view.m_spacial.exists
              (otherNode, m_view.m_extentsBuff, 0);
            final float otherNodeX = (m_view.m_extentsBuff[0] +
                                      m_view.m_extentsBuff[2]) / 2;
            final float otherNodeY = (m_view.m_extentsBuff[1] +
                                      m_view.m_extentsBuff[3]) / 2;
            m_line.setLine(nodeX, nodeY, otherNodeX, otherNodeY);
            if (m_line.intersects(xMin, yMin, xMax - xMin,
                                  yMax - yMin)) {
              m_stack2.push(~edge); } } }
        m_hash.put(node); } }
    else { // Last render high detail.
      for (int i = 0; i < edgeNodesCount; i++) {
        final int node = edgeNodesEnum.nextInt(); // Positive.
        m_view.m_spacial.exists(node, m_view.m_extentsBuff, 0);
        final byte nodeShape = m_view.m_nodeDetails.shape(node);
        final IntEnumerator touchingEdges =
          graph.edgesAdjacent(node, true, true, true);
        while (touchingEdges.numRemaining() > 0) {
          final int edge = touchingEdges.nextInt(); // Positive.
          final double segThicknessDiv2 =
            m_view.m_edgeDetails.segmentThickness(edge) / 2.0d;
          final int otherNode =
            node ^ graph.edgeSource(edge) ^ graph.edgeTarget(edge);
          if (m_hash.get(otherNode) < 0) {
            m_view.m_spacial.exists(otherNode, m_extentsBuff2, 0);
            final byte otherNodeShape =
              m_view.m_nodeDetails.shape(otherNode);
            final byte srcShape, trgShape;
            final float[] srcExtents, trgExtents;
            if (node == graph.edgeSource(edge)) {
              srcShape = nodeShape; trgShape = otherNodeShape;
              srcExtents = m_view.m_extentsBuff;
              trgExtents = m_extentsBuff2; }
            else { // node == graph.edgeTarget(edge).
              srcShape = otherNodeShape; trgShape = nodeShape;
              srcExtents = m_extentsBuff2;
              trgExtents = m_view.m_extentsBuff; }
            final byte srcArrow, trgArrow;
            final float srcArrowSize, trgArrowSize;
            if ((m_lastRenderDetail &
                 GraphRenderer.LOD_EDGE_ARROWS) == 0) {
              srcArrow = trgArrow = GraphGraphics.ARROW_NONE;
              srcArrowSize = trgArrowSize = 0.0f; }
            else {
              srcArrow = m_view.m_edgeDetails.sourceArrow(edge);
              trgArrow = m_view.m_edgeDetails.targetArrow(edge);
              srcArrowSize =
                ((srcArrow == GraphGraphics.ARROW_NONE) ? 0.0f :
                 m_view.m_edgeDetails.sourceArrowSize(edge));
              trgArrowSize =
                ((trgArrow == GraphGraphics.ARROW_NONE ||
                  trgArrow == GraphGraphics.ARROW_MONO) ? 0.0f :
                 m_view.m_edgeDetails.targetArrowSize(edge)); }
            final EdgeAnchors anchors =
              (((m_lastRenderDetail &
                GraphRenderer.LOD_EDGE_ANCHORS) == 0) ? null :
               m_view.m_edgeDetails.anchors(edge));
            if (!GraphRenderer.computeEdgeEndpoints
                (m_grafx, srcExtents, srcShape, srcArrow,
                 srcArrowSize, anchors, trgExtents, trgShape,
                 trgArrow, trgArrowSize, m_floatBuff1, m_floatBuff2)) {
              continue; }
            m_grafx.getEdgePath
              (srcArrow, srcArrowSize, trgArrow, trgArrowSize,
               m_floatBuff1[0], m_floatBuff1[1], anchors,
               m_floatBuff2[0], m_floatBuff2[1], m_path);
            GraphRenderer.computeClosedPath
              (m_path.getPathIterator(null), m_path2);
            if (m_path2.intersects
                (xMin - segThicknessDiv2, yMin - segThicknessDiv2,
                 (xMax - xMin) + segThicknessDiv2 * 2,
                 (yMax - yMin) + segThicknessDiv2 * 2)) {
              m_stack2.push(~edge); } } }
        m_hash.put(node); } }
  }

}

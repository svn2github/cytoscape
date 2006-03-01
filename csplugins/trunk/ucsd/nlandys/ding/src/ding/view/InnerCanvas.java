package ding.view;

import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
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
import java.awt.image.BufferedImage;

class InnerCanvas extends Canvas implements MouseListener, MouseMotionListener
{

  final double[] m_ptBuff = new double[2];
  final GeneralPath m_path = new GeneralPath();
  final IntStack m_stack = new IntStack();
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
  private boolean m_lastRenderLowDetail = false;
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
        m_lastRenderLowDetail =
          ((GraphRenderer.renderGraph((FixedGraph) m_view.m_drawPersp,
                                      m_view.m_spacial,
                                      m_lod,
                                      m_view.m_nodeDetails,
                                      m_view.m_edgeDetails,
                                      m_hash,
                                      m_grafx,
                                      m_bgPaint,
                                      m_xCenter,
                                      m_yCenter,
                                      m_scaleFactor) &
            GraphRenderer.LOD_HIGH_DETAIL) == 0); } }
  }

  public void update(Graphics g)
  {
    if (m_grafx == null) { return; }

    // This is the magical portion of code that transfers what is in the
    // visual data structures into what's on the image.
    synchronized (m_lock) {
      m_lastRenderLowDetail =
        ((GraphRenderer.renderGraph((FixedGraph) m_view.m_drawPersp,
                                    m_view.m_spacial,
                                    m_lod,
                                    m_view.m_nodeDetails,
                                    m_view.m_edgeDetails,
                                    m_hash,
                                    m_grafx,
                                    m_bgPaint,
                                    m_xCenter,
                                    m_yCenter,
                                    m_scaleFactor) &
          GraphRenderer.LOD_HIGH_DETAIL) == 0); }
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
      boolean mustRedraw = false;
      int[] unselectedNodes = null;
      int[] unselectedEdges = null;
      int chosenNode = 0;
      boolean chosenNodeSelected = false;
      synchronized (m_lock) {
        if (m_view.m_nodeSelection) {
          m_ptBuff[0] = m_lastXMousePos;
          m_ptBuff[1] = m_lastYMousePos;
          m_view.xformComponentToNodeCoords(m_ptBuff);
          m_stack.empty();
          m_view.getNodesIntersectingRectangle
            ((float) m_ptBuff[0], (float) m_ptBuff[1],
             (float) m_ptBuff[0], (float) m_ptBuff[1],
             m_lastRenderLowDetail, m_stack);
          chosenNode = (m_stack.size() > 0) ? m_stack.peek() : 0;
          if (!e.isShiftDown()) {
            // Unselect all nodes and edges.
            unselectedNodes = m_view.getSelectedNodeIndices();
            if (unselectedNodes.length > 0) { mustRedraw = true; }
            for (int i = 0; i < unselectedNodes.length; i++) {
              ((DNodeView) m_view.getNodeView(unselectedNodes[i])).
                unselectInternal(); }
            unselectedEdges = m_view.getSelectedEdgeIndices();
            if (unselectedEdges.length > 0) { mustRedraw = true; }
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
            mustRedraw = true;
            m_button1NodeDrag = true; }
          else {
            m_selectionRect =
              new Rectangle(m_lastXMousePos, m_lastYMousePos, 0, 0);
            mustRedraw = true;
            m_button1NodeDrag = false; } }
        else { m_button1NodeDrag = false; } }
      if (mustRedraw) { repaint(); }
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
               (m_view, new int[] { chosenNode })); } } } }
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
          m_selectionRect = null;
          repaint(); } } }
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
          m_grafx.xformImageToNodeCoords(m_ptBuff);
          final double oldX = m_ptBuff[0];
          final double oldY = m_ptBuff[1];
          m_lastXMousePos = e.getX();
          m_lastYMousePos = e.getY();
          m_ptBuff[0] = m_lastXMousePos;
          m_ptBuff[1] = m_lastYMousePos;
          m_grafx.xformImageToNodeCoords(m_ptBuff);
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

}

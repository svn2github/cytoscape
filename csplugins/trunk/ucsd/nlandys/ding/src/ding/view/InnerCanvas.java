package ding.view;

import cytoscape.geom.spacial.SpacialEntry2DEnumerator;
import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.util.intr.IntHash;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

class InnerCanvas extends Canvas implements MouseListener, MouseMotionListener
{

  final double[] m_ptBuff = new double[2];
  final GeneralPath m_path = new GeneralPath();
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
    paint(g);
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

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      boolean mustRedraw = false;
      synchronized (m_lock) {
        if (m_view.m_nodeSelection && !e.isShiftDown()) {
          m_grafx.xformImageToNodeCoords(m_ptBuff);
          final SpacialEntry2DEnumerator under = m_view.m_spacial.queryOverlap
            ((float) m_ptBuff[0], (float) m_ptBuff[1],
             (float) m_ptBuff[0], (float) m_ptBuff[1], null, 0, false);
          int chosen = -1;
          while (under.numRemaining() > 0) {
            final int node = under.nextExtents(m_view.m_extentsBuff, 0);
            m_grafx.getNodeShape
              (m_view.m_nodeDetails.shape(node),
               m_view.m_extentsBuff[0], m_view.m_extentsBuff[1],
               m_view.m_extentsBuff[2], m_view.m_extentsBuff[3], m_path);
            if (m_path.contains(m_ptBuff[0], m_ptBuff[1])) {
              chosen = node;
              break; } }
          if (chosen > 0) {
            m_view.getNodeView(~chosen).select();
            mustRedraw = true;
          }
          else { // Unselect all nodes and edges because the background was
            // clicked.
          } } }
      if (mustRedraw) { repaint(); }
    }
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
    if (e.getButton() == MouseEvent.BUTTON2) {
      if (m_currMouseButton == 2) { m_currMouseButton = 0; } }
    else if (e.getButton() == MouseEvent.BUTTON3) {
      if (m_currMouseButton == 3) { m_currMouseButton = 0; } }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 2) {
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

package ding.view;

import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.util.intr.IntHash;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.image.BufferedImage;

class InnerCanvas extends Canvas
{

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

}

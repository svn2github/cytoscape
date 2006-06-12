package cytoscape.fung;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphLOD;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.util.intr.IntHash;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Paint;
import java.awt.image.BufferedImage;

final class InnerCanvas extends Component
{

  private final Fung m_fung;
  private final IntHash m_hash = new IntHash();
  private Image m_img = null;
  GraphGraphics m_grafx;
  Paint m_bgPaint;
  GraphLOD m_lod;
  double m_xCenter;
  double m_yCenter;
  double m_scaleFactor;
  boolean m_viewportChanged = false;
  private int m_lastRenderDetail = 0;

  InnerCanvas(final Fung fung)
  {
    super();
    m_fung = fung;
    m_bgPaint = Color.white;
    m_lod = new GraphLOD();
    m_xCenter = 0.0d;
    m_yCenter = 0.0d;
    m_scaleFactor = 1.0d;
    m_grafx = new GraphGraphics(null, false);
  }

  public final void reshape(final int x, final int y,
                            final int width, final int height)
  {
    super.reshape(x, y, width, height);
    if (width > 0 && height > 0) {
      final Image img =
        new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      final GraphGraphics grafx = new GraphGraphics(img, false);
      synchronized (m_fung.m_lock) {
        m_img = img;
        grafx.importCustomNodeShapes(m_grafx);
        m_grafx = grafx;
        m_viewportChanged = true; } }
  }

  public final void update(final Graphics g)
  {
    if (m_img == null) { return; }
    synchronized (m_fung.m_lock) {
      GraphRenderer.renderGraph(m_fung.m_graphModel.m_graph,
                                m_fung.m_rtree,
                                m_lod,
                                m_fung.m_nodeDetails,
                                m_fung.m_edgeDetails,
                                m_hash,
                                m_grafx,
                                m_bgPaint,
                                m_xCenter,
                                m_yCenter,
                                m_scaleFactor); }
    g.drawImage(m_img, 0, 0, null);
  }

  public final void paint(final Graphics g)
  {
    if (m_img == null) { return; }
    g.drawImage(m_img, 0, 0, null);
  }

  public final void print(final Graphics g)
  {
    final ImageImposter img = new ImageImposter(g, getWidth(), getHeight());
    synchronized (m_fung.m_lock) {
      GraphRenderer.renderGraph(m_fung.m_graphModel.m_graph,
                                m_fung.m_rtree,
                                m_lod,
                                m_fung.m_nodeDetails,
                                m_fung.m_edgeDetails,
                                m_hash,
                                new GraphGraphics(img, false),
                                m_bgPaint,
                                m_xCenter,
                                m_yCenter,
                                m_scaleFactor); }
  }

}

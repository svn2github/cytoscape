package cytoscape.fung;

import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.util.intr.IntHash;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.image.BufferedImage;

final class InnerCanvas extends Canvas
{

  private final Fung m_fung;
  private final IntHash m_hash = new IntHash();
  private Image m_img;
  private GraphGraphics m_grafx;

  InnerCanvas(final Fung fung)
  {
    super();
    m_fung = fung;
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
        m_grafx = grafx;
        GraphRenderer.renderGraph(m_fung.m_graphModel.m_graph,
                                  m_fung.m_rtree,
                                  null, // LOD
                                  m_fung.m_nodeDetails,
                                  m_fung.m_edgeDetails,
                                  m_hash,
                                  m_grafx,
                                  null, // bg paint
                                  0.0d, // x center
                                  0.0d, // y center
                                  1.0d); // scale factor
      } }
  }

}

package ding.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class BirdsEyeView extends Component
{

  private final double[] m_extents = new double[4];
  private final DGraphView m_view;
  private final ContentChangeListener m_cLis;
  private final ViewportChangeListener m_vLis;
  private Image m_img = null;
  private boolean m_resized = false;
  private boolean m_contentChanged = false;
  private boolean m_viewportChanged = false;
  private double m_viewXCenter = 0.0d;
  private double m_viewYCenter = 0.0d;
  private double m_viewScaleFactor = 1.0d;

  public BirdsEyeView(DGraphView view)
  {
    super();
    m_view = view;
    m_cLis = new InnerContentChangeListener();
    m_vLis = new InnerViewportChangeListener();
    addComponentListener(new ComponentAdapter() {
        public void componentShown(ComponentEvent evt) {
          m_view.addContentChangeListener(m_cLis);
          m_view.addViewportChangeListener(m_vLis); }
        public void componentHidden(ComponentEvent evt) {
          m_view.removeContentChangeListener(m_cLis);
          m_view.removeViewportChangeListener(m_vLis); } });
  }

  public void reshape(int x, int y, int width, int height)
  {
    super.reshape(x, y, width, height);
    if (width > 0 && height > 0) {
      m_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); }
    m_resized = true;
  }

  public void update(Graphics g)
  {
    if (m_img == null) { return; }
    if (m_contentChanged || m_resized) {
      final double xCenter, yCenter, scaleFactor;
      if (m_view.getExtents(m_extents)) {
        xCenter = (m_extents[0] + m_extents[2]) / 2.0d;
        yCenter = (m_extents[1] + m_extents[3]) / 2.0d;
        scaleFactor = 0.9d * Math.min
          (((double) getWidth()) / (m_extents[2] - m_extents[0]),
           ((double) getHeight()) / (m_extents[3] - m_extents[1])); }
      else {
        xCenter = 0.0d; yCenter = 0.0d; scaleFactor = 1.0d; }
      m_view.drawSnapshot(m_img, m_view.getGraphLOD(),
                          m_view.getBackgroundPaint(),
                          xCenter, yCenter, scaleFactor);
      m_contentChanged = false;
      m_resized = false; }
    g.drawImage(m_img, 0, 0, null);
  }

  public void paint(Graphics g)
  {
    update(g);
  }

  private final class InnerContentChangeListener
    implements ContentChangeListener
  {

    public void contentChanged()
    {
      m_contentChanged = true;
      repaint();
    }

  }

  private final class InnerViewportChangeListener
    implements ViewportChangeListener
  {

    public void viewportChanged(double newXCenter, double newYCenter,
                                double newScaleFactor)
    {
      m_viewportChanged = true;
      m_viewXCenter = newXCenter;
      m_viewYCenter = newYCenter;
      m_viewScaleFactor = newScaleFactor;
//       repaint();
    }

  }

}

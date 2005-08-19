package cytoscape.render.test;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;

public final class TryBezierCurveSelection extends Frame
{

  public static final void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TryBezierCurveSelection();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 640;
  private final int m_imgHeight = 480;
  private final Image m_img;
  private final GeneralPath m_curve1;
//   private final GeneralPath m_curve2;
//   private final GeneralPath m_curve3;

  public TryBezierCurveSelection()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_curve1 = new GeneralPath();
    m_curve1.moveTo(10.0f, 10.0f);
    m_curve1.curveTo(300.0f, 10.0f, 310.0f, 20.0f, 310.0f, 320.0f);
    m_curve1.curveTo(310.0f, 620.0f, 100.0f, 300.0f, 590.0f, 150.0f);
    updateImage();
  }

  private final void updateImage()
  {
    final Graphics2D g2d = (Graphics2D) m_img.getGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                         RenderingHints.VALUE_STROKE_PURE);
    g2d.setBackground(Color.white);
    g2d.clearRect(0, 0, m_imgWidth, m_imgHeight);
    g2d.setColor(Color.black);
    g2d.draw(m_curve1);
  }

  public void paint(final Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  public boolean isResizable() { return false; }

}

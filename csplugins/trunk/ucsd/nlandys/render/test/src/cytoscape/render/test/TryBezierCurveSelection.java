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

  public TryBezierCurveSelection()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
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

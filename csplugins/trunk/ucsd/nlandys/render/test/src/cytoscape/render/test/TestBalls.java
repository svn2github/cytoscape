package cytoscape.render.test;

import cytoscape.render.immed.GraphGraphics;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class TestBalls extends Frame
{

  public final static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestBalls();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 600;
  private final int m_imgHeight = 480;
  private final Image m_img;
  private final GraphGraphics m_grafx;

  public TestBalls()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, Color.white, true);
    generateImage();
  }

  public void paint(final Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  private final void generateImage()
  {
    m_grafx.clear(0.0d, 0.0d, 1.0d);
    m_grafx.drawNodeFull(GraphGraphics.SHAPE_ELLIPSE,
                         -300.0f, -240.0f,
                         -200.0f, -180.0f,
                         Color.red, 5.0f, Color.black);
  }

  public boolean isResizable() { return false; }

}

package cytoscape.render.test;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import cytoscape.render.immed.GraphGraphics;

public final class DrawNodeShapes extends Frame
{

  public final static void main(final String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          for (int i = 0; i <= 7; i++) {
            Frame f = new DrawNodeShapes(i);
            f.show();
            f.move(i * 10, i * 10);
            f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                  System.exit(0); } }); } } });
  }

  private final int m_imgWidth = 64;
  private final int m_imgHeight = 64;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private final byte m_shape;

  public DrawNodeShapes(int shape)
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, true);
    m_shape = (byte) shape;
    updateNodeImage();
  }

  public void paint(final Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  public void update(Graphics g)
  {
    final Insets insets = insets();
    updateNodeImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private final void updateNodeImage()
  {
    m_grafx.clear(Color.white, 0.0d, 0.0d, 1.0d);
    m_grafx.drawNodeFull(m_shape, -28.0f, -28.0f, 28.0f, 28.0f,
                         Color.yellow, 3.5f, Color.black);
  }

}

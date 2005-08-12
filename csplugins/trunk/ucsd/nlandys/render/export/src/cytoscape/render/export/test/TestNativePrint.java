package cytoscape.render.export.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestNativePrint extends Frame implements MouseListener
{

  public static void main(String[] args) throws Exception
  {
    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestNativePrint();
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final int m_imgWidth = 640;
  private final int m_imgHeight = 480;
  private final Color m_bgColor = Color.white;
  private final Image m_img;

  public TestNativePrint()
  {
    super();
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    drawGraph(new GraphGraphics(m_img, m_bgColor, true));
    addMouseListener(this);
  }

  public void paint(Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  private void drawGraph(GraphGraphics gg)
  {
    gg.clear(-80.0d, -70.0d, 1.6d);
    gg.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                    -200.0f, -200.0f, -100.0f, -50.0f,
                    new Color(255, 0, 0, 128), 3.0f,
                    new Color(0, 0, 0, 128));
    gg.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                    -220.0f, -100.0f, -30.0f, 0.0f,
                    new Color(0, 0, 255, 128), 3.0f,
                    new Color(0, 0, 0, 128));
    gg.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                    -130.0f, -150.0f, -15.0f, 10.0f,
                    new Color(0, 255, 0, 128), 3.0f,
                    new Color(0, 0, 0, 128));
    gg.drawTextFull(new Font("Dialog", Font.PLAIN, 12),
                    2.0d, "Click here to print.",
                    -20.0f, 35.0f, Color.black, true);
  }

  public boolean isResizable() { return false; }

  public void mouseClicked(MouseEvent e) {
    System.out.println("Great!"); }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}


}

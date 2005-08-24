package cytoscape.render.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;

public class TestNativePrint extends Frame
  implements MouseListener, Printable
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
    drawGraph(new GraphGraphics(m_img, true));
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
    gg.clear(m_bgColor, -80.0d, -70.0d, 1.6d);
    gg.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                    -200.0f, -200.0f, -100.0f, -50.0f,
                    new Color(255, 0, 0, 127), 3.0f,
                    new Color(0, 0, 0, 127));
    gg.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                    -220.0f, -100.0f, -30.0f, 0.0f,
                    new Color(0, 0, 255, 127), 3.0f,
                    new Color(0, 0, 0, 127));
    gg.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                    -130.0f, -150.0f, -15.0f, 10.0f,
                    new Color(0, 255, 0, 127), 3.0f,
                    new Color(0, 0, 0, 127));
    gg.drawEdgeFull(GraphGraphics.ARROW_DELTA, 10.0f,
                    new Color(255, 0, 255, 127),
                    GraphGraphics.ARROW_TEE, 10.0f,
                    new Color(0, 0, 255, 127),
                    71.4f, 20.83f, -10.1f, -81.3f, 3.0f,
                    new Color(255, 255, 0, 127), 9.0f);
    gg.drawTextFull(new Font("Dialog", Font.PLAIN, 12),
                    2.0d, "Click here to print.",
                    -20.0f, 35.0f, Color.black, true);
  }

  public boolean isResizable() { return false; }

  public void mouseClicked(MouseEvent e)
  {
    final PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog()) {
      try { printJob.print(); }
      catch (PrinterException pe) {
        pe.printStackTrace(System.err); } }
  }

  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

  public int print(Graphics g, PageFormat pageFormat, int pageInx)
  {
    if (pageInx > 0) { return Printable.NO_SUCH_PAGE; }
    final Graphics2D g2d = (Graphics2D) g;
    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    drawGraph(new GraphGraphics
              (new ImageImposter(g2d, m_imgWidth, m_imgHeight), true));
    return Printable.PAGE_EXISTS;
  }

}

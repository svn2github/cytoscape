package cytoscape.render.export.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;

public class MinimalTest
{

  public static void main(String[] args)
  {
    final int width = 200;
    final int height = 200;
    final PSGraphics2D psg = new PSGraphics2D(System.out,
                                              new Dimension(width, height));
    psg.startExport();
    Graphics2D grafx = (Graphics2D) psg.create();
    grafx.setBackground(Color.yellow);
    grafx.clearRect(0, 0, width, height);
    grafx.dispose();
    grafx = null;
    grafx = (Graphics2D) psg.create();
    grafx.setBackground(Color.yellow);
    grafx.clearRect(0, 0, width, height);
    grafx.setColor(Color.black);
    grafx.fillRect(10, 10, 100, 100);
    grafx.drawString("hello", 110, 150);
    psg.endExport();
  }

}

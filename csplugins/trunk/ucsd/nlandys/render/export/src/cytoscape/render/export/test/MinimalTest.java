package cytoscape.render.export.test;

import java.awt.Color;
import java.awt.Dimension;
import org.freehep.graphicsio.ps.PSGraphics2D;

public class MinimalTest
{

  public static void main(String[] args)
  {
    final int width = 200;
    final int height = 200;
    PSGraphics2D g = new PSGraphics2D(System.out,
                                      new Dimension(width, height));
    g.startExport();
    g.setColor(Color.black);
    g.fillRect(10, 10, 100, 100);
    g.drawString("hello", 110, 150);
    g.endExport();
  }

}

package cytoscape.render.export.test;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Properties;
import org.freehep.graphicsio.ps.PSGraphics2D;

public class MinimalTest
{

  public static void main(String[] args)
  {
    final int width = 400;
    final int height = 300;
    final Properties p = new Properties();
    p.setProperty("PageSize", "A5");
    PSGraphics2D g = new PSGraphics2D(System.out,
                                      new Dimension(width, height));
    g.setProperties(p);
    g.startExport();
    g.setColor(Color.black);
    g.fillRect(10, 10, 100, 100);
    g.endExport();
  }

}

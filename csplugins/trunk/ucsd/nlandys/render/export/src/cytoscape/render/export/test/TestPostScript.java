package cytoscape.render.export.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import org.freehep.graphicsio.ps.PSGraphics2D;

public class TestPostScript
{

  public static void main(String[] args)
  {
    final int width = 600;
    final int height = 480;
    final PSGraphics2D psGrafx =
      new PSGraphics2D(System.out, new Dimension(width, height));
    final Image img = new ImageImposter(psGrafx, width, height);
    final GraphGraphics grafx = new GraphGraphics(img, Color.white, false);
    psGrafx.startExport();
    grafx.clear(0.0d, 0.0d, 1.0d);
    grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                       -101.3f, -20.9f, 23.34f, 67.81f, Color.red, 1.4f,
                       Color.black);
    psGrafx.endExport();
  }

}

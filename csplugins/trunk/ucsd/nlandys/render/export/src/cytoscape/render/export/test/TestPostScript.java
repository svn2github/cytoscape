package cytoscape.render.export.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import org.freehep.graphicsio.ps.PSGraphics2D;

public class TestPostScript
{

  public static void main(String[] args)
  {
    final int width = 200;
    final int height = 200;
    final PSGraphics2D psGrafx =
      new PSGraphics2D(System.out, new Dimension(width, height));
    final Image img = new ImageImposter(psGrafx, width, height);
    final GraphGraphics grafx =
      new GraphGraphics(img, Color.white,// 0, false);
                        GraphGraphics.FLAG_TEXT_AS_STRING, false);
    psGrafx.startExport();
    grafx.clear(0.0d, 0.0d, 1.0d);
    grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                       -87.3f, -20.9f, 23.34f, 67.81f, Color.cyan, 1.4f,
                       Color.black);
    final Font font = new Font("Serif", Font.PLAIN, 14);
    grafx.drawText(font, 1.0d, "Tequst", -31.98f, 23.455f, Color.black);
    grafx.drawEdgeFull(GraphGraphics.ARROW_DELTA, 10.0f, Color.magenta,
                       GraphGraphics.ARROW_TEE, 10.0f, Color.blue,
                       71.4f, 20.83f, -10.1f, -81.3f,
                       3.0f, Color.orange, 9.0f);
    psGrafx.endExport();
  }

}

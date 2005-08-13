package cytoscape.render.export.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import org.jibble.epsgraphics.EpsGraphics2D;

public class TestPostScript2
{

  public static void main(String[] args) throws Exception
  {
    final int width = 200;
    final int height = 200;
    final EpsGraphics2D psGrafx = new EpsGraphics2D
      ("foo", System.out, 0, 0, width, height);
    final Image img = new ImageImposter(psGrafx, width, height);
    final GraphGraphics grafx = new GraphGraphics(img, Color.white, false);
    grafx.clear(0.0d, 0.0d, 1.0d);
    grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                       -87.3f, -20.9f, 23.34f, 67.81f, Color.cyan, 1.4f,
                       Color.black);
    final Font font = new Font("Serif", Font.PLAIN, 14);
    grafx.drawTextFull(font, 1.0d, "Tequst", -31.98f, 23.455f, Color.black,
                       args.length > 0);
    grafx.drawEdgeFull(GraphGraphics.ARROW_DELTA, 10.0f, Color.magenta,
                       GraphGraphics.ARROW_TEE, 10.0f, Color.blue,
                       71.4f, 20.83f, -10.1f, -81.3f,
                       3.0f, Color.orange, 9.0f);
    psGrafx.flush();
  }

}

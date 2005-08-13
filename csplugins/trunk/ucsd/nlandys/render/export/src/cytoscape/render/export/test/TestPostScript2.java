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
    final int width = 640;
    final int height = 480;
    final EpsGraphics2D psGrafx = new EpsGraphics2D
      ("foo", System.out, 0, 0, width, height);
    final Image img = new ImageImposter(psGrafx, width, height);
    final GraphGraphics gg = new GraphGraphics(img, false);
    gg.clear(Color.white, -80.0d, -70.0d, 1.6d);
    final int alpha = 128;
    gg.drawNodeFull(GraphGraphics.SHAPE_RECTANGLE,
                    -200.0f, -200.0f, -100.0f, -50.0f,
                    new Color(255, 0, 0, alpha), 3.0f,
                    new Color(0, 0, 0, alpha));
    gg.drawNodeFull(GraphGraphics.SHAPE_RECTANGLE,
                    -220.0f, -100.0f, -30.0f, 0.0f,
                    new Color(0, 0, 255, alpha), 3.0f,
                    new Color(0, 0, 0, alpha));
    gg.drawNodeFull(GraphGraphics.SHAPE_RECTANGLE,
                    -130.0f, -150.0f, -15.0f, 10.0f,
                    new Color(0, 255, 0, alpha), 3.0f,
                    new Color(0, 0, 0, alpha));
    psGrafx.flush();
  }

}

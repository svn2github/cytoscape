package cytoscape.render.export.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import org.freehep.graphicsio.ps.PSGraphics2D;

public class TestTranslucentPostScript
{

  public static void main(String[] args)
  {
    final Color transparentWhite = new Color(255, 255, 255, 0);
    final Color translucentCyan = new Color(0, 255, 255, 128);
    final Color translucentBlack = new Color(0, 0, 0, 128);
    final Color translucentMagenta = new Color(255, 0, 255, 128);
    final Color translucentBlue = new Color(0, 0, 255, 128);
    final Color translucentYellow = new Color(255, 255, 0, 128);
    final int width = 200;
    final int height = 200;
    final PSGraphics2D psGrafx =
      new PSGraphics2D(System.out, new Dimension(width, height));
    final Image img = new ImageImposter(psGrafx, width, height);
    final GraphGraphics grafx = new GraphGraphics(img, false);
    psGrafx.startExport();
    grafx.clear(args.length > 0 ? Color.white : transparentWhite,
                0.0d, 0.0d, 1.0d);
    grafx.drawNodeFull(GraphGraphics.SHAPE_ROUNDED_RECTANGLE,
                       -87.3f, -20.9f, 23.34f, 67.81f,
                       args.length > 0 ? Color.cyan : translucentCyan,
                       1.4f, args.length > 0 ? Color.black : translucentBlack);
    grafx.drawNodeFull(GraphGraphics.SHAPE_HEXAGON,
                       -30.43f, -50.87f, 63.68f, 32.6f,
                       args.length > 0 ? Color.cyan : translucentCyan,
                       1.4f, args.length > 0 ? Color.black : translucentBlack);
    final Font font = new Font("Serif", Font.PLAIN, 14);
    grafx.drawTextFull(font, 1.0d, "Tequst", -31.98f, 23.455f,
                       args.length > 0 ? Color.black : translucentBlack,
                       true);
    grafx.drawEdgeFull(GraphGraphics.ARROW_DELTA, 10.0f,
                       args.length > 0 ? Color.magenta : translucentMagenta,
                       GraphGraphics.ARROW_TEE, 10.0f,
                       args.length > 0 ? Color.blue : translucentBlue,
                       71.4f, 20.83f, -10.1f, -81.3f, 3.0f,
                       args.length > 0 ? Color.yellow : translucentYellow,
                       9.0f);
    psGrafx.endExport();
  }

}

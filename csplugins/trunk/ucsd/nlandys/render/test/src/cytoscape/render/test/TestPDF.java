package cytoscape.render.test;

import cytoscape.render.export.ImageImposter;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import org.freehep.graphicsio.pdf.PDFGraphics2D;

public class TestPDF
{

  public static void main(String[] args)
  {
    final int width = 640;
    final int height = 480;
    final PDFGraphics2D pdfGrafx =
      new PDFGraphics2D(System.out, new Dimension(width, height));
    final Image img = new ImageImposter(pdfGrafx, width, height);
    final GraphGraphics gg = new GraphGraphics(img, false);
    pdfGrafx.startExport();
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
    pdfGrafx.endExport();
  }

}

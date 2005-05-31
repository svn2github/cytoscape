package cytoscape.render.immed.util;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Graphics2D;

final class GraphGraphics2Java2D implements GraphGraphics
{

  GraphGraphics2Java2D(Graphics2D graphics)
  {
  }

  public final void extents(double[] extentsArr) {}

  public final void clear() {}

  public final void hiDrawNode(byte shape, double width, double height,
                               double xPos, double yPos, int fillColorRGB,
                               byte borderType, double borderWidth,
                               int borderColorRGB) {}

  public final void loDrawNode(double width, double height, double xPos,
                               double yPos, int fillColorRGB) {}

}

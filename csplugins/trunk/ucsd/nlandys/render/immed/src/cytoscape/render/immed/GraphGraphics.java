package cytoscape.render.immed;

public interface GraphGraphics
{

  public static final byte SHAPE_DIAMOND = 0;
  public static final byte SHAPE_ELLIPSE = 1;
  public static final byte SHAPE_HEXAGON = 2;
  public static final byte SHAPE_OCTAGON = 3;
  public static final byte SHAPE_PARALLELOGRAM = 4;
  public static final byte SHAPE_RECTANGLE = 5;
  public static final byte SHAPE_TRIANGLE = 6;

  public static final byte BORDER_NONE = 0;
  public static final byte BORDER_SOLID = 1;
  public static final byte BORDER_DASHED = 2;

  public void extents(double[] extentsArr);

  public void clear();

  public void hiDrawNode(byte shape, double width, double height,
                         double xPos, double yPos, int fillColorRGB,
                         byte borderType, double borderWidth,
                         int borderColorRGB);

  public void loDrawNode(double width, double height, double xPos, double yPos,
                         int fillColorRGB);

}

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

  /**
   * Copies the graphics object's extents into the input array specified.
   * A graphics object's extents do not change during the duration
   * of the graphics object's lifespan.<p>
   * Any drawing operations done outside of the graphics object's extents
   * will not be visible, and will otherwise do no harm.<p>
   * The information written into extentsArr is as follows:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>array index</th>  <th>value</th>  </tr>
   *   <tr>  <td>offset</td>       <td>xMin</td>   </tr>
   *   <tr>  <td>offset+1</td>     <td>yMin</td>   </tr>
   *   <tr>  <td>offset+2</td>     <td>xMax</td>   </tr>
   *   <tr>  <td>offset+3</td>     <td>yMax</td>   </tr>
   * </table></blockquote>
   */
  public void extents(double[] extentsArr, int offset);

  public void clear();

  public void hiDrawNode(byte shape, double width, double height,
                         double xPos, double yPos, int fillColorRGB,
                         byte borderType, double borderWidth,
                         int borderColorRGB);

  public void loDrawNode(double width, double height, double xPos, double yPos,
                         int fillColorRGB);

}

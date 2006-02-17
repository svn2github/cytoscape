package cytoscape.fung;

import cytoscape.render.stateful.NodeDetails;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;

public abstract class Label
{

  public static final byte ANCHOR_CENTER = NodeDetails.ANCHOR_CENTER;
  public static final byte ANCHOR_NORTH = NodeDetails.ANCHOR_NORTH;
  public static final byte ANCHOR_NORTHEAST = NodeDetails.ANCHOR_NORTHEAST;
  public static final byte ANCHOR_EAST = NodeDetails.ANCHOR_EAST;
  public static final byte ANCHOR_SOUTHEAST = NodeDetails.ANCHOR_SOUTHEAST;
  public static final byte ANCHOR_SOUTH = NodeDetails.ANCHOR_SOUTH;
  public static final byte ANCHOR_SOUTHWEST = NodeDetails.ANCHOR_SOUTHWEST;
  public static final byte ANCHOR_WEST = NodeDetails.ANCHOR_WEST;
  public static final byte ANCHOR_NORTHWEST = NodeDetails.ANCHOR_NORTHWEST;

  public static final byte JUSTIFY_CENTER =
    NodeDetails.LABEL_WRAP_JUSTIFY_CENTER;
  public static final byte JUSTIFY_LEFT =
    NodeDetails.LABEL_WRAP_JUSTIFY_LEFT;
  public static final byte JUSTIFY_RIGHT =
    NodeDetails.LABEL_WRAP_JUSTIFY_RIGHT;

  final String m_text;
  final Font m_font;
  final double m_scaleFactor;
  final Paint m_paint;
  final byte m_textAnchor;
  final float m_offsetVectorX;
  final float m_offsetVectorY;
  final byte m_justify;

  Label(String text, Font font, double scaleFactor, Paint paint,
        byte textAnchor, float offsetVectorX, float offsetVectorY,
        byte justify)
  {
    m_text = text;
    if (m_text == null) { throw new NullPointerException("text is null"); }
    m_font = font;
    if (m_font == null) { throw new NullPointerException("font is null"); }
    m_scaleFactor = scaleFactor;
    if (!(m_scaleFactor > 0.0d)) {
      throw new IllegalArgumentException("scaleFactor is not positive"); }
    m_paint = paint;
    if (m_paint == null) { throw new NullPointerException("paint is null"); }
    m_textAnchor = textAnchor;
    switch (m_textAnchor) {
    case ANCHOR_CENTER:
    case ANCHOR_NORTH:
    case ANCHOR_NORTHEAST:
    case ANCHOR_EAST:
    case ANCHOR_SOUTHEAST:
    case ANCHOR_SOUTH:
    case ANCHOR_SOUTHWEST:
    case ANCHOR_WEST:
    case ANCHOR_NORTHWEST:
      break;
    default:
      throw new IllegalArgumentException("textAnchor is not recognized"); }
    m_offsetVectorX = offsetVectorX;
    m_offsetVectorY = offsetVectorY;
    m_justify = justify;
    switch (m_justify) {
    case JUSTIFY_CENTER:
    case JUSTIFY_LEFT:
    case JUSTIFY_RIGHT:
      break;
    default:
      throw new IllegalArgumentException("justify is not recognized"); }
  }

  public final String getText()
  {
    return m_text;
  }

  public final Font getFont()
  {
    return m_font;
  }

  public final double getScaleFactor()
  {
    return m_scaleFactor;
  }

  public final Paint getPaint()
  {
    return m_paint;
  }

  public final byte getTextAnchor()
  {
    return m_textAnchor;
  }

  public final Point2D getOffsetVector()
  {
    return new Point2D.Double(m_offsetVectorX, m_offsetVectorY);
  }

  public final byte getJustify()
  {
    return m_justify;
  }

}

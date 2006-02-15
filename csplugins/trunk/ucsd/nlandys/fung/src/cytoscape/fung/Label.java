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
  final double m_offsetVectorX;
  final double m_offsetVectorY;
  final byte m_justify;

  Label(String text, Font font, double scaleFactor, Paint paint,
        byte textAnchor, double offsetVectorX, double offsetVectorY,
        byte justify)
  {
    m_text = text;
    m_font = font;
    m_scaleFactor = scaleFactor;
    m_paint = paint;
    m_textAnchor = textAnchor;
    m_offsetVectorX = offsetVectorX;
    m_offsetVectorY = offsetVectorY;
    m_justify = justify;
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

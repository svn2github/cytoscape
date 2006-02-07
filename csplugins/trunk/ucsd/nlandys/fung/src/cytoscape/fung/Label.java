package cytoscape.fung;

import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;

public abstract class Label
{

  final String m_text = null;
  final Font m_font = null;
  final double m_scaleFactor = 1.0d;
  final Paint m_paint = null;
  final byte m_textAnchor = 0;

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
    return null;
  }

  public final byte getJustify()
  {
    return 0;
  }

}

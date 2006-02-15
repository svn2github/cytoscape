package cytoscape.fung;

import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;

public final class NodeLabel extends Label
{

  final byte m_nodeAnchor;

  public NodeLabel(final String text,
                   final Font font,
                   final double scaleFactor,
                   final Paint paint,
                   final byte textAnchor,
                   final byte nodeAnchor,
                   final Point2D offsetVector,
                   final byte justify)
  {
    super(text, font, scaleFactor, paint, textAnchor,
          offsetVector.getX(), offsetVector.getY(), justify);
    m_nodeAnchor = nodeAnchor;
  }

  public final byte getNodeAnchor()
  {
    return m_nodeAnchor;
  }

}

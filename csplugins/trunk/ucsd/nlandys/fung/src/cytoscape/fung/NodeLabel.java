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
          (float) offsetVector.getX(), (float) offsetVector.getY(), justify);
    m_nodeAnchor = nodeAnchor;
    switch (m_nodeAnchor) {
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
      throw new IllegalArgumentException("nodeAnchor is not recognized"); }
  }

  public final byte getNodeAnchor()
  {
    return m_nodeAnchor;
  }

}

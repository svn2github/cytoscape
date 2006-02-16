package cytoscape.fung;

import cytoscape.render.stateful.EdgeDetails;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;

public final class EdgeLabel extends Label
{

  public static final byte EDGE_ANCHOR_MIDPOINT =
    EdgeDetails.EDGE_ANCHOR_MIDPOINT;
  public static final byte EDGE_ANCHOR_SOURCE =
    EdgeDetails.EDGE_ANCHOR_SOURCE;
  public static final byte EDGE_ANCHOR_TARGET =
    EdgeDetails.EDGE_ANCHOR_TARGET;

  final byte m_edgeAnchor;

  public EdgeLabel(final String text,
                   final Font font,
                   final double scaleFactor,
                   final Paint paint,
                   final byte textAnchor,
                   final byte edgeAnchor,
                   final Point2D offsetVector,
                   final byte justify)
  {
    super(text, font, scaleFactor, paint, textAnchor,
          offsetVector.getX(), offsetVector.getY(), justify);
    m_edgeAnchor = edgeAnchor;
    switch (m_edgeAnchor) {
    case EDGE_ANCHOR_MIDPOINT:
    case EDGE_ANCHOR_SOURCE:
    case EDGE_ANCHOR_TARGET:
      break;
    default:
      throw new IllegalArgumentException("edgeAnchor is not recognized"); }
  }

  public final byte getEdgeAnchor()
  {
    return m_edgeAnchor;
  }

}

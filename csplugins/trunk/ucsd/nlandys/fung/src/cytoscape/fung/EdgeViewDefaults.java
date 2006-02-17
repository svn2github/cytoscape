package cytoscape.fung;

import java.awt.Color;

public final class EdgeViewDefaults
{

  public final static Color DEFAULT_COLOR_LOW_DETAIL = Color.blue;
  public final static byte DEFAULT_SOURCE_ARROW = EdgeView.ARROW_NONE;

  final Color m_colorLowDetail;
  final byte m_sourceArrow;

  public EdgeViewDefaults()
  {
    this(DEFAULT_COLOR_LOW_DETAIL, DEFAULT_SOURCE_ARROW);
  }

  public EdgeViewDefaults(final Color colorLowDetail,
                          final byte sourceArrow)
  {
    m_colorLowDetail = colorLowDetail;
    if (m_colorLowDetail == null) {
      throw new NullPointerException("colorLowDetail is null"); }
    m_sourceArrow = sourceArrow;
    switch (m_sourceArrow) {
    case EdgeView.ARROW_NONE:
    case EdgeView.ARROW_DELTA:
    case EdgeView.ARROW_DIAMOND:
    case EdgeView.ARROW_DISC:
    case EdgeView.ARROW_TEE:
      break;
    default:
      throw new IllegalArgumentException("sourceArrow is unrecognized"); }
  }

  public final Color getColorLowDetail()
  {
    return m_colorLowDetail;
  }

  public final byte getSourceArrow()
  {
    return m_sourceArrow;
  }

}

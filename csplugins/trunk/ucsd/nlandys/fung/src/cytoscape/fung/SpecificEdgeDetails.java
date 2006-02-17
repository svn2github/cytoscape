package cytoscape.fung;

import java.awt.Color;

final class SpecificEdgeDetails extends DefaultEdgeDetails
{

  final ObjArray m_colorsLowDetail = new ObjArray();

  SpecificEdgeDetails(final Fung fung)
  {
    super(fung);
  }

  final void unregisterEdge(final int edge)
  {
    m_colorsLowDetail.setObjAtIndex(null, edge);
  }

  public final Color colorLowDetail(final int edge)
  {
    final Object o = m_colorsLowDetail.getObjAtIndex(edge);
    if (o == null) { return super.colorLowDetail(edge); }
    return (Color) o;
  }

  /*
   * A null color has the special meaning to use default color.
   */
  final void overrideColorLowDetail(final int edge, final Color color)
  {
    if (color == null ||
        color.equals(super.colorLowDetail(edge))) {
      m_colorsLowDetail.setObjAtIndex(null, edge); }
    else {
      m_colorsLowDetail.setObjAtIndex(color, edge); }
  }

}

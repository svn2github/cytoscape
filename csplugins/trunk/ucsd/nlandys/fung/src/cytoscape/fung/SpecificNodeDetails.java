package cytoscape.fung;

import java.awt.Color;

final class SpecificNodeDetails extends DefaultNodeDetails
{

  final ObjArray m_colorsLowDetail = new ObjArray();

  SpecificNodeDetails(final Fung fung)
  {
    super(fung);
  }

  final void unregisterNode(final int node)
  {
    m_colorsLowDetail.setObjAtIndex(null, node);
  }

  public final Color colorLowDetail(final int node)
  {
    final Object o = m_colorsLowDetail.getObjAtIndex(node);
    if (o == null) { return super.colorLowDetail(node); }
    return (Color) o;
  }

  final void overrideColorLowDetail(final int node, final Color color)
  {
    if (color == null ||
        color.equals(super.colorLowDetail(node))) {
      m_colorsLowDetail.setObjAtIndex(null, node); }
    else {
      m_colorsLowDetail.setObjAtIndex(color, node); }
  }

}

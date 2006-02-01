package cytoscape.fung;

import cytoscape.util.intr.IntArray;
import java.awt.Color;
import java.util.HashMap;

final class SpecificNodeDetails extends DefaultNodeDetails
{

  final ObjArray m_colorsLowDetail = new ObjArray();
  final IntArray m_shapes = new IntArray();

  SpecificNodeDetails(final Fung fung)
  {
    super(fung);
  }

  final void unregisterNode(final int node)
  {
    m_colorsLowDetail.setObjAtIndex(null, node);
    m_shapes.setIntAtIndex(0, node);
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

  public final byte shape(final int node)
  {
    final int i = m_shapes.getIntAtIndex(node);
    if (i == 0) { return super.shape(node); }
    return (byte) (i - 256);
  }

  final void overrideShape(final int node, final byte shape)
  {
    if (shape < 0 || shape == super.shape(node)) {
      m_shapes.setIntAtIndex(0, node); }
    else {
      m_shapes.setIntAtIndex(256 + (int) shape, node); }
  }

}

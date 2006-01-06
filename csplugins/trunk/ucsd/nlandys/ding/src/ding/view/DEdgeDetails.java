package ding.view;

import cytoscape.util.intr.IntObjHash;
import java.awt.Color;
import java.util.HashMap;

class DEdgeDetails extends IntermediateEdgeDetails
{

  final IntObjHash m_colorsLowDetail = new IntObjHash();
  final Object m_deletedEntry = new Object();

  final HashMap m_segmentThicknesses = new HashMap();

  public Color colorLowDetail(int edge)
  {
    final Object o = m_colorsLowDetail.get(edge);
    if (o == null || o == m_deletedEntry) {
      return super.colorLowDetail(edge); }
    return (Color) o;
  }

  /*
   * A null color has the special meaning to remove overridden color.
   */
  void overrideColorLowDetail(int edge, Color color)
  {
    if (color == null ||
        color.equals(super.colorLowDetail(edge))) {
      final Object val = m_colorsLowDetail.get(edge);
      if (val != null && val != m_deletedEntry) {
        m_colorsLowDetail.put(edge, m_deletedEntry); } }
    else {
      m_colorsLowDetail.put(edge, color); }
  }

  public float segmentThickness(int edge)
  {
    final Object o = m_segmentThicknesses.get(new Integer(edge));
    if (o == null) { return super.segmentThickness(edge); }
    return ((Float) o).floatValue();
  }

  /*
   * A negative thickness value has the special meaning to remove overridden
   * thickness.
   */
  void overrideSegmentThickness(int edge, float thickness)
  {
    if (thickness < 0.0f ||
        thickness == super.segmentThickness(edge)) {
      m_segmentThicknesses.remove(new Integer(edge)); }
    else {m_segmentThicknesses.put(new Integer(edge), new Float(thickness)); }
  }

}

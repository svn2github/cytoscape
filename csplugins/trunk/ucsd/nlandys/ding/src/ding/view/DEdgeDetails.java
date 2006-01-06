package ding.view;

import cytoscape.util.intr.IntObjHash;
import java.awt.Color;
import java.awt.Paint;
import java.util.HashMap;

class DEdgeDetails extends IntermediateEdgeDetails
{

  final IntObjHash m_colorsLowDetail = new IntObjHash();
  final Object m_deletedEntry = new Object();

  final HashMap m_segmentThicknesses = new HashMap();
  final HashMap m_segmentPaints = new HashMap();

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
    else { m_segmentThicknesses.put(new Integer(edge), new Float(thickness)); }
  }

  public Paint segmentPaint(int edge)
  {
    final Object o = m_segmentPaints.get(new Integer(edge));
    if (o == null) { return super.segmentPaint(edge); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideSegmentPaint(int edge, Paint paint)
  {
    if (paint == null ||
        paint.equals(super.segmentPaint(edge))) {
      m_segmentPaints.remove(new Integer(edge)); }
    else { m_segmentPaints.put(new Integer(edge), paint); }
  }

}

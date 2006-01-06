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
  final HashMap m_sourceArrowPaints = new HashMap();
  final HashMap m_targetArrowPaints = new HashMap();
  final HashMap m_segmentPaints = new HashMap();
  final HashMap m_labelPaints = new HashMap();

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

  public Paint sourceArrowPaint(int edge)
  {
    final Object o = m_sourceArrowPaints.get(new Integer(edge));
    if (o == null) { return super.sourceArrowPaint(edge); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideSourceArrowPaint(int edge, Paint paint)
  {
    if (paint == null ||
        paint.equals(super.sourceArrowPaint(edge))) {
      m_sourceArrowPaints.remove(new Integer(edge)); }
    else { m_sourceArrowPaints.put(new Integer(edge), paint); }
  }

  public Paint targetArrowPaint(int edge)
  {
    final Object o = m_targetArrowPaints.get(new Integer(edge));
    if (o == null) { return super.targetArrowPaint(edge); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideTargetArrowPaint(int edge, Paint paint)
  {
    if (paint == null ||
        paint.equals(super.targetArrowPaint(edge))) {
      m_targetArrowPaints.remove(new Integer(edge)); }
    else { m_targetArrowPaints.put(new Integer(edge), paint); }
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

  public Paint labelPaint(int node, int labelInx)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    final Object o = m_labelPaints.get(new Long(key));
    if (o == null) { return super.labelPaint(node, labelInx); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideLabelPaint(int node, int labelInx, Paint paint)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    if (paint == null ||
        paint.equals(super.labelPaint(node, labelInx))) {
      m_labelPaints.remove(new Long(key)); }
    else { m_labelPaints.put(new Long(key), paint); }
  }

}

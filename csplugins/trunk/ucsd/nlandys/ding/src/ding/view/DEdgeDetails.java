package ding.view;

import cytoscape.render.immed.EdgeAnchors;
import cytoscape.util.intr.IntObjHash;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.HashMap;

class DEdgeDetails extends IntermediateEdgeDetails
{

  final DGraphView m_view;

  final IntObjHash m_colorsLowDetail = new IntObjHash();
  final Object m_deletedEntry = new Object();

  final HashMap m_segmentThicknesses = new HashMap();
  final HashMap m_sourceArrows = new HashMap();
  final HashMap m_sourceArrowPaints = new HashMap();
  final HashMap m_targetArrows = new HashMap();
  final HashMap m_targetArrowPaints = new HashMap();
  final HashMap m_segmentPaints = new HashMap();
  final HashMap m_segmentDashLengths = new HashMap();
  final HashMap m_labelCounts = new HashMap();
  final HashMap m_labelTexts = new HashMap();
  final HashMap m_labelFonts = new HashMap();
  final HashMap m_labelPaints = new HashMap();

  DEdgeDetails(DGraphView view)
  {
    m_view = view;
  }

  void unregisterEdge(int edge)
  {
    final Object o = m_colorsLowDetail.get(edge);
    if (o != null && o != m_deletedEntry) {
      m_colorsLowDetail.put(edge, m_deletedEntry); }
    final Integer key = new Integer(edge);
    m_segmentThicknesses.remove(key);
    m_sourceArrows.remove(key);
    m_sourceArrowPaints.remove(key);
    m_targetArrows.remove(key);
    m_targetArrowPaints.remove(key);
    m_segmentPaints.remove(key);
    m_segmentDashLengths.remove(key);
    m_labelCounts.remove(key);
    m_labelTexts.remove(key);
    m_labelFonts.remove(key);
    m_labelPaints.remove(key);
  }

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

  public byte sourceArrow(int edge)
  {
    final Object o = m_sourceArrows.get(new Integer(edge));
    if (o == null) { return super.sourceArrow(edge); }
    return ((Byte) o).byteValue();
  }

  /*
   * A non-negative arrowType has the special meaning to remove overridden
   * arrow.
   */
  void overrideSourceArrow(int edge, byte arrowType)
  {
    if (arrowType >= 0 ||
        arrowType == super.sourceArrow(edge)) {
      m_sourceArrows.remove(new Integer(edge)); }
    else { m_sourceArrows.put(new Integer(edge), new Byte(arrowType)); }
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

  public byte targetArrow(int edge)
  {
    final Object o = m_targetArrows.get(new Integer(edge));
    if (o == null) { return super.targetArrow(edge); }
    return ((Byte) o).byteValue();
  }

  /*
   * A non-negative arrowType has the special meaning to remove overridden
   * arrow.
   */
  void overrideTargetArrow(int edge, byte arrowType)
  {
    if (arrowType >= 0 ||
        arrowType == super.targetArrow(edge)) {
      m_targetArrows.remove(new Integer(edge)); }
    else { m_targetArrows.put(new Integer(edge), new Byte(arrowType)); }
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

  public EdgeAnchors anchors(int edge)
  {
    return (EdgeAnchors) (m_view.getEdgeView(~edge));
  }

  public float anchorSize(int edge, int anchorInx)
  {
    if (m_view.getEdgeView(~edge).isSelected()) {
      return m_view.getAnchorSize(); }
    else {
      return 0.0f; }
  }

  public Paint anchorPaint(int edge, int anchorInx)
  {
    if (((DEdgeView) (m_view.getEdgeView(~edge))).
        m_unselectedAnchors.count(anchorInx) > 0) {
      return m_view.getAnchorUnselectedPaint(); }
    else {
      return m_view.getAnchorSelectedPaint(); }
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

  public float segmentDashLength(int edge)
  {
    final Object o = m_segmentDashLengths.get(new Integer(edge));
    if (o == null) { return super.segmentDashLength(edge); }
    return ((Float) o).floatValue();
  }

  /*
   * A negative length value has the special meaning to remove overridden
   * length.
   */
  void overrideSegmentDashLength(int edge, float length)
  {
    if (length < 0.0f ||
        length == super.segmentDashLength(edge)) {
      m_segmentDashLengths.remove(new Integer(edge)); }
    else { m_segmentDashLengths.put(new Integer(edge), new Float(length)); }
  }

  public int labelCount(int edge)
  {
    final Object o = m_labelCounts.get(new Integer(edge));
    if (o == null) { return super.labelCount(edge); }
    return ((Integer) o).intValue();
  }

  /*
   * A negative labelCount has the special meaning to remove overridden count.
   */
  void overrideLabelCount(int edge, int labelCount)
  {
    if (labelCount < 0 ||
        labelCount == super.labelCount(edge)) {
      m_labelCounts.remove(new Integer(edge)); }
    else { m_labelCounts.put(new Integer(edge), new Integer(labelCount)); }
  }

  public String labelText(int edge, int labelInx)
  {
    final long key = (((long) edge) << 32) | ((long) labelInx);
    final Object o = m_labelTexts.get(new Long(key));
    if (o == null) { return super.labelText(edge, labelInx); }
    return (String) o;
  }

  /*
   * A null text has the special meaning to remove overridden text.
   */
  void overrideLabelText(int edge, int labelInx, String text)
  {
    final long key = (((long) edge) << 32) | ((long) labelInx);
    if (text == null ||
        text.equals(super.labelText(edge, labelInx))) {
      m_labelTexts.remove(new Long(key)); }
    else { m_labelTexts.put(new Long(key), text); }
  }

  public Font labelFont(int edge, int labelInx)
  {
    final long key = (((long) edge) << 32) | ((long) labelInx);
    final Object o = m_labelFonts.get(new Long(key));
    if (o == null) { return super.labelFont(edge, labelInx); }
    return (Font) o;
  }

  /*
   * A null font has the special meaning to remove overridden font.
   */
  void overrideLabelFont(int edge, int labelInx, Font font)
  {
    final long key = (((long) edge) << 32) | ((long) labelInx);
    if (font == null ||
        font.equals(super.labelFont(edge, labelInx))) {
      m_labelFonts.remove(new Long(key)); }
    else { m_labelFonts.put(new Long(key), font); }
  }

  public Paint labelPaint(int edge, int labelInx)
  {
    final long key = (((long) edge) << 32) | ((long) labelInx);
    final Object o = m_labelPaints.get(new Long(key));
    if (o == null) { return super.labelPaint(edge, labelInx); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideLabelPaint(int edge, int labelInx, Paint paint)
  {
    final long key = (((long) edge) << 32) | ((long) labelInx);
    if (paint == null ||
        paint.equals(super.labelPaint(edge, labelInx))) {
      m_labelPaints.remove(new Long(key)); }
    else { m_labelPaints.put(new Long(key), paint); }
  }

}

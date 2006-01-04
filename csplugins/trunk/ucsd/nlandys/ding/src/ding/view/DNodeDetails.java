package ding.view;

import cytoscape.render.stateful.NodeDetails;
import cytoscape.util.intr.IntObjHash;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.HashMap;

/*
 * Access to the methods of this class should be synchronized externally if
 * there is a threat of multiple threads.
 */
class DNodeDetails extends IntermediateNodeDetails
{

  final IntObjHash m_colorsLowDetail = new IntObjHash();
  final Object m_deletedEntry = new Object();

  // The values are Byte objects; the bytes are shapes defined in
  // cytoscape.render.immed.GraphGraphics.
  final HashMap m_shapes = new HashMap();
  final HashMap m_fillPaints = new HashMap();
  final HashMap m_borderWidths = new HashMap();
  final HashMap m_borderPaints = new HashMap();
  final HashMap m_labelCounts = new HashMap();
  final HashMap m_labelTexts = new HashMap();
  final HashMap m_labelFonts = new HashMap();
  final HashMap m_labelPaints = new HashMap();

  public Color colorLowDetail(int node)
  {
    final Object o = m_colorsLowDetail.get(node);
    if (o == null || o == m_deletedEntry) {
      return super.colorLowDetail(node); }
    return (Color) o;
  }

  /*
   * A null color has the special meaning to remove overridden color.
   */
  void overrideColorLowDetail(int node, Color color)
  {
    if (color == null ||
        color.equals(super.colorLowDetail(node))) {
      final Object val = m_colorsLowDetail.get(node);
      if (val != null && val != m_deletedEntry) {
        m_colorsLowDetail.put(node, m_deletedEntry); } }
    else {
      m_colorsLowDetail.put(node, color); }
  }

  public byte shape(int node)
  {
    final Object o = m_shapes.get(new Integer(node));
    if (o == null) { return super.shape(node); }
    return ((Byte) o).byteValue();
  }

  /*
   * The shape argument must be pre-checked for correctness.
   * A negative shape value has the special meaning to remove overridden shape.
   */
  void overrideShape(int node, byte shape)
  {
    if (shape < 0 ||
        shape == super.shape(node)) { m_shapes.remove(new Integer(node)); }
    else { m_shapes.put(new Integer(node), new Byte(shape)); }
  }

  public Paint fillPaint(int node)
  {
    final Object o = m_fillPaints.get(new Integer(node));
    if (o == null) { return super.fillPaint(node); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideFillPaint(int node, Paint paint)
  {
    if (paint == null ||
        paint.equals(super.fillPaint(node))) {
      m_fillPaints.remove(new Integer(node)); }
    else { m_fillPaints.put(new Integer(node), paint); }
  }

  public float borderWidth(int node)
  {
    final Object o = m_borderWidths.get(new Integer(node));
    if (o == null) { return super.borderWidth(node); }
    return ((Float) o).floatValue();
  }

  /*
   * A negative width value has the special meaning to remove overridden width.
   */
  void overrideBorderWidth(int node, float width)
  {
    if (width < 0.0f ||
        width == super.borderWidth(node)) {
      m_borderWidths.remove(new Integer(node)); }
    else { m_borderWidths.put(new Integer(node), new Float(width)); }
  }

  public Paint borderPaint(int node)
  {
    final Object o = m_borderPaints.get(new Integer(node));
    if (o == null) { return super.borderPaint(node); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to remove overridden paint.
   */
  void overrideBorderPaint(int node, Paint paint)
  {
    if (paint == null ||
        paint.equals(super.borderPaint(node))) {
      m_borderPaints.remove(new Integer(node)); }
    else { m_borderPaints.put(new Integer(node), paint); }
  }

  public int labelCount(int node)
  {
    final Object o = m_labelCounts.get(new Integer(node));
    if (o == null) { return super.labelCount(node); }
    return ((Integer) o).intValue();
  }

  /*
   * A negative labelCount has the special meaning to remove overridden count.
   */
  void overrideLabelCount(int node, int labelCount)
  {
    if (labelCount < 0 ||
        labelCount == super.labelCount(node)) {
      m_labelCounts.remove(new Integer(node)); }
    else { m_labelCounts.put(new Integer(node), new Integer(labelCount)); }
  }

  public String labelText(int node, int labelInx)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    final Object o = m_labelTexts.get(new Long(key));
    if (o == null) { return super.labelText(node, labelInx); }
    return (String) o;
  }

  /*
   * A null text has the special meaning to remove overridden text.
   */
  void overrideLabelText(int node, int labelInx, String text)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    if (text == null ||
        text.equals(super.labelText(node, labelInx))) {
      m_labelTexts.remove(new Long(key)); }
    else { m_labelTexts.put(new Long(key), text); }
  }

  public Font labelFont(int node, int labelInx)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    final Object o = m_labelFonts.get(new Long(key));
    if (o == null) { return super.labelFont(node, labelInx); }
    return (Font) o;
  }

  /*
   * A null font has the special meaning to remove overridden font.
   */
  void overrideLabelFont(int node, int labelInx, Font font)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    if (font == null ||
        font.equals(super.labelFont(node, labelInx))) {
      m_labelFonts.remove(new Long(key)); }
    else { m_labelFonts.put(new Long(key), font); }
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

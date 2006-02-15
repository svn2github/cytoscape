package cytoscape.fung;

import cytoscape.util.intr.IntArray;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.HashMap;

final class SpecificNodeDetails extends DefaultNodeDetails
{

  final ObjArray m_colorsLowDetail = new ObjArray();
  final IntArray m_shapes = new IntArray();
  final ObjArray m_fillPaints = new ObjArray();
  final ObjArray m_borderWidths = new ObjArray();
  final ObjArray m_borderPaints = new ObjArray();
  final IntArray m_labelCounts = new IntArray();
  final HashMap m_labelTexts = new HashMap();
  final HashMap m_labelFonts = new HashMap();
  final HashMap m_labelPaints = new HashMap();

  SpecificNodeDetails(final Fung fung)
  {
    super(fung);
  }

  final void unregisterNode(final int node)
  {
    m_colorsLowDetail.setObjAtIndex(null, node);
    m_shapes.setIntAtIndex(0, node);
    m_fillPaints.setObjAtIndex(null, node);
    m_borderWidths.setObjAtIndex(null, node);
    m_borderPaints.setObjAtIndex(null, node);
    final int labelCount = m_labelCounts.getIntAtIndex(node);
    m_labelCounts.setIntAtIndex(0, node);
    for (int i = 0; i < labelCount; i++) {
      final Long key = new Long((((long) node) << 32) | ((long) i));
      m_labelTexts.remove(key);
      m_labelFonts.remove(key);
      m_labelPaints.remove(key); }
  }

  public final Color colorLowDetail(final int node)
  {
    final Object o = m_colorsLowDetail.getObjAtIndex(node);
    if (o == null) { return super.colorLowDetail(node); }
    return (Color) o;
  }

  /*
   * A null color has the special meaning to use default color.
   */
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

  /*
   * The shape argument must be pre-checked for correctness.
   */
  final void overrideShape(final int node, final byte shape)
  {
    if (shape == super.shape(node)) { m_shapes.setIntAtIndex(0, node); }
    else { m_shapes.setIntAtIndex(256 + (int) shape, node); }
  }

  public final Paint fillPaint(final int node)
  {
    final Object o = m_fillPaints.getObjAtIndex(node);
    if (o == null) { return super.fillPaint(node); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to use default paint.
   */
  final void overrideFillPaint(final int node, final Paint paint)
  {
    if (paint == null ||
        paint.equals(super.fillPaint(node))) {
      m_fillPaints.setObjAtIndex(null, node); }
    else {
      m_fillPaints.setObjAtIndex(paint, node); }
  }

  public final float borderWidth(final int node)
  {
    final Object o = m_borderWidths.getObjAtIndex(node);
    if (o == null) { return super.borderWidth(node); }
    return ((Float) o).floatValue();
  }

  /*
   * The width argument must be pre-checked for correctness; it should not
   * be negative, for example.
   */
  final void overrideBorderWidth(final int node, final float width)
  {
    if (width == super.borderWidth(node)) {
      m_borderWidths.setObjAtIndex(null, node); }
    else {
      m_borderWidths.setObjAtIndex(new Float(width), node); }
  }

  public final Paint borderPaint(final int node)
  {
    final Object o = m_borderPaints.getObjAtIndex(node);
    if (o == null) { return super.borderPaint(node); }
    return (Paint) o;
  }

  /*
   * A null paint has the special meaning to use default paint.
   */
  final void overrideBorderPaint(final int node, final Paint paint)
  {
    if (paint == null ||
        paint.equals(super.borderPaint(node))) {
      m_borderPaints.setObjAtIndex(null, node); }
    else {
      m_borderPaints.setObjAtIndex(paint, node); }
  }

  public final int labelCount(final int node)
  {
    return m_labelCounts.getIntAtIndex(node);
  }

  final void overrideLabelCount(final int node, final int count)
  {
    m_labelCounts.setIntAtIndex(count, node);
  }

  public final String labelText(final int node, final int labelInx)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    return (String) m_labelTexts.get(new Long(key));
  }

  final void overrideLabelText(final int node, final int labelInx,
                               final String text)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    if (text == null) { m_labelTexts.remove(new Long(key)); }
    else { m_labelTexts.put(new Long(key), text); }
  }

  public final Font labelFont(final int node, final int labelInx)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    return (Font) m_labelFonts.get(new Long(key));
  }

  final void overrideLabelFont(final int node, final int labelInx,
                               final Font font)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    if (font == null) { m_labelFonts.remove(new Long(key)); }
    else { m_labelFonts.put(new Long(key), font); }
  }

  public final Paint labelPaint(final int node, final int labelInx)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    return (Paint) m_labelPaints.get(new Long(key));
  }

  final void overrideLabelPaint(final int node, final int labelInx,
                                final Paint paint)
  {
    final long key = (((long) node) << 32) | ((long) labelInx);
    if (paint == null) { m_labelPaints.remove(new Long(key)); }
    else { m_labelPaints.put(new Long(key), paint); }
  }

}

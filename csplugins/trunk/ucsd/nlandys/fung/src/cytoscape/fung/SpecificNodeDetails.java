package cytoscape.fung;

import cytoscape.util.intr.IntArray;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Vector;

final class SpecificNodeDetails extends DefaultNodeDetails
{

  final ObjArray m_colorsLowDetail = new ObjArray();
  final IntArray m_shapes = new IntArray();
  final ObjArray m_fillPaints = new ObjArray();
  final ObjArray m_borderWidths = new ObjArray();
  final ObjArray m_borderPaints = new ObjArray();
  final HashMap m_labels = new HashMap();

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
    m_labels.remove(new Integer(node));
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
    final Object v = m_labels.get(new Integer(node));
    if (v == null) { return 0; }
    return ((Vector) v).size();
  }

  public final String labelText(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getText();
  }

  public final Font labelFont(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getFont();
  }

  public final double labelScaleFactor(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getScaleFactor();
  }

  public final Paint labelPaint(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getPaint();
  }

  public final byte labelTextAnchor(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getTextAnchor();
  }

  public final byte labelNodeAnchor(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getNodeAnchor();
  }

  public final float labelOffsetVectorX(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return (float) (((NodeLabel) v.get(labelInx)).getOffsetVector().getX());
  }

  public final float labelOffsetVectorY(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return (float) (((NodeLabel) v.get(labelInx)).getOffsetVector().getY());
  }

  public final byte labelJustify(final int node, final int labelInx)
  {
    final Vector v = (Vector) m_labels.get(new Integer(node));
    return ((NodeLabel) v.get(labelInx)).getJustify();
  }

}

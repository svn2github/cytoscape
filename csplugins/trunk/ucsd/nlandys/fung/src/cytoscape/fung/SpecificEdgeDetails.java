package cytoscape.fung;

import cytoscape.util.intr.IntArray;
import java.awt.Color;
import java.awt.Paint;

final class SpecificEdgeDetails extends DefaultEdgeDetails
{

  final ObjArray m_colorsLowDetail = new ObjArray();
  final IntArray m_sourceArrows = new IntArray();
  final ObjArray m_sourceArrowSizes = new ObjArray();
  final ObjArray m_sourceArrowPaints = new ObjArray();

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

  public final byte sourceArrow(final int edge)
  {
    final int i = m_sourceArrows.getIntAtIndex(edge);
    if (i == 0) { return super.sourceArrow(edge); }
    return (byte) (i - 256);
  }

  /*
   * The arrow argument must be pre-checked for correctness.
   */
  final void overrideSourceArrow(final int edge, final byte arrow)
  {
    if (arrow == super.sourceArrow(edge)) {
      m_sourceArrows.setIntAtIndex(0, edge); }
    else {
      m_sourceArrows.setIntAtIndex(256 + (int) arrow, edge); }
  }

  public final float sourceArrowSize(final int edge)
  {
    final Object o = m_sourceArrowSizes.getObjAtIndex(edge);
    if (o == null) { return super.sourceArrowSize(edge); }
    return ((Float) o).floatValue();
  }

  final void overrideSourceArrowSize(final int edge, final float size)
  {
    if (size == super.sourceArrowSize(edge)) {
      m_sourceArrowSizes.setObjAtIndex(null, edge); }
    else {
      m_sourceArrowSizes.setObjAtIndex(new Float(size), edge); }
  }

  public final Paint sourceArrowPaint(final int edge)
  {
    final Object o = m_sourceArrowPaints.getObjAtIndex(edge);
    if (o == null) { return super.sourceArrowPaint(edge); }
    return (Paint) o;
  }

  final void overrideSourceArrowPaint(final int edge, final Paint paint)
  {
    if (paint == null ||
        paint.equals(super.sourceArrowPaint(edge))) {
      m_sourceArrowPaints.setObjAtIndex(null, edge); }
    else {
      m_sourceArrowPaints.setObjAtIndex(paint, edge); }
  }

}

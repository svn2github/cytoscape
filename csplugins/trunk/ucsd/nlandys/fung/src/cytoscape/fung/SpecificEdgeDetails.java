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
  final IntArray m_targetArrows = new IntArray();
  final ObjArray m_targetArrowSizes = new ObjArray();
  final ObjArray m_targetArrowPaints = new ObjArray();
  final ObjArray m_segmentThicknesses = new ObjArray();
  final ObjArray m_segmentPaints = new ObjArray();

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

  public final byte targetArrow(final int edge)
  {
    final int i = m_targetArrows.getIntAtIndex(edge);
    if (i == 0) { return super.targetArrow(edge); }
    return (byte) (i - 256);
  }

  /*
   * The arrow argument must be pre-checked for correctness.
   */
  final void overrideTargetArrow(final int edge, final byte arrow)
  {
    if (arrow == super.targetArrow(edge)) {
      m_targetArrows.setIntAtIndex(0, edge); }
    else {
      m_targetArrows.setIntAtIndex(256 + (int) arrow, edge); }
  }

  public final float targetArrowSize(final int edge)
  {
    final Object o = m_targetArrowSizes.getObjAtIndex(edge);
    if (o == null) { return super.targetArrowSize(edge); }
    return ((Float) o).floatValue();
  }

  final void overrideTargetArrowSize(final int edge, final float size)
  {
    if (size == super.targetArrowSize(edge)) {
      m_targetArrowSizes.setObjAtIndex(null, edge); }
    else {
      m_targetArrowSizes.setObjAtIndex(new Float(size), edge); }
  }

  public final Paint targetArrowPaint(final int edge)
  {
    final Object o = m_targetArrowPaints.getObjAtIndex(edge);
    if (o == null) { return super.targetArrowPaint(edge); }
    return (Paint) o;
  }

  final void overrideTargetArrowPaint(final int edge, final Paint paint)
  {
    if (paint == null ||
        paint.equals(super.targetArrowPaint(edge))) {
      m_targetArrowPaints.setObjAtIndex(null, edge); }
    else {
      m_targetArrowPaints.setObjAtIndex(paint, edge); }
  }

  public final float segmentThickness(final int edge)
  {
    final Object o = m_segmentThicknesses.getObjAtIndex(edge);
    if (o == null) { return super.segmentThickness(edge); }
    return ((Float) o).floatValue();
  }

  final void overrideSegmentThickness(final int edge, final float thickness)
  {
    if (thickness == super.segmentThickness(edge)) {
      m_segmentThicknesses.setObjAtIndex(null, edge); }
    else {
      m_segmentThicknesses.setObjAtIndex(new Float(thickness), edge); }
  }

  public final Paint segmentPaint(final int edge)
  {
    final Object o = m_segmentPaints.getObjAtIndex(edge);
    if (o == null) { return super.segmentPaint(edge); }
    return (Paint) o;
  }

  final void overrideSegmentPaints(final int edge, final Paint paint)
  {
    if (paint == null ||
        paint.equals(super.segmentPaint(edge))) {
      m_segmentPaints.setObjAtIndex(null, edge); }
    else {
      m_segmentPaints.setObjAtIndex(paint, edge); }
  }

}

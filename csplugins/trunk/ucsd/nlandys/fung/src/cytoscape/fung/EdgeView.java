package cytoscape.fung;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Vector;

public final class EdgeView
{

  public final static byte ARROW_NONE = GraphGraphics.ARROW_NONE;
  public final static byte ARROW_DELTA = GraphGraphics.ARROW_DELTA;
  public final static byte ARROW_DIAMOND = GraphGraphics.ARROW_DIAMOND;
  public final static byte ARROW_DISC = GraphGraphics.ARROW_DISC;
  public final static byte ARROW_TEE = GraphGraphics.ARROW_TEE;

  Fung m_fung; // Not final so that we can destroy reference.
  private final int m_edge;

  EdgeView(final Fung fung, final int edge)
  {
    m_fung = fung;
    m_edge = edge;
  }

  public final int getEdge()
  {
    return m_edge;
  }

  public final Color getColorLowDetail()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.colorLowDetail(m_edge); }
  }

  public final void setColorLowDetail(final Color colorLowDetail)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_edgeDetails.overrideColorLowDetail(m_edge, colorLowDetail); }
  }

  public final byte getSourceArrow()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.sourceArrow(m_edge); }
  }

  public final void setSourceArrow(final byte arrow)
  {
    synchronized (m_fung.m_lock) {
      switch (arrow) {
      case ARROW_NONE:
      case ARROW_DELTA:
      case ARROW_DIAMOND:
      case ARROW_DISC:
      case ARROW_TEE:
        break;
      default:
        throw new IllegalArgumentException("arrow is not recognized"); }
      { // Reconcile arrow size if not ARROW_NONE.
        if (arrow != ARROW_NONE) {
          final float segmentThickness =
            m_fung.m_edgeDetails.segmentThickness(m_edge);
          final float sourceArrowSize =
            m_fung.m_edgeDetails.sourceArrowSize(m_edge);
          if (!(sourceArrowSize >= segmentThickness)) {
            m_fung.m_edgeDetails.overrideSourceArrowSize
              (m_edge, segmentThickness); } }
      }
      m_fung.m_edgeDetails.overrideSourceArrow(m_edge, arrow); }
  }

  public final double getSourceArrowSize()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.sourceArrowSize(m_edge); }
  }

  public final void setSourceArrowSize(final double arrowSize)
  {
    synchronized (m_fung.m_lock) {
      float fArrowSize = (float) arrowSize;
      if (!(fArrowSize >= 0.0f)) {
        throw new IllegalArgumentException
          ("arrowSize must be positive or zero"); }
      { // Reconcile arrow size.
        if (m_fung.m_edgeDetails.sourceArrow(m_edge) != ARROW_NONE) {
          final double segmentThickness =
            m_fung.m_edgeDetails.segmentThickness(m_edge);
          if (!(fArrowSize >= segmentThickness)) {
            fArrowSize = (float) segmentThickness; } }
      }
      m_fung.m_edgeDetails.overrideSourceArrowSize(m_edge, fArrowSize); }
  }

  public final Paint getSourceArrowPaint()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.sourceArrowPaint(m_edge); }
  }

  public final void setSourceArrowPaint(final Paint paint)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_edgeDetails.overrideSourceArrowPaint(m_edge, paint); }
  }

  public final byte getTargetArrow()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.targetArrow(m_edge); }
  }

  public final void setTargetArrow(final byte arrow)
  {
    synchronized (m_fung.m_lock) {
      switch (arrow) {
      case ARROW_NONE:
      case ARROW_DELTA:
      case ARROW_DIAMOND:
      case ARROW_DISC:
      case ARROW_TEE:
        break;
      default:
        throw new IllegalArgumentException("arrow is not recognized"); }
      { // Reconcile arrow size if not ARROW_NONE.
        if (arrow != ARROW_NONE) {
          final float segmentThickness =
            m_fung.m_edgeDetails.segmentThickness(m_edge);
          final float targetArrowSize =
            m_fung.m_edgeDetails.targetArrowSize(m_edge);
          if (!(targetArrowSize >= segmentThickness)) {
            m_fung.m_edgeDetails.overrideTargetArrowSize
              (m_edge, segmentThickness); } }
      }
      m_fung.m_edgeDetails.overrideTargetArrow(m_edge, arrow); }
  }

  public final double getTargetArrowSize()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.targetArrowSize(m_edge); }
  }

  public final void setTargetArrowSize(final double arrowSize)
  {
    synchronized (m_fung.m_lock) {
      float fArrowSize = (float) arrowSize;
      if (!(fArrowSize >= 0.0f)) {
        throw new IllegalArgumentException
          ("arrowSize must be positive or zero"); }
      { // Reconcile arrow size.
        if (m_fung.m_edgeDetails.targetArrow(m_edge) != ARROW_NONE) {
          final double segmentThickness =
            m_fung.m_edgeDetails.segmentThickness(m_edge);
          if (!(fArrowSize >= segmentThickness)) {
            fArrowSize = (float) segmentThickness; } }
      }
      m_fung.m_edgeDetails.overrideTargetArrowSize(m_edge, fArrowSize); }
  }

  public final Paint getTargetArrowPaint()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.targetArrowPaint(m_edge); }
  }

  public final void setTargetArrowPaint(final Paint paint)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_edgeDetails.overrideTargetArrowPaint(m_edge, paint); }
  }

  public final double getSegmentThickness()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.segmentThickness(m_edge); }
  }

  public final void setSegmentThickness(final double segmentThickness)
  {
    synchronized (m_fung.m_lock) {
      final float fSegmentThickness = (float) segmentThickness;
      if (!(fSegmentThickness >= 0.0f)) {
        throw new IllegalArgumentException
          ("segmentThickness must be positive or zero"); }
      { // Reconcile with arrow sizes.
        if (m_fung.m_edgeDetails.sourceArrow(m_edge) != ARROW_NONE) {
          if (m_fung.m_edgeDetails.sourceArrowSize(m_edge) >
              fSegmentThickness) {
            m_fung.m_edgeDetails.overrideSourceArrowSize
              (m_edge, fSegmentThickness); } }
        if (m_fung.m_edgeDetails.targetArrow(m_edge) != ARROW_NONE) {
          if (m_fung.m_edgeDetails.targetArrowSize(m_edge) >
              fSegmentThickness) {
            m_fung.m_edgeDetails.overrideTargetArrowSize
              (m_edge, fSegmentThickness); } }
      }
      m_fung.m_edgeDetails.overrideSegmentThickness(m_edge,
                                                    fSegmentThickness); }
  }

  public final Paint getSegmentPaint()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.segmentPaint(m_edge); }
  }

  public final void setSegmentPaint(final Paint paint)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_edgeDetails.overrideSegmentPaint(m_edge, paint); }
  }

  public final double getSegmentDashLength()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.segmentDashLength(m_edge); }
  }

  public final void setSegmentDashLength(final double segmentDashLength)
  {
    synchronized (m_fung.m_lock) {
      final float fSegmentDashLength = (float) segmentDashLength;
      if (!(fSegmentDashLength >= 0.0f)) {
        throw new IllegalArgumentException
          ("segmentDashLength must be positive or zero"); }
      m_fung.m_edgeDetails.overrideSegmentDashLength
        (m_edge, fSegmentDashLength); }
  }

  public final int getLabelCount()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_edgeDetails.labelCount(m_edge); }
  }

  public final EdgeLabel getLabel(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_edgeDetails.m_labels.get(new Integer(m_edge));
      if (v == null) {
        throw new IndexOutOfBoundsException("no labels set on this edge"); }
      return (EdgeLabel) v.get(inx); }
  }

  public final void addLabel(final EdgeLabel label)
  {
    if (label == null) { throw new NullPointerException("label is null"); }
    synchronized (m_fung.m_lock) {
      Vector v =
        (Vector) m_fung.m_edgeDetails.m_labels.get(new Integer(m_edge));
      boolean newVec = false;
      if (v == null) { v = new Vector(); newVec = true; }
      v.add(label);
      if (newVec) {
        m_fung.m_edgeDetails.m_labels.put(new Integer(m_edge), v); } }
  }

  public final EdgeLabel removeLabel(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_edgeDetails.m_labels.get(new Integer(m_edge));
      if (v == null) {
        throw new IndexOutOfBoundsException("no labels set on this edge"); }
      final EdgeLabel returnThis = (EdgeLabel) v.remove(inx);
      if (v.size() == 0) {
        m_fung.m_edgeDetails.m_labels.remove(new Integer(m_edge)); }
      return returnThis; }
  }

  public final int getAnchorCount()
  {
    synchronized (m_fung.m_lock) {
      final Object v = m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      if (v == null) { return 0; }
      return ((Vector) v).size(); }
  }

  public final void getAnchor(final int inx, final double[] p)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      if (v == null) {
        throw new IndexOutOfBoundsException("no anchors set on this edge"); }
      final Point2D pt = (Point2D) v.get(inx);
      p[0] = pt.getX(); p[1] = pt.getY(); }
  }

  public final void addAnchor(final int inx, final double x, final double y)
  {
    synchronized (m_fung.m_lock) {
      Vector v =
        (Vector) m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      boolean newVec = false;
      if (v == null) { v = new Vector(); newVec = true; }
      v.add(inx, new Point2D.Float((float) x, (float) y));
      if (newVec) {
        m_fung.m_edgeDetails.m_anchors.put(new Integer(m_edge), v); } }
  }

  public final void removeAnchor(final int inx)
  {
  }

  /**
   * @return false if anchors define curved edge segments, true if anchors
   *   define straight line segments.
   */
  public final boolean getAnchorType()
  {
    synchronized (m_fung.m_lock) {
      final int i = m_fung.m_edgeDetails.m_anchorTypes.getIntAtIndex(m_edge);
      if (i == 0) { return false; }
      return true; }
  }

  /**
   * @param straightLineSegments if false then anchors define curved line
   *   segments; if true then anchors define straight line segments.
   */
  public final void setAnchorType(final boolean straightLineSegments)
  {
    synchronized (m_fung.m_lock) {
      final int i = (straightLineSegments ? 1 : 0);
      m_fung.m_edgeDetails.m_anchorTypes.setIntAtIndex(i, m_edge); }
  }

}

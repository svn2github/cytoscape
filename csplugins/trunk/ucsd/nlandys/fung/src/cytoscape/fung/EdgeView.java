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
  private Color m_colorLowDetail;
  private Color m_selectedColorLowDetail;
  private Paint m_segmentPaint;
  private Paint m_selectedSegmentPaint;

  /*
   * People calling this constructor shall be holding m_fung.m_lock.
   */
  EdgeView(final Fung fung, final int edge)
  {
    m_fung = fung;
    m_edge = edge;
    final EdgeViewDefaults edgeDefaults;
    if (m_fung.m_graphModel.edgeType(m_edge) == 1) { // Directed.
      edgeDefaults = m_fung.m_directedEdgeDefaults; }
    else { // Undirected.
      edgeDefaults = m_fung.m_undirectedEdgeDefaults; }
    m_colorLowDetail = edgeDefaults.m_colorLowDetail;
    m_selectedColorLowDetail = edgeDefaults.m_selectedColorLowDetail;
    m_segmentPaint = edgeDefaults.m_segmentPaint;
    m_selectedSegmentPaint = edgeDefaults.m_selectedSegmentPaint;
  }

  public final int getEdge()
  {
    return m_edge;
  }

  public final Color getColorLowDetail()
  {
    return m_colorLowDetail;
  }

  public final void setColorLowDetail(final Color colorLowDetail)
  {
    synchronized (m_fung.m_lock) {
      if (colorLowDetail == null) {
        m_colorLowDetail =
          ((m_fung.m_graphModel.edgeType(m_edge) == 1) ?
           m_fung.m_directedEdgeDefaults.m_colorLowDetail :
           m_fung.m_undirectedEdgeDefaults.m_colorLowDetail); }
      else {
        m_colorLowDetail = colorLowDetail; }
      if (!isSelected()) {
        m_fung.m_edgeDetails.overrideColorLowDetail
          (m_edge, m_colorLowDetail); } }
  }

  public final Color getSelectedColorLowDetail()
  {
    return m_selectedColorLowDetail;
  }

  public final void setSelectedColorLowDetail(
                                            final Color selectedColorLowDetail)
  {
    synchronized (m_fung.m_lock) {
      if (selectedColorLowDetail == null) {
        m_selectedColorLowDetail =
          ((m_fung.m_graphModel.edgeType(m_edge) == 1) ?
           m_fung.m_directedEdgeDefaults.m_selectedColorLowDetail :
           m_fung.m_undirectedEdgeDefaults.m_selectedColorLowDetail); }
      else {
        m_selectedColorLowDetail = selectedColorLowDetail; }
      if (isSelected()) {
        m_fung.m_edgeDetails.overrideColorLowDetail
          (m_edge, m_selectedColorLowDetail); } }
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
    return m_segmentPaint;
  }

  public final void setSegmentPaint(final Paint segmentPaint)
  {
    synchronized (m_fung.m_lock) {
      if (segmentPaint == null) {
        m_segmentPaint =
          ((m_fung.m_graphModel.edgeType(m_edge) == 1) ?
           m_fung.m_directedEdgeDefaults.m_segmentPaint :
           m_fung.m_undirectedEdgeDefaults.m_segmentPaint); }
      else {
        m_segmentPaint = segmentPaint; }
      if (!isSelected()) {
        m_fung.m_edgeDetails.overrideSegmentPaint(m_edge, m_segmentPaint); } }
  }

  public final Paint getSelectedSegmentPaint()
  {
    return m_selectedSegmentPaint;
  }

  public final void setSelectedSegmentPaint(final Paint selectedSegmentPaint)
  {
    synchronized (m_fung.m_lock) {
      if (selectedSegmentPaint == null) {
        m_selectedSegmentPaint =
          ((m_fung.m_graphModel.edgeType(m_edge) == 1) ?
           m_fung.m_directedEdgeDefaults.m_selectedSegmentPaint :
           m_fung.m_undirectedEdgeDefaults.m_selectedSegmentPaint); }
      else {
        m_selectedSegmentPaint = selectedSegmentPaint; }
      if (isSelected()) {
        m_fung.m_edgeDetails.overrideSegmentPaint
          (m_edge, m_selectedSegmentPaint); } }
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

  public final double getAnchorXPosition(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      if (v == null) {
        throw new IndexOutOfBoundsException("no anchors set on this edge"); }
      final Point2D pt = (Point2D) v.get(inx);
      return pt.getX(); }
  }

  public final double getAnchorYPosition(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      if (v == null) {
        throw new IndexOutOfBoundsException("no anchors set on this edge"); }
      final Point2D pt = (Point2D) v.get(inx);
      return pt.getY(); }
  }

  public final void addAnchor(final int inx, final double x, final double y)
  {
    synchronized (m_fung.m_lock) {
      Vector v =
        (Vector) m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      boolean newVec = false;
      if (v == null) { v = new Vector(); newVec = true; }
      if (v.size() == GraphGraphics.MAX_EDGE_ANCHORS / 2) {
        throw new IllegalStateException("too many anchors already on edge"); }
      v.add(inx, new Point2D.Float((float) x, (float) y));
      if (newVec) {
        m_fung.m_edgeDetails.m_anchors.put(new Integer(m_edge), v); } }
  }

  public final int addAnchor(final double x, final double y)
  {
    synchronized (m_fung.m_lock) {
      if (getAnchorCount() == 0) {
        addAnchor(0, x, y);
        return 0; }
      final Point2D newPt = new Point2D.Double(x, y);
      final NodeView srcNode =
        m_fung.getNodeView(m_fung.getGraphModel().edgeSource(m_edge));
      final Point2D srcLoc = new Point2D.Double
        (srcNode.getXPosition(), srcNode.getYPosition());
      final NodeView trgNode =
        m_fung.getNodeView(m_fung.getGraphModel().edgeTarget(m_edge));
      final Point2D trgLoc = new Point2D.Double
        (trgNode.getXPosition(), trgNode.getYPosition());
      final Vector anchors = (Vector)
        m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      double bestDist =
        newPt.distance(srcLoc) + newPt.distance((Point2D) anchors.get(0)) -
        srcLoc.distance((Point2D) anchors.get(0));
      int bestInx = 0;
      for (int i = 1; i < anchors.size(); i++) {
        final double distCand =
          newPt.distance((Point2D) anchors.get(i - 1)) +
          newPt.distance((Point2D) anchors.get(i)) -
          ((Point2D) anchors.get(i)).distance
          ((Point2D) anchors.get(i - 1));
        if (distCand < bestDist) {
          bestDist = distCand;
          bestInx = i; } }
      final double lastCand =
        newPt.distance(trgLoc) +
        newPt.distance((Point2D) anchors.get(anchors.size() - 1)) -
        trgLoc.distance((Point2D) anchors.get(anchors.size() - 1));
      if (lastCand < bestDist) {
        bestDist = lastCand;
        bestInx = anchors.size(); }
      addAnchor(bestInx, x, y);
      return bestInx; }
  }

  public final void removeAnchor(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_edgeDetails.m_anchors.get(new Integer(m_edge));
      if (v == null) {
        throw new IndexOutOfBoundsException("no anchors set on this edge"); }
      v.remove(inx);
      if (v.size() == 0) {
        m_fung.m_edgeDetails.m_anchors.remove(new Integer(m_edge)); } }
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

  public boolean isSelected()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_selectedEdges.count(m_edge) > 0; }
  }

}

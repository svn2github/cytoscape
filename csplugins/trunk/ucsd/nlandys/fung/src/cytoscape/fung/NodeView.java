package cytoscape.fung;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Dimension2D;
import java.util.Vector;

public final class NodeView
{

  public final static byte SHAPE_RECTANGLE = GraphGraphics.SHAPE_RECTANGLE;
  public final static byte SHAPE_DIAMOND = GraphGraphics.SHAPE_DIAMOND;
  public final static byte SHAPE_ELLIPSE = GraphGraphics.SHAPE_ELLIPSE;
  public final static byte SHAPE_HEXAGON = GraphGraphics.SHAPE_HEXAGON;
  public final static byte SHAPE_OCTAGON = GraphGraphics.SHAPE_OCTAGON;
  public final static byte SHAPE_PARALLELOGRAM =
    GraphGraphics.SHAPE_PARALLELOGRAM;
  public final static byte SHAPE_ROUNDED_RECTANGLE =
    GraphGraphics.SHAPE_ROUNDED_RECTANGLE;
  public final static byte SHAPE_TRIANGLE =
    GraphGraphics.SHAPE_TRIANGLE;

  Fung m_fung; // Not final so that we can destroy reference.
  private final int m_node;
  private Color m_colorLowDetail;
  private Color m_selectedColorLowDetail;
  private Paint m_fillPaint;
  private Paint m_selectedFillPaint;

  /*
   * People calling this constructor shall be holding m_fung.m_lock.
   */
  NodeView(final Fung fung, final int node)
  {
    m_fung = fung;
    m_node = node;
    m_colorLowDetail = m_fung.m_nodeDefaults.m_colorLowDetail;
    m_selectedColorLowDetail = m_fung.m_nodeDefaults.m_selectedColorLowDetail;
    m_fillPaint = m_fung.m_nodeDefaults.m_fillPaint;
    m_selectedFillPaint = m_fung.m_nodeDefaults.m_selectedFillPaint;
  }

  public final int getNode()
  {
    return m_node;
  }

  public final double getXPosition()
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      return (((double) m_fung.m_extentsBuff[0]) +
              m_fung.m_extentsBuff[2]) / 2.0d; }
  }

  public final double getYPosition()
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      return (((double) m_fung.m_extentsBuff[1]) +
              m_fung.m_extentsBuff[3]) / 2.0d; }
  }

  public final void setLocation(final double x, final double y)
  {
    // As a node changes location, its width and height may change as a
    // result of floating point imprecision.  However, we're not going to
    // error check this shifting size against border width.
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      final double wDiv2 =
        (((double) m_fung.m_extentsBuff[2]) - m_fung.m_extentsBuff[0]) / 2.0d;
      final double hDiv2 =
        (((double) m_fung.m_extentsBuff[3]) - m_fung.m_extentsBuff[1]) / 2.0d;
      final float xMin = (float) (x - wDiv2);
      final float yMin = (float) (y - hDiv2);
      final float xMax = (float) (x + wDiv2);
      final float yMax = (float) (y + hDiv2);
      if (!(xMax > xMin)) {
        throw new IllegalStateException
          ("width of node has degenerated to zero after rounding"); }
      if (!(yMax > yMin)) {
        throw new IllegalStateException
          ("height of node has degenerated to zero after rounding"); }
      m_fung.m_rtree.delete(m_node);
      m_fung.m_rtree.insert(m_node, xMin, yMin, xMax, yMax); }
  }

  public final double getWidth()
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      return ((double) m_fung.m_extentsBuff[2]) - m_fung.m_extentsBuff[0]; }
  }

  public final double getHeight()
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      return ((double) m_fung.m_extentsBuff[3]) - m_fung.m_extentsBuff[1]; }
  }

  public final void setSize(final double width, final double height)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_rtree.exists(m_node, m_fung.m_extentsBuff, 0);
      final double xCenter =
        (((double) m_fung.m_extentsBuff[0]) + m_fung.m_extentsBuff[2]) / 2.0d;
      final double yCenter =
        (((double) m_fung.m_extentsBuff[1]) + m_fung.m_extentsBuff[3]) / 2.0d;
      final double wDiv2 = width / 2.0d;
      final double hDiv2 = height / 2.0d;
      final float xMin = (float) (xCenter - wDiv2);
      final float yMin = (float) (yCenter - hDiv2);
      final float xMax = (float) (xCenter + wDiv2);
      final float yMax = (float) (yCenter + hDiv2);
      if (!(xMax > xMin)) {
        throw new IllegalArgumentException("width is too small"); }
      if (!(yMax > yMin)) {
        throw new IllegalArgumentException("height is too small"); }
      m_fung.m_rtree.delete(m_node);
      m_fung.m_rtree.insert(m_node, xMin, yMin, xMax, yMax);
      { // Reconcile node shape if rounded rectangle.
        final byte shape = m_fung.m_nodeDetails.shape(m_node);
        if (shape == SHAPE_ROUNDED_RECTANGLE) {
          if (!(Math.max(((double) xMax) - xMin, ((double) yMax) - yMin) <
                2.0d * Math.min(((double) xMax) - xMin,
                                ((double) yMax) - yMin))) {
            m_fung.m_nodeDetails.overrideShape(m_node, SHAPE_RECTANGLE); } }
      }
      { // Reconcile border width.
        final float borderWidth = m_fung.m_nodeDetails.borderWidth(m_node);
        final double borderWidthConstraint =
          Math.min(((double) xMax) - xMin, ((double) yMax) - yMin) / 6.0d;
        if (borderWidth > borderWidthConstraint) {
          m_fung.m_nodeDetails.overrideBorderWidth
            (m_node, (float) borderWidthConstraint); }
      } }
  }

  public final Color getColorLowDetail()
  {
    return m_colorLowDetail;
  }

  public final void setColorLowDetail(final Color colorLowDetail)
  {
    synchronized (m_fung.m_lock) {
      if (colorLowDetail == null) {
        m_colorLowDetail = m_fung.m_nodeDefaults.m_colorLowDetail; }
      else {
        if (colorLowDetail.getAlpha() != 255) {
          throw new IllegalArgumentException
            ("colorLowDetail must be opaque"); }
        m_colorLowDetail = colorLowDetail; }
      if (!isSelected()) {
        m_fung.m_nodeDetails.overrideColorLowDetail
          (m_node, m_colorLowDetail); } }
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
          m_fung.m_nodeDefaults.m_selectedColorLowDetail; }
      else {
        if (selectedColorLowDetail.getAlpha() != 255) {
          throw new IllegalArgumentException
            ("selectedColorLowDetail must be opaque"); }
        m_selectedColorLowDetail = selectedColorLowDetail; }
      if (isSelected()) {
        m_fung.m_nodeDetails.overrideColorLowDetail
          (m_node, m_selectedColorLowDetail); } }
  }

  public final byte getShape()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.shape(m_node); }
  }

  public final void setShape(byte shape)
  {
    synchronized (m_fung.m_lock) {
      switch (shape) {
      case SHAPE_RECTANGLE:
      case SHAPE_DIAMOND:
      case SHAPE_ELLIPSE:
      case SHAPE_HEXAGON:
      case SHAPE_OCTAGON:
      case SHAPE_PARALLELOGRAM:
      case SHAPE_ROUNDED_RECTANGLE:
      case SHAPE_TRIANGLE:
        break;
      default:
        if (!m_fung.customNodeShapeExists(shape))
          throw new IllegalArgumentException("shape is not recognized"); }
      { // Reconcile node shape if rounded rectangle.
        if (shape == SHAPE_ROUNDED_RECTANGLE) {
          final double w = getWidth();
          final double h = getHeight();
          if (!(Math.max(w, h) < 2.0d * Math.min(w, h))) {
            shape = SHAPE_RECTANGLE; } }
      }
      m_fung.m_nodeDetails.overrideShape(m_node, shape); }
  }

  public final Paint getFillPaint()
  {
    return m_fillPaint;
  }

  public final void setFillPaint(final Paint fillPaint)
  {
    synchronized (m_fung.m_lock) {
      if (fillPaint == null) {
        m_fillPaint = m_fung.m_nodeDefaults.m_fillPaint; }
      else {
        m_fillPaint = fillPaint; }
      if (!isSelected()) {
        m_fung.m_nodeDetails.overrideFillPaint(m_node, m_fillPaint); } }
  }

  public final Paint getSelectedFillPaint()
  {
    return m_selectedFillPaint;
  }

  public final void setSelectedFillPaint(final Paint selectedFillPaint)
  {
    synchronized (m_fung.m_lock) {
      if (selectedFillPaint == null) {
        m_selectedFillPaint = m_fung.m_nodeDefaults.m_selectedFillPaint; }
      else {
        m_selectedFillPaint = selectedFillPaint; }
      if (isSelected()) {
        m_fung.m_nodeDetails.overrideFillPaint(m_node,
                                               m_selectedFillPaint); } }
  }

  public final double getBorderWidth()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.borderWidth(m_node); }
  }

  public final void setBorderWidth(final double borderWidth)
  {
    synchronized (m_fung.m_lock) {
      float fBorderWidth = (float) borderWidth;
      if (!(fBorderWidth >= 0.0f)) {
        throw new IllegalArgumentException
          ("borderWidth must be positive or zero"); }
      { // Reconcile border width with size.
        final double w = getWidth();
        final double h = getHeight();
        final double borderWidthConstraint = Math.min(w, h) / 6.0d;
        if (borderWidthConstraint < (double) fBorderWidth) {
          fBorderWidth = (float) borderWidthConstraint; }
      }
      m_fung.m_nodeDetails.overrideBorderWidth(m_node, fBorderWidth); }
  }

  public final Paint getBorderPaint()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.borderPaint(m_node); }
  }

  public final void setBorderPaint(final Paint borderPaint)
  {
    synchronized (m_fung.m_lock) {
      m_fung.m_nodeDetails.overrideBorderPaint(m_node, borderPaint); }
  }

  public final int getLabelCount()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_nodeDetails.labelCount(m_node); }
  }

  public final NodeLabel getLabel(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_nodeDetails.m_labels.get(new Integer(m_node));
      if (v == null) {
        throw new IndexOutOfBoundsException("no labels set on this node"); }
      return (NodeLabel) v.get(inx); }
  }

  public final void addLabel(final NodeLabel label)
  {
    if (label == null) { throw new NullPointerException("label is null"); }
    synchronized (m_fung.m_lock) {
      Vector v =
        (Vector) m_fung.m_nodeDetails.m_labels.get(new Integer(m_node));
      boolean newVec = false;
      if (v == null) { v = new Vector(); newVec = true; }
      v.add(label);
      if (newVec) {
        m_fung.m_nodeDetails.m_labels.put(new Integer(m_node), v); } }
  }

  public final NodeLabel removeLabel(final int inx)
  {
    synchronized (m_fung.m_lock) {
      final Vector v =
        (Vector) m_fung.m_nodeDetails.m_labels.get(new Integer(m_node));
      if (v == null) {
        throw new IndexOutOfBoundsException("no labels set on this node"); }
      final NodeLabel returnThis = (NodeLabel) v.remove(inx);
      if (v.size() == 0) {
        m_fung.m_nodeDetails.m_labels.remove(new Integer(m_node)); }
      return returnThis; }
  }

  public final boolean isSelected()
  {
    synchronized (m_fung.m_lock) {
      return m_fung.m_selectedNodes.count(m_node) > 0; }
  }

  /**
   * @return true if this operation was successful; false if nothing
   *   has been changed.
   */
  public final boolean setSelected(final boolean selected)
  {
    if (selected) {
      SelectionListener lis;
      synchronized (m_fung.m_lock) {
        if (!select()) { return false; }
        lis = m_fung.m_selLis; }
      if (lis != null) { lis.nodeSelected(m_node); }
      return true; }
    else {
      SelectionListener lis;
      synchronized (m_fung.m_lock) {
        if (!unselect()) { return false; }
        lis = m_fung.m_selLis; }
      if (lis != null) { lis.nodeUnselected(m_node); }
      return true; }
  }

  /*
   * Returns true if this operation was successful, false if this node view
   * was already selected.  Callers should synchronize around m_fung.m_lock.
   * Also, appropriate events should be fired by callers.
   */
  final boolean select()
  {
    if (m_fung.m_selectedNodes.count(m_node) > 0) { return false; }
    m_fung.m_selectedNodes.insert(m_node);
    m_fung.m_nodeDetails.overrideColorLowDetail
      (m_node, m_selectedColorLowDetail);
    m_fung.m_nodeDetails.overrideFillPaint(m_node, m_selectedFillPaint);
    return true;
  }

  /*
   * Returns true if this operation was successful, false if this node view
   * was already unselected.  Callers should synchronize around m_fung.m_lock.
   * Also, appropriate events should be fired by callers.
   */
  final boolean unselect()
  {
    if (m_fung.m_selectedNodes.count(m_node) == 0) { return false; }
    m_fung.m_selectedNodes.delete(m_node);
    m_fung.m_nodeDetails.overrideColorLowDetail(m_node, m_colorLowDetail);
    m_fung.m_nodeDetails.overrideFillPaint(m_node, m_fillPaint);
    return true;
  }

}

package ding.view;

import cytoscape.render.immed.GraphGraphics;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.Label;
import giny.view.NodeView;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.List;

class DNodeView implements NodeView, Label
{

  DGraphView m_view;
  final int m_inx;
  boolean m_selected;
  Paint m_unselectedPaint;
  Paint m_selectedPaint;
  Paint m_textPaint;
  Font m_textFont;

  /*
   * @param inx the RootGraph index of node (a negative number).
   */
  DNodeView(DGraphView view, int inx)
  {
    m_view = view;
    m_inx = ~inx;
    m_selected = false;
    m_unselectedPaint = m_view.m_nodeDetails.fillPaint(m_inx);
    m_selectedPaint = Color.yellow;
    m_textPaint = Color.black;
    m_textFont = m_view.m_defaultFont;
  }

  public GraphView getGraphView()
  {
    return m_view;
  }

  public Node getNode()
  {
    return null;
  }

  public int getGraphPerspectiveIndex()
  {
    return ~m_inx;
  }

  public int getRootGraphIndex()
  {
    return ~m_inx;
  }

  public List getEdgeViewsList(NodeView otherNode)
  {
    return null;
  }

  public int getShape()
  {
    synchronized (m_view.m_lock) {
      final byte nativeShape = m_view.m_nodeDetails.shape(m_inx);
      switch (nativeShape) {
      case GraphGraphics.SHAPE_RECTANGLE:
        return NodeView.RECTANGLE;
      case GraphGraphics.SHAPE_DIAMOND:
        return NodeView.DIAMOND;
      case GraphGraphics.SHAPE_ELLIPSE:
        return NodeView.ELLIPSE;
      case GraphGraphics.SHAPE_HEXAGON:
        return NodeView.HEXAGON;
      case GraphGraphics.SHAPE_OCTAGON:
        return NodeView.OCTAGON;
      case GraphGraphics.SHAPE_PARALLELOGRAM:
        return NodeView.PARALELLOGRAM;
      case GraphGraphics.SHAPE_ROUNDED_RECTANGLE:
        return NodeView.ROUNDED_RECTANGLE;
      default: // GraphGraphics.SHAPE_TRIANGLE
        return NodeView.TRIANGLE; } }
  }

  public void setSelectedPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_selectedPaint = paint;
      if (isSelected()) {
        m_view.m_nodeDetails.overrideFillPaint(m_inx, m_selectedPaint);
        if (m_selectedPaint instanceof Color) {
          m_view.m_nodeDetails.overrideColorLowDetail
            (m_inx, (Color) m_selectedPaint); } } }
  }

  public Paint getSelectedPaint()
  {
    return m_selectedPaint;
  }

  public void setUnselectedPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_unselectedPaint = paint;
      if (!isSelected()) {
        m_view.m_nodeDetails.overrideFillPaint(m_inx, m_unselectedPaint);
        if (m_unselectedPaint instanceof Color) {
          m_view.m_nodeDetails.overrideColorLowDetail
            (m_inx, (Color) m_unselectedPaint); } } }
  }

  public Paint getUnselectedPaint()
  {
    return m_unselectedPaint;
  }

  public void setBorderPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_view.m_nodeDetails.overrideBorderPaint(m_inx, paint); }
  }

  public Paint getBorderPaint()
  {
    synchronized (m_view.m_lock) {
      return m_view.m_nodeDetails.borderPaint(m_inx); }
  }

  public void setBorderWidth(float width)
  {
    synchronized (m_view.m_lock) {
      if (!(width >= 0)) { throw new IllegalArgumentException
                             ("width is negative"); }
      m_view.m_nodeDetails.overrideBorderWidth(m_inx, width); }
  }

  public float getBorderWidth()
  {
    synchronized (m_view.m_lock) {
      return m_view.m_nodeDetails.borderWidth(m_inx); }
  }

  public void setBorder(Stroke stroke)
  {
    if (stroke instanceof BasicStroke) {
      setBorderWidth(((BasicStroke) stroke).getLineWidth()); }
  }

  public Stroke getBorder()
  {
    return new BasicStroke(getBorderWidth());
  }

  public void setTransparency(float trans)
  {
  }

  public float getTransparency()
  {
    return 1.0f;
  }

  public boolean setWidth(double width)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return false; }
      final double xCenter =
        (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
      final double wDiv2 = width / 2.0d;
      final float xMin = (float) (xCenter - wDiv2);
      final float xMax = (float) (xCenter + wDiv2);
      if (!(xMax > xMin))
        throw new IllegalArgumentException("width is too small");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, xMin, m_view.m_extentsBuff[1],
                              xMax, m_view.m_extentsBuff[3]);
      return true; }
  }

  public double getWidth()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return -1.0d; }
      return m_view.m_extentsBuff[2] - m_view.m_extentsBuff[0]; }
  }

  public boolean setHeight(double height)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return false; }
      final double yCenter =
        (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;
      final double hDiv2 = height / 2.0d;
      final float yMin = (float) (yCenter - hDiv2);
      final float yMax = (float) (yCenter + hDiv2);
      if (!(yMax > yMin))
        throw new IllegalArgumentException("height is too small");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, m_view.m_extentsBuff[0], yMin,
                              m_view.m_extentsBuff[2], yMax);
      return true; }
  }

  public double getHeight()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return -1.0d; }
      return m_view.m_extentsBuff[3] - m_view.m_extentsBuff[1]; }
  }

  public Label getLabel()
  {
    return this;
  }

  public int getDegree()
  {
    return 0;
  }

  public void setOffset(double x, double y)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return; }
      final double wDiv2 =
        (((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0]) / 2.0d;
      final double hDiv2 =
        (((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1]) / 2.0d;
      final float xMin = (float) (x - wDiv2);
      final float xMax = (float) (x + wDiv2);
      final float yMin = (float) (y - hDiv2);
      final float yMax = (float) (y + hDiv2);
      if (!(xMax > xMin)) throw new IllegalStateException
                            ("width of node has degenerated to zero after " +
                             "rounding");
      if (!(yMax > yMin)) throw new IllegalStateException
                            ("height of node has degenerated to zero after " +
                             "rounding");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, xMin, yMin, xMax, yMax); }
  }

  public Point2D getOffset()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return null; }
      final double xCenter =
        (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
      final double yCenter =
        (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;
      return new Point2D.Double(xCenter, yCenter); }
  }

  public void setXPosition(double xPos)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return; }
      final double wDiv2 =
        (((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0]) / 2.0d;
      final float xMin = (float) (xPos - wDiv2);
      final float xMax = (float) (xPos + wDiv2);
      if (!(xMax > xMin)) throw new IllegalStateException
                            ("width of node has degenerated to zero after " +
                             "rounding");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, xMin, m_view.m_extentsBuff[1],
                              xMax, m_view.m_extentsBuff[3]); }
  }

  public void setXPosition(double xPos, boolean update)
  {
    setXPosition(xPos);
    if (update) { m_view.updateView(); }
  }

  public double getXPosition()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return Double.NaN; }
      return (((double) m_view.m_extentsBuff[0]) +
              m_view.m_extentsBuff[2]) / 2.0d; }
  }

  public void setYPosition(double yPos)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return; }
      final double hDiv2 =
        (((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1]) / 2.0d;
      final float yMin = (float) (yPos - hDiv2);
      final float yMax = (float) (yPos + hDiv2);
      if (!(yMax > yMin)) throw new IllegalStateException
                            ("height of node has degenerated to zero after " +
                             "rounding");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, m_view.m_extentsBuff[1], yMin,
                              m_view.m_extentsBuff[3], yMax); }
  }

  public void setYPosition(double yPos, boolean update)
  {
    setYPosition(yPos);
    if (update) { m_view.updateView(); }
  }

  public double getYPosition()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return Double.NaN; }
      return (((double) m_view.m_extentsBuff[1]) +
              m_view.m_extentsBuff[3]) / 2.0d; }
  }

  public void setNodePosition(boolean animate)
  {
    m_view.updateView();
  }

  public void select()
  {
    synchronized (m_view.m_lock) {
      if (m_selected) { return; }
      m_selected = true;
      m_view.m_nodeDetails.overrideFillPaint(m_inx, m_selectedPaint);
      if (m_selectedPaint instanceof Color) {
        m_view.m_nodeDetails.overrideColorLowDetail
          (m_inx, (Color) m_selectedPaint); } }
    }

  public void unselect()
  {
    synchronized (m_view.m_lock) {
      if (!m_selected) { return; }
      m_selected = false;
      m_view.m_nodeDetails.overrideFillPaint(m_inx, m_unselectedPaint);
      if (m_unselectedPaint instanceof Color) {
        m_view.m_nodeDetails.overrideColorLowDetail
          (m_inx, (Color) m_unselectedPaint); } }
  }

  public boolean isSelected()
  {
    return m_selected;
  }

  public boolean setSelected(boolean selected)
  {
    synchronized (m_view.m_lock) {
      if (selected) {
        if (m_selected) { return false; }
        select(); return true; }
      else {
        if (!m_selected) { return false; }
        unselect(); return true; } }
  }

  public void setShape(final int shape)
  {
    synchronized (m_view.m_lock) {
      final byte nativeShape;
      switch (shape) {
      case NodeView.TRIANGLE:
        nativeShape = GraphGraphics.SHAPE_TRIANGLE; break;
      case NodeView.DIAMOND:
        nativeShape = GraphGraphics.SHAPE_DIAMOND; break;
      case NodeView.ELLIPSE:
        nativeShape = GraphGraphics.SHAPE_ELLIPSE; break;
      case NodeView.HEXAGON:
        nativeShape = GraphGraphics.SHAPE_HEXAGON; break;
      case NodeView.OCTAGON:
        nativeShape = GraphGraphics.SHAPE_OCTAGON; break;
      case NodeView.PARALELLOGRAM:
        nativeShape = GraphGraphics.SHAPE_PARALLELOGRAM; break;
      case NodeView.RECTANGLE:
        nativeShape = GraphGraphics.SHAPE_RECTANGLE; break;
      case NodeView.ROUNDED_RECTANGLE:
        nativeShape = GraphGraphics.SHAPE_ROUNDED_RECTANGLE; break;
      default:
        throw new IllegalArgumentException("shape is not recognized"); }
      m_view.m_nodeDetails.overrideShape(m_inx, nativeShape); }
  }

  public void setToolTip(String tip)
  {
  }


  // Interface giny.view.Label:

  public void setPositionHint(int position)
  {
  }

  public Paint getTextPaint()
  {
    return m_textPaint;
  }

  public void setTextPaint(Paint textPaint)
  {
    synchronized (m_view.m_lock) {
      if (textPaint == null) {
        throw new NullPointerException("textPaint is null"); }
      m_textPaint = textPaint;
      if (m_view.m_nodeDetails.labelCount(m_inx) > 0) {
        m_view.m_nodeDetails.overrideLabelPaint(m_inx, 0, m_textPaint); } }
  }

  public double getGreekThreshold()
  {
    return 0.0d;
  }

  public void setGreekThreshold(double threshold)
  {
  }

  public String getText()
  {
    return null;
  }

  public void setText(String text)
  {
  }

  public Font getFont()
  {
    return m_textFont;
  }

  public void setFont(Font font)
  {
    synchronized (m_view.m_lock) {
      if (font == null) {
        throw new NullPointerException("font is null"); }
      m_textFont = font;
      if (m_view.m_nodeDetails.labelCount(m_inx) > 0) {
        m_view.m_nodeDetails.overrideLabelFont(m_inx, 0, m_textFont); } }
  }

}

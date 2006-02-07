package cytoscape.fung;

import java.awt.Color;
import java.awt.Paint;

public final class NodeViewDefaults
{

  public final static double DEFAULT_WIDTH = 10.0d;
  public final static double DEFAULT_HEIGHT = 10.0d;
  public final static Color DEFAULT_COLOR_LOW_DETAIL = Color.red;
  public final static byte DEFAULT_SHAPE = NodeView.SHAPE_ELLIPSE;
  public final static Paint DEFAULT_FILL_PAINT = Color.red;
  public final static double DEFAULT_BORDER_WIDTH = 1.0d;
  public final static Paint DEFAULT_BORDER_PAINT = Color.black;

  final float m_widthDiv2;
  final float m_heightDiv2;
  final Color m_colorLowDetail;
  final byte m_shape;
  final Paint m_fillPaint;
  final double m_borderWidth;
  final Paint m_borderPaint;

  public NodeViewDefaults()
  {
    this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_COLOR_LOW_DETAIL,
         DEFAULT_SHAPE, DEFAULT_FILL_PAINT, DEFAULT_BORDER_WIDTH,
         DEFAULT_BORDER_PAINT);
  }

  public NodeViewDefaults(final double width,
                          final double height,
                          final Color colorLowDetail,
                          final byte shape,
                          final Paint fillPaint,
                          final double borderWidth,
                          final Paint borderPaint)
  {
    m_widthDiv2 = (float) (width / 2.0d);
    if (!(m_widthDiv2 > 0.0f)) {
      throw new IllegalArgumentException("width is too small"); }
    m_heightDiv2 = (float) (height / 2.0d);
    if (!(m_heightDiv2 > 0.0f)) {
      throw new IllegalArgumentException("height is too small"); }
    m_colorLowDetail = colorLowDetail;
    if (m_colorLowDetail == null) {
      throw new NullPointerException("colorLowDetail is null"); }
    m_shape = shape;
    switch (m_shape) {
    case NodeView.SHAPE_RECTANGLE:
    case NodeView.SHAPE_DIAMOND:
    case NodeView.SHAPE_ELLIPSE:
    case NodeView.SHAPE_HEXAGON:
    case NodeView.SHAPE_OCTAGON:
    case NodeView.SHAPE_PARALLELOGRAM:
    case NodeView.SHAPE_ROUNDED_RECTANGLE:
    case NodeView.SHAPE_TRIANGLE:
      break;
    default:
      throw new IllegalArgumentException("shape is unrecognized"); }
    if (m_shape == NodeView.SHAPE_ROUNDED_RECTANGLE) {
      if (!(Math.max(m_widthDiv2, m_heightDiv2) <
            2.0d * Math.min(m_widthDiv2, m_heightDiv2))) {
        throw new IllegalStateException
          ("rounded rectangle is too long"); } }
    m_fillPaint = fillPaint;
    if (m_fillPaint == null) {
      throw new NullPointerException("fillPaint is null"); }
    m_borderWidth = borderWidth;
    if (!(m_borderWidth >= 0.0d)) {
      throw new IllegalArgumentException("borderWidth is negative"); }
    if (!(m_borderWidth <= Math.min(m_widthDiv2, m_heightDiv2) / 3.0d)) {
      throw new IllegalArgumentException
        ("borderWidth is too small relative to node size"); }
    m_borderPaint = borderPaint;
    if (m_borderPaint == null) {
      throw new NullPointerException("borderPaint is null"); }
  }

  public final double getWidth()
  {
    return 2.0d * m_widthDiv2;
  }

  public final double getHeight()
  {
    return 2.0d * m_heightDiv2;
  }

  public final Color getColorLowDetail()
  {
    return m_colorLowDetail;
  }

  public final byte getShape()
  {
    return m_shape;
  }

  public final Paint getFillPaint()
  {
    return m_fillPaint;
  }

  public final double getBorderWidth()
  {
    return m_borderWidth;
  }

  public final Paint getBorderPaint()
  {
    return m_borderPaint;
  }

}

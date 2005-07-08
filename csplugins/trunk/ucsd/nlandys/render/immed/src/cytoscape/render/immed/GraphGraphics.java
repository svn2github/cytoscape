package cytoscape.render.immed;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * This is functional programming at it's finest [sarcasm].
 * The purpose of this class is to make the proper calls on a Graphics2D
 * object to efficiently render nodes, labels, and edges.
 */
public final class GraphGraphics
{

  public static final byte SHAPE_RECTANGLE = 0;
  public static final byte SHAPE_DIAMOND = 1;
  public static final byte SHAPE_ELLIPSE = 2;
  public static final byte SHAPE_HEXAGON = 3;
  public static final byte SHAPE_OCTAGON = 4;
  public static final byte SHAPE_PARALLELOGRAM = 5;
  public static final byte SHAPE_TRIANGLE = 6;

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private final Color m_bgColor;
  private final boolean m_debug;
  private final Rectangle2D.Float m_rect2d;
  private final Rectangle2D.Float m_rect2d_;
  private final Ellipse2D.Float m_ellp2d;
  private final GeneralPath m_poly2d;
  private final Line2D.Float m_line2d;
  private final float[] m_dash;
  private Graphics2D m_g2d;
  private AffineTransform m_currXform; // Not sure that we will need this.
  private boolean m_antialias;
  private float m_currStrokeWidth;

  /**
   * All rendering operations will be performed on the specified image.
   * This constructor needs to be called from the AWT event handling thread.
   * @param image an off-screen image (an image that supports the
   *   getGraphics() method).
   * @param bgColor a color to use when clearing the image before painting
   *   a new frame; transparent colors are honored, provided that the image
   *   argument supports transparent colors.
   * @param debug if this is true, extra [and time-consuming] error checking
   *   will take place.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public GraphGraphics(final Image image, final Color bgColor,
                       final boolean debug)
  {
    this.image = image;
    m_bgColor = bgColor;
    m_debug = debug;
    m_rect2d = new Rectangle2D.Float();
    m_rect2d_ = new Rectangle2D.Float();
    m_ellp2d = new Ellipse2D.Float();
    m_poly2d = new GeneralPath();
    m_line2d = new Line2D.Float();
    m_dash = new float[] { 0.0f, 0.0f };
    clear(0.0d, 0.0d, 1.0d);
  }

  /**
   * Clears image area with the specified background color, and sets an
   * appropriate transformation of coordinate systems.
   * It is healthy to call this method right before starting
   * to render a new picture.  Don't try to be clever in not calling this
   * method.<p>
   * This method must be called from the AWT event handling thread.
   * @param xCenter the x component of the translation transform for the frame
   *   about to be rendered; a node whose center is at the X coordinate xCenter
   *   will be rendered exactly in the middle of the image going across.
   * @param yCenter the y component of the translation transform for the frame
   *   about to be rendered; a node whose center is at the Y coordinate yCenter
   *   will be rendered exactly in the middle of the image going top to bottom.
   * @param scaleFactor the scaling that is to take place when rendering nodes;
   *   a distance of 1 in node coordinates translates to a distance of
   *   scaleFactor in image coordinates.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if scaleFactor is not positive.
   */
  public final void clear(final double xCenter, final double yCenter,
                          final double scaleFactor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!(scaleFactor > 0.0d))
        throw new IllegalArgumentException("scaleFactor is not positive"); }
    m_g2d = (Graphics2D) image.getGraphics();
    final Composite origComposite = m_g2d.getComposite();
    m_g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    m_g2d.setBackground(m_bgColor);
    m_g2d.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
    m_g2d.setComposite(origComposite);
    setLowDetail();
    setStroke(0.0f, 0.0f);
    // Set transform.  This is an infrequently used method so don't optimize.
    final AffineTransform translationPreScale = new AffineTransform();
    translationPreScale.setToTranslation(-xCenter, -yCenter);
    final AffineTransform scale = new AffineTransform();
    scale.setToScale(scaleFactor, scaleFactor);
    final AffineTransform translationPostScale = new AffineTransform();
    translationPostScale.setToTranslation
      (0.5d * (double) image.getWidth(null),
       0.5d * (double) image.getHeight(null));
    final AffineTransform finalTransform = new AffineTransform();
    finalTransform.concatenate(translationPostScale);
    finalTransform.concatenate(scale);
    finalTransform.concatenate(translationPreScale);
    m_currXform = finalTransform;
    m_g2d.transform(m_currXform);
  }

  /**
   * The xMin, yMin, xMax, and yMax parameters specify the extents of the
   * node shape (in the node coordinate system), including the border
   * width.
   * @param borderWidth the border width, in node coordinate space; if
   *   this value is zero, the rendering engine skips over the process of
   *   rendering the border, which gives a significant performance boost.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if xMin is greater than xMax or if
   *   yMin is greater than yMax, or if borderWidth is negative,
   *   or if shapeType is not one of the SHAPE_* constants.
   */
  public final void drawNodeFull(final byte shapeType,
                                 final float xMin, final float yMin,
                                 final float xMax, final float yMax,
                                 final Color fillColor,
                                 final float borderWidth,
                                 final Color borderColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
      if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax");
      if (borderWidth < 0.0f)
        throw new IllegalArgumentException("borderWidth < 0"); }
    if (!m_antialias) setHighDetail();
    final Shape shape;
    switch (shapeType) {
    case SHAPE_RECTANGLE:
      m_rect2d.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
      shape = m_rect2d;
      break;
    case SHAPE_ELLIPSE:
      m_ellp2d.setFrame(xMin, yMin, xMax - xMin, yMax - yMin);
      shape = m_ellp2d;
      break;
    case SHAPE_DIAMOND:
      m_poly2d.reset();
      m_poly2d.moveTo((xMin + xMax) / 2.0f, yMin);
      m_poly2d.lineTo((yMin + yMax) / 2.0f, xMax);
      m_poly2d.lineTo((xMin + xMax) / 2.0f, yMax);
      m_poly2d.lineTo((yMin + yMax) / 2.0f, xMin);
      m_poly2d.closePath();
      shape = m_poly2d;
      break;
    case SHAPE_HEXAGON:
      m_poly2d.reset();
      m_poly2d.moveTo((2.0f * xMin + xMax) / 3.0f, yMin);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMin);
      m_poly2d.lineTo(xMax, (yMin + yMax) / 2.0f);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMax);
      m_poly2d.lineTo((2.0f * xMin + xMax) / 3.0f, yMax);
      m_poly2d.lineTo(xMin, (yMin + yMax) / 2.0f);
      m_poly2d.closePath();
      shape = m_poly2d;
      break;
    case SHAPE_OCTAGON:
      m_poly2d.reset();
      m_poly2d.moveTo((2.0f * xMin + xMax) / 3.0f, yMin);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMin);
      m_poly2d.lineTo(xMax, (2.0f * yMin + yMax) / 3.0f);
      m_poly2d.lineTo(xMax, (yMin + 2.0f * yMax) / 3.0f);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMax);
      m_poly2d.lineTo((2.0f * xMin + xMax) / 3.0f, yMax);
      m_poly2d.lineTo(xMin, (yMin + 2.0f * yMax) / 3.0f);
      m_poly2d.lineTo(xMin, (2.0f * yMin + yMax) / 3.0f);
      m_poly2d.closePath();
      shape = m_poly2d;
      break;
    case SHAPE_PARALLELOGRAM:
      m_poly2d.reset();
      m_poly2d.moveTo(xMin, yMin);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMin);
      m_poly2d.lineTo(xMax, yMax);
      m_poly2d.lineTo((2.0f * xMin + xMax) / 3.0f, yMax);
      m_poly2d.closePath();
      shape = m_poly2d;
      break;
    case SHAPE_TRIANGLE:
      m_poly2d.reset();
      m_poly2d.moveTo(xMin, yMax);
      m_poly2d.lineTo((xMin + xMax) / 2.0f, yMin);
      m_poly2d.lineTo(xMax, yMax);
      m_poly2d.closePath();
      shape = m_poly2d;
      break;
    default:
      throw new IllegalArgumentException("shapeType is not recognized"); }
    if (borderWidth == 0.0f) m_g2d.setColor(fillColor);
    else m_g2d.setColor(borderColor);
    m_g2d.fill(shape);
    if (borderWidth != 0.0f) { // Fill inner node.
      while (true) {
        final Shape innerShape;
        if (shapeType == SHAPE_ELLIPSE) {
          // This is an approximation to proper border width.  It's much
          // faster than drawing a curvy path of some thickness, and this
          // approach leads to exact intersection calculations for edges.
          final float innerXMin = xMin + borderWidth;
          final float innerYMin = yMin + borderWidth;
          final float innerXMax = xMax - borderWidth;
          final float innerYMax = yMax - borderWidth;
          if (innerXMin >= innerXMax || innerYMin >= innerYMax) {
            innerShape = null; break; }
          m_ellp2d.setFrame(innerXMin, innerYMin,
                            innerXMax - innerXMin, innerYMax - innerYMin);
          innerShape = m_ellp2d;
          break; }
        else if (shapeType == SHAPE_RECTANGLE) {
          final float innerXMin = xMin + borderWidth;
          final float innerYMin = yMin + borderWidth;
          final float innerXMax = xMax - borderWidth;
          final float innerYMax = yMax - borderWidth;
          if (innerXMin >= innerXMax || innerYMin >= innerYMax) {
            innerShape = null; break; }
          m_rect2d.setRect(innerXMin, innerYMin,
                           innerXMax - innerXMin, innerYMax - innerYMin);
          innerShape = m_rect2d;
          break; }
        else { // A general polygon.
          innerShape = null;
          break;
        }
      }
    }
  }

  /**
   * This is the method that will render a node very quickly.
   * The node shape used by this method is SHAPE_RECTANGLE.<p>
   * xMin, yMin, xMax, and yMax specify the extents of the node in the
   * node coordinate space, not the image coordinate space.  Thus, these
   * values will likely not change from frame to frame, as zoom and pan
   * operations are performed.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if xMin is greater than xMax or if
   *   yMin is greater than yMax.
   */
  public final void drawNodeLow(final float xMin, final float yMin,
                                final float xMax, final float yMax,
                                final Color fillColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
      if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax"); }
    if (m_antialias) setLowDetail();
    m_rect2d.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
    m_g2d.setColor(fillColor);
    m_g2d.fill(m_rect2d);
  }

  public final void drawEdgeLow(final float x0, final float y0,
                                final float x1, final float y1,
                                final Color edgeColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher"); }
    if (m_antialias) setLowDetail();
    m_line2d.setLine(x0, y0, x1, y1);
    m_g2d.setColor(edgeColor);
    // I'm setting the stroke width to zero so that I get a guarantee that
    // the simple and efficient Bresenham line drawing algorithm gets used
    // regardless of how zoomed in we are.  Otherwise, on certain zoom levels
    // the line drawing pipeline will start to fill polygons, which is slower
    // by a factor of 100.
    if (m_dash[0] != 0.0f || m_currStrokeWidth != 0.0f) setStroke(0.0f, 0.0f);
    m_g2d.draw(m_line2d);
  }

  /**
   * @param dashLength a positive value representing the length of dashes
   *   on the edge, or zero to indicate that the edge is solid.
   * @exception IllegalArgumentException if edgeThickness is less than zero or
   *   if dashLength is less than zero.
   */
  public final void drawEdgeFull(final float x0, final float y0,
                                 final float x1, final float y1,
                                 final float edgeThickness,
                                 final Color edgeColor,
                                 final float dashLength)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (edgeThickness < 0.0f)
        throw new IllegalArgumentException("edgeThickness < 0");
      if (dashLength < 0.0f)
        throw new IllegalArgumentException("dashLength < 0"); }
    if (!m_antialias) setHighDetail();
    if (m_dash[0] != dashLength || m_currStrokeWidth != edgeThickness)
      setStroke(edgeThickness, dashLength);
    m_line2d.setLine(x0, y0, x1, y1);
    m_g2d.setColor(edgeColor);
    m_g2d.draw(m_line2d);
  }

  private final void setLowDetail()
  {
    m_antialias = false;
    m_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_DEFAULT);
    m_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
    m_g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
    m_g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_DEFAULT);
  }

  private final void setHighDetail()
  {
    m_antialias = true;
    m_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_PURE);
  }

  private final void setStroke(final float width, final float dashLength)
  {
    m_dash[0] = dashLength;
    m_dash[1] = dashLength;
    m_currStrokeWidth = width;
    if (m_dash[0] == 0.0f)
      m_g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER, 10.0f));
    else
      m_g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_MITER, 10.0f,
                                      m_dash, 0.0f));
  }

}

package cytoscape.render.immed;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * This is functional programming at it's finest [sarcasm].
 * The purpose of this class is to make the proper calls on a Graphics2D
 * object to efficiently render nodes, labels, and edges.
 */
public final class GraphGraphics
{

  public static final byte SHAPE_DIAMOND = 0;
  public static final byte SHAPE_ELLIPSE = 1;
  public static final byte SHAPE_HEXAGON = 2;
  public static final byte SHAPE_OCTAGON = 3;
  public static final byte SHAPE_PARALLELOGRAM = 4;
  public static final byte SHAPE_RECTANGLE = 5;
  public static final byte SHAPE_TRIANGLE = 6;

  public static final byte BORDER_DASHED = 0;
  public static final byte BORDER_SOLID = 1;

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private final Color m_bgColor;
  private final boolean m_antialias;
  private final boolean m_debug;
  private final Rectangle2D.Double m_rect2d;
  private final Ellipse2D.Double m_ellp2d;
  private Graphics2D m_g2d;
  private float m_currStrokeWidth;
  private AffineTransform m_currXform; // Not sure that we will need this.

  /**
   * All rendering operations will be performed on the specified image.
   * This constructor needs to be called from the AWT event handling thread.
   * @param image an off-screen image (an image that supports the
   *   getGraphics() method).
   * @param debug if this is true, extra [and time-consuming] error checking
   *   will take place.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public GraphGraphics(final Image image, final Color bgColor,
                       final boolean antialias, final boolean debug)
  {
    this.image = image;
    m_bgColor = bgColor;
    m_antialias = antialias;
    m_debug = debug;
    m_rect2d = new Rectangle2D.Double();
    m_ellp2d = new Ellipse2D.Double();
    clear(0.0d, 0.0d, 1.0d);
  }

  /**
   * Clears image area with the specified background color, and sets an
   * appropriate transformation of coordinate systems.
   * It is healthy to call this method right before starting
   * to render a new picture.  Don't try to be clever in not calling this
   * method.<p>
   * This method must be called from the AWT event handling thread.<p>
   * NOTE: Initially, I wanted to have the background always be transparent.
   * The idea was to be able to render a complete graph scene by rendering
   * several images on top of one another.  Unfortunately, with the Java2D
   * architecture, this becomes a very special (and slow) case; we'd have to
   * use BufferedImage to do this, and I want to allow any kind of image
   * to be used by an instance of this class.
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
    m_g2d.setBackground(m_bgColor);
    m_g2d.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
    if (m_antialias)
      m_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

    // Set stroke.
    m_currStrokeWidth = 1.0f;
    final BasicStroke stroke = new BasicStroke
      (m_currStrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    m_g2d.setStroke(stroke);

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
   * node shape (in the node coordinate system), but not including the border
   * width.  If a node's border is nonzero, then that node's extents will
   * be slightly larger than specified.  The ``fill'' part of the node,
   * however, will remain exactly as specified.
   * @param borderWidth the border width, in node coordinate system; if
   *   this value is zero, the rendering engine skips over the process of
   *   rendering the border, which gives a significant performance boost.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if xMin is greater than xMax or if
   *   yMin is greater than yMax, or if borderWidth is negative,
   *   or if shapeType is not one of the
   *   SHAPE_* constants, or if borderType is not one of the BORDER_*
   *   constants.
   */
  public final void drawNodeFull(final byte shapeType,
                                 final double xMin, final double yMin,
                                 final double xMax, final double yMax,
                                 final Color fillColor, final byte borderType,
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
    default:
      throw new IllegalArgumentException("shapeType is not recognized"); }
    m_g2d.setColor(fillColor);
    m_g2d.fill(shape);
    if (borderWidth != 0.0f) {
      if (borderWidth != m_currStrokeWidth) {
        m_currStrokeWidth = borderWidth;
        final BasicStroke stroke = new BasicStroke
          (m_currStrokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        m_g2d.setStroke(stroke); }
      m_g2d.setColor(borderColor);
      m_g2d.draw(shape); }
  }

  /**
   * This is the method that will render a node very quickly.  For maximum
   * performance, use this method and render all nodes with the same color.
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
  public final void drawNodeLow(final double xMin, final double yMin,
                                final double xMax, final double yMax,
                                final Color fillColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
      if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax"); }
    m_rect2d.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
    m_g2d.setColor(fillColor);
    m_g2d.fill(m_rect2d);
  }

}

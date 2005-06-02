package cytoscape.render.immed;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
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
  public static final byte BORDER_NONE = 1;
  public static final byte BORDER_SOLID = 2;

  private static final boolean s_debug = true;
  private static final Color s_transparent = new Color(0, 0, 0, 0);
  private static final Color s_defaultColor = new Color(0);

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private final int m_imageWidth;
  private final int m_imageHeight;

  private Graphics2D m_g2d;
  private int m_currColor;
  private final Rectangle2D.Double m_rect2d;

  /**
   * All rendering operations will be performed on the specified image.
   * This constructor needs to be called from the AWT event handling thread.
   * @param image an off-screen image (an image gotten via the call
   *   java.awt.Component.createImage(int, int)).
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public GraphGraphics(final Image image)
  {
    this.image = image;
    m_imageWidth = this.image.getWidth(null);
    m_imageHeight = this.image.getHeight(null);
    this.clear();
    m_rect2d = new Rectangle2D.Double();
  }

  /**
   * Clears image area and makes it transparent.
   * It is healthy to call this method right before starting
   * to render a new picture.  Don't try to be clever in not calling this
   * method.<p>
   * This method must be called from the AWT event handling thread.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public final void clear()
  {
    if (s_debug && !EventQueue.isDispatchThread())
      throw new IllegalStateException
        ("calling thread is not AWT event dispatcher");
    m_g2d = (Graphics2D) image.getGraphics();
    m_g2d.setBackground(s_transparent);
    m_g2d.clearRect(0, 0, m_imageWidth, m_imageHeight);
    m_currColor = 0x00000000;
    m_g2d.setColor(s_defaultColor);
    m_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
  }

  /**
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if xMin is greater than xMax or if
   *   yMin is greater than yMax, or if shapeType is not one of the
   *   SHAPE_* constants, or if borderType is not one of the BORDER_*
   *   constants.
   */
  public final void drawNodeFull(final byte shapeType,
                                 final double xMin, final double yMin,
                                 final double xMax, final double yMax,
                                 final int fillColorRGB, final byte borderType,
                                 final double borderWidth,
                                 final int borderColorRGB)
  {
    if (s_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
      if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax"); }
    final Shape shape;
    switch (shapeType) {
    case SHAPE_RECTANGLE:
      shape = m_rect2d;
      m_rect2d.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
      break;
    case SHAPE_ELLIPSE:
      break;
    default:
      throw new IllegalArgumentException("shapeType is not recognized");
  }

  /**
   * This is the method that will render a node very quickly.  For maximum
   * performance, use this method and render all nodes with the same color.
   * The node shape used by this method is SHAPE_RECTANGLE.<p>
   * xMin, yMin, xMax, and yMax specify the extents of the node in the
   * underlying image's coordinate space.
   * @param fillColorRGB 0xRRGGBB (red, green, and blue components); the most
   *   significant 8 bits are completely ignored; it is suggested to use all
   *   zero bits for the most significant 8 bits for performance reasons.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if xMin is greater than xMax or if
   *   yMin is greater than yMax.
   */
  public final void drawNodeLow(final double xMin, final double yMin,
                                final double xMax, final double yMax,
                                final int fillColorRGB)
  {
    if (s_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
      if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax"); }
    if (fillColorRGB != m_currColor) {
      m_currColor = fillColorRGB;
      m_g2d.setColor(new Color(fillColorRGB)); }
    m_rect2d.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
    m_g2d.fill(m_rect2d);
  }

}

package cytoscape.render.immed;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * The purpose of this class is to make the proper calls on a Graphics2D
 * object to efficiently render nodes, labels, and edges.
 * This is functional programming at it's finest [sarcasm].<p>
 * This class deals with two coordinate systems: an image coordinate system
 * and a node coordinate system.  The programmer who uses this API will be
 * dealing mostly with the node coordinate system, especially when rendering
 * individual nodes and edges.  The clear() method specifies the mapping from
 * the node coordinate system to the image coordinate system.  The two
 * coordinate systems do not have the same orientations: while in the
 * image coordinate system increasing y values travel towards the bottom of
 * the image, the opposite is true in the node coordinate system.  That is,
 * in the node coordinate system, increasing x values point to the right
 * and increasing y values point to the top.  This is the "classical"
 * orientation for the xy plane, while we are forced to use the native Java
 * image coordinate system which has a different orientation.  The native
 * Java image coordinate system dictates that (0,0) is the upper left corner
 * of the image and that each unit represents a pixel width (or height).<p>
 * NOTE: Every method on an instance of this class needs to be called by
 * the AWT event dispatching thread save the constructor.  However,
 * checks for this are made only if debug is set to true (see constructur).
 * In fact, in certain situations [such as rendering to a non-image such
 * as a vector graphic] it may make sense to never call any of the methods
 * from the AWT event dispatching thread; use this class in this way at your
 * own risk and peril.
 */
public final class GraphGraphics
{

  public static final byte SHAPE_RECTANGLE = 0;
  public static final byte SHAPE_DIAMOND = 1;
  public static final byte SHAPE_ELLIPSE = 2;
  public static final byte SHAPE_HEXAGON = 3;
  public static final byte SHAPE_OCTAGON = 4;
  public static final byte SHAPE_PARALLELOGRAM = 5;
  public static final byte SHAPE_ROUNDED_RECTANGLE = 6;
  public static final byte SHAPE_TRIANGLE = 7;
  public static final byte SHAPE_VEE = 8;
  private static final byte s_last_shape = SHAPE_VEE;

  /**
   * This value is currently 100.
   */
  public static final int CUSTOM_SHAPE_MAX_VERTICES = 100;

  public static final byte ARROW_NONE = -1;
  public static final byte ARROW_DELTA = -2;
  public static final byte ARROW_DIAMOND = -3;
  public static final byte ARROW_DISC = -4;
  public static final byte ARROW_TEE = -5;
  public static final byte ARROW_BIDIRECTIONAL = -6;

  /**
   * This arrow type is not currently supported.  It will be in the
   * very near future.
   */
  public static final byte ARROW_MONO = -7;

  /**
   * A constant for controlling how cubic Bezier curves are drawn; This
   * particular constant results in elliptical-looking curves.
   */
  public static final double CURVE_ELLIPTICAL =
    4.0d * (Math.sqrt(2.0d) - 1.0d) / 3.0d;

  /**
   * A constant for controlling how cubic Bezier curves are drawn; This
   * particular constnat results in natural-looking curves.
   */
  public static final double CURVE_NATURAL = 2.0d / 3.0d;

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private final boolean m_debug;
  private final AffineTransform m_currXform = new AffineTransform();
  private final AffineTransform m_currNativeXform = new AffineTransform();
  private final AffineTransform m_xformUtil = new AffineTransform();
  private final Arc2D.Double m_arc2d = new Arc2D.Double();
  private final Ellipse2D.Double m_ellp2d = new Ellipse2D.Double();
  private final GeneralPath m_path2d = new GeneralPath();
  private final GeneralPath m_path2dPrime = new GeneralPath();
  private final Line2D.Double m_line2d = new Line2D.Double();
  private final double[] m_polyCoords = // I need this for extra precision.
    new double[2 * CUSTOM_SHAPE_MAX_VERTICES];
  private final HashMap m_customShapes = new HashMap();
  private final double[] m_ptsBuff = new double[4];
  private int m_polyNumPoints; // Used with m_polyCoords.
  private Graphics2D m_g2d;
  private Graphics2D m_gMinimal; // We use mostly java.awt.Graphics methods.
  private boolean m_cleared;

  /**
   * All rendering operations will be performed on the specified image.
   * No rendering operations are performed as a result of calling this
   * constructor.  It is safe to call this constructor from any thread.<p>
   * The image argument passed to this constructor must support at least three
   * methods: getGraphics(), getWidth(ImageObserver), and
   * getHeight(ImageObserver).  The image.getGraphics() method must return an
   * instance of java.awt.Graphics2D.  The hypothetical method calls
   * image.getWidth(null) and image.getHeight(null) must return the
   * corresponding dimension immediately.<p>
   * Notice that it is not possible to resize the image area with this API.
   * This is not a problem; instances of this class are very lightweight and
   * are stateless; simply instantiate a new GraphGraphics when the image
   * area changes.
   * @param image an off-screen image; passing an image gotten from a call to
   *   java.awt.Component.createImage(int, int) works well, although
   *   experience shows that for full support of non-opaque colors,
   *   java.awt.image.BufferedImage should be used instead.
   * @param debug if this is true, extra [and time-consuming] error checking
   *   will take place in each method call; it is recommended to have this
   *   value set to true during the testing phase; set it to false once
   *   you are sure that code does not mis-use this module.
   */
  public GraphGraphics(final Image image, final boolean debug)
  {
    this.image = image;
    m_debug = debug;
    m_path2dPrime.setWindingRule(GeneralPath.WIND_EVEN_ODD);
    m_cleared = false;
  }

  /**
   * Clears image area with background color specified [may be non-opaque],
   * and sets an appropriate transformation of coordinate systems.  See the
   * class description for a definition of the two coordinate systems:
   * the node coordinate system and the image coordinate system.<p>
   * It is mandatory to call this method before making the first rendering
   * call.
   * @param bgColor a color to use when clearing the image before painting
   *   a new frame; transparent colors are honored, provided that the
   *   underlying image supports transparent colors.
   * @param xCenter the x component of the translation transform for the frame
   *   about to be rendered; a node whose center is at the X coordinate xCenter
   *   will be rendered exactly in the middle of the image going across;
   *   increasing x values (in the node coordinate system) result in movement
   *   towards the right on the image.
   * @param yCenter the y component of the translation transform for the frame
   *   about to be rendered; a node whose center is at the Y coordinate yCenter
   *   will be rendered exactly in the middle of the image going top to bottom;
   *   increasing y values (in the node coordinate system) result in movement
   *   towards the top on the image (not the bottom of the image!!!).
   * @param scaleFactor the scaling that is to take place when rendering;
   *   a distance of 1 in node coordinates translates to a distance of
   *   scaleFactor in the image coordinate system (usually one unit in the
   *   image coordinate system equates to one pixel width).
   * @exception IllegalArgumentException if scaleFactor is not positive.
   */
  public final void clear(final Color bgColor,
                          final double xCenter, final double yCenter,
                          final double scaleFactor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!(scaleFactor > 0.0d))
        throw new IllegalArgumentException("scaleFactor is not positive"); }
    if (m_gMinimal != null) { m_gMinimal.dispose(); m_gMinimal = null; }
    if (m_g2d != null) m_g2d.dispose();
    m_g2d = (Graphics2D) image.getGraphics();
    final Composite origComposite = m_g2d.getComposite();
    m_g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    m_g2d.setBackground(bgColor);
    m_g2d.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
    m_g2d.setComposite(origComposite);
    m_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_SPEED);
    m_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_PURE);
    setStroke(0.0f, 0.0f, BasicStroke.CAP_ROUND, true);

    m_currXform.setToTranslation(0.5d * image.getWidth(null),
                                 0.5d * image.getHeight(null));
    m_currXform.scale(scaleFactor, -scaleFactor);
    m_currXform.translate(-xCenter, -yCenter);
    m_g2d.transform(m_currXform);
    m_currNativeXform.setTransform(m_g2d.getTransform());
    m_cleared = true;
  }

  /**
   * Determines whether or not the point (xQuery, yQuery) lies inside
   * the shape of type nodeShape (may be a custom shape or one of the
   * SHAPE_* constants) with extents (xMin, yMin) to (xMax, yMax).<p>
   * This method can be used together with xformImageToNodeCoords()
   * for determining whether or not user mouse clicks are inside
   * of a node area (if the graph image generated by this instance is
   * associated with a user interface).<p>
   * There is a constraint that only applies to SHAPE_ROUNDED_RECTANGLE
   * which imposes that the maximum of the width and height be strictly
   * less than twice the minimum of the width and height of the node.
   * @param nodeShape the shape (SHAPE_* constant or custom shape) of
   *   the area to query.
   * @param xMin an extent of the area to query.
   * @param yMin an extent of the area to query.
   * @param xMax an extent of the area to query.
   * @param yMax an extent of the area to query.
   * @param xQuery the x coordinate of the query point, in the node
   *   coordinate system.
   * @param yQuery the y coordinate of the query point, in the node
   *   coordinate system.
   * @return true if and only if the point (xQuery, yQuery) lies inside
   *   of the area specified; if the point lies on the boundary of the
   *   query area, the return value can be either true or false.
   * @exception IllegalArgumentException if xMin is not less than xMax
   *   or if yMin is not less than yMax.
   */
  public final boolean contains(final byte nodeShape,
                                final float xMin, final float yMin,
                                final float xMax, final float yMax,
                                final float xQuery, final float yQuery)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!(xMin < xMax)) throw new IllegalArgumentException
                            ("xMin not less than xMax");
      if (!(yMin < yMax)) throw new IllegalArgumentException
                            ("yMin not less than yMax");
      if (nodeShape == SHAPE_ROUNDED_RECTANGLE) {
        final double width = ((double) xMax) - xMin;
        final double height = ((double) yMax) - yMin;
        if (!(Math.max(width, height) < 2.0d * Math.min(width, height)))
          throw new IllegalArgumentException
            ("rounded rectangle does not meet constraint " +
             "max(width, height) < 2 * min(width, height)"); } }
    return getShape(nodeShape, xMin, yMin, xMax, yMax).contains(xQuery,
                                                                yQuery);
  }

  /**
   * Uses the current transform to map the specified image coordinates to
   * node coordinates.
   * The transform used is defined by the last call to clear().
   * It does not make sense to call this method if clear() has not been
   * called at least once previously, and this method will cause errors in
   * this case.
   * @param coords an array of length [at least] two which acts both
   *   as the input and as the output of this method; coords[0] is the
   *   input x coordinate in the image coordinate system and is written
   *   as the x coordinate in the node coordinate system by this method;
   *   coords[1] is the input y coordinate in the image coordinate system and
   *   is written as the y coordinate in the node coordinate system by this
   *   method; the exact transform which takes place is defined by the
   *   previous call to the clear() method.
   */
  public final void xformImageToNodeCoords(final double[] coords)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!m_cleared) throw new IllegalStateException
                        ("clear() has not been called previously"); }
    try {
      m_currXform.inverseTransform(coords, 0, coords, 0, 1); }
    catch (java.awt.geom.NoninvertibleTransformException e) {
      throw new RuntimeException("noninvertible matrix - cannot happen"); }
  }

  /*
   * This method has the side effect of setting m_ellp2d or m_path2d;
   * if m_path2d is set (every case but the ellipse and rounded rectangle),
   * then m_polyCoords and m_polyNumPoints are also set.
   */
  private final Shape getShape(final byte nodeShape,
                               final double xMin, final double yMin,
                               final double xMax, final double yMax)
  {
    switch (nodeShape) {
    case SHAPE_ELLIPSE:
      m_ellp2d.setFrame(xMin, yMin, xMax - xMin, yMax - yMin);
      return m_ellp2d;
    case SHAPE_RECTANGLE:
      m_polyNumPoints = 4;
      m_polyCoords[0] = xMin;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = xMax;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = yMax;
      m_polyCoords[6] = xMin;
      m_polyCoords[7] = yMax;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    case SHAPE_DIAMOND:
      m_polyNumPoints = 4;
      m_polyCoords[0] = (xMin + xMax) / 2.0d;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = xMax;
      m_polyCoords[3] = (yMin + yMax) / 2.0d;
      m_polyCoords[4] = (xMin + xMax) / 2.0d;
      m_polyCoords[5] = yMax;
      m_polyCoords[6] = xMin;
      m_polyCoords[7] = (yMin + yMax) / 2.0d;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    case SHAPE_HEXAGON:
      m_polyNumPoints = 6;
      m_polyCoords[0] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = (yMin + yMax) / 2.0d;
      m_polyCoords[6] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[7] = yMax;
      m_polyCoords[8] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[9] = yMax;
      m_polyCoords[10] = xMin;
      m_polyCoords[11] = (yMin + yMax) / 2.0d;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    case SHAPE_OCTAGON:
      m_polyNumPoints = 8;
      m_polyCoords[0] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = (2.0d * yMin + yMax) / 3.0d;
      m_polyCoords[6] = xMax;
      m_polyCoords[7] = (2.0d * yMax + yMin) / 3.0d;
      m_polyCoords[8] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[9] = yMax;
      m_polyCoords[10] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[11] = yMax;
      m_polyCoords[12] = xMin;
      m_polyCoords[13] = (2.0d * yMax + yMin) / 3.0d;
      m_polyCoords[14] = xMin;
      m_polyCoords[15] = (2.0d * yMin + yMax) / 3.0d;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    case SHAPE_PARALLELOGRAM:
      m_polyNumPoints = 4;
      m_polyCoords[0] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = xMax;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[5] = yMax;
      m_polyCoords[6] = xMin;
      m_polyCoords[7] = yMax;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    case SHAPE_ROUNDED_RECTANGLE:
      // A condition that must be satisfied (pertaining to radius) is that
      // max(width, height) <= 2 * min(width, height).
      computeRoundedRectangle(xMin, yMin, xMax, yMax,
                              Math.max(xMax - xMin, yMax - yMin) / 4.0d,
                              m_path2d);
      return m_path2d;
    case SHAPE_TRIANGLE:
      m_polyNumPoints = 3;
      m_polyCoords[0] = xMin;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = xMax;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = (xMin + xMax) / 2.0d;
      m_polyCoords[5] = yMax;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    case SHAPE_VEE:
      m_polyNumPoints = 4;
      m_polyCoords[0] = xMin;
      m_polyCoords[1] = yMax;
      m_polyCoords[2] = (xMin + xMax) / 2.0d;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = yMax;
      m_polyCoords[6] = (xMin + xMax) / 2.0d;
      m_polyCoords[7] = (2.0d * yMax + yMin) / 3.0d;
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d;
    default: // Try a custom node shape or throw an exception.
      final double[] storedPolyCoords = // To optimize don't construct Byte.
        (double[]) m_customShapes.get(new Byte(nodeShape));
      if (storedPolyCoords == null)
        throw new IllegalArgumentException("nodeShape is not recognized");
      m_polyNumPoints = storedPolyCoords.length / 2;
      final double desiredXCenter = (xMin + xMax) / 2.0d;
      final double desiredYCenter = (yMin + yMax) / 2.0d;
      final double desiredWidth = xMax - xMin;
      final double desiredHeight = yMax - yMin;
      m_xformUtil.setToTranslation(desiredXCenter, desiredYCenter);
      m_xformUtil.scale(desiredWidth, desiredHeight);
      m_xformUtil.transform(storedPolyCoords, 0,
                            m_polyCoords, 0, m_polyNumPoints);
      // The rest of this code can be factored with other cases.
      m_path2d.reset();
      m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_path2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_path2d.closePath();
      return m_path2d; }
  }

  private final static void computeRoundedRectangle(final double xMin,
                                                    final double yMin,
                                                    final double xMax,
                                                    final double yMax,
                                                    final double radius,
                                                    final GeneralPath path2d)
  {
    path2d.reset();
    path2d.moveTo((float) (xMax - radius), (float) yMin);
    path2d.curveTo((float) ((CURVE_ELLIPTICAL - 1.0d) * radius + xMax),
                   (float) yMin,
                   (float) xMax,
                   (float) ((1.0d - CURVE_ELLIPTICAL) * radius + yMin),
                   (float) xMax, (float) (radius + yMin));
    path2d.lineTo((float) xMax, (float) (yMax - radius));
    path2d.curveTo((float) xMax,
                   (float) ((CURVE_ELLIPTICAL - 1.0d) * radius + yMax),
                   (float) ((CURVE_ELLIPTICAL - 1.0d) * radius + xMax),
                   (float) yMax,
                   (float) (xMax - radius), (float) yMax);
    path2d.lineTo((float) (radius + xMin), (float) yMax);
    path2d.curveTo((float) ((1.0d - CURVE_ELLIPTICAL) * radius + xMin),
                   (float) yMax,
                   (float) xMin,
                   (float) ((CURVE_ELLIPTICAL - 1.0d) * radius + yMax),
                   (float) xMin, (float) (yMax - radius));
    path2d.lineTo((float) xMin, (float) (radius + yMin));
    path2d.curveTo((float) xMin,
                   (float) ((1.0d - CURVE_ELLIPTICAL) * radius + yMin),
                   (float) ((1.0d - CURVE_ELLIPTICAL) * radius + xMin),
                   (float) yMin,
                   (float) (radius + xMin), (float) yMin);
    path2d.closePath();
  }

  // This member variable only to be used from within defineCustomNodeShape().
  private byte m_nextCustomShapeType = s_last_shape + 1;

  /**
   * The custom node shape that is defined is a polygon specified
   * by the coordinates supplied.  The polygon must meet several constraints
   * listed below.<p>
   * If we define the value xCenter to be the average of the minimum and
   * maximum X values of the vertices and if we define yCenter likewise, then
   * the specified polygon must meet the following constraints:
   * <ol>
   *   <li>Each polygon line segment must have nonzero length.</li>
   *   <li>No two consecutive polygon line segments can be parallel (this
   *     essentially implies that the polygon must have at least three
   *     vertices).</li>
   *   <li>No two distinct non-consecutive polygon line segments may
   *     intersect (not even at the endpoints); this makes possible the
   *     notion of interior of the polygon.</li>
   *   <li>The polygon must be star-shaped with respect to the point
   *     (xCenter, yCenter); a polygon is said to be <i>star-shaped with
   *     respect to a point (a,b)</i> if and only if for every point (x,y)
   *     in the interior or on the boundary of the polygon, the interior of
   *     the segment (a,b)->(x,y) lies in the interior of the polygon.</li>
   *   <li>The path traversed by the polygon must be counter-clockwise where
   *     +x points right and +y points up.</li>
   * </ol><p>
   * In addition to these constraints, when rendering custom nodes with
   * nonzero border width, possible problems may arise if the border width
   * is large with respect to the kinks in the polygon.
   * @param coords vertexCount * 2 consecutive coordinate values are read
   *   from this array starting at coords[offset]; coords[offset],
   *   coords[offset + 1], coords[offset + 2], coords[offset + 3] and so on
   *   are interpreted as x0, y0, x1, y1, and so on; the initial vertex must
   *   not be repeated as the last vertex specified.
   * @param offset the starting index of where to read coordinates from
   *   in the coords parameter.
   * @param vertexCount the number of vertices to read from coords;
   *   vertexCount * 2 entries in coords are read.
   * @return the node shape identifier to be used in future rendering calls
   *   (to be used as parameter nodeShape in method drawNodeFull()).
   * @exception IllegalArgumentException if any of the constraints are not met,
   *   or if the specified polygon has more than CUSTOM_SHAPE_MAX_VERTICES
   *   vertices.
   * @exception IllegalStateException if too many custom node shapes are
   *   already defined; a little over one hundered custom node shapes can be
   *   defined.
   */
  public final byte defineCustomNodeShape(final float[] coords,
                                          final int offset,
                                          final int vertexCount)
  {
    if (vertexCount > CUSTOM_SHAPE_MAX_VERTICES)
      throw new IllegalArgumentException
        ("too many vertices (greater than " + CUSTOM_SHAPE_MAX_VERTICES + ")");
    final double[] polyCoords;
    {
      polyCoords = new double[vertexCount * 2];
      for (int i = 0; i < polyCoords.length; i++)
        polyCoords[i] = coords[offset + i];

      // Normalize the polygon so that it spans [-0.5, 0.5] x [-0.5, 0.5].
      double xMin = Double.POSITIVE_INFINITY;
      double yMin = Double.POSITIVE_INFINITY;
      double xMax = Double.NEGATIVE_INFINITY;
      double yMax = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < polyCoords.length;) {
        xMin = Math.min(xMin, coords[i]);
        xMax = Math.max(xMax, coords[i++]);
        yMin = Math.min(yMin, coords[i]);
        yMax = Math.max(yMax, coords[i++]); }
      final double xDist = xMax - xMin;
      if (xDist == 0.0d) throw new IllegalArgumentException
                           ("polygon does not move in the X direction");
      final double yDist = yMax - yMin;
      if (yDist == 0.0d) throw new IllegalArgumentException
                           ("polygon does not move in the Y direction");
      final double xMid = (xMin + xMax) / 2.0d;
      final double yMid = (yMin + yMax) / 2.0d;
      for (int i = 0; i < polyCoords.length;) {
        double foo = (polyCoords[i] - xMid) / xDist;
        polyCoords[i++] = Math.min(Math.max(-0.5d, foo), 0.5d);
        foo = (polyCoords[i] - yMid) / yDist;
        polyCoords[i++] = Math.min(Math.max(-0.5d, foo), 0.5d); }
    }
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      // Test all criteria.
      int yInterceptsCenter = 0;
      for (int i = 0; i < vertexCount; i++) {
        final double x0 = polyCoords[i * 2];
        final double y0 = polyCoords[i * 2 + 1];
        final double x1 = polyCoords[(i * 2 + 2) % (vertexCount * 2)];
        final double y1 = polyCoords[(i * 2 + 3) % (vertexCount * 2)];
        final double x2 = polyCoords[(i * 2 + 4) % (vertexCount * 2)];
        final double y2 = polyCoords[(i * 2 + 5) % (vertexCount * 2)];
        final double distP0P1 = Math.sqrt((x1 - x0) * (x1 - x0) +
                                          (y1 - y0) * (y1 - y0));
        if ((float) distP0P1 == 0.0f) { // Too close to distance zero.
          throw new IllegalArgumentException
            ("a line segment has distance [too close to] zero"); }
        final double distP2fromP0P1 =
          ((y0 - y1) * x2 + (x1 - x0) * y2 + x0 * y1 - x1 * y0) / distP0P1;
        if ((float) distP2fromP0P1 == 0.0f) { // Too close to parallel.
          throw new IllegalArgumentException
            ("either a line segment has distance [too close to] zero or " +
             "two consecutive line segments are [too close to] parallel"); }
        final double distCenterFromP0P1 = (x0 * y1 - x1 * y0) / distP0P1;
        if (!((float) distCenterFromP0P1 > 0.0f)) {
          throw new IllegalArgumentException
            ("polygon is going clockwise or is not star-shaped with " +
             "respect to center"); }
        if (Math.min(y0, y1) < 0.0d && Math.max(y0, y1) >= 0.0d) {
          yInterceptsCenter++; } }
      if (yInterceptsCenter != 2)
        throw new IllegalArgumentException
          ("the polygon self-intersects (we know this because the winding " +
           "number of the center is not one)"); }

    // polyCoords now contains a polygon spanning [-0.5, 0.5] X [-0.5, 0.5]
    // that passes all of the criteria.
    if (m_nextCustomShapeType < 0)
      throw new IllegalStateException
        ("too many custom node shapes are already defined");
    m_customShapes.put(new Byte(m_nextCustomShapeType), polyCoords);
    return m_nextCustomShapeType++;
  }

  /**
   * If this is a new instance, imports the custom node shapes from the
   * GraphGraphics specified into this GraphGraphics.
   * @param grafx custom node shapes will be imported from this
   *   GraphGraphics.
   * @exception IllegalStateException if at least one custom node shape
   *   is already defined in this GraphGraphics.
   */
  public final void importCustomNodeShapes(final GraphGraphics grafx)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher"); }
    // I define this error check outside the scope of m_debug because
    // clobbering existing custom node shape definitions could be a major.
    if (m_nextCustomShapeType != s_last_shape + 1)
      throw new IllegalStateException
        ("a custom node shape is already defined in this GraphGraphics");
    final Iterator oldEntries = grafx.m_customShapes.entrySet().iterator();
    while (oldEntries.hasNext()) {
      final Map.Entry entry = (Map.Entry) oldEntries.next();
      m_customShapes.put(entry.getKey(), entry.getValue());
      m_nextCustomShapeType++; }
  }

  /**
   * Draws a node with medium to high detail, depending on parameters
   * specified.
   * The xMin, yMin, xMax, and yMax parameters specify the extents of the
   * node shape (in the node coordinate system), including the border
   * width.  That is, the drawn border won't extend beyond the extents
   * specified.<p>
   * There is an imposed constraint on borderWidth which, using the
   * implemented algorithms, prevents strange-looking borders.  The
   * constraint is that borderWidth may not exceed
   * the minimum of the node width and node height divided by six.  In
   * addition, for custom node shapes, this requirement may be more
   * constrained, depending on the kinks in the custom node shape.<p>
   * There is a constraint that only applies to SHAPE_ROUNDED_RECTANGLE
   * which imposes that the maximum of the width and height be strictly
   * less than twice the minimum of the width and height of the node.<p>
   * This method will not work unless clear() has been called at least once
   * previously.
   * @param nodeShape the shape of the node to draw (one of the SHAPE_*
   *   constants or a custom node shape).
   * @param xMin an extent of the node shape to draw, in node coordinate
   *   space; the drawn shape will theoretically contain a point that lies
   *   on this X coordinate.
   * @param yMin an extent of the node shape to draw, in node coordinate
   *   space; the drawn shape will theoretically contain a point that lies
   *   on this X coordinate.
   * @param xMax an extent of the node shape to draw, in node coordinate
   *   space; the drawn shape will theoretically contain a point that lies
   *   on this Y coordinate.
   * @param yMax an extent of the node shape to draw, in node coordinate
   *   space; the drawn shape will theoretically contain a point that lies
   *   on this Y coordinate.
   * @param fillColor the color to use when drawing the node area minus
   *   the border (the "interior" of the node).
   * @param borderWidth the border width, in node coordinate space; if
   *   this value is zero, the rendering engine skips over the process of
   *   rendering the border, which gives a significant performance boost.
   * @param borderColor if borderWidth is not zero, this color is used for
   *   rendering the node border; otherwise, this parameter is ignored (and
   *   may be null).
   * @exception IllegalArgumentException if xMin is not less than xMax or if
   *   yMin is not less than yMax, if borderWidth is negative or is greater
   *   than Math.min(xMax - xMin, yMax - yMin) / 6 (for custom node shapes
   *   borderWidth may be even more limited, depending on the specific shape),
   *   if nodeShape is SHAPE_ROUNDED_RECTANGLE and the condition
   *   max(width, height) < 2 * min(width, height) does not hold,
   *   or if nodeShape is neither one of the SHAPE_* constants nor a
   *   previously defined custom node shape.
   */
  public final void drawNodeFull(final byte nodeShape,
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
      if (!m_cleared) throw new IllegalStateException
                      ("clear() has not been called previously");
      if (!(xMin < xMax)) throw new IllegalArgumentException
                            ("xMin not less than xMax");
      if (!(yMin < yMax)) throw new IllegalArgumentException
                            ("yMin not less than yMax");
      if (!(borderWidth >= 0.0f))
        throw new IllegalArgumentException
          ("borderWidth not zero or positive");
      if (!(6.0d * borderWidth <= Math.min(((double) xMax) - xMin,
                                           ((double) yMax) - yMin)))
        throw new IllegalArgumentException
          ("borderWidth is not less than the minimum of node width and node " +
           "height divided by six");
      if (nodeShape == SHAPE_ROUNDED_RECTANGLE) {
        final double width = ((double) xMax) - xMin;
        final double height = ((double) yMax) - yMin;
        if (!(Math.max(width, height) < 2.0d * Math.min(width, height)))
          throw new IllegalArgumentException
            ("rounded rectangle does not meet constraint " +
             "max(width, height) < 2 * min(width, height)"); } }

    if (borderWidth == 0.0f) {
      m_g2d.setColor(fillColor);
      m_g2d.fill(getShape(nodeShape, xMin, yMin, xMax, yMax)); }
    else { // There is a border.
      m_path2dPrime.reset();
      m_path2dPrime.append(getShape(nodeShape, xMin, yMin, xMax, yMax),
                           false); // Make a copy, essentially.
      final Shape innerShape;
      if (nodeShape == SHAPE_ELLIPSE) {
        // TODO: Compute a more accurate inner area for ellipse + border.
        innerShape = getShape
          (SHAPE_ELLIPSE,
           ((double) xMin) + borderWidth, ((double) yMin) + borderWidth,
           ((double) xMax) - borderWidth, ((double) yMax) - borderWidth); }
      else if (nodeShape == SHAPE_ROUNDED_RECTANGLE) {
        computeRoundedRectangle
          (((double) xMin) + borderWidth, ((double) yMin) + borderWidth,
           ((double) xMax) - borderWidth, ((double) yMax) - borderWidth,
           Math.max(((double) xMax) - xMin,
                    ((double) yMax) - yMin) / 4.0d - borderWidth, m_path2d);
        innerShape = m_path2d; }
      else {
        // A general [possibly non-convex] polygon with certain
        // restrictions: no two consecutive line segments can be parallel,
        // each line segment must have nonzero length, the polygon cannot
        // self-intersect, and the polygon must be counter-clockwise
        // in the node coordinate system.
        m_path2d.reset();
        final double xNot = m_polyCoords[0];
        final double yNot = m_polyCoords[1];
        final double xOne = m_polyCoords[2];
        final double yOne = m_polyCoords[3];
        double xPrev = xNot;
        double yPrev = yNot;
        double xCurr = xOne;
        double yCurr = yOne;
        double xNext = m_polyCoords[4];
        double yNext = m_polyCoords[5];
        computeInnerPoint(m_ptsBuff, xPrev, yPrev, xCurr, yCurr,
                          xNext, yNext, borderWidth);
        m_path2d.moveTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);
        for (int i = 6;;) {
          if (i == m_polyNumPoints * 2) {
            computeInnerPoint(m_ptsBuff, xCurr, yCurr, xNext, yNext,
                              xNot, yNot, borderWidth);
            m_path2d.lineTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);
            computeInnerPoint(m_ptsBuff, xNext, yNext, xNot, yNot,
                              xOne, yOne, borderWidth);
            m_path2d.lineTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);
            m_path2d.closePath();
            break; }
          else {
            xPrev = xCurr;
            yPrev = yCurr;
            xCurr = xNext;
            yCurr = yNext;
            xNext = m_polyCoords[i++];
            yNext = m_polyCoords[i++];
            computeInnerPoint(m_ptsBuff, xPrev, yPrev, xCurr, yCurr,
                              xNext, yNext, borderWidth);
            m_path2d.lineTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]); } }
        innerShape = m_path2d; }
      m_g2d.setColor(fillColor);
      m_g2d.fill(innerShape);

      // Render the border such that it does not overlap with the fill
      // region because translucent colors may be used.  Don't do
      // things differently for opaque and translucent colors for the
      // sake of consistency.
      m_path2dPrime.append(innerShape, false);
      m_g2d.setColor(borderColor);
      m_g2d.fill(m_path2dPrime); }
  }

  /*
   * output[0] is the x return value and output[1] is the y return value.
   * The line prev->curr cannot be parallel to curr->next.
   */
  private final static void computeInnerPoint(final double[] output,
                                              final double xPrev,
                                              final double yPrev,
                                              final double xCurr,
                                              final double yCurr,
                                              final double xNext,
                                              final double yNext,
                                              final double borderWidth)
  {
    final double segX1 = xCurr - xPrev;
    final double segY1 = yCurr - yPrev;
    final double segLength1 = Math.sqrt(segX1 * segX1 + segY1 * segY1);
    final double segX2 = xNext - xCurr;
    final double segY2 = yNext - yCurr;
    final double segLength2 = Math.sqrt(segX2 * segX2 + segY2 * segY2);
    final double segX2Normal = segX2 / segLength2;
    final double segY2Normal = segY2 / segLength2;
    final double xNextPrime = segX2Normal * segLength1 + xPrev;
    final double yNextPrime = segY2Normal * segLength1 + yPrev;
    final double segPrimeX = xNextPrime - xCurr;
    final double segPrimeY = yNextPrime - yCurr;
    final double distancePrimeToSeg1 =
      (segX1 * yNextPrime - segY1 * xNextPrime +
       xPrev * yCurr - xCurr * yPrev) / segLength1;
    final double multFactor = borderWidth / distancePrimeToSeg1;
    output[0] = multFactor * segPrimeX + xCurr;
    output[1] = multFactor * segPrimeY + yCurr;
  }

  /**
   * This is the method that will render a node very quickly.
   * The node shape used by this method is SHAPE_RECTANGLE.  Translucent
   * colors are not officially supported by the low detail rendering
   * methods, so use translucent colors at your own risk.<p>
   * xMin, yMin, xMax, and yMax specify the extents of the node in the
   * node coordinate space, not the image coordinate space.  Thus, these
   * values will likely not change from frame to frame, as zoom and pan
   * operations are performed.<p>
   * This method will not work unless clear() has been called at least once
   * previously.
   * @exception IllegalArgumentException if xMin is not less than xMax or if
   *   yMin is not less than yMax.
   */
  public final void drawNodeLow(final float xMin, final float yMin,
                                final float xMax, final float yMax,
                                final Color fillColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!m_cleared) throw new IllegalStateException
                        ("clear() has not been called previously");
      if (!(xMin < xMax)) throw new IllegalArgumentException
                            ("xMin not less than xMax");
      if (!(yMin < yMax)) throw new IllegalArgumentException
                            ("yMin not less than yMax"); }
    if (m_gMinimal == null) makeMinimalGraphics();
    // I'm transforming points manually because the resulting underlying
    // graphics pipeline used is much faster.
    m_ptsBuff[0] = xMin; m_ptsBuff[1] = yMin;
    m_ptsBuff[2] = xMax; m_ptsBuff[3] = yMax;
    m_currXform.transform(m_ptsBuff, 0, m_ptsBuff, 0, 2);
    // Here, double values outside of the range of ints will be cast to
    // the nearest int without overflow.
    final int xNot = (int) m_ptsBuff[0];
    final int yNot = (int) m_ptsBuff[3]; // y coordinates are inverse.
    final int xOne = (int) m_ptsBuff[2];
    final int yOne = (int) m_ptsBuff[1]; // y coordinates are inverse.
    m_gMinimal.setColor(fillColor);
    m_gMinimal.fillRect(xNot, yNot, Math.max(1, xOne - xNot), // Overflow will
                        Math.max(1, yOne - yNot));            // be problem.
  }

  /**
   * This method will not work unless clear() has been called at least once
   * previously.  Translucent
   * colors are not officially supported by the low detail rendering
   * methods, so use translucent colors at your own risk.
   */
  public final void drawEdgeLow(final float x0, final float y0,
                                final float x1, final float y1,
                                final Color edgeColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!m_cleared) throw new IllegalStateException
                        ("clear() has not been called previously"); }
    // This following statement has to be consistent with the full edge
    // rendering logic.
    if (x0 == x1 && y0 == y1) return;
    if (m_gMinimal == null) makeMinimalGraphics();
    // I'm transforming points manually because the resulting underlying
    // graphics pipeline used is much faster.
    m_ptsBuff[0] = x0; m_ptsBuff[1] = y0;
    m_ptsBuff[2] = x1; m_ptsBuff[3] = y1;
    m_currXform.transform(m_ptsBuff, 0, m_ptsBuff, 0, 2);
    final int xNot = (int) m_ptsBuff[0];
    final int yNot = (int) m_ptsBuff[1];
    final int xOne = (int) m_ptsBuff[2];
    final int yOne = (int) m_ptsBuff[3];
    m_gMinimal.setColor(edgeColor);
    m_gMinimal.drawLine(xNot, yNot, xOne, yOne);
  }

  /*
   * Sets m_gMinimal.
   */
  private final void makeMinimalGraphics()
  {
    m_gMinimal = (Graphics2D) image.getGraphics();
    m_gMinimal.setRenderingHint
      (RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
  }

  /**
   * Something is rendered in all cases except where the length of the edge
   * is zero (because in that case directionality cannot be determined for
   * at least some arrowheads).  However, it may make sense for higher levels
   * of implementation to only render edges when their arrowheads do not
   * become cluttered.  For higher levels to be able to do this, I've provided
   * detailed information regarding the size and position of arrowheads (see
   * table below).<p>
   * The arrow types must each be one of the ARROW_* constants.
   * The arrow at endpoint 1 is always on top of the arrow at endpoint 0
   * because the arrow at endpoint 0 gets rendered first.<p>
   * There are some constraints on the ratio of edge thickness to arrow
   * size, listed in the table below.  Note that it is enough for this ratio
   * to be less than or equal to 0.47 for all of the specific arrowhead
   * constraints to pass.
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>arrow type</th>     <th>placement of arrow</th>          </tr>
   *   <tr>  <td>ARROW_NONE</td>     <td>the edge line segment has
   *                                   endpoint specified, and
   *                                   the line segment has a round
   *                                   end (center of round
   *                                   semicircle end exactly equal to
   *                                   endpoint specified); arrow size
   *                                   and arrow color are ignored</td>   </tr>
   *   <tr>  <td>ARROW_DISC</td>     <td>the disc arrowhead is placed
   *                                   such that its center is at the
   *                                   specified endpoint; the diameter
   *                                   of the disk is the arrow size
   *                                   specified; the arrow size cannot
   *                                   be less than edge thickness</td>   </tr>
   *   <tr>  <td>ARROW_DELTA</td>    <td>the sharp tip of the arrowhead
   *                                   is exactly at the endpint
   *                                   specified; the delta is as wide as
   *                                   the arrow size specified and twice
   *                                   that in length; the ratio of edge
   *                                   thickness to arrow size cannot
   *                                   exceed 4/sqrt(17)</td>             </tr>
   *   <tr>  <td>ARROW_DIAMOND</td>  <td>the sharp tip of the arrowhead
   *                                   is exactly at the endpoint
   *                                   specified; the diamond is as wide as
   *                                   the arrow size specified and twice
   *                                   that in length; the ratio of edge
   *                                   thickness to arrow size cannot
   *                                   exceed 2/sqrt(5)</td>              </tr>
   *   <tr>  <td>ARROW_TEE</td>      <td>the center of the tee intersection
   *                                   lies at the specified endpoint; the
   *                                   width of the top of the tee is one
   *                                   quarter of
   *                                   the arrow size specified, and the
   *                                   span of the top of the tee is
   *                                   four times the arrow size; the ratio
   *                                   of edge thickness to arrow
   *                                   size cannot exceed one-half</td>   </tr>
   *   <tr>  <td>ARROW_BIDIRECTIONAL</td>
   *                                 <td>either both arrowheads must be
   *                                   of this type of neither one must be
   *                                   of this type; bidirectional edges
   *                                   look completely different from other
   *                                   edges; arrow colors are completely
   *                                   ignored for this type of edge;
   *                                   the edge arrow is drawn such that
   *                                   it fits snugly inside of an
   *                                   ARROW_DELTA of specified size times
   *                                   two, where the delta's tip is at edge
   *                                   endpoint specified;
   *                                   the ratio of edge thickness
   *                                   to arrow size cannot exceed
   *                                   8/17</td>                          </tr>
   *   <tr>  <td>ARROW_MONO</td>     <td>either both arrowheads must be
   *                                   of this type of neither one must be
   *                                   of this type; mono edges look
   *                                   completely different from other edges
   *                                   because an arrowhead (an ARROW_DELTA)
   *                                   is placed such that its tip is in the
   *                                   middle of the edge
   *                                   segment, pointing from (x0,y0) to
   *                                   (x1,y1); the color
   *                                   and size of the first arrow (arrow0)
   *                                   are read and the color and size of the
   *                                   other arrow are completely ignored;
   *                                   the ratio of edge thickness to arrow
   *                                   size cannot exceed one</td>        </tr>
   * </table></blockquote><p>
   * Note that if the edge segment length is zero then nothing gets
   * rendered.<p>
   * This method will not work unless clear() has been called at least once
   * previously.
   * @param dashLength a positive value representing the length of dashes
   *   on the edge, or zero to indicate that the edge is solid.
   * @exception IllegalArgumentException if edgeThickness is less than zero,
   *   if dashLength is less than zero, or if any one of the arrow sizes
   *   does not meet specified criteria.
   */
  public final void drawEdgeFull(final byte arrowType0,
                                 final float arrow0Size,
                                 final Color arrow0Color,
                                 final byte arrowType1,
                                 final float arrow1Size,
                                 final Color arrow1Color,
                                 final float x0, final float y0,
                                 final float x1, final float y1,
                                 final float edgeThickness,
                                 final Color edgeColor,
                                 final float dashLength)
  {
    if (m_debug) {
      edgeFullDebug(false, arrowType0, arrow0Size, arrowType1, arrow1Size,
                    edgeThickness, dashLength, null, 0.0d); }
    // End debug.  Here the real code begins.

    final double len = Math.sqrt((((double) x1) - x0) * (((double) x1) - x0) +
                                 (((double) y1) - y0) * (((double) y1) - y0));
    // If the length of the edge is zero we're going to skip completely over
    // all rendering.
    if (len == 0.0d) return;

    if (arrowType0 == ARROW_BIDIRECTIONAL) { // Draw and return.
      final double a = (6.0d + Math.sqrt(17.0d) / 2.0d) * edgeThickness;
      m_path2d.reset();
      final double f = (-17.0d / 8.0d) * edgeThickness + arrow0Size;
      m_path2d.moveTo((float) (a + 4.0d * f),
                      (float) (f + 1.5d * edgeThickness));
      m_path2d.lineTo((float) a, (float) (1.5d * edgeThickness));
      if (2.0d * a < len) {
        m_path2d.lineTo((float) (len - a), (float) (1.5d * edgeThickness)); }
      final double g = (-17.0d / 8.0d) * edgeThickness + arrow1Size;
      m_path2d.moveTo((float) (len - (a + 4.0d * g)),
                      (float) (-g + -1.5d * edgeThickness));
      m_path2d.lineTo((float) (len - a), (float) (-1.5d * edgeThickness));
      if (2.0d * a < len) {
        m_path2d.lineTo((float) a, (float) (-1.5d * edgeThickness)); }
      // I want the transform to first rotate, then translate.
      final double cosTheta = (((double) x1) - x0) / len;
      final double sinTheta = (((double) y1) - y0) / len;
      m_xformUtil.setTransform
        (cosTheta, sinTheta, -sinTheta, cosTheta, x0, y0);
      m_path2d.transform(m_xformUtil);
      // Right now, we're drawing bidirectional edges with butt ends instead
      // of round ends.  If we wanted round ends, substantially more work
      // would have to be done for the following reasons.  For dashed
      // lines, we need butt ends to be consistent with the appearance of
      // dashes with other edge types.  Therefore, if we wanted round
      // ends, we would have to create shapes for these ends and render them
      // separately.  Computing the round end for the angled arrow segment
      // is not easy, especially for the case where this segment is very
      // short relative to the edge thickness.  I prefer to keep things
      // simple in the code at the expense of sacrificing a little bit of
      // prettiness.
      setStroke(edgeThickness, dashLength, BasicStroke.CAP_BUTT, false);
      m_g2d.setColor(edgeColor);
      m_g2d.draw(m_path2d);
      return; }

    if (arrowType0 == ARROW_MONO) { // To be implemented.
      throw new IllegalStateException("mono edges not implemented yet"); }

    final double x0Adj;
    final double y0Adj;
    final double x1Adj;
    final double y1Adj;
    { // Render the line segment if necessary.
      final double t0 = getT(arrowType0) * arrow0Size / len;
      x0Adj = t0 * (((double) x1) - x0) + x0;
      y0Adj = t0 * (((double) y1) - y0) + y0;
      final double t1 = getT(arrowType1) * arrow1Size / len;
      x1Adj = t1 * (((double) x0) - x1) + x1;
      y1Adj = t1 * (((double) y0) - y1) + y1;
      // If the vector point0->point1 is pointing opposite to
      // adj0->adj1, then don't render the line segment.
      // Dot product determines this.
      if ((((double) x1) - x0) * (x1Adj - x0Adj) +
          (((double) y1) - y0) * (y1Adj - y0Adj) > 0.0d) {
        // Must render the line segment.
        final boolean simpleSegment = arrowType0 == ARROW_NONE &&
          arrowType1 == ARROW_NONE && dashLength == 0.0f;
        setStroke(edgeThickness, dashLength,
                  simpleSegment ? BasicStroke.CAP_ROUND :
                  BasicStroke.CAP_BUTT, false);
        m_line2d.setLine(x0Adj, y0Adj, x1Adj, y1Adj);
        m_g2d.setColor(edgeColor);
        m_g2d.draw(m_line2d);
        if (simpleSegment) { return; } }
    } // End rendering of line segment.

    // Using x0, x1, y0, and y1 instead of the "adjusted" endpoints is
    // accurate enough in computation of cosine and sine because the
    // length is guaranteed to be at least as large.  Remember that the
    // original endpoint values are specified as float whereas the adjusted
    // points are double.
    final double cosTheta = (((double) x0) - x1) / len;
    final double sinTheta = (((double) y0) - y1) / len;

    if (dashLength == 0.0f) { // Render arrow cap at point 0.
      final Shape arrow0Cap = computeUntransformedArrowCap
        (arrowType0, ((double) arrow0Size) / edgeThickness);
      if (arrow0Cap != null) {
        m_xformUtil.setTransform(cosTheta, sinTheta, -sinTheta, cosTheta,
                                 x0Adj, y0Adj);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(edgeThickness, edgeThickness);
        // The color is already set to edge color.
        m_g2d.fill(arrow0Cap);
        m_g2d.setTransform(m_currNativeXform); } }

    if (dashLength == 0.0f) { // Render arrow cap at point 1.
      final Shape arrow1Cap = computeUntransformedArrowCap
        (arrowType1, ((double) arrow1Size) / edgeThickness);
      if (arrow1Cap != null) {
        m_xformUtil.setTransform(-cosTheta, -sinTheta, sinTheta, -cosTheta,
                                 x1Adj, y1Adj);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(edgeThickness, edgeThickness);
        // The color is already set to edge color.
        m_g2d.fill(arrow1Cap);
        m_g2d.setTransform(m_currNativeXform); } }

    { // Render arrow at point 0.
      final Shape arrow0 = computeUntransformedArrow(arrowType0);
      if (arrow0 != null) {
        m_xformUtil.setTransform(cosTheta, sinTheta, -sinTheta, cosTheta,
                                 x0, y0);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(arrow0Size, arrow0Size);
        m_g2d.setColor(arrow0Color);
        m_g2d.fill(arrow0);
        m_g2d.setTransform(m_currNativeXform); }
    }

    { // Render arrow at point 1.
      final Shape arrow1 = computeUntransformedArrow(arrowType1);
      if (arrow1 != null) {
        m_xformUtil.setTransform(-cosTheta, -sinTheta, sinTheta, -cosTheta,
                                 x1, y1);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(arrow1Size, arrow1Size);
        m_g2d.setColor(arrow1Color);
        m_g2d.fill(arrow1);
        m_g2d.setTransform(m_currNativeXform); }
    }
  }

  public final void drawPolyEdgeFull(final byte arrowType0,
                                     final float arrow0Size,
                                     final Color arrow0Color,
                                     final byte arrowType1,
                                     final float arrow1Size,
                                     final Color arrow1Color,
                                     final float x0, final float y0,
                                     final EdgeAnchors anchors,
                                     final float x1, final float y1,
                                     final float edgeThickness,
                                     final Color edgeColor,
                                     final float dashLength,
                                     final double curveFactor)
  {
    if (m_debug) {
      edgeFullDebug(true, arrowType0, arrow0Size, arrowType1, arrow1Size,
                    edgeThickness, dashLength, anchors, curveFactor); }

    if (!computeCubicPolyEdgePath
        (arrowType0, arrowType0 == ARROW_NONE ? 0.0f : arrow0Size,
         arrowType1, arrowType1 == ARROW_NONE ? 0.0f : arrow1Size,
         x0, y0, anchors, x1, y1, curveFactor)) {
      // After filtering duplicate start and end points, there are less
      // than 3 total.
      if (m_edgePtsCount == 2) { // Draw an ordinary edge.
        drawEdgeFull(arrowType0, arrow0Size, arrow0Color,
                     arrowType1, arrow1Size, arrow1Color,
                     (float) m_edgePtsBuff[0], (float) m_edgePtsBuff[1],
                     (float) m_edgePtsBuff[2], (float) m_edgePtsBuff[3],
                     edgeThickness, edgeColor, dashLength); }
      return; }

    { // Render the edge polypath.
      final boolean simpleSegment = arrowType0 == ARROW_NONE &&
        arrowType1 == ARROW_NONE && dashLength == 0.0f;
      setStroke(edgeThickness, dashLength,
                simpleSegment ? BasicStroke.CAP_ROUND :
                BasicStroke.CAP_BUTT, false);
      // Set m_path2d to contain the cubic curves computed in m_edgePtsBuff.
      m_path2d.reset();
      m_path2d.moveTo((float) m_edgePtsBuff[2], (float) m_edgePtsBuff[3]);
      int inx = 4;
      final int count = (m_edgePtsCount - 1) * 6 - 2;
      while (inx < count) {
        m_path2d.curveTo
          ((float) m_edgePtsBuff[inx++], (float) m_edgePtsBuff[inx++],
           (float) m_edgePtsBuff[inx++], (float) m_edgePtsBuff[inx++],
           (float) m_edgePtsBuff[inx++], (float) m_edgePtsBuff[inx++]); }
      m_g2d.setColor(edgeColor);
      m_g2d.draw(m_path2d);
      if (simpleSegment) { return; }
      // We need to figure out the phase at the end of the cubic poly-path
      // for dashed segments.  I cannot find a Java API to do this; our best
      // bet would be to implement our own cubic curve length calculating
      // function, but our computation may not agree with BasicStroke's
      // computation.  So what we're going to do is always render the arrow
      // caps.
    }

    final double dx0 = m_edgePtsBuff[0] - m_edgePtsBuff[4];
    final double dy0 = m_edgePtsBuff[1] - m_edgePtsBuff[5];
    final double len0 = Math.sqrt(dx0 * dx0 + dy0 * dy0);
    final double cosTheta0 = dx0 / len0;
    final double sinTheta0 = dy0 / len0;

    final double dx1 = m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 2] -
      m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 6];
    final double dy1 = m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 1] -
      m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 5];
    final double len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
    final double cosTheta1 = dx1 / len1;
    final double sinTheta1 = dy1 / len1;

    if (dashLength == 0.0f)
    { // Render arrow cap at origin of poly path.
      final Shape arrow0Cap = computeUntransformedArrowCap
        (arrowType0, ((double) arrow0Size) / edgeThickness);
      if (arrow0Cap != null) {
        m_xformUtil.setTransform(cosTheta0, sinTheta0, -sinTheta0, cosTheta0,
                                 m_edgePtsBuff[2], m_edgePtsBuff[3]);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(edgeThickness, edgeThickness);
        // The color is already set to edge color.
        m_g2d.fill(arrow0Cap);
        m_g2d.setTransform(m_currNativeXform); }
    }

    if (dashLength == 0.0f)
    { // Render arrow cap at end of poly path.
      final Shape arrow1Cap = computeUntransformedArrowCap
        (arrowType1, ((double) arrow1Size) / edgeThickness);
      if (arrow1Cap != null) {
        m_xformUtil.setTransform(cosTheta1, sinTheta1, -sinTheta1, cosTheta1,
                                 m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 4],
                                 m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 3]);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(edgeThickness, edgeThickness);
        // The color is already set to edge color.
        m_g2d.fill(arrow1Cap);
        m_g2d.setTransform(m_currNativeXform); }
    }

    { // Render arrow at origin of poly path.
      final Shape arrow0 = computeUntransformedArrow(arrowType0);
      if (arrow0 != null) {
        m_xformUtil.setTransform(cosTheta0, sinTheta0, -sinTheta0, cosTheta0,
                                 m_edgePtsBuff[0], m_edgePtsBuff[1]);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(arrow0Size, arrow0Size);
        m_g2d.setColor(arrow0Color);
        m_g2d.fill(arrow0);
        m_g2d.setTransform(m_currNativeXform); }
    }

    { // Render arrow at end of poly path.
      final Shape arrow1 = computeUntransformedArrow(arrowType1);
      if (arrow1 != null) {
        m_xformUtil.setTransform(cosTheta1, sinTheta1, -sinTheta1, cosTheta1,
                                 m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 2],
                                 m_edgePtsBuff[(m_edgePtsCount - 1) * 6 - 1]);
        m_g2d.transform(m_xformUtil);
        m_g2d.scale(arrow1Size, arrow1Size);
        m_g2d.setColor(arrow1Color);
        m_g2d.fill(arrow1);
        m_g2d.setTransform(m_currNativeXform); }
    }
  }

  /**
   * Computes the path that an edge takes; this method is useful if a user
   * interface would allow user selection of edges, for example.  The returned
   * path is the path along the center of the edge segment, extending to the
   * points which specify the arrow locations.  Note that this path therefore
   * disregards edge thickness and arrow outline.
   * @param anchors specifies the edge anchors.
   * @param path the computed path is returned in this parameter.
   * @return true if and only if the specified edge would be drawn (which
   *   is if and only if any two points from the edge anchor set plus the
   *   beginning and end point are distinct); if false is returned, path is
   *   not set.
   */
  public final boolean getPolyEdgePath(final byte arrowType0,
                                       final float arrow0Size,
                                       final byte arrowType1,
                                       final float arrow1Size,
                                       final float x0, final float y0,
                                       final EdgeAnchors anchors,
                                       final float x1, final float y1,
                                       final double curveFactor,
                                       final GeneralPath path)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      switch (arrowType0) {
      case ARROW_NONE:
      case ARROW_DELTA:
      case ARROW_DIAMOND:
      case ARROW_DISC:
      case ARROW_TEE:
        break;
      case ARROW_BIDIRECTIONAL:
      case ARROW_MONO:
        throw new IllegalArgumentException
          ("ARROW_BIDIRECTIONAL and ARROW_MONO not supported in poly edges");
      default:
        throw new IllegalArgumentException("arrowType0 is not recognized"); }
      switch (arrowType1) {
      case ARROW_NONE:
      case ARROW_DELTA:
      case ARROW_DIAMOND:
      case ARROW_DISC:
      case ARROW_TEE:
        break;
      case ARROW_BIDIRECTIONAL:
      case ARROW_MONO:
        throw new IllegalArgumentException
          ("ARROW_BIDIRECTIONAL and ARROW_MONO not supported in poly edges");
      default:
        throw new IllegalArgumentException("arrowType1 is not recognized"); }
      if (anchors.numRemaining() > 100)
        throw new IllegalArgumentException
          ("at most 100 edge anchors can be specified");
      if (!(curveFactor == CURVE_ELLIPTICAL || curveFactor == CURVE_NATURAL))
        throw new IllegalArgumentException
          ("curveFactor should be either CURVE_ELLIPTICAL or CURVE_NATURAL"); }

    return false;
  }

  private final void edgeFullDebug(final boolean polyEdge,
                                   final byte arrowType0,
                                   final float arrow0Size,
                                   final byte arrowType1,
                                   final float arrow1Size,
                                   final float edgeThickness,
                                   final float dashLength,
                                   final EdgeAnchors anchors,
                                   final double curveFactor)
  {
    if (!EventQueue.isDispatchThread())
      throw new IllegalStateException
        ("calling thread is not AWT event dispatcher");
    if (!m_cleared) throw new IllegalStateException
                      ("clear() has not been called previously");
    if (!(edgeThickness >= 0.0f))
      throw new IllegalArgumentException("edgeThickness < 0");
    if (!(dashLength >= 0.0f))
      throw new IllegalArgumentException("dashLength < 0");
    switch (arrowType0) {
    case ARROW_NONE:
      break;
    case ARROW_DELTA:
      if (!(Math.sqrt(17.0d) * edgeThickness <= 4.0d * arrow0Size))
        throw new IllegalArgumentException
          ("for ARROW_DELTA e/s is greater than 4/sqrt(17)");
      break;
    case ARROW_DIAMOND:
      if (!(Math.sqrt(5.0d) * edgeThickness <= 2.0d * arrow0Size))
        throw new IllegalArgumentException
          ("for ARROW_DIAMOND e/s is greater than 2/sqrt(5)");
      break;
    case ARROW_DISC:
      if (!(edgeThickness <= arrow0Size))
        throw new IllegalArgumentException
          ("for ARROW_DISC e/s is greater than 1");
      break;
    case ARROW_TEE:
      if (!(((double) edgeThickness) <= 0.5d * arrow0Size))
        throw new IllegalArgumentException
          ("for ARROW_TEE e/s is greater than 1/2");
      break;
    case ARROW_BIDIRECTIONAL:
      if (polyEdge)
        throw new IllegalArgumentException
          ("ARROW_BIDIRECTIONAL not supported for poly edges");
      if (!(17.0d * edgeThickness <= 8.0d * arrow0Size))
        throw new IllegalArgumentException
          ("for ARROW_BIDIRECTIONAL e/s is greater than 8/17");
      if (arrowType1 != ARROW_BIDIRECTIONAL)
        throw new IllegalArgumentException
          ("either both or neither arrows must be ARROW_BIDIRECTIONAL");
      break;
    case ARROW_MONO:
      if (polyEdge)
        throw new IllegalArgumentException
          ("ARROW_MONO not supported for poly edges");
      if (!(edgeThickness <= arrow0Size))
        throw new IllegalArgumentException
          ("for ARROW_MONO e/s is greater than 1");
      if (arrowType1 != ARROW_MONO)
        throw new IllegalArgumentException
          ("either both or neither arrows must be ARROW_MONO");
      break;
    default:
      throw new IllegalArgumentException("arrowType0 is not recognized"); }
    switch (arrowType1) {
    case ARROW_NONE:
      break;
    case ARROW_DELTA:
      if (!(Math.sqrt(17.0d) * edgeThickness <= 4.0d * arrow1Size))
        throw new IllegalArgumentException
          ("for ARROW_DELTA e/s is greater than 4/sqrt(17)");
      break;
    case ARROW_DIAMOND:
      if (!(Math.sqrt(5.0d) * edgeThickness <= 2.0d * arrow1Size))
        throw new IllegalArgumentException
          ("for ARROW_DIAMOND e/s is greater than 2/sqrt(5)");
      break;
    case ARROW_DISC:
      if (!(edgeThickness <= arrow1Size))
        throw new IllegalArgumentException
          ("for ARROW_DISC e/s is greater than 1");
      break;
    case ARROW_TEE:
      if (!(((double) edgeThickness) <= 0.5d * arrow1Size))
        throw new IllegalArgumentException
          ("for ARROW_TEE e/s is greater than 1/2");
      break;
    case ARROW_BIDIRECTIONAL:
      if (!(17.0d * edgeThickness <= 8.0d * arrow1Size))
        throw new IllegalArgumentException
          ("for ARROW_BIDIRECTIONAL e/s is greater than 8/17");
      if (arrowType0 != ARROW_BIDIRECTIONAL)
        throw new IllegalArgumentException
          ("either both or neither arrows must be ARROW_BIDIRECTIONAL");
      break;
    case ARROW_MONO:
      if (arrowType0 != ARROW_MONO)
        throw new IllegalArgumentException
          ("either both or neither arrows must be ARROW_MONO");
      break;
    default:
      throw new IllegalArgumentException("arrowType1 is not recognized"); }
    if (polyEdge) {
      if (anchors.numRemaining() > 100)
        throw new IllegalArgumentException
          ("at most 100 edge anchors can be specified");
      if (!(curveFactor == CURVE_ELLIPTICAL || curveFactor == CURVE_NATURAL))
        throw new IllegalArgumentException
          ("curveFactor should be either CURVE_ELLIPTICAL or CURVE_NATURAL"); }
  }

  /*
   * Returns non-null if and only if an arrow is necessary for the arrow
   * type specified.  m_path2d and m_ellp2d may be mangled as a side effect.
   * arrowType must be one of the primitive arrow types or ARROW_NONE (no
   * ARROW_BIDIRECTIONAL or ARROW_MONO allowed).
   */
  private final Shape computeUntransformedArrow(final byte arrowType)
  {
    switch (arrowType) {
    case ARROW_NONE:
      return null;
    case ARROW_DELTA:
      m_path2d.reset();
      m_path2d.moveTo(-2.0f, -0.5f);
      m_path2d.lineTo(0.0f, 0.0f);
      m_path2d.lineTo(-2.0f, 0.5f);
      m_path2d.closePath();
      return m_path2d;
    case ARROW_DIAMOND:
      m_path2d.reset();
      m_path2d.moveTo(-1.0f, -0.5f);
      m_path2d.lineTo(0.0f, 0.0f);
      m_path2d.lineTo(-1.0f, 0.5f);
      m_path2d.lineTo(-2.0f, 0.0f);
      m_path2d.closePath();
      return m_path2d;
    case ARROW_DISC:
      m_ellp2d.setFrame(-0.5d, -0.5d, 1.0d, 1.0d);
      return m_ellp2d;
    default: // ARROW_TEE.
      m_path2d.reset();
      m_path2d.moveTo(-0.125f, -2.0f);
      m_path2d.lineTo(0.125f, -2.0f);
      m_path2d.lineTo(0.125f, 2.0f);
      m_path2d.lineTo(-0.125f, 2.0f);
      m_path2d.closePath();
      return m_path2d; }
  }

  /*
   * The ratio parameter specifies the ratio of arrow size (disc
   * diameter) to edge thickness (only used for some arrow types).
   * Returns non-null if and only if a cap is necessary for the arrow type
   * specified.  If non-null is returned then m_path2d and
   * m_arc2d may be mangled as a side effect.
   * arrowType must be one of the primitive arrow types or ARROW_NONE (no
   * ARROW_BIDIRECTIONAL or ARROW_MONO allowed).
   */
  private final Shape computeUntransformedArrowCap(final byte arrowType,
                                                         final double ratio)
  {
    switch (arrowType) {
    case ARROW_NONE:
      m_arc2d.setArc(-0.5d, -0.5d, 1.0d, 1.0d, 270.0d, 180.0d, Arc2D.CHORD);
      return m_arc2d;
    case ARROW_DELTA:
      return null;
    case ARROW_DIAMOND:
      m_path2d.reset();
      m_path2d.moveTo(0.0f, -0.5f);
      m_path2d.lineTo(1.0f, -0.5f);
      m_path2d.lineTo(0.0f, 0.0f);
      m_path2d.lineTo(1.0f, 0.5f);
      m_path2d.lineTo(0.0f, 0.5f);
      m_path2d.closePath();
      return m_path2d;
    case ARROW_DISC:
      final double theta = Math.toDegrees(Math.asin(1.0d / ratio));
      m_arc2d.setArc(0.0d, ratio / -2.0d, ratio, ratio, 180.0d - theta,
                     theta * 2, Arc2D.OPEN);
      m_path2d.reset();
      m_path2d.append(m_arc2d, false);
      m_path2d.lineTo(0.0f, 0.5f);
      m_path2d.lineTo(0.0f, -0.5f);
      m_path2d.closePath();
      return m_path2d;
    default: // ARROW_TEE.
      return null; }
  }

  /*
   * arrowType must be one of the primitive arrow types or ARROW_NONE (no
   * ARROW_BIDIRECTIONAL or ARROW_MONO allowed).
   */
  private final static double getT(final byte arrowType)
  { // I could implement this as an array instead of a switch statement.
    switch (arrowType) {
    case ARROW_NONE:
      return 0.0d;
    case ARROW_DELTA:
      return 2.0d;
    case ARROW_DIAMOND:
      return 2.0d;
    case ARROW_DISC:
      return 0.5d;
    default: // ARROW_TEE.
      return 0.125d; }
  }

  private final float[] m_floatBuff = new float[2];
  private final double[] m_edgePtsBuff = new double[303];
  private int m_edgePtsCount; // Number of points stored in m_edgePtsBuff.

  /*
   * If arrowType0 is ARROW_NONE, arrow0Size should be zero.
   * If arrowType1 is ARROW_NONE, arrow1Size should be zero.
   * curveFactor should be CURVE_ELLIPTICAL or CURVE_NATURAL.
   */
  private final boolean computeCubicPolyEdgePath(final byte arrowType0,
                                                 final float arrow0Size,
                                                 final byte arrowType1,
                                                 final float arrow1Size,
                                                 final float x0,
                                                 final float y0,
                                                 final EdgeAnchors anchors,
                                                 final float x1,
                                                 final float y1,
                                                 final double curveFactor)
  {
    m_edgePtsBuff[0] = x0;
    m_edgePtsBuff[1] = y0;
    m_edgePtsCount = 1;
    while (anchors.numRemaining() > 0) {
      anchors.nextAnchor(m_floatBuff, 0);
      if (!(m_floatBuff[0] == x0 && m_floatBuff[1] == y0)) {
        m_edgePtsBuff[2] = m_floatBuff[0];
        m_edgePtsBuff[3] = m_floatBuff[1];
        m_edgePtsCount = 2;
        break; } }
    while (anchors.numRemaining() > 0) {
      anchors.nextAnchor(m_floatBuff, 0);
      // Duplicate anchors are allowed.
      m_edgePtsBuff[m_edgePtsCount * 2] = m_floatBuff[0];
      m_edgePtsBuff[m_edgePtsCount * 2 + 1] = m_floatBuff[1];
      m_edgePtsCount++; }
    m_edgePtsBuff[m_edgePtsCount * 2] = x1;
    m_edgePtsBuff[m_edgePtsCount * 2 + 1] = y1;
    m_edgePtsCount++;
    while (m_edgePtsCount > 1) {
      if (m_edgePtsBuff[m_edgePtsCount * 2 - 2] == // Last X coord equals
          m_edgePtsBuff[m_edgePtsCount * 2 - 4] && // eecond-to-last X coord.
          m_edgePtsBuff[m_edgePtsCount * 2 - 1] == // Last Y coord equals
          m_edgePtsBuff[m_edgePtsCount * 2 - 3]) { // second-to-last Y coord.
        m_edgePtsCount--; }
      else { break; } }
    if (m_edgePtsCount < 3) { return false; }
    final int edgePtsCount = m_edgePtsCount;

    { // First set the three points related to point 1.
      m_edgePtsCount--;
      m_edgePtsBuff[m_edgePtsCount * 6 - 2] =
        m_edgePtsBuff[m_edgePtsCount * 2];
      m_edgePtsBuff[m_edgePtsCount * 6 - 1] =
        m_edgePtsBuff[m_edgePtsCount * 2 + 1];
      double dx = m_edgePtsBuff[m_edgePtsCount * 2 - 2] -
        m_edgePtsBuff[m_edgePtsCount * 2];
      double dy = m_edgePtsBuff[m_edgePtsCount * 2 - 1] -
        m_edgePtsBuff[m_edgePtsCount * 2 + 1];
      double len = Math.sqrt(dx * dx + dy * dy);
      dx /= len; dy /= len; // Normalized.
      m_edgePtsBuff[m_edgePtsCount * 6 - 4] =
        m_edgePtsBuff[m_edgePtsCount * 6 - 2] +
        dx * arrow1Size * getT(arrowType1);
      m_edgePtsBuff[m_edgePtsCount * 6 - 3] =
        m_edgePtsBuff[m_edgePtsCount * 6 - 1] +
        dy * arrow1Size * getT(arrowType1);
      double candX1 = m_edgePtsBuff[m_edgePtsCount * 6 - 4] +
        dx * 2.0d * arrow1Size;
      double candX2 = m_edgePtsBuff[m_edgePtsCount * 6 - 4] +
        curveFactor * (m_edgePtsBuff[m_edgePtsCount * 2 - 2] -
                       m_edgePtsBuff[m_edgePtsCount * 6 - 4]);
      if (Math.abs(candX1 - m_edgePtsBuff[m_edgePtsCount * 2]) >
          Math.abs(candX2 - m_edgePtsBuff[m_edgePtsCount * 2])) {
        m_edgePtsBuff[m_edgePtsCount * 6 - 6] = candX1; }
      else { m_edgePtsBuff[m_edgePtsCount * 6 - 6] = candX2; }
      double candY1 = m_edgePtsBuff[m_edgePtsCount * 6 - 3] +
        dy * 2.0d * arrow1Size;
      double candY2 = m_edgePtsBuff[m_edgePtsCount * 6 - 3] +
        curveFactor * (m_edgePtsBuff[m_edgePtsCount * 2 - 1] -
                       m_edgePtsBuff[m_edgePtsCount * 6 - 3]);
      if (Math.abs(candY1 - m_edgePtsBuff[m_edgePtsCount * 2 + 1]) >
          Math.abs(candY2 - m_edgePtsBuff[m_edgePtsCount * 2 + 1])) {
        m_edgePtsBuff[m_edgePtsCount * 6 - 5] = candY1; }
      else { m_edgePtsBuff[m_edgePtsCount * 6 - 5] = candY2; }
    }

    // Next set all edge anchor information.
    while (m_edgePtsCount > 2) {
      m_edgePtsCount--;
      final double midX = (m_edgePtsBuff[m_edgePtsCount * 2 - 2] +
                           m_edgePtsBuff[m_edgePtsCount * 2]) / 2.0d;
      final double midY = (m_edgePtsBuff[m_edgePtsCount * 2 - 1] +
                           m_edgePtsBuff[m_edgePtsCount * 2 + 1]) / 2.0d;
      m_edgePtsBuff[m_edgePtsCount * 6 - 2] = midX +
        (m_edgePtsBuff[m_edgePtsCount * 2] - midX) * curveFactor;
      m_edgePtsBuff[m_edgePtsCount * 6 - 1] = midY +
        (m_edgePtsBuff[m_edgePtsCount * 2 + 1] - midY) * curveFactor;
      m_edgePtsBuff[m_edgePtsCount * 6 - 4] = midX;
      m_edgePtsBuff[m_edgePtsCount * 6 - 3] = midY;
      m_edgePtsBuff[m_edgePtsCount * 6 - 6] = midX +
        (m_edgePtsBuff[m_edgePtsCount * 2 - 2] - midX) * curveFactor;
      m_edgePtsBuff[m_edgePtsCount * 6 - 5] = midY +
        (m_edgePtsBuff[m_edgePtsCount * 2 - 1] - midY) * curveFactor; }

    { // Last set the three points related to point 0.
      double dx = m_edgePtsBuff[2] - m_edgePtsBuff[0];
      double dy = m_edgePtsBuff[3] - m_edgePtsBuff[1];
      double len = Math.sqrt(dx * dx + dy * dy);
      dx /= len; dy /= len; // Normalized.
      double segStartX = m_edgePtsBuff[0] + dx * arrow0Size * getT(arrowType0);
      double segStartY = m_edgePtsBuff[1] + dy * arrow0Size * getT(arrowType0);
      double candX1 = segStartX + dx * 2.0d * arrow0Size;
      double candX2 = segStartX + curveFactor * (m_edgePtsBuff[2] - segStartX);
      if (Math.abs(candX1 - m_edgePtsBuff[0]) >
          Math.abs(candX2 - m_edgePtsBuff[0])) {
        m_edgePtsBuff[4] = candX1; }
      else { m_edgePtsBuff[4] = candX2; }
      double candY1 = segStartY + dy * 2.0d * arrow0Size;
      double candY2 = segStartY + curveFactor * (m_edgePtsBuff[3] - segStartY);
      if (Math.abs(candY1 - m_edgePtsBuff[1]) >
          Math.abs(candY2 - m_edgePtsBuff[1])) {
        m_edgePtsBuff[5] = candY1; }
      else { m_edgePtsBuff[5] = candY2; }
      m_edgePtsBuff[2] = segStartX;
      m_edgePtsBuff[3] = segStartY;
    }

    m_edgePtsCount = edgePtsCount;
    return true;
  }

  // The three following member variables shall only be referenced from
  // the scope of setStroke() definition.
  private float m_currStrokeWidth;
  private final float[] m_currDash = new float[] { 0.0f, 0.0f };
  private int m_currCapType;

  private final void setStroke(final float width, final float dashLength,
                               final int capType, final boolean ignoreCache)
  {
    if ((!ignoreCache) && width == m_currStrokeWidth &&
        dashLength == m_currDash[0] && capType == m_currCapType) return;
    m_currStrokeWidth = width;
    m_currDash[0] = dashLength;
    m_currDash[1] = dashLength;
    m_currCapType = capType;
    // Unfortunately, BasicStroke is not mutable.  So we have to construct
    // lots of new strokes if they constantly change.
    if (m_currDash[0] == 0.0f)
      m_g2d.setStroke(new BasicStroke(width, capType,
                                      BasicStroke.JOIN_BEVEL, 10.0f));
    else
      m_g2d.setStroke(new BasicStroke(width, capType,
                                      BasicStroke.JOIN_BEVEL, 10.0f,
                                      m_currDash, 0.0f));
  }

  // The following three member variables shall only be accessed from the
  // scope of computeEdgeIntersection() definition.
  private final double[] m_fooPolyCoords =
    new double[CUSTOM_SHAPE_MAX_VERTICES * 4];
  private final double[] m_foo2PolyCoords =
    new double[CUSTOM_SHAPE_MAX_VERTICES * 4];
  private final boolean[] m_fooRoundedCorners =
    new boolean[CUSTOM_SHAPE_MAX_VERTICES];

  /**
   * There is a constraint that only applies to SHAPE_ROUNDED_RECTANGLE
   * which imposes that the maximum of the width and height be strictly
   * less than twice the minimum of the width and height of the node.<p>
   */
  public final boolean computeEdgeIntersection(final byte nodeShape,
                                               final float xMin,
                                               final float yMin,
                                               final float xMax,
                                               final float yMax,
                                               final float offset,
                                               final float ptX,
                                               final float ptY,
                                               final float[] returnVal)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!(xMin < xMax)) throw new IllegalArgumentException
                            ("xMin not less than xMax");
      if (!(yMin < yMax)) throw new IllegalArgumentException
                            ("yMin not less than yMax");
      if (!(offset >= 0.0f))
        throw new IllegalArgumentException("offset < 0");
      if (nodeShape == SHAPE_ROUNDED_RECTANGLE) {
        final double width = ((double) xMax) - xMin;
        final double height = ((double) yMax) - yMin;
        if (!(Math.max(width, height) < 2.0d * Math.min(width, height)))
          throw new IllegalArgumentException
            ("rounded rectangle does not meet constraint " +
             "max(width, height) < 2 * min(width, height)"); } }
    final double centerX = (((double) xMin) + xMax) / 2.0d;
    final double centerY = (((double) yMin) + yMax) / 2.0d;

    if (nodeShape == SHAPE_ELLIPSE) {
      // First, compute the actual intersection of the edge with the
      // ellipse, if it exists.  We will use this intersection point
      // regardless of whether or not offset is zero.
      // For nonzero offsets on the ellipse, use tangent lines to approximate
      // intersection with offset instead of solving a quartic equation.
      final double ptPrimeX = ptX - centerX;
      final double ptPrimeY = ptY - centerY;
      final double ellpW = ((double) xMax) - xMin;
      final double ellpH = ((double) yMax) - yMin;
      final double xScaleFactor = 2.0d / ellpW;
      final double yScaleFactor = 2.0d / ellpH;
      final double xformedPtPrimeX = ptPrimeX * xScaleFactor;
      final double xformedPtPrimeY = ptPrimeY * yScaleFactor;
      final double xformedDist = Math.sqrt(xformedPtPrimeX * xformedPtPrimeX +
                                           xformedPtPrimeY * xformedPtPrimeY);
      final double xsectXformedPtPrimeX = xformedPtPrimeX / xformedDist;
      final double xsectXformedPtPrimeY = xformedPtPrimeY / xformedDist;
      final double tangentXformedPtPrimeX =
        xsectXformedPtPrimeX + xsectXformedPtPrimeY;
      final double tangentXformedPtPrimeY =
        xsectXformedPtPrimeY - xsectXformedPtPrimeX;
      final double xsectPtPrimeX = xsectXformedPtPrimeX / xScaleFactor;
      final double xsectPtPrimeY = xsectXformedPtPrimeY / yScaleFactor;
      final double tangentPtPrimeX = tangentXformedPtPrimeX / xScaleFactor;
      final double tangentPtPrimeY = tangentXformedPtPrimeY / yScaleFactor;
      final double vTangentX = tangentPtPrimeX - xsectPtPrimeX;
      final double vTangentY = tangentPtPrimeY - xsectPtPrimeY;
      final double tanLen = Math.sqrt(vTangentX * vTangentX +
                                      vTangentY * vTangentY);
      final double distPtPrimeToTangent =
        (vTangentX * ptPrimeY - vTangentY * ptPrimeX +
         xsectPtPrimeX * tangentPtPrimeY - tangentPtPrimeX * xsectPtPrimeY) /
        tanLen;
      if (distPtPrimeToTangent < offset) { // This includes cases where
        // distPtPrimeToTangent is negative, which means that the true
        // intersection point lies inside the ellipse (no intersection even
        // with zero offset).
        return false; }
      if (distPtPrimeToTangent == 0.0d) { // Therefore offset is zero also.
        returnVal[0] = (float) (xsectPtPrimeX + centerX);
        returnVal[1] = (float) (xsectPtPrimeY + centerY);
        return true; }
      // Even if offset is zero, do extra computation for sake of simple code.
      final double multFactor = offset / distPtPrimeToTangent;
      returnVal[0] = (float)
        (centerX +
         (xsectPtPrimeX + multFactor * (ptPrimeX - xsectPtPrimeX)));
      returnVal[1] = (float)
        (centerY +
         (xsectPtPrimeY + multFactor * (ptPrimeY - xsectPtPrimeY)));
      return true; }

    else { // Not ellipse.
      final double trueOffset;
      if (nodeShape == SHAPE_ROUNDED_RECTANGLE) {
        final double radius = Math.max(((double) xMax) - xMin,
                                       ((double) yMax) - yMin) / 4.0d;
        // One of our constraints is that for rounded rectangle,
        // max(width, height) < 2 * min(width, height) in 32 bit floating
        // point world.  Therefore, with 64 bits of precision, the rectangle
        // calculated below does not degenerate in width or height.
        getShape(SHAPE_RECTANGLE, radius + xMin, radius + yMin,
                 -radius + xMax, -radius + yMax);
        trueOffset = radius + offset; }
      else {
        // This next method call has the side effect of setting m_polyCoords
        // and m_polyNumPoints - this is all that we are going to use.
        getShape(nodeShape, xMin, yMin, xMax, yMax);
        trueOffset = offset; }

      if (trueOffset == 0.0d) {
        final int twicePolyNumPoints = m_polyNumPoints * 2;
        for (int i = 0; i < twicePolyNumPoints;) {
          final double x0 = m_polyCoords[i++];
          final double y0 = m_polyCoords[i++];
          final double x1 = m_polyCoords[i % twicePolyNumPoints];
          final double y1 = m_polyCoords[(i + 1) % twicePolyNumPoints];
          if (segmentIntersection(m_ptsBuff, ptX, ptY, centerX, centerY,
                                  x0, y0, x1, y1)) {
            returnVal[0] = (float) m_ptsBuff[0];
            returnVal[1] = (float) m_ptsBuff[1];
            return true; } }
        return false; }

      // The rest of this code is the polygonal case where offset is nonzero.
      for (int i = 0; i < m_polyNumPoints; i++) {
        final double x0 = m_polyCoords[i * 2];
        final double y0 = m_polyCoords[i * 2 + 1];
        final double x1 = m_polyCoords[(i * 2 + 2) % (m_polyNumPoints * 2)];
        final double y1 = m_polyCoords[(i * 2 + 3) % (m_polyNumPoints * 2)];
        final double vX = x1 - x0;
        final double vY = y1 - y0;
        final double len = Math.sqrt(vX * vX + vY * vY);
        final double vNormX = vX / len;
        final double vNormY = vY / len;
        m_fooPolyCoords[i * 4] = x0 + vNormY * trueOffset;
        m_fooPolyCoords[i * 4 + 1] = y0 - vNormX * trueOffset;
        m_fooPolyCoords[i * 4 + 2] = x1 + vNormY * trueOffset;
        m_fooPolyCoords[i * 4 + 3] = y1 - vNormX * trueOffset; }
      int inx = 0;
      for (int i = 0; i < m_polyNumPoints; i++) {
        if (segmentIntersection // We could perhaps use the sign of a cross
            (m_ptsBuff,         // product to perform this test quicker.
             m_fooPolyCoords[i * 4 + 2], // Because non-convex polygons are
             m_fooPolyCoords[i * 4 + 3], // rare, we will almost never use
             m_fooPolyCoords[i * 4],     // the computed intersection point.
             m_fooPolyCoords[i * 4 + 1],
             m_fooPolyCoords[(i * 4 + 4) % (m_polyNumPoints * 4)],
             m_fooPolyCoords[(i * 4 + 5) % (m_polyNumPoints * 4)],
             m_fooPolyCoords[(i * 4 + 6) % (m_polyNumPoints * 4)],
             m_fooPolyCoords[(i * 4 + 7) % (m_polyNumPoints * 4)])) {
          m_foo2PolyCoords[inx++] = m_ptsBuff[0];
          m_foo2PolyCoords[inx++] = m_ptsBuff[1];
          m_fooRoundedCorners[i] = false; }
        else {
          m_foo2PolyCoords[inx++] = m_fooPolyCoords[i * 4 + 2];
          m_foo2PolyCoords[inx++] = m_fooPolyCoords[i * 4 + 3];
          m_foo2PolyCoords[inx++] =
            m_fooPolyCoords[(i * 4 + 4) % (m_polyNumPoints * 4)];
          m_foo2PolyCoords[inx++] =
            m_fooPolyCoords[(i * 4 + 5) % (m_polyNumPoints * 4)];
          m_fooRoundedCorners[i] = true; } }
      final int foo2Count = inx;
      inx = 0;
      for (int i = 0; i < m_polyNumPoints; i++) {
        if (m_fooRoundedCorners[i]) {
          if (segmentIntersection
              (m_ptsBuff, ptX, ptY, centerX, centerY,
               m_foo2PolyCoords[inx++], m_foo2PolyCoords[inx++],
               m_foo2PolyCoords[inx], m_foo2PolyCoords[inx + 1])) {
            final double segXsectX = m_ptsBuff[0];
            final double segXsectY = m_ptsBuff[1];
            final int numXsections = bad_circleIntersection
              (m_ptsBuff, ptX, ptY, centerX, centerY,
               m_polyCoords[2 * ((i + 1) % m_polyNumPoints)],
               m_polyCoords[2 * ((i + 1) % m_polyNumPoints) + 1],
               trueOffset);
            // We don't expect tangential intersections because of
            // constraints on allowed polygons.  Therefore, if the circle
            // intersects the edge segment in only one point, then that
            // intersection point is the "outer arc" only if the edge segment
            // intersection point with the corner polygon segment (the arc
            // approximation) lies between the center of the polygon and
            // this one circle intersection point.
            if (numXsections == 2 ||
                (numXsections == 1 &&
                 Math.min(centerX, m_ptsBuff[0]) <= segXsectX &&
                 segXsectX <= Math.max(centerX, m_ptsBuff[0]) &&
                 Math.min(centerY, m_ptsBuff[1]) <= segXsectY &&
                 segXsectY <= Math.max(centerY, m_ptsBuff[1]))) {
              returnVal[0] = (float) m_ptsBuff[0]; // The first returnVal is
              returnVal[1] = (float) m_ptsBuff[1]; // closer to (ptX, ptY);
                                                   // see API.
              return true; }
            else {
              // The edge segment didn't quite make it to the outer section
              // of the circle; only the inner part was intersected.
              return false; } }
          else if (segmentIntersection // Test against the true line segment
                   (m_ptsBuff,         // that comes after the arc.
                    ptX, ptY, centerX, centerY,
                    m_foo2PolyCoords[inx++], m_foo2PolyCoords[inx++],
                    m_foo2PolyCoords[inx % foo2Count],
                    m_foo2PolyCoords[(inx + 1) % foo2Count])) {
            returnVal[0] = (float) m_ptsBuff[0];
            returnVal[1] = (float) m_ptsBuff[1];
            return true; } }
        else { // Not a rounded corner here.
          if (segmentIntersection
              (m_ptsBuff, ptX, ptY, centerX, centerY,
               m_foo2PolyCoords[inx++], m_foo2PolyCoords[inx++],
               m_foo2PolyCoords[inx % foo2Count],
               m_foo2PolyCoords[(inx + 1) % foo2Count])) {
            returnVal[0] = (float) m_ptsBuff[0];
            returnVal[1] = (float) m_ptsBuff[1];
            return true; } } }
      return false; }
  }

  // This member variable shall only be used from within drawTextFull().
  private char[] m_charBuff = new char[20];

  /**
   * @param font the font to be used when rendering specified text; the
   *   rendering of glyph shapes generated by this font (using the context
   *   returned by getFontRenderContextFull()) is subjected to the same
   *   transformation as are nodes and edges (the transformation is defined
   *   by the clear() method); therefore, it is the point size of the
   *   font in addition to the scaling factor defined by clear() that
   *   determines the resulting size of text rendered to the graphics
   *   context.
   * @param scaleFactor in order to prevent very small fonts from
   *   "degenerating" and large fonts from taking a long time to load,
   *   it is necessary to allow users to specify a scale
   *   factor in addition to the font size and transform defined by
   *   the clear() method; if this value is 1.0, no additional scaling
   *   is performed; otherwise, the size of the font is multiplied by this
   *   value to yield a new virtual font size.
   * @param xCenter the text string is drawn so that its logical bounds
   *   rectangle with specified font is centered on this X coordinate.
   * @param yCenter the text string is drawn so that its logical bounds
   *   rectangle with specified font is centered on this Y coordinate.
   * @param drawTextAsShape
   *   this flag controls the way that text is drawn to the underlying
   *   graphics context;  by default, all text rendering operations involve
   *   calling the operation Graphics2D.drawString(String, float, float) after
   *   setting specified font in the underlying graphics contexxt; however,
   *   if this flag is set, the text to be rendered is converted to a primitive
   *   shape using font specified, and this shape is then rendered using
   *   Graphics2D.fill(Shape); on some systems, the shape filling method
   *   produces better-looking results when the graphics context is associated
   *   with an image that is to be rendered to the screen; however, on all
   *   systems tested, the shape filling method results in a sybstantial
   *   performance hit when the graphics context is associated with an image
   *   that is to be rendered to the screen; it is recommended to set this
   *   flag to true when either not much text is being rendered or when
   *   the zoom level is very high; the Graphics2D.drawString() operation
   *   has a difficult time when it needs to render text under a transformation
   *   with a very large scale factor.
   */
  public final void drawTextFull(final Font font,
                                 final double scaleFactor,
                                 final String text,
                                 final float xCenter,
                                 final float yCenter,
                                 final Color color,
                                 final boolean drawTextAsShape)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!m_cleared) throw new IllegalStateException
                        ("clear() has not been called previously");
      if (!(scaleFactor >= 0.0d))
        throw new IllegalArgumentException("scaleFactor must be positive"); }
    m_g2d.translate(xCenter, yCenter);
    m_g2d.scale(scaleFactor, -scaleFactor);
    m_g2d.setColor(color);
    if (drawTextAsShape) {
      final GlyphVector glyphV;
      {
        if (text.length() > m_charBuff.length)
          m_charBuff = new char[Math.max(m_charBuff.length * 2,
                                         text.length())];
        text.getChars(0, text.length(), m_charBuff, 0);
        glyphV = font.layoutGlyphVector
          (getFontRenderContextFull(), m_charBuff, 0, text.length(),
           Font.LAYOUT_NO_LIMIT_CONTEXT);
      }
      final Rectangle2D glyphBounds = glyphV.getLogicalBounds();
      m_g2d.translate(-glyphBounds.getCenterX(), -glyphBounds.getCenterY());
      m_g2d.fill(glyphV.getOutline()); }
    else {
      // Note: A new Rectangle2D is being constructed by this method call.
      // As far as I know this performance hit is unavoidable.
      final Rectangle2D textBounds =
        font.getStringBounds(text, getFontRenderContextFull());
      m_g2d.translate(-textBounds.getCenterX(), -textBounds.getCenterY());
      m_g2d.setFont(font);
      m_g2d.drawString(text, 0.0f, 0.0f); }
    m_g2d.setTransform(m_currNativeXform);
  }

  private final FontRenderContext m_fontRenderContextFull =
    new FontRenderContext(null, true, true);

  /**
   * Returns the context that is used by drawTextFull() to produce text shapes
   * to be drawn to the screen.  This context always has the identity
   * transform.
   */
  public final FontRenderContext getFontRenderContextFull()
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher"); }
    return m_fontRenderContextFull;
  }

  /**
   * Translucent colors are not officially supported by the low detail
   * rendering methods, so use translucent colors at your own risk.
   */
  public final void drawTextLow(final Font font,
                                final String text,
                                final float xCenter,
                                final float yCenter,
                                final Color color)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (!m_cleared) throw new IllegalStateException
                        ("clear() has not been called previously"); }
    if (m_gMinimal == null) makeMinimalGraphics();
    m_ptsBuff[0] = xCenter;
    m_ptsBuff[1] = yCenter;
    m_currXform.transform(m_ptsBuff, 0, m_ptsBuff, 0, 1);
    m_gMinimal.setFont(font);
    final FontMetrics fMetrics = m_gMinimal.getFontMetrics();
    m_gMinimal.setColor(color);
    m_gMinimal.drawString
      (text, (int) (-0.5d * fMetrics.stringWidth(text) + m_ptsBuff[0]),
       (int) (0.5d * fMetrics.getHeight() - fMetrics.getDescent() +
              m_ptsBuff[1]));
  }

  /**
   * Returns the context that is used by drawTextLow() to produce text shapes
   * to be drawn to the screen.  The trasform contained in the returned
   * context specifies the only scaling that will be done to fonts when
   * rendering text using drawTextLow().  This transform does not change
   * between frames no matter what scaling factor is specified in
   * clear().
   */
  public final FontRenderContext getFontRenderContextLow()
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher"); }
    if (m_gMinimal == null) makeMinimalGraphics();
    return m_gMinimal.getFontRenderContext();
  }

  /*
   * Computes the intersection of the line segment from (x1,y1)
   * to (x2,y2) with the line segment from (x3,y3)
   * to (x4,y4).  If no intersection exists, returns
   * false.  Otherwise returns true, and
   * returnVal[0] is set to be the X coordinate of the
   * intersection point and returnVal[1] is set to be the Y
   * coordinate of the intersection point.  If more than one intersection
   * point exists, "the intersection point" is defined to be the
   * intersection point closest to (x1,y1).
   * A note about overlapping line segments.  Because of floating point
   * numbers' inability to be totally accurate, it is quite difficult to
   * represent overlapping line segments with floating point coordinates
   * without using an absolute-precision math package.  Because of this,
   * poorly behaved outcome may result when computing the intersection of
   * two [nearly] overlapping line segments.  The only way around this
   * would be to round intersection points to the nearest 32-bit floating
   * point quantity.  But then dynamic range is greatly compromised.
   */
  private final static boolean segmentIntersection(final double[] returnVal,
                                                   double x1, double y1,
                                                   double x2, double y2,
                                                   double x3, double y3,
                                                   double x4, double y4)
  {
    // Arrange the segment endpoints such that in segment 1, y1 >= y2
    // and such that in segment 2, y3 >= y4.
    boolean s1reverse = false;
    if (y2 > y1) {
      s1reverse = !s1reverse;
      double temp = x1; x1 = x2; x2 = temp;
      temp = y1; y1 = y2; y2 = temp; }
    if (y4 > y3) {
      double temp = x3; x3 = x4; x4 = temp;
      temp = y3; y3 = y4; y4 = temp; }

    /*

    Note: While this algorithm for computing an intersection is
          completely bulletproof, it's not a straighforward 'classic'
          bruteforce method.  This algorithm is well-suited for an
          implementation using fixed-point arithmetic instead of
          floating-point arithmetic because all computations are
          contrained to a certain dynamic range relative to the input
          parameters.

    We're going to reduce the problem in the following way:


    (x1,y1)
      +
       \
        \
         \     (x3,y3)                                 x1      x3
 ---------+------+----------- yMax            ---------+------+----------- yMax
           \     |                                      \     |
            \    |                                       \    |
             \   |                                        \   |
              \  |                      \                  \  |
               \ |                  =====\                  \ |
                \|                        >                  \|
                 +                  =====/                    + (x,y)
                 |\                     /                     |\
                 | \                                          | \
                 |  \                                         |  \
 ----------------+---+------- yMin            ----------------+---+------ yMin
                 |  (x2,y2)                                  x4   x2
                 |
                 |
                 +                If  W := (x2-x4) / ((x2-x4) + (x3-x1)) , then
              (x4,y4)
                                                 x = x2 + W*(x1-x2)  and
                                                 y = yMin + W*(yMax-yMin)


     */

    final double yMax = Math.min(y1, y3);
    final double yMin = Math.max(y2, y4);
    if (yMin > yMax) return false;
    if (y1 > yMax) {
      x1 = x1 + (x2 - x1) * (yMax - y1) / (y2 - y1);
      y1 = yMax; }
    if (y3 > yMax) {
      x3 = x3 + (x4 - x3) * (yMax - y3) / (y4 - y3);
      y3 = yMax; }
    if (y2 < yMin) {
      x2 = x1 + (x2 - x1) * (yMin - y1) / (y2 - y1);
      y2 = yMin; }
    if (y4 < yMin) {
      x4 = x3 + (x4 - x3) * (yMin - y3) / (y4 - y3);
      y4 = yMin; }

    // Handling for yMin == yMax.  That is, in the reduced problem, both
    // segments are horizontal.
    if (yMin == yMax) {
      // Arrange the segment endpoints such that in segment 1, x1 <= x2
      // and such that in segment 2, x3 <= x4.
      if (x2 < x1) {
        s1reverse = !s1reverse;
        double temp = x1; x1 = x2; x2 = temp;
        temp = y1; y1 = y2; y2 = temp; }
      if (x4 < x3) {
        double temp = x3; x3 = x4; x4 = temp;
        temp = y3; y3 = y4; y4 = temp; }
      final double xMin = Math.max(x1, x3);
      final double xMax = Math.min(x2, x4);
      if (xMin > xMax) return false;
      else {
        if (s1reverse) returnVal[0] = Math.max(xMin, xMax);
        else returnVal[0] = Math.min(xMin, xMax);
        returnVal[1] = yMin; // == yMax
        return true; } }

    // It is now true that yMin < yMax because we've fully handled
    // the yMin == yMax case above.
    // Following if statement checks for a "twist" in the line segments.
    if ((x1 < x3 && x2 < x4) || (x3 < x1 && x4 < x2)) return false;

    // The segments are guaranteed to intersect.
    if ((x1 == x3) && (x2 == x4)) { // The segments overlap.
      if (s1reverse) { returnVal[0] = x2; returnVal[1] = y2; }
      else { returnVal[0] = x1; returnVal[1] = y1; } }

    // The segments are guaranteed to intersect in exactly one point.
    final double W = (x2 - x4) / ((x2 - x4) + (x3 - x1));
    returnVal[0] = x2 + W * (x1 - x2);
    returnVal[1] = yMin + W * (yMax - yMin);
    return true;
  }

  /*
   * Computes the intersection of the line segment from (x1,y1)
   * to (x2,y2) with the circle at center (cX,cY) and radius specified.
   * Returns the number of intersection points.  The returnVal parameter
   * passed in should be of length 4, and values written to it are such:
   *   returnVal[0] - x component of first intersection point
   *   returnVal[1] - y component of first intersection point
   *   returnVal[2] - x component of second intersection point
   *   returnVal[3] - y component of second intersection point
   * Furthermore, if more than one point is returned, then the first point
   * returned shall be closer to (x1,y1).
   * Note: I don't like the implementation of this method because the
   * computation blows up when the line segment endpoints are close together.
   * Luckily, the way that this method is used from within this class prevents
   * such blowing up.  However, I have named this method bad_*() because
   * I don't want this code to become a generic routine that is used outside
   * the scope of this class.
   */
  private final static int bad_circleIntersection(final double[] returnVal,
                                                  final double x1,
                                                  final double y1,
                                                  final double x2,
                                                  final double y2,
                                                  final double cX,
                                                  final double cY,
                                                  final double radius)
  {
    final double vX = x2 - x1;
    final double vY = y2 - y1;
    if (vX == 0.0d && vY == 0.0d)
      throw new IllegalStateException
        ("the condition of both line segment endpoint being the same " +
         "will not occur if polygons are star-shaped with no marginal " +
         "conditions");
    final double a = vX * vX + vY * vY;
    final double b = 2 * (vX * (x1 - cX) + vY * (y1 - cY));
    final double c =
      cX * cX + cY * cY + x1 * x1 + y1 * y1 -
      2 * (cX * x1 + cY * y1) - radius * radius;
    final double sq = b * b - 4 * a * c;
    if (sq < 0.0d) return 0;
    final double sqrt = Math.sqrt(sq);
    if (sqrt == 0.0d) { // Exactly one solution for infinite line.
      final double u = -b / (2 * a);
      if (!(u <= 1.0d && u >= 0.0d)) return 0;
      returnVal[0] = x1 + u * vX;
      returnVal[1] = y1 + u * vY;
      return 1; }
    else { // Two solutions for infinite line.
      double u1 = (-b + sqrt) / (2 * a);
      double u2 = (-b - sqrt) / (2 * a);
      if (u2 < u1) {
        double temp = u1;
        u1 = u2;
        u2 = temp; }
      // Now u1 is less than or equal to u2.
      int solutions = 0;
      if (u1 <= 1.0d && u1 >= 0.0d) {
        returnVal[0] = x1 + u1 * vX;
        returnVal[1] = y1 + u1 * vY;
        solutions++; }
      if (u2 <= 1.0d && u2 >= 0.0d) {
        returnVal[solutions * 2] = x1 + u2 * vX;
        returnVal[solutions * 2 + 1] = y1 + u2 * vY;
        solutions++; }
      return solutions; }
  }

}

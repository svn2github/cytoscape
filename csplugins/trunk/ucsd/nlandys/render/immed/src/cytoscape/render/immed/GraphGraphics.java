package cytoscape.render.immed;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
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

  public static final byte ARROW_NONE = -1;
  public static final byte ARROW_DISC = -2;
  public static final byte ARROW_DELTA = -3;
  public static final byte ARROW_DIAMOND = -4;
  public static final byte ARROW_TEE = -5;

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private final Color m_bgColor;
  private final boolean m_debug;
  private final Ellipse2D.Double m_ellp2d;
  private final GeneralPath m_poly2d;
  private final double[] m_polyCoords; // I need this for extra precision.
                                       // GeneralPath stores 32 bit floats,
                                       // and I need more precision when
                                       // calculating the inner polygon
                                       // for nonzero border width so that
                                       // edges of polygon are guaranteed to
                                       // have nonzero length during
                                       // computation.
  private final double[] m_fooPolyCoords;
  private final Line2D.Double m_line2d;
  private final float[] m_dash;
  private final double[] m_ptsBuff;
  private final AffineTransform m_currXform;
  private final AffineTransform m_xformUtil;
  private int m_polyNumPoints; // Used with m_polyCoords.
  private Graphics2D m_g2d;
  private Graphics m_gMinimal;
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
   *   will take place in each method call.
   * @exception IllegalStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public GraphGraphics(final Image image, final Color bgColor,
                       final boolean debug)
  {
    if (!EventQueue.isDispatchThread())
      throw new IllegalStateException
        ("calling thread is not AWT event dispatcher");
    this.image = image;
    m_bgColor = bgColor;
    m_debug = debug;
    m_ellp2d = new Ellipse2D.Double();
    m_poly2d = new GeneralPath();
    m_polyCoords = new double[2 * 8]; // Octagon has the most corners.
    m_fooPolyCoords = new double[m_polyCoords.length * 2];
    m_line2d = new Line2D.Double();
    m_dash = new float[] { 0.0f, 0.0f };
    m_ptsBuff = new double[4];
    m_currXform = new AffineTransform();
    m_xformUtil = new AffineTransform();
    clear(0.0d, 0.0d, 1.0d);
  }

  /**
   * Clears image area with background color specified in constructor,
   * and sets an appropriate transformation of coordinate systems.
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
   *   scaleFactor pixels in the image.
   * @exception IllegalStateException if the calling thread isn't the
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
    if (m_gMinimal != null) { m_gMinimal.dispose(); m_gMinimal = null; }
    if (m_g2d != null) m_g2d.dispose();
    m_g2d = (Graphics2D) image.getGraphics();
    final Composite origComposite = m_g2d.getComposite();
    m_g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    m_g2d.setBackground(m_bgColor);
    m_g2d.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
    m_g2d.setComposite(origComposite);
    m_g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    m_g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                           RenderingHints.VALUE_STROKE_PURE);
    setStroke(0.0f, 0.0f);

    m_currXform.setToTranslation(0.5d * image.getWidth(null),
                                 0.5d * image.getHeight(null));
    m_currXform.scale(scaleFactor, scaleFactor);
    m_currXform.translate(-xCenter, -yCenter);
    m_g2d.transform(m_currXform);
  }

  /**
   * @param xQuery the x coordinate of the query point, in the node
   *   coordinate system.
   * @param yQuery the y coordinate of the query point, in the node
   *   coordinate system.
   * @exception IllegalArgumentException if xMin is not less than xMax
   *   or if yMin is not less than yMax.
   */
  public final boolean contains(final byte shapeType,
                                final float xMin, final float yMin,
                                final float xMax, final float yMax,
                                final float xQuery, final float yQuery)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (xMin >= xMax) throw new IllegalArgumentException("xMin >= xMax");
      if (yMin >= yMax) throw new IllegalArgumentException("yMin >= yMax"); }
    return getShape(shapeType, xMin, yMin, xMax, yMax).contains(xQuery,
                                                                yQuery);
  }

  /**
   * @param coords an array of length [at least] two which acts both
   *   as the input and as the output of this method; coords[0] is the
   *   input x coordinate in the canvas coordinate system and is written
   *   as the x coordinate in the node coordinate system by this method;
   *   coords[1] is the input y coordinate in the canvas coordinate system and
   *   is written as the y coordinate in the node coordinate system by this
   *   method.
   */
  public final void xformCanvasToNodeCoords(final double[] coords)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher"); }
    try {
      m_currXform.inverseTransform(coords, 0, coords, 0, 1); }
    catch (java.awt.geom.NoninvertibleTransformException e) {
      throw new RuntimeException("noninvertible matrix - cannot happen"); }
  }

  // This method has the side effect of setting m_ellp2d or m_poly2d;
  // if m_poly2d is set (every case but the ellipse),
  // then m_polyCoords and m_polyNumPoints are also set.
  private final Shape getShape(final byte shapeType,
                               final float xMin, final float yMin,
                               final float xMax, final float yMax)
  {
    switch (shapeType) {
    case SHAPE_ELLIPSE:
      m_ellp2d.setFrame((double) xMin,
                        (double) yMin,
                        ((double) xMax) - xMin,
                        ((double) yMax) - yMin);
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
      m_poly2d.reset();
      m_poly2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_poly2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_poly2d.closePath();
      return m_poly2d;
    case SHAPE_DIAMOND:
      m_polyNumPoints = 4;
      m_polyCoords[0] = (((double) xMin) + xMax) / 2.0d;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = xMax;
      m_polyCoords[3] = (((double) yMin) + yMax) / 2.0d;
      m_polyCoords[4] = (((double) xMin) + xMax) / 2.0d;
      m_polyCoords[5] = yMax;
      m_polyCoords[6] = xMin;
      m_polyCoords[7] = (((double) yMin) + yMax) / 2.0d;
      // The rest of this code can be factored with other cases.
      m_poly2d.reset();
      m_poly2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_poly2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_poly2d.closePath();
      return m_poly2d;
    case SHAPE_HEXAGON:
      m_polyNumPoints = 6;
      m_polyCoords[0] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = (((double) yMin) + yMax) / 2.0d;
      m_polyCoords[6] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[7] = yMax;
      m_polyCoords[8] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[9] = yMax;
      m_polyCoords[10] = xMin;
      m_polyCoords[11] = (((double) yMin) + yMax) / 2.0d;
      // The rest of this code can be factored with other cases.
      m_poly2d.reset();
      m_poly2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_poly2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_poly2d.closePath();
      return m_poly2d;
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
      m_poly2d.reset();
      m_poly2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_poly2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_poly2d.closePath();
      return m_poly2d;
    case SHAPE_PARALLELOGRAM:
      m_polyNumPoints = 4;
      m_polyCoords[0] = xMin;
      m_polyCoords[1] = yMin;
      m_polyCoords[2] = (2.0d * xMax + xMin) / 3.0d;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = yMax;
      m_polyCoords[6] = (2.0d * xMin + xMax) / 3.0d;
      m_polyCoords[7] = yMax;
      // The rest of this code can be factored with other cases.
      m_poly2d.reset();
      m_poly2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_poly2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_poly2d.closePath();
      return m_poly2d;
    case SHAPE_TRIANGLE:
      m_polyNumPoints = 3;
      m_polyCoords[0] = xMin;
      m_polyCoords[1] = yMax;
      m_polyCoords[2] = (((double) xMin) + xMax) / 2.0d;
      m_polyCoords[3] = yMin;
      m_polyCoords[4] = xMax;
      m_polyCoords[5] = yMax;
      // The rest of this code can be factored with other cases.
      m_poly2d.reset();
      m_poly2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);
      for (int i = 2; i < m_polyNumPoints * 2;)
        m_poly2d.lineTo((float) m_polyCoords[i++], (float) m_polyCoords[i++]);
      m_poly2d.closePath();
      return m_poly2d;
    default:
      throw new IllegalArgumentException("shapeType is not recognized"); }
  }

  /**
   * The xMin, yMin, xMax, and yMax parameters specify the extents of the
   * node shape (in the node coordinate system), including the border
   * width.
   * @param borderWidth the border width, in node coordinate space; if
   *   this value is zero, the rendering engine skips over the process of
   *   rendering the border, which gives a significant performance boost.
   * @exception IllegalStateException if the calling thread isn't the
   *   AWT event handling thread.
   * @exception IllegalArgumentException if xMin is not less than xMax or if
   *   yMin is not less than yMax, or if borderWidth is negative,
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
      if (xMin >= xMax) throw new IllegalArgumentException("xMin >= xMax");
      if (yMin >= yMax) throw new IllegalArgumentException("yMin >= yMax");
      if (borderWidth < 0.0f)
        throw new IllegalArgumentException("borderWidth < 0"); }
    final Shape shape = getShape(shapeType, xMin, yMin, xMax, yMax);
    if (borderWidth == 0.0f) m_g2d.setColor(fillColor);
    else m_g2d.setColor(borderColor);
    m_g2d.fill(shape);
    if (borderWidth != 0.0f) { // Fill inner node.
      final Shape innerShape;
      if (shapeType == SHAPE_ELLIPSE) {
        // This is an approximation to proper border width.  It's
        // faster than drawing an elliptical path of some thickness, and this
        // approach leads to precise intersection calculations for edges.
        final double innerXMin = ((double) xMin) + borderWidth;
        final double innerYMin = ((double) yMin) + borderWidth;
        final double innerXMax = ((double) xMax) - borderWidth;
        final double innerYMax = ((double) yMax) - borderWidth;
        m_ellp2d.setFrame(innerXMin, innerYMin,
                          innerXMax - innerXMin, innerYMax - innerYMin);
        innerShape = m_ellp2d; }
      else {
        // A general [possibly non-convex] polygon with certain
        // restrictions: no two consecutive line segments can be parallel,
        // each line segment must have nonzero length, the polygon cannot
        // self-intersect, and the polygon must be clockwise (where +y is down
        // and +x is right).
        m_poly2d.reset();
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
        m_poly2d.moveTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);
        for (int i = 6;;) {
          if (i == m_polyNumPoints * 2) {
            computeInnerPoint(m_ptsBuff, xCurr, yCurr, xNext, yNext,
                              xNot, yNot, borderWidth);
            m_poly2d.lineTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);
            computeInnerPoint(m_ptsBuff, xNext, yNext, xNot, yNot,
                              xOne, yOne, borderWidth);
            m_poly2d.lineTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);
            m_poly2d.closePath();
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
            m_poly2d.lineTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]); } }
        innerShape = m_poly2d; }
      m_g2d.setColor(fillColor);
      m_g2d.fill(innerShape); }
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
   * The node shape used by this method is SHAPE_RECTANGLE.<p>
   * xMin, yMin, xMax, and yMax specify the extents of the node in the
   * node coordinate space, not the image coordinate space.  Thus, these
   * values will likely not change from frame to frame, as zoom and pan
   * operations are performed.
   * @exception IllegalStateException if the calling thread isn't the
   *   AWT event handling thread.
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
      if (xMin >= xMax) throw new IllegalArgumentException("xMin >= xMax");
      if (yMin >= yMax) throw new IllegalArgumentException("yMin >= yMax"); }
    if (m_gMinimal == null) m_gMinimal = image.getGraphics();
    // I'm transforming points manually because the resulting underlying
    // graphics pipeline used is much faster.
    m_ptsBuff[0] = xMin; m_ptsBuff[1] = yMin;
    m_ptsBuff[2] = xMax; m_ptsBuff[3] = yMax;
    m_currXform.transform(m_ptsBuff, 0, m_ptsBuff, 0, 2);
    // Here, double values outside of the range of ints will be case to
    // the nearest int without overflow.
    final int xNot = (int) m_ptsBuff[0];
    final int yNot = (int) m_ptsBuff[1];
    final int xOne = (int) m_ptsBuff[2];
    final int yOne = (int) m_ptsBuff[3];
    m_gMinimal.setColor(fillColor);
    m_gMinimal.fillRect(xNot, yNot, Math.max(1, xOne - xNot), // Overflow will
                        Math.max(1, yOne - yNot));            // be problem.
  }

  public final void drawEdgeLow(final float x0, final float y0,
                                final float x1, final float y1,
                                final Color edgeColor)
  {
    if (m_debug) {
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher"); }
    if (m_gMinimal == null) m_gMinimal = image.getGraphics();
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

  /**
   * The arrow types must each be one of the ARROW_* constants.
   * The arrow at endpoint 1 is always on top of the arrow at endpoint 0
   * because the arrow at endpoint 0 gets rendered first.
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>arrow type</th>     <th>placement of arrow</th>          </tr>
   *   <tr>  <td>ARROW_NONE</td>     <td>the edge line segment has
   *                                   endpoint specified, and
   *                                   the line segment has a round
   *                                   end (center of round
   *                                   semicircle end exactly equal to
   *                                   endpoint specified); arrow size
   *                                   and color are ignored</td>         </tr>
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
   * <table></blockquote>
   * @param dashLength a positive value representing the length of dashes
   *   on the edge, or zero to indicate that the edge is solid.
   * @exception IllegalArgumentException if edgeThickness is less than zero,
   *   if dashLength is less than zero, or if any one of the arrow sizes
   *   is less than edgeThickness.
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
      if (!EventQueue.isDispatchThread())
        throw new IllegalStateException
          ("calling thread is not AWT event dispatcher");
      if (edgeThickness < 0.0f)
        throw new IllegalArgumentException("edgeThickness < 0");
      if (dashLength < 0.0f)
        throw new IllegalArgumentException("dashLength < 0");
      switch (arrowType0) {
      case ARROW_NONE:
        break;
      case ARROW_DELTA:
        if (((double) edgeThickness) / arrow0Size > 4.0d / Math.sqrt(17.0d))
          throw new IllegalArgumentException
            ("for ARROW_DELTA e/s is greater than 4/sqrt(17)");
        break;
      case ARROW_DIAMOND:
        if (((double) edgeThickness) / arrow0Size > 2.0d / Math.sqrt(5.0d))
          throw new IllegalArgumentException
            ("for ARROW_DIAMOND e/s is greater than 2/sqrt(5)");
        break;
      case ARROW_DISC:
        if (((double) edgeThickness) / arrow0Size > 1.0d)
          throw new IllegalArgumentException
            ("for ARROW_DISC e/s is greater than 1");
        break;
      case ARROW_TEE:
        if (((double) edgeThickness) / arrow0Size > 0.5d)
          throw new IllegalArgumentException
            ("for ARROW_TEE e/s is greater than 1/2");
        break;
      default:
        throw new IllegalArgumentException("arrowType0 is not recognized"); }
      if (arrowType0 != ARROW_NONE && arrow0Size <= 0.0f)
        throw new IllegalArgumentException("arrow0Size must be positive");
      switch (arrowType1) {
      case ARROW_NONE:
        break;
      case ARROW_DELTA:
        if (((double) edgeThickness) / arrow1Size > 4.0d / Math.sqrt(17.0d))
          throw new IllegalArgumentException
            ("for ARROW_DELTA e/s is greater than 4/sqrt(17)");
        break;
      case ARROW_DIAMOND:
        if (((double) edgeThickness) / arrow1Size > 2.0d / Math.sqrt(5.0d))
          throw new IllegalArgumentException
            ("for ARROW_DIAMOND e/s is greater than 2/sqrt(5)");
        break;
      case ARROW_DISC:
        if (((double) edgeThickness) / arrow1Size > 1.0d)
          throw new IllegalArgumentException
            ("for ARROW_DISC e/s is greater than 1");
        break;
      case ARROW_TEE:
        if (((double) edgeThickness) / arrow1Size > 0.5d)
          throw new IllegalArgumentException
            ("for ARROW_TEE e/s is greater than 1/2");
        break;
      default:
        throw new IllegalArgumentException("arrowType0 is not recognized"); }
      if (arrowType1 != ARROW_NONE && arrow1Size <= 0.0f)
        throw new IllegalArgumentException("arrow1Size must be positive"); }
    // End debug.  Here the real code begins.

    final double len = Math.sqrt((((double) x1) - x0) * (((double) x1) - x0) +
                                 (((double) y1) - y0) * (((double) y1) - y0));
    // If the length of the edge is zero we're going to skip completely over
    // all rendering.  This may not be the 100% correct approach.  We'll see.
    if (len == 0.0d) return;

    { // Render the line segment if necessary.
      final double x0Adj;
      final double y0Adj;
      switch (arrowType0) {
      case ARROW_TEE:
      case ARROW_DELTA:
      case ARROW_DIAMOND:
        final double t = getT(arrowType0) * arrow0Size / len;
        x0Adj = t * (((double) x1) - x0) + x0;
        y0Adj = t * (((double) y1) - y0) + y0;
        break;
      default: // ARROW_NONE or ARROW_DISC.
        // Don't change endpoint 0.
        x0Adj = x0; y0Adj = y0;
        break; }

      final double x1Adj;
      final double y1Adj;
      switch (arrowType1) {
      case ARROW_TEE:
      case ARROW_DELTA:
      case ARROW_DIAMOND:
        final double t = getT(arrowType1) * arrow1Size / len;
        x1Adj = t * (((double) x0) - x1) + x1;
        y1Adj = t * (((double) y0) - y1) + y1;
        break;
      default: // ARROW_NONE or ARROW_DISC.
        // Don't change endpoint 1.
        x1Adj = x1; y1Adj = y1;
        break; }

      // If the vector point0->point1 is pointing opposite to
      // adj0->adj1, then don't render the line segment.
      // Dot product determines this.
      if ((((double) x1) - x0) * (x1Adj - x0Adj) +
          (((double) y1) - y0) * (y1Adj - y0Adj) > 0.0d) {
        // Render the line segment.
        if (m_dash[0] != dashLength || m_currStrokeWidth != edgeThickness)
          setStroke(edgeThickness, dashLength);
        m_line2d.setLine(x0Adj, y0Adj, x1Adj, y1Adj);
        m_g2d.setColor(edgeColor);
        m_g2d.draw(m_line2d); }
    }

    { // Render the arrow at point 0.
      final Shape arrow0Shape;
      switch (arrowType0) {
      case ARROW_DISC:
        m_ellp2d.setFrame(((double) x0) - 0.5d * arrow0Size,
                          ((double) y0) - 0.5d * arrow0Size,
                          (double) arrow0Size, (double) arrow0Size);
        arrow0Shape = m_ellp2d;
        break;
      case ARROW_DELTA:
      case ARROW_DIAMOND:
      case ARROW_TEE:
        computeUntransformedArrow(arrowType0);
        // I want the transform to first scale, then rotate, then translate.
        final double cosTheta = (((double) x0) - x1) / len;
        final double sinTheta = (((double) y0) - y1) / len;
        m_xformUtil.setTransform(cosTheta, sinTheta, -sinTheta, cosTheta,
                                 x0, y0);
        m_xformUtil.scale(arrow0Size, arrow0Size);
        m_poly2d.transform(m_xformUtil);
        arrow0Shape = m_poly2d;
        break;
      default: // ARROW_NONE.
        // Don't render anything.
        arrow0Shape = null;
        break; }
      if (arrow0Shape != null) {
        m_g2d.setColor(arrow0Color);
        m_g2d.fill(arrow0Shape); }
    }

    { // Render the arrow at point 1.
      final Shape arrow1Shape;
      switch (arrowType1) {
      case ARROW_DISC:
        m_ellp2d.setFrame(((double) x1) - 0.5d * arrow1Size,
                          ((double) y1) - 0.5d * arrow1Size,
                          arrow1Size, arrow1Size);
        arrow1Shape = m_ellp2d;
        break;
      case ARROW_DELTA:
      case ARROW_DIAMOND:
      case ARROW_TEE:
        computeUntransformedArrow(arrowType1);
        // I want the transform to first scale, then rotate, then translate.
        final double cosTheta = (((double) x1) - x0) / len;
        final double sinTheta = (((double) y1) - y0) / len;
        m_xformUtil.setTransform(cosTheta, sinTheta, -sinTheta, cosTheta,
                                 x1, y1);
        m_xformUtil.scale(arrow1Size, arrow1Size);
        m_poly2d.transform(m_xformUtil);
        arrow1Shape = m_poly2d;
        break;
      default: // ARROW_NONE.
        // Don't render anything.
        arrow1Shape = null;
        break; }
      if (arrow1Shape != null) {
        m_g2d.setColor(arrow1Color);
        m_g2d.fill(arrow1Shape); }
    }
  }

  /*
   * This method has the side effect of mangling m_poly2d.
   * arrowType must be one of the following: ARROW_DELTA, ARROW_DIAMOND,
   * or ARROW_TEE.
   */
  private final void computeUntransformedArrow(final byte arrowType)
  {
    switch (arrowType) {
    case ARROW_DELTA:
      m_poly2d.reset();
      m_poly2d.moveTo(-2.0f, -0.5f);
      m_poly2d.lineTo(0.0f, 0.0f);
      m_poly2d.lineTo(-2.0f, 0.5f);
      m_poly2d.closePath();
      break;
    case ARROW_DIAMOND:
      m_poly2d.reset();
      m_poly2d.moveTo(-1.0f, -0.5f);
      m_poly2d.lineTo(-2.0f, 0.0f);
      m_poly2d.lineTo(-1.0f, 0.5f);
      m_poly2d.lineTo(0.0f, 0.0f);
      m_poly2d.closePath();
      break;
    default: // ARROW_TEE.
      m_poly2d.reset();
      m_poly2d.moveTo(-0.125f, -2.0f);
      m_poly2d.lineTo(0.125f, -2.0f);
      m_poly2d.lineTo(0.125f, 2.0f);
      m_poly2d.lineTo(-0.125f, 2.0f);
      m_poly2d.closePath();
      break; }
  }

  /*
   * arrowType must be one of the following: ARROW_DELTA, ARROW_DIAMOND,
   * or ARROW_TEE.
   */
  private final static double getT(final byte arrowType)
  { // I could implement this as an array instead of a switch statement.
    switch (arrowType) {
    case ARROW_DELTA:
      return 2.0d;
    case ARROW_DIAMOND:
      return 1.0d;
    default: // ARROW_TEE.
      return 0.125d; }
  }

  private final void setStroke(final float width, final float dashLength)
  {
    m_dash[0] = dashLength;
    m_dash[1] = dashLength;
    m_currStrokeWidth = width;
    if (m_dash[0] == 0.0f)
      m_g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_MITER, 10.0f));
    else
      m_g2d.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_MITER, 10.0f,
                                      m_dash, 0.0f));
  }

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
      if (xMin >= xMax) throw new IllegalArgumentException("xMin >= xMax");
      if (yMin >= yMax) throw new IllegalArgumentException("yMin >= yMax");
      if (offset < 0.0f) throw new IllegalArgumentException("offset < 0"); }
    if (nodeShape == SHAPE_ELLIPSE) {
      // First, compute the actual intersection of the edge with the
      // ellipse, if it exists.  We will use this intersection point
      // regardless of whether or not offset is zero.
      // For nonzero offsets on the ellipse, use tangent lines to approximate
      // intersection with offset instead of solving a quartic equation.
      final double ellpCenterX = (((double) xMin) + xMax) / 2.0d;
      final double ellpCenterY = (((double) yMin) + yMax) / 2.0d;
      final double ptPrimeX = ptX - ellpCenterX;
      final double ptPrimeY = ptY - ellpCenterY;
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
        returnVal[0] = (float) (xsectPtPrimeX + ellpCenterX);
        returnVal[1] = (float) (xsectPtPrimeY + ellpCenterY);
        return true; }
      // Even if offset is zero, do extra computation for sake of simple code.
      final double multFactor = offset / distPtPrimeToTangent;
      returnVal[0] = (float)
        (ellpCenterX +
         (xsectPtPrimeX + multFactor * (ptPrimeX - xsectPtPrimeX)));
      returnVal[1] = (float)
        (ellpCenterY +
         (xsectPtPrimeY + multFactor * (ptPrimeY - xsectPtPrimeY)));
      return true; }
    else {
      // This next method call has the side effect of settingg m_polyCoords and
      // m_polyNumPoints - this is all that we are going to use.
      getShape(nodeShape, xMin, yMin, xMax, yMax);
      if (offset != 0.0f) {
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
          m_fooPolyCoords[i * 4] = x0 + vNormY;
          m_fooPolyCoords[i * 4 + 1] = y0 - vNormX;
          m_fooPolyCoords[i * 4 + 2] = x1 + vNormY;
          m_fooPolyCoords[i * 4 + 3] = y1 - vNormX; } }
      return false;
    }
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

}

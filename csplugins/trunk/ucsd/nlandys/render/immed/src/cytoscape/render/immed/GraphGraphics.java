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
  private final Rectangle2D.Float m_rect2d;
  private final Ellipse2D.Float m_ellp2d;
  private final GeneralPath m_poly2d;
  private final GeneralPath m_innerPoly2d;
  private final Line2D.Float m_line2d;
  private final float[] m_dash;
  private final float[] m_pathBuff;
  private final double[] m_ptsBuff;
  private Graphics2D m_g2d;
  private Graphics m_gMinimal;
  private AffineTransform m_currXform;
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
    m_ellp2d = new Ellipse2D.Float();
    m_poly2d = new GeneralPath();
    m_innerPoly2d = new GeneralPath();
    m_line2d = new Line2D.Float();
    m_dash = new float[] { 0.0f, 0.0f };
    m_pathBuff = new float[6];
    m_ptsBuff = new double[4];
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
   * @param xQuery the x coordinate of the query point, in the node
   *   coordinate system.
   * @param yQuery the y coordinate of the query point, in the node
   *   coordinate system.
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
      if (xMin > xMax) throw new IllegalArgumentException("xMin > xMax");
      if (yMin > yMax) throw new IllegalArgumentException("yMin > yMax"); }
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
    try {
      m_currXform.inverseTransform(coords, 0, coords, 0, 1); }
    catch (java.awt.geom.NoninvertibleTransformException e) {
      throw new RuntimeException("noninvertible matrix - cannot happen"); }
  }

  // This method has the side effect of setting m_rect2d, m_ellp2d, or
  // m_poly2d.
  private final Shape getShape(final byte shapeType,
                               final float xMin, final float yMin,
                               final float xMax, final float yMax)
  {
    switch (shapeType) {
    case SHAPE_RECTANGLE:
      m_rect2d.setRect(xMin, yMin, xMax - xMin, yMax - yMin);
      return m_rect2d;
    case SHAPE_ELLIPSE:
      m_ellp2d.setFrame(xMin, yMin, xMax - xMin, yMax - yMin);
      return m_ellp2d;
    case SHAPE_DIAMOND:
      m_poly2d.reset();
      m_poly2d.moveTo((xMin + xMax) / 2.0f, yMin);
      m_poly2d.lineTo(xMax, (yMin + yMax) / 2.0f);
      m_poly2d.lineTo((xMin + xMax) / 2.0f, yMax);
      m_poly2d.lineTo(xMin, (yMin + yMax) / 2.0f);
      m_poly2d.closePath();
      return m_poly2d;
    case SHAPE_HEXAGON:
      m_poly2d.reset();
      m_poly2d.moveTo((2.0f * xMin + xMax) / 3.0f, yMin);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMin);
      m_poly2d.lineTo(xMax, (yMin + yMax) / 2.0f);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMax);
      m_poly2d.lineTo((2.0f * xMin + xMax) / 3.0f, yMax);
      m_poly2d.lineTo(xMin, (yMin + yMax) / 2.0f);
      m_poly2d.closePath();
      return m_poly2d;
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
      return m_poly2d;
    case SHAPE_PARALLELOGRAM:
      m_poly2d.reset();
      m_poly2d.moveTo(xMin, yMin);
      m_poly2d.lineTo((xMin + 2.0f * xMax) / 3.0f, yMin);
      m_poly2d.lineTo(xMax, yMax);
      m_poly2d.lineTo((2.0f * xMin + xMax) / 3.0f, yMax);
      m_poly2d.closePath();
      return m_poly2d;
    case SHAPE_TRIANGLE:
      m_poly2d.reset();
      m_poly2d.moveTo(xMin, yMax);
      m_poly2d.lineTo((xMin + xMax) / 2.0f, yMin);
      m_poly2d.lineTo(xMax, yMax);
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
    final Shape shape = getShape(shapeType, xMin, yMin, xMax, yMax);
    if (borderWidth == 0.0f) m_g2d.setColor(fillColor);
    else m_g2d.setColor(borderColor);
    m_g2d.fill(shape);
    if (borderWidth != 0.0f) { // Fill inner node.
      final Shape innerShape;
      if (shapeType == SHAPE_ELLIPSE) {
        // This is an approximation to proper border width.  It's
        // faster than drawing a curvy path of some thickness, and this
        // approach leads to exact intersection calculations for edges.
        final float innerXMin = xMin + borderWidth;
        final float innerYMin = yMin + borderWidth;
        final float innerXMax = xMax - borderWidth;
        final float innerYMax = yMax - borderWidth;
        m_ellp2d.setFrame(innerXMin, innerYMin,
                          innerXMax - innerXMin, innerYMax - innerYMin);
        innerShape = m_ellp2d; }
      else if (shapeType == SHAPE_RECTANGLE) {
        final float innerXMin = xMin + borderWidth;
        final float innerYMin = yMin + borderWidth;
        final float innerXMax = xMax - borderWidth;
        final float innerYMax = yMax - borderWidth;
        m_rect2d.setRect(innerXMin, innerYMin,
                         innerXMax - innerXMin, innerYMax - innerYMin);
        innerShape = m_rect2d; }
      else {
        // A general [possibly non-convex] polygon with certain
        // restrictions: no two consecutive line segments can be parallel,
        // and each line segment must have nonzero length.
        m_innerPoly2d.reset();
        final PathIterator path = m_poly2d.getPathIterator(null);
        path.currentSegment(m_pathBuff); // PathIterator.SEG_MOVETO.
        final float xNot = m_pathBuff[0];
        final float yNot = m_pathBuff[1];
        path.next();
        path.currentSegment(m_pathBuff); // PathIterator.SEG_LINETO.
        final float xOne = m_pathBuff[0];
        final float yOne = m_pathBuff[1];
        float xPrev = xNot;
        float yPrev = yNot;
        float xCurr = xOne;
        float yCurr = yOne;
        path.next();
        path.currentSegment(m_pathBuff); // PathIterator.SEG_LINETO.
        float xNext = m_pathBuff[0];
        float yNext = m_pathBuff[1];
        computeInnerPoint(m_pathBuff, xPrev, yPrev, xCurr, yCurr,
                          xNext, yNext, borderWidth);
        m_innerPoly2d.moveTo(m_pathBuff[0], m_pathBuff[1]);
        while (true) {
          path.next();
          if (path.currentSegment(m_pathBuff) == PathIterator.SEG_CLOSE) {
            computeInnerPoint(m_pathBuff, xCurr, yCurr, xNext, yNext,
                              xNot, yNot, borderWidth);
            m_innerPoly2d.lineTo(m_pathBuff[0], m_pathBuff[1]);
            computeInnerPoint(m_pathBuff, xNext, yNext, xNot, yNot,
                              xOne, yOne, borderWidth);
            m_innerPoly2d.lineTo(m_pathBuff[0], m_pathBuff[1]);
            m_innerPoly2d.closePath();
            break; }
          else { // PathIterator.SEG_LINETO.
            xPrev = xCurr;
            yPrev = yCurr;
            xCurr = xNext;
            yCurr = yNext;
            xNext = m_pathBuff[0];
            yNext = m_pathBuff[1];
            computeInnerPoint(m_pathBuff, xPrev, yPrev, xCurr, yCurr,
                              xNext, yNext, borderWidth);
            m_innerPoly2d.lineTo(m_pathBuff[0], m_pathBuff[1]); } }
        innerShape = m_innerPoly2d; }
      m_g2d.setColor(fillColor);
      m_g2d.fill(innerShape); }
  }

  /*
   * output[0] is the x return value and output[1] is the y return value.
   * The line prev->curr cannot be parallel to curr->next.
   */
  private final static void computeInnerPoint(final float[] output,
                                              final float xPrev,
                                              final float yPrev,
                                              final float xCurr,
                                              final float yCurr,
                                              final float xNext,
                                              final float yNext,
                                              final float borderWidth)
  {
    final double segX1 = xCurr - xPrev;
    final double segY1 = yCurr - yPrev;
    final double segLength1 = Math.sqrt(segX1 * segX1 + segY1 * segY1);
    final double segX2 = xNext - xCurr;
    final double segY2 = yNext - yCurr;
    final double segLength2 = Math.sqrt(segX2 * segX2 + segY2 * segY2);
    final double segX2Normal = segX2 / segLength2;
    final double segY2Normal = segY2 / segLength2;
    final double xNextPrime = xPrev + segX2Normal * segLength1;
    final double yNextPrime = yPrev + segY2Normal * segLength1;
    final double segPrimeX = xNextPrime - xCurr;
    final double segPrimeY = yNextPrime - yCurr;
    final double distancePrimeToSeg1 =
      (segX1 * yNextPrime - segY1 * xNextPrime +
       ((double) xPrev) * yCurr - ((double) xCurr) * yPrev) / segLength1;
    final double multFactor = borderWidth / distancePrimeToSeg1;
    output[0] = (float) (multFactor * segPrimeX + xCurr);
    output[1] = (float) (multFactor * segPrimeY + yCurr);
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
    if (m_gMinimal == null) m_gMinimal = image.getGraphics();
    // I'm transforming points manually because the resulting underlying
    // graphics pipeline used is much faster.
    m_ptsBuff[0] = xMin; m_ptsBuff[1] = yMin;
    m_ptsBuff[2] = xMax; m_ptsBuff[3] = yMax;
    m_currXform.transform(m_ptsBuff, 0, m_ptsBuff, 0, 2);
    final int xNot = (int) m_ptsBuff[0];
    final int yNot = (int) m_ptsBuff[1];
    final int xOne = (int) m_ptsBuff[2];
    final int yOne = (int) m_ptsBuff[3];
    m_gMinimal.setColor(fillColor);
    m_gMinimal.fillRect(xNot, yNot, Math.max(1, xOne - xNot),
                        Math.max(1, yOne - yNot));
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
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <th>arrow type</th>     <th>placement of arrow</th>          </tr>
   *   <tr>  <td>ARROW_NONE</td>     <td>the edge line segment has
   *                                   endpoints specified, and
   *                                   the line segment has round
   *                                   ends (center of round
   *                                   semicircle end exactly equal to
   *                                   endpoint specified)</td>           </tr>
   *   <tr>  <td>ARROW_DISC</td>     <td>the disc arrowhead is placed
   *                                   such that its center is at a
   *                                   specified endpoint; the diameter
   *                                   of the disk is the arrow width
   *                                   specified</td>                     </tr>
   *   <tr>  <td>ARROW_DELTA</td>    <td>the sharp tip of the arrowhead
   *                                   is exactly at an endpint
   *                                   specified</td>                     </tr>
   *   <tr>  <td>ARROW_DIAMOND</td>  <td>the sharp tip of the arrowhead
   *                                   is exactly at an endpoint
   *                                   specified</td>                     </tr>
   *   <tr>  <td>ARROW_TEE</td>      <td>the center of the tee intersection
   *                                   lies at a specified endpoint; the
   *                                   span of the top of the tee is
   *                                   the arrow width specified, and the
   *                                   width of the top of the tee line
   *                                   segment is a fixed fraction of
   *                                   its span</td></tr>
   * <table></blockquote>
   * @param dashLength a positive value representing the length of dashes
   *   on the edge, or zero to indicate that the edge is solid.
   * @exception IllegalArgumentException if edgeThickness is less than zero,
   *   if dashLength is less than zero, or if any one of the arrow widths
   *   is less than edgeThickness.
   */
  public final void drawEdgeFull(final byte arrowType0,
                                 final float arrow0Width,
                                 final Color arrow0Color,
                                 final byte arrowType1,
                                 final float arrow1Width,
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
      if (arrow0Width < edgeThickness)
        throw new IllegalArgumentException("arrow0Width < edgeThickness");
      if (arrow1Width < edgeThickness)
        throw new IllegalArgumentException("arrow1Width < edgeThickness");
      if (dashLength < 0.0f)
        throw new IllegalArgumentException("dashLength < 0"); }
    // We're giving CAP_BUTT ends to edge segments for a simple reason:
    // What if one end is ARROW_NONE and the other is ARROW_DELTA with the
    // delta arrowhead the same width as the edge?  We can't convince
    // BasicStroke to have two different caps on both ends.  So instead, we
    // will draw CAP_BUTT and manually fill a disc at one or both ends if
    // we need to.
    if (m_dash[0] != dashLength || m_currStrokeWidth != edgeThickness)
      setStroke(edgeThickness, dashLength);
    m_line2d.setLine(x0, y0, x1, y1);
    m_g2d.setColor(edgeColor);
    m_g2d.draw(m_line2d);
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

}

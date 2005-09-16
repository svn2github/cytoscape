package cytoscape.render.stateful;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;

/**
 * Defines visual properties of a node modulo the node size and location.
 * Even though this class is not declared abstract, in most situations it
 * makes sense to override at least some of its methods in order to gain
 * control over node visual properties.<p>
 * To understand the significance of each method's return value, it makes
 * sense to become familiar with the API cytoscape.render.immed.GraphGraphics.
 */
public class NodeDetails
{

  /**
   * Specifies that a text anchor point lies at the center of a text
   * label's logical bounds box.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_CENTER = 0;

  /**
   * Specifies that a text anchor point lies on the north edge of a
   * text label's logical bounds box, halfway between the east and west edges.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_NORTH = 1;

  /**
   * Specifies that a text anchor point lies on the northeast corner of
   * a text label's logical bounds box.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_NORTHEAST = 2;

  /**
   * Specifies that a text anchor point lies on the east edge of a
   * text label's logical bounds box, halfway between the north and south
   * edges.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_EAST = 3;

  /**
   * Specifies that a text anchor point lies on the southeast corner of
   * a text label's logical bounds box.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_SOUTHEAST = 4;

  /**
   * Specifies that a text anchor point lies on the south edge of a
   * text label's logical bounds box, halfway between the east and west
   * edges.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_SOUTH = 5;

  /**
   * Specifies that a text anchor point lies on the southwest corner of a
   * text label's logical bounds box.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_SOUTHWEST = 6;

  /**
   * Specifies that a text anchor point lies on the west edge of a
   * text label's logical bounds box, halfway between the north and south
   * edges.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_WEST = 7;

  /**
   * Specifies that a text anchor point lies on the northwest corner of a
   * text label's logical bounds box.
   * @see #labelTextAnchor(int)
   */
  public static final byte LABEL_ANCHOR_TEXT_NORTHWEST = 8;

  /**
   * Specifies that a node anchor point lies in the center of a node's
   * extents rectangle.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_CENTER = -1;

  /**
   * Specifies that a node anchor point lies on the north edge of a
   * node's extents rectangle, halfway between the east and west edges.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_NORTH = -2;

  /**
   * Specifies that a node anchor point lies on the northeast corner of
   * a node's extents rectangle.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_NORTHEAST = -3;

  /**
   * Specifies that a node anchor point lies on the east edge of a
   * node's extents rectangle, halfway between the north and south edges.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_EAST = -4;

  /**
   * Specifies that a node anchor point lies on the southeast corner of
   * a node's extents rectangle.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_SOUTHEAST = -5;

  /**
   * Specifies that a node anchor point lies on the south edge of a
   * node's extents rectangle, halfway between the east and west edges.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_SOUTH = -6;

  /**
   * Specifies that a node anchor point lies on the southwest corner of
   * a node's extents rectangle.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_SOUTHWEST = -7;

  /**
   * Specifies that a node anchor point lies on the west edge of a
   * node's extents rectangle, halfway between the north and south edges.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_WEST = -8;

  /**
   * Specifies that a node anchor point lies on the northwest corner of
   * a node's extents rectangle.
   * @see #labelNodeAnchor(int)
   */
  public static final byte LABEL_ANCHOR_NODE_NORTHWEST = -9;

  /**
   * Specifies that the lines in a multi-line node label should each have
   * a center point with similar X coordinate.
   * @see #labelJustify(int)
   */
  public static final byte LABEL_WRAP_JUSTIFY_CENTER = 64;

  /**
   * Specifies that the lines of a multi-line node label should each have
   * a leftmost point with similar X coordinate.
   * @see #labelJustify(int)
   */
  public static final byte LABEL_WRAP_JUSTIFY_LEFT = 65;

  /**
   * Specifies that the lines of a multi-line node label should each have
   * a rightmost point with similar X coordinate.
   * @see #labelJustify(int)
   */
  public static final byte LABEL_WRAP_JUSTIFY_RIGHT = 66;

  /**
   * Returns the color of node in low detail rendering mode.
   * By default this method returns Color.red.  It is an error to return
   * null in this method.<p>
   * In low detail rendering mode, this is the only method from this class
   * that is looked at.  The rest of the methods in this class define visual
   * properties that are used in full detail rendering mode.  In low detail
   * rendering mode translucent colrs are not supported whereas in full
   * detail rendering mode they are; therefore, colorLowDetail(node) and
   * fillPaint(node) may return two different colors.
   */
  public Color colorLowDetail(final int node) {
    return Color.red; }

  /**
   * Returns a GraphGraphics.SHAPE_* constant (or a custom node shape that an
   * instance of GraphGraphics understands); this defines the shape that this
   * node takes.
   * By default this method returns GraphGraphics.SHAPE_RECTANGLE.
   * Take note of certain constraints specified in
   * GraphGraphics.drawNodeFull() that pertain to rounded rectangles.
   */
  public byte shape(final int node) {
    return GraphGraphics.SHAPE_RECTANGLE; }

  /**
   * Returns the paint of the interior of the node shape.  By default this
   * method returns Color.red.  It is an error to return null in this method.
   */
  public Paint fillPaint(final int node) {
    return Color.red; }

  /**
   * Returns the border width of the node shape.  By default this method
   * returns zero.  Take note of certain constraints specified in
   * GraphGraphics.drawNodeFull().
   */
  public float borderWidth(final int node) {
    return 0.0f; }

  /**
   * Returns the paint of the border of the node shape.  By default this method
   * returns null.  This return value is ignored if borderWidth(node)
   * returns zero; it is an error to return null if borderWidth(node) returns
   * a value greater than zero.
   */
  public Paint borderPaint(final int node) {
    return null; }

  /**
   * Returns the text label this node has.  By default this method returns
   * null; returning null is the optimal way to specify that this
   * node has no text label.<p>
   * To specify multiple lines of text in a node label, simply insert the
   * '\n' character between lines of text.
   */
  public String label(final int node) {
    return null; }

  /**
   * Returns the font to use when rendering a text label on this node.
   * By default this method returns null.
   * This return value is ignored if label(node) returns either null or the
   * empty string; it is an error to return null if label(node) returns a
   * non-empty string.
   */
  public Font font(final int node) {
    return null; }

  /**
   * Returns an additional scaling factor that is to be applied to the font
   * used to render text labels; this scaling factor, applied to the point
   * size of the font returned by font(node), yields a new virtual font that
   * is used to actually render text labels.  By default this method returns
   * 1.0.  This return value is ignored if label(node) returns either null or
   * the empty string.
   */
  public double fontScaleFactor(final int node) {
    return 1.0d; }

  /**
   * Returns the paint of the text label on this node.  By default this method
   * returns null.  This return value is ignored if label(node) returns
   * either null or the empty string; it is an error to return null if
   * label(node) returns a non-empty string.
   */
  public Paint labelPaint(final int node) {
    return null; }

  /**
   * By returning one of the LABEL_ANCHOR_TEXT_* constants, specifies
   * where on the text label's logical bounds box an anchor point lies.  This
   * <i>text anchor point</i> together with the
   * node anchor point and label offset vector
   * determines where, relative to the node, the text's logical bounds
   * box is to be placed.  The text's logical bounds box is placed such that
   * the label offset vector plus the node anchor point equals the text anchor
   * point.<p>
   * By default this method returns LABEL_ANCHOR_TEXT_CENTER.  This return
   * value is ignored if label(node) returns either null or the empty string.
   * @see #labelNodeAnchor(int)
   * @see #labelOffsetVector(int, Point2D.Float)
   */
  public byte labelTextAnchor(final int node) {
    return LABEL_ANCHOR_TEXT_CENTER; }

  /**
   * By returning one of the LABEL_ANCHOR_NODE_* constants, specifies
   * where on the node's extents box an anchor point lies.  This
   * <i>node anchor point</i> together with the text anchor point and label
   * offset vector determines where, relative to the node, the text's logical
   * bounds box is to be placed.  The text's logical bounds box is placed
   * such that the label offset vector plus the node anchor point equals the
   * text anchor point.<p>
   * By default this method returns LABEL_ANCHOR_NODE_CENTER.  This return
   * value is ignored if label(node) returns either null or the empty string.
   * @see #labelTextAnchor(int)
   * @see #labelOffsetVector(int, Point2D.Float)
   */
  public byte labelNodeAnchor(final int node) {
    return LABEL_ANCHOR_NODE_CENTER; }

  /**
   * By modifying the contents of the vector parameter, specifies the
   * distance that separates the text anchor point from the node anchor point.
   * This <i>label offset vector</i> together with the text anchor point and
   * node anchor point determines where, relative to the node, the text's
   * logical bounds box is to be placed.  The text's logical bounds box is
   * placed such that the label offset vector plus the node anchor point
   * equals the text anchor point.<p>
   * By default this method sets the vector to be of distance zero.  This
   * method is not invoked if label(node) returns either null or the empty
   * string.  It is a mistake to not modify the vector parameter when this
   * method is invoked.
   * @see #labelTextAnchor(int)
   * @see #labelNodeAnchor(int)
   */
  public void labelOffsetVector(final int node, final Point2D.Float vector) {
    vector.setLocation(0.0f, 0.0f); }

  /**
   * By returning one of the LABEL_WRAP_JUSTIFY_* constants, determines
   * how to justify node labels spanning multiple lines.  The choice made here
   * does not affect the size of the logical bounding box of a node label's
   * text.  The lines of text are justified within that logical bounding
   * box.<p>
   * By default this method returns LABEL_WRAP_JUSTIFY_CENTER.  This return
   * value is ignored if label(node) returns a text string that does not
   * span multiple lines.
   */
  public byte labelJustify(final int node) {
    return LABEL_WRAP_JUSTIFY_CENTER; }

  /**
   * Readers: please ignore this for now; this is a reminder to myself.
   */
  public NodeLabelAreaCallback labelAreaCallback(final int node) {
    return null; }

}

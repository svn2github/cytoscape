package cytoscape.render.stateful;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

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
   * node has no text label.  A node's text label is rendered such that
   * the text is centered on the center of the node.
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

}

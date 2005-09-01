package cytoscape.render.stateful;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Font;

/**
 * Defines visual properties of a node modulo the node size and location.
 * Even though this class is not declared abstract, in most situations it
 * makes sense to override at least some of its methods in order to gain
 * control over graph visual properties.
 */
public class NodeDetails
{

  /**
   * Returns a GraphGraphics.SHAPE_* constant (or a custom node shape) that an
   * instance of GraphGraphics understands.
   * By default this method returns GraphGraphics.SHAPE_RECTANGLE.
   */
  public byte shape(final int node) {
    return GraphGraphics.SHAPE_RECTANGLE; }

  /**
   * Returns the color of the interior of the node shape.  By default this
   * method returns Color.red.  It is an error to return null in this method.
   */
  public Color fillColor(final int node) {
    return Color.red; }

  /**
   * Returns the border width of the node shape.  By default this method
   * returns zero.
   */
  public float borderWidth(final int node) {
    return 0.0f; }

  /**
   * Returns the color of the border of the node shape.  By default this method
   * returns null.  This return value is ignored if borderWidth(node)
   * returns zero; it is an error to return null if borderWidth(node) returns
   * a value greater than zero.
   */
  public Color borderColor(final int node) {
    return null; }

  /**
   * Returns the text label this node has.  By default this method returns
   * null; returning null is the optimal way to specify that this
   * node has no text label.
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
   * Returns the color of the text label on this node.  By default this method
   * returns null.  This return value is ignored if label(node) returns
   * either null or the empty string; it is an error to return null if
   * label(node) returns a non-empty string.
   */
  public Color labelColor(final int node) {
    return null; }

}

package cytoscape.render.stateful;

import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;

/**
 * Defines visual properties of a node modulo the node size and location.
 * Even though this class is not declared abstract, in most situations it
 * makes sense to override at least some of its methods in order to gain
 * control over graph visual properties.
 */
public class NodeDetails
{

  /**
   * Returns a SHAPE_* constant (or a custom node shape) that an instance of
   * cytoscape.render.immed.GraphGraphics understands.
   * By default this method returns SHAPE_RECTANGLE.
   */
  public byte shape(final int node) {
    return GraphGraphics.SHAPE_RECTANGLE; }

  /**
   * Returns the color of the interior of the node shape.  By default this
   * method returns Color.red.
   */
  public Color fillColor(final int node) {
    return Color.red; }

  /**
   * Returns the border width of the node shape.  By default this method
   * returns 0.0f.
   */
  public float borderWidth(final int node) {
    return 0.0f; }

  /**
   * Returns the color of the border of the node shape.  By default this method
   * returns null.
   */
  public Color borderColor(final int node) {
    return null; }

  // What about node label?

}

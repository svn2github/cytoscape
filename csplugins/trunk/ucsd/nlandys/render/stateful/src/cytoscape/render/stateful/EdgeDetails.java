package cytoscape.render.stateful;

import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

/**
 * Defines the visual properties of an edge.  Even though this class is not
 * declared abstract, in most situations it makes sense to override at least
 * some of its methods (especially segmentThickness()) in order to gain
 * control over edge visual properties.<p>
 * To understand the significance of each method's return value, it makes
 * sense to become familiar with the API cytoscape.render.immed.GraphGraphics.
 */
public class EdgeDetails
{

  /**
   * Returns the color of edge in low detail rendering mode.
   * By default this method returns Color.blue.  It is an error to return null
   * in this method.<p>
   * In low detail rendering mode, this is the only method from this class
   * that is looked at.  The rest of the methods in this class define visual
   * properties that are used in full detail rendering mode.  In low detail
   * rendering mode translucent colors are not supported whereas in full
   * detail rendering mode they are.
   */
  public Color colorLowDetail(final int edge) {
    return Color.blue; }

  /**
   * Returns a GraphGraphics.ARROW_* constant; this defines the arrow
   * to use when rendering the edge endpoint touching source node.
   * By default this method returns GraphGraphics.ARROW_NONE.
   * Take note of certain constraints specified in
   * GraphGraphics.drawEdgeFull().
   */
  public byte sourceArrow(final int edge) {
    return GraphGraphics.ARROW_NONE; }

  /**
   * Returns the size of the arrow at edge endpoint touching source node.
   * By default this method returns zero.  This return value is ignored
   * if sourceArrow(edge) returns GraphGraphics.ARROW_NONE.
   * Take note of certain constraints specified in
   * GraphGraphics.drawEdgeFull().
   */
  public float sourceArrowSize(final int edge) {
    return 0.0f; }

  /**
   * Returns the paint of the arrow at edge endpoint touching source node.
   * By default this method returns null.  This return value is ignored if
   * sourceArrow(edge) returns GraphGraphics.ARROW_NONE or
   * GraphGraphics.ARROW_BIDIRECTIONAL; otherwise, it is an error to return
   * null.
   */
  public Paint sourceArrowPaint(final int edge) {
    return null; }

  /**
   * Returns a GraphGraphics.ARROW_* constant; this defines the arrow
   * to use when rendering the edge endpoint at the target node.
   * By default this method returns GraphGraphics.ARROW_NONE.
   * Take note of certain constraints specified in
   * GraphGraphics.drawEdgeFull().
   */
  public byte targetArrow(final int edge) {
    return GraphGraphics.ARROW_NONE; }

  /**
   * Returns the size of the arrow at edge endpoint touching target node.
   * By default this method returns zero.  This return value is ignored
   * if targetArrow(edge) returns GraphGraphics.ARROW_NONE or
   * GraphGraphics.ARROW_MONO.  Take note of certain constraints specified
   * in GraphGraphics.drawEdgeFull().
   */
  public float targetArrowSize(final int edge) {
    return 0.0f; }

  /**
   * Returns the paint of the arrow at edge endpoint touching target node.
   * By default this method returns null.  This return value is ignored if
   * targetArrow(edge) returns GraphGraphics.ARROW_NONE,
   * GraphGraphics.ARROW_BIDIRECTIONAL, or GraphGraphics.ARROW_MONO;
   * otherwise, it is an error to return null.
   */
  public Paint targetArrowPaint(final int edge) {
    return null; }

  /**
   * Returns edge anchors to use when rendering this edge.
   * By default this method returns null; returning null is the optimal
   * way to specify that this edge has no anchors.  Take note of certain
   * constraints, specified in GraphGraphics.drawEdgeFull(), pertaining to
   * edge anchors.<p>
   * The anchors returned are interpreted such that the anchor at index zero
   * (the "first" anchor) is the anchor next to the source node of this edge;
   * the last anchor is the anchor next to the target node of this edge.  The
   * rendering engine works such that if the first anchor lies inside
   * the source node shape or if the last anchor lies inside the target
   * node shape, the edge is not rendered.
   */
  public EdgeAnchors anchors(final int edge) {
    return null; }

  /**
   * Returns the thickness of the edge segment.
   * <font color="red">By default this method returns zero.</font>
   * Take note of certain constraints specified in
   * GraphGraphics.drawEdgeFull().
   */
  public float segmentThickness(final int edge) {
    return 0.0f; }

  /**
   * Returns the paint of the edge segment.
   * By default this method returns Color.blue.  It is an error to
   * return null in this method.
   */
  public Paint segmentPaint(final int edge) {
    return Color.blue; }

  /**
   * Returns the length of dashes on edge segment, or zero to indicate
   * that the edge segment is solid.  By default this method returns zero.
   */
  public float dashLength(final int edge) {
    return 0.0f; }

  /**
   * Returns the text label this edge has.  By default this method returns
   * null; returning null is the optimal way to specify that this
   * edge has no text label.  An edge's text label is rendered such that the
   * text is centered at at point lying on the edge path; the point on
   * edge path is chosen such that it is close to the "middle" of the edge
   * path.
   */
  public String labelText(final int edge) {
    return null; }

  /**
   * Returns the font to use when rendering a text label on this edge.
   * By default this method returns null.
   * This return value is ignored if labelText(edge) returns either null or the
   * empty string; it is an error the return null if labelText(edge) returns a
   * non-empty string.
   */
  public Font labelFont(final int edge) {
    return null; }

  /**
   * Returns an additional scaling factor that is to be applied to the font
   * used to render text labels; this scaling factor, applied to the point
   * size of the font returned by labelFont(edge), yields a new virtual font
   * that is used to actually render text labels.  By default this method
   * returns 1.0.  This return value is ignored if labelText(edge) returns
   * either null or the empty string.
   */
  public double labelScaleFactor(final int edge) {
    return 1.0d; }

  /**
   * Returns the paint of the text label on this edge.  By default this method
   * returns null.  This return value is ignored if labelText(edge) returns
   * either null or the empty string; it is an error to return null if
   * labelText(edge) returns a non-empty string.
   */
  public Paint labelPaint(final int edge) {
    return null; }

}

package cytoscape.render.stateful;

import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import java.awt.Color;

/**
 * Defines the visual properties of an edge.
 */
public abstract class EdgeDetails
{

  public byte arrow0(final int edge) {
    return GraphGraphics.ARROW_NONE; }

  public float arrow0Size(final int edge) {
    return 0.0f; }

  public Color arrow0Color(final int edge) {
    return null; }

  public byte arrow1(final int edge) {
    return GraphGraphics.ARROW_NONE; }

  public float arrow1Size(final int edge) {
    return 0.0f; }

  public Color arrow1Color(final int edge) {
    return null; }

  public EdgeAnchors anchors(final int edge) {
    return null; }

  public abstract float thickness(final int edge);

  public Color color(final int edge) {
    return Color.blue; }

  public float dashLength(final int edge) {
    return 0.0f; }

  // What about edge label?

}

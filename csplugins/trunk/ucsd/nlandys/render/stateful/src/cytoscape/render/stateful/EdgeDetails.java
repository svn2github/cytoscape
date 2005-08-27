package cytoscape.render.stateful;

import cytoscape.render.immed.EdgeAnchors;

import java.awt.Color;

/**
 * Defines the visual properties of an edge.  The methods on this interface
 * are called at an extemely high frequency; the same method with the same
 * arguments on the same instance may be called in intervals of nanoseconds.
 * Therefore, the implementation of this interface must be extremely optimized
 * for speed if there is to be any hope of rendering large graphcs at high
 * speed.  This interface only makes sense in the context of a Mongo.
 */
public interface EdgeDetails
{

  public byte arrow0(int edge);

  public float arrow0Size(int edge);

  public Color arrow0Color(int edge);

  public byte arrow1(int edge);

  public float arrow1Size(int edge);

  public Color arrow1Color(int edge);

  public EdgeAnchors anchors(int edge);

  public float thickness(int edge);

  public Color color(int edge);

  public float dashLength(int edge);

  // What about edge label?

}

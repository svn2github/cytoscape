package cytoscape.render.stateful;

import java.awt.Color;

/**
 * Defines visual properties of a node modulo the node size and location.
 * This interface only makes sense in the context of a Mongo.
 */
public interface NodeDetails
{

  public byte shape(int node);

  public Color fillColor(int node);

  public float borderWidth(int node);

  public Color borderColor(int node);

  // What about node label?

}

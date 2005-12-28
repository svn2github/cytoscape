package ding.view;

import cytoscape.render.stateful.NodeDetails;
import java.util.HashMap;

/*
 * Access to the methods of this class should be synchronized externally if
 * there is a threat of multiple threads.
 */
class DNodeDetails extends NodeDetails
{

  // The values are Byte objects; the bytes are shapes defined in
  // cytoscape.render.immed.GraphGraphics.
  final HashMap m_shapes = new HashMap();

  public byte shape(int node)
  {
    final Object o = m_shapes.get(new Integer(node));
    if (o == null) { return super.shape(node); }
    return ((Byte) o).byteValue();
  }

  /*
   * The shape argument must be pre-checked for correctness.
   */
  void overrideShape(int node, byte shape)
  {
    if (super.shape(node) == shape) { m_shapes.remove(new Integer(node)); }
    else { m_shapes.put(new Integer(node), new Byte(shape)); }
  }

}

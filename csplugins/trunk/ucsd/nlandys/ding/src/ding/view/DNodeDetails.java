package ding.view;

import cytoscape.render.stateful.NodeDetails;
import java.awt.Color;
import java.awt.Paint;
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
  final HashMap m_fillPaints = new HashMap();

  public Color colorLowDetail(int node)
  {
    // TODO: Implement using non-object-oriented hashmap.
    return super.colorLowDetail(node);
  }

  void overrideColorLowDetail(int node, Color color)
  {
  }

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

  public Paint fillPaint(int node)
  {
    final Object o = m_fillPaints.get(new Integer(node));
    if (o == null) { return super.fillPaint(node); }
    return (Paint) o;
  }

  /*
   * The paint argument must be pre-checked for null.  Don't pass null in.
   */
  void overrideFillPaint(int node, Paint paint)
  {
    if (super.fillPaint(node).equals(paint)) {
      m_fillPaints.remove(new Integer(node)); }
    else { m_fillPaints.put(new Integer(node), paint); }
  }

}

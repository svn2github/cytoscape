package cytoscape.graph.layout.algorithm;

/**
 * Represents a <code>MutableGraphLayout</code> with an added constraint
 * such that only certain nodes can be moved.<p>
 * Note from the author on Fri Oct  8 15:20:20 PDT 2004:
 * I don't like the fact that a more restrictive interface
 * <code>SubMutableGraphLayout</code> extends
 * a less restrictive interface <code>MutableGraphLayout</code>.
 * If we cast our instance of <code>SubMutableGraphLayout</code> to
 * a <code>MutableGraphLayout</code> and give it to someone, they won't know
 * that they're only allowed to move certain nodes.  This is a bad design
 * and needs some re-thinking.
 **/
public interface SubMutableGraphLayout extends MutableGraphLayout
{

  /**
   * Tells us whether or not the node at index <code>nodeIndex</code>
   * can be moved by <code>setNodePosition()</code>.
   *
   * @param nodeIndex index of node whose mobility we are querying.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   * @see #setNodePosition(int, double, double)
   */
  public boolean isMovableNode(int nodeIndex);

  /**
   * See the description for this method in the superinterface
   * <code>MutableGraphLayout</code>.
   *
   * @exception UnsupportedOperationException if
   *   <nobr><code>isMovableNode(nodeIndex)</code></nobr> returns
   *   <code>false</code>.
   * @see isMovableNode(int)
   */
  public void setNodePosition(int nodeIndex, double xPos, double yPos);

}

package cytoscape.graph.layout.algorithm;

/**
 * Represents a <code>MutableGraphLayout</code> with an added constraint
 * such that only certain nodes can be moved.
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
   * @see super.setNodePosition(int, double, double)
   */
  public void setNodePosition(int nodeIndex, double xPos, double yPos);

}

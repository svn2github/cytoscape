package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.GraphLayout;

/**
 * This class offers a hook for layout algorithms to operate on.
 */
public interface MutableGraphLayout extends GraphLayout
{

  /**
   * Returns the maximum allowable X value with respect to
   * setting node position.  Node position X values may be set to be within the
   * interval <nobr><code>[0, getMaxSettableWidth()]</code></nobr> only.<p>
   * Note: This method may return a value that is less than the return value
   * of <code>getMaxWidth()</code>; the constraint returned by this method
   * only applies to nodes which satisfy <code>isMovableNode()</code> whereas
   * the constraint returned by <code>getMaxWidth()</code> applies to all
   * nodes.<p>
   * <font color="#ff0000">IMPORTANT!</font>  To &quot;follow correct
   * protocol&quot;, all movable nodes should lie within this constraint
   * at all times, particularly when this <code>MutableGraphLayout</code> is
   * passed to a <code>LayoutAlgorithm</code>'s constructor.
   *
   * @see #setNodePosition(int, double, double)
   * @see #isMovableNode(int)
   * @see #getMaxWidth()
   * @see LayoutAlgorithm
   */
  public double getMaxSettableWidth();

  /**
   * Returns the maximum allowable Y value with respect to
   * setting node position.  Node position Y values may be set to be within the
   * interval <nobr><code>[0, getMaxSettableHeight()]</code></nobr> only.<p>
   * Note: This method may return a value that is less than the return value
   * of <code>getMaxHeight()</code>; the constraint returned by this method
   * only applies to nodes which satisfy <code>isMovableNode()</code> whereas
   * the constraint returned by <code>getMaxHeight()</code> applies to all
   * nodes.<p>
   * <font color="#ff0000">IMPORTANT!</font>  To &quot;follow correct
   * protocol&quot;, all movable nodes should lie within this constraint
   * at all times, particularly when this <code>MutableGraphLayout</code> is
   * passed to a <code>LayoutAlgorithm</code>'s constructor.
   *
   * @see #setNodePosition(int, double, double)
   * @see #isMovableNode(int)
   * @see #getMaxWidth()
   * @see LayoutAlgorithm
   */
  public double getMaxSettableHeight();

  /**
   * Tells us whether or not the node at index <code>nodeIndex</code>
   * can be moved by <code>setNodePosition()</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   * @see #areAllNodesMovable()
   * @see #setNodePosition(int, double, double)
   */
  public boolean isMovableNode(int nodeIndex);

  /**
   * Returns <code>true</code> if and only if
   * <nobr><code>isMovableNode(nodeIndex)</code></nobr> returns
   * <code>true</code> for every <code>nodeIndex</code> in the interval
   * <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   */
  public boolean areAllNodesMovable();

  /**
   * Sets the X,Y position of a node at index <code>nodeIndex</code>.
   * This is a hook for layout algorithms to actually set locations of
   * nodes.  Layout algorithms should call this method.<p>
   * X, Y values set by this method shall be reflected in the return values
   * of <code>getNodePosition()</code>.
   *
   * @exception IllegalArgumentException if
   *   <nobr><code>xPos < 0.0</code></nobr>, if
   *   <nobr><code>xPos > getMaxSettableWidth()</code></nobr>, if
   *   <nobr><code>yPos < 0.0</code></nobr>, or if
   *   <nobr><code>yPos > getMaxSettableHeight()</code></nobr>.
   * @exception UnsupportedOperationException if
   *   <nobr><code>isMoveableNode(nodeIndex)</code></nobr> returns
   *   <code>false</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   * @see #getMaxSettableWidth()
   * @see #getMaxSettableHeight()
   * @see #getNodePosition(int)
   */
  public void setNodePosition(int nodeIndex, double xPos, double yPos);

}

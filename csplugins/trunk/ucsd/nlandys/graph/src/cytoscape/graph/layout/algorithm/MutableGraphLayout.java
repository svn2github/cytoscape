package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.GraphLayout;

/**
 * This class offers a hook for layout algorithms to operate on.
 */
public interface MutableGraphLayout extends GraphLayout
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
   * Returns <code>true</code> if and only if
   * <code>isMovableNode(nodeIx)</code> returns <code>true</code> for every
   * <code>nodeIx</code> in the interval
   * <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   **/
  public boolean areAllNodesMovable();

  /**
   * Sets the X,Y position of a node at index <code>nodeIndex</code>.
   * This is a hook for layout algorithms to actually set locations of
   * nodes.  Layout algorithms should call this method.<p>
   * X, Y values set by this method shall be reflected in the return values
   * of <code>getNodePosition()</code> - that is, if we call
   * <blockquote><code>setNodePosition(nodeIndex, x, y)</code></blockquote>
   * then the subsequent expressions
   * <blockquote>
   * <nobr><code>x == getNodePosition(nodeIndex).getX()</code></nobr><br />
   * <nobr><code>y == getNodePosition(nodeIndex).getY()</code></nobr>
   * </blockquote>
   * both evaluate to <code>true</code> (assuming that <code>nodeIndex</code>,
   * <code>x</code>, and <code>y</code> are allowable parameters).<p>
   * Layout algorithms are encouraged to set node positions such that
   * their X and Y values use the full range of allowable values, including
   * the boundary values <code>0</code>, <code>getMaxWidth()</code>, and
   * <code>getMaxHeight()</code>.  Any notion of node thickness, graph
   * border on perimeter, etc. should be predetermined by the application
   * using a layout algorithm; <code>getMaxWidth()</code> and
   * <code>getMaxHeight()</code> should be defined accordingly by the
   * application using a layout algorithm.
   *
   * @exception IllegalArgumentException if
   *   <nobr><code>xPos < 0.0</code></nobr>, if
   *   <nobr><code>xPos > getMaxWidth()</code></nobr>, if
   *   <nobr><code>yPos < 0.0</code></nobr>, or if
   *   <nobr><code>yPos > getMaxHeight()</code></nobr>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   * @exception UnsupportedOperationException if
   *   <nobr><code>isMovableNode(nodeIndex)</code></nobr> returns
   *   <code>false</code>.
   *
   * @see #getMaxWidth()
   * @see #getMaxHeight()
   * @see #getNodePosition(int)
   * @see #isMovableNode(int)
   */
  public void setNodePosition(int nodeIndex, double xPos, double yPos);

}

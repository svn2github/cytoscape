package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.GraphLayout;

/**
 * This class offers a hook for layout algorithms to operate on.
 */
public interface MutableGraphLayout extends GraphLayout
{

  /**
   * Sets the X,Y position of a node at index <code>nodeIndex</code>.
   * This is a hook for layout algorithms to actually set locations of
   * nodes.  Layout algorithms should call this method.<p>
   * X, Y values set by this method shall be reflected in the return values
   * of <code>getNodePosition()</code>.<p>
   * Layout algorithms are encouraged to set node positions such that
   * their X and Y values use the full range of allowable values, including
   * the boundary values <code>0</code>, <code>getMaxWidth()</code>, and
   * <code>getMaxHeight()</code>.  Any notion of node thickness, graph
   * border on perimeter, etc. should be predetermined by the application
   * using a layout algorithm; <code>getMaxWidth()</code> and
   * <code>getMaxHeight()</code> should be defined accordingly.
   *
   * @exception IllegalArgumentException if
   *   <nobr><code>xPos < 0.0</code></nobr>, if
   *   <nobr><code>xPos > getMaxWidth()</code></nobr>, if
   *   <nobr><code>yPos < 0.0</code></nobr>, or if
   *   <nobr><code>yPos > getMaxHeight()</code></nobr>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   * @see #getMaxWidth()
   * @see #getMaxHeight()
   * @see #getNodePosition(int)
   */
  public void setNodePosition(int nodeIndex, double xPos, double yPos);

}

package org.cytoscape.graph.layout;

import java.awt.geom.Point2D;
import org.cytoscape.graph.Graph;

public interface LayoutGraph extends Graph
{

  /**
   * Returns the maximum allowable value of X positions of nodes.
   * Setting node position X values beyond <code>getMaxWidth()</code> is
   * not allowed.  Node position X values live in the interval
   * <nobr><code>[0, getMaxWidth()]</code></nobr>.
   * @see #setNodePosition(int, double, double)
   */
  public double getMaxWidth();

  /**
   * Returns the maximum allowable value of Y positions of nodes.
   * Setting node position Y values beyond <code>getMaxHeight()</code> is
   * not allowed.  Node position Y values live in the interval
   * <nobr><code>[0, getMaxHeight()]</code></nobr>.
   * @see #setNodePosition(int, double, double)
   */
  public double getMaxHeight();

  /**
   * Tells us whether or not the node at index <code>nodeIndex</code>
   * can be moved by <code>setNodePosition()</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
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
   * nodes.  Layout algorithms should call this method.
   * @exception IllegalArgumentException if
   *   <nobr><code>xPos < 0</code></nobr>, if
   *   <nobr><code>xPos > getMaxWidth()</code></nobr>, if
   *   <nobr><code>yPos < 0</code></nobr>, or if
   *   <nobr><code>yPos > getMaxHeight()</code></nobr>.
   * @exception UnsupportedOperationException if
   *   <nobr><code>isMoveableNode(nodeIndex)</code></nobr> returns
   *   <code>false</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   */
  public void setNodePosition(int nodeIndex, double xPos, double yPos);

  /**
   * Returns the existing X,Y position of a node.  Node positions set
   * via <code>setNodePosition()</code> will be reflected in the return
   * values of this method.  A graph's nodes will have positions even before
   * any layout is performed on the graph (maybe each node's position is
   * at the origin); therefore this method shall never return
   * <code>null</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
   */
  public Point2D getNodePosition(int nodeIndex);

}

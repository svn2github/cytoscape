package cytoscape.graph.layout;

import cytoscape.graph.fixed.FixedGraph;

/**
 * This class represents not just a graph's topology but also a layout of its
 * nodes in 2D space (a "straight-line graph drawing").
 **/
public interface GraphLayout extends FixedGraph
{

  /**
   * Returns the maximum allowable value of X positions of nodes.
   * All X positions of nodes in this graph will lie in the interval
   * [0.0, getMaxWidth()].
   *
   * @see #getNodePosition(int, boolean)
   **/
  public double getMaxWidth();

  /**
   * Returns the maximum allowable value of Y positions of nodes.
   * All Y positions of nodes in this graph will lie in the interval
   * [0.0, getMaxHeight()].
   *
   * @see #getNodePosition(int, boolean)
   **/
  public double getMaxHeight();

  /**
   * Returns the X or Y position of a node.
   *
   * @param node the node whose position we're seeking.
   * @param xPosition if true, return X position; if false, return Y position.
   * @return the X or Y position of node.
   * @exception IllegalArgumentException if specified node is not
   *   a node in this graph.
   **/
  public double getNodePosition(int node, boolean xPosition);

}

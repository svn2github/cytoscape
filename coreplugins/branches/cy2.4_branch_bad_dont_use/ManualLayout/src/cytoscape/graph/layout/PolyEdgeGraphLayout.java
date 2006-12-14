package cytoscape.graph.layout;

/**
 * This class extends <code>GraphLayout</code> to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).<p>
 * For a given edge E in this graph, the definition of a poly-line
 * for E is as follows.
 * If we define a sequence of points {P[0], P[1], ... P[N+1]} where<ul>
 * <li>P[0] is the source node of edge E,</li>
 * <li>P[N+1] is the target node of edge E, and</li>
 * <li>for i not equal to 0 or N+1, P[i] is edge E's anchor
 *     point at index i-1,</li></ul>
 * then the poly-line for edge E is defined to be the union of line segments
 * {S[0], S[1], ... S[N]} where
 * each S[i] is the straight-line segment starting
 * at P[i] and ending at P[i+1].
 **/
public interface PolyEdgeGraphLayout extends GraphLayout
{

  /**
   * Returns the number of anchor points belonging to an edge.
   * In other methods of this
   * interface an anchor point is referenced by the edge to which
   * the anchor point belongs along with the anchor point's index within that
   * edge.  Indices of anchor points within an edge E
   * start at 0 and end at getNumAnchors(E) - 1, inclusive.
   *
   * @return the number of edge anchor points belonging to specified ege.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   **/
  public int getNumAnchors(int edge);

  /**
   * Returns the X or Y position of an edge anchor point.
   *
   * @param edge the edge to which the anchor point whose
   *   position we're seeking belongs.
   * @param anchorIndex the index of anchor point, within specified edge,
   *   whose position we're seeking.
   * @param xPosition if true, return X position of anchor point;
   *   if false, return Y position of anchor point.
   * @return the X or Y position of anchor point with index
   *   anchorIndex within specified edge;
   *   X return values are within the interval
   *   [0.0, getMaxWidth()] and Y return values
   *   are within the interval [0.0, getMaxHeight()].
   *
   * @exception IllegalArgumentException if specified edge is not an
   *   edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge) - 1].
   **/
  public double getAnchorPosition(int edge,
                                  int anchorIndex,
                                  boolean xPosition);

}

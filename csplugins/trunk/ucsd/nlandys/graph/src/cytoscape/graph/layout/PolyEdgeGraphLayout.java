package cytoscape.graph.layout;

import cytoscape.graph.IndexIterator;

/**
 * This class extends <code>GraphLayout</code> to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
 **/
public interface PolyEdgeGraphLayout extends GraphLayout
{

  /**
   * Defines a &quot;poly-line&quot; for edge at index
   * <code>edgeIndex</code>.<p>
   * The poly-line is defined in the following manner.
   * If we define a sequence of points {P[0], P[1], ... P[N+1]} where<ul>
   * <li>P[0] is the source node of edge at index
   *     <code>edgeIndex</code>,</li>
   * <li>P[N+1] is the target node of edge at index
   *     <code>edgeIndex</code>, and</li>
   * <li>for i not equal to 0 or N+1, P[i] is the edge anchor
   *     point whose index is the i-th element in the return value of
   *     <code>getAnchorIndices(edgeIndex)</code>,</li></ul>
   * then the poly-line for edge at index <code>edgeIndex</code> is defined to
   * be the union of line segments {S[0], S[1], ... S[N]} where
   * each S[i] is the straight-line segment starting
   * at P[i] and ending at P[i+1].
   *
   * @return a non-<code>null</code> iteration of indices of edge anchor points
   *   belonging to edge at index <code>edgeIndex</code>; the order of the
   *   iteration is significant - see above description.
   * @exception IndexOutOfBoundsException if <code>edgeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
   **/
  public IndexIterator getAnchorIndices(int edgeIndex);

  /**
   * Returns the X or Y position of an edge anchor point.
   *
   * @param anchorIndex the index of edge anchor point whose position we're
   *   seeking.
   * @param xPosition if <code>true</code>, return X position; if
   *   <code>false</code>, return Y position.
   * @return the X or Y position of edge anchor point at index
   *   <code>anchorIndex</code>.
   * @exception IndexOutOfBoundsException if <code>anchorIndex</code> is not
   *   an index of any edge anchor point on this graph.
   **/
  public double getAnchorPosition(int anchorIndex, boolean xPosition);

}

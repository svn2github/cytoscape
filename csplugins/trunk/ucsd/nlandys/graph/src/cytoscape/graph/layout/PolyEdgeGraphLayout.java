package cytoscape.graph.layout;

import cytoscape.graph.IndexIterator;

/**
 * This class extends <code>GraphLayout</code> to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
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
 **/
public interface PolyEdgeGraphLayout extends GraphLayout
{

  /**
   * Returns the number of anchor points belonging to edge at index
   * <code>edgeIndex</code>.  In other methods of this
   * interface an anchor point is referenced by the index of the edge to which
   * it belongs along with the anchor point's index within that edge.
   * Indices of anchor points within an edge at index <code>edgeIndex</code>
   * start at <code>0</code> and end at
   * <nobr><code>getNumAnchors(edgeIndex) - 1</code></nobr>, inclusive.
   *
   * @return the number of edge anchor points belonging to edge at index
   *   <code>edgeIndex</code>.
   * @exception IndexOutOfBoundsException if <code>edgeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
   **/
  public int getNumAnchors(int edgeIndex);

  /**
   * Returns the X or Y position of an edge anchor point.
   *
   * @param edgeIndex the index of the edge to which the anchor point whose
   *   position we're seeking belongs.
   * @param anchorIndex the index within an edge of the anchor point whose
   *   position we're seeking.
   * @param xPosition if <code>true</code>, return X position; if
   *   <code>false</code>, return Y position.
   * @return the X or Y position of anchor point with index
   *   <code>anchorIndex</code> within edge at index <code>edgeIndex</code>.
   * @exception IndexOutOfBoundsException if <code>edgeIndex</code> is not in
   *   the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
   * @exception IndexOutOfBoundsException if <code>anchorIndex</code> is not
   *   in the interval
   *   <nobr><code>[0, getNumAnchors(edgeIndex) - 1]</code></nobr>.
   **/
  public double getAnchorPosition(int edgeIndex,
                                  int anchorIndex,
                                  boolean xPosition);

}

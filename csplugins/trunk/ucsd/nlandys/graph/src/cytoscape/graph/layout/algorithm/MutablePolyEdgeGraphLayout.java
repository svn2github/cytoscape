package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.PolyEdgeGraphLayout;

/**
 * This class extends <code>MutableGraphLayout</code> to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
 **/
public interface MutablePolyEdgeGraphLayout
  extends PolyEdgeGraphLayout, MutableGraphLayout
{

  /**
   * Deletes an edge anchor point.
   * If this anchor point belongs to edge E, then deletion of this anchor point
   * will have no effect on the order of E's remaining anchor points.
   *
   * @param anchorIndex the index of anchor point we're
   *   trying to delete.
   * @exception IndexOutOfBoundsException if <code>anchorIndex</code> is not
   *   an index of any edge anchor point on this graph.
   * @exception UnsupportedOperationException if the edge anchor point at index
   *   <code>anchorIndex</code> belongs to an edge whose source and target
   *   nodes are both non-movable.
   **/
  public void deleteAnchor(int anchorIndex);

  /**
   * Creates a new anchor on edge with index <code>edgeIndex</code>.<p>
   * <!-- If we define a sequence of points {P[0], P[1], ... P[N+1]} where:<ul>
   * <li>P[0] is the source node of edge at index
   *     <code>edgeIndex</code>,</li>
   * <li>P[N+1] is the target node of edge at index
   *     <code>edgeIndex</code>, and</li>
   * <li>for i not equal to 0 or N+1, P[i] is the edge anchor
   *     point whose index is the i-th element in the return value of
   *     <code>getAnchorIndices(edgeIndex)</code>,</li></ul>
   * then -->
   * This method adds an anchor point to edge at index
   * <code>edgeIndex</code> such that:<ul>
   * <li>the order of existing edge anchor points is not changed and</li>
   * <li>the new anchor point becomes such that its index will be the
   *     <nobr><code>(position+1)</code>-th</nobr>
   *     element in the return value of a subsequent call to
   *     <code>getAnchorIndices(edgeIndex)</code>.</li></ul><p>
   * Returns the index of the newly created anchor.
   *
   * @param edgeIndex new anchor point will be created on edge with
   *   index <code>edgeIndex</code>.
   * @param position defines the position, among other existing anchor
   *   points on this edge, into which to insert this new anchor point; see
   *   above definition of <code>position</code>.
   * @return the anchor index of the newly created anchor point.
   * @exception IndexOutOfBoundsException if <code>position</code>
   *   is less than zero or if <code>position</code> is greater than
   *   the number of existing anchor points on edge at index
   *   <code>edgeIndex</code>.
   * @exception IndexOutOfBoundsException if <code>edgeIndex</code> is not
   *   in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
   * @exception UnsupportedOperationException if edge at index
   *   <code>edgeIndex</code> has source and target
   *   nodes that are both non-movable.
   **/
  public int createAnchor(int edgeIndex, int position);

  /**
   * Sets the X,Y position of an edge anchor point.
   *
   * @param anchorIndex the index of anchor point whose location we're
   *   trying to specify.
   * @param xPosition the desired X position of anchor point at index
   *   <code>anchorIndex</code>.
   * @param yPosition the desired Y position of anchor point at index
   *   <code>anchorIndex</code>.
   * @exception IndexOutOfBoundsException if <code>anchorIndex</code> is not
   *   an index of any edge anchor point on this graph.
   * @exception IllegalArgumentException if
   *   <nobr><code>xPosition < 0.0</code></nobr>, if
   *   <nobr><code>xPosition > getMaxWidth()</code></nobr>, if
   *   <nobr><code>yPosition < 0.0</code></nobr>, or if
   *   <nobr><code>yPosition > getMaxHeight()</code></nobr>.
   * @exception UnsupportedOperationException if the edge anchor point at index
   *   <code>anchorIndex</code> belongs to an edge whose source and target
   *   nodes are both non-movable.
   **/
  public void setAnchorPosition(int anchorIndex, double xPosition,
                                double yPosition);

}

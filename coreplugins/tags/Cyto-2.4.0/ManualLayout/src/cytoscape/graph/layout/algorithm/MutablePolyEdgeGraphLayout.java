package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.PolyEdgeGraphLayout;

/**
 * This class extends MutableGraphLayout to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
 **/
public interface MutablePolyEdgeGraphLayout
  extends PolyEdgeGraphLayout, MutableGraphLayout
{

  /**
   * Deletes an edge anchor point.<p>
   * The deletion of an anchor point is accomplished such that the ordering of
   * remaining anchor points stays the same.  An anchor point [belonging
   * to specified edge] with index greater than
   * anchorIndex will be assigned a new index equal to its
   * previous index minus one; an anchor point with index less than
   * anchorIndex will keep its index.
   *
   * @param edge the edge to which the anchor point to be
   *   deleted belongs.
   * @param anchorIndex the index of anchor point, within specified edge,
   *   which we're trying to delete.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge) - 1].
   * @exception UnsupportedOperationException if specified edge
   *   has source and target nodes that are both
   *   non-movable.
   **/
  public void deleteAnchor(int edge, int anchorIndex);

  /**
   * Creates a new edge anchor point.<p>
   * The creation of an anchor point is accomplished such that the ordering
   * of existing anchor points stays the same.  An existing anchor point
   * [belonging to specified edge] with index greater
   * than or equal to anchorIndex will be assigned a new index
   * equal to its previous index plus one; an existing anchor point with index
   * less than anchorIndex will keep its index.<p>
   * A new anchor point P's X,Y position is the midpoint along the segment
   * whose end points are P's neighbors in the edge poly-line definition;
   * X,Y positions of existing anchor points and nodes are unchanged.
   *
   * @param edge new anchor point will be created on specified edge.
   * @param anchorIndex new anchor point will have index
   *   anchorIndex within specified edge.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge)].
   * @exception UnsupportedOperationException if specified edge
   *   source and target nodes that are both non-movable.
   **/
  public void createAnchor(int edge, int anchorIndex);

  /**
   * Sets the X,Y position of an edge anchor point.<p>
   * X, Y values set by this method shall be reflected in the return values
   * of getAnchorPosition() - that is, if we call
   * <blockquote><code>setAnchorPosition(edge, aInx, x, y)</code></blockquote>
   * then the subsequent expressions
   * <blockquote>
   * <nobr><code>x == getAnchorPosition(edge, aInx, true)</code></nobr><br />
   * <nobr><code>y == getAnchorPosition(edge, aInx, false)</code></nobr>
   * </blockquote>
   * both evaluate to true.
   *
   * @param edge the edge to which the anchor point to be
   *   positioned belongs.
   * @param anchorIndex the index of anchor point, within specified edge,
   *   which we're trying to position.
   * @param xPosition the desired X position of specified edge anchor point.
   * @param yPosition the desired Y position of specified edge anchor point.
   * @exception IllegalArgumentException if specified edge is not
   *   an edge in this graph.
   * @exception IndexOutOfBoundsException if anchorIndex is not
   *   in the interval [0, getNumAnchors(edge) - 1].
   * @exception IllegalArgumentException if specified X position or
   *   specified Y position falls outside of [0.0, getMaxWidth()] and
   *   [0.0, getMaxHeight()], respectively.
   * @exception UnsupportedOperationException if specified edge
   *   has source and target nodes that are both non-movable.
   **/
  public void setAnchorPosition(int edge,
                                int anchorIndex,
                                double xPosition,
                                double yPosition);

}

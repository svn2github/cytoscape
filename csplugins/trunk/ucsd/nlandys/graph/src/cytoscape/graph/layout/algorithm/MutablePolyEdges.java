package cytoscape.graph.layout.algorithm;

import cytoscape.graph.layout.PolyEdges;

public interface MutablePolyEdges extends PolyEdges
{

  public void deleteAnchor(int anchorIndex);

  /**
   * Creates a new anchor on edge with edge index <code>edgeIndex</code>.
   * If <code>position</code> is <code>0</code> then this anchor point
   * defines a straight segment between the source node and this anchor
   * point.  If <code>position</code> is <code>1</code> then this anchor point
   * defines a straight segment between the first existing anchor point and
   * this new anchor point, and this anchor point defines a straight segment
   * between this anchor point and the second existing anchor point.
   * And so on.  If <code>position</code> is infinitely large then
   * this anchor point defines a segment between this anchor point and the
   * target node.
   * Returns the index of the newly created anchor.
   **/
  public int createAnchor(int edgeIndex, int position);

  public void setAnchorPosition(int anchorIndex, double xPosition,
                                double yPosition);

}

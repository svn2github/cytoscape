package cytoscape.graph.layout;

import cytoscape.graph.IndexIterator;

public interface PolyEdgeGraphLayout extends GraphLayout
{

  /**
   * Never returns <code>null</code>.
   * By definition, an edge with no anchor points is a straight edge; that is,
   * end-node positions are not anchor points, by definition.  In general,
   * an edge with N anchor points is an edge composed of N+1 line segments.
   * The order of anchor indices returnes is such that a straight segment
   * connects the source node to the first anchor in the iterator, ..., a
   * straight segment connects the last anchor in the iterator to the target
   * node.
   **/
  public IndexIterator getAnchorIndices(int edgeIndex);

  public double getAnchorPosition(int anchorIndex, boolean xPosition);

}

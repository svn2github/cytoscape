package cytoscape.graph;

/**
 * This interface represents neighboring node information for a given
 * graph.
 */
public interface NodeNeighbors
{

  /**
   * Returns a neighboring nodes list.<p>
   *
   * @param nodeIndex the index of the node whose neighbors we're trying
   *   to find.
   * @return a non-repeating list of indices of all nodes B such that
   *   B is a neighbor of node at index <code>nodeIndex</code>; this method
   *   never returns <code>null</code>.
   * @exception IndexOutOfBoundsException if <code>nodeIndex</code> does not
   *   fall within a suitable interval.
   */
  public IndexIterator getNeighboringNodeIndices(int nodeIndex);

}

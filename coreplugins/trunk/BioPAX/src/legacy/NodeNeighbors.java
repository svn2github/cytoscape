package legacy;

/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * This interface represents neighboring node information for a given
 * graph.<p>
 * Assuming that a binary relation on the set of nodes in a graph defining
 * &quot;is a neighbor of&quot; is provided, it is possible to compute
 * the information returned by this interface.  Nonetheless, a programmer
 * may choose to access a legacy graph implementation's node adjacency list
 * information instead of computing it inside of this &quot;wrapper&quot;
 * graph framework - that's exactly what this interface is for.
 */
public interface NodeNeighbors {

    /**
     * Returns a neighboring nodes list.<p>
     *
     * @param nodeIndex the index of the node whose neighbors we're trying
     *                  to find.
     * @return a non-repeating list of indices of all nodes B such that
     *         B is a neighbor of node at index <code>nodeIndex</code>; this method
     *         never returns <code>null</code>.
     * @throws IndexOutOfBoundsException if <code>nodeIndex</code> does not
     *                                   fall within a suitable interval.
     */
    public IndexIterator getNeighboringNodeIndices(int nodeIndex);

}

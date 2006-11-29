package nct;

import java.util.Iterator;

/**
 * Undirected graphs have edges without direction.
 * <tt>UndirectedGraph</tt> is the opposite of <tt>DirectedGraph</tt>.
 * Consider a graph with two nodes: A and B. If node A is connected to
 * node B, node B <i>is</i> necessarily connected to node A.
 *
 * @author Samad Lotia
 */
public interface UndirectedGraph<N,E> extends Graph<N,E>
{
	/**
	 * @return The degree of <tt>nodeIndex</tt>.
	 */
	public int degree(int nodeIndex);

	/**
	 * @return An iterator for all valid node-indices of the neighors
	 *         of <tt>nodeIndex</tt>. If there are multiple edges between
	 *         the node and any of its neighbors, the neighbor will only
	 *         be returned once.
	 */
	public Iterator<Integer> neighborsIterator(int nodeIndex);

	/**
	 * @return An iterator for all valid edge-indices with edges
	 *         connecting <tt>nodeIndex</tt> and its neighbors.
	 */
	public Iterator<Integer> connectingEdgesIterator(int nodeIndex);
}

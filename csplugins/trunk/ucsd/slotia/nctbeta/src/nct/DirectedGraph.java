package nct;

import java.util.Iterator;

/**
 * Directed graphs have edges with direction.
 * <tt>DirectedGraph</tt> is the opposite of <tt>UndirectedGraph</tt>.
 * Consider a graph with two nodes: A and B. If node A is connected to
 * node B, node B <i>is not</i> necessarily connected to node A.
 *
 * @author Samad Lotia
 */
public interface DirectedGraph<N,E> extends Graph<N,E>
{
	/**
	 * @return The number of edges that point to <tt>node</tt>.
	 */
	public int inDegree(int node);

	/**
	 * @return The number of edges that point away from <tt>node</tt>.
	 */
	public int outDegree(int node);

	/**
	 * @return An iterator of all valid node-indices of neighbors
	 *         with edges connecting to <tt>node</tt> from the neighbors.
	 */
	public Iterator<Integer> inNeighborsIterator(int node);

	/**
	 * @return An iterator of all valid node-indices of neighbors
	 *         with edges connecting from <tt>node</tt> to the neighbors.
	 */
	public Iterator<Integer> outNeighborsIterator(int node);

	/**
	 * @return An iterator of all valid edge-indices with edges connecting
	 * from the neighbors to <tt>node</tt>.
	 */
	public Iterator<Integer> inConnectingEdgesIterator(int node);

	/**
	 * @return An iterator of all valid edge-indices with edges connecting
	 * to the neighbors from <tt>node</tt>.
	 */
	public Iterator<Integer> outConnectingEdgesIterator(int node);
}

package modlab;

import oiler.Graph;

/**
 * An interface for scoring algorithms.
 *
 * <p>Typically, an implementation will only implement one of these
 * methods. For the other methods it does not implement,
 * it should throw <code>UnsupportedOperationException</code>.
 * As a convenience, implementations can extend 
 * <code>AbstractScore</code>.</p>
 */
public interface Score<N,E>
{
	/**
	 * Score a node.
	 *
	 * <p><i>WARNING!</i> This method must be thread-safe, especially
	 * if it is used with <code>SearchExecutor</code>.</p>
	 *
	 * @param network The network containing the node.
	 *        <code>network</code> must be unique to the
	 *        thread that is calling it.
	 * @param node The node-index to score.
	 * @return The score of <code>node</code>.
	 * @throws UnsupportedOperationException if the implementation
	 *         does not support scoring nodes.
	 */
	public double scoreNode(Graph<N,E> network, int node);

	/**
	 * Score an edge.
	 *
	 * <p><i>WARNING!</i> This method must be thread-safe, especially
	 * if it is used with <code>SearchExecutor</code>.</p>
	 *
	 * @param network The network containing the edge.
	 *        <code>network</code> must be unique to the
	 *        thread that is calling it.
	 * @param edge The edge-index to score.
	 * @return The score of <code>edge</code>.
	 * @throws UnsupportedOperationException if the implementation
	 *         does not support scoring edges.
	 */
	public double scoreEdge(Graph<N,E> network, int edge);

	/**
	 * Score a module.
	 *
	 * <p><i>WARNING!</i> This method must be thread-safe, especially
	 * if it is used with <code>SearchExecutor</code>.</p>
	 *
	 * @param module The module to score.
	 * @return The score of <code>module</code>.
	 * @throws UnsupportedOperationException if the implementation
	 *         does not support scoring modules.
	 */
	public double scoreModule(Graph<N,E> module);
}

package modlab;

import oiler.Graph;

/**
 * An interface for algorithms that randomize networks.
 */
public interface Randomize<N,E>
{
	/**
	 * Randomizes the network.
	 *
	 * <p>This method should be called
	 * before calling the <code>search()</code> method of
	 * the <code>Search</code> interface.</p>
	 *
	 * <p><i>WARNING!</i> This method must be thread-safe, especially
	 * if it is used with <code>SearchExecutor</code>.</p>
	 *
	 * @param network The network to randomize.
	 *        <code>network</code> must be unique to the
	 *        thread that is calling it.
	 */
	public void randomize(Graph<N,E> network);
}

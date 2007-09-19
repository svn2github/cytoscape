package modlab;

import oiler.Graph;
import java.util.List;

/**
 * An interface for search algorithms.
 */
public interface Search<N,E>
{
	/**
	 * Performs the search.
	 *
	 * <p><i>WARNING!</i> This method must be thread-safe, especially
	 * if it is used with <code>SearchExecutor</code>.</p>
	 *
	 * @param network The network to search. It is required
	 *        that <code>network</code> is unique
	 *        to the thread that is calling it.
	 * @param score The scoring algorithm to use. It is not
	 *        guaranteed that <code>score</code> is
	 *        unique to the thread that is calling it.
	 * @return A list of modules produced by the search
	 *         algorithm.
	 */
	public List<Graph<N,E>> search(Graph<N,E> network, Score<N,E> score);
}

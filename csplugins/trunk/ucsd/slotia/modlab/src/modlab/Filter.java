package modlab;

import oiler.Graph;
import java.util.List;

/**
 * An interface for algorithms that filter modules.
 */
public interface Filter<N,E>
{
	/**
	 * Filters a list of modules.
	 * @param modules The modules to filter.
	 * @return A list of modules that passed the filter.
	 */
	public List<Graph<N,E>> filter(List<Graph<N,E>> modules);
}

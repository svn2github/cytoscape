package oiler.alg;

import oiler.Graph;
import oiler.util.LongIntHashMap;
import oiler.util.IntIterator;

/**
 * Cache for fast lookup of edge indices
 * between node pairs; only for simple graphs.
 *
 * @author Samad Lotia
 */
public class EdgeIndices
{
	protected LongIntHashMap map;

	/**
	 * Create the cache.
	 * Time complexity: O(number of edges)
	 */
	public EdgeIndices(final Graph<?,?> graph)
	{
		map = new LongIntHashMap(graph.edgeCount());
		final IntIterator edges = graph.edges();
		while (edges.hasNext())
		{
			final int edge = edges.next();
			final int source = graph.edgeSource(edge);
			final int target = graph.edgeTarget(edge);

			map.put(combineKeys(source, target), edge);
			if (graph.edgeType(edge) == Graph.UNDIRECTED_EDGE)
				map.put(combineKeys(target, source), edge);
		}
	}

	public final int edgeIndex(final int source, final int target)
	{
		final int index = map.get(combineKeys(source, target));
		if (index == LongIntHashMap.KEY_NOT_FOUND)
			return Graph.INVALID_INDEX;
		else
			return index;
	}

	private final long combineKeys(final int key0, final int key1)
	{
		// return a long whose low bits are key0 and the high bits are key1
		return ((long) key0) | ((long) key1 << Integer.SIZE);
	}
}

package oiler.alg;

import oiler.Graph;
import oiler.util.IntIterator;

/**
 * Implementation of Floyd-Warshall's algorithm;
 * it computes the shortest distance between
 * every pair of nodes in the graph in cubic time.
 * Distance between two nodes is defined as the minimum
 * number of edges between two nodes.
 *
 * See: http://en.wikipedia.org/wiki/Floyd-Warshall_algorithm
 *
 * @author Samad Lotia
 */
public class NodeDistances
{
	protected int[][] matrix;

	/**
	 * Creates a cache of node distances.
	 */
	public NodeDistances(final Graph<?,?> g)
	{
		final EdgeIndices edgeIndices = new EdgeIndices(g);

		// Create the matrix
		final int n = g.maxNodeIndex() + 1;
		matrix = new int[n][n];

		// Initialize the matrix
		final IntIterator nodes = g.nodes();
		while (nodes.hasNext())
		{
			final int node = nodes.next();
			final IntIterator nodes2 = g.nodes();
			while (nodes2.hasNext())
			{
				final int node2 = nodes2.next();
				if (node == node2)
					matrix[node][node2] = 0;
				else if (edgeIndices.edgeIndex(node, node2) != Graph.INVALID_INDEX)
					matrix[node][node2] = 1;
				else
					matrix[node][node2] = Integer.MAX_VALUE;
			}
		}

		// Do Floyd-Warshall
		final IntIterator ks = g.nodes();
		while (ks.hasNext())
		{
			final int k = ks.next();
			final IntIterator js = g.nodes();
			while (js.hasNext())
			{
				final int j = js.next();
				final IntIterator is = g.nodes();
				while (is.hasNext())
				{
					final int i = is.next();
					if (matrix[i][j] < matrix[i][k] + matrix[k][j])
						matrix[i][j] = matrix[i][k] + matrix[k][j];
				} // end while (is)
			} // end while (js)
		} // end while (ks)
	}

	public final int distance(final int source, final int target)
	{
		return matrix[source][target];
	}
}

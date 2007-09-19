package oiler.alg;

import oiler.Graph;
import oiler.util.IntStack;
import oiler.util.IntIterator;
import oiler.util.IntHashSet;

/**
 * Breadth first search algorithm.
 *
 * <p>Example:</p>
 * <p><pre>
 * Graph&lt;String,Double&gt; graph = ...;
 * BreathFirst&lt;String,Double&gt; myBFS = new BreadthFirst&lt;String,Double&gt;()
 * {
 *   public boolean encounteredNode(Graph&lt;String,Double&gt; graph, int node, int fromNode, int depth)
 *   {
 *     System.out.println("Node " + graph.nodeObject(node) + " from " + graph.nodeObject(fromNode) + " at depth " + depth);
 *     return true;
 *   }
 * };
 * myBFS.search(graph);
 * </pre></p>
 *
 * @author Samad Lotia
 */
public abstract class BreadthFirst<N,E>
{
	public BreadthFirst() {}

	/**
	 * Called on every node the <code>BreadthFirst</code> encounters.
	 * @param graph The Graph <code>BreadthFirst</code> is searching
	 * @param node The node encountered by the algorithm
	 * @param fromNode The node <code>BreadthFirst</code> moved from to get to <code>node</code>
	 * @param depth The distance from <code>node</code> to the starting node
	 * @return True if <code>BreadthFirst</code> should continue searching, false if it should terminate searching
	 */
	public abstract boolean encounteredNode(Graph<N,E> graph, int node, int fromNode, int depth);

	/**
	 * Initiate breadth first search.
	 * All edges of the default type will be traversed. It will traverse every node
	 * at all depths.
	 *
	 * Calls <code>encounteredNode</code> on every node it finds. It will not call
	 * it for <code>startNode</code>.
	 *
	 * @param graph The Graph to search
	 * @param startNode The node to initiate searching from
	 */
	public void search(final Graph<N,E> graph, final int startNode)
	{
		search(graph, startNode, Integer.MAX_VALUE);
	}
	
	/**
	 * Initiate breadth first search.
	 * All edges of the default type will be traversed.
	 *
	 * Calls <code>encounteredNode</code> on every node it finds. It will not call
	 * it for <code>startNode</code>.
	 *
	 * @param graph The Graph to search
	 * @param startNode The node to initiate searching from
	 * @param maxDepth The maximum distance from <code>startNode</code> to nodes it finds.
	 * 		Pass <code>Integer.MAX_VALUE</code> for no limit to the maximum distance.
	 * 		<code>maxDepth</code> must be <code>1 &le;= maxDepth &le;= Integer.MAX_VALUE</code>.
	 */
	public void search(final Graph<N,E> graph, final int startNode, final int maxDepth)
	{
		search(graph, startNode, maxDepth, graph.defaultEdgeType());
	}

	/**
	 * Initiate breadth first search.
	 * Calls <code>encounteredNode</code> on every node it finds. It will not call
	 * it for <code>startNode</code>.
	 *
	 * @param graph The Graph to search
	 * @param startNode The node to initiate searching from
	 * @param maxDepth The maximum distance from <code>startNode</code> to nodes it finds.
	 * 		Pass <code>Integer.MAX_VALUE</code> for no limit to the maximum distance.
	 * 		<code>maxDepth</code> must be <code>1 &le;= maxDepth &le;= Integer.MAX_VALUE</code>.
	 * @param edgeType The types of edges <code>BreadthFirst</code> should traverse.
	 */
	public void search(final Graph<N,E> graph, final int startNode, final int maxDepth, final byte edgeType)
	{
		if (graph == null)
			throw new IllegalArgumentException("graph == null");
		if (maxDepth <= 0)
			throw new IllegalArgumentException("maxDepth <= 0");
	
		final IntStack nodeStack = new IntStack();
		final IntStack distanceStack = new IntStack();
		final IntHashSet nodesSeen = new IntHashSet(32);

		nodeStack.push(startNode);
		distanceStack.push(0);

		while (!nodeStack.isEmpty())
		{
			final int node = nodeStack.pop();
			final int distance = distanceStack.pop();
			nodesSeen.add(node);

			final IntIterator neighbors = graph.adjacentNodes(node, edgeType);
			while (neighbors.hasNext())
			{
				final int neighbor = neighbors.next();
				final int neighborDistance = distance + 1;

				if (nodesSeen.contains(neighbor))
					continue;

				if (!encounteredNode(graph, neighbor, node, neighborDistance))
					return;

				if (neighborDistance < maxDepth)
				{
					nodeStack.push(neighbor);
					distanceStack.push(neighborDistance);
				}
			}
		}
	}
}

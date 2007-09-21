package pinnaclez;

import java.util.List;
import java.util.ArrayList;
import oiler.Graph;
import oiler.alg.BreadthFirst;
import oiler.util.IntHashSet;
import oiler.util.IntIterator;
import modlab.Search;
import modlab.Score;

/**
 * A greedy search algorithm, using
 * StackSubsetGraph for the modules.
 *
 * <p>This search algorithm does not
 * require any specific node or edge
 * object types.</p>
 *
 * <p>This search algorithm requires an
 * order in the nodes of a module, where
 * nodes added first come before nodes
 * added later.</p>
 *
 * <p>Synopsis of the algorithm:</p>
 * <p><ul>
 * <li>It iterates through each node in
 * the network, using this node as a starting
 * point for the greedy search. A module
 * is created for each node in the network.
 * This class uses StackSubsetGraph for
 * the modules. The nodes in StackSubsetGraph
 * is an ordered set, where the starting node
 * (also called the seed) of the module
 * is always the first node in a module's nodes.</li>
 *
 * <li>It finds the best neighbor for each module, and
 * add that node to that module. If there are no
 * good neighbors found, the algorithm stops
 * adding nodes to that module. If the nodes in
 * a module is equal to maxModuleSize, the algorithm
 * stops adding nodes.</li>
 *
 * <li>The algorithm adds a good neighbor to the
 *     module if it fits this criteria:
 *   <ol>
 *   <li>must be an immediate neighbor of any
 *       node in the module,</li>
 *   <li>have a degree less than maxNodeDegree,</li>
 *   <li>must not already be in the module,</li>
 *   <li>must not exceed the distance from the
 *       starting node specified by maxRadius,</li>
 *   <li>and it imrpoves the module's score by at least
 *       minImprovement.</li>
 *   </ol>
 * </li>
 * </ul></p>
 */
public class GreedySearch<N,E> implements Search<N,E>
{
	protected final int maxModuleSize;
	protected final int maxRadius;
	protected final int maxNodeDegree;
	protected final double minImprovement;

	public GreedySearch()
	{
		this(20, 2, 300, 0.05);
	}

	/**
	 * @param maxModuleSize Max number of nodes in a module
	 * @param maxRadius The max distance between any two nodes in
	 *                  the module is 2*maxRadius. If there is no
	 *                  max distance (in other words, the max distance
	 *                  is infinity), set this value to 
	 *                  <code>Integer.MAX_VALUE</code>.
	 * @param maxNodeDegree Max degree of all nodes in the module.
	 * @param minImprovement Min percentage improvement when a neighbor
	 *                       is considered; range: (0,1)
	 */
	public GreedySearch(final int maxModuleSize, final int maxRadius, final int maxNodeDegree, final double minImprovement)
	{
		this.maxModuleSize = maxModuleSize;
		this.maxRadius = maxRadius;
		this.maxNodeDegree = maxNodeDegree;
		this.minImprovement = minImprovement;
	}
	
	public List<Graph<N,E>> search(final Graph<N,E> network, final Score<N,E> score)
	{
		final ArrayList<Graph<N,E>> modules = new ArrayList<Graph<N,E>>(network.nodeCount());
		final IntIterator nodes = network.nodes();
		while (nodes.hasNext())
		{
			final int node = nodes.next();
			final IntHashSet allowableNodes = (maxRadius == Integer.MAX_VALUE ? null : getNeighborsSet(network, node, maxRadius));
			final StackSubsetGraph<N,E> module = new StackSubsetGraph(network, node);
			module.setScore(score.scoreModule(module));
			while (module.nodeCount() < maxModuleSize)
				if (!getNextBestNeighbor(network, score, module, allowableNodes))
					break;
			modules.add(module);
		}
		return modules;
	}

	private final IntHashSet getNeighborsSet(final Graph<N,E> network, final int startNode, final int depth)
	{
		final IntHashSet result = new IntHashSet(16);
		final BreadthFirst<N,E> breadthFirst = new BreadthFirst<N,E>()
		{
			public final boolean encounteredNode(final Graph<N,E> network, final int node, final int fromNode, final int depth)
			{
				result.add(node);
				return true;
			}
		};
		breadthFirst.search(network, startNode, depth);
		return result;
	}

	private boolean getNextBestNeighbor(	final Graph<N,E>		network,
						final Score<N,E>		score,
						final StackSubsetGraph<N,E>	module,
						final IntHashSet		allowableNodes)
	{
		// Step 1: Calculate the neighbors.
		// This is the set of nodes we will need to check
		// for the best module. Neighbors are the
		// adjacent nodes of every node in the module.
		final IntHashSet neighbors = new IntHashSet();
		final IntIterator moduleNodes = module.nodes();
		while (moduleNodes.hasNext())
		{
			final int node = moduleNodes.next();

			// Do not consider nodes whose degree exceeds
			// the maxNodeDegree criteria
			if (network.degree(node) >= maxNodeDegree)
				continue;

			final IntIterator neighborsIterator = network.adjacentNodes(node);
			while (neighborsIterator.hasNext())
			{
				final int neighbor = neighborsIterator.next();
				// Do not include nodes that are already in the module
				if (module.nodeExists(neighbor))
					continue;
				// Do not include nodes not in the allowableNodes set if it exists.
				if (allowableNodes != null && !allowableNodes.contains(neighbor))
					continue;
				neighbors.add(neighbor);
			}
		}

		// Step 2: Determine which neighbor in the
		// neighbors set has the best score.
		double bestScore = module.score;
		int bestNeighbor = -1;

		final IntIterator iterator = neighbors.iterator();
		while (iterator.hasNext())
		{
			final int node = iterator.next();
			module.addNode(node);

			// Calculate the score
			final double moduleScore = score.scoreModule(module);
			if (moduleScore > bestScore)
			{
				bestScore = moduleScore;
				bestNeighbor = node;
			}

			module.removeNode();
		}

		final double ratio = (bestScore - module.score()) / module.score();
		if ((ratio < minImprovement) && module.score() != -1)
			bestNeighbor = -1;

		if (bestNeighbor < 0)
			return false;
		
		module.addNode(bestNeighbor);
		module.setScore(bestScore);
		return true;
	}
}

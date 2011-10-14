package csplugins.jActiveModules;
//------------------------------------------------------------------------------

import giny.model.GraphPerspective;
import giny.model.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.logger.CyLogger;

public class GreedySearchThread extends Thread {

	private static CyLogger logger = CyLogger.getLogger(GreedySearchThread.class);
	
	int max_depth, search_depth;
	ActivePathFinderParameters apfParams;
	Iterator nodeIterator;
	MyProgressMonitor pm;
	HashMap node2BestComponent;
	/**
	 * Track the best score generated from the current starting point
	 */
	double bestScore;

	/**
	 * Map from a node to the number of nodes which are dependent on this node
	 * for connectivity into the graph
	 */
	HashMap node2DependentCount;

	/**
	 * Map from a node to it's predecessor in the search tree When we remove
	 * this node, that predecessor may be optionally added to the list of
	 * removable nodes, dependending if it has any other predecessors
	 */
	HashMap node2Predecessor;

	/**
	 * Lets us know if we need to repeat the greedy search from a new starting
	 * point
	 */
	boolean greedyDone;
	/**
	 * Determines which nodes are within max depth of the starting point
	 */
	HashSet withinMaxDepth;
	Node[] nodes;
	GraphPerspective graph;
	public GreedySearchThread(GraphPerspective graph, ActivePathFinderParameters apfParams,
			Collection nodeList, MyProgressMonitor tpm, HashMap temp_hash,
			Node[] node_array) {
		this.apfParams = apfParams;
		max_depth = apfParams.getMaxDepth();
		search_depth = apfParams.getSearchDepth();
		this.nodeIterator = nodeList.iterator();
		pm = tpm;
		node2BestComponent = temp_hash;
		nodes = node_array;
		this.graph = graph;
		logger.info("Max Depth: " + max_depth);
		logger.info("Search Depth: " + search_depth);
	}
	/**
	 * Recursively find the nodes within a max depth
	 */
	private void initializeMaxDepth(Node current, int depth) {
		withinMaxDepth.add(current);
		if (depth > 0) {
			Iterator listIt = graph.neighborsList(current).iterator();
			while (listIt.hasNext()) {
				Node myNode = (Node) listIt.next();
				if (!withinMaxDepth.contains(myNode)) {
					initializeMaxDepth(myNode, depth - 1);
				}
			}
		}
	}

	/**
	 * Runs the greedy search algorithm. This function will run a greedy search
	 * iteratively using each node of the graph as a starting point
	 */
	public void run() {
		boolean done = false;
		Node seed = null;
		synchronized (nodeIterator) {
			if (nodeIterator.hasNext()) {
				seed = (Node) nodeIterator.next();
			} else {
				done = true;
			}
		}
		while (!done) {

			// determine which nodes are within max-depth
			// of this starting node and add them to a hash set
			// so we can easily identify them
			withinMaxDepth = new HashSet();
			// if the user doesn't wish to limit the maximum
			// depth, just add every node into the max depth
			// hash, thus all nodes are accepted as possible
			// additions
			if (!apfParams.getEnableMaxDepth()) {
				for (int j = 0; j < nodes.length; j++) {
					withinMaxDepth.add(nodes[j]);
				}
			} else {
				// recursively find the nodes within a max depth
				initializeMaxDepth(seed, max_depth);
			}

			// set the neighborhood of nodes to initially be only
			// the single node we are starting the search from
			Component component = new Component();
			component.addNode(seed);
			// make sure that the seed is never added to the list of removables
			node2DependentCount = new HashMap();
			node2Predecessor = new HashMap();
			node2DependentCount.put(seed, new Integer(1));
			// we don't need to make a predecessor entry for the seed,
			// since it should never be added to the list of removable nodes
			HashSet removableNodes = new HashSet();
			bestScore = Double.NEGATIVE_INFINITY;
			runGreedySearchRecursive(search_depth, component, seed,
					removableNodes);
			runGreedyRemovalSearch(component, removableNodes);
			Iterator it = component.getNodes().iterator();

			synchronized (node2BestComponent) {
				// node2BestComponent.put(seed,component);
				// }
				while (it.hasNext()) {
					Node current = (Node) it.next();
					Component oldBest = (Component) node2BestComponent
							.get(current);
					if (oldBest == null
							|| oldBest.getScore() < component.getScore()) {
						node2BestComponent.put(current, component);
					}
				}
			}
			if (pm != null) {
				synchronized (pm) {
					pm.update();
				}
			}

			synchronized (nodeIterator) {
				if (nodeIterator.hasNext()) {
					seed = (Node) nodeIterator.next();
				} else {
					done = true;
				}
			}
		}
	}

	/**
	 * Recursive greedy search function. Called from runGreedySearch() to a
	 * recursive set of calls to greedily identify high scoring networks. The
	 * idea for this search is that we make a recursive call for each addition
	 * of a node from the neighborhood. At each stage we check to see if we have
	 * found a higher scoring network, and if so, store it in one of the global
	 * variables. You know how in the Wonder Twins, one of them turned into an
	 * elephant and the other turned into a bucket of water? This function is
	 * like the elephant.
	 * 
	 * @param depth
	 *            The remaining depth allowed for this greed search.
	 * @param component
	 *            The current component we are branching from.
	 * @param lastAdded
	 *            The last node added.
	 * @param removableNodes
	 *            Nodes that can be removed. 
	 */
	private boolean runGreedySearchRecursive(int depth, Component component,
			Node lastAdded, HashSet removableNodes) {
		boolean improved = false;
		// score this component, check and see if the global top scores should
		// be updated, if we have found a better score, then return true
		if (component.getScore() > bestScore) {
			depth = search_depth;
			improved = true;
			bestScore = component.getScore();
		}

		if (depth > 0) {
			// if depth > 0, otherwise we are out of depth and the recursive
			// calls will end
			// Get an iterator of nodes which are next to the
			Iterator nodeIt = graph.neighborsList(lastAdded).iterator();
			boolean anyCallImproved = false;
			removableNodes.remove(lastAdded);
			int dependentCount = 0;
			while (nodeIt.hasNext()) {
				Node newNeighbor = (Node) nodeIt.next();
				// this node is only a new neighbor if it is not currently
				// in the component.
				if (withinMaxDepth.contains(newNeighbor)
						&& !component.contains(newNeighbor)) {
					component.addNode(newNeighbor);
					removableNodes.add(newNeighbor);
					boolean thisCallImproved = runGreedySearchRecursive(
							depth - 1, component, newNeighbor, removableNodes);
					if (!thisCallImproved) {
						component.removeNode(newNeighbor);
						removableNodes.remove(newNeighbor);
					} // end of if ()
					else {
						dependentCount += 1;
						anyCallImproved = true;
						node2Predecessor.put(newNeighbor, lastAdded);
					} // end of else
				} // end of if ()
			}
			improved |= anyCallImproved;
			if (dependentCount > 0) {
				removableNodes.remove(lastAdded);
				node2DependentCount.put(lastAdded, new Integer(dependentCount));
			} // end of if ()

		}
		return improved;
	}

	public void runGreedyRemovalSearch(Component component,
			HashSet removableNodes) {
		LinkedList list = new LinkedList(removableNodes);
		while (!list.isEmpty()) {
			Node current = (Node) list.removeFirst();
			component.removeNode(current);
			double score = component.getScore();
			if (score > bestScore) {
				bestScore = score;
				Node predecessor = (Node) node2Predecessor.get(current);
				int dependentCount = ((Integer) node2DependentCount
						.get(predecessor)).intValue();
				dependentCount -= 1;
				if (dependentCount == 0) {
					removableNodes.add(predecessor);
				} // end of if ()
				else {
					node2DependentCount.put(predecessor, new Integer(
							dependentCount));
				} // end of else

			} // end of if ()
			else {
				component.addNode(current);
			} // end of else

		} // end of while ()

	}
}

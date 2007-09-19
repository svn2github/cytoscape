//============================================================================
// 
//  file: CompatibilityGraph.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================
package nct.networkblast;

import nct.graph.*;

import nct.score.ScoreModel;

import java.lang.*;

import java.util.*;
import java.util.logging.Logger;


/**
 * This class creates a compatibility graph based on the homology
 * of proteins between two species and the interaction graphs for
 * the given species.
 */
public class CompatibilityGraph extends BasicGraph<CompatibilityNode<String, Double>, Double> {
	protected Map<String, Map<String, Double>> homologyMap;
	protected Map<String, Map<String, String>> edgeDescMap;
	protected List<?extends DistanceGraph<String, Double>> interactionGraphs;
	protected KPartiteGraph<String, Double, ?extends DistanceGraph<String, Double>> homologyGraph;
	protected ScoreModel<String, Double> scoreModel;

	/**
	 * 
	 */
	public boolean allowZero = false;
	private static Logger log = Logger.getLogger("networkblast");

	/**
	 * Constructor.
	 * @param homologyGraph A k-partite graph where edges represent homology relations between
	 * proteins and partitions represent species/organisms.
	 * @param interactionGraphs
	 * @param scoreModel The scoring model used to score the edges of the compat graph.
	 */
	public CompatibilityGraph(KPartiteGraph<String, Double, ?extends DistanceGraph<String, Double>> homologyGraph,
	                          List<?extends DistanceGraph<String, Double>> interactionGraphs,
	                          ScoreModel<String, Double> scoreModel) {
		super();

		try {
			if (homologyGraph.getNumPartitions() != 2)
				throw new Exception("We can only handle 2 graphs at the moment");

			this.homologyGraph = homologyGraph;
			this.interactionGraphs = interactionGraphs;
			this.scoreModel = scoreModel;

			edgeDescMap = new HashMap<String, Map<String, String>>();

			createCompatGraph();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert (weightMap.size() > 0) : "No edges added to graph!";

		log.info("compat graph construction finished");
		log.info("number of nodes: " + numberOfNodes());
		log.info("number of edges: " + numberOfEdges());
	}

	/**
	 * Creates the compatibility graph.
	 */
	private void createCompatGraph() throws Exception {
		List<CompatibilityNode<String, Double>> listOfCompatibilityNodes = getCompatNodes();

		List<?extends DistanceGraph<String, Double>> graphs = homologyGraph.getPartitions();

		for (int x = 0; x < listOfCompatibilityNodes.size(); x++) {
			CompatibilityNode<String, Double> nodeBase = listOfCompatibilityNodes.get(x);

			for (int y = x + 1; y < listOfCompatibilityNodes.size(); y++) {
				CompatibilityNode<String, Double> nodeBranch = listOfCompatibilityNodes.get(y);
				evaluateNodes(graphs, nodeBase, nodeBranch);
			}
		}

		// This isn't strictly necessary, but it doesn't cost us much and is
		// useful for debugging.	
		for (Edge<CompatibilityNode<String, Double>, Double> e : getEdgeList()) {
			CompatibilityNode<String, Double> source = e.getSourceNode();
			CompatibilityNode<String, Double> target = e.getTargetNode();

			// split the compat nodes into their constituent parts
			double escore = 0;

			for (int i = 0; i < graphs.size(); i++) {
				Graph<String, Double> part = graphs.get(i);

				String src = source.getNode(part);
				String tar = target.getNode(part);
				escore += scoreModel.scoreEdge(src, tar, part);
			}

			setEdgeWeight(source, target, escore);
		}
	}

	/**
	 * In general, nodes in the compatibility graph are k-cliques in the homology graph
	 * which means we "only" have to enumerate the k-cliques in the k-partite graph. For
	 * k=2, this is easy: all edges represent a 2-clique and thus a compatibility node.
	 * For k&gt;2, the problem is (much) harder and we haven't implemented a solution yet.
	 */
	private List<CompatibilityNode<String, Double>> getCompatNodes() {
		List<CompatibilityNode<String, Double>> compatNodes = new ArrayList<CompatibilityNode<String, Double>>();

		List<?extends DistanceGraph<String, Double>> graphs = homologyGraph.getPartitions();

		// at the momement this only works for graphs of size 2
		Set<Edge<String, Double>> edges = homologyGraph.getEdges();

		for (Edge<String, Double> e : edges) {
			String src = e.getSourceNode();
			String tar = e.getTargetNode();

			// TODO Super hacky!!!!!
			// nodes need to be listed in the order in which the graphs are listed!!!
			CompatibilityNode<String, Double> c = new CompatibilityNode<String, Double>();

			if (graphs.get(0).isNode(src)) {
				//String[] s = { src, tar };
				//compatNodes.add(s);
				c.add(graphs.get(0), src);
				c.add(graphs.get(1), tar);
			} else {
				//String[] s = { tar, src };
				//compatNodes.add(s);
				c.add(graphs.get(1), src);
				c.add(graphs.get(0), tar);
			}

			compatNodes.add(c);
		}

		return compatNodes;
	}

	/**
	 * The method that determines which nodes to add, adds them
	 * if appropriate, and calculates the edge score.
	 * @param partitionGraphs Possibly used to
	 * @param nodeBase An array of nodes from the respective
	 * partition graphs that form a potential compatibility node.
	 * @param nodeBranch An array of nodes from the respective
	 * partition graphs that form a potential compatibility node.
	 */
	private void evaluateNodes(List<?extends DistanceGraph<String, Double>> partitionGraphs,
	                           CompatibilityNode<String, Double> nodeBase,
	                           CompatibilityNode<String, Double> nodeBranch) throws Exception {
		boolean foundOne = false;
		byte[] distance = new byte[partitionGraphs.size()];

		for (DistanceGraph<String, Double> g : partitionGraphs) {
			String base = nodeBase.getNode(g);
			String branch = nodeBranch.getNode(g);

			int z = nodeBase.getIndex(g);
			if ( z != nodeBranch.getIndex(g) ) 
				throw ( new Exception("compat node index error   base: " + z + 
				                      " branch: " + nodeBranch.getIndex(g) + " base node: " + 
									  base + " branch node: " + branch + 
									  " -- make sure node names are unique between input files!") );

			distance[z] = g.getDistance(base, branch);

			if (distance[z] == (byte) 1)
				foundOne = true;

			// don't allow nodes with distance 3 
			if (distance[z] == (byte) 3)
				return;

			// only include 0 if we're allowed
			if ((distance[z] == (byte) 0) && !allowZero)
				return;
		}

		if (!foundOne)
			return;

		StringBuffer distDesc = new StringBuffer();

		for (int z = 0; z < distance.length; z++)
			distDesc.append(Byte.toString(distance[z]));

		addNode(nodeBase);
		addNode(nodeBranch);

		addEdge(nodeBase, nodeBranch, new Double(0), distDesc.toString());

		return;
	}
}

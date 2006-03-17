
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



package nct.networkblast.graph;

import java.lang.*;
import java.util.*;
import java.util.logging.Logger;

import nct.networkblast.score.ScoreModel;
import nct.networkblast.graph.compatibility.CompatibilityCalculator;
import nct.graph.Graph;
import nct.graph.DistanceGraph;
import nct.graph.KPartiteGraph;
import nct.graph.Edge;
import nct.graph.basic.BasicGraph;

/**
 * This class creates a compatibility graph based on the homology
 * of proteins between two species and the interaction graphs for
 * the given species.
 */
public class CompatibilityGraph extends BasicGraph<String,Double> {

	protected Map<String,Map<String,Double>> homologyMap; 
	protected Map<String,Map<String,String>> edgeDescMap; 
	protected List<? extends DistanceGraph<String,Double>> interactionGraphs;
	protected KPartiteGraph<String,Double,? extends DistanceGraph<String,Double>> homologyGraph;
	protected ScoreModel<String,Double> scoreModel;
	protected CompatibilityCalculator compatCalc;

	private static Logger log = Logger.getLogger("networkblast");

	/**
	 * Constructor.
	 * @param homologyGraph A k-partite graph where edges represent homology relations between
	 * proteins and partitions represent species/organisms.
	 * @param interactionGraphs 
	 * @param scoreModel The ScoreModel used to score an edge in the graph. 
	 * @param compatCalc The CompatibilityCalculator used to determine whether two possible compatibility 
	 * nodes should be added to the compatibility graph and if so, adds the nodes and edge.
	 */
	public CompatibilityGraph(KPartiteGraph<String,Double,? extends DistanceGraph<String,Double>> homologyGraph, 
				  List<? extends DistanceGraph<String,Double>> interactionGraphs, 
				  ScoreModel<String,Double> scoreModel,
				  CompatibilityCalculator compatCalc) {
		super();

		try {

		if ( homologyGraph.getNumPartitions() != 2 )
			throw new Exception("We can only handle 2 graphs at the moment");

		this.homologyGraph = homologyGraph;
		this.interactionGraphs = interactionGraphs;
		this.scoreModel = scoreModel;
		this.compatCalc = compatCalc;

		edgeDescMap = new HashMap<String,Map<String,String>>(); 

		createCompatGraph();

		} catch (Exception e) { e.printStackTrace(); }

		assert( weightMap.size() > 0 ) : "No edges added to graph!";

		log.info("compat graph construction finished");
		log.info("number of nodes: " + numberOfNodes());
		log.info("number of edges: " + numberOfEdges());
	}

	/**
	 * Creates the compatibility graph.  
	 */
	private void createCompatGraph() {

		List<String[]> listOfCompatibilityNodes = getCompatNodes();
		

		List<? extends DistanceGraph<String,Double>> graphs = homologyGraph.getPartitions();
		int numGraphs = graphs.size(); 

		for ( int x = 0; x < listOfCompatibilityNodes.size(); x++ ) {
			String[] nodeBase = listOfCompatibilityNodes.get(x);
			for ( int y = x+1; y < listOfCompatibilityNodes.size(); y++ ) {
				String[] nodeBranch = listOfCompatibilityNodes.get(y);
				compatCalc.calculate(this,graphs,nodeBase,nodeBranch);

			}
		}
	}


	/**
	 * In general, nodes in the compatibility graph are k-cliques in the homology graph
	 * which means we "only" have to enumerate the k-cliques in the k-partite graph. For
	 * k=2, this is easy: all edges represent a 2-clique and thus a compatibility node.  
	 * For k&gt;2, the problem is (much) harder and we haven't implemented a solution yet. 
	 */
	private List<String[]> getCompatNodes() {

		List<String[]> compatNodes = new ArrayList<String[]>();

		List<? extends DistanceGraph<String,Double>> graphs = homologyGraph.getPartitions();

		// at the momement this only works for graphs of size 2
		Set<Edge<String,Double>> edges = homologyGraph.getEdges();		
		for ( Edge<String,Double> e: edges ) {
			String src = e.getSourceNode();	
			String tar = e.getTargetNode();	
			// TODO Super hacky!!!!!
			// nodes need to be listed in the order in which the graphs are listed!!!
			if ( graphs.get(0).isNode(src) ) {
				String[] s = { src, tar };
				compatNodes.add(s);
			} else {
				String[] s = { tar, src };
				compatNodes.add(s);
			}
		}
		return compatNodes;
	}
}

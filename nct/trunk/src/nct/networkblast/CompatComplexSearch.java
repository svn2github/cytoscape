
//============================================================================
// 
//  file: CompatComplexSearch.java
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

import java.util.*;
import java.util.logging.Logger;
import nct.score.ScoreModel;
import nct.graph.BasicGraph;
import nct.graph.Graph;
import nct.graph.Edge;
import nct.search.*;

/**
 * This class implements the SearchGraph interface.  It implements the
 * greedy search algorithm described in the supplemental material of
 * Sharan, et al. 2005. "Conserved patterns of protein interaction in
 * mulitple species." PNAS, 102(6),1974-1979.
 * Complexes are defined as  branched or unbranched pathways invovling 
 * seedSize or more nodes.
 */
public class CompatComplexSearch<NodeType extends Comparable<? super NodeType>> 
	implements SearchGraph<NodeType,Double> {

	protected int seedSize;
	protected int maxComplexSize;
	protected boolean createSeeds;
	protected List<Graph<NodeType,Double>> seeds;
	protected Graph<NodeType,Double> graph;
	protected ScoreModel<NodeType,Double> scoreObj;

	public CompatComplexSearch(int seedSize, int maxComplexSize) {
		this(seedSize,maxComplexSize,true,new ArrayList<Graph<NodeType,Double>>());
	}

	public CompatComplexSearch(int seedSize, 
	                     int maxComplexSize, 
			     boolean createSeeds, 
			     List<Graph<NodeType,Double>> seeds) {
		this.seedSize = seedSize;
		this.maxComplexSize = maxComplexSize;
		this.createSeeds = createSeeds;
		this.seeds = seeds;
	}

	public List<Graph<NodeType,Double>> searchGraph(Graph<NodeType,Double> graph, 
	                                                ScoreModel<NodeType,Double> scoreObj) {

		this.graph = graph;
		this.scoreObj = scoreObj;

		List<Graph<NodeType,Double>> results = new ArrayList<Graph<NodeType,Double>>();

		System.out.println("seeds.size " + seeds.size());

		if ( createSeeds )
			generateSeeds(seeds);

		System.out.println("seeds.size after gen " + seeds.size());

		for ( Graph<NodeType,Double> orig : seeds ) {
			Graph<NodeType,Double> gg  = growGraph( orig, maxComplexSize, seedSize );
			if ( gg.numberOfNodes() > seedSize )
				results.add( gg ); 
		}

		return results;
	}

	@SuppressWarnings("unchecked") // for a call to Graph<N,W>.clone() below...
	private Graph<NodeType,Double> growGraph( Graph<NodeType,Double> orig, int maxSize, int minSize ) { 

		Graph<NodeType,Double> complex = (Graph<NodeType,Double>)orig.clone();	
		Set<Integer> checkedSet = new HashSet<Integer>();

		//System.out.println("complex: ");
		//System.out.println(complex.toString());
		Map<NodeType,Graph<NodeType,Double>> scoreMap = new HashMap<NodeType,Graph<NodeType,Double>>();
		while ( true ) { 
			//System.out.println("--------------------------------------------");

			scoreMap.clear();

			// Evaluate each node in the complex.
			for ( NodeType n : complex.getNodes() ) {

				// If the node is not a seed node, see what it buys
				// us to remove that node.
				if ( complex.numberOfNodes() > minSize && !orig.isNode(n) ) {
					Graph<NodeType,Double> tmp = (Graph<NodeType,Double>)complex.clone();	
					tmp.removeNode(n);
					if ( !alreadyChecked( checkedSet, tmp ) ) {
						tmp.setScore( scoreObj.scoreGraph( tmp ) );
						//System.out.println("remove node: " + n);
						//System.out.println(tmp.toString());
						scoreMap.put(n,tmp);
					}
				}

				// If we're below the max complex size, 
				// look at all neighbors of the node and see
				// what adding that neighbor to the complex gets us.
				if ( complex.numberOfNodes() < maxSize ) { 
					for ( NodeType neigh : graph.getNeighbors( n ) ) {
						if ( !scoreMap.containsKey(neigh) && !complex.isNode(neigh)) {
							Graph<NodeType,Double> tmp2 = (Graph<NodeType,Double>)complex.clone();	
							addNodeToComplex( tmp2, graph, neigh );
							if ( !alreadyChecked( checkedSet, tmp2 ) ) {
								//System.out.println("add node: " + neigh);
								if ( tmp2 == null ) {
									//System.out.println("tmp2 is null");
									continue;
								}
									
								tmp2.setScore( scoreObj.scoreGraph( tmp2 ) );
								//System.out.println(tmp2.toString());
								scoreMap.put(neigh,tmp2);	
							}
						}
					}
				}
			}

			// Once we've checked all removable nodes and all neighors,
			// find the best option.
			//System.out.println("iterating over scores");
			//System.out.println("Current best complex:");
			//System.out.println(complex.toString());
			boolean foundBetter = false;
			for ( NodeType n : scoreMap.keySet() ) {
				Graph<NodeType,Double> possible = scoreMap.get(n); 
				//System.out.println(possible.toString());
				if ( complex.getScore() < possible.getScore() ) {
					//System.out.println("changing for node " + n);
					complex = possible;
					//System.out.println("best: ");
					//System.out.println(complex.toString());
					foundBetter = true;
				}
			}

			// This means we haven't found anything better than the current
			// complex, so break.
			if ( !foundBetter )
				break;
		}

		//System.out.println("final complex:");
		//System.out.println(complex.toString());
		return complex;
	}


	private void addNodeToComplex(Graph<NodeType,Double> g1, Graph<NodeType,Double> g2, NodeType n) {
		//System.out.println("  ~~ add node: " + n.toString());
		g1.addNode(n);
		for ( NodeType neighbor : g2.getNeighbors(n) ) {
			//System.out.println("  ~~ neighbor node: " + neighbor.toString());
			if ( g1.isNode(neighbor) ) {
				g1.addEdge(n,neighbor,g2.getEdgeWeight(n,neighbor));
				//System.out.println("  ~~ adding edge: " + n.toString() + " - " + neighbor.toString());
			}
		}
	}

	private boolean alreadyChecked(Set<Integer> checkedSet, Graph<NodeType,Double> g) {
		Integer code = new Integer( g.hashCode() );
		if ( checkedSet.contains( code ) ) {
			return true;
		} else {
			checkedSet.add( code );
			return false;
		}
	}

	@SuppressWarnings("unchecked") // for a call to Graph<N,W>.clone() below...
	public void setSeeds(List<Graph<NodeType,Double>> newSeeds) {
		//seeds = newSeeds;
		seeds.clear();
		for ( Graph<NodeType,Double> asdf : newSeeds )
			seeds.add( (Graph<NodeType,Double>)asdf.clone() );
	}

	private void generateSeeds(List<Graph<NodeType,Double>> seedList) {
		for (NodeType seedNode : graph.getNodes()) {
			Graph<NodeType,Double> g = new BasicGraph<NodeType,Double>();
			g.addNode( seedNode );
			g.setScore(0.0);
			Graph<NodeType,Double> gg = growGraph(g,seedSize,seedSize); 
			if ( gg.numberOfNodes() >= seedSize )
				seedList.add( gg ); 
		}
	}
}

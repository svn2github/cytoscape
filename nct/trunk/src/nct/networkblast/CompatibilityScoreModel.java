
//============================================================================
// 
//  file: CompatibilityScoreModel.java
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

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import nct.graph.*;
import nct.score.*;

/**
 * This interface will provide the scoring interface for all scoring 
 * algorithms to be used on a Graph object.  Consequently, all scoring 
 * algorithms should implement this interface.
 */
public class CompatibilityScoreModel
			    implements ScoreModel<CompatibilityNode<String,Double>,Double> {


	private List<? extends Graph<String,Double>> compatConstituentGraphs;
	private ScoreModel<String,Double> edgeScore;

	public CompatibilityScoreModel(List<? extends Graph<String,Double>> cg, ScoreModel<String,Double> logScore ) {
		compatConstituentGraphs = cg;	
		edgeScore = logScore;
		simpleEdgeMap = new HashMap<String,Map<String,SimpleEdge>>();
	}
	
	/**
	 * Given a sourceNode and destNode from a graph, this method will return 
	 * the score of the edge.  
	 * @param srcNode Source node of edge to score. 
	 * @param destNode Dest node of edge to score. 
	 * @param g The graph that contains the edge to score. 
	 * @return The score for the given pair of nodes in the given graph.
	 */
	public double scoreEdge(CompatibilityNode<String,Double> srcNode, CompatibilityNode<String,Double> destNode, Graph<CompatibilityNode<String,Double>,Double> g) {
		return 0;
	}

	/**
	 * Given a node from graph, this function will return 
	 * the score of the node. 
	 * @param node Node to score.
	 * @param g The graph that contains the node to score. 
	 * @return The score for the node in the given graph.
	 */
	public double scoreNode(CompatibilityNode<String,Double> node, Graph<CompatibilityNode<String,Double>,Double> g) {
		return 0;
	}

	public double scoreGraph(Graph<CompatibilityNode<String,Double>,Double> g) {
	
		List<Set<SimpleEdge>> glist = new ArrayList<Set<SimpleEdge>>(compatConstituentGraphs.size());
		for (int i = 0; i < compatConstituentGraphs.size(); i++ ) 
			glist.add( new HashSet<SimpleEdge>());

		for (Edge<CompatibilityNode<String,Double>,Double> e : g.getEdgeList()) {

			CompatibilityNode<String,Double> source = e.getSourceNode();
			CompatibilityNode<String,Double> target = e.getTargetNode();
	
			// split the compat nodes into their constituent parts
			for (int i = 0; i < compatConstituentGraphs.size(); i++ ) {
				Graph<String,Double> part = compatConstituentGraphs.get(i);

				String src = source.getNode(part);
				String tar = target.getNode(part);
				double escore = edgeScore.scoreEdge(src,tar,part);

				// order nodes so that we don't get dupes, e.g. A-B, B-A
				if ( src.compareTo(tar) > 0 ) {
					String tmp = src;
					src = tar;
					tar = tmp;
				}
				
				SimpleEdge partEdge = getSimpleEdge(src,tar,escore); 

				glist.get(i).add( partEdge );
			}
		}

		double total = 0;

		//System.out.println(" ");
		for (int i = 0; i < compatConstituentGraphs.size(); i++ )
			for ( SimpleEdge edge : glist.get(i)  ) {
				//System.out.println("edge: " + edge.toString());
				total += edge.score;
			}

		//System.out.println("total score: " + total);
		//System.out.println("numCreated " + numCreated);

		return total;
	}

	private Map<String,Map<String,SimpleEdge>> simpleEdgeMap;

	//private static int numCreated = 0;

	private SimpleEdge getSimpleEdge( String s, String t, double sc) {
		Map<String,SimpleEdge> sm = simpleEdgeMap.get(s);

		if ( sm == null ) {
			sm = new HashMap<String,SimpleEdge>();
			simpleEdgeMap.put(s,sm);
		}

		SimpleEdge se = sm.get(t);
		if ( se == null ) {
			se = new SimpleEdge(s,t,sc);	
	//		numCreated++;
			sm.put(t, se); 
		}

		return se;
	}
			

	private class SimpleEdge {
		String src;
		String tar;
		double score;
		int hashcode;
		SimpleEdge(String s, String t, double sc) {
			src = s;
			tar = t;
			score = sc;
			String tmp = s + "--" + t;
			hashcode = tmp.hashCode();
		}

		public int hashCode() {
			return hashcode;
		}

		public boolean equals(Object o) {
			return ((SimpleEdge)o).hashCode() == hashcode;
		}
			
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("s: " );
			sb.append(src);
			sb.append(" t: " );
			sb.append(tar);
			sb.append(" sc: " );
			sb.append(Double.toString(score));
			sb.append(" hc: " );
			sb.append(Integer.toString(hashcode));
			return sb.toString();
		}
	}
}

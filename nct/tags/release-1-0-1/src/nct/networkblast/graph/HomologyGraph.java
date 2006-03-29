
//============================================================================
// 
//  file: HomologyGraph.java
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

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import nct.graph.Graph;
import nct.graph.basic.BasicKPartiteGraph;
import nct.graph.SequenceGraph;
import nct.service.homology.HomologyModel;

import org.biojava.bio.*;
import org.biojava.bio.seq.db.*;
import org.biojava.bio.seq.io.*;
import org.biojava.bio.symbol.*;


/**
 * This class creates a K-partite graph based on the homology
 * of proteins between two or more species. 
 */
public class HomologyGraph 
	extends BasicKPartiteGraph<String,Double,SequenceGraph<String,Double>> {

	private static Logger log = Logger.getLogger("networkblast");

	protected HomologyModel homModel;
	protected double exThresh;
	

	/**
	 * @param homModel The homology model used to generate the edges in the graph.
	 * @param exThresh The expectation threshold.  Expectation values above this
	 * threshold will not be added to the graph.
	 */
	public HomologyGraph(HomologyModel homModel, double exThresh) {
		super();
		this.homModel = homModel;
		this.exThresh = exThresh;
	}

	/**
	 * @param homModel The homology model used to generate the edges in the graph.
	 * @param exThresh The expectation threshold.  Expectation values above this
	 * threshold will not be added to the graph.
	 * @param graphs A collection of graphs to be added to this graph.
	 */
	public HomologyGraph(HomologyModel homModel, double exThresh, Collection<SequenceGraph<String,Double>> graphs) {
		super();
		this.homModel = homModel;
		this.exThresh = exThresh;
		for (SequenceGraph<String,Double> sg : graphs)
			addGraph(sg);
	}

	/**
	 * Adds the nodes of the specified graph to this graph and adds the graph as a
	 * partition.
	 * @param sg The SequenceGraph to be added to this graph.
	 * @return Returns true if we're able to add the graph as a partition and we successfully
	 * add at least one node to the graph.
	 */
	public boolean addGraph(SequenceGraph<String,Double> sg) {

		List<SequenceGraph<String,Double>> partitions = getPartitions();
		int numAdded = 0;
		if ( partitions == null || !partitions.contains(sg) ) {
			//System.out.println("adding graph " + sg.toString());
			// add the nodes for the new graph
			for ( String node: sg.getNodes()) {
				//System.out.println("adding node: '" + node + "'");
				if ( addNode(node,sg) )
					numAdded++;
				else
					System.out.println("didn't add node: " + node);
			}
			
			// add all homology edges between each existing graph/partition
			List<SequenceGraph<String,Double>> updatedParts = getPartitions();
			if ( updatedParts.size() > 1 )
				for ( int i = 0; i < updatedParts.size(); i++ )
					for ( int j = i+1; j < updatedParts.size(); j++ )
						createHomologyEdges(updatedParts.get(i),updatedParts.get(j));
			if ( numAdded > 0 )
				return true;
			else
				return false;
		} else
			return false; // graph has already been added
	}

	private void createHomologyEdges(SequenceGraph<String,Double> sg1, SequenceGraph<String,Double> sg2) {
		Map<String,Map<String,Double>> homologyMap = homModel.expectationValues( sg1, sg2 );
		//System.out.println("creating homology edges for:");
		//System.out.println("sg1 " + sg1.toString());
		//System.out.println("sg2 " + sg2.toString());
		//System.out.println("homologyMap size " + homologyMap.size());

		for ( String nodeA: homologyMap.keySet() ) {
			//System.err.println ("A node: " + nodeA );
			for ( String nodeB: homologyMap.get(nodeA).keySet() ) {
				//System.err.print ("B node: " + nodeB );
				Double score = homologyMap.get(nodeA).get(nodeB);
				//System.err.println ("  value: " + score);
				if ( score != null && score.doubleValue() <= exThresh )
					if ( ! addEdge(nodeA,nodeB,score) )
						System.out.println("didn't add edge: " + nodeA + " " + nodeB);
			}
		}
		//System.out.println("number of homology edges: " + numberOfEdges());
		
	}
}

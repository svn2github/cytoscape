
//============================================================================
// 
//  file: UniqueCompatNodeFilter.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.networkblast.filter;

import java.util.*;

import nct.graph.Graph;
import nct.filter.Filter;

/**
 * This Filter filters graphs based on whether the subnodes that form
 * the compatibility nodes that represent nodes in this graph are all
 * unique.  The goal is to prevent any distance 0 nodes.
 */
public class UniqueCompatNodeFilter implements Filter<String,Double> {
    
    /**
     * @param solutions The List of solutions to process.
     * @return Either a filtered solution set or null if solutions is null.
     */
    public List<Graph<String,Double>> filter(List<Graph<String,Double>> solutions) {

	if (solutions == null) 
	    return null;

	//System.out.println("unfiltered set size: " + solutions.size());

	List<Graph<String,Double>> newSolns = new Vector<Graph<String,Double>>();

	HashSet<String> nodeSet = new HashSet<String>();
	for ( Graph<String,Double> graph: solutions ) {
		nodeSet.clear();
		for ( String compatNode : graph.getNodes() ) {
			String[] subNodes = compatNode.split("\\|");
			for ( int i = 0; i < subNodes.length; i++ ) {
				//System.out.println("subnode '" + subNodes[i] + "'");
				nodeSet.add(subNodes[i]);
			}
		}
		//System.out.println("graph num " + graph.numberOfNodes());
		//System.out.println("set num " + nodeSet.size()); 
		if ( (graph.numberOfNodes() * 2) == nodeSet.size() )
			newSolns.add( graph );
	}

	//System.out.println("filtered set size: " + newSolns.size());

	return newSolns;
    }
}
		 
		

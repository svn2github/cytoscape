
//============================================================================
// 
//  file: UniqueCompatNodeFilter.java
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
		 
		

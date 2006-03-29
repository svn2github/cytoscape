
//============================================================================
// 
//  file: SortFilter.java
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



package nct.filter;

import java.util.*;
import nct.graph.Graph;

/**
 * This filter sorts the given solutions from best score (most positive) to 
 * worst.
 */

public class SortFilter<NodeType extends Comparable<? super NodeType>, WeightType extends Comparable<? super WeightType>> implements Filter<NodeType,WeightType> {
    /**
     * Given a List of Graphs, it sorts the graphs based on the comparable implementation 
     * of the graph. 
     * @param solutions the List of Graphs to sort
     */
    public List<Graph<NodeType,WeightType>> filter(List<Graph<NodeType,WeightType>> solutions) {
	if (solutions == null) {
	    return null;
	} else if (solutions.size() == 0) {
	    return new ArrayList<Graph<NodeType,WeightType>>();
	}
	Collections.sort(solutions);
	//System.out.println("unfiltered set size: " + solutions.size());
	//System.out.println("filtered set size: " + solutions.size());
	return solutions;
    }
}

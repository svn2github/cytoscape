
//============================================================================
// 
//  file: Filter.java
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
 * This provides the interface for a Filter type.  The filters should simply 
 * take in a Graph object, a List of SubGraphs, and return another List of 
 * SubGraphs.  These are meant to be post-processing steps.
 */
public interface Filter<NodeType extends Comparable<? super NodeType>, WeightType extends Comparable<? super WeightType>> {
    /**
     * This provides the basic foundation for filtering.  Basic filter to be 
     * written should include removing duplicate solutions, merging solutions, 
     * and finding significant complexes.  This function is defined NOT to 
     * modify the solutions List.
     * @param solutions the List of SubGraphs (solutions)
     * @return a new List containing the solutions passing the filter
     */
    public List<Graph<NodeType,WeightType>> filter(List<Graph<NodeType,WeightType>> solutions);
}

package nct.filter;

import java.util.*;
import nct.graph.Graph;

/**
 * This provides the interface for a Filter type.  The filters should simply 
 * take in a Graph object, a List of SubGraphs, and return another List of 
 * SubGraphs.  These are meant to be post-processing steps.
 */
public interface Filter<NodeType extends Comparable<NodeType>, WeightType extends Comparable<WeightType>> {
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

package org.cytoscape.filter;

/**
 * Different methods for combining two <code>CyFilterResult</code>s together.
 */
public enum MergeType {
	/**
	 * Computes the set-union of the nodes and edges in both
	 * <code>CyFilterResult</code>s.  In other words, the resulting
	 * <code>CyFilterResult</code> will contain nodes and edges that
	 * occur in either of the input <code>CyFilterResult</code>s.
	 */
	UNION,
	
	/**
	 * Computes the set-intersection of the nodes and edges in both
	 * <code>CyFilterResult</code>s.  In other words, the resulting
	 * <code>CyFilterResult</code> will only contain the nodes and
	 * edges that occur in both input <code>CyFilterResult</code>s.
	 */
	INTERSECTION,
}

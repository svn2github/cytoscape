package org.cytoscape.filter;

import org.cytoscape.model.CyNetwork;

/**
 * Tracks the nodes and edges that have been filtered from a
 * <code>CyNetwork</code>.
 */
public interface CyFilterResult {
	/**
	 * Returns the <code>CyNetwork</code> this result set applies to.
	 * @return
	 */
    CyNetwork getNetwork();
    
	/**
	 * Adds the edge with the given index to this result set.  If the edge
	 * was previously added, this method does nothing.
	 * @param index
	 */
    void addEdge(int index);
    
    /**
     * Removes the edge with the given index from this result set.  If the
     * edge was not previously added, this method does nothing.
     * @param index
     */
    void removeEdge(int index);
    
    /**
     * Returns <code>true</code> if the edge with the given index is part of
     * this result set, and <code>false</code> otherwise.
     * @param index
     * @return
     */
    boolean hasEdge(int index);
    
    /**
     * Returns an <code>Iterable</code> over all the edges in this result set.
     * @return
     */
    Iterable<Integer> edges();
    
    /**
     * Adds the node with the given index to this result set.  If the node
     * was previously added, this method does nothing. 
     * @param index
     */
    void addNode(int index);
    
    /**
     * Removes the node with the given index from this result set.  If the
     * node was not previously added, this method does nothing. 
     * @param index
     */
    void removeNode(int index);
    
    /**
     * Returns <code>true</code> if the node with the given index is part of
     * this result set, and <code>false</code> otherwise. 
     * @param index
     * @return
     */
    boolean hasNode(int index);
    
    /**
     * Returns an <code>Iterable</code> over all the nodes in this result set.
     * @return
     */
    Iterable<Integer> nodes();
    
    /**
     * Merges this object with <code>result</code> using the combination
     * method specified by <code>type</code>.
     * @param result
     * @param type
     */
    void merge(CyFilterResult result, MergeType type);
    
    /**
     * Updates this object so that it contains the set complement of its
     * edges and its nodes, with respect to the network it belongs to.
     */
    void invert();
    
    void clear();
}

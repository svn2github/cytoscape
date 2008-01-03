package org.cytoscape.model.graph;

/**
 * A GraphChangeListener is called whenever nodes or edges
 * are added or removed from the graph.
 */
public interface GraphChangeListener {
	public enum GraphChangeType { NODEADDED, NODEREMOVE, EDGEADDED, EDGEREMOVE; }

	/**
	 * Handle a change event.  Note that events might be "batched" together.
	 */
	public void graphChanged(GraphChangeType type, int[] identifiers);
}


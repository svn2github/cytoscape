package org.cytoscape.view.model;

public interface ContinuousRange<T> extends Range<T> {
	
	T getMin();
	T getMax();

	boolean includeMin();
	boolean includeMax();
}

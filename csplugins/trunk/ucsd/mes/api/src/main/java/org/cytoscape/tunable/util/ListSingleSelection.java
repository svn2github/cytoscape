

package org.cytoscape.tunable.util;

import java.util.List;

/**
 * Allows a single value from a list to be selected. 
 */
public class ListSingleSelection<T> extends ListSelection<T> {

	private T selected;

	public ListSingleSelection(final List<T> values) {
		super(values);
	}

	public T getSelectedValue() {	
		return selected;
	}

	public void setSelectedValue(T val) {
		if ( !values.contains(val) )
			throw new IllegalArgumentException("value not contained is list of possible values");

		selected = val;
	}
}

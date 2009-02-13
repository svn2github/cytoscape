package org.cytoscape.work.util;

import java.util.ArrayList;
import java.util.List;


public class ListSelection<T> {

	protected final List<T> values;

	
	public ListSelection(final List<T> values) {
		if (values == null)
			throw new NullPointerException("values is null!");
		if (values.size() == 0)
			throw new IllegalArgumentException("list has size 0");
		this.values = values;
	}


	public List<T> getPossibleValues() {
		return new ArrayList<T>(values);
	}
}


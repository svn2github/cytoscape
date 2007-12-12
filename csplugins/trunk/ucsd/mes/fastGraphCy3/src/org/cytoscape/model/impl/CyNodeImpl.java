package org.cytoscape.model.impl;

import org.cytoscape.model.CyNode;

class CyNodeImpl implements CyNode {

	private int index;

	CyNodeImpl(int ind) {
		index = ind;
	}

	public int getIndex() {
		return index;
	}

	public String toString() {
		return Integer.toString(index);
	}
}

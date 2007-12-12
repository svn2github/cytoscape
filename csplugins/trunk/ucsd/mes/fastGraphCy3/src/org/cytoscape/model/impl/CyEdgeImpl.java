
package org.cytoscape.model.impl;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;

class CyEdgeImpl implements CyEdge {

	private CyNode source;
	private CyNode target;
	private int index;
	private boolean directed;

	CyEdgeImpl(CyNode src, CyNode tgt, boolean dir, int ind) {
		source = src;
		target = tgt;
		directed = dir;
		index = ind;
	}

	public int getIndex() {
		return index;
	}
	public CyNode getSource() {
		return source;
	}

	public CyNode getTarget() {
		return target;
	}

	public boolean isDirected() {
		return directed;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("source: ");
		sb.append(source.toString());
		sb.append("  target: ");
		sb.append(target.toString());
		sb.append("  directed: ");
		sb.append(Boolean.toString(directed));
		sb.append("  index: ");
		sb.append(Integer.toString(index));

		return sb.toString();
	}
}

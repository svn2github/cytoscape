
package org.cytoscape.model;

public interface CyEdge extends GraphObject {

	public enum Type {
		UNDIRECTED,
		INCOMING,
		OUTGOING,
		DIRECTED,
		ANY,
	}

	public int getIndex();
	public CyNode getSource();
	public CyNode getTarget();
	public boolean isDirected();
}

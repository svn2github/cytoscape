
package org.cytoscape.network;

public interface CyEdge extends GraphObject {
	public int getIndex();
	public CyNode getSource();
	public CyNode getTarget();
	public boolean isDirected();
}

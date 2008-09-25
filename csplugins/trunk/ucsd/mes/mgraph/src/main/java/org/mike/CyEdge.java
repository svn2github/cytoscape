
package org.mike;

public interface CyEdge {
	public int getIndex();
	public CyNode getSource();
	public CyNode getTarget();
	public boolean isDirected();
}

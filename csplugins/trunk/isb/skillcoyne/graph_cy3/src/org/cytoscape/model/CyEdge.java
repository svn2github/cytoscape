/**
 * 
 */
package org.cytoscape.model;

/**
 * @author skillcoy
 *
 */
public interface CyEdge {
	
	public void setIndex(int index);
	public int getIndex();

	public CyNode getSource();
	public CyNode getTarget();
	
	public Object getEdgeType(); //???
	
	public boolean isDirected();
	
	public void setAttributes(Attribute edgeAtt);
	public Attribute getAttributes();
}

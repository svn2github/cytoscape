/**
 * 
 */
package org.cytoscape.model;

/**
 *
 */
public interface NodeGroup {

	public void addNode(Node node);
	public void removeNode(Node node);

	// attributes
	public AttributeHolder getAttributeHolder();
}

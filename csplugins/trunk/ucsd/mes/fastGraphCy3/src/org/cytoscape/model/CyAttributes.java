package org.cytoscape.model;

public interface CyAttributes {
	public void setAttribute(int id, String attributeName, AttributeType type, Object value);
	public Object getAttribute(int id, String attributeName, AttributeType type);
	public boolean hasAttribute(int id, String attributeName);
	public AttributeType getType(String attributeName);
	public void deleteAttribute(int id, String attributeName);
}

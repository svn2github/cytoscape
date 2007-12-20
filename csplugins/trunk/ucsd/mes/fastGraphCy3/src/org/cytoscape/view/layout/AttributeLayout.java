package org.cytoscape.view.layout;

import java.util.List;

public interface AttributeLayout {

	// This list should be of the type of however we end up identifying
	// CyAttribute types
	public List supportsNodeAttributes();

	// This list should be of the type of however we end up identifying
	// CyAttribute types
	public List supportsEdgeAttributes();

	public void setLayoutAttribute(String attributeName);
}


/**
 * 
 */
package org.cytoscape.model;

import java.util.*;

public interface Edge {

	public Node getSource();
	public Node getTarget();
	
	// this could very easily be an attribute 
	public boolean isDirected();
	
	// attributes
	public AttributeHolder getAttributeHolder();
}

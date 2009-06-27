package org.cytoscape.view.presentation.processing;

import java.util.Set;

public interface ShapeManager {
	
	public Set<ObjectShape> getAllShapes();
	
	public Set<ObjectShape> getNodeShapes();
	
	public Set<ObjectShape> getEdgeShapes();

}

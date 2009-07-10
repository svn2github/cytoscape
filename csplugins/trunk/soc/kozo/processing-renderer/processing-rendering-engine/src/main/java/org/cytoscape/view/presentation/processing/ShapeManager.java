package org.cytoscape.view.presentation.processing;

import java.util.Set;

public interface ShapeManager {
	
	public Set<CyDrawable> getAllShapes();
	
	public Set<CyDrawable> getNodeShapes();
	
	public Set<CyDrawable> getEdgeShapes();

}

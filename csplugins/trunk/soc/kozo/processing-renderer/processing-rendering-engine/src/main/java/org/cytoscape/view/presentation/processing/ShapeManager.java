package org.cytoscape.view.presentation.processing;

import java.util.Set;

public interface ShapeManager {
	
	public Set<P5Shape> getAllShapes();
	
	public Set<P5Shape> getNodeShapes();
	
	public Set<P5Shape> getEdgeShapes();

}

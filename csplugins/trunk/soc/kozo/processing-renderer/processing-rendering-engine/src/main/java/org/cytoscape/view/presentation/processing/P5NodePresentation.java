package org.cytoscape.view.presentation.processing;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

public interface P5NodePresentation extends P5Presentation<CyNode> {
	
	
	/**
	 * return the backend view model for this node presentation.
	 * This is immutable.
	 * 
	 * @return Node View Model.
	 */
	public View<CyNode> getViewModel();
	
}

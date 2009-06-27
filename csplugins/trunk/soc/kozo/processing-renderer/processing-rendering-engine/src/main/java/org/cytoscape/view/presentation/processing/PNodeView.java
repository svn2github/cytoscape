package org.cytoscape.view.presentation.processing;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;

public interface PNodeView extends PViewObject {
	
	
	/**
	 * return the backend view model for this node presentation.
	 * This is immutable.
	 * 
	 * @return Node View Model.
	 */
	public View<CyNode> getViewModel();
	
}

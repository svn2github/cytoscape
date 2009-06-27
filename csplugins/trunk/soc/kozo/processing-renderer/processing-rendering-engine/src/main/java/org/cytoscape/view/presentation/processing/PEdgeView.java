package org.cytoscape.view.presentation.processing;

import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;


/**
 * Node View rendered by Processing engine
 * 
 * @author kono
 *
 */
public interface PEdgeView extends PViewObject {
	
	public View<CyEdge> getViewModel();

}

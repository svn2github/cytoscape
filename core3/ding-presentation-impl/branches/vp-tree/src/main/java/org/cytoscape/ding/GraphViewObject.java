package org.cytoscape.ding;

import org.cytoscape.view.model.VisualProperty;

public interface GraphViewObject {

	GraphView getGraphView();
	
	void setVisualPropertyValue(final VisualProperty<?> vp, final Object value);

}

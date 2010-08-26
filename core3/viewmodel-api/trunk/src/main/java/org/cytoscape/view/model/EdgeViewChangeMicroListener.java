package org.cytoscape.view.model;

import org.cytoscape.event.CyMicroListener;
import org.cytoscape.model.CyEdge;

public interface EdgeViewChangeMicroListener extends CyMicroListener {

	/**
	 * The method called when the specified VisualProperty is set to the
	 * specified value in the View being listened to. 
	 */
	void  edgeVisualPropertySet(final View<CyEdge> target, final VisualProperty<?> vp, final Object value);
}

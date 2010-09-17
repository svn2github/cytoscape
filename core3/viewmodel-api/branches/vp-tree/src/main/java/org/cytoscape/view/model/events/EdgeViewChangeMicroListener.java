package org.cytoscape.view.model.events;

import org.cytoscape.event.CyMicroListener;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

public interface EdgeViewChangeMicroListener extends CyMicroListener {

	/**
	 * The method called when the specified VisualProperty is set to the
	 * specified value in the View being listened to. 
	 */
	void  edgeVisualPropertySet(final View<CyEdge> target, final VisualProperty<?> vp, final Object value);
}

package org.cytoscape.view.model.events;

import org.cytoscape.event.CyMicroListener;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;

/**
 * Micro Listener for edge views.  Every time Visual Property is set to an edge view, this listener catches the micro event.
 * 
 * @author kono
 *
 */
public interface EdgeViewChangeMicroListener extends CyMicroListener {

	/**
	 * This method will be called when the specified Visual Property value is set to the
	 * specified view. 
	 */
	void  edgeVisualPropertySet(final View<CyEdge> target, final VisualProperty<?> vp, final Object value);
}

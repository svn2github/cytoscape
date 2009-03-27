package org.cytoscape.view.presentation;

import org.cytoscape.view.model.CyNetworkView;

public interface PresentationFactory {
    /**
     * This method should add whatever content it likes to the JInternalFrame for
     * display.  It should also be sure to register said content as a transfer
     * (Drag 'n Drop) listener, if so desired.
     */
    void addPresentation(Object frame, CyNetworkView view);

	/**
 	 * 
	 */
    NavigationPresentation addNavigationPresentation(Object targetComponent, Object navBounds);
}

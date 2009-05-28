package org.cytoscape.view.presentation;

import org.cytoscape.view.model.CyNetworkView;

public interface PresentationFactory {
    /**
     * This method should add whatever content it likes to the JInternalFrame for
     * display.  It should also be sure to register said content as a transfer
     * (Drag 'n Drop) listener, if so desired.
     */
    public void addPresentation(Object frame, CyNetworkView view);

	/**
 	 * 
	 */
    public NavigationPresentation addNavigationPresentation(Object targetComponent, Object navBounds);
    
    public NetworkRenderer getPresentation(CyNetworkView view);
}

package org.cytoscape.view.presentation;

import java.awt.Container;

import org.cytoscape.view.model.View;

public interface PresentationFactory {
    /**
     * This method should add whatever content it likes to the JInternalFrame for
     * display.  It should also be sure to register said content as a transfer
     * (Drag 'n Drop) listener, if so desired.
     */
    public <T extends View<?>> Renderer<T> addPresentation(Container presentationContainer, T view);

	/**
 	 * 
	 */
    public NavigationPresentation addNavigationPresentation(Object targetComponent, Object navBounds);
    
    public <T extends View<?>> Renderer<T> createPresentation(T view);
}

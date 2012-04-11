package org.cytoscape.app.internal.event;

import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.event.AbstractCyEvent;

/**
 * An event used to notify AppsChangedListeners that an app has been added or removed, and that 
 * the listeners (such as UI components) should update their data to reflect the change.
 */
public final class AppsChangedEvent {

	private AppManager source;

	public AppsChangedEvent(AppManager source) {
		this.source = source;
	}
	
	public AppManager getSource() {
		return source;
	}
}

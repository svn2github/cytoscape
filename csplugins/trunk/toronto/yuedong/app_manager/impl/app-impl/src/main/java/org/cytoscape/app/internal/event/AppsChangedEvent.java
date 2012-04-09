package org.cytoscape.app.internal.event;

import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.event.AbstractCyEvent;

public final class AppsChangedEvent {

	private AppManager source;

	public AppsChangedEvent(AppManager source) {
		this.source = source;
	}
	
	public AppManager getSource() {
		return source;
	}
}

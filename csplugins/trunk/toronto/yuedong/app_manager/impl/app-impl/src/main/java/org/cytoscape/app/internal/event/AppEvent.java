package org.cytoscape.app.internal.event;

import org.cytoscape.app.internal.manager.App;
import org.cytoscape.app.internal.manager.AppManager;
import org.cytoscape.event.AbstractCyEvent;

public final class AppEvent {

	private AppManager source;
	private App app;

	public AppEvent(AppManager source, App app) {
		this.source = source;
		this.app = app;
	}
	
	public AppManager getSource() {
		return source;
	}
	
	public App getApp() {
		return app;
	}
}

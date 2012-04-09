package org.cytoscape.app.internal.event;

public interface AppListener {
	
	public void appInstalled(AppEvent event);
	
	public void appUninstalled(AppEvent event);
}

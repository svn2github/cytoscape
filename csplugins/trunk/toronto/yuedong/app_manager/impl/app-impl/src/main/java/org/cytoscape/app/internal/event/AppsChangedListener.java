package org.cytoscape.app.internal.event;

/**
 * A listener used to detect when an app is added, removed, or changed. This is useful for UI components
 * to update their data when the list of apps has changed.
 */
public interface AppsChangedListener {
	
	public void appsChanged(AppsChangedEvent event);
}

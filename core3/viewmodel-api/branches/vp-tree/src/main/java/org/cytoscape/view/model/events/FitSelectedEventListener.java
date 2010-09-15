package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

public interface FitSelectedEventListener extends CyListener {
	public void handleEvent(FitSelectedEvent e);
}

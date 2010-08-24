package org.cytoscape.view.model.events;

import org.cytoscape.event.CyListener;

public interface FitContentEventListener extends CyListener {
	public void handleEvent(FitContentEvent e);
}

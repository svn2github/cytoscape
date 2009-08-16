package org.cytoscape.search.ui.event;

import org.cytoscape.session.SessionLoadedEvent;
import org.cytoscape.session.SessionLoadedListener;
import org.cytoscape.session.SessionSavedEvent;
import org.cytoscape.session.SessionSavedListener;

public class HistoryHandler implements SessionLoadedListener,
		SessionSavedListener {

	void init() {
		// register the listeners
		// cytoscape.Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this)
	}

	public void handleEvent(SessionSavedEvent e) {
		// save the search history here
		System.out.println("Session Saved");
	}

	public void handleEvent(SessionLoadedEvent e) {
		// restore session here
		System.out.println("Session Loaded");
	}
	
}

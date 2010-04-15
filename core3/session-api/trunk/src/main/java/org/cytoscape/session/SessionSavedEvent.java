
package org.cytoscape.session;

import org.cytoscape.event.AbstractCyEvent;

/**
 * 
 */
public final class SessionSavedEvent extends AbstractCyEvent<CySessionManager> {
	public SessionSavedEvent(final CySessionManager source) {
		super(source, SessionSavedListener.class);
	}
}

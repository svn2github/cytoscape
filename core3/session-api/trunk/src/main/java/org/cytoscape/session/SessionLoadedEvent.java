
package org.cytoscape.session;

import org.cytoscape.event.AbstractCyEvent;

/**
 * 
 */
public final class SessionLoadedEvent extends AbstractCyEvent<CySessionManager> {
	public SessionLoadedEvent(final CySessionManager source) {
		super(source,SessionLoadedListener.class);
	}
}

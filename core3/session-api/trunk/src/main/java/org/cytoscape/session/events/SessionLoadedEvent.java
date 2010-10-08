
package org.cytoscape.session.events;

import org.cytoscape.session.CySession;
import org.cytoscape.session.CySessionManager;

import org.cytoscape.event.AbstractCyEvent;

/**
 * This event is fired after a new session has been set in the 
 * {@link CySessionManager#setCurrentCySession(session,filename)} 
 * method and is used to notify interested parties in the change 
 * of state. 
 */
public final class SessionLoadedEvent extends AbstractCyEvent<CySessionManager> {

	private final CySession session;
	private final String fileName;

	/**
	 * @param source The CySessionManager that is the source of this event.
	 * @param session The CySession object that was just loaded.
	 */
	public SessionLoadedEvent(final CySessionManager source, CySession session, String fileName) {
		super(source,SessionLoadedListener.class);
		this.session = session;
		this.fileName = fileName;
	}

	/**
	 * @return The session that was just loaded.
	 */
	public CySession getLoadedSession() {
		return session;
	}

	/**
	 * @return The file name of the session just loaded.
	 */
	public String getLoadedFileName() {
		return fileName;
	}
}

package org.cytoscape.session;


/**
 * This class primarily acts as a listener and tracks the state of 
 * the Cytoscape application. This state can be interogated at any
 * time and the result is an immutable CySession object suitable
 * for serialization. Likewise, setting a new session will replace
 * the current session with a new one.
 */
public interface CySessionManager {

	enum State {
		NEW, 
		OPENED,
		CHANGED,
		CLOSED,
	}

    State getCurrentSessionState();
    
    CySession getCurrentSession();
    
    void setCurrentSession(CySession session);
}


package org.cytoscape.session;


/**
 * This interface merely captures what was in Cytoscape.java and serves
 * as a placeholder until we're able to think about sessions properly.
 */
//TODO uhh, implement this
public interface CySessionManager {

	enum State {
		NEW, 
		OPENED,
		CHANGED,
		CLOSED,
	}

    String getCurrentSessionFileName();

    void setCurrentSessionFileName(String newName);

    void setSessionState(int state);

    int getSessionstate();

	void createNewSession();
}


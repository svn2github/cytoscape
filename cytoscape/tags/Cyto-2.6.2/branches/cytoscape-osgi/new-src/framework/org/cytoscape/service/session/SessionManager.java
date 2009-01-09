
package org.cytoscape.service.session;

public interface SessionManager {
	public String getCurrentSessionFileName();
	public void setCurrentSessionFileName(String newName);
	public void setSessionState(int state);
	public int getSessionstate();
	public void createNewSession();
}

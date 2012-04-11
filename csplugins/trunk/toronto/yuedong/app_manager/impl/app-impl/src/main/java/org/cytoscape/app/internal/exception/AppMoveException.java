package org.cytoscape.app.internal.exception;

import org.cytoscape.app.internal.manager.AppManager;

/**
 * An exception thrown by the {@link AppManager} when it encounters errors while attempting to move an app file.
 */
public class AppMoveException extends Exception {

	/** Long serial version identifier required by the Serializable class */
	private static final long serialVersionUID = 9013239765398696280L;
	
	public AppMoveException(String message) {
		super(message);
	}
	
	public AppMoveException(String message, Throwable cause) {
		super(message, cause);
	}
}

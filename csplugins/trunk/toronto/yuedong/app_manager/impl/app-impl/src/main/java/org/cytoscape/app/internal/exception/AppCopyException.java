package org.cytoscape.app.internal.exception;

public class AppCopyException extends Exception {

	/** Long serial version identifier required by the Serializable class */
	private static final long serialVersionUID = 9013239765398696280L;
	
	public AppCopyException(String message) {
		super(message);
	}
	
	public AppCopyException(String message, Throwable cause) {
		super(message, cause);
	}
}

package org.cytoscape.app.internal.exception;

public class AppParsingException extends Exception {

	/** Long serial version identifier required by the Serializable class */
	private static final long serialVersionUID = 7578373418714543699L;
	
	public AppParsingException(String message) {
		super(message);
	}
	
	public AppParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}

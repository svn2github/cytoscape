package org.mskcc.pathway_commons.web_service;

/**
 * Indicates Error Connecting to cPath.
 *
 * @author Ethan Cerami.
 */
public class CPathException extends Exception {
	/**
	 * Constructor.
	 * @param msg Error message.
	 */
	public CPathException(String msg) {
		super(msg);
	}

	/**
	 * Constructor.
	 * @param msg Error message.
	 * @param t Root throwable.
	 */
	public CPathException(String msg, Throwable t) {
		super(msg, t);
	}
}

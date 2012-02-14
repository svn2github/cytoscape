package org.cytoscape.io.read;

import java.io.InputStream;

public interface InputStreamTaskContext {

	/**
	 * Sets the input stream that will be read by the Reader created from
	 * this factory.
	 * @param is The {@link java.io.InputStream} to be read.
	 * @param inputName The name of the input. 
	 */
	void setInputStream(InputStream is, String inputName);

	InputStream getInputStream();

	String getInputName();

}

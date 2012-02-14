package org.cytoscape.io.write;

import java.io.OutputStream;

public interface CyWriterContext {

	OutputStream getOutputStream();

	/**
	 * This method defines where the generated {@link CyWriter} Task should
	 * write its data to. This method is meant to be called prior
	 * to calling getWriter().
	 * @param os The {@link java.io.OutputStream} to be written to.
	 */
	void setOutputStream(OutputStream os);

}

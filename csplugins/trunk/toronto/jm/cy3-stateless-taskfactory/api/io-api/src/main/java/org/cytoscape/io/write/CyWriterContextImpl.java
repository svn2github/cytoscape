package org.cytoscape.io.write;

import java.io.OutputStream;

public class CyWriterContextImpl implements CyWriterContext {

	private OutputStream stream;

	@Override
	public OutputStream getOutputStream() {
		return stream;
	}
	
	@Override
	public void setOutputStream(OutputStream os) {
		stream = os;
	}
}

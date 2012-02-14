package org.cytoscape.io.write;

import java.io.OutputStream;

public class CyPropertyWriterContextImpl implements CyPropertyWriterContext {

	private OutputStream stream;
	private Object property;

	@Override
	public OutputStream getOutputStream() {
		return stream;
	}

	@Override
	public void setOutputStream(OutputStream os) {
		stream = os;
	}

	@Override
	public void setProperty(Object property) {
		this.property = property;
	}

	public Object getProperty() {
		return property;
	}
}

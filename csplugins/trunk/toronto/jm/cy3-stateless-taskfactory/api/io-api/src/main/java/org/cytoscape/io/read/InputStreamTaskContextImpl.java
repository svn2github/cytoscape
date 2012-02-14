package org.cytoscape.io.read;

import java.io.InputStream;

public class InputStreamTaskContextImpl implements InputStreamTaskContext {

	private InputStream stream;
	private String inputName;

	@Override
	public void setInputStream(InputStream is, String inputName) {
		stream = is;
		this.inputName = inputName;
	}

	@Override
	public InputStream getInputStream() {
		return stream;
	}
	
	@Override
	public String getInputName() {
		return inputName;
	}
}

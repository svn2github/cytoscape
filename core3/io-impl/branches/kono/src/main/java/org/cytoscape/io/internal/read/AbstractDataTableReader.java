package org.cytoscape.io.internal.read;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.model.CyNetwork;

public abstract class AbstractDataTableReader implements CyReader {

	protected boolean cancel = false;
	protected InputStream inputStream;
	protected Map<Class<?>, Object> readObjects;
	
	protected CyNetwork network;
	protected String objectType;
	
	public AbstractDataTableReader() {
		this.readObjects = new HashMap<Class<?>, Object>();
	}
	
	public void setTableOwner(CyNetwork network) {
		this.network = network;
	}
	
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public void cancel() {
		if (cancel)
			throw new IllegalStateException("AbstractDataTableReader has already been cancelled");

		cancel = true;
		try {
			inputStream.close();
		} catch (IOException e) {
		}
		inputStream = null;
	}

	public void setInputStream(InputStream is) {
		if (is == null)
			throw new NullPointerException("Input stream is null");
		inputStream = is;
		cancel = false;
	}

}

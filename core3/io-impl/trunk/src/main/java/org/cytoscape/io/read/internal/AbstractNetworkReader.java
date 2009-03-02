package org.cytoscape.io.read.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.model.CyNetworkFactory;

abstract public class AbstractNetworkReader implements CyReader {

	protected static final String NODE_NAME_ATTR_LABEL = "name";
	
	protected InputStream inputStream;
	protected CyNetworkFactory networkFactory;
	protected Map<Class<?>, Object> readObjects;

	public AbstractNetworkReader(CyNetworkFactory factory) {
		this.networkFactory = factory;
		this.readObjects = new HashMap<Class<?>, Object>();
	}

	public <T> T getReadData(Class<T> type) {
		return type.cast(readObjects.get(type));
	}

	public void setInputStream(InputStream is) {
		if (is == null)
			throw new NullPointerException("Input stream is null");
		inputStream = is;
	}

	public Set<Class<?>> getSupportedDataTypes() {
		return readObjects.keySet();
	}

}
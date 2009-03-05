package org.cytoscape.io.read.internal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.GraphViewFactory;

abstract public class AbstractNetworkReader implements CyReader {

	protected static final String NODE_NAME_ATTR_LABEL = "name";
	
	protected InputStream inputStream;
	protected CyNetworkFactory cyNetworkFactory;
	protected GraphViewFactory graphViewFactory;
	
	protected Map<Class<?>, Object> readObjects;

	public AbstractNetworkReader() {
		this.readObjects = new HashMap<Class<?>, Object>();
	}
	
	public void setCyNetworkFactory(CyNetworkFactory cyNetworkFactory) {
		this.cyNetworkFactory = cyNetworkFactory;
	}
	
	public void setGraphViewFactory(GraphViewFactory graphViewFactory) {
		this.graphViewFactory = graphViewFactory;
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
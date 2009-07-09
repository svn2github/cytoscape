package org.cytoscape.io.internal.read;

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;

abstract public class AbstractNetworkReader implements CyReader {

	protected static final String NODE_NAME_ATTR_LABEL = "name";

	protected boolean cancel = false;
	protected InputStream inputStream;
	protected CyNetworkFactory cyNetworkFactory;
	protected CyNetworkViewFactory cyNetworkViewFactory;

	protected CyLayouts layouts;

	protected Map<Class<?>, Object> readObjects;

	public AbstractNetworkReader() {
		this.readObjects = new HashMap<Class<?>, Object>();
	}

	public void setCyNetworkFactory(CyNetworkFactory cyNetworkFactory) {
		this.cyNetworkFactory = cyNetworkFactory;
	}

	public void setCyNetworkViewFactory(CyNetworkViewFactory cyNetworkViewFactory) {
		this.cyNetworkViewFactory = cyNetworkViewFactory;
	}

	public void setLayouts(CyLayouts layouts) {
		this.layouts = layouts;
	}

	public void setInputStream(InputStream is) {
		if (is == null)
			throw new NullPointerException("Input stream is null");
		inputStream = is;
		cancel = false;
	}

	public void cancel()
	{
		cancel = true;
		try
		{
			inputStream.close();
		}
		catch (IOException e) {}
		inputStream = null;
	}
}

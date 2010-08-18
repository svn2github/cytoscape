package org.cytoscape.io.internal.read;

import java.io.InputStream;
import java.io.IOException;

import org.cytoscape.io.read.CyNetworkViewProducer;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;


public abstract class AbstractNetworkViewProducer implements CyNetworkViewProducer{

	protected static final String NODE_NAME_ATTR_LABEL = "name";
	protected CyNetworkView[] cyNetworkViews;
	protected VisualStyle[] visualstyles;
	protected boolean cancel = false;
	protected InputStream inputStream;
	protected CyNetworkFactory cyNetworkFactory;
	protected CyNetworkViewFactory cyNetworkViewFactory;

	protected CyLayouts layouts;

	public CyNetworkView[] getNetworkViews() {
		return cyNetworkViews;
	}

	public VisualStyle[] getVisualStyles() {
		return visualstyles;
	}
	
	public void setInputStream(InputStream is) {
		if (is == null)
			throw new NullPointerException("Input stream is null");
		inputStream = is;
		cancel = false;
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

	public void cancel()
	{
		if (cancel)
			throw new IllegalStateException("AbstractNetworkViewProducer has already been cancelled");

		cancel = true;
		try
		{
			inputStream.close();
		}
		catch (IOException e) {}
		inputStream = null;
	}
	
}

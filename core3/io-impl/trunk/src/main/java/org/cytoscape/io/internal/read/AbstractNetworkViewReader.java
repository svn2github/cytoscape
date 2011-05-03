package org.cytoscape.io.internal.read;

import java.io.InputStream;

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;

public abstract class AbstractNetworkViewReader extends AbstractTask implements CyNetworkViewReader {

    protected CyNetworkView[] cyNetworkViews;

    protected VisualStyle[] visualstyles;
    protected InputStream inputStream;

    protected final CyNetworkViewFactory cyNetworkViewFactory;
    protected final CyNetworkFactory cyNetworkFactory;


    public AbstractNetworkViewReader(InputStream inputStream, final CyNetworkViewFactory cyNetworkViewFactory,
	    final CyNetworkFactory cyNetworkFactory) {
	if (inputStream == null)
	    throw new NullPointerException("Input stream is null");
	if (cyNetworkViewFactory == null)
	    throw new NullPointerException("CyNetworkViewFactory is null");
	if (cyNetworkFactory == null)
	    throw new NullPointerException("CyNetworkFactory is null");
	
	this.inputStream = inputStream;
	this.cyNetworkViewFactory = cyNetworkViewFactory;
	this.cyNetworkFactory = cyNetworkFactory;
    }

    @Override
    public CyNetworkView[] getNetworkViews() {
	return cyNetworkViews;
    }

    @Override
    public VisualStyle[] getVisualStyles() {
	return visualstyles;
    }
}

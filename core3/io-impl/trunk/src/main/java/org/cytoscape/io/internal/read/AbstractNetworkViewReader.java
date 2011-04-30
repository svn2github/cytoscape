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

    // If number of graph objects is above this value, NullCyNetworkView will be
    // created instead of actual view.
    protected final int viewThreshold;

    public AbstractNetworkViewReader(InputStream inputStream, final CyNetworkViewFactory cyNetworkViewFactory,
	    final CyNetworkFactory cyNetworkFactory, final Integer viewThreshold) {
	if (inputStream == null)
	    throw new NullPointerException("Input stream is null");
	if (cyNetworkViewFactory == null)
	    throw new NullPointerException("CyNetworkViewFactory is null");
	if (cyNetworkFactory == null)
	    throw new NullPointerException("CyNetworkFactory is null");
	this.inputStream = inputStream;
	this.cyNetworkViewFactory = cyNetworkViewFactory;
	this.cyNetworkFactory = cyNetworkFactory;
	
	if(viewThreshold != null)
	    this.viewThreshold = viewThreshold;
	else
	    this.viewThreshold = DEF_VIEW_THRESHOLD;
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

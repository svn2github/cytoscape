package org.cytoscape.io.internal.read;

import java.io.InputStream;

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.NullCyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;

public abstract class AbstractNetworkViewReader extends AbstractTask implements CyNetworkViewReader {

    protected CyNetwork[] networks;

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

	if (viewThreshold != null)
	    this.viewThreshold = viewThreshold;
	else
	    this.viewThreshold = DEF_VIEW_THRESHOLD;
    }

    @Override
    public CyNetworkView[] getNetworkViews() {
	if(cyNetworkViews == null)
	    this.createViews();
	
	return cyNetworkViews;
    }

    @Override
    public VisualStyle[] getVisualStyles() {
	return visualstyles;
    }

    protected void createViews() {
	if (networks == null || networks.length == 0)
	    throw new IllegalStateException("No network model is available.");
	this.cyNetworkViews = new CyNetworkView[networks.length];

	for (int i = 0; i < networks.length; i++) {
	    final CyNetwork network = networks[i];
	    final int objectCount = network.getEdgeCount() + network.getNodeCount();
	    if (this.viewThreshold < objectCount)
		cyNetworkViews[i] = new NullCyNetworkView(network);
	    else
		cyNetworkViews[i] = createView(network);
	}
    }

    abstract protected CyNetworkView createView(final CyNetwork network);
}

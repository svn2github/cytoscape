package org.cytoscape.io.internal.read.gml;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.read.AbstractNetworkViewReaderFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.TaskIterator;


public class GMLNetworkViewReaderFactory extends AbstractNetworkViewReaderFactory {
	private final RenderingEngineManager renderingEngineManager;

	public GMLNetworkViewReaderFactory(CyFileFilter filter,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkFactory networkFactory,
			RenderingEngineManager renderingEngineManager) {
		super(filter, networkViewFactory, networkFactory);
		this.renderingEngineManager = renderingEngineManager;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new GMLNetworkViewReader(inputStream, cyNetworkFactory, cyNetworkViewFactory, renderingEngineManager));
	}

}

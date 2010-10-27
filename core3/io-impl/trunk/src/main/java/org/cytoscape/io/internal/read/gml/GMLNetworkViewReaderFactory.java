package org.cytoscape.io.internal.read.gml;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.read.AbstractNetworkViewReaderFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;


public class GMLNetworkViewReaderFactory extends AbstractNetworkViewReaderFactory {
	private final CyApplicationManager applicationManager;

	public GMLNetworkViewReaderFactory(CyFileFilter filter,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkFactory networkFactory,
			CyApplicationManager applicationManager) {
		super(filter, networkViewFactory, networkFactory);
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new GMLNetworkViewReader(inputStream, cyNetworkFactory, cyNetworkViewFactory, applicationManager));
	}

}

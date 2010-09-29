package org.cytoscape.io.internal.read.gml;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.read.AbstractNetworkViewReaderFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class GMLNetworkViewReaderFactory extends AbstractNetworkViewReaderFactory {

	private final CyNetworkManager networkManager;

	public GMLNetworkViewReaderFactory(CyFileFilter filter,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkFactory networkFactory,
			CyNetworkManager networkManager) {
		super(filter, networkViewFactory, networkFactory);
		this.networkManager = networkManager;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new GMLNetworkViewReader(inputStream, cyNetworkFactory, cyNetworkViewFactory, networkManager));
	}

}

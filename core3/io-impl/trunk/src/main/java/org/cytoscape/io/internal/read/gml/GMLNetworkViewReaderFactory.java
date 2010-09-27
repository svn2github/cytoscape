package org.cytoscape.io.internal.read.gml;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.read.AbstractNetworkViewReaderFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class GMLNetworkViewReaderFactory extends AbstractNetworkViewReaderFactory {

	public GMLNetworkViewReaderFactory(CyFileFilter filter,
			CyNetworkViewFactory networkViewFactory,
			CyNetworkFactory networkFactory) {
		super(filter, networkViewFactory, networkFactory);
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new GMLNetworkViewReader(inputStream, cyNetworkFactory, cyNetworkViewFactory));
	}

}

package org.cytoscape.tableimport.internal;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;

// Copy from io-impl
public abstract class AbstractNetworkReaderFactory implements InputStreamTaskFactory<InputStreamTaskContext> {

	private final CyFileFilter filter;

	protected final CyNetworkViewFactory cyNetworkViewFactory;
	protected final CyNetworkFactory cyNetworkFactory;

	public AbstractNetworkReaderFactory(CyFileFilter filter, CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkFactory cyNetworkFactory) {
		this.filter = filter;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkFactory = cyNetworkFactory;
	}

	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}
}

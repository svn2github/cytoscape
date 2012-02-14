package org.cytoscape.tableimport.internal.reader.ontology;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class OBONetworkReaderFactory implements InputStreamTaskFactory<InputStreamTaskContext> {

	private final CyFileFilter filter;

	protected final CyNetworkViewFactory cyNetworkViewFactory;
	protected final CyNetworkFactory cyNetworkFactory;

	private final CyEventHelper eventHelper;

	public OBONetworkReaderFactory(CyFileFilter filter) {
		this.filter = filter;
		this.cyNetworkViewFactory = CytoscapeServices.cyNetworkViewFactory;
		this.cyNetworkFactory = CytoscapeServices.cyNetworkFactory;
		this.eventHelper = CytoscapeServices.cyEventHelper;
	}

	public CyFileFilter getFileFilter() {
		return filter;
	}

	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		return new TaskIterator(new OBOReader(context.getInputName(), context.getInputStream(), cyNetworkViewFactory, cyNetworkFactory, eventHelper));
	}
}

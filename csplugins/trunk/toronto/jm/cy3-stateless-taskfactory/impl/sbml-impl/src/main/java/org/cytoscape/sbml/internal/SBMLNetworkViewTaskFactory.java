package org.cytoscape.sbml.internal;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class SBMLNetworkViewTaskFactory implements InputStreamTaskFactory<InputStreamTaskContext> {

	private final CyFileFilter filter;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory viewFactory;
	
	public SBMLNetworkViewTaskFactory(CyFileFilter filter, CyNetworkFactory networkFactory, CyNetworkViewFactory viewFactory) {
		this.networkFactory = networkFactory;
		this.viewFactory = viewFactory;
		this.filter = filter;
	}

	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}
	
	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		return new TaskIterator(new SBMLNetworkViewReader(context.getInputStream(), networkFactory, viewFactory));
	}

	public CyFileFilter getFileFilter() {
		return filter;
	}
}

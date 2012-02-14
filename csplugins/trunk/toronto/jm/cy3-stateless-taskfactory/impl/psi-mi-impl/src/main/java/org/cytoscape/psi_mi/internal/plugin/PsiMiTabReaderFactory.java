package org.cytoscape.psi_mi.internal.plugin;

import java.util.Properties;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class PsiMiTabReaderFactory implements InputStreamTaskFactory<InputStreamTaskContext> {

	private final CyFileFilter filter;

	private final CyNetworkViewFactory cyNetworkViewFactory;
	private final CyNetworkFactory cyNetworkFactory;
	private final CyLayoutAlgorithmManager layouts;

	private final CyProperty<Properties> prop;
	
	public PsiMiTabReaderFactory(
			CyFileFilter filter,
			CyNetworkViewFactory cyNetworkViewFactory,
			CyNetworkFactory cyNetworkFactory, CyLayoutAlgorithmManager layouts, final CyProperty<Properties> prop) {
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.filter = filter;
		this.layouts = layouts;
		this.prop = prop;
	}

	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		return new TaskIterator(new PsiMiTabReader(context.getInputStream(),
				cyNetworkViewFactory, cyNetworkFactory, layouts, prop));
	}

	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}
}

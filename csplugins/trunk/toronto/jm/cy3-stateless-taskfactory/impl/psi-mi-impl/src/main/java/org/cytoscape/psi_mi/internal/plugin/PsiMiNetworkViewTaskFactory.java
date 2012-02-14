package org.cytoscape.psi_mi.internal.plugin;

import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.io.read.InputStreamTaskContextImpl;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.psi_mi.internal.plugin.PsiMiCyFileFilter.PSIMIVersion;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class PsiMiNetworkViewTaskFactory implements InputStreamTaskFactory<InputStreamTaskContext> {	
	private final CyNetworkViewFactory networkViewFactory;
	private final CyNetworkFactory networkFactory;
	private final CyLayoutAlgorithmManager layouts;
	private final CyFileFilter filter;
	
	private final PSIMIVersion version;

	public PsiMiNetworkViewTaskFactory(final PSIMIVersion version, CyFileFilter filter, CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory, CyLayoutAlgorithmManager layouts) {
		this.filter = filter;
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.layouts = layouts;
		this.version = version;
		
	}

	@Override
	public InputStreamTaskContext createTaskContext() {
		return new InputStreamTaskContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(InputStreamTaskContext context) {
		// Usually 3 tasks: load, visualize, and layout.
		
		InputStream inputStream = context.getInputStream();
		if(version == PSIMIVersion.PSIMI25)
			return new TaskIterator(3, new PSIMI25XMLNetworkViewReader(inputStream, networkFactory, networkViewFactory, layouts));
		else
			return new TaskIterator(3, new PSIMI10XMLNetworkViewReader(inputStream, networkFactory, networkViewFactory, layouts));
	}

	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}
}

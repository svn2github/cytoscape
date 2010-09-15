package org.cytoscape.psi_mi.internal.plugin;

import java.io.InputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class PsiMiNetworkViewTaskFactory implements InputStreamTaskFactory {	
	private final CyFileFilter filter;
	private final CyNetworkViewFactory networkViewFactory;
	private final CyNetworkFactory networkFactory;
	private final CyLayouts layouts;
	
	private InputStream inputStream;

	public PsiMiNetworkViewTaskFactory(CyNetworkFactory networkFactory, CyNetworkViewFactory networkViewFactory, CyLayouts layouts) {
		this.filter = new PsiMiCyFileFilter();
		this.networkFactory = networkFactory;
		this.networkViewFactory = networkViewFactory;
		this.layouts = layouts;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new PsiMiNetworkViewReader(inputStream, networkFactory, networkViewFactory, layouts));
	}

	@Override
	public CyFileFilter getCyFileFilter() {
		return filter;
	}

	@Override
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}

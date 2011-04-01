package org.cytoscape.cpath2.internal.biopax;

import java.io.InputStream;

import org.cytoscape.cpath2.internal.biopax.view.BioPaxContainer;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.TaskIterator;

public class BioPaxNetworkViewTaskFactory implements InputStreamTaskFactory {

	private final CyFileFilter filter;
	private final CyNetworkFactory networkFactory;
	private final CyNetworkViewFactory viewFactory;
	private final CyNetworkNaming naming;
	private final BioPaxContainer bpContainer;

	private InputStream inputStream;
	private String inputName;

	public BioPaxNetworkViewTaskFactory(CyFileFilter filter, CyNetworkFactory networkFactory, CyNetworkViewFactory viewFactory, CyNetworkNaming naming, BioPaxContainer bpContainer) {
		this.filter = filter;
		this.networkFactory = networkFactory;
		this.viewFactory = viewFactory;
		this.naming = naming;
		this.bpContainer = bpContainer;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		BioPaxNetworkViewReaderTask task = new BioPaxNetworkViewReaderTask(inputStream, inputName, networkFactory, viewFactory, naming, bpContainer);
		return new TaskIterator(task);
	}

	@Override
	public CyFileFilter getCyFileFilter() {
		return filter;
	}

	@Override
	public void setInputStream(InputStream inputStream, String inputName) {
		this.inputStream = inputStream;
		this.inputName = inputName;
	}

}

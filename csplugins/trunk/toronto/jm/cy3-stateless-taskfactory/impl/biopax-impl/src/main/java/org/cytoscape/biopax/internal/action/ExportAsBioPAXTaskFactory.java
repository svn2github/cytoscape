package org.cytoscape.biopax.internal.action;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterContext;
import org.cytoscape.io.write.CyNetworkViewWriterContextImpl;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.work.TaskIterator;

public class ExportAsBioPAXTaskFactory implements CyNetworkViewWriterFactory<CyNetworkViewWriterContext> {

	private final CyFileFilter filter;
	private final String fileName;

	public ExportAsBioPAXTaskFactory(String fileName, CyFileFilter filter) {
		this.filter = filter;
		this.fileName = fileName;
	}

	@Override
	public CyNetworkViewWriterContext createTaskContext() {
		return new CyNetworkViewWriterContextImpl();
	}
	
	@Override
	public CyWriter createWriterTask(CyNetworkViewWriterContext context) {
		return new ExportAsBioPAXTask(fileName, context.getOutputStream(), context.getNetwork());
	}

	@Override
	public TaskIterator createTaskIterator(CyNetworkViewWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return filter;
	}
}

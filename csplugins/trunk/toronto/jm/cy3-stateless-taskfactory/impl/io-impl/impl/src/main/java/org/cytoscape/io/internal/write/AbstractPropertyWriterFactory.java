package org.cytoscape.io.internal.write;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyPropertyWriterFactory;
import org.cytoscape.io.write.CyPropertyWriterContext;
import org.cytoscape.io.write.CyPropertyWriterContextImpl;
import org.cytoscape.work.TaskIterator;

public abstract class AbstractPropertyWriterFactory implements CyPropertyWriterFactory<CyPropertyWriterContext> {
	
	private final CyFileFilter thisFilter;

	public AbstractPropertyWriterFactory(CyFileFilter thisFilter) {
		this.thisFilter = thisFilter;
	}

	@Override
	public CyPropertyWriterContext createTaskContext() {
		return new CyPropertyWriterContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(CyPropertyWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return thisFilter;
	}
}

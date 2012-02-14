package org.cytoscape.io.internal.write.graphics;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.PresentationWriterContext;
import org.cytoscape.io.write.PresentationWriterContextImpl;
import org.cytoscape.io.write.PresentationWriterFactory;
import org.cytoscape.work.TaskIterator;

public abstract class AbstractPresentationWriterFactory implements PresentationWriterFactory<PresentationWriterContext> {
	protected final CyFileFilter fileFilter;

	public AbstractPresentationWriterFactory(final CyFileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	@Override
	public PresentationWriterContext createTaskContext() {
		return new PresentationWriterContextImpl();
	}
	
	@Override
	public TaskIterator createTaskIterator(PresentationWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return fileFilter;
	}
}

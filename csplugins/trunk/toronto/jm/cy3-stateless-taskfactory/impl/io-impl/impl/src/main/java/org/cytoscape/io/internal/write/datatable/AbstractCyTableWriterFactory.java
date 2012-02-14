package org.cytoscape.io.internal.write.datatable;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyTableWriterContext;
import org.cytoscape.io.write.CyTableWriterFactory;
import org.cytoscape.work.TaskIterator;

public abstract class AbstractCyTableWriterFactory implements CyTableWriterFactory<CyTableWriterContext> {

	private CyFileFilter fileFilter;

	protected AbstractCyTableWriterFactory(CyFileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}
	
	@Override
	public CyFileFilter getFileFilter() {
		return fileFilter;
	}
	
	@Override
	public TaskIterator createTaskIterator(CyTableWriterContext context) {
		return new TaskIterator(createWriterTask(context));
	}
}

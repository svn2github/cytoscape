package org.cytoscape.task.internal.exporttable;

import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.task.AbstractDataTableTaskFactory;
import org.cytoscape.task.internal.io.CyTableWriter;
import org.cytoscape.work.TaskIterator;

public class ExportCurrentTableTaskFactory extends AbstractDataTableTaskFactory {

	private final CyTableWriterManager writerManager;

	public ExportCurrentTableTaskFactory(CyTableWriterManager writerManager) {
		this.writerManager = writerManager;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new CyTableWriter(writerManager, table));
	}
}

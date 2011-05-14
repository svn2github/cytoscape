package org.cytoscape.task.internal.exporttable;

import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.internal.io.CyTableWriter;
import org.cytoscape.work.TaskIterator;

public class ExportEdgeTableTaskFactory extends AbstractNetworkViewTaskFactory {

	private final CyTableWriterManager writerManager;

	public ExportEdgeTableTaskFactory(CyTableWriterManager writerManager) {
		this.writerManager = writerManager;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		CyTable table = view.getModel().getDefaultEdgeTable();
		return new TaskIterator(new CyTableWriter(writerManager, table));
	}

}

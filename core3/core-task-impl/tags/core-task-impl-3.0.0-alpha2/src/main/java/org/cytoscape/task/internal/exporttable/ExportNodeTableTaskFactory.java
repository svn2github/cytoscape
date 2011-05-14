package org.cytoscape.task.internal.exporttable;

import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.internal.io.CyTableWriter;
import org.cytoscape.work.TaskIterator;

public class ExportNodeTableTaskFactory extends AbstractNetworkViewTaskFactory {

	private final CyTableWriterManager writerManager;

	public ExportNodeTableTaskFactory(CyTableWriterManager writerManager) {
		this.writerManager = writerManager;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		CyTable table = view.getModel().getDefaultNodeTable();
		return new TaskIterator(new CyTableWriter(writerManager, table));
	}

}

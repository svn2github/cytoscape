package org.cytoscape.task.internal.export.table;

import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskContext;
import org.cytoscape.work.TaskIterator;

public class ExportNodeTableTaskFactory extends AbstractNetworkViewTaskFactory {

	private final CyTableWriterManager writerManager;

	public ExportNodeTableTaskFactory(CyTableWriterManager writerManager) {
		this.writerManager = writerManager;
	}
	
	@Override
	public TaskIterator createTaskIterator(NetworkViewTaskContext context) {
		CyTable table = context.getNetworkView().getModel().getDefaultNodeTable();
		return new TaskIterator(2,new CyTableWriter(writerManager, table));
	}

}

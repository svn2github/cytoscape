package org.cytoscape.task.internal.loaddatatable;

import org.cytoscape.io.read.CyDataTableReaderManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

public class LoadDataTableTaskFactoryImpl implements TaskFactory {

	private CyDataTableReaderManager mgr;
	
	public LoadDataTableTaskFactoryImpl(CyDataTableReaderManager mgr) {
		this.mgr = mgr;
	}

	public Task getTask() {
		return new LoadDataTableTask(mgr);
	}
}

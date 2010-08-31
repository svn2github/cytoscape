package org.cytoscape.task.internal.loaddatatable;


import org.cytoscape.io.read.CyDataTableReaderManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class LoadDataTableTaskFactoryImpl implements TaskFactory {
	private CyDataTableReaderManager mgr;
	
	public LoadDataTableTaskFactoryImpl(CyDataTableReaderManager mgr) {
		this.mgr = mgr;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new LoadDataTableTask(mgr));
	}
}

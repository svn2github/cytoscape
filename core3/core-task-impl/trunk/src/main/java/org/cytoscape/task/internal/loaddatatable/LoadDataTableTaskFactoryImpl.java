package org.cytoscape.task.internal.loaddatatable;


import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class LoadDataTableTaskFactoryImpl implements TaskFactory {
	private CyTableReaderManager mgr;
	
	public LoadDataTableTaskFactoryImpl(CyTableReaderManager mgr) {
		this.mgr = mgr;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new LoadDataTableTask(mgr));
	}
}

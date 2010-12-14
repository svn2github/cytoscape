package org.cytoscape.task.internal.loaddatatable;


import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskFactory;


public class LoadAttributesTaskFactoryImpl implements TaskFactory {
	private CyTableReaderManager mgr;
	private CyTableManager tableMgr;
	public LoadAttributesTaskFactoryImpl(CyTableReaderManager mgr, CyTableManager tableMgr) {
		this.mgr = mgr;
		this.tableMgr = tableMgr;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new LoadAttributesTask(mgr, tableMgr));
	}
}

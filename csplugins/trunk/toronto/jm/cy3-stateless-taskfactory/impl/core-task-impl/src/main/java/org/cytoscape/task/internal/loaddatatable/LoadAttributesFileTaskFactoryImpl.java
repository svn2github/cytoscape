package org.cytoscape.task.internal.loaddatatable;


import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


public class LoadAttributesFileTaskFactoryImpl implements TaskFactory<Object> {
	private CyTableReaderManager mgr;
	public LoadAttributesFileTaskFactoryImpl(CyTableReaderManager mgr) {
		this.mgr = mgr;
	}

	@Override
	public Object createTaskContext() {
		return new Object();
	}
	
	public TaskIterator createTaskIterator(Object context) {
		return new TaskIterator(2, new LoadAttributesFileTask(mgr));
	}
}

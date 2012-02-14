package org.cytoscape.ding.customgraphicsmgr.internal;

import java.io.File;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class RestoreImageTaskFactory implements TaskFactory<Object> {
	
	private final File imageLocation;
	private final CustomGraphicsManagerImpl manager;
	
	RestoreImageTaskFactory(final File imageLocation, final CustomGraphicsManagerImpl manager) {
		this.manager = manager;
		this.imageLocation = imageLocation;
	}

	@Override
	public TaskIterator createTaskIterator(Object context) {
		
		return new TaskIterator(new RestoreImageTask(imageLocation, manager));
	}

	@Override
	public Object createTaskContext() {
		return new Object();
	}
}

package org.cytoscape.ding.customgraphicsmgr.internal;

import java.io.File;

import org.cytoscape.ding.customgraphics.CustomGraphicsManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class PersistImageTaskFactory implements TaskFactory<Object> {

	private final File location;
	private final CustomGraphicsManager manager;

	PersistImageTaskFactory(final File location,
			final CustomGraphicsManager manager) {
		this.manager = manager;
		this.location = location;
	}

	@Override
	public TaskIterator createTaskIterator(Object context) {
		return new TaskIterator(new PersistImageTask(location, manager));
	}

	@Override
	public Object createTaskContext() {
		return new Object();
	}
}

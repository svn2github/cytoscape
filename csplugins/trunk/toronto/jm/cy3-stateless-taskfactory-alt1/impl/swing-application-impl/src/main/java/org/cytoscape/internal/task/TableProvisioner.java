package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class TableProvisioner<T> implements TaskFactory<T> {
	private final TableTaskFactory<T> factory;
	private final CyApplicationManager applicationManager;

	public TableProvisioner(TableTaskFactory<T> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(T tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getCurrentTable());
	}
	
	@Override
	public T createTunableContext() {
		return factory.createTunableContext();
	}
}

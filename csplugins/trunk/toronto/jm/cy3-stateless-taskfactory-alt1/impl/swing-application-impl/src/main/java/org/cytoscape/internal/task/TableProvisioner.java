package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.TableTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class TableProvisioner implements TaskFactory<Object> {
	private final TableTaskFactory<Object> factory;
	private final CyApplicationManager applicationManager;

	public TableProvisioner(TableTaskFactory<Object> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(Object tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getCurrentTable());
	}
	
	@Override
	public Object createTunableContext() {
		return factory.createTunableContext();
	}
}

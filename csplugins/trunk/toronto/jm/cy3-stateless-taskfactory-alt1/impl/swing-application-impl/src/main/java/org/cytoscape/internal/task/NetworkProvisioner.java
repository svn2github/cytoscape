package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkProvisioner implements TaskFactory<Object> {
	private final NetworkTaskFactory<Object> factory;
	private final CyApplicationManager applicationManager;

	public NetworkProvisioner(NetworkTaskFactory<Object> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(Object tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getCurrentNetwork());
	}
	
	@Override
	public Object createTunableContext() {
		return factory.createTunableContext();
	}
}

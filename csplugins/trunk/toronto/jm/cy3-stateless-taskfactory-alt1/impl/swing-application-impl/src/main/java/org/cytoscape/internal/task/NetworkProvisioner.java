package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkProvisioner<T> implements TaskFactory<T> {
	private final NetworkTaskFactory<T> factory;
	private final CyApplicationManager applicationManager;

	public NetworkProvisioner(NetworkTaskFactory<T> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(T tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getCurrentNetwork());
	}
	
	@Override
	public T createTunableContext() {
		return factory.createTunableContext();
	}
}

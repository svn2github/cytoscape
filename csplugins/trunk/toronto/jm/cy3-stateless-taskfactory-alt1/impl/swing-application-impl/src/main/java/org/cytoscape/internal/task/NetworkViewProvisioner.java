package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkViewProvisioner implements TaskFactory<Object> {
	private final NetworkViewTaskFactory<Object> factory;
	private final CyApplicationManager applicationManager;

	public NetworkViewProvisioner(NetworkViewTaskFactory<Object> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(Object tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getCurrentNetworkView());
	}
	
	@Override
	public Object createTunableContext() {
		return factory.createTunableContext();
	}
}


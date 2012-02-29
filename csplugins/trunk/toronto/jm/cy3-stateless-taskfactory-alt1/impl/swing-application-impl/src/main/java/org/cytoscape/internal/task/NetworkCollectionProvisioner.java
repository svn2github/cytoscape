package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkCollectionProvisioner implements TaskFactory<Object> {
	private final NetworkCollectionTaskFactory<Object> factory;
	private final CyApplicationManager applicationManager;

	public NetworkCollectionProvisioner(NetworkCollectionTaskFactory<Object> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(Object tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getSelectedNetworks());
	}
	
	@Override
	public Object createTunableContext() {
		return factory.createTunableContext();
	}
}

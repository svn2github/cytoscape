package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkCollectionProvisioner<T> implements TaskFactory<T> {
	private final NetworkCollectionTaskFactory<T> factory;
	private final CyApplicationManager applicationManager;

	public NetworkCollectionProvisioner(NetworkCollectionTaskFactory<T> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(T tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getSelectedNetworks());
	}
	
	@Override
	public T createTunableContext() {
		return factory.createTunableContext();
	}
}

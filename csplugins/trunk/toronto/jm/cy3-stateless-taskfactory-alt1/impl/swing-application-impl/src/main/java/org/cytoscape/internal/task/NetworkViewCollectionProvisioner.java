package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkViewCollectionProvisioner<T> implements TaskFactory<T> {
	private final NetworkViewCollectionTaskFactory<T> factory;
	private final CyApplicationManager applicationManager;

	public NetworkViewCollectionProvisioner(NetworkViewCollectionTaskFactory<T> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(T tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getSelectedNetworkViews());
	}
	
	@Override
	public T createTunableContext() {
		return createTunableContext();
	}
}

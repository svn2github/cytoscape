package org.cytoscape.internal.task;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.task.NetworkViewCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class NetworkViewCollectionProvisioner implements TaskFactory<Object> {
	private final NetworkViewCollectionTaskFactory<Object> factory;
	private final CyApplicationManager applicationManager;

	public NetworkViewCollectionProvisioner(NetworkViewCollectionTaskFactory<Object> factory, CyApplicationManager applicationManager) {
		this.factory = factory;
		this.applicationManager = applicationManager;
	}

	@Override
	public TaskIterator createTaskIterator(Object tunableContext) {
		return factory.createTaskIterator(tunableContext, applicationManager.getSelectedNetworkViews());
	}
	
	@Override
	public Object createTunableContext() {
		return createTunableContext();
	}
}

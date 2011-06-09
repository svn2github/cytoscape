package org.cytoscape.task.internal.edit;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ConnectSelectedNodesTaskFactory implements TaskFactory {

	private final CyApplicationManager appManager;
//	private final CyNetworkViewManager viewManager;
	private final CyEventHelper eventHelper;

	public ConnectSelectedNodesTaskFactory(final CyApplicationManager appManager, final CyEventHelper eventHelper) {
		this.appManager = appManager;
		this.eventHelper = eventHelper;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ConnectSelectedNodesTask(appManager.getCurrentNetwork(), eventHelper));
	}

}

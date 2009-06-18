package org.cytoscape.view.presentation.processing.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

import cytoscape.CyNetworkManager;

public class CreatePresentationTaskFactory implements TaskFactory {
	
	private CyNetworkManager manager;
	
	public CreatePresentationTaskFactory(CyNetworkManager manager) {
		this.manager = manager;
	}

	public Task getTask() {
		return new CreatePresentationTask(manager);
	}

}

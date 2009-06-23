package org.cytoscape.view.presentation.processing.internal;

import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;

import cytoscape.CyNetworkManager;

public class CreatePresentationTaskFactory implements TaskFactory {
	
	private CyNetworkManager manager;
	private PresentationFactory pFactory;
	
	public CreatePresentationTaskFactory(CyNetworkManager manager, PresentationFactory pFactory) {
		this.manager = manager;
		this.pFactory = pFactory;
	}

	public Task getTask() {
		return new CreatePresentationTask(manager, pFactory);
	}

}

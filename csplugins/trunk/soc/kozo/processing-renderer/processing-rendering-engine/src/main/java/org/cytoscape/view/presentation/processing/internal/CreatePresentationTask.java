package org.cytoscape.view.presentation.processing.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cytoscape.CyNetworkManager;

public class CreatePresentationTask implements Task {

	
	private CyNetworkManager manager;
	
	private TaskMonitor taskMonitor;
	
	public CreatePresentationTask(CyNetworkManager manager) {
		this.manager = manager;
		
	}
	
	public void cancel() {
		// TODO Auto-generated method stub

	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		
		this.taskMonitor = taskMonitor;
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Creating Processing Presentation...");
		
		System.out.println("* Creating Processing presentation for: " + manager.getCurrentNetwork());
		
		taskMonitor.setProgress(1.0);
		

	}

}

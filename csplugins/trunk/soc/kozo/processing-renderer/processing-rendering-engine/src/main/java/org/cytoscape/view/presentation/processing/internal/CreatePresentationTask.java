package org.cytoscape.view.presentation.processing.internal;

import javax.swing.JFrame;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


public class CreatePresentationTask implements Task {

	
	private CyNetworkManager manager;
	
	private TaskMonitor taskMonitor;
	
	private PresentationFactory pFactory;
	
	public CreatePresentationTask(CyNetworkManager manager, PresentationFactory pFactory) {
		this.manager = manager;
		this.pFactory = pFactory;
	}
	
	public void cancel() {
		// TODO Auto-generated method stub

	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		
//		this.taskMonitor = taskMonitor;
//		taskMonitor.setProgress(-1.0);
//		taskMonitor.setStatusMessage("Creating Processing Presentation...");
		
		CyNetwork targetNetwork = manager.getCurrentNetwork();
		System.out.println("* Creating Processing presentation for: " + targetNetwork);

		JFrame frame = new JFrame("Processing Presentation OSGi");
		pFactory.addPresentation(frame, manager.getCurrentNetworkView());
		
		System.out.println("=======> Presentation OK");
		
		//taskMonitor.setProgress(1.0);
	}

}

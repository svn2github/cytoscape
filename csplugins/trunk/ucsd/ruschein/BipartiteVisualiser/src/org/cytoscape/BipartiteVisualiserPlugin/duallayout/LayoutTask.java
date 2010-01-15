package org.cytoscape.BipartiteVisualiserPlugin.duallayout;

import giny.view.EdgeView;
import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class LayoutTask implements Task {
	
	private final EdgeView edgeView;

	private final CyNetwork parentNetwork;
	private final CyNetwork network1;
	private final CyNetwork network2;
	
	public LayoutTask(final EdgeView edgeView, final CyNetwork parentNetwork, final CyNetwork network1, final CyNetwork network2) {
		this.edgeView = edgeView;
		this.parentNetwork = parentNetwork;
		this.network1 = network1;
		this.network2 = network2;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void halt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTaskMonitor(TaskMonitor arg0)
			throws IllegalThreadStateException {
		// TODO Auto-generated method stub
		
	}

}

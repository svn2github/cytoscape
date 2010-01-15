package org.cytoscape.BipartiteVisualiserPlugin.duallayout;

import giny.view.EdgeView;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.TaskMonitor;

public class LayoutEngine {
	
	private final EdgeView edgeView;

	private final CyNetwork parentNetwork;
	private final CyNetwork network1;
	private final CyNetwork network2;

	public LayoutEngine(final EdgeView edgeView, final CyNetwork parentNetwork,
			final CyNetwork network1, final CyNetwork network2) {
		
		this.edgeView = edgeView;
		this.parentNetwork = parentNetwork;
		this.network1 = network1;
		this.network2 = network2;

	}
	
	public void doLayout(TaskMonitor taskMonitor) {
		if (taskMonitor != null)
			taskMonitor.setPercentCompleted(0);
		
		// Create network
		int[] nodes1 = network1.getNodeIndicesArray();
		int[] nodes2 = network2.getNodeIndicesArray();
		
			
		
	}
	
	private void pickEdges() {
		
		
	}
	
	private void performeLayout() {
		
	}

}

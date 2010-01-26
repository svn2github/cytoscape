package org.cytoscape.BipartiteVisualiserPlugin.duallayout;

import giny.view.EdgeView;
import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class LayoutTask implements Task {

	private final LayoutEngine engine;
	private TaskMonitor taskMonitor;

	public LayoutTask(final EdgeView edgeView, final CyNetwork parentNetwork,
			final CyNetwork network1, final CyNetwork network2) {
		this.engine = new LayoutEngine(edgeView, parentNetwork, network1, network2);
	}

	public String getTitle() {
		return "Bipartite Layout";
	}

	public void halt() {
		// TODO Auto-generated method stub

	}

	public void run() {
		setPercentCompleted(-1);
		setStatus("Running Bipartite Layout...");

		// Run the engine
		engine.doLayout(taskMonitor);
		
		setStatus("Layout Finished!");
		setPercentCompleted(100);
	}

	public void setTaskMonitor(TaskMonitor arg0)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	private void setPercentCompleted(int percent) {
		if (taskMonitor != null)
			taskMonitor.setPercentCompleted(percent);
	}

	private void setStatus(String message) {
		if (taskMonitor != null)
			taskMonitor.setStatus(message);
	}

	private void setException(Throwable t, String message) {
		if (taskMonitor != null)
			taskMonitor.setException(t, message);
	}

}

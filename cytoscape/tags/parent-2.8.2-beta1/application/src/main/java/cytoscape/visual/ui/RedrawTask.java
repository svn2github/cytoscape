package cytoscape.visual.ui;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

public class RedrawTask implements Task {

	private final CyNetworkView view;
	private TaskMonitor taskMonitor;

	RedrawTask(CyNetworkView view) {
		this.view = view;
	}

	public void run() {
		taskMonitor.setStatus("Updating network view.  Please wait...");
		taskMonitor.setPercentCompleted(-1);

		try {
			view.redrawGraph(false, true);
		} catch (Exception e) {
			taskMonitor.setException(e,
					"Could not update network view for network: "
							+ view.getTitle());
		}

		taskMonitor.setPercentCompleted(100);
	}

	public void halt() {
	}

	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "Updating Network View";
	}

}

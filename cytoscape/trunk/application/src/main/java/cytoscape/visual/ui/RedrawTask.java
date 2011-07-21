package cytoscape.visual.ui;

import cytoscape.logger.CyLogger;
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
		setStatus("Updating network view.  Please wait...");
		setPercentCompleted(-1);

		try {
			view.redrawGraph(false, true);
		} catch (Exception e) {
			setException(e,
					"Could not update network view for network: "
							+ view.getTitle());
		}

		setPercentCompleted(100);
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

	private void setStatus(String status) {
		if (taskMonitor != null)
			taskMonitor.setStatus(status);
	}

	private void setPercentCompleted(int pc) {
		if (taskMonitor != null)
			taskMonitor.setPercentCompleted(pc);
	}

	private void setException(Throwable e, String s) {
		if (taskMonitor != null)
			taskMonitor.setException(e,s);
		CyLogger.getLogger("Network Redraw").warning(s,e);
	}

}

package cytoscape.visual.customgraphic;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class RestoreImageTask implements Task {
	
	private TaskMonitor taskMonitor;

	private static final CyLogger logger = CyLogger.getLogger();


	/**
	 * Execute task.<br>
	 */
	public void run() {
		taskMonitor
				.setStatus("Loding image library from local disk.\n\nPlease wait...");
		taskMonitor.setPercentCompleted(-1);
		

		final long startTime = System.currentTimeMillis();
		final CustomGraphicsManager manager = Cytoscape.getVisualMappingManager()
				.getCustomGraphicsManager();

		manager.restoreImages();

		long endTime = System.currentTimeMillis();
		double sec = (endTime - startTime) / (1000.0);
		logger.info("Image saving process finished in " + sec + " sec.");
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return "Loading Image Library";
	}
	
	
	
}

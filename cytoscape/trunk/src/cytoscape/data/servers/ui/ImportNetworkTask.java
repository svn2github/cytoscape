package cytoscape.data.servers.ui;

import java.io.IOException;
import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.NetworkTableReader;
import cytoscape.data.readers.TextTableReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class ImportNetworkTask implements Task {
	private final GraphReader reader;
	private final URL source;
	
	private TaskMonitor taskMonitor;
	
	public ImportNetworkTask(final GraphReader reader, final URL source) {
		this.reader = reader;
		this.source = source;
	}

	/**
	 * Executes Task.
	 */
	public void run() {

		taskMonitor.setStatus("Loading network and edge attributes...");
		taskMonitor.setPercentCompleted(-1);

		try {
			reader.read();
			Cytoscape.createNetwork(reader, true, null);
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null,
					source);
			
			
			taskMonitor.setPercentCompleted(100);
		} catch (IOException e) {
			e.printStackTrace();
			taskMonitor.setException(e, "Unable to import network.");
		}

		informUserOfAnnotationStats();
	}

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfAnnotationStats() {
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Succesfully loaded network and edge attributes.");

		taskMonitor.setStatus(sb.toString());
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
		return new String("Loading Annotation");
	}

}

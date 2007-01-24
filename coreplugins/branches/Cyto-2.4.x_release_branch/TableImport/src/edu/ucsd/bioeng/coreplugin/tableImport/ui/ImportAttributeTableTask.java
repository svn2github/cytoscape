package edu.ucsd.bioeng.coreplugin.tableImport.ui;

import java.io.IOException;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader;

public class ImportAttributeTableTask implements Task {
	private TextTableReader reader;

	private String source;

	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            File.
	 * @param fileType
	 *            FileType, e.g. Cytoscape.FILE_SIF or Cytoscape.FILE_GML.
	 */

	public ImportAttributeTableTask(TextTableReader reader, String source) {
		this.reader = reader;
		this.source = source;
	}

	/**
	 * Executes Task.
	 */
	public void run() {

		taskMonitor.setStatus("Loading attribute data file...");
		taskMonitor.setPercentCompleted(-1);

		try {
			reader.readTable();
			taskMonitor.setPercentCompleted(100);
			informUserOfAnnotationStats();
		} catch (IOException e) {
			e.printStackTrace();
			taskMonitor.setException(e, "Unable to import annotation data.");
		}

	}

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfAnnotationStats() {
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Succesfully loaded attribute data from:\n\n");
		sb.append(source + "\n\n");
		
		sb.append(reader.getReport());

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
		return new String("Loading Attributes");
	}

}

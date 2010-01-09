package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

public class SearchTask implements Task {
	private TaskMonitor taskMonitor = null;
	boolean needsToHalt = false;
	static int numOfRuns = 1;
	
	private SearchParameters parameters;

	public SearchTask(final SearchParameters parameters) {
		this.parameters = parameters;
	}

	public void run() {
		setPercentCompleted(0);
		setStatus("Searching...");

		final CyNetwork network = parameters.network;

		//
		// Stage 1.C: Read network file
		//

		List<Result> results = new ArrayList<Result>();
		ResultsPanel resultsPanel = new ResultsPanel(null, results);
		resultsPanel.setVisible(true);
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.EAST);
		cytoPanel.add("DenovoPGNetworkAlignment Results " + (numOfRuns++),
				resultsPanel);
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(resultsPanel));
		cytoPanel.setState(CytoPanelState.DOCK);
	}

	public void halt() {
		needsToHalt = true;
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "DenovoPGNetworkAlignment";
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

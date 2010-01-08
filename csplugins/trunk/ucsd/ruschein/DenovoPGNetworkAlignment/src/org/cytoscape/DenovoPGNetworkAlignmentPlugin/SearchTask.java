package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;
import java.io.BufferedReader;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;


public class SearchTask implements Task
{
	TaskMonitor taskMonitor = null;
	boolean needsToHalt = false;
	static int numOfRuns = 1;

	public SearchTask() {
	}


	public void run() {
		//
		// Stage 1.A: Read input files
		//
		
		setPercentCompleted(0);
		setStatus("Reading class file...");
		try
		{
		}
		catch (Exception e)
		{
			setException(e, e.getMessage());
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 1.B: Read expression matrix file
		//

		setPercentCompleted(5);
		setStatus("Reading expression matrix file...");
		try {
		}
		catch (final Exception e) {
			setException(e, e.getMessage());
			return;
		}

		if (needsToHalt) return;

		//
		// Stage 1.C: Read network file
		//



		List<Result> results = new ArrayList<Result>();
		ResultsPanel resultsPanel = new ResultsPanel(null, results);
		resultsPanel.setVisible(true);
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		cytoPanel.add("DenovoPGNetworkAlignment Results " + (numOfRuns++), resultsPanel);
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(resultsPanel));
		cytoPanel.setState(CytoPanelState.DOCK);
	}

	public void halt()
	{
		needsToHalt = true;
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) 
	{
		this.taskMonitor = taskMonitor;
	}

	public String getTitle()
	{
		return "DenovoPGNetworkAlignment";
	}

	private void setPercentCompleted(int percent)
	{
		if (taskMonitor != null)
			taskMonitor.setPercentCompleted(percent);
	}

	private void setStatus(String message)
	{
		if (taskMonitor != null)
			taskMonitor.setStatus(message);
	}

	private void setException(Throwable t, String message)
	{
		if (taskMonitor != null)
			taskMonitor.setException(t, message);
	}
}

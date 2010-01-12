package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import networks.denovoPGNetworkAlignment.BFEdge;
import networks.denovoPGNetworkAlignment.HCScoringFunction;
import networks.denovoPGNetworkAlignment.HCSearch2;
import networks.denovoPGNetworkAlignment.SouravScore;
import networks.linkedNetworks.TypedLinkNetwork;
import networks.linkedNetworks.TypedLinkNodeModule;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class SearchTask implements Task {
	private TaskMonitor taskMonitor = null;
	boolean needsToHalt = false;
	static int numOfRuns = 1;

	private SearchParameters parameters;

	private HCScoringFunction hcScoringFunction;

	public SearchTask(final SearchParameters parameters) {
		this.parameters = parameters;
	}

	public void run() {
		setPercentCompleted(0);
		setStatus("Searching complexes...");

		final ConvertCyNetworkToSFNetworks converter = new ConvertCyNetworkToSFNetworks(
				parameters.getNetwork(), parameters.getPhysicalEdgeAttrName(),
				parameters.getGeneticEdgeAttrName());
		hcScoringFunction = new SouravScore(converter.getPhysicalNetwork(),
				converter.getGeneticNetwork(), (float) parameters.getAlpha(),
				(float) parameters.getAlphaMultiplier());
		hcScoringFunction.Initialize(converter.getPhysicalNetwork(), converter
				.getGeneticNetwork());
		final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results = HCSearch2
				.search(converter.getPhysicalNetwork(), converter.getGeneticNetwork(), hcScoringFunction);
		
		setStatus("Search finished!\n\n" + HCSearch2.report(results));
		
		setPercentCompleted(100);

	}

	public void halt() {
		needsToHalt = true;
	}

	public void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "Denovo PG Network Alignment";
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

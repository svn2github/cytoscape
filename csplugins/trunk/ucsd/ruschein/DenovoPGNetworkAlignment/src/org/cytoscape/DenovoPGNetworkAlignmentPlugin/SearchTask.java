package org.cytoscape.DenovoPGNetworkAlignmentPlugin;

import java.util.ArrayList;

import networks.SFNetwork;
import networks.denovoPGNetworkAlignment.BFEdge;
import networks.denovoPGNetworkAlignment.HCScoringFunction;
import networks.denovoPGNetworkAlignment.HCSearch2;
import networks.denovoPGNetworkAlignment.SouravScore;
import networks.linkedNetworks.TypedLinkEdge;
import networks.linkedNetworks.TypedLinkNetwork;
import networks.linkedNetworks.TypedLinkNodeModule;
import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


/**
 * @uthor kono, ruschein
 */
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
		setStatus("Searching for complexes...");

		final CyNetwork inputNetwork = parameters.getNetwork();

		final ConvertCyNetworkToSFNetworks converter = new ConvertCyNetworkToSFNetworks(
				inputNetwork, parameters.getPhysicalEdgeAttrName(),
				parameters.getGeneticEdgeAttrName());
		final SFNetwork physicalNetwork = converter.getPhysicalNetwork();
		final SFNetwork geneticNetwork  = converter.getGeneticNetwork();
		
		final HCScoringFunction hcScoringFunction =
			new SouravScore(physicalNetwork, geneticNetwork,
		                       (float)parameters.getAlpha(),
		                       (float)parameters.getAlphaMultiplier());
		hcScoringFunction.Initialize(physicalNetwork, geneticNetwork);

		final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results =
			HCSearch2.search(physicalNetwork, geneticNetwork, hcScoringFunction);

		final TypedLinkNetwork<String, Float> pNet = physicalNetwork.asTypedLinkNetwork();
		final TypedLinkNetwork<String, Float> gNet = geneticNetwork.asTypedLinkNetwork();

		final NestedNetworkCreator nnCreator =
			new NestedNetworkCreator(results, inputNetwork, pNet, gNet, parameters.getEdgeCutoff());

		setStatus("Search finished!\n\n" + "Number of complexes = "
		          + nnCreator.getOverviewNetwork().getNodeCount() + "\n\n"
		          + HCSearch2.report(results));

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

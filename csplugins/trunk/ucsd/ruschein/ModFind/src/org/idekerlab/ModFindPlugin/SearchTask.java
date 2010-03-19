package org.idekerlab.ModFindPlugin;

import java.util.*;
import org.idekerlab.ModFindPlugin.data.*;
import org.idekerlab.ModFindPlugin.ModFinder.BFEdge;
import org.idekerlab.ModFindPlugin.ModFinder.HCScoringFunction;
import org.idekerlab.ModFindPlugin.ModFinder.HCSearch2;
import org.idekerlab.ModFindPlugin.ModFinder.SouravScore;
import org.idekerlab.ModFindPlugin.networks.*;
import org.idekerlab.ModFindPlugin.networks.linkedNetworks.*;

import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


/**
 * @author kono, ruschein
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
		final float SEARCH_PERCENTAGE      = 40.0f; // Progress bar should go up to here for the search part.
		final float COMPUTE_SIG_PERCENTAGE = 70.0f;

		taskMonitor.setPercentCompleted(1);
		taskMonitor.setStatus("Searching for complexes...");

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
			HCSearch2.search(physicalNetwork, geneticNetwork, hcScoringFunction,
			                 taskMonitor, SEARCH_PERCENTAGE);
		final double pValueThreshold = parameters.getPValueThreshold();
		computeSig(results, geneticNetwork, pValueThreshold, taskMonitor, SEARCH_PERCENTAGE, COMPUTE_SIG_PERCENTAGE);

		final TypedLinkNetwork<String, Float> pNet = physicalNetwork.asTypedLinkNetwork();
		final TypedLinkNetwork<String, Float> gNet = geneticNetwork.asTypedLinkNetwork();

		final NestedNetworkCreator nnCreator =
			new NestedNetworkCreator(results, inputNetwork, pNet, gNet,
			                         pValueThreshold, taskMonitor,
			                         100.0f - COMPUTE_SIG_PERCENTAGE);

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
		return "ModFind";
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

	//This function compute a p-value for each edge in the complex-complex network
	private static void computeSig(final TypedLinkNetwork<TypedLinkNodeModule<String,BFEdge>,BFEdge> results, SFNetwork gnet,
	                               double pValueThreshold, final TaskMonitor taskMonitor, final float startProgressPercentage,
	                               final float endProgressPercentage)
	{
		taskMonitor.setStatus("4. Computing permutations...");
		final int NUM_PERMS = 100000;

		Map<Integer,DoubleVector> numLinks2empiricalDist = new HashMap<Integer,DoubleVector>(30);
		TypedLinkNetwork<String,Float> gn = gnet.asTypedLinkNetwork();
        
		DoubleVector allEdgeValues = new DoubleVector(gn.numEdges());
	
		for(TypedLinkEdge<String,Float> eachEdge : gn.edgeIterator())
			allEdgeValues.add(eachEdge.value());
        
		//Iterate through each complex-complex interaction in the network:
		//(1) Check if that number of edges has been previously samples, if so, output p-value
		//(2) If not, sample and produce empirical distribution. Cache distribution!
		final int TOTAL_NUM_EDGES = results.numEdges();
		int currentEdgeNum = 0;
		final Set<TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge>> deleteSet = new HashSet<TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge>>();
		for (TypedLinkEdge<TypedLinkNodeModule<String,BFEdge>,BFEdge> edge : results.edgeIterator()) {
			++currentEdgeNum;

			//Find number of edges and sum of edge values in the hyperedge
			int numGeneticLinks = 0;
			double sumOfGeneticValues=0.0;
			for (TypedLinkEdge<String,Float> eachEdge : gn.getAllEdgeValues(edge.source().value().asStringSet(), edge.target().value().asStringSet()))
			{
				sumOfGeneticValues += eachEdge.value();
				++numGeneticLinks;
			}
            
			//No need to sample
			if (numLinks2empiricalDist.containsKey(numGeneticLinks)) {
				//How to save p-value?
				double pVal = numLinks2empiricalDist.get(numGeneticLinks).getEmpiricalPvalue(sumOfGeneticValues, true);
				if (pVal < pValueThreshold)
					edge.value().setLinkMerge((float)pVal);
				else
					deleteSet.add(edge);
			} else {
				double numGreaterThan = 0.0;
				numLinks2empiricalDist.put(numGeneticLinks, new DoubleVector(NUM_PERMS));
				
				for(int i = 1; i <= NUM_PERMS; i++) {
					double permVal = allEdgeValues.sample(numGeneticLinks, false).sum();
					numLinks2empiricalDist.get(numGeneticLinks).add(permVal);
					if(permVal>sumOfGeneticValues) numGreaterThan+=1.0;
				}
				
				//Where to save pval
				double pVal = numGreaterThan/NUM_PERMS;
				if (pVal < pValueThreshold)
					edge.value().setLinkMerge((float)pVal);
				else
					deleteSet.add(edge);
			}

			final int percentCompleted = Math.round(startProgressPercentage + (endProgressPercentage - startProgressPercentage) * (float)currentEdgeNum / TOTAL_NUM_EDGES);
			taskMonitor.setPercentCompleted(percentCompleted);
			taskMonitor.setStatus("4. Computing permutations: " + percentCompleted + "% completed.");
		}
		results.removeAllEdges(deleteSet);
	}
}

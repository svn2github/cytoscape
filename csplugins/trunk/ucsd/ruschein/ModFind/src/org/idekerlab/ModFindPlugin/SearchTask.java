package org.idekerlab.ModFindPlugin;

import java.net.URL;
import java.util.*;

import org.idekerlab.ModFindPlugin.data.*;
import org.idekerlab.ModFindPlugin.ModFinder.BFEdge;
import org.idekerlab.ModFindPlugin.ModFinder.HCScoringFunction;
import org.idekerlab.ModFindPlugin.ModFinder.HCSearch2;
import org.idekerlab.ModFindPlugin.ModFinder.SouravScore;
import org.idekerlab.ModFindPlugin.networks.*;
import org.idekerlab.ModFindPlugin.networks.hashNetworks.FloatHashNetwork;
import org.idekerlab.ModFindPlugin.networks.linkedNetworks.*;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;


/**
 * @author kono, ruschein
 */
public class SearchTask implements Task {
	public static URL url1 = ModFindPlugin.class.getResource("/resources/ModFind_overview_vs.props");
	public static URL url2 = ModFindPlugin.class.getResource("/resources/ModFind_module_vs.props");
	private static String VS_OVERVIEW_NAME = "ModFind";
	private static String VS_MODULE_NAME = "ModFind_module";
	private static VisualStyle vs_overview = null;
	private static VisualStyle vs_module = null;
//
	static {		
		// Create visualStyles based on the definition in property files
		Set<String> names = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyleNames();
		if (!names.contains(VS_OVERVIEW_NAME)){
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,url1);
		}
		if (!names.contains(VS_MODULE_NAME)){
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null,url2);
		}
	}
	
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
		final float COMPUTE_SIG_PERCENTAGE = 95.0f; // Progress bar should go up to here for the permutations part.

		taskMonitor.setPercentCompleted(1);
		taskMonitor.setStatus("Searching for complexes...");

		final CyNetwork physicalInputNetwork = parameters.getPhysicalNetwork();
		final CyNetwork geneticInputNetwork = parameters.getGeneticNetwork();

		final SFNetwork physicalNetwork =
			convertCyNetworkToSFNetwork(physicalInputNetwork, parameters.getPhysicalEdgeAttrName());
		final SFNetwork geneticNetwork =
			convertCyNetworkToSFNetwork(geneticInputNetwork, parameters.getGeneticEdgeAttrName());
		
		final HCScoringFunction hcScoringFunction =
			new SouravScore(physicalNetwork, geneticNetwork,
		                       (float)parameters.getAlpha(),
		                       (float)parameters.getAlphaMultiplier());
		hcScoringFunction.Initialize(physicalNetwork, geneticNetwork);

		final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results =
			HCSearch2.search(physicalNetwork, geneticNetwork, hcScoringFunction,
			                 taskMonitor, SEARCH_PERCENTAGE);
		final double pValueThreshold = parameters.getPValueThreshold();
		final int numberOfSamples = parameters.getNumberOfSamples();
		computeSig(results, geneticNetwork, pValueThreshold, numberOfSamples, taskMonitor, SEARCH_PERCENTAGE, COMPUTE_SIG_PERCENTAGE);

		final TypedLinkNetwork<String, Float> pNet = physicalNetwork.asTypedLinkNetwork();
		final TypedLinkNetwork<String, Float> gNet = geneticNetwork.asTypedLinkNetwork();

		final NestedNetworkCreator nnCreator =
			new NestedNetworkCreator(results, physicalInputNetwork, geneticInputNetwork,
			                         pNet, gNet, pValueThreshold, taskMonitor,
			                         100.0f - COMPUTE_SIG_PERCENTAGE);

		setStatus("Search finished!\n\n" + "Number of complexes = "
		          + nnCreator.getOverviewNetwork().getNodeCount() + "\n\n"
		          + HCSearch2.report(results));

		setPercentCompleted(100);
		
		// Set the visualSTyle for the overview network
		applyVisualStyle(nnCreator.getOverviewNetwork(),vs_overview);
		
		
		// Create an edge attribute "overlapScore", which is defined as NumberOfSharedNodes/min(two network sizes)
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		int[] edgeIndexArray = nnCreator.getOverviewNetwork().getEdgeIndicesArray();
		
		for (int i=0; i<edgeIndexArray.length; i++ ){
		
			CyEdge aEdge = (CyEdge) nnCreator.getOverviewNetwork().getEdge(edgeIndexArray[i]);
			int NumberOfSharedNodes = getNumberOfSharedNodes((CyNetwork)aEdge.getSource().getNestedNetwork(), 
					(CyNetwork)aEdge.getTarget().getNestedNetwork());
			
			int minNodeCount = Math.min(aEdge.getSource().getNestedNetwork().getNodeCount(), 
								aEdge.getTarget().getNestedNetwork().getNodeCount());
			
			double overlapScore = (double)NumberOfSharedNodes/minNodeCount;
			cyEdgeAttrs.setAttribute(aEdge.getIdentifier(), "overlapScore", overlapScore);			
		}

		
	}

	
	private static int getNumberOfSharedNodes(CyNetwork networkA, CyNetwork networkB){
		
		int[] nodeIndicesA = networkA.getNodeIndicesArray();
		int[] nodeIndicesB = networkB.getNodeIndicesArray();
		
		
		HashSet<Integer> hashSet = new HashSet<Integer>();
		for (int i=0; i< nodeIndicesA.length; i++){
			hashSet.add( new Integer(nodeIndicesA[i]));
		}

		int sharedNodeCount =0;
		for (int i=0; i< nodeIndicesB.length; i++){
			if (hashSet.contains(new Integer(nodeIndicesB[i]))){
				sharedNodeCount++;
			}
		}
		
		return sharedNodeCount;
	}

	
	private static void applyVisualStyle(CyNetwork net, VisualStyle vs) {
		CyNetworkView view = Cytoscape.getNetworkView( net.getIdentifier() );
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		view.setVisualStyle(vs.getName());
		vmm.setNetworkView(view);
		vmm.setVisualStyle(vs);
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
	                               final double pValueThreshold, final int numberOfSamples, final TaskMonitor taskMonitor, final float startProgressPercentage,
	                               final float endProgressPercentage)
	{
		taskMonitor.setStatus("4. Computing permutations...");

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

			// Find number of edges and sum of edge values in the hyperedge
			int numGeneticLinks = 0;
			double sumOfGeneticValues=0.0;
			for (final TypedLinkEdge<String,Float> eachEdge :
			     gn.getAllEdgeValues(edge.source().value().asStringSet(), edge.target().value().asStringSet()))
			{
				sumOfGeneticValues += eachEdge.value();
				++numGeneticLinks;
			}
            
			// No need to sample
			double pVal;
			if (numLinks2empiricalDist.containsKey(numGeneticLinks)) {
				//How to save p-value?
				pVal = numLinks2empiricalDist.get(numGeneticLinks).getEmpiricalValueFromSortedDist(sumOfGeneticValues);
			} else {
				DoubleVector temp = new DoubleVector(numberOfSamples);
				
				for (int i = 1; i <= numberOfSamples; i++) {
					double permVal = allEdgeValues.sample(numGeneticLinks, false).sum();
					temp.add(permVal);
				}
				
				temp = temp.sort();
				numLinks2empiricalDist.put(numGeneticLinks, temp);

				// Where to save pval
				pVal = temp.getEmpiricalValueFromSortedDist(sumOfGeneticValues);
			}

			if (pVal < pValueThreshold)
				edge.value().setLinkMerge((float)pVal);
			else
				deleteSet.add(edge);

			final float permutationsFraction = (float)currentEdgeNum / TOTAL_NUM_EDGES;
			final float percentCompleted = startProgressPercentage + (endProgressPercentage - startProgressPercentage) * permutationsFraction;
			taskMonitor.setPercentCompleted(Math.round(percentCompleted));
			taskMonitor.setStatus("4. Computing permutations: " + Math.round(permutationsFraction * 100.0f) + "% completed.");
		}
		results.removeAllEdges(deleteSet);
	}

	/**
	 *  Converts a Cytoscape-style network to an SFNetwork.
	 *
	 *  @param inputNetwork    name of the network that will be converted
	 *  @param numericAttrName optional name of a numeric edge attribute.  Should this be missing, 1.0 will be assumed for all edges
	 */
	private SFNetwork convertCyNetworkToSFNetwork(final CyNetwork inputNetwork, final String numericAttrName)
		throws IllegalArgumentException, ClassCastException
	{
		@SuppressWarnings("unchecked") List<CyEdge> edges = (List<CyEdge>)inputNetwork.edgesList();
		final FloatHashNetwork outputNetwork = new FloatHashNetwork(/* selfOk = */false, /* directed = */false, /* startsize = */1);

		if (inputNetwork == null)
			throw new IllegalArgumentException("input parameter inputNetwork must not be null!");

		if (numericAttrName == null || numericAttrName.length() == 0) {
			for (final CyEdge edge : edges)
				outputNetwork.add(edge.getSource().getIdentifier(),
				                  edge.getTarget().getIdentifier(), 1.0f);
		} else {
			// Validate that "numericAttrName" is a known numeric edge attribute.
			final CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			final byte edgeAttribType = edgeAttributes.getType(numericAttrName);
			if (edgeAttribType != CyAttributes.TYPE_FLOATING && edgeAttribType != CyAttributes.TYPE_INTEGER)
				throw new IllegalArgumentException("\"" + numericAttrName
				                                   + "\" is not the name of a known numeric edge attribute!");

			for (final CyEdge edge : edges) {
				final String edgeID = edge.getIdentifier();
				if (edgeAttribType == CyAttributes.TYPE_FLOATING) {
					final Double attrValue = edgeAttributes.getDoubleAttribute(edgeID, numericAttrName);
					if (attrValue != null)
						outputNetwork.add(edge.getSource().getIdentifier(),
						                  edge.getTarget().getIdentifier(),
						                  (float)(double)attrValue);
				} else { // Assume we have an integer attribute.
					final Integer attrValue = edgeAttributes.getIntegerAttribute(edgeID, numericAttrName);
					if (attrValue != null)
						outputNetwork.add(edge.getSource().getIdentifier(),
						                  edge.getTarget().getIdentifier(),
						                  (float)(int)attrValue);
				}
			}
		}

		return outputNetwork;
	}
}

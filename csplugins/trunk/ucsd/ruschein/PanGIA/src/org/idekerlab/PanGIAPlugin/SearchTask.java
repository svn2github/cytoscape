package org.idekerlab.PanGIAPlugin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.ModFinder.BFEdge;
import org.idekerlab.PanGIAPlugin.ModFinder.HCScoringFunction;
import org.idekerlab.PanGIAPlugin.ModFinder.HCSearch2;
import org.idekerlab.PanGIAPlugin.ModFinder.SouravScore;
import org.idekerlab.PanGIAPlugin.ModFinder.BFEdge.InteractionType;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;
import org.idekerlab.PanGIAPlugin.networks.SFNetwork;
import org.idekerlab.PanGIAPlugin.networks.SNodeModule;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.FloatHashNetwork;
import org.idekerlab.PanGIAPlugin.networks.linkedNetworks.*;
import org.idekerlab.PanGIAPlugin.utilities.collections.HashMapUtil;
import org.idekerlab.PanGIAPlugin.utilities.collections.SetUtil;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
//import cytoscape.util.ProbabilityScaler;
//import cytoscape.util.ScalingMethod;


/**
 * @author kono, ruschein
 */
public class SearchTask implements Task {
	
	private static final float SEARCH_PERCENTAGE      = 40.0f; // Progress bar should go up to here for the search part.
	private static final float COMPUTE_SIG_PERCENTAGE = 95.0f; // Progress bar should go up to here for the permutations part.
	
	protected static final String EDGE_TYPE_ATTR_NAME = "Module Finder.Interaction Type";
	
	private TaskMonitor taskMonitor = null;
	boolean needsToHalt = false;
	static int numOfRuns = 1;

	private SearchParameters parameters;

	
	public SearchTask(final SearchParameters parameters) {
		this.parameters = parameters;
	}

	public void run() {
		taskMonitor.setPercentCompleted(1);
		taskMonitor.setStatus("Searching for modules...");
		
		if (needsToHalt) return;
		
		/***
		 * TODO: Annotation
		 * Use absolute value for training?
		 */
		
		
		final CyNetwork physicalInputNetwork = parameters.getPhysicalNetwork();
		addEdgeType(physicalInputNetwork, InteractionType.Physical);
		
		SFNetwork physicalNetwork = convertCyNetworkToSFNetwork(physicalInputNetwork,
									      parameters.getPhysicalEdgeAttrName(),
									      parameters.getPhysicalScalingMethod());

		final CyNetwork geneticInputNetwork = parameters.getGeneticNetwork();
		addEdgeType(geneticInputNetwork, InteractionType.Genetic);
		
		SFNetwork geneticNetwork = convertCyNetworkToSFNetwork(geneticInputNetwork,
									     parameters.getGeneticEdgeAttrName(),
									     parameters.getGeneticScalingMethod());
		
		if (needsToHalt) return;
		
		//Apply the degree filter
		int degreeFilter = parameters.getPhysicalNetworkFilterDegree();
		if (degreeFilter!=-1)
		{
			TypedLinkNetwork<String,Float> ptlnet = physicalNetwork.asTypedLinkNetwork();
			Set<String> pnodes = physicalNetwork.getNodes();
			physicalNetwork = new FloatHashNetwork(ptlnet.subNetwork(pnodes, degreeFilter));
		}
		
		if (needsToHalt) return;
		
		//Load trainingComplexes
		List<SNodeModule> trainingComplexes = null;
		if (parameters.getComplexTraining() || parameters.getComplexAnnotation())
		{
			final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
			Map<String,Set<String>> annot_node = new HashMap<String,Set<String>>(1000);
			
			for (String gnode : geneticNetwork.nodeIterator())
				for (Object annot : nodeAttr.getListAttribute(gnode, parameters.getAnnotationAttrName()))
					HashMapUtil.updateMapSet(annot_node, annot.toString(), gnode);
			
			trainingComplexes = new ArrayList<SNodeModule>(annot_node.size());
			
			for (String annot : annot_node.keySet())
				trainingComplexes.add(new SNodeModule(annot,annot_node.get(annot)));
		}
		
		if (needsToHalt) return;
		
		//Perform training
		if (parameters.getComplexTraining())
		{
			physicalNetwork = ComplexRegression.complexRegress(physicalNetwork, trainingComplexes, true);
			geneticNetwork = ComplexRegression.complexRegress(geneticNetwork, trainingComplexes, true);
		}
		
		if (needsToHalt) return;
		
		//Initialize the scoring function
		final HCScoringFunction hcScoringFunction =
			new SouravScore(physicalNetwork, geneticNetwork,
		                       (float)parameters.getAlpha(),
		                       (float)parameters.getAlphaMultiplier());
		hcScoringFunction.Initialize(physicalNetwork, geneticNetwork);

		if (needsToHalt) return;
		
		//Run the clustering algorithm
		final TypedLinkNetwork<TypedLinkNodeModule<String, BFEdge>, BFEdge> results =
			HCSearch2.search(physicalNetwork, geneticNetwork, hcScoringFunction,
			                 taskMonitor, SEARCH_PERCENTAGE, this);
		
		if (needsToHalt) return;
		
		//Compute significance
		final double pValueThreshold = parameters.getPValueThreshold();
		final int numberOfSamples = parameters.getNumberOfSamples();
		computeSig(results, geneticNetwork, pValueThreshold, numberOfSamples, taskMonitor, SEARCH_PERCENTAGE, COMPUTE_SIG_PERCENTAGE);

		if (needsToHalt) return;
		
		//Annotate complexes
		Map<TypedLinkNodeModule<String, BFEdge>,String> module_name = null;
		System.out.println("PARAM: "+parameters.getComplexTraining()+", "+parameters.getComplexAnnotation()+", "+parameters.getAnnotationThreshold());
		
		if (parameters.getComplexAnnotation())
		{
			module_name = new HashMap<TypedLinkNodeModule<String, BFEdge>,String>(results.numNodes(),1);
			
			for (SNodeModule complex : trainingComplexes)
			{
				double bestScore = 0;
				TypedLinkNodeModule<String, BFEdge> bestNode = null;
				
				for (TypedLinkNode<TypedLinkNodeModule<String, BFEdge>,BFEdge> n : results.nodeIterator())
				{
					double jaccard = SetUtil.jaccard(complex.getMemberData(), n.value().getMemberValues());
					if (jaccard>bestScore)
					{
						bestScore = jaccard;
						bestNode = n.value();
					}
				}
				
				if (bestNode!=null && bestScore>=parameters.getAnnotationThreshold()) module_name.put(bestNode, complex.getID());
			}
			System.out.println("Number of module annotation matches: "+module_name.size());
		}
		
		if (needsToHalt) return;
		
		final TypedLinkNetwork<String, Float> pNet = physicalNetwork.asTypedLinkNetwork();
		final TypedLinkNetwork<String, Float> gNet = geneticNetwork.asTypedLinkNetwork();

		final NestedNetworkCreator nnCreator =
			new NestedNetworkCreator(results, physicalInputNetwork, geneticInputNetwork,
			                         pNet, gNet, pValueThreshold, taskMonitor,
			                         100.0f - COMPUTE_SIG_PERCENTAGE, module_name);

		setStatus("Search finished!\n\n" + "Number of modules = "
		          + nnCreator.getOverviewNetwork().getNodeCount() + "\n\n"
		          + HCSearch2.report(results));

		setPercentCompleted(100);
		
		// Create an edge attribute "overlapScore", which is defined as NumberOfSharedNodes/min(two network sizes)
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		int[] edgeIndexArray = nnCreator.getOverviewNetwork().getEdgeIndicesArray();
		
		for (int i = 0; i < edgeIndexArray.length; i++) {
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

	
	public void halt() {
		needsToHalt = true;
	}

	public boolean needsToHalt() {
		return needsToHalt;
	}	
	
	public void setTaskMonitor(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "PanGIA";
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
	private void computeSig(final TypedLinkNetwork<TypedLinkNodeModule<String,BFEdge>,BFEdge> results, SFNetwork gnet,
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

			if (needsToHalt) return;
			
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
				double[] dist = new double[numberOfSamples];
				
				for (int i = 0; i < numberOfSamples; i++) {
					if (needsToHalt) return;
					double permVal = allEdgeValues.sample(numGeneticLinks, false).sum();
					dist[i] = permVal;
				}
				
				DoubleVector temp = new DoubleVector(dist);
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
	private SFNetwork convertCyNetworkToSFNetwork(final CyNetwork inputNetwork, final String numericAttrName,
						      final ScalingMethodX scalingMethod)
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

			// Collect edge attribute values:
			final float[] edgeAttribValues = new float[edges.size()];
			int edgeIndex = 0;
			for (final CyEdge edge : edges) {
				final String edgeID = edge.getIdentifier();
				if (edgeAttribType == CyAttributes.TYPE_FLOATING) {
					final Double attrValue = edgeAttributes.getDoubleAttribute(edgeID, numericAttrName);
					if (attrValue != null)
						edgeAttribValues[edgeIndex] = (float)(double)attrValue;
				} else { // Assume we have an integer attribute.
					final Integer attrValue = edgeAttributes.getIntegerAttribute(edgeID, numericAttrName);
					if (attrValue != null)
						edgeAttribValues[edgeIndex] = (float)(int)attrValue;
				}
				++edgeIndex;
			}

			final StringBuilder errorMessage = new StringBuilder();
			final float[] scaledEdgeAttribValues = scaleEdgeAttribValues(edgeAttribValues, scalingMethod, errorMessage);
			if (scaledEdgeAttribValues == null)
				throw new IllegalArgumentException("attribute values scaling failed: " + errorMessage);

			edgeIndex = 0;
			for (final CyEdge edge : edges) {
				final String edgeID = edge.getIdentifier();
				if (edgeAttributes.getAttribute(edgeID, numericAttrName) != null)
					outputNetwork.add(edge.getSource().getIdentifier(),
							  edge.getTarget().getIdentifier(),
							  scaledEdgeAttribValues[edgeIndex]);
				++edgeIndex;
			}
		}

		return outputNetwork;
	}
	
	private void addEdgeType(final CyNetwork inputNetwork, final InteractionType edgeType) {
		final CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		final List<CyEdge> edgeList = inputNetwork.edgesList();
		
		for(CyEdge edge: edgeList)
			edgeAttr.setAttribute(edge.getIdentifier(), EDGE_TYPE_ATTR_NAME, edgeType.name());
	}

	private float[] scaleEdgeAttribValues(final float[] edgeAttribValues, final ScalingMethodX scalingMethod,
					      final StringBuilder errorMessage)
	{
		float[] scaledEdgeAttribValues = ProbabilityScaler.scale(edgeAttribValues, scalingMethod,
									 errorMessage);
		if (scaledEdgeAttribValues == null || scalingMethod == ScalingMethodX.NONE)
			return scaledEdgeAttribValues;

		// Generate log-likelihood values:
		for (int i = 0; i < scaledEdgeAttribValues.length; ++i) {
			final double p = (double)scaledEdgeAttribValues[i];
			scaledEdgeAttribValues[i] = (float)Math.log(p / (1.0 - p));
		}

		return scaledEdgeAttribValues;
	}
}

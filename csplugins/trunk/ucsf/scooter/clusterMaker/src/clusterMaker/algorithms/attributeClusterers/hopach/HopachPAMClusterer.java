package clusterMaker.algorithms.attributeClusterers.hopach;

import java.util.Arrays;

import javax.swing.JPanel;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterer;
import clusterMaker.algorithms.attributeClusterers.BaseMatrix;
import clusterMaker.algorithms.attributeClusterers.hopach.types.SplitCost;
import clusterMaker.algorithms.numeric.SummaryMethod;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.KnnView;

public class HopachPAMClusterer extends AbstractAttributeClusterer {
	
	SplitCost splitCost;
	SummaryMethod summaryMethod;
	int maxLevel, K, L;
	double minCostReduction;
	boolean forceInitSplit;
	
	public HopachPAMClusterer() {
		logger = CyLogger.getLogger(HopachPAMClusterer.class);
		initializeProperties();
	}
	
	@Override
	public void initializeProperties() {
		super.initializeProperties();
		
		clusterProperties.add(
			new Tunable(
				"dMetric",
				"Distance Metric",
				Tunable.LIST, new Integer(0),
				(Object)BaseMatrix.distanceTypes, null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"splitCost",
				"Split cost type",
				Tunable.LIST, new Integer(0),
				(Object)SplitCost.values(), null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"summaryMethod",
				"Value summarization method",
				Tunable.LIST, new Integer(0),
				(Object)SummaryMethod.values(), null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"maxLevel",
				"Maximum number of splitting level",
				Tunable.INTEGER, new Integer(9),
				1, null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"K",
				"Maximum number of clusters at each level",
				Tunable.INTEGER, new Integer(9),
				1, null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"L",
				"Maximum number of subclusters at each level",
				Tunable.INTEGER, new Integer(9),
				1, null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"forceInitSplit",
				"Force splitting at initial level",
				Tunable.BOOLEAN, new Boolean(false)
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"minCostReduction",
				"Minimum cost reduction for collapse",
				Tunable.DOUBLE, new Double(0.0),
				0.0, null, 0
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"attributeListGroup",
				"Source for array data",
				Tunable.GROUP, new Integer(1)
			)
		);
		
		attributeArray = getAllAttributes();
		clusterProperties.add(
			new Tunable(
				"attributeList",
				"Array sources",
				Tunable.LIST, "",
				(Object)attributeArray, null, Tunable.MULTISELECT
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"selectedOnly",
				"Only use selected nodes/edges for cluster",
				Tunable.BOOLEAN, new Boolean(selectedOnly)
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"clusterAttributes",
				"Cluster attributes as well as nodes",
				Tunable.BOOLEAN, new Boolean(clusterAttributes)
			)
		);
		
		clusterProperties.add(
			new Tunable(
				"createGroups",
				"Create groups from clusters",
				Tunable.BOOLEAN, new Boolean(createGroups)
			)
		);
		
		clusterProperties.initializeProperties();
		updateSettings(true);
	}
	
	@Override
	public void updateSettings() {
		updateSettings(false);
	}
	
	public void updateSettings(boolean force) {
		clusterProperties.updateValues();
		super.updateSettings(force);
		
		Tunable t;
		
		t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force)) {
			distanceMetric = BaseMatrix.distanceTypes[((Integer) t.getValue()).intValue()];
		}
		
		t = clusterProperties.get("splitCost");
		if ((t != null) && (t.valueChanged() || force)) {
			splitCost = SplitCost.values()[((Integer) t.getValue()).intValue()];
		}
		
		t = clusterProperties.get("summaryMethod");
		if ((t != null) && (t.valueChanged() || force)) {
			summaryMethod = SummaryMethod.values()[((Integer) t.getValue()).intValue()];
		}
		
		t = clusterProperties.get("maxLevel");
		if ((t != null) && (t.valueChanged() || force)) {
			maxLevel = ((Integer) t.getValue()).intValue();
		}
		
		t = clusterProperties.get("K");
		if ((t != null) && (t.valueChanged() || force)) {
			K = ((Integer) t.getValue()).intValue();
		}
		
		t = clusterProperties.get("L");
		if ((t != null) && (t.valueChanged() || force)) {
			L = ((Integer) t.getValue()).intValue();
		}
		
		t = clusterProperties.get("forceInitSplit");
		if ((t != null) && (t.valueChanged() || force)) {
			forceInitSplit = ((Boolean) t.getValue()).booleanValue();
		}
		
		t = clusterProperties.get("minCostReduction");
		if ((t != null) && (t.valueChanged() || force)) {
			minCostReduction = ((Double) t.getValue()).doubleValue();
		}
		
		t = clusterProperties.get("clusterAttributes");
		if ((t != null) && (t.valueChanged() || force)) {
			clusterAttributes = ((Boolean) t.getValue()).booleanValue();
		}

		t = clusterProperties.get("selectedOnly");
		if ((t != null) && (t.valueChanged() || force)) {
			selectedOnly = ((Boolean) t.getValue()).booleanValue();
		}

		t = clusterProperties.get("createGroups");
		if ((t != null) && (t.valueChanged() || force)) {
			createGroups = ((Boolean) t.getValue()).booleanValue();
		}

		t = clusterProperties.get("attributeList");
		if ((t != null) && (t.valueChanged() || force)) {
			dataAttributes = (String) t.getValue();
		}
	}

	@Override
	public String getShortName() {
		return "HOPACH";
	}

	@Override
	public String getName() {
		return "HOPACH-PAM";
	}
	
	@Override
	public ClusterViz getVisualizer() {
		return new KnnView();
	}

	@Override
	public JPanel getSettingsPanel() {
		// update attributes
		Tunable attributeTunable = clusterProperties.get("attributeList");
		attributeArray = getAllAttributes();
		attributeTunable.setLowerBound((Object)attributeArray);
		
		return clusterProperties.getTunablePanel();
	}

	@Override
	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		
		if (debug) {
			logger.debug("Performing HOPACH-PAM cluster with k = " + kNumber + " using " + distanceMetric + " and attributes: " + dataAttributes);
		}
		
		// sanity check of parameters
		if (dataAttributes == null || dataAttributes.length() == 0) {
			if (monitor != null) {
				monitor.setException(null, "Error: no attribute list selected");
				logger.warning("Must have an attribute list to use for cluster weighting");
			} else {
				logger.error("Must have an attribute list to use or cluster weighting");
			}
			return;
		}
	
		// get attributes to be used for clustering
		String attributeArray[] = getAttributeArray(dataAttributes);
		Arrays.sort(attributeArray);
		
		HopachPAMCluster algo = new HopachPAMCluster(attributeArray, distanceMetric, logger, monitor);
		algo.setCreateGroups(createGroups);
		algo.setIgnoreMissing(true);
		algo.setSelectedOnly(selectedOnly);
		algo.setDebug(debug);
		algo.setUseSilhouette(useSilhouette);
		algo.setClusterInterface(this);
		algo.setParameters(splitCost, summaryMethod, maxLevel, K, L, forceInitSplit, minCostReduction);
		
		String resultsString = getName() + " results:";
		
		int nIterations = 0;
		
		// Cluster the attributes
		if (clusterAttributes && attributeArray.length > 1) {
			if (monitor != null) {
				monitor.setStatus("Clustering attributes");
			}
			resultsString = "\nAttributes: " + algo.cluster(kNumber,  nIterations,  true, getShortName());
		}
		
		// Cluster the nodes
		if (monitor != null) {
			monitor.setStatus("Clustering nodes");
		}
		resultsString = "\nNodes: " + algo.cluster(kNumber, nIterations, false, getShortName());
		if (monitor != null) {
			monitor.setStatus(resultsString);
		}
		
		// signal completion to listeners
		pcs.firePropertyChange(ClusterAlgorithm.CLUSTER_COMPUTED, null, this);
	}
}

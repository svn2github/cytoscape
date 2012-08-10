package clusterMaker.algorithms.attributeClusterers.pam;

import java.util.Arrays;

import javax.swing.JPanel;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterer;
import clusterMaker.algorithms.attributeClusterers.BaseMatrix;
import clusterMaker.ui.ClusterViz;
import clusterMaker.ui.KnnView;

/**
 * 
 * @author DavidS
 *
 */
public class PAMClusterer extends AbstractAttributeClusterer {
	
	public PAMClusterer() {
		logger = CyLogger.getLogger(PAMClusterer.class);
		initializeProperties();
	}
	
	@Override
	public void initializeProperties() {
		super.initializeProperties();
		
		addKTunables();
		
		// TODO Number of iterations
		
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
		updateKTunables(force);
		
		Tunable t;
		
		// TODO Number of iterations
		
		t = clusterProperties.get("dMetric");
		if ((t != null) && (t.valueChanged() || force)) {
			distanceMetric = BaseMatrix.distanceTypes[((Integer) t.getValue()).intValue()];
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
		return "PAM";
	}

	@Override
	public String getName() {
		return "Partitioning Around Medoids";
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
		
		// update estimate
		updateKEstimates();
		
		return clusterProperties.getTunablePanel();
	}

	@Override
	public void doCluster(TaskMonitor monitor) {
		this.monitor = monitor;
		
		if (debug) {
			logger.debug("Performing PAM cluster with k = " + kNumber + " using " + distanceMetric + " and attributes: " + dataAttributes);
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
		
		PAMCluster algo = new PAMCluster(attributeArray, distanceMetric, logger, monitor);
		algo.setCreateGroups(createGroups);
		algo.setIgnoreMissing(true);
		algo.setSelectedOnly(selectedOnly);
		algo.setDebug(debug);
		algo.setUseSilhouette(useSilhouette);
		algo.setKMax(kMax);
		algo.setClusterInterface(this);
		
		String resultsString = "PAM results:";
		
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

package clusterMaker.algorithms.attributeClusterers.hopach;

import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.Clusters;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.Matrix;

public class HopachPAMCluster extends AbstractAttributeClusterAlgorithm {
	
	public HopachPAMCluster(String weightAttributes[], DistanceMetric metric, CyLogger logger, TaskMonitor monitor) {
		this.logger = logger;
		this.weightAttributes = weightAttributes;
		this.metric = metric;
		this.monitor = monitor;
		resetAttributes();
	}
	
	@Override
	public int kcluster(int nClusters, int nIterations, Matrix matrix, DistanceMetric metric, int[] clusterId) {
		
		if (monitor != null) monitor.setPercentCompleted(0);
		
		HopachPAM hopachPam = new HopachPAM(matrix, metric);
		hopachPam.setParameters(9,  0,  true);
		Clusters c = hopachPam.run();
		
		// copy results into clusterId
		for (int i = 0; i < c.size(); ++i) {
			clusterId[i] = c.getClusterIndex(i);
		}
		
		return c.getNumberOfClusters();
	}
}

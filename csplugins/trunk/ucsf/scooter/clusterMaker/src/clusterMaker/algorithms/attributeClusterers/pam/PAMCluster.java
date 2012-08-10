package clusterMaker.algorithms.attributeClusterers.pam;

import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.Clusters;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.Matrix;

public class PAMCluster extends AbstractAttributeClusterAlgorithm {
	
	public PAMCluster(String weightAttributes[], DistanceMetric metric, CyLogger log, TaskMonitor monitor) {
		this.logger = log;
		this.weightAttributes = weightAttributes;
		this.metric = metric;
		this.monitor = monitor;
		resetAttributes();
	}
	
	@Override
	public int kcluster(int nClusters, int nIterations, Matrix matrix, DistanceMetric metric, int[] clusterId) {
		
		if (monitor != null) monitor.setPercentCompleted(0);
		
		PAM pam = new PAM(matrix, metric);
		Clusters c = pam.cluster(nClusters);
		
		// copy results into clusterId
		for (int i = 0; i < c.size(); ++i) {
			clusterId[i] = c.getClusterIndex(i);
		}
		
		return c.getNumberOfClusters();
	}
}

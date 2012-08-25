package clusterMaker.algorithms.attributeClusterers.hopach;

import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import clusterMaker.algorithms.attributeClusterers.AbstractAttributeClusterAlgorithm;
import clusterMaker.algorithms.attributeClusterers.Clusters;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.Matrix;
import clusterMaker.algorithms.attributeClusterers.hopach.types.SplitCost;
import clusterMaker.algorithms.attributeClusterers.pam.HopachablePAM;
import clusterMaker.algorithms.numeric.MeanSummarizer;
import clusterMaker.algorithms.numeric.MedianSummarizer;
import clusterMaker.algorithms.numeric.PrimitiveMeanSummarizer;
import clusterMaker.algorithms.numeric.PrimitiveMedianSummarizer;
import clusterMaker.algorithms.numeric.PrimitiveSummarizer;
import clusterMaker.algorithms.numeric.Summarizer;
import clusterMaker.algorithms.numeric.SummaryMethod;

public class HopachPAMCluster extends AbstractAttributeClusterAlgorithm {
	
	SplitCost splitCost;
	SummaryMethod summaryMethod;
	int maxLevel, K, L;
	double minCostReduction;
	boolean forceInitSplit;
	
	public HopachPAMCluster(String weightAttributes[], DistanceMetric metric, CyLogger logger, TaskMonitor monitor) {
		this.logger = logger;
		this.weightAttributes = weightAttributes;
		this.metric = metric;
		this.monitor = monitor;
		resetAttributes();
	}
	
	void setParameters(SplitCost splitCost, SummaryMethod summaryMethod, int maxLevel, int K, int L, boolean forceInitSplit, double minCostReduction) {
		this.splitCost = splitCost;
		this.summaryMethod = summaryMethod;
		this.maxLevel = maxLevel;
		this.K = K;
		this.L = L;
		this.forceInitSplit = forceInitSplit;
		this.minCostReduction = minCostReduction;
	}
	
	@Override
	public int kcluster(int nClusters, int nIterations, Matrix matrix, DistanceMetric metric, int[] clusterId) {
		
		if (monitor != null) monitor.setPercentCompleted(0);
		
		Summarizer summarizer;
		PrimitiveSummarizer psummarizer;
		switch (summaryMethod) {
		case MEDIAN:
			summarizer = new MedianSummarizer();
			psummarizer = new PrimitiveMedianSummarizer();
			break;
		case MEAN:
		default:
			summarizer = new MeanSummarizer();
			psummarizer = new PrimitiveMeanSummarizer();
			break;
		}
		
		HopachablePAM partitioner = new HopachablePAM(matrix, metric);
		partitioner.setParameters(K, L, splitCost, summarizer);
		
		HopachPAM hopachPam = new HopachPAM(partitioner);
		hopachPam.setParameters(maxLevel,  minCostReduction,  forceInitSplit, psummarizer);
		
		Clusters c = hopachPam.run();
		
		// copy results into clusterId
		for (int i = 0; i < c.size(); ++i) {
			clusterId[i] = c.getClusterIndex(i);
		}
		
		return c.getNumberOfClusters();
	}
}

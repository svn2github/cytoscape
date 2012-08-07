package clusterMaker.algorithms.attributeClusterers.pam;

import clusterMaker.algorithms.attributeClusterers.BaseMatrix;
import clusterMaker.algorithms.attributeClusterers.Clusters;
import clusterMaker.algorithms.attributeClusterers.DistanceMatrix;
import clusterMaker.algorithms.attributeClusterers.DistanceMetric;
import clusterMaker.algorithms.attributeClusterers.hopach.types.Hopachable;
import clusterMaker.algorithms.attributeClusterers.hopach.types.Subsegregatable;
import clusterMaker.algorithms.attributeClusterers.silhouette.DistanceCalculator;
import clusterMaker.algorithms.attributeClusterers.silhouette.MSplitSilhouetteCalculator;

/**
 * A PAM partitioner that implements Hopachable.
 * @author djh.shih
 *
 */
public class HopachablePAM extends PAM implements Hopachable, Subsegregatable {
	
	// maximum number of partitions to consider to splitting
	int maxK = 9;
	
	// maximum number of sub-partitions to consider for sub-splitting each partition
	int maxL = 9;
	
	public HopachablePAM(BaseMatrix data, DistanceMetric metric) {
		super(data, metric);
	}
	
	HopachablePAM(BaseMatrix data, DistanceMetric metric, DistanceMatrix distances, int[] idx) {
		super(data, metric, distances, idx);
	}
	
	public void setParameters(int maxK, int maxL) {
		this.maxK = maxK;
		this.maxL = maxL;
	}

	@Override
	public Hopachable subset(int[] index) {
		// shallow copy super class's data and use supplied index
		return new HopachablePAM(super.data, super.metric, super.distances, index);
	}

	@Override
	public double[][] segregations(Clusters clusters) {
		return DistanceCalculator.segregations(super.distances, clusters);
	}

	@Override
	public double[][] separations(Clusters clusters) {
		return DistanceCalculator.separations(super.distances, clusters.getClusterLabels());
	}
	
	@Override
	public Clusters split() {
		return MSplitSilhouetteCalculator.splitByMedianSplitSilhouette(this, maxK, maxL);
	}

	@Override
	public Clusters collapse(int i, int j, Clusters clusters) {
		// NB    In Pollard's implementation, the choice of the new medoid probably does not change downstream results...
		Clusters c = new Clusters(clusters);
		c.merge(i,  j);
		// set new cost
		c.setCost( MSplitSilhouetteCalculator.medianSplitSilhouette(this, c, maxL) );
		return c;
	}

	@Override
	public int[] order(Clusters clusters) {
		// TODO Auto-generated method stub
		// put elements of same cluster together, and order the elements within each cluster based on neighbouring clusters
		return null;
	}

}

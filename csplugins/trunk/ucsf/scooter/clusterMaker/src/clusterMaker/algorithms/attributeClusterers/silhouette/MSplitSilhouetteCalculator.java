package clusterMaker.algorithms.attributeClusterers.silhouette;

import java.util.ArrayList;

import clusterMaker.algorithms.attributeClusterers.Clusters;
import clusterMaker.algorithms.attributeClusterers.hopach.types.*;
import clusterMaker.algorithms.numeric.Numeric;


// FIXME eliminate code redundancy
//       implement a Silhouettes.getAverage( Summarizer )


/**
 * MSplitSilhouetteCalculator calculates the median/mean split silhouette.
 * @author djh.shih
 *
 */
public class MSplitSilhouetteCalculator {
	
	public static Clusters segregateByMeanSilhouette(Segregatable seg, int K) {
		Clusters split = null;
		
		int m = seg.size();
		
		// silhouette can only be calculated for 2 <= k <= m-1
		// bound K
		if ( K > m-1) {
			K = m - 1;
		}
		
		// maximize average silhouette
		double avgSil = -1.0;
		for (int k = 2; k <= K; ++k) {
			Clusters clusters = seg.cluster(k);
			Silhouettes sils = SilhouetteCalculator.silhouettes(seg.segregations(clusters), clusters);
			double t = sils.getMean();
			if (t > avgSil) {
				avgSil = t;
				split = clusters;
			}
		}
		
		if (split != null) {
			// replace classification cost by (1 - average silhouette)
			split.setCost(1 - avgSil);
		}
		
		return split;
	}
	
	public static Clusters segregateByMedianSilhouette(Segregatable seg, int K) {
		Clusters split = null;
		
		int m = seg.size();
		
		// silhouette can only be calculated for 2 <= k <= m-1
		// bound K
		if ( K > m-1) {
			K = m - 1;
		}
		
		// maximize average silhouette
		double avgSil = -1.0;
		for (int k = 2; k <= K; ++k) {
			Clusters clusters = seg.cluster(k);
			Silhouettes sils = SilhouetteCalculator.silhouettes(seg.segregations(clusters), clusters);
			double t = sils.getMedian();
			if (t > avgSil) {
				avgSil = t;
				split = clusters;
			}
		}
		
		if (split != null) {
			// replace classification cost by (1 - average silhouette)
			split.setCost(1 - avgSil);
		}
		
		return split;
	}
	
	public static ArrayList<Double> meanSilhouettes(Subsegregatable sseg, Clusters clusters, int L) {
		int K = clusters.getNumberOfClusters();
		ArrayList<Double> splitSilhouettes = new ArrayList<Double>();
		int[][] partitions = clusters.getPartitions();
		// calculate the split silhouette of each cluster
		for (int kk = 0; kk < K; ++K) {
			Clusters subclusters = segregateByMeanSilhouette(sseg.subset(partitions[kk]), L);
			if (subclusters != null) {
				// cluster could be split further into subclusters
				splitSilhouettes.add(1 - subclusters.getCost());
			}
		}
		return splitSilhouettes;
	}
	
	// TODO replace ArrayList with native array? (need handle to null values in downstream median calculation)
	public static ArrayList<Double> medianSilhouettes(Subsegregatable sseg, Clusters clusters, int L) {
		int K = clusters.getNumberOfClusters();
		ArrayList<Double> splitSilhouettes = new ArrayList<Double>();
		int[][] partitions = clusters.getPartitions();
		// calculate the split silhouette of each cluster
		for (int kk = 0; kk < K; ++kk) {
			Clusters subclusters = segregateByMedianSilhouette(sseg.subset(partitions[kk]), L);
			if (subclusters != null) {
				// cluster could be split further into subclusters
				splitSilhouettes.add(1 - subclusters.getCost());
			}
		}
		return splitSilhouettes;
	}
	
	public static double meanSplitSilhouette(Subsegregatable sseg, Clusters clusters, int L) {
		ArrayList<Double> splitSilhouettes = meanSilhouettes(sseg, clusters, L);
		if (splitSilhouettes.size() == 0) {
			// no cluster has a valid silhouette value (e.g. when all clusters have size < 3)
			return Double.POSITIVE_INFINITY;
		}
		return Numeric.mean(splitSilhouettes.toArray(new Double[splitSilhouettes.size()]));
		
	}
	
	public static double medianSplitSilhouette(Subsegregatable sseg, Clusters clusters, int L) {
		ArrayList<Double> splitSilhouettes = medianSilhouettes(sseg, clusters, L);
		if (splitSilhouettes.size() == 0) {
			// no cluster has a valid silhouette value (e.g. when all clusters have size < 3)
			return Double.POSITIVE_INFINITY;
		}
		return Numeric.median(splitSilhouettes.toArray(new Double[splitSilhouettes.size()]));
	}
	
	public static Clusters splitByMeanSplitSilhouette(Subsegregatable sseg, int K, int L) {
		return splitByMeanSplitSilhouette(sseg, K, L, false);
	}
	
	public static Clusters splitByMeanSplitSilhouette(Subsegregatable sseg, int K, int L, boolean forceSplit) {
		Clusters split = null;
		int m = sseg.size();
		
		// mean split silhouette can only be calculated for 2 <= k <= m-1
		// bound K
		if ( K > m / 3) {
			K = m / 3;
		}
		
		int minK = (forceSplit ? 2 : 1);
		
		// minimize the mean split silhouette
		double avgSplitSil = Double.POSITIVE_INFINITY;
		for (int k = minK; k <= K; k++) {
			Clusters clusters = sseg.cluster(k);
			double t = meanSplitSilhouette(sseg, clusters, L);
			if (t < avgSplitSil) {
				avgSplitSil = t;
				split = clusters;
			}
		}
		
		if (split == null) {
			split = sseg.cluster(minK);
		} 
		split.setCost(avgSplitSil);
		
		return split;
	}
	
	public static Clusters splitByMedianSplitSilhouette(Subsegregatable sseg, int K, int L) {
		return splitByMedianSplitSilhouette(sseg, K, L, false);
	}
	
	public static Clusters splitByMedianSplitSilhouette(Subsegregatable sseg, int K, int L, boolean forceSplit) {
		Clusters split = null;
		int m = sseg.size();
		
		// median split silhouette can only be calculated for 2 <= k <= m-1
		// bound K
		if ( K > m / 3) {
			K = m / 3;
		}
		
		int minK = (forceSplit ? 2 : 1);
		
		// minimize the median split silhouette
		double avgSplitSil = Double.POSITIVE_INFINITY;
		for (int k = minK; k <= K; k++) {
			Clusters clusters = sseg.cluster(k);
			double t = medianSplitSilhouette(sseg, clusters, L);
			if (t < avgSplitSil) {
				avgSplitSil = t;
				split = clusters;
			}
		}
		
		if (split == null) {
			split = sseg.cluster(minK);
		} 
		split.setCost(avgSplitSil);
		
		return split;
	}
	
}

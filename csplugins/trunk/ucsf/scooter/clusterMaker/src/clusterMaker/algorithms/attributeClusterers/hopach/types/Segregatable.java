package clusterMaker.algorithms.attributeClusterers.hopach.types;

import clusterMaker.algorithms.attributeClusterers.Clusters;

/**
 * Segregatable is an interface for constructing segregation matrices.
 * @author djh.shih
 *
 */
public interface Segregatable extends KClusterable {
	public double[][] segregations(Clusters clusters);
}
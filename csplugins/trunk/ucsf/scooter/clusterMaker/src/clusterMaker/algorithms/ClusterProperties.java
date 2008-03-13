// vim: set ts=2: */
package clusterMaker.algorithms;

import cytoscape.layout.LayoutProperties;

/**
 * The ClusterProperties class is a helper class to support the management
 * of settings and properties for cluster algorithms that implement
 * ClusterAlgorithm or extend AbstractClusterAlgorithm.  ClusterProperties objects
 * maintain a list of Tunables that are supplied by the individual
 * algorithms.  Each Tunable represents a value that should be loaded
 * from the Cytoscape properties file, and made available as a setting
 * in the ClusterSettingsDialog.  Tunables are added to the ClusterProperties
 * using the <tt>add</tt> method and are retrieved with the <tt>get</tt>
 * method.
 */
public class ClusterProperties extends LayoutProperties {

	/**
	 * Constructor.
	 *
	 * @param propertyPrefix String representing the prefix to be used
	 *                       when pulling properties from the property
	 *                       list.
	 */
	public ClusterProperties(String propertyPrefix) {
		super(propertyPrefix);
		setModuleType("clusterMaker");
	}
}

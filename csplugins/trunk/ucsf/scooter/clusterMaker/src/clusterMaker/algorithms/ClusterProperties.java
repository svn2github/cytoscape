// vim: set ts=2: */
package clusterMaker.algorithms;

import cytoscape.util.ModulePropertiesImpl;
import cytoscape.layout.Tunable;

import java.awt.GridLayout;
import javax.swing.JPanel;


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
public class ClusterProperties extends ModulePropertiesImpl {

	/**
	 * Constructor.
	 *
	 * @param propertyPrefix String representing the prefix to be used
	 *                       when pulling properties from the property
	 *                       list.
	 */
	public ClusterProperties(String propertyPrefix) {
		super(propertyPrefix, null);
	}


	/**
	 * This method returns a JPanel that represents the all of the Tunables
	 * associated with this LayoutProperties object.
	 *
	 * @return JPanel that contains all of the Tunable widgets
	 */
	public JPanel getTunablePanel() {
		JPanel tunablesPanel = new JPanel(new GridLayout(0, 1));

		for (Tunable tunable: tunablesList) {
			JPanel p = tunable.getPanel();

			if (p != null)
				tunablesPanel.add(p);
		}

		return tunablesPanel;
	}

	protected String getPrefix() {
		String prefix = "clusterMaker." + propertyPrefix;

		if (prefix.lastIndexOf('.') != prefix.length())
			prefix = prefix + ".";

		return prefix;
	}
}

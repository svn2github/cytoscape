// vim: set ts=2: */
package cytoscape.plugin.cheminfo.model;

import cytoscape.layout.LayoutProperties;

/**
 * The ChemInfoProperties class is a helper class to support the management
 * of settings and properties for the ChemInfo plugin. ChemInfoProperties objects
 * maintain a list of Tunables that can be saved to the Cytoscape properties.
 */
public class ChemInfoProperties extends LayoutProperties {

	/**
	 * Constructor.
	 *
	 * @param propertyPrefix String representing the prefix to be used
	 *                       when pulling properties from the property
	 *                       list.
	 */
	public ChemInfoProperties(String propertyPrefix) {
		super(propertyPrefix);
		setModuleType("plugin");
	}
}

package org.cytoscape.BipartiteVisualiserPlugin;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

/**
 * 
 */
public class BipartiteVisualiserPlugin extends CytoscapePlugin {
	/**
	 * Instantiate a new listener to create context menu for each network
	 */
	public BipartiteVisualiserPlugin() {
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new BipartiteLayoutNetworkListener());
	}
}

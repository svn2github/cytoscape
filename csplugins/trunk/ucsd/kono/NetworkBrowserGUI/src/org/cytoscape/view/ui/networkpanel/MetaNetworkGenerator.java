package org.cytoscape.view.ui.networkpanel;

import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.groups.CyGroup;

public interface MetaNetworkGenerator {
	
	/**
	 * Create a network to represent a relationship between modules.
	 * 
	 * @param parent
	 * @param groups
	 * @return
	 */
	public CyNetwork generateMetaNetwrok(String metaNetName, CyNetwork parent, Set<CyGroup> groups);

}

package org.cytoscape.view.ui.networkpanel;

import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.groups.CyGroup;

public interface MetaNetworkGenerator {
	
	public static final String NETWORK_NODE = "NETWORK NODE";
	public static final String MODULE_NODE = "MODULE NODE";
	
	public static final String NODE_TYPE = "modules.node_type";
	public static final String EXEC_COUNTER = "modules.run";
	
	/**
	 * Create a network to represent a relationship between modules.
	 * 
	 * @param parent
	 * @param groups
	 * @return
	 */
	public CyNetwork generateMetaNetwork(String metaNetName, CyNetwork parent, Set<CyGroup> groups);
	
	public CyNetwork getMetaNetwork();

}

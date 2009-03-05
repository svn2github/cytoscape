package cytoscape.util;

import java.awt.Component;

import org.cytoscape.model.CyNetwork;

import cytoscape.CyNetworkManager;

public interface CyNetworkNaming {

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parentNetwork DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract String getSuggestedSubnetworkTitle(CyNetwork parentNetwork,
			CyNetworkManager netmgr);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param desiredTitle DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract String getSuggestedNetworkTitle(String desiredTitle,
			CyNetworkManager netmgr);

	/**
	 * This will prompt the user to edit the title of a given CyNetork,
	 * and after ensuring that the network title is not already in use,
	 * this will assign that title to the given CyNetwork
	 * @para network is the CyNetwork whose title is to be changed
	 */
	public abstract void editNetworkTitle(CyNetwork network, Component parent,
			CyNetworkManager netmgr);

}
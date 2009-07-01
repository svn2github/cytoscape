package org.cytoscape.session;

import org.cytoscape.model.CyNetwork;

public interface CyNetworkNaming {

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parentNetwork DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	String getSuggestedSubnetworkTitle(CyNetwork parentNetwork);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param desiredTitle DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	String getSuggestedNetworkTitle(String desiredTitle);
}

/*
 File: CyApplicationManager.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.session;


import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngine;


/**
 * Basic access to current and/or currently selected networks, views and rendering engines in an
 * instance of Cytoscape.
 */
public interface CyApplicationManager {
	/**
	 * Provides access to the current network.
	 * 
	 * @return the current network or null if there is no current network
	 */
	public CyNetwork getCurrentNetwork();

	/**
	 * Sets the current network to the one with the provided network SUID.
	 * 
	 * @param network_id  must be the SUID of a network
	 */
	public void setCurrentNetwork(final long network_id);

	/**
	 * Provides access to the current network view.
	 * 
	 * @return the current network view of null if no network is currently being visualised
	 */
	public CyNetworkView getCurrentNetworkView();

	/**
	 * DOCUMENT ME!
	 * 
	 * @param view_id
	 *            DOCUMENT ME!
	 */
	public void setCurrentNetworkView(final long view_id);

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<CyNetwork> getSelectedNetworks();

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public List<CyNetworkView> getSelectedNetworkViews();

	/**
	 * DOCUMENT ME!
	 * 
	 * @param modelIDs
	 *            DOCUMENT ME!
	 */
	public void setSelectedNetworkViews(final List<Long> modelIDs);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param ids
	 *            DOCUMENT ME!
	 */
	public void setSelectedNetworks(final List<Long> ids);
	
	public RenderingEngine<CyNetwork> getCurrentRenderingEngine();

	public void setCurrentRenderingEngine(final RenderingEngine<CyNetwork> engine);
}

/*
 File: CyNetworkManager.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

import org.cytoscape.model.CyNetwork;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.NetworkRenderer;

import java.util.List;
import java.util.Set;


/**
 * Basic access to networks and view in an instance of Cytoscape.
 */
public interface CyNetworkManager {
	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CyNetwork getCurrentNetwork();

	/**
	 * DOCUMENT ME!
	 *
	 * @param network_id
	 *            DOCUMENT ME!
	 */
	public void setCurrentNetwork(final long network_id);

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
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
	public Set<CyNetwork> getNetworkSet();

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Set<CyNetworkView> getNetworkViewSet();

	/**
	 * DOCUMENT ME!
	 *
	 * @param id
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CyNetwork getNetwork(long id);

	/**
	 * DOCUMENT ME!
	 *
	 * @param network_id
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public CyNetworkView getNetworkView(long network_id);

	/**
	 * DOCUMENT ME!
	 *
	 * @param network_id
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean networkExists(long network_id);

	/**
	 * DOCUMENT ME!
	 *
	 * @param network_id
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean viewExists(long network_id);

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
	 * @param viewIDs
	 *            DOCUMENT ME!
	 */
	public void setSelectedNetworkViews(final List<Long> viewIDs);

	/**
	 * DOCUMENT ME!
	 *
	 * @param ids
	 *            DOCUMENT ME!
	 */
	public void setSelectedNetworks(final List<Long> ids);

	/**
	 * DOCUMENT ME!
	 *
	 * @param network
	 *            DOCUMENT ME!
	 */
	public void destroyNetwork(CyNetwork network);

	/**
	 * DOCUMENT ME!
	 *
	 * @param view
	 *            DOCUMENT ME!
	 */
	public void destroyNetworkView(CyNetworkView view);

	/**
	 * DOCUMENT ME!
	 *
	 * @param network
	 *            DOCUMENT ME!
	 */
	public void addNetwork(CyNetwork network);

	/**
	 * DOCUMENT ME!
	 *
	 * @param view
	 *            DOCUMENT ME!
	 */
	public void addNetworkView(CyNetworkView view);

	public NetworkRenderer getCurrentPresentation();
	public void setCurrentPresentation(NetworkRenderer renderer);
}

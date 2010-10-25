/*
 File: NetworkManager.java

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

package org.cytoscape.session.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkAddedEvent;
import org.cytoscape.session.events.NetworkDestroyedEvent;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.NetworkViewDestroyedEvent;
import org.cytoscape.session.events.SetCurrentNetworkEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetSelectedNetworkViewsEvent;
import org.cytoscape.session.events.SetSelectedNetworksEvent;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.RenderingEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of CyNetworkManager.
 * 
 *
 */
public class CyNetworkManagerImpl implements CyNetworkManager {
	
	private static final Logger logger = LoggerFactory.getLogger(CyNetworkManagerImpl.class);

	private final Map<Long, CyNetworkView> networkViewMap;
	private final Map<Long, CyNetwork> networkMap;

	private final List<CyNetworkView> selectedNetworkViews;
	private final List<CyNetwork> selectedNetworks;

	private final CyEventHelper cyEventHelper;

	// Trackers for current network object
	private CyNetwork currentNetwork;
	private CyNetworkView currentNetworkView;
	private RenderingEngine<CyNetwork> currentRenderer;

	
	/**
	 * 
	 * @param cyEventHelper
	 */
	public CyNetworkManagerImpl(final CyEventHelper cyEventHelper) {
		networkMap = new HashMap<Long, CyNetwork>();
		networkViewMap = new HashMap<Long, CyNetworkView>();
		selectedNetworkViews = new LinkedList<CyNetworkView>();
		selectedNetworks = new LinkedList<CyNetwork>();
		
		currentNetwork = null;
		currentNetworkView = null;
		this.currentRenderer = null;
		
		this.cyEventHelper = cyEventHelper;
	}

	public synchronized CyNetwork getCurrentNetwork() {
		return currentNetwork;
	}

	public void setCurrentNetwork(final long networkId) {
		synchronized (this) {
			if (!networkMap.containsKey(networkId))
				throw new IllegalArgumentException(
						"Network is not registered in this NetworkManager: ID = " + networkId);

			logger.info("Set current network called.  Current network ID = " + networkId);
			currentNetwork = networkMap.get(networkId);

			// reset selected networks
			selectedNetworks.clear();
			selectedNetworks.add(currentNetwork);
		}

		logger.debug("Current network is set.  Firing SetCurrentNetworkEvent: Network ID = "
				+ networkId);
		cyEventHelper.fireSynchronousEvent(new SetCurrentNetworkEvent(CyNetworkManagerImpl.this,currentNetwork)); 
	}

	public synchronized Set<CyNetwork> getNetworkSet() {
		return new HashSet<CyNetwork>(networkMap.values());
	}

	public synchronized Set<CyNetworkView> getNetworkViewSet() {
		return new HashSet<CyNetworkView>(networkViewMap.values());
	}

	public synchronized CyNetwork getNetwork(long id) {
		return networkMap.get(id);
	}

	public synchronized CyNetworkView getNetworkView(long network_id) {
		return networkViewMap.get(network_id);
	}

	public synchronized boolean networkExists(long network_id) {
		return networkMap.containsKey(network_id);
	}

	public synchronized boolean viewExists(long network_id) {
		return networkViewMap.containsKey(network_id);
	}

	public synchronized CyNetworkView getCurrentNetworkView() {
		return currentNetworkView;
	}

	
	// FIXME: THIS IS WRONG!  we can have multiple view model, so this parameter should be model SUID.
	public void setCurrentNetworkView(final long modelID) {
		synchronized (this) {
			if (!networkMap.containsKey(modelID)
					|| !networkViewMap.containsKey(modelID))
				throw new IllegalArgumentException(
						"network view is not recognized by this NetworkManager");

			logger.info("Set current network view called: View ID = "
					+ networkViewMap.get(modelID).getSUID());

			setCurrentNetwork(modelID);

			currentNetworkView = networkViewMap.get(modelID);

			// reset selected network views
			selectedNetworkViews.clear();
			selectedNetworkViews.add(currentNetworkView);
		}

		logger.debug("Current network view i set.  Firing SetCurrentNetworkViewEvent: View ID = "
				+ networkViewMap.get(modelID).getSUID());
		cyEventHelper.fireSynchronousEvent(new SetCurrentNetworkViewEvent(CyNetworkManagerImpl.this,currentNetworkView));
	}

	public synchronized List<CyNetworkView> getSelectedNetworkViews() {
		return new ArrayList<CyNetworkView>(selectedNetworkViews);
	}

	public void setSelectedNetworkViews(final List<Long> viewIDs) {

		if (viewIDs == null)
			return;

		synchronized (this) {

			selectedNetworkViews.clear();

			for (Long id : viewIDs) {
				CyNetworkView nview = networkViewMap.get(id);

				if (nview != null)
					selectedNetworkViews.add(nview);
			}

			CyNetworkView cv = getCurrentNetworkView();

			if (!selectedNetworkViews.contains(cv)) {
				selectedNetworkViews.add(cv);
			}
		}

		cyEventHelper.fireSynchronousEvent(new SetSelectedNetworkViewsEvent(CyNetworkManagerImpl.this, new ArrayList<CyNetworkView>(selectedNetworkViews)));
	}

	public synchronized List<CyNetwork> getSelectedNetworks() {
		return new ArrayList<CyNetwork>(selectedNetworks);
	}

	public void setSelectedNetworks(final List<Long> ids) {

		if (ids == null)
			return;

		synchronized (this) {
			selectedNetworks.clear();

			for (Long id : ids) {
				CyNetwork n = networkMap.get(id);

				if (n != null)
					selectedNetworks.add(n);
			}

			CyNetwork cn = currentNetwork;

			if (!selectedNetworks.contains(cn))
				selectedNetworks.add(cn);
		}

		cyEventHelper.fireSynchronousEvent(new SetSelectedNetworksEvent(CyNetworkManagerImpl.this, new ArrayList<CyNetwork>(selectedNetworks)));
	}

	// TODO
	// Does this need to distinguish between root networks and subnetworks?
	public void destroyNetwork(CyNetwork network) {
		if (network == null)
			throw new NullPointerException("network is null");

		final Long networkId = network.getSUID();

		synchronized (this) {
			if (!networkMap.containsKey(networkId))
				throw new IllegalArgumentException(
						"network is not recognized by this NetworkManager");

			// TODO firing an event from within a lock!!!!
			final CyNetwork toDestroy = network;
			cyEventHelper.fireSynchronousEvent(new NetworkAboutToBeDestroyedEvent(CyNetworkManagerImpl.this, toDestroy));
			selectedNetworks.remove(network);

			for (CyNode n : network.getNodeList())
				n.getCyRow().set("selected", false);
			for (CyEdge e : network.getEdgeList())
				e.getCyRow().set("selected", false);

			networkMap.remove(networkId);

			if (network == currentNetwork) {
				if (networkMap.size() <= 0) {
					currentNetwork = null;
				} else {
					// randomly pick a network to become the current network
					for (CyNetwork net : networkMap.values()) {
						currentNetwork = net;
						break;
					}
				}
			}

			if (viewExists(networkId))
				destroyNetworkView(networkViewMap.get(networkId));

			network = null;
		}

		// lets everyone know that *A* network is gone
		cyEventHelper.fireSynchronousEvent(new NetworkDestroyedEvent( CyNetworkManagerImpl.this ));
	}

	public void destroyNetworkView(CyNetworkView view) {

		if (view == null)
			throw new NullPointerException("view is null");

		final Long viewID = view.getModel().getSUID();

		synchronized (this) {
			if (!networkViewMap.containsKey(viewID))
				throw new IllegalArgumentException(
						"network view is not recognized by this NetworkManager");

			// TODO firing an event from within a lock!!!!
			final CyNetworkView toDestroy = view;
			cyEventHelper.fireSynchronousEvent(new NetworkViewAboutToBeDestroyedEvent(CyNetworkManagerImpl.this, toDestroy));

			selectedNetworkViews.remove(view);

			if (view.equals(currentNetworkView)) {
				if (networkViewMap.size() <= 0 || currentNetwork == null)
					currentNetworkView = null;
				else {
					// depending on which randomly chosen currentNetwork we get,
					// we may or may not have a view for it.
					CyNetworkView newCurr = networkViewMap.get(currentNetwork
							.getSUID());

					if (newCurr != null)
						currentNetworkView = newCurr;
					else
						currentNetworkView = null;
				}
			}

			networkViewMap.remove(viewID);
			view = null;
		}

		cyEventHelper.fireSynchronousEvent(new NetworkViewDestroyedEvent( CyNetworkManagerImpl.this ));
	}

	
	public void addNetwork(final CyNetwork network) {
		if (network == null)
			throw new NullPointerException("Network is null");

		synchronized (this) {
			logger.debug("Adding new Network Model: Model ID = " + network.getSUID());
			networkMap.put(network.getSUID(), network);
		}

		cyEventHelper.fireSynchronousEvent(new NetworkAddedEvent(CyNetworkManagerImpl.this, network));
	}

	
	public void addNetworkView(final CyNetworkView view) {
		if (view == null)
			throw new NullPointerException("CyNetworkView is null");

		CyNetwork network = view.getModel();
		long networkId = network.getSUID();

		synchronized (this) {
			if (!networkExists(networkId))
				addNetwork(network);
			logger.debug("Adding new Network View Model: Model ID = " + networkId);
			networkViewMap.put(networkId, view);
		}

		logger.debug("Firing event: NetworkViewAddedEvent");
		cyEventHelper.fireSynchronousEvent(new NetworkViewAddedEvent( CyNetworkManagerImpl.this, view));
		logger.debug("Done event: NetworkViewAddedEvent");
	}

	
	public RenderingEngine<CyNetwork> getCurrentRenderingEngine() {
		return currentRenderer;
	}

	
	public void setCurrentRenderingEngine(RenderingEngine<CyNetwork> engine) {
		this.currentRenderer = engine;		
	}
}

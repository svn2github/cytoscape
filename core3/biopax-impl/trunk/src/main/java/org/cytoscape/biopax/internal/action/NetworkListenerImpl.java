// $Id: NetworkListener.java,v 1.13 2006/06/15 22:02:52 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.biopax.internal.action;

import java.beans.PropertyChangeEvent;

import org.cytoscape.biopax.NetworkListener;
import org.cytoscape.biopax.internal.view.BioPaxDetailsPanel;
import org.cytoscape.biopax.util.BioPaxUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;


/**
 * Listens for Network Events, and takes appropriate Actions.
 * May be subclassed.
 *
 * @author Ethan Cerami / Benjamin Gross / Igor Rodchenkov.
 */
public class NetworkListenerImpl implements NetworkListener {
	private final BioPaxDetailsPanel bpPanel;
	private CyNetworkViewManager viewManager;

	/**
	 * Constructor.
	 *
	 * @param bpPanel BioPaxDetails Panel Object.
	 */
	public NetworkListenerImpl(BioPaxDetailsPanel bpPanel, CyNetworkViewManager viewManager) {
		this.bpPanel = bpPanel;
		this.viewManager = viewManager;
		
		// to catch network creation / destruction events
//		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

		// to catch network selection / focus events
//		Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport()
//		         .addPropertyChangeListener(this);
	}

	/**
	 * Registers a newly created Network.
	 *
	 * @param cyNetwork Object.
	 */
	@Override
	public void registerNetwork(CyNetwork cyNetwork) {
		registerNodeSelectionEvents(cyNetwork);
	}

	/**
	 * Register to Listen for Node Selection Events
	 *
	 * @param cyNetwork CyNetwork Object.
	 */
	private void registerNodeSelectionEvents(CyNetwork cyNetwork) {
		// TODO: Port this later
//		SelectFilter flagFilter = cyNetwork.getSelectFilter();
//		flagFilter.addSelectEventListener(new DisplayBioPaxDetails(bpPanel));
		bpPanel.resetText();
	}

	/**
	 * Property change listener - to get network/network view destroy events.
	 *
	 * @param event PropertyChangeEvent
	 */
	// TODO: Port/detangle this
//	public void propertyChange(PropertyChangeEvent event) {
//		boolean relevantEventFlag = false;
//
//		// network destroyed, we may have to remove it from our list
//		if (event.getPropertyName() == Cytoscape.NETWORK_CREATED) {
//			networkCreatedEvent(event);
//		} else if (event.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
//			networkDestroyed((String) event.getNewValue());
//			relevantEventFlag = true;
//		} else if (event.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED) {
//			relevantEventFlag = true;
//		} else if (event.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
//			networkFocusEvent(event, false);
//		} else if (event.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {
//			networkFocusEvent(event, false);
//		} else if (event.getPropertyName() == Cytoscape.SESSION_LOADED) {
//            CySessionUtil.setSessionReadingInProgress(false);
//			networkCreatedEvent(event);
//			networkFocusEvent(event, true);
//        } else if (event.getPropertyName().equals(Integer.toString(Cytoscape.SESSION_OPENED))) {
//            CySessionUtil.setSessionReadingInProgress(true);
//        }
//
//		if (relevantEventFlag && !networkViewsRemain()) {
//			onZeroNetworkViewsRemain();
//		}
//	}

	/**
	 * Network Created Event
	 */
	private void networkCreatedEvent(PropertyChangeEvent event) {
		// get the network
		/* (why was that?)
		CyNetwork cyNetwork = null;
		Object newValue = event.getNewValue();

		if (event.getPropertyName() == Cytoscape.SESSION_LOADED) {
			cyNetwork = Cytoscape.getCurrentNetwork();
		} else if (newValue instanceof CyNetwork) {
			cyNetwork = (CyNetwork) newValue;
		} else if (newValue instanceof String) {
			String networkID = (String) newValue;
			cyNetwork = Cytoscape.getNetwork(networkID);
		}
		
		if(cyNetwork != null && BioPaxUtil.isBioPAXNetwork(cyNetwork)) {
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_CREATED, null, cyNetwork);
		}
		*/
		
		bpPanel.resetText();
		
	}

	/**
	 * Network Focus Event.
	 */
	private void networkFocusEvent(PropertyChangeEvent event, boolean sessionLoaded) {
//		// get network id
//		String networkId = null;
//		CyNetwork cyNetwork = null;
//		Object newValue = event.getNewValue();
//
//		if (event.getPropertyName() == Cytoscape.SESSION_LOADED) {
//			cyNetwork = Cytoscape.getCurrentNetwork();
//			networkId = cyNetwork.getIdentifier();
//		} else if (newValue instanceof CyNetwork) {
//			cyNetwork = (CyNetwork) newValue;
//			networkId = cyNetwork.getIdentifier();
//		} else if (newValue instanceof String) {
//			networkId = (String) newValue;
//			cyNetwork = Cytoscape.getNetwork(networkId);
//		}
//
//		if (networkId != null) {
//			// update bpPanel accordingly
//            if (!sessionLoaded) {
//            	if (BioPaxUtil.getNetworkModelMap().containsKey(networkId)) {
//                    bpPanel.resetText();
//                } else {
//                    bpPanel.resetText("Node details are not provided for"
//                                      + " the currently selected network.");
//                }
//            }
//            
//            // due to quirky-ness in event model, we could get here without registering network
//            // check if this is a biopax network
//            if (BioPaxUtil.isBioPAXNetwork(cyNetwork) 
//            		&& !BioPaxUtil.getNetworkModelMap().containsKey(networkId)) {
//            	registerNetwork(cyNetwork);
//            }
//        }
	}

	/*
	* Removes CyNetwork from our list if it has just been destroyed.
	*
	* @param networkID the ID of the CyNetwork just destroyed.
	*/
	private void networkDestroyed(String networkID) {
		// destroy the corresponding model
		BioPaxUtil.removeNetworkModel(networkID);
	}

	/*
	* Determines if any network views we have created remains.
	*
	 * @return boolean if any network views that we have created remain.
	*/
	private boolean networkViewsRemain() {
		// interate through our network list checking if their views exists
		for (Long networkId : BioPaxUtil.getNetworkModelMap().keySet()) {
			// get the network view via id
			CyNetworkView cyNetworkView = viewManager.getNetworkView(networkId);
			if (cyNetworkView != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Event:  No Registered Network Views Remain.
	 * May be subclassed.
	 */
	protected void onZeroNetworkViewsRemain() {
		bpPanel.resetText("BioPAX Details not available.  Please load"
		                  + " a BioPAX file to proceed.");
	}

}

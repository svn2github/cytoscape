/* vim: set ts=2:

  File: ClusterSettingsDialog.java

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
  Dout of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package clusterMaker.ui;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

import giny.view.GraphViewChangeListener;
import giny.view.GraphViewChangeEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;

public class ClusterMakerLinkNetworks extends JCheckBoxMenuItem implements ActionListener,GraphViewChangeListener,PropertyChangeListener {
	private boolean state = false;
	private Map<String, CyNetwork> networkMap = null;

	public ClusterMakerLinkNetworks() {
		super("Link network selection");
		setEnabled(false);
		setState(state);
		addActionListener(this);

		updateNetworkMap();

		// Catch new network loaded and change events so we can maintain our list of networks to
		// link
		Cytoscape.getPropertyChangeSupport()
        .addPropertyChangeListener( Cytoscape.NETWORK_LOADED, this );
		Cytoscape.getPropertyChangeSupport()
        .addPropertyChangeListener( Cytoscape.NETWORK_DESTROYED, this );
		Cytoscape.getPropertyChangeSupport()
        .addPropertyChangeListener( Cytoscape.NETWORK_CREATED, this );
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
                           .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
	}

	public void actionPerformed(ActionEvent e) {
		if (state) {
			state = false;
			// Remove ourselves from all networks
			for (CyNetwork network: networkMap.values()) {
				CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
				if (view != null && view != Cytoscape.getNullNetworkView())
					view.removeGraphViewChangeListener(this);
			}
		} else {
			state = true;
			// Add ourselves to all networks
			for (CyNetwork network: networkMap.values()) {
				CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
				if (view != null && view != Cytoscape.getNullNetworkView())
					view.addGraphViewChangeListener(this);
			}
		}
		setState(state);
	}

	public void graphViewChanged(GraphViewChangeEvent event) {
		CyNetworkView myView = (CyNetworkView)event.getSource();
		CyNetwork myNetwork = myView.getNetwork();

		if (event.getType() == GraphViewChangeEvent.NODES_UNSELECTED_TYPE || 
		    event.getType() == GraphViewChangeEvent.NODES_SELECTED_TYPE) {
			// Get the currently selected nodes
			Set<CyNode> nodes = (Set<CyNode>)myNetwork.getSelectedNodes();
			// Select them in every network
			selectNodes(nodes, myNetwork);
		} else if ((event.getType() == GraphViewChangeEvent.EDGES_UNSELECTED_TYPE ||
		            event.getType() == GraphViewChangeEvent.EDGES_SELECTED_TYPE)) {
			// Get the currently selected edges
			Set<CyEdge> edges = (Set<CyEdge>)myNetwork.getSelectedEdges();
			// Select them in every network
			selectEdges(edges, myNetwork);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName() == Cytoscape.NETWORK_LOADED || 
		     evt.getPropertyName() == Cytoscape.NETWORK_CREATED ) {
			if (evt.getNewValue() == null) {
				// Session loaded
				updateNetworkMap();
			} else {
				String networkName = evt.getNewValue().toString();
				// Get the network 
				CyNetwork network = Cytoscape.getNetwork(networkName);
				if (network != null && network != Cytoscape.getNullNetwork()) {
			 		networkMap.put(networkName, network);
				}
			}
		} else if ( evt.getPropertyName() == Cytoscape.NETWORK_DESTROYED ) {
			String networkName = evt.getNewValue().toString();
			if (networkMap.containsKey(networkName)) {
				networkMap.remove(networkName);
			}
		} else if ( evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			CyNetworkView view = (CyNetworkView)evt.getNewValue();
			if (!networkMap.containsKey(view.getIdentifier()))
				networkMap.put(view.getIdentifier(), view.getNetwork());
			if (state) 
				view.addGraphViewChangeListener(this);
		}
	}

	private void selectNodes(Set<CyNode> nodes, CyNetwork selectedNetwork) {
		for (CyNetwork network: networkMap.values()) {
			if (network != selectedNetwork) {
				CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
				view.removeGraphViewChangeListener(this);
				network.unselectAllNodes();
				network.setSelectedNodeState(nodes, true);
				view.updateView();
				view.addGraphViewChangeListener(this);
			}
		}
	}

	private void selectEdges(Set<CyEdge> edges, CyNetwork selectedNetwork) {
		for (CyNetwork network: networkMap.values()) {
			if (network != selectedNetwork) {
				CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
				view.removeGraphViewChangeListener(this);
				network.unselectAllEdges();
				network.setSelectedEdgeState(edges, true);
				view.updateView();
				view.addGraphViewChangeListener(this);
			}
		}
	}

	private void updateNetworkMap() {
		networkMap = new HashMap<String, CyNetwork>();

		// Find all of the current open networks and add them to our map
		Set<CyNetwork> netSet = Cytoscape.getNetworkSet();
		for (CyNetwork network: netSet) {
			String id = network.getIdentifier();
			networkMap.put(id, network);
			if (state && Cytoscape.getNetworkView(id) != Cytoscape.getNullNetworkView())
			 	Cytoscape.getNetworkView(id).addGraphViewChangeListener(this);
		}

		if (networkMap.size() > 0) 
			setEnabled(true);
		else
			setEnabled(false);
	}


}

package org.cytoscape.view.ui.networkpanel.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.cytoscape.view.ui.networkpanel.NetworkDagManager;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

public class NetworkDagManagerImpl implements PropertyChangeListener,
		NetworkDagManager {

	private DefaultTreeTableModel tableModel;

	// Inject model
	public NetworkDagManagerImpl() {
		tableModel = new DefaultTreeTableModel();
	}

	public List<CyNetwork> getRoots() {
		// TODO Auto-generated method stub
		return null;
	}

	private void addNetwork(String id, String parentID) {
		
		
		if(parentID == null) {
			
		}

	}

	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == Cytoscape.NETWORK_CREATED) {
			addNetwork((String) e.getNewValue(), (String) e.getOldValue());
		} else if (e.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
			// removeNetwork((String) e.getNewValue());
		} else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {
			// if ( e.getSource() != this )
			// focusNetworkNode((String) e.getNewValue());
		} else if (e.getPropertyName() == Cytoscape.NETWORK_TITLE_MODIFIED) {
			// CyNetworkTitleChange cyNetworkTitleChange =
			// (CyNetworkTitleChange) e.getNewValue();
			// String newID = cyNetworkTitleChange.getNetworkIdentifier();
			// //String newTitle = cyNetworkTitleChange.getNetworkTitle();
			// CyNetwork _network = Cytoscape.getNetwork(newID);
			// // Network "0" is the default and does not appear in the netowrk
			// panel
			// if (_network != null && !_network.getIdentifier().equals("0"))
			// updateTitle(_network);
		}
	}

}

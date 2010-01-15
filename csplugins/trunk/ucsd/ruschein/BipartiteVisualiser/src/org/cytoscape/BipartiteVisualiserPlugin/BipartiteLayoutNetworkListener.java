package org.cytoscape.BipartiteVisualiserPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

public class BipartiteLayoutNetworkListener implements PropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(event
				.getPropertyName())) {

			final BipartiteLayoutContextMenuListener edge_menu_listener = new BipartiteLayoutContextMenuListener();
			Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(
					edge_menu_listener);
		}

	}

}

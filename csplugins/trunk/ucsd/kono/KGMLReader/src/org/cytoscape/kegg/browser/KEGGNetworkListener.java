package org.cytoscape.kegg.browser;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

public class KEGGNetworkListener implements PropertyChangeListener {

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(event
				.getPropertyName())) {

			final KEGGNodeContextMenuListener nodeMenuListener = new KEGGNodeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(
					nodeMenuListener);
			
			System.out.println("-----Conetxt added: " + event.getNewValue());
		}

	}

}
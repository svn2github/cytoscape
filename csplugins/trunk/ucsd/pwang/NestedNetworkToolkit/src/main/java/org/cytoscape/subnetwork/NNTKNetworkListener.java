package org.cytoscape.subnetwork; 

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.CyNetwork;
import cytoscape.CyNetworkTitleChange;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

public class NNTKNetworkListener implements PropertyChangeListener
{
	public void propertyChange(PropertyChangeEvent event) {

		if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(event.getPropertyName()))
        {
                final CyNetworkView view = (CyNetworkView) event.getNewValue();

                final NNTKNodeContextMenuListener nodeMenuListener = new NNTKNodeContextMenuListener(view);
                view.addNodeContextMenuListener(nodeMenuListener);
        }
	}
}

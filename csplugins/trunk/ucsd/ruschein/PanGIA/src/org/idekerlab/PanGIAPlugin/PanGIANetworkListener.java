package org.idekerlab.PanGIAPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

public class PanGIANetworkListener implements PropertyChangeListener
{
	public void propertyChange(PropertyChangeEvent event) {
        if (CytoscapeDesktop.NETWORK_VIEW_CREATED.equals(event.getPropertyName())) {
                
                final CyNetworkView view = (CyNetworkView) event.getNewValue();

                // Node right-click menu
                final PanGIANodeContextMenuListener nodeMenuListener = new PanGIANodeContextMenuListener(view);
                Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(
                                nodeMenuListener);
                
                // Edge right-click menu
                final PanGIAEdgeContextMenuListener edgeMenuListener = new PanGIAEdgeContextMenuListener(view);
                Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(
                                edgeMenuListener);
        }
}

}

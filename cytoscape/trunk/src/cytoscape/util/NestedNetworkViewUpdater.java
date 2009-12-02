package cytoscape.util;

import giny.view.NodeView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.DNodeView;


/** 
 * This class manages images that represent nested networks.  This "management" includes creation, 
 * updating and destruction of such images as well as updating network views when any of their 
 * nodes nested networks have changed.
 * 
 * @since Cytoscape 2.7.0
 * @author kono, ruschein
 */
public class NestedNetworkViewUpdater implements PropertyChangeListener {
	
	private static final String NESTED_NETWORK_VS_NAME = "Nested Network Style";

	public NestedNetworkViewUpdater() {
		//Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		System.out.println("------- Got Signal --------> " + evt.getPropertyName() + ", " + evt.getSource());
		if (evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED) ||
				evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
			
			final boolean created = evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED);
			// "New value" always contains newly created/destroyed CyNetworkView.
			final CyNetworkView view = (CyNetworkView) evt.getNewValue();
			final List<String> parents = Cytoscape.getNetworkAttributes().getListAttribute(view.getNetwork().getIdentifier(), CyNode.PARENT_NODES_ATTR);
			if (parents == null || parents.isEmpty()) {
				return;  // Not a nested network.
			}
			
			// First, grab all the available network views.
			final Collection<CyNetworkView> networkViews = Cytoscape.getNetworkViewMap().values();
			for (final CyNetworkView networkView: networkViews) {
				for (final String parentNode: parents) {
					// If this view contains a parentNode, then update its nested network view.
					//TODO: When we load saved session with nested network, the following can be null.  WHY?
					final CyNode node = Cytoscape.getCyNode(parentNode);
					final NodeView nodeView = networkView.getNodeView(node);
					if (nodeView != null && node != null && node.getNestedNetwork() != null) {
						System.out.println("---------> Nested = " + node.getNestedNetwork());
						((DNodeView)nodeView).setNestedNetworkView(
							created ? (DGraphView) Cytoscape.getNetworkView(((CyNetwork)nodeView.getNode().getNestedNetwork()).getIdentifier())
									: null);
					}
				}
			}
		}
	}
}

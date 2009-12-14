package cytoscape.util;

import giny.view.NodeView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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
	
	private boolean ignoreNextEvent = false;

	public NestedNetworkViewUpdater() {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}

	public void propertyChange(final PropertyChangeEvent evt) {	
		if (evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED) ||
				evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
			if (ignoreNextEvent) {
				ignoreNextEvent = false;
				return;
			} else {
				ignoreNextEvent = true;
			}
			final boolean created = evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED);
			
			// "New value" always contains newly created/destroyed CyNetworkView.
			final CyNetworkView view = (CyNetworkView) evt.getNewValue();
			if (created) {
				updateNodeViews(view.getNetwork(), view);
			} 
			
			final List<String> parents = Cytoscape.getNetworkAttributes().getListAttribute(view.getNetwork().getIdentifier(), 
					CyNode.PARENT_NODES_ATTR);
			if (parents == null || parents.isEmpty()) {
				return;  // Not a nested network.
			}
			setNestedNetworkViews(parents, created);
			
		} else if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_CREATED)) {
			final CyNode parentNode = (CyNode) evt.getOldValue();
			final List<String> parents = new ArrayList<String>();
			parents.add(parentNode.getIdentifier());
			setNestedNetworkViews(parents, /* created = */ true);
		} else if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_DESTROYED)) {
			final CyNode parentNode = (CyNode) evt.getOldValue();
			final List<String> parents = new ArrayList<String>();
			parents.add(parentNode.getIdentifier());
			setNestedNetworkViews(parents, /* created = */ false);
		}
	}
	
	
	private void setNestedNetworkViews(final List<String> parents, final boolean created) {
		final Collection<CyNetworkView> networkViews = Cytoscape.getNetworkViewMap().values();
		for (final CyNetworkView networkView: networkViews) {
			boolean applyStyle = false;
			for (final String parentNode: parents) {
				// If this view contains a parentNode, then update its nested network view.
				final CyNode node = Cytoscape.getCyNode(parentNode);
				if (node == null) {
					continue;
				}
				final NodeView nodeView = networkView.getNodeView(node);
				
				if (nodeView != null) {
					final CyNetwork nestedNetwork = (CyNetwork)nodeView.getNode().getNestedNetwork();
					if (nestedNetwork != null) {
						CyNetworkView nestedNetworkView = Cytoscape.getNetworkView(nestedNetwork.getIdentifier());
						if (nestedNetworkView == Cytoscape.getNullNetworkView()) {
							nestedNetworkView = null;
						}
						((DNodeView)nodeView).setNestedNetworkView(created ? (DGraphView) nestedNetworkView : null);
					}
					applyStyle = true;
				}
			}
			
			// Apply visual style if necessary
			if (applyStyle)
				networkView.redrawGraph(/* do layout = */ false, /* apply visual style = */ true);
		}
	}
	
	
	private void updateNodeViews(final CyNetwork currentNetwork, final CyNetworkView currentNetworkView) {		
		for (final CyNode node: (List<CyNode>)currentNetwork.nodesList()) {
			final CyNetwork nestedNetwork = (CyNetwork) node.getNestedNetwork();
			if (nestedNetwork != null) {
				final CyNetworkView nestedNetworkView = Cytoscape.getNetworkView(nestedNetwork.getIdentifier());
				if (!nestedNetworkView.equals(Cytoscape.getNullNetworkView())) {
					final NodeView nodeView = currentNetworkView.getNodeView(node);
					((DNodeView)nodeView).setNestedNetworkView((DGraphView) nestedNetworkView);
				}
			}
		}
	}
}

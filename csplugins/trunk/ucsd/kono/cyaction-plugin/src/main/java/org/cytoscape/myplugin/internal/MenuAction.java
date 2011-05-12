package org.cytoscape.myplugin.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;

/**
 * Creates a new menu item under Plugins menu section.
 * 
 */
public class MenuAction extends AbstractCyAction {

	public MenuAction(final CyApplicationManager applicationManager,
			final String menuTitle) {
		super(menuTitle, applicationManager);
		setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent e) {
		final CyNetworkView currentNetworkView = applicationManager
				.getCurrentNetworkView();
		if (currentNetworkView == null)
			return;

		// View is always associated with its model.
		final CyNetwork network = currentNetworkView.getModel();

		for (CyNode node : network.getNodeList()) {
			if (network.getNeighborList(node, CyEdge.Type.ANY).isEmpty())
				currentNetworkView.getNodeView(node).setVisualProperty(
						MinimalVisualLexicon.NODE_VISIBLE, false);
		}

		currentNetworkView.updateView();

	}
}

package org.cytoscape.myplugin.internal;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;

public class MenuAction extends AbstractCyAction {

	private static final long serialVersionUID = -7457271379655577597L;

	public MenuAction(final CyApplicationManager applicationManager) {
		super("Hide unconnected nodes", applicationManager);
		setPreferredMenu("Select");
	}

	public void actionPerformed(ActionEvent e) {
		final CyNetwork currentNetwork = this.applicationManager
				.getCurrentNetwork();
		if (currentNetwork == null)
			return;

		final CyNetworkView currentNetworkView = applicationManager
				.getCurrentNetworkView();

		for (CyNode node : currentNetwork.getNodeList()) {
			if (currentNetwork.getNeighborList(node, CyEdge.Type.ANY).isEmpty())
				currentNetworkView.getNodeView(node).setVisualProperty(
						MinimalVisualLexicon.NODE_VISIBLE, false);
		}

		currentNetworkView.updateView();
	}
}

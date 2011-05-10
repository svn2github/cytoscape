package org.cytoscape.plugin.example;


import java.awt.event.ActionEvent;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.model.CyNetworkView;
import static org.cytoscape.view.presentation.property.MinimalVisualLexicon.NODE_VISIBLE;


public class MenuAction extends AbstractCyAction {
	private final CyPluginAdapter adapter;

	public MenuAction(CyPluginAdapter adapter) {
		super("Hide unconnected nodes", adapter.getCyApplicationManager());
		this.adapter = adapter;
		setPreferredMenu("Select");
	}

	public void actionPerformed(ActionEvent e) {
		final CyNetwork currentNetwork = adapter.getCyApplicationManager().getCurrentNetwork();
		if (currentNetwork == null)
			return;

		final CyNetworkView currentNetworkView = adapter.getCyApplicationManager().getCurrentNetworkView();

		for (CyNode node : currentNetwork.getNodeList()) {
			if (currentNetwork.getNeighborList(node, CyEdge.Type.ANY).isEmpty())
				currentNetworkView.getNodeView(node).setVisualProperty(NODE_VISIBLE, false);
		}
	}
}

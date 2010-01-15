package org.cytoscape.BipartiteVisualiserPlugin;

import java.util.SortedSet;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import ding.view.EdgeContextMenuListener;

public class BipartiteLayoutContextMenuListener implements
		EdgeContextMenuListener {

	@Override
	public void addEdgeContextMenuItems(final EdgeView edgeView,
			final JPopupMenu menu) {
		if (menu == null)
			return;

		final Edge edge = edgeView.getEdge();

		final Node source = edge.getSource();
		final CyNetwork network1 = (CyNetwork) source.getNestedNetwork();
		System.out.println("====== Net1: " + network1);
		if (network1 == null)
			return;

		final Node target = edge.getTarget();
		final CyNetwork network2 = (CyNetwork) target.getNestedNetwork();
		System.out.println("====== Net2: " + network2);
		if (network2 == null)
			return;

		final JMenuItem createBipartiteViewMenuItem = new JMenuItem(
				"Create Nested Network Side-by-Side View");
		menu.add(createBipartiteViewMenuItem);

		// Parent network is ALWAYS current network view
		final CyNetwork parentNetwork = Cytoscape.getCurrentNetworkView()
				.getNetwork();

		createBipartiteViewMenuItem
				.addActionListener(new CreateBipartiteViewAction(
						edgeView,parentNetwork, network1, network2));

	}
	
	private SortedSet<CyNetwork> getReferenceNetworkCandidates(final CyNetwork parentNetwork) { 
		return null;
	}

}

package org.cytoscape.BipartiteVisualiserPlugin;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cytoscape.CyNetwork;
import ding.view.EdgeContextMenuListener;

public class BipartiteLayoutContextMenuListener implements
		EdgeContextMenuListener {

	@Override
	public void addEdgeContextMenuItems(final EdgeView edgeView,
			final JPopupMenu menu) {
		if (menu == null)
			return;

		final JMenuItem createBipartiteViewMenuItem = new JMenuItem("Create Nested Network Side-by-Side View");
		menu.add(createBipartiteViewMenuItem);
		
		final Edge edge = edgeView.getEdge();

		final Node source = edge.getSource();
		final CyNetwork network1 = (CyNetwork) source.getNestedNetwork();
		System.out.println("====== Net1: " + network1); 
		if (network1 == null) {
			createBipartiteViewMenuItem.setEnabled(false);
			return;
		}

		final Node target = edge.getTarget();
		final CyNetwork network2 = (CyNetwork) target.getNestedNetwork();
		System.out.println("====== Net2: " + network2);
		if (network2 == null) {
			createBipartiteViewMenuItem.setEnabled(false);
			return;
		}

		
		createBipartiteViewMenuItem.addActionListener(new CreateBipartiteViewAction(edgeView));
		
	}

}

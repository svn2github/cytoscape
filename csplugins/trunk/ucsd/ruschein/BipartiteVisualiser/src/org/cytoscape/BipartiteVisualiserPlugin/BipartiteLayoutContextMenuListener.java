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

		final Edge edge = edgeView.getEdge();

		final Node source = edge.getSource();
		final CyNetwork network1 = (CyNetwork) source.getNestedNetwork();
		if (network1 == null)
			return;

		final Node target = edge.getTarget();
		final CyNetwork network2 = (CyNetwork) source.getNestedNetwork();
		if (network2 == null)
			return;

		final JMenuItem createBipartiteViewMenuItem = new JMenuItem("Create Side-by-Side View");
		//createBipartiteViewMenuItem.addActionListener(new MyEdgeAction(edgeView));
		menu.add(createBipartiteViewMenuItem);
	}

}

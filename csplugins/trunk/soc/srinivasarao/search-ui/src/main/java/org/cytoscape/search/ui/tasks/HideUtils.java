package org.cytoscape.search.ui.tasks;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.EDGE_VISIBLE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_VISIBLE;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;

abstract class HideUtils {

	static void setVisibleNodes(Collection<CyNode> nodes, boolean visible,
			CyNetworkView view) {
		for (CyNode n : nodes) {
			if (view != null) {
				view.getNodeView(n).setVisualProperty(NODE_VISIBLE, visible);

				for (CyNode n2 : n.getNeighborList(CyEdge.Type.ANY))
					for (CyEdge e : view.getSource().getConnectingEdgeList(n,
							n2, CyEdge.Type.ANY))
						view.getEdgeView(e).setVisualProperty(EDGE_VISIBLE,
								visible);
			}
		}
	}

	static void setVisibleEdges(Collection<CyEdge> edges, boolean visible,
			CyNetworkView view) {
		for (CyEdge e : edges) {
			if (view != null)
				view.getEdgeView(e).setVisualProperty(EDGE_VISIBLE, visible);
		}
	}
}

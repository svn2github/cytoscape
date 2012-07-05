package org.cytoscape.cytobridge;

import java.util.Collection;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.AddedNodeViewsEvent;
import org.cytoscape.view.model.events.AddedNodeViewsListener;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;

public class NodeViewListener implements AddedNodeViewsListener {

	private VisualMappingManager visMan;
	
	public NodeViewListener(VisualMappingManager visMan) {
		this.visMan = visMan;
	}
	
	public void handleEvent(AddedNodeViewsEvent e) {
		//update layout automatically (of new nodes)
		CyNetworkView networkView = e.getSource();
		Collection<CyRow> wasSelected = networkView.getModel().getDefaultNodeTable().getMatchingRows(CyNetwork.SELECTED, true);
		for (CyRow node : wasSelected) {
			node.set(CyNetwork.SELECTED, false);
		}
		VisualStyle style = visMan.getVisualStyle(networkView);
		for (View<CyNode> v : e.getNodeViews()) {
			networkView.getModel().getRow(v.getModel()).set(CyNetwork.SELECTED, true);
			style.apply(networkView.getModel().getRow(v.getModel()), networkView.getNodeView(v.getModel()));
		}
		for (CyRow node : wasSelected) {
			node.set(CyNetwork.SELECTED, true);
		}
		networkView.updateView();
	}

}

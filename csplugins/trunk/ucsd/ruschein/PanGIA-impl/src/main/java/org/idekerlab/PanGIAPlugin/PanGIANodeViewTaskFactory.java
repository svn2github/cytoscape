package org.idekerlab.PanGIAPlugin;


import org.cytoscape.model.CyNode;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;


public class PanGIANodeViewTaskFactory extends DynamicSupport implements NodeViewTaskFactory {

	public PanGIANodeViewTaskFactory() {
		//super();
	}	
	
	
	public void setNodeView(View<CyNode> nodeView, CyNetworkView netView) {
		setViews(nodeView, netView);
	}

}



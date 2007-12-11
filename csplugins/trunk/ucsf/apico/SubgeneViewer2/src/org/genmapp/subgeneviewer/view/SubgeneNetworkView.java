package org.genmapp.subgeneviewer.view;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.ding.DingNetworkView;

public  class SubgeneNetworkView extends DingNetworkView {


	private CyNode parentNode = null;
	public CyNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(CyNode parentNode) {
		this.parentNode = parentNode;
	}

	public SubgeneNetworkView(CyNetwork net, String title)
	{
		super(net, title);
	}
	
	
	
}

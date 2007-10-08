package org.genmapp.subgeneviewer.splice.view;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class Feature extends SubgeneNodeView {

//todo: may just be a wrapper around cynode
	
	Region region = null;
	
	CyNode cyNode;
	
	public Feature (Region region)
	{
		this.region = region;
	}
	
	public Feature (Region region, String nodeId)
	{
		this.region = region;
		this.cyNode = Cytoscape.getCyNode(nodeId, true);
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public CyNode getCyNode() {
		return cyNode;
	}

	public void setCyNode(CyNode cyNode) {
		this.cyNode = cyNode;
	}
}

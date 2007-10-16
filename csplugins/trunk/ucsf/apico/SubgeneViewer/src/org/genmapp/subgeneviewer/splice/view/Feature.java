package org.genmapp.subgeneviewer.splice.view;

import java.awt.Graphics;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class Feature extends SubgeneNodeView {

//todo: may just be a wrapper around cynode
	
	Region region = null;
	
	CyNode cyNode;
		
	/**
	 * label for the feature.  Will initially default to the nodeID passed in
	 * N.B. -- the ParseSplice class should pass in feature_id from the file as
	 * the value for the node_id.
	 */
	String feature_id;

	public Feature (Region region)
	{
		this.region = region;
	}
	
	public Feature (Region region, String nodeId)
	{
		this.region = region;
		this.cyNode = Cytoscape.getCyNode(nodeId, true);
		this.feature_id = nodeId;
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

	public String getFeature_id() {
		return feature_id;
	}

	public void setFeature_id(String feature_id) {
		this.feature_id = feature_id;
	}
	
	public void paint (Graphics g)
	{
		super.paint(g);
		g.drawString (this.getFeature_id(), this.getBounds().x, this.getBounds().y);
	}

}

package org.genmapp.subgeneviewer.splice.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class Feature extends SubgeneNodeView {

	// todo: may just be a wrapper around cynode

	Region region = null;

	CyNode cyNode;

	CyAttributes _att = Cytoscape.getNodeAttributes();

	/**
	 * label for the feature. Will initially default to the nodeID passed in
	 * N.B. -- the ParseSplice class should pass in feature_id from the file as
	 * the value for the node_id.
	 */
	String feature_id;

	public Feature(Region region) {
		this.region = region;
	}

	public Feature(Region region, String nodeId) {
		this.region = region;
		this.cyNode = Cytoscape.getCyNode(nodeId, true);

		// temp setting of color att
		Double random = Math.random();
		// map from green to red through black, THIS IS A TEMPORARY HACK
		if (random < 0.5) {
		_att.setAttribute(cyNode.getIdentifier(), "red",
				0);
		_att.setAttribute(cyNode.getIdentifier(), "green",
				(int) (255 * (1.0 - random)));
		_att.setAttribute(cyNode.getIdentifier(), "blue",0);
		}
		else {
			_att.setAttribute(cyNode.getIdentifier(), "red",
					(int) (255 * random));
			_att.setAttribute(cyNode.getIdentifier(), "green",
					0);
			_att.setAttribute(cyNode.getIdentifier(), "blue",0);
			
		}

		// System.out.println("node is "+ cyNode);
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

	public void paint(Graphics g) {
		super.paint(g);
		Rectangle r = this.getBounds();
		g.drawRect(r.x, r.y, r.width, r.height);
		g.drawString(this.getFeature_id(), this.getBounds().x,
				this.getBounds().y);
	}

}

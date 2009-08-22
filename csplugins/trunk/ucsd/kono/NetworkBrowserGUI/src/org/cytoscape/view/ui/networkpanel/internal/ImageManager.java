package org.cytoscape.view.ui.networkpanel.internal;

import giny.model.GraphPerspective;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.groups.CyGroup;

public class ImageManager {

	private static final int IMAGE_W = 250;
	private static final int IMAGE_H = 250;

	private final Map<CyGroup, Image> imageMap;

	public ImageManager() {
		this.imageMap = new HashMap<CyGroup, Image>();
	}

	public void addGroup(CyGroup group) {
		if (group == null)
			return;

		final CyNetwork network = Cytoscape.getCurrentNetwork();
		final Image groupImage = MCODEUtil.convertNetworkToImage(toGP(network,
				group), IMAGE_H, IMAGE_W, null, true);

		if (groupImage != null) {
			System.out.println("############### Got Image = " + groupImage);
			imageMap.put(group, groupImage);
		}

	}
	
	public Image getImage(CyGroup group) {
		return this.imageMap.get(group);
	}

	private GraphPerspective toGP(CyNetwork network, CyGroup cluster) {
		int[] nodes = new int[cluster.getNodes().size()];
		for (int i = 0; i < cluster.getNodes().size(); i++) {
			nodes[i] = cluster.getNodes().get(i).getRootGraphIndex();
		}
		return network.createGraphPerspective(nodes);
	}

}

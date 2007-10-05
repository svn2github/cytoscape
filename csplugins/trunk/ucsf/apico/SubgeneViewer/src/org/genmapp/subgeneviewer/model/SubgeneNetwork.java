package org.genmapp.subgeneviewer.model;

import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.splice.view.Region;

import cytoscape.CyNetwork;

public class SubgeneNetwork {
	
	private CyNetwork cynetwork;
	
	public CyNetwork getCynetwork() {
		return cynetwork;
	}

	public void setCynetwork(CyNetwork cynetwork) {
		this.cynetwork = cynetwork;
	}

}

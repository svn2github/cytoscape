package org.genmapp.subgeneviewer.splice.view;

import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.splice.model.SpliceEvent;
import org.genmapp.subgeneviewer.splice.model.StartSite;
import org.genmapp.subgeneviewer.view.SubgeneNetworkView;

import cytoscape.CyNode;

public class SpliceNetworkView extends SubgeneNetworkView {
	
	// todo: will have a list of blocks
	List<Block> listOfBlocks = new ArrayList<Block>();

	// todo: will have a list of splice events
	List<SpliceEvent> listOfSpliceEvents = new ArrayList<SpliceEvent>();

	// todo: list of start sites
	List<StartSite> listOfStartSites = new ArrayList<StartSite>();
	
	private CyNode parentNode = null;
	
	public void addBlock (Block block)
	{
		listOfBlocks.add(block);
		block.setNetworkView(this);
	}
	
	public void removeBlock  (Block block)
	{
		listOfBlocks.remove(block);
	}

	public CyNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(CyNode parentNode) {
		this.parentNode = parentNode;
	}

}

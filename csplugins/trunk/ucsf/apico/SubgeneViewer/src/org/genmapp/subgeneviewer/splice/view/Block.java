package org.genmapp.subgeneviewer.splice.view;

import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;
import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class Block extends SubgeneNodeView {

	//todo: has a list of regions
	List<Region> listOfRegions = new ArrayList<Region>();
	
	SubgeneNetworkView networkView = null;   // pointer back to subgene NetworkView that created me
	
	public Block (SubgeneNetworkView NetworkView)
	{
		this.networkView = NetworkView;
	}

	public SubgeneNetworkView getNetworkView() {
		return networkView;
	}

	public void setNetworkView(SubgeneNetworkView NetworkView) {
		this.networkView = NetworkView;
	}
	
	public void addRegion (Region region)
	{
		listOfRegions.add(region);
		region.setBlock(this);
	}
	
	public void removeRegion  (Region region)
	{
		listOfRegions.remove(region);
	}

}

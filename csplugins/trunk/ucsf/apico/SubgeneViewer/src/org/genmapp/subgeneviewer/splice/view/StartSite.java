package org.genmapp.subgeneviewer.splice.view;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class StartSite extends SubgeneNodeView {

	private Region region = null;

	SpliceNetworkView networkView = null;   // pointer back to subgene NetworkView that created me

	public StartSite (Region region)
	{
		this.region = region;
	}

	
	public StartSite (SpliceNetworkView NetworkView)
	{
		this.networkView = NetworkView;
	}

	public StartSite (SpliceNetworkView NetworkView, Region region)
	{
		this.networkView = NetworkView;
		this.region = region;
	}
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public SpliceNetworkView getNetworkView() {
		return networkView;
	}

	public void setNetworkView(SpliceNetworkView networkView) {
		this.networkView = networkView;
	}

}

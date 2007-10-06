package org.genmapp.subgeneviewer.splice.view;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class StartSite extends SubgeneNodeView {

	private Region region = null;
	
	public StartSite (Region region)
	{
		this.region = region;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

}

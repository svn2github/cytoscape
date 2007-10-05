package org.genmapp.subgeneviewer.splice.view;

import org.genmapp.subgeneviewer.model.SubgeneNode;
import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class Feature extends SubgeneNodeView {

//todo: may just be a wrapper around cynode
	
	Region region = null;
	
	public Feature (Region region)
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

package org.genmapp.subgeneviewer.splice.view;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class SpliceEvent extends SubgeneNodeView {

	private Region region = null;
	private String _toBlock;
	private String _toRegion;
	
	public SpliceEvent (Region region)
	{
		this.region = region;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public void setId(String toBlock, String toRegion){
		_toBlock = toBlock;
		_toRegion = toRegion;
	}
	
}

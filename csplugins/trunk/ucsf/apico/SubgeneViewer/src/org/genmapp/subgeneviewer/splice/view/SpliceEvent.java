package org.genmapp.subgeneviewer.splice.view;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class SpliceEvent extends SubgeneNodeView {

	private Region region = null;
	private Integer _toBlock;
	private Integer _toRegion;
	
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

	public void setId(Integer toBlock, Integer toRegion){
		_toBlock = toBlock;
		_toRegion = toRegion;
	}
	
}

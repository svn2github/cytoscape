package org.genmapp.subgeneviewer.view;

import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.model.SubgeneNetwork;
import org.genmapp.subgeneviewer.model.SubgeneNode;

public class Region extends SubgeneNodeView {

	//todo: has a list features
	List<Feature> listOfFeatures = new ArrayList<Feature>();
	
	Block block = null;

	public Region (Block block)
	{
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	public void addFeature (Feature feature)
	{
		listOfFeatures.add(feature);
		feature.setRegion(this);
	}
	
	public void removeFeature  (Feature feature)
	{
		listOfFeatures.remove(feature);
	}

}

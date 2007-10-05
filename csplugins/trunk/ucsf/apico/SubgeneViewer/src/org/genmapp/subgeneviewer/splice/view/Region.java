package org.genmapp.subgeneviewer.splice.view;

import java.util.ArrayList;
import java.util.List;

import org.genmapp.subgeneviewer.model.SubgeneNetwork;
import org.genmapp.subgeneviewer.model.SubgeneNode;
import org.genmapp.subgeneviewer.view.SubgeneNodeView;

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
	
	public Feature addFeature (String id)
	{
		Feature feature = new Feature(this);
		feature.setId(id);
		listOfFeatures.add(feature);
		feature.setRegion(this);
		return feature;
	}
	
	public void removeFeature  (Feature feature)
	{
		listOfFeatures.remove(feature);
	}

}

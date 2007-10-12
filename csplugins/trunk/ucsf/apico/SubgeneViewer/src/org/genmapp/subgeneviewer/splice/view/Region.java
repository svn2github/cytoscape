package org.genmapp.subgeneviewer.splice.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genmapp.subgeneviewer.model.SubgeneNetwork;
import org.genmapp.subgeneviewer.model.SubgeneNode;
import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class Region extends SubgeneNodeView {

	//todo: has a list features
	List<Feature> listOfFeatures = new ArrayList<Feature>();
	public Iterator<Feature> getFeatureInterator()
	{
		return listOfFeatures.iterator();
	}
	
	List<SpliceEvent> listOfSpliceEvents = new ArrayList<SpliceEvent>();
	public Iterator<SpliceEvent> getSpliceEventInterator()
	{
		return listOfSpliceEvents.iterator();
	}
	
	boolean _containsStartSite = false;
	
	private Block _block = null;

	public Region (Block block)
	{
		_block = block;
	}

	public Block getBlock() {
		return _block;
	}

	public void setBlock(Block block) {
		_block = block;
	}
	
	public Feature addFeature (String id)
	{
		return addFeature (id, null);
	}
	
	public Feature addFeature (String id, String feature_id)
	{
		Feature feature = new Feature(this);
		feature.setId(id);
		feature.setFeature_id(feature_id);
		listOfFeatures.add(feature);
		feature.setRegion(this);
		return feature;
	}
	
	
	
	public void removeFeature  (Feature feature)
	{
		listOfFeatures.remove(feature);
	}

	public SpliceEvent addSpliceEvent(String toBlock, String toRegion) {
		SpliceEvent spliceEvent = new SpliceEvent(this);
		spliceEvent.setId(toBlock, toRegion);
		listOfSpliceEvents.add(spliceEvent);
		spliceEvent.setRegion(this);
		return spliceEvent;
	}

	public boolean containsStartSite() {
		return _containsStartSite;
	}

	public void containsStartSite(boolean containsStartSite) {
		this._containsStartSite = containsStartSite;
	}

}

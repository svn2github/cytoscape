package org.genmapp.subgeneviewer.splice.view;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
//		this.add(feature); // for repaint
		return feature;
	}
	
	
	
	public void removeFeature  (Feature feature)
	{
		listOfFeatures.remove(feature);
	}

	/**
	 * get Feature by ID 
	 * currently iterates through list of Features until it finds a match
	 * this is inefficient but may not be an issue if lists are small
	 * @param id
	 */
	public Feature getFeature (String id)
	{
		Feature myFeature = null;
		Iterator<Feature> it = this.getFeatureInterator();
		while (it.hasNext())
		{
			myFeature = it.next();
			if (myFeature.getId().equals(id))
			{
				return myFeature;
			}
		}
		return null;
	}	
	
	public void paint (Graphics g)
	{
		System.out.println ("now drawing from region: " + this);
		Rectangle r = this.getBounds();
		System.out.println("region bounds (Rectangle are: ) " + r);
		g.drawRect(r.x, r.y, r.width, r.height);
		this.paintChildren(g);
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

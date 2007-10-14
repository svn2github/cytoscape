package org.genmapp.subgeneviewer.splice.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;
import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class Block extends SubgeneNodeView {

	//todo: has a list of regions
	List<Region> listOfRegions = new ArrayList<Region>();
	public Iterator<Region> getRegionInterator()
	{
		return listOfRegions.iterator();
	}
	
	private String type; // intron or exon

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
	
	public Region addRegion (String id)
	{
		Region region = new Region(this);
		region.setId(id);
		listOfRegions.add(region);
		region.setBlock(this);
		return region;
	}
	
	public void removeRegion  (Region region)
	{
		listOfRegions.remove(region);
	}

	/**
	 * get Region by ID 
	 * currently iterates through list of Regions until it finds a match
	 * this is inefficient but may not be an issue if lists are small
	 * @param id
	 */
	public Region getRegion (String id)
	{
		Region myRegion = null;
		Iterator<Region> it = this.getRegionInterator();
		while (it.hasNext())
		{
			myRegion = it.next();
			if (myRegion.getId().equals(id))
			{
				return myRegion;
			}
		}
		return null;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}

package org.genmapp.subgeneviewer.splice.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.genmapp.subgeneviewer.view.SubgeneNetworkView;
import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class SpliceEvent extends SubgeneNodeView {

	private Region region = null;
	private String _toBlock;
	private String _toRegion;

	SpliceNetworkView networkView = null;   // pointer back to Splice NetworkView that created me

	// AJK: 10/14/07 defaults.  Should these be centralized?
	private int _lineWidth = 2;
	private int _height = 35;  // from what I can tell this should be about 1.5 * Feature height
	private int _offset;
	private Color _color = Color.blue;  // should this be configurable?  Where?
	
	
	public SpliceEvent (Region region)
	{
		this.region = region;
	}

	public SpliceEvent (SpliceNetworkView betworkView)
	{
		this.networkView = betworkView;
	}
	
	public SpliceEvent (SpliceNetworkView networkView, Region region)
	{
		this.networkView = networkView;
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
	
	/**
	 * paint myself on the subgeneview in the subgeneview's coordinates
	 */
	public void paint (Graphics g)
	{
		Rectangle regionBounds = networkView.getBounds(region);
		if (regionBounds == null)
		{
			return;
		}

		int startX = regionBounds.x + regionBounds.width;
		int startY = regionBounds.y + regionBounds.height;
		
		Block block = networkView.getBlock(_toBlock);
		Region toRegion = block.getRegion(_toRegion);
		
		Rectangle toRegionBounds = networkView.getBounds(toRegion);
		if (toRegionBounds == null)
		{
			return;
		}

		int endX = toRegionBounds.x;
		int endY = toRegionBounds.y + toRegionBounds.height;
		
		g.setColor(this._color);
		
		// draw the lines. 
		// consider using Graphics2D so that we have control over width of line
		g.drawLine(startX, startY, ((startX + endX) / 2), startY + _height);
		g.drawLine(((startX + endX) / 2), startY + _height, endX, endY);
		
	}

	public SpliceNetworkView getNetworkView() {
		return networkView;
	}

	public void setNetworkView(SpliceNetworkView networkView) {
		this.networkView = networkView;
	}
	
}

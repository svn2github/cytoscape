package org.genmapp.subgeneviewer.splice.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;

import org.genmapp.subgeneviewer.view.SubgeneNodeView;

public class StartSite extends SubgeneNodeView {

	private Region region = null;

	SpliceNetworkView networkView = null;   // pointer back to subgene NetworkView that created me

	// AJK: 10/14/07 defaults.  Should these be centralized?
	private int _height = 35;  // from what I can tell this should be about 1.5 * Feature height
	private Color _color = Color.red;  // should this be configurable?  Where?
	private int _flagWidth = 10;  // width of bounding box for triangle of flag
	private int _flagHeight = 10;  // height of bounding box for triangle of flag

	public StartSite (Region region)
	{
		this.region = region;
	}

	
	public StartSite (SpliceNetworkView NetworkView)
	{
		this.networkView = NetworkView;
	}

	public StartSite (SpliceNetworkView NetworkView, Region region)
	{
		this.networkView = NetworkView;
		this.region = region;
	}
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public SpliceNetworkView getNetworkView() {
		return networkView;
	}

	public void setNetworkView(SpliceNetworkView networkView) {
		this.networkView = networkView;
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

		int startX = regionBounds.x;
		int startY = regionBounds.y - _height; // draw flag above region
		int endY = regionBounds.y;
		
		g.setColor(this._color);
		
		// draw the staff of the flag
		// consider using Graphics2D so that we have control over width of line
		g.drawLine(startX, startY, startX, endY);
		
		// now draw the flag
		Polygon p = new Polygon();
		p.addPoint(startX, startY);
		p.addPoint(startX + _flagWidth, startY + (_flagHeight / 2));
		p.addPoint(startX, startY + _flagHeight);
		g.fillPolygon(p);
		
	}

}

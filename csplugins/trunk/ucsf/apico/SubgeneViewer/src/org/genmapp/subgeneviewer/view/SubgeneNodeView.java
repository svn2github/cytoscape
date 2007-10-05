package org.genmapp.subgeneviewer.view;

import java.awt.Rectangle;

import cytoscape.view.CyNodeView;

public class SubgeneNodeView {
	
	// todo: needs a label
	private String id;
	

	private CyNodeView cynodeView;

	private Rectangle bounds = null;
	

	public CyNodeView getCynodeView() {
		return cynodeView;
	}

	public void setCynodeView(CyNodeView cynodeView) {
		this.cynodeView = cynodeView;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public void setBounds(int x, int y, int width, int height)
	{
		this.bounds = new Rectangle(x, y, width, height);
	}
	
	/**
	 * computeBounds	should be overwritten in subclasses, e.g. block, region, feature
	 * @return
	 */
	public Rectangle computeBounds()
	{
		return bounds;
	}

	public String getId() {
		return id;
	}

	public void setId(String label) {
		this.id = label;
	}
	
}

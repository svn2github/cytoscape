package org.genmapp.subgeneviewer.view;

import java.awt.Rectangle;

import cytoscape.view.CyNodeView;

public class SubgeneNodeView {

	private Integer _id;

	private CyNodeView _cynodeView;

	private Rectangle _bounds = null;
	

	public CyNodeView getCynodeView() {
		return _cynodeView;
	}

	public void setCynodeView(CyNodeView cynodeView) {
		_cynodeView = cynodeView;
	}

	public Rectangle getBounds() {
		return _bounds;
	}

	public void setBounds(Rectangle bounds) {
		_bounds = bounds;
	}
	
	public void setBounds(int x, int y, int width, int height)
	{
		_bounds = new Rectangle(x, y, width, height);
	}
	
	/**
	 * computeBounds	should be overwritten in subclasses, e.g. block, region, feature
	 * @return
	 */
	public Rectangle computeBounds()
	{
		return _bounds;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer id) {
		_id = id;
	}
	
}

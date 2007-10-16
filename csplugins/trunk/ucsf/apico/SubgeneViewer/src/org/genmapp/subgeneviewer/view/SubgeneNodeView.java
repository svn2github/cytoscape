package org.genmapp.subgeneviewer.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;



public class SubgeneNodeView extends JComponent {

	private String _id;

	/**
	 * computeBounds	should be overwritten in subclasses, e.g. block, region, feature
	 * @return
	 */
	public Rectangle computeBounds()
	{
		return this.getBounds();
	}

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}
	
	public void paint (Graphics g)
	{
		this.paintChildren(g);
		g.setColor(Color.black);
		g.drawRect(this.getBounds().x, this.getBounds().y, this.getBounds().width, this.getBounds().height);
	}
	
}

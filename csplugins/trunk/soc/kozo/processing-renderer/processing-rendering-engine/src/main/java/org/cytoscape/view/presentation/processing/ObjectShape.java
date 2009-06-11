package org.cytoscape.view.presentation.processing;

import java.awt.Color;

/**
 * 
 * Defines shape of object rendered in Processing.
 * 
 * @author kono, kozo
 * @version 0.0.1
 * 
 *
 */
public interface ObjectShape {
	
	public void draw();
	
	/**
	 * Name of this shape, such as ellipse, rectangle, triangle, etc.
	 * 
	 * @return Name of shape as string
	 * 
	 */
	public String getDisplayName();
	
	public float getX();
	public float getY();
	
	public void setX(final float x);
	public void setY(final float y);
	
	public Color getColor();
	public void setColor(Color color);
	
	public float getWidth();
	public float getHeight();
	
	public void setWidth(final float width);
	public void setHeight(final float height);
	
}

package org.cytoscape.view.presentation.processing;

import java.awt.Paint;

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
	 * This is immutable.
	 * 
	 * @return Name of shape as string
	 * 
	 */
	public String getDisplayName();
	
	public float getX();
	public float getY();
	public float getZ();
	
	public void setX(final float x);
	public void setY(final float y);
	public void setZ(final float z);
	public void setLocation(float x, float y, float z);
	
	public Paint getPaint();
	public void setPaint(Paint paint);
	
	public void setOpacity(float alpha);
	public float getOpacity();
	
	public float getWidth();
	public float getHeight();
	public float getDepth();
	
	public void setWidth(final float width);
	public void setHeight(final float height);
	public void setDepth(final float depth);
	public void setSize(float w, float h, float d);
	
	public void scale(float s);
	public void rotate(float angle);
	
	
	
}

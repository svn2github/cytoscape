package org.cytoscape.view.presentation.processing.internal.shape;

import java.awt.Color;

import org.cytoscape.view.presentation.processing.ObjectShape;
import org.cytoscape.view.presentation.processing.PickableObject;

public abstract class AbstractObjectShape implements ObjectShape, PickableObject {
	
	protected float width;
	protected float height;
	protected float depth;
	
	protected float x, y, z;
	
	protected boolean picked;
	
	protected Color basicColor;

	// This should be implemented in each shape implementation classes.
	public abstract void draw();

	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setColor(Color color) {
		// TODO Auto-generated method stub

	}

	public void setHeight(float height) {
		// TODO Auto-generated method stub

	}

	public void setWidth(float width) {
		// TODO Auto-generated method stub

	}

	public void setX(float x) {
		// TODO Auto-generated method stub

	}

	public void setY(float y) {
		// TODO Auto-generated method stub

	}
	
	public void pick(float x, float y){
		
	}

}

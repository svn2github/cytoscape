package org.cytoscape.view.presentation.processing.internal.shape;

import java.awt.Color;

import org.cytoscape.view.presentation.processing.ObjectShape;
import org.cytoscape.view.presentation.processing.PickableObject;

import processing.core.PApplet;

public abstract class AbstractObjectShape implements ObjectShape, PickableObject {
	
	protected PApplet p;
	
	protected float width;
	protected float height;
	protected float depth;
	
	protected float x, y, z;
	
	protected boolean picked;
	
	protected Color basicColor;
	
	public AbstractObjectShape(PApplet parent) {
		this.p = parent;
	}

	public abstract void draw();

	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getOpacity() {
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

	public float getZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void rotate(float angle) {
		// TODO Auto-generated method stub
		
	}

	public void scale(float s) {
		// TODO Auto-generated method stub
		
	}

	public void setColor(Color color) {
		// TODO Auto-generated method stub
		
	}

	public void setDepth(float depth) {
		// TODO Auto-generated method stub
		
	}

	public void setHeight(float height) {
		// TODO Auto-generated method stub
		
	}

	public void setLocation(float x, float y, float z) {
		// TODO Auto-generated method stub
		
	}

	public void setOpacity(float alpha) {
		// TODO Auto-generated method stub
		
	}

	public void setSize(float w, float h, float d) {
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

	public void setZ(float z) {
		// TODO Auto-generated method stub
		
	}

	public void pick(float x, float y) {
		// TODO Auto-generated method stub
		
	}



	
}

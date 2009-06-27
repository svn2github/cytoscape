package org.cytoscape.view.presentation.processing.internal.shape;

import java.awt.Paint;

import org.cytoscape.view.presentation.processing.ObjectShape;
import org.cytoscape.view.presentation.processing.PickableObject;

import processing.core.PApplet;

public abstract class AbstractObjectShape implements ObjectShape, PickableObject {
	
	// Parent component, which is the Processing canvas.
	protected final PApplet p;
	
	// Human readable name of this shape.  This is mandatory and immutable.
	protected String displayName;
	
	// Dimension of this object
	protected float width, height, depth;
	
	// Location
	protected float x, y, z;
	
	// Selection status
	protected boolean picked;
	
	// Basic Paint of this object.  Will be used if texture is not available.
	protected Paint basicPaint;
	
	// Opacity of this shape.
	protected float opacity;
	
	public AbstractObjectShape(PApplet parent) {
		this(0, 0, 0, parent);
	}
	
	public AbstractObjectShape(float x, float y, float z, PApplet parent) {
		this.p = parent;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public abstract void draw();

	public Paint getPaint() {
		// TODO Auto-generated method stub
		return null;
	}

	public float getDepth() {
		return depth;
	}

	public String getDisplayName() {
		return displayName;
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
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void rotate(float angle) {
		// TODO Auto-generated method stub
		
	}

	public void scale(float s) {
		// TODO Auto-generated method stub
		
	}

	public void setPaint(Paint paint) {		
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
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void pick(float x, float y) {
		// TODO Auto-generated method stub
		
	}
}

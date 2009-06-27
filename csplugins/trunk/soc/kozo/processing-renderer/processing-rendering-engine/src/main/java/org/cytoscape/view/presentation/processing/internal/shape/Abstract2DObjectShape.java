package org.cytoscape.view.presentation.processing.internal.shape;

import processing.core.PApplet;

public abstract class Abstract2DObjectShape extends AbstractObjectShape {
	
	protected float borderWidth;

	public Abstract2DObjectShape(float x, float y, PApplet parent) {
		super(x, y, 0, parent);
	}

	public float getDepth() {
		return 0f;
	}

}

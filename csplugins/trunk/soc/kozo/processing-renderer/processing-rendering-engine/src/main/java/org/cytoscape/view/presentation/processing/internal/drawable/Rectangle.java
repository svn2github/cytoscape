package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.internal.ProcessingNetworkRenderer;

import processing.core.PApplet;

public class Rectangle extends AbstractSolidCyDrawable {

	protected float width;
	protected float height;

	public Rectangle(PApplet parent) {
		super(parent);
	}

	public void draw() {
		super.draw();
		
		p.pushMatrix();
		p.strokeWeight(3f);
		p.translate(0, 0, location.z);
		p.rectMode(PApplet.CENTER);
		p.rect(location.x, location.y, width, height);
		p.popMatrix();
	}

	public void setContext(View<?> viewModel) {
		super.setContext(viewModel);

		
		width = size;
		height = viewModel.getVisualProperty(NODE_Y_SIZE).floatValue();
		if (height <= 0)
			height = DEF_SIZE;

	}

}

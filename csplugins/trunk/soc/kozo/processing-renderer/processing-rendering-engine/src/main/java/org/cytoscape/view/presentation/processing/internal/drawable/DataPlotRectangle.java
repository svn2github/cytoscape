package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;

public class DataPlotRectangle extends AbstractSolidCyDrawable {
	
	private TexturedRectangle tex1;
	private Rectangle child2;
	
	protected float width;
	protected float height;

	public DataPlotRectangle(PApplet parent) {
		super(parent);
		
		// Create children
		// 1. Graphics window
		tex1 = new TexturedRectangle(p);
		this.children.add(tex1);
		// 2. Pi Chart as texture
		child2 = new Rectangle(p);
		this.children.add(child2);
		
	}
	
	public void draw() {
		

		p.pushMatrix();
		p.strokeWeight(3f);
		p.stroke(100, 100, 200, 150);
		p.fill(100, 100, 100, 100);
		p.translate(0, 0, location.z);
		p.rectMode(PApplet.CENTER);
		p.rect(location.x, location.y, width, height);
		p.popMatrix();
		
		for (CyDrawable child : children)
			child.draw();

	}
	
	public void setContext(View<?> viewModel) {
		super.setContext(viewModel);

		
		width = size;
		height = viewModel.getVisualProperty(NODE_Y_SIZE).floatValue();
		if (height <= 0)
			height = DEF_SIZE;
		
		// Set internal components relative to this object.
		tex1.width = width/2;
		tex1.height = height/2;
		tex1.location.x = location.x - width/2+ 5;
		tex1.location.y = location.y - tex1.height/2;
		
		child2.width = width/2-20f;
		child2.height = height/2;
		child2.location.x = location.x + child2.width/2 + 10;
		child2.location.y = location.y;
		child2.b = 255f;
	}

}

package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_OPACITY;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_SELECTED_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class Rectangle extends AbstractCyDrawable {

	private static final int DEF_SIZE = 20;

	private Vec3D location;

	private float width;
	private float height;

	public Rectangle(PApplet parent) {
		super(parent);
		location = new Vec3D();

		compatibleDataType = new HashSet<Class<?>>();
		compatibleDataType.add(CyNode.class);
	}

	public void draw() {
		p.pushMatrix();
		p.strokeWeight(3f);
		p.noFill();
		p.stroke(100f, 0f, 0f, 100f);
		p.translate(0, 0, location.z);
		p.rectMode(PApplet.CENTER);
		p.rect(location.x, location.y, width, height);
		p.popMatrix();
	}

	public void setContext(View<?> viewModel) {
		this.selected = ((CyNode) viewModel.getSource()).attrs().get(
				"selected", Boolean.class);
		this.selectedColor = (Color) viewModel
				.getVisualProperty(NODE_SELECTED_COLOR);

		// Pick compatible lexicon only.
		location.x = viewModel.getVisualProperty(NODE_X_LOCATION).floatValue();
		location.y = viewModel.getVisualProperty(NODE_Y_LOCATION).floatValue();
		location.z = viewModel.getVisualProperty(NODE_Z_LOCATION).floatValue();

		this.size = viewModel.getVisualProperty(NODE_X_SIZE).floatValue();
		if (size <= 0)
			size = DEF_SIZE;
		width = size;
		height = viewModel.getVisualProperty(NODE_Y_SIZE).floatValue();
		if (height <= 0)
			height = DEF_SIZE;

		final Paint color = viewModel.getVisualProperty(NODE_COLOR);
		if (selected) {
			this.r = selectedColor.getRed();
			this.g = selectedColor.getGreen();
			this.b = selectedColor.getBlue();
			this.alpha = 200f;
		} else if (color instanceof Color) {
			this.r = ((Color) color).getRed();
			this.g = ((Color) color).getGreen();
			this.b = ((Color) color).getBlue();
			this.alpha = viewModel.getVisualProperty(NODE_OPACITY).floatValue();
		}

		// Set values for children
		for (CyDrawable child : children)
			child.setContext(viewModel);
	}

	public boolean isPicked() {
		return selected;
	}

	public void pick(float cx, float cy) {

	}

}

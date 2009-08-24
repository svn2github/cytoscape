package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon.NODE_OPACITY;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_SELECTED_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.awt.Color;
import java.awt.Paint;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public abstract class AbstractSolidCyDrawable extends AbstractCyDrawable {
	
	protected static final int DEF_SIZE = 20;
	protected final Vec3D location;
	protected float size;

	public AbstractSolidCyDrawable(PApplet parent) {
		super(parent);
		this.lexicon = null;
		this.location = new Vec3D();

		// Create children for label
		this.children.add(new Text(p, lexicon));

		compatibleDataType.add(CyNode.class);

	}

	public void draw() {
		if (!fastRendering) {
			for (CyDrawable child : children)
				child.draw();
			p.noStroke();
			p.fill(r, g, b, alpha);

		} else {
			// Do not draw children, and use wireframe.
			p.noFill();
			p.strokeWeight(2f);
			p.stroke(r, g, b, alpha);
		}

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

		final Paint color = viewModel.getVisualProperty(NODE_COLOR);
		if (selected) {
//			this.r = p.random(255);
//			this.g = p.random(255);
//			this.b = p.random(255);
			this.r = selectedColor.getRed();
			this.g = selectedColor.getGreen();
			this.b = selectedColor.getBlue();
			this.alpha = 180f;
//			this.alpha = p.random(255);
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

	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// If the VP is not in the context, ignore
		if (lexicon.getAllVisualProperties().contains(vp) == false)
			return;

		// Extract value for the visual property
		Object value = viewModel.getVisualProperty(vp);

	}

}

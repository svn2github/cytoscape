package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_OPACITY;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_SELECTED_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.awt.Color;
import java.awt.Paint;

import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;

import processing.core.PApplet;
import toxi.geom.Vec3D;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 * 
 */
public class Cube extends AbstractCyDrawable implements Pickable {

	private static final long serialVersionUID = -3971892445041605908L;

	private static final int DEF_SIZE = 20;

	private final Vec3D location;

	private static final String IMAGE_URL = "http://processing.org/img/processing_beta_cover.gif";

	public Cube(PApplet parent) {
		super(parent);
		this.lexicon = null;
		this.location = new Vec3D();

		// Create children for label
		this.children.add(new Text(p, lexicon));

		compatibleDataType.add(CyNode.class);

	}

	public Icon getIcon(int width, int height) {
		// TODO Implement icon renderer
		return null;
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
			p.strokeWeight(1f);
			p.stroke(50, 100, 100, 50);
		}

		p.pushMatrix();
		p.translate(location.x, location.y, location.z);
		p.box(size);
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

	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// If the VP is not in the context, ignore
		if (lexicon.getAllVisualProperties().contains(vp) == false)
			return;

		// Extract value for the visual property
		Object value = viewModel.getVisualProperty(vp);

	}

	public boolean isPicked() {
		return selected;
	}

	public void pick(float cx, float cy) {

		final float distance = PApplet.dist(cx, cy, p.screenX(location.x,
				location.y, location.z), p.screenY(location.x, location.y,
				location.z));
		System.out.println("Distance = " + distance);
		if (distance < 200) {
			selected = true;
			System.out.println("PICKED!!");
			this.r = 0;
			g = 250;
			b = 0;
			alpha = 255;
			System.out.println("Color of PICKED node" + g);
		} else
			selected = false;

	}

}

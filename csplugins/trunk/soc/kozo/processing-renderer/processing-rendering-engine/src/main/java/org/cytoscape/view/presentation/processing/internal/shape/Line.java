package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.*;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_SELECTED_COLOR;

import java.awt.Color;
import java.awt.Paint;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.EdgeItem;

import processing.core.PApplet;
import toxi.geom.Vec3D;

public class Line implements CyDrawable, EdgeItem {

	private final PApplet p;

	// Start and end point of this line.
	private Vec3D source;
	private Vec3D target;

	private float r, g, b, alpha;

	private float strokeWidth = 1f;

	private Boolean picked;

	private Color selected;

	public Line(PApplet p) {
		this.p = p;
		this.source = new Vec3D();
		this.target = new Vec3D();
	}

	public void draw() {

		p.stroke(r, g, b, alpha);
		p.strokeWeight(strokeWidth);

		p.line(source.x, source.y, source.z, target.x, target.y, target.z);
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Class<?>> getCompatibleModels() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContext(View<?> viewModel) {

		this.picked = ((CyEdge) viewModel.getSource()).attrs().get("selected",
				Boolean.class);
		this.selected = (Color) viewModel
				.getVisualProperty(EDGE_SELECTED_COLOR);

		Paint edgePaint = viewModel.getVisualProperty(EDGE_COLOR);
		alpha = viewModel.getVisualProperty(EDGE_OPACITY).floatValue();
		strokeWidth = viewModel.getVisualProperty(EDGE_WIDTH).floatValue();

		if (picked) {
			this.r = selected.getRed();
			this.g = selected.getGreen();
			this.b = selected.getBlue();
			this.alpha = 200f;
		} else if (edgePaint instanceof Color) {
			this.r = ((Color) edgePaint).getRed();
			this.g = ((Color) edgePaint).getGreen();
			this.b = ((Color) edgePaint).getBlue();
		}
	}

	public void setSource(View<?> sourceView) {
		source.x = sourceView.getVisualProperty(NODE_X_LOCATION).floatValue();
		source.y = sourceView.getVisualProperty(NODE_Y_LOCATION).floatValue();
		source.z = sourceView.getVisualProperty(NODE_Z_LOCATION).floatValue();
	}

	public void setTarget(View<?> targetView) {
		target.x = targetView.getVisualProperty(NODE_X_LOCATION).floatValue();
		target.y = targetView.getVisualProperty(NODE_Y_LOCATION).floatValue();
		target.z = targetView.getVisualProperty(NODE_Z_LOCATION).floatValue();
	}

	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// TODO Auto-generated method stub

	}

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub

	}

	public void setDetailFlag(boolean flag) {
		// TODO Auto-generated method stub

	}

}

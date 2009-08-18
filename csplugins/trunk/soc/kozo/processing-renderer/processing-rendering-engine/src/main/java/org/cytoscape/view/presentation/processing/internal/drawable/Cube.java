package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.*;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.*;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;
import org.cytoscape.view.presentation.processing.internal.ProcessingNetworkRenderer;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.Vec3D;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 * 
 */
public class Cube implements CyDrawable, Pickable {

	private static final long serialVersionUID = -3971892445041605908L;
	private static final String DISPLAY_NAME = "Cube";

	private static final int DEF_SIZE = 20;

	private static final int OFFSET = 10;

	private boolean picked;
	private Set<Class<?>> compatibleDataType;

	private final VisualLexicon lexicon;

	private PApplet p;

	private float size;
	private float r, g, b, alpha;
	private Color selected;

	private Vec3D location;
	
	private final List<CyDrawable> children;

	private boolean fastRendering = false;

	private Map<VisualProperty<?>, Object> fieldMap;

	private static final String IMAGE_URL = "http://processing.org/img/processing_beta_cover.gif";
	
	public Cube(PApplet parent) {
		super();
		this.p = parent;
		this.lexicon = null;
		this.picked = false;
		this.location = new Vec3D();

		this.children = new ArrayList<CyDrawable>();
		// Create children for label
		this.children.add(new Text(p, lexicon));

		compatibleDataType = new HashSet<Class<?>>();
		compatibleDataType.add(CyNode.class);
		
	}

	public Set<Class<?>> getCompatibleModels() {
		return compatibleDataType;
	}

	public String getDisplayName() {
		return DISPLAY_NAME;
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
		
		p.strokeWeight(3f);
		p.noFill();
		p.stroke(100f, 0f, 0f, 100f);
		p.translate(0, 0, location.z);
		p.rectMode(PApplet.CENTER);
		p.rect(location.x, location.y, size, size);
		
		
		//p.box(size);
		
		
//		p.noFill();
//		p.stroke(10, 10, 10, 100);
//		p.strokeWeight(1);
//		p.sphereDetail(5);
//		p.sphere(size*1.5f);
		p.popMatrix();

	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}

	public void setContext(View<?> viewModel) {
		
		this.picked = ((CyNode)viewModel.getSource()).attrs().get("selected", Boolean.class);
		this.selected = (Color) viewModel.getVisualProperty(NODE_SELECTED_COLOR);
		
		// Pick compatible lexicon only.
		location.x = viewModel.getVisualProperty(NODE_X_LOCATION).floatValue();
		location.y = viewModel.getVisualProperty(NODE_Y_LOCATION).floatValue();
		location.z = viewModel.getVisualProperty(NODE_Z_LOCATION).floatValue();

		this.size = viewModel.getVisualProperty(NODE_X_SIZE).floatValue();
		if (size <= 0)
			size = DEF_SIZE;

		final Paint color = viewModel.getVisualProperty(NODE_COLOR);
		if (picked) {
			this.r = selected.getRed();
			this.g = selected.getGreen();
			this.b = selected.getBlue();
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
		return picked;
	}

	public void pick(float cx, float cy) {

		final float distance = PApplet.dist(cx, cy, p.screenX(location.x, location.y,
				location.z), p.screenY(location.x, location.y, location.z));
		System.out.println("Distance = " + distance);
		if (distance < 200) {
			picked = true;
			System.out.println("PICKED!!");
			this.r = 0;
			g = 250;
			b = 0;
			alpha = 255;
			System.out.println("Color of PICKED node" + g);
		} else
			picked = false;

	}

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub
		this.children.add(child);
	}

	public void setDetailFlag(boolean flag) {
		this.fastRendering = flag;
	}

}

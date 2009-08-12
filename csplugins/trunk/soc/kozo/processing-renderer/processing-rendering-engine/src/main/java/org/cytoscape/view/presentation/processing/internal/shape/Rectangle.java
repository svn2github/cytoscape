package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;

import processing.core.PApplet;

import toxi.geom.Vec3D;

public class Rectangle extends Vec3D implements CyDrawable, Pickable {
	
	private static final String DISPLAY_NAME = "Rectangle";
	
	private static final int DEF_SIZE = 20;
	
	private PApplet p;
	
	private float width;
	private float height;
	private float depth;
		
	private int r, g, b, alpha;

	private boolean picked;
	private final VisualLexicon lexicon;
	
	private Set<Class<?>> compatibleDataType;
	
	public Rectangle(PApplet parent, VisualLexicon lexicon){
		super();
		this.p = parent;
		this.lexicon = lexicon;
		this.picked = false;
		
		compatibleDataType = new HashSet<Class<?>>();
		compatibleDataType.add(CyNode.class);
	}

	public Set<Class<?>> getCompatibleModels() {
		return compatibleDataType;
	}

	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	public Icon getIcon(int width, int height){
		// TODO Implement icon renderer
		return null;
	}
	
	public void draw() {
		p.pushMatrix();
		p.noStroke();
		p.translate(x, y, z);
		p.fill(r, g, b, alpha);
		p.box(width, height, depth);
		p.popMatrix();
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContext(View<?> viewModel) {
		// Pick compatible lexicon only.
		this.x = viewModel.getVisualProperty(NODE_X_LOCATION).floatValue();
		this.y = viewModel.getVisualProperty(NODE_Y_LOCATION).floatValue();
		
		if(p.random(1) > 0.5){
			viewModel.setVisualProperty(NODE_Z_LOCATION, 500d);
		}
		
		this.z = viewModel.getVisualProperty(NODE_Z_LOCATION).floatValue();
		
		this.width = viewModel.getVisualProperty(NODE_X_SIZE).floatValue();
		this.height = viewModel.getVisualProperty(NODE_Y_SIZE).floatValue();
		this.depth = viewModel.getVisualProperty(NODE_Z_SIZE).floatValue();
		
		if(width <= 0)
			width = DEF_SIZE;
		
		Paint color = viewModel.getVisualProperty(NODE_COLOR);
		if(picked){
			this.r = 0;
			g = 250;
			b = 0;
			alpha = 255;
		}else if (color instanceof Color){
			this.r = ((Color)color).getRed();
			this.g = ((Color)color).getGreen();
			this.b = ((Color)color).getBlue();
			this.alpha = ((Color)color).getAlpha();	
		}
	}

	public boolean isPicked() {
		return picked;
	}

	public void pick(float cx, float cy) {

		final float distance = PApplet.dist(cx, cy, p.screenX(this.x, this.y, this.z), p.screenY(x, y, z));
		System.out.println("Distance = " + distance);
		if(distance < 200){
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

	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		
	}

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub
		
	}

}

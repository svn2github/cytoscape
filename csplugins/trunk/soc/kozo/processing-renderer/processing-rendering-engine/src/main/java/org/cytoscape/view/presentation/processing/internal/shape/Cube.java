package org.cytoscape.view.presentation.processing.internal.shape;

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

import processing.core.PApplet;
import processing.core.PGraphics;
import toxi.geom.Vec3D;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 *
 */
public class Cube extends Vec3D implements CyDrawable, Pickable {

	private static final long serialVersionUID = -3971892445041605908L;
	private static final String DISPLAY_NAME = "Cube";
	
	private static final int DEF_SIZE = 20;
	
	private static final int OFFSET = 10;

	
	private boolean picked;
	private Set<Class<?>> compatibleDataType;
	
	private final VisualLexicon lexicon;
	
	private PApplet p;
	
	private float size;
	private int r, g, b, alpha;
	
	private final List<CyDrawable> children;
	
	
	private Map<VisualProperty<?>, Object> fieldMap;
	
	public Cube(PApplet parent, VisualLexicon lexicon) {
		super();
		this.p = parent;
		this.lexicon = lexicon;
		this.picked = false;
		
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
		p.pushMatrix();
		p.noStroke();
		p.translate(x, y, z);
		p.fill(r, g, b, alpha);
		p.box(size);
		p.popMatrix();
		
		for(CyDrawable child: children)
			child.draw();
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}

	public void setContext(View<?> viewModel) {
		
		// Pick compatible lexicon only.
		this.x = viewModel.getVisualProperty(NODE_X_LOCATION).floatValue();
		this.y = viewModel.getVisualProperty(NODE_Y_LOCATION).floatValue();		
		this.z = viewModel.getVisualProperty(NODE_Z_LOCATION).floatValue();
		
		System.out.println("Z location = " + this.z);
		
		this.size = viewModel.getVisualProperty(NODE_X_SIZE).floatValue();
		if(size <= 0)
			size = DEF_SIZE;
		
		Paint color = viewModel.getVisualProperty(NODE_COLOR);
		Double opacity = viewModel.getVisualProperty(NODE_OPACITY);
		if(picked) {
			this.r = 0;
			g = 250;
			b = 0;
			alpha = 255;
		}else if(color instanceof Color) {
			this.r = ((Color)color).getRed();
			this.g = ((Color)color).getGreen();
			this.b = ((Color)color).getBlue();
			//this.alpha = opacity.intValue();		
			this.alpha = 100;
		}
		
//		String text = viewModel.getVisualProperty(NODE_LABEL);
//		if(text != null || text.length() != 0) {
//			children.add(new Text(p, lexicon));
//		}
		
		
		
		// Set values for children
		for(CyDrawable child: children)
			child.setContext(viewModel);
	}
	
	public void setContext(View<?> viewModel, VisualProperty<?> vp) {
		// If the VP is not in the context, ignore
		if(lexicon.getAllVisualProperties().contains(vp) == false) return;
		
		// Extract value for the visual property
		Object value = viewModel.getVisualProperty(vp);
		
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

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub
		this.children.add(child);
	}

	

}

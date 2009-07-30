package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;

import processing.core.PApplet;
import toxi.geom.Vec3D;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 *
 */
public class Cube extends Vec3D implements CyDrawable {

	private static final long serialVersionUID = -3971892445041605908L;
	private static final String DISPLAY_NAME = "Cube";
	
	private static final int DEF_SIZE = 20;
	
	private Set<Class<?>> compatibleDataType;
	
	private final VisualLexicon lexicon;
	
	private PApplet p;
	
	private float size;
	private int r, g, b, alpha;
	 
	
	public Cube(PApplet parent, VisualLexicon lexicon) {
		super();
		this.p = parent;
		this.lexicon = lexicon;
		
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
		p.translate(x, y, z);
		p.fill(r, g, b, alpha);
		//p.fill(204, 102, 0);
		p.box(size);
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
		this.z = viewModel.getVisualProperty(NODE_Z_LOCATION).floatValue();
		
		this.size = viewModel.getVisualProperty(NODE_X_SIZE).floatValue();
		if(size <= 0)
			size = DEF_SIZE;
		
		Paint color = viewModel.getVisualProperty(NODE_COLOR);
		if(color instanceof Color) {
			this.r = ((Color)color).getRed();
			this.g = ((Color)color).getGreen();
			this.b = ((Color)color).getBlue();
			//this.alpha = ((Color)color).getAlpha();
			this.alpha = 90;
			System.out.println("Color of node = " + r +", " + g + ", " + b + ", alpha = " + alpha);
		}
		
		
	}

}

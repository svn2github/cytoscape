package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.media.opengl.GL;
import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
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
	
	private ProcessingVisualLexicon lexicon;
	
	private Set<Class<?>> compatibleDataType;
	
	private static VisualLexicon sub;
	
	static {
		sub = new BasicVisualLexicon();
		sub.addVisualProperty(NODE_COLOR);
		sub.addVisualProperty(NODE_X_LOCATION);
		sub.addVisualProperty(NODE_Y_LOCATION);
		sub.addVisualProperty(NODE_Z_LOCATION);
	}
	
	
	private PApplet p;
	private GL gl;
	private PGraphicsOpenGL pgl;
	
	
	private float size;
	private float r, g, b, alpha;
	 
	
	public Cube(PApplet parent) {
		super();
		this.p = parent;
		size = 20;
	}
	
	public Cube(ProcessingVisualLexicon lexicon) {
		
		this.lexicon = lexicon;
		compatibleDataType = new HashSet<Class<?>>();
		compatibleDataType.add(CyNode.class);
		
		this.lexicon.registerSubLexicon(this.getClass(), sub);
		
		r = 100;
		g = 200;
		b = 100;
		alpha = 150;
		
		this.rotateX(p.random(p.PI));
		this.rotateY(p.random(p.PI));
		this.rotateZ(p.random(p.PI));
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

	public VisualLexicon getCompatibleVisualProperties() {
		return lexicon.getSubLexicon(this.getClass());
	}

	public void draw() {
		p.pushMatrix();
		p.translate(x, y, z);
		p.box(size);
		p.popMatrix();
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}

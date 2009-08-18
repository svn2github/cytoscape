package org.cytoscape.view.presentation.processing.internal.drawable;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.*;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_SELECTED_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;

import java.awt.Color;
import java.awt.Paint;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.Pickable;
import org.cytoscape.view.presentation.processing.internal.ProcessingNetworkRenderer;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PGraphics3D;
import processing.core.PMatrix3D;
import processing.opengl.PGraphicsOpenGL;
import toxi.geom.Vec3D;

public class Text extends Vec3D implements CyDrawable, Pickable {

	private static final int DEF_SIZE = 20;

	private static final int OFFSET = 10;

	private boolean picked;
	private Set<Class<?>> compatibleDataType;

	private final VisualLexicon lexicon;

	private PApplet p;

	private float size;
	private float r, g, b, alpha;
	private String text;

	private float offsetX = 30;
	private float offsetY = 0;
	private float offsetZ = 30;

	private PFont font;

	private Map<VisualProperty<?>, Object> fieldMap;

	private Color selected;

	public Text(PApplet parent, VisualLexicon lexicon) {
		super();
		this.p = parent;
		this.lexicon = lexicon;
		this.picked = false;

		compatibleDataType = new HashSet<Class<?>>();
		compatibleDataType.add(CyNode.class);
		compatibleDataType.add(CyEdge.class);
	}

	public Set<Class<?>> getCompatibleModels() {
		return compatibleDataType;
	}

	public Icon getIcon(int width, int height) {
		// TODO Implement icon renderer
		return null;
	}

	public void draw() {

//		PGraphicsOpenGL graphics = (PGraphicsOpenGL) p.g;
//		PMatrix3D m_inv = graphics.camera;
//
//		p.applyMatrix(
//				1, 0, 0, m_inv.m03, 
//				0, 1, 0, m_inv.m13, 
//				0, 0, 1, m_inv.m23, 
//				m_inv.m30, m_inv.m31, m_inv.m32, m_inv.m33);
//
		p.fill(r, g, b, alpha);
		p.textFont(((ProcessingNetworkRenderer)p).getDefaultFont(), size);
		p.text(text, x, y, z);
	}

	public List<CyDrawable> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setContext(View<?> viewModel) {
		
		this.picked = ((CyNode)viewModel.getSource()).attrs().get("selected", Boolean.class);
		this.selected = (Color) viewModel.getVisualProperty(NODE_SELECTED_COLOR);

		this.text = viewModel.getVisualProperty(NODE_LABEL);
		offsetX = viewModel.getVisualProperty(NODE_X_SIZE).floatValue()/2 + 15;

		this.x = viewModel.getVisualProperty(NODE_X_LOCATION).floatValue()
				+ offsetX;
		this.y = viewModel.getVisualProperty(NODE_Y_LOCATION).floatValue()
				+ offsetY;
		this.z = viewModel.getVisualProperty(NODE_Z_LOCATION).floatValue()
				+ offsetZ;

		this.size = viewModel.getVisualProperty(NODE_LABEL_SIZE).floatValue();

		final Paint color = viewModel.getVisualProperty(NODE_LABEL_COLOR);
		
		if (picked) {
			this.r = selected.getRed();
			this.g = selected.getGreen();
			this.b = selected.getBlue();
			this.alpha = 200f;
		} else if (color instanceof Color) {
			this.r = ((Color) color).getRed();
			this.g = ((Color) color).getGreen();
			this.b = ((Color) color).getBlue();
			this.alpha = viewModel.getVisualProperty(NODE_LABEL_OPACITY).floatValue();
		}
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

		
	}

	public void addChild(CyDrawable child) {
		// TODO Auto-generated method stub

	}

	public void setDetailFlag(boolean flag) {
		// TODO Auto-generated method stub
		
	}

}

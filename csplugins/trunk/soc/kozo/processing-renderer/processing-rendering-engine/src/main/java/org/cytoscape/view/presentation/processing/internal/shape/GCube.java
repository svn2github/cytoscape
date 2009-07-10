package org.cytoscape.view.presentation.processing.internal.shape;

import gestalt.context.GLContext;
import gestalt.impl.jogl.shape.JoglCube;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 *
 */
public class GCube extends JoglCube implements CyDrawable {

	private static final long serialVersionUID = -3971892445041605908L;
	private static final String DISPLAY_NAME = "Cube";

	private final Collection<Class<?>> compatibleDataType;

	public GCube() {
		super();
		compatibleDataType = new ArrayList<Class<?>>();
		compatibleDataType.add(CyNode.class);
	}

	public void draw(GLContext context) {
		super.draw(context);
	}

	public Collection<Class<?>> getCompatibleModels() {
		return compatibleDataType;
	}

	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	public Icon getIcon(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisualLexicon getLexicon() {
		// TODO Auto-generated method stub
		return null;
	}

}

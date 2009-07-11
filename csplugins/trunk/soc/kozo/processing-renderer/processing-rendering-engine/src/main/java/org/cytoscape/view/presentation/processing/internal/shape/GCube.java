package org.cytoscape.view.presentation.processing.internal.shape;

import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_LOCATION;
import gestalt.context.GLContext;
import gestalt.impl.jogl.shape.JoglCube;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.processing.CyDrawable;
import org.cytoscape.view.presentation.processing.visualproperty.ProcessingVisualLexicon;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;

/**
 * Wrapper for JOGL-based Cube object.
 * 
 * @author kono
 *
 */
public class GCube extends JoglCube implements CyDrawable {

	private static final long serialVersionUID = -3971892445041605908L;
	private static final String DISPLAY_NAME = "Cube";
	
	private ProcessingVisualLexicon lexicon;
	
	private List<Class<?>> compatibleDataType;
	
	public GCube(ProcessingVisualLexicon lexicon) {
		super();
		this.lexicon = lexicon;
		compatibleDataType = new ArrayList<Class<?>>();
		compatibleDataType.add(CyNode.class);
		VisualLexicon sub = new BasicVisualLexicon();
		
		sub.addVisualProperty(NODE_COLOR);
		sub.addVisualProperty(NODE_X_LOCATION);
		sub.addVisualProperty(NODE_Y_LOCATION);
		sub.addVisualProperty(NODE_Z_LOCATION);
		
		this.lexicon.registerSubLexicon(this, sub);
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
		// TODO Implement icon renderer
		return null;
	}

	public VisualLexicon getCompatibleVisualProperties() {
		return lexicon.getSubLexicon(this);
	}

}

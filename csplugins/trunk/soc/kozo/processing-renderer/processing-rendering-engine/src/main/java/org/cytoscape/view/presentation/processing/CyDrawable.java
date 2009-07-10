package org.cytoscape.view.presentation.processing;

import java.util.Collection;

import javax.swing.Icon;

import org.cytoscape.view.model.VisualLexicon;

/**
 * 
 * Defines shape of object rendered in Processing.
 * 
 * @author kono, kozo
 * @version 0.0.1
 * 
 * 
 */
public interface CyDrawable {

	/**
	 * Name of this shape, such as ellipse, rectangle, triangle, etc. This is
	 * immutable.
	 * 
	 * @return Name of shape as string
	 * 
	 */
	public String getDisplayName();
	
	public Collection<Class<?>> getCompatibleModels();
	
	public Icon getIcon(int width, int height);
	
	public VisualLexicon getLexicon();
	
}

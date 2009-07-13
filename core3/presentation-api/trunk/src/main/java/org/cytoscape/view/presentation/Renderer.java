package org.cytoscape.view.presentation;

import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

/**
 * Generic interface for all presentations.
 * Represents a drawing function for a given data model.
 * 
 *  This can be used for any view model, including networks and attributes.
 */
public interface Renderer<T extends View<?>> {
	
	/**
	 * Returns backend View data structure.
	 * Model can be extracted from the View object.
	 * 
	 * @return view model.
	 */
	public T getViewModel();
	
	/**
	 * Return a Visual Lexicon which contains compatible Visual Properties for
	 * this renderer.
	 * 
	 */
	public VisualLexicon getVisualLexicon();

	/**
	 * 
	 * 
	 * @param props
	 *            DOCUMENT ME!
	 */
	public void setProperties(Properties props);

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Properties getProperties();

	/**
	 * For export image function.
	 * 
	 * @return DOCUMENT ME!
	 */
	public Printable getPrintable();

	/**
	 * Render image from the current view model state.
	 * 
	 * @return Image object created from current window.
	 */
	public Image getImage(int width, int height);

	/**
	 * For a given Visual Property, render an Icon based on the default value of
	 * the Visual Property.
	 * 
	 * @param vp
	 *            Visual Property.
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @exception IllegalArgumentException
	 *                if vp is not in the lexicon.
	 */
	public Icon getDefaultIcon(VisualProperty<?> vp);
}

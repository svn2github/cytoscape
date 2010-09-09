package org.cytoscape.view.presentation;

import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;

/**
 * RenderingEngine is an interface for all visualizers. For a given view-model
 * it renders actual view on display, documents, etc.
 * 
 * @author kono
 * @since Cytoscape 3.0
 * 
 * @param <T>
 *            source data object to be visualized. For now we have only one
 *            implementation for CyNetwork, but it can be anything, including
 *            CyTable.
 */
public interface RenderingEngine<T> {

	/**
	 * Returns View Model being rendered.
	 * 
	 * @return view model.  This is an immutable object.
	 */
	public View<T> getViewModel();

	
	/**
	 * Provide all compatible Visual Properties as a Visual Lexicon.
	 * 
	 * @return Visual Lexicon of this rendering engine.
	 */
	public VisualLexicon getVisualLexicon();
	

	/**
	 * Set prop values to rendering engine, like LOD
	 */
	public void setProperties(final Properties props);
	

	/**
	 * Get current rendering engine dependent properties, like LOD
	 */
	public Properties getProperties();
	

	/**
	 * For export image function.
	 * 
	 * @return A Printable object suitable for submission to a printer.
	 */
	public Printable createPrintable();
	

	/**
	 * Render image from the current view model state.
	 * 
	 * @return Image object created from current window.
	 */
	public Image createImage(final int width, final int height);
	

	/**
	 * For a given Visual Property, render an Icon based on the default value of
	 * the Visual Property.
	 * 
	 * @param vp
	 *            Visual Property.
	 * 
	 * @return Rendered icon for the Visual Property.
	 * 
	 * @exception IllegalArgumentException
	 *                if vp is not in the lexicon.
	 */
	public Icon createIcon(final VisualProperty<?> vp);

}

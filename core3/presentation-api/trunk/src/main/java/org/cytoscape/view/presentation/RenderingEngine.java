package org.cytoscape.view.presentation;

import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;


public interface RenderingEngine<T> {
	
	/**
	 * Returns backend View Models for this presentation.
	 * 
	 * @return view models.
	 */
	public View<T> getViewModel();
	
	
	// TODO: should view model immutable?
	public void setViewModel(final View<T> viewModel);
	
	/**
	 * Provide all compatible Visual Properties as a Visual Lexicon.
	 * 
	 * @return Visual Lexicon of this rendering engine.
	 */
	public VisualLexicon getVisualLexicon();
	

	/**
	 * Rendering engine dependent properties, like LOD
	 */
	public void setProperties(Properties props);
	public Properties getProperties();

	/**
	 * For export image function.
	 * 
	 * @return DOCUMENT ME!
	 */
	public Printable createPrintable();
	

	/**
	 * Render image from the current view model state.
	 * 
	 * @return Image object created from current window.
	 */
	public Image createImage(int width, int height);
	

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
	public Icon createIcon(VisualProperty<?> vp);
	
}

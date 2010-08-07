package org.cytoscape.ding.impl;

import java.awt.Image;
import java.awt.print.Printable;
import java.util.Properties;

import javax.swing.Icon;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * 
 * @author kono
 *
 */
public class NavigationRenderingEngine implements RenderingEngine<CyNetwork> {
	
	private final View<CyNetwork> viewModel;
	
	public NavigationRenderingEngine(final View<CyNetwork> viewModel) {
		this.viewModel = viewModel;
	}
	

	@Override
	public View<CyNetwork> getViewModel() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public VisualLexicon getVisualLexicon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperties(Properties props) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Properties getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Printable createPrintable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image createImage(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon createIcon(VisualProperty<?> vp) {
		// TODO Auto-generated method stub
		return null;
	}

}

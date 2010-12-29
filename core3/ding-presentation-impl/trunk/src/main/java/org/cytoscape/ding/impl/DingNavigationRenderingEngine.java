package org.cytoscape.ding.impl;

import java.awt.Component;
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
 * Wrapper for Navigation View
 *
 */
public class DingNavigationRenderingEngine implements RenderingEngine<CyNetwork> {
	
	private final DGraphView dgv;	
	private final Properties props;
	
	private final BirdsEyeView bev;
	
	public DingNavigationRenderingEngine(final Component container, final DGraphView dgv) {
		this.dgv = dgv;
		this.props = new Properties();
		
		bev = new BirdsEyeView(container, dgv);
	}
	

	@Override
	public View<CyNetwork> getViewModel() {
		return dgv.getViewModel();
	}
	

	@Override
	public VisualLexicon getVisualLexicon() {
		return dgv.getVisualLexicon();
	}


	@Override
	public Properties getProperties() {
		return props;
	}

	@Override
	public Printable createPrintable() {
		return dgv.createPrintable();
	}

	@Override
	public Image createImage(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <V> Icon createIcon(VisualProperty<V> vp, V value, int w, int h) {
		// TODO Auto-generated method stub
		return null;
	}

}

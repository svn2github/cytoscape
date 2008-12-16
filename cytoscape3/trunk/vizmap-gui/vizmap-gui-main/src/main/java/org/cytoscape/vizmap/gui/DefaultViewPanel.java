package org.cytoscape.vizmap.gui;

import java.awt.Component;

import org.cytoscape.view.GraphView;

public interface DefaultViewPanel {

	/**
	 * Get dummy network view.
	 * Dummy network view is a network displayed on the default view editor.
	 * Usually it is a network with an edge and two nodes.
	 * 
	 * @return DOCUMENT ME!
	 */
	public GraphView getView();

	
	/**
	 * Returns rendrer's canvas.
	 * For example, Cytoscape default rendering engine returns InnerCanvas, 
	 * and Processing returns PApplet.
	 * 
	 * @return
	 */
	public Component getRendererComponent();

}
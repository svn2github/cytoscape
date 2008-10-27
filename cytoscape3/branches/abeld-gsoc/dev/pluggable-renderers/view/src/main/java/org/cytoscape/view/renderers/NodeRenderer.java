package org.cytoscape.view.renderers;

import java.awt.Graphics2D;
import java.util.Collection;

import org.cytoscape.view.NodeView;
import org.cytoscape.view.VisualProperty;

import cytoscape.render.stateful.NodeDetails;

public interface NodeRenderer extends Renderer{
	/**
	 * 
	 */
	public void render(Graphics2D graphics, NodeDetails nodeDetails, float[] position, int node, NodeView nodeView);
	
	/**
	 * Draw a preview image on canvas at given place (using some default NodeDetails that the renderer can make up)
	 */
	public void generatePreview(Graphics2D graphics, float[] position);
	
	/**
	 * Return a list of visual attributes this renderer can use
	 */
	public Collection<VisualProperty> supportedVisualAttributes();
	
	/** Returns user-friendly name */
	public String name();
}

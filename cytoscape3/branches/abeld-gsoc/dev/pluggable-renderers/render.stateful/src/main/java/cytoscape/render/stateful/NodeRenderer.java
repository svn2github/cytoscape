package cytoscape.render.stateful;

import java.awt.Graphics2D;
import org.cytoscape.view.NodeView;

public interface NodeRenderer {
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
	public void supportedVisualAttributes();
}

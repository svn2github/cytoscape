package cytoscape;

//import cytoscape.render.immed.GraphGraphics;
import java.awt.*;
import java.awt.geom.*;
import java.util.Collection;
import java.util.HashSet;

import org.cytoscape.view.NodeView;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.renderers.NodeRenderer;

import cytoscape.render.stateful.NodeDetails;

public class TrivialRenderer implements NodeRenderer {
	private final Ellipse2D.Double m_ellp2d = new Ellipse2D.Double();
	private String name;
	
	public TrivialRenderer(String name){
		this.name = name;
	}
	
	/** Returns user-friendly name */
	public String name(){
		return this.name;
	}

	/**
	 * Draw a preview image on canvas at given place (using some default NodeDetails that the renderer can make up)
	 */
	public void generatePreview(Graphics2D graphics, float[] position){
		// TODO
	}
	
	/**
	 * Return a list of visual attributes this renderer can use
	 */
	public Collection<VisualProperty>  supportedVisualAttributes(){
		return new HashSet<VisualProperty>(); // no VisualPropeties defined by this Renderer
	}

	public void render(Graphics2D m_g2d, NodeDetails nodeDetails, float[] floatBuff1, int node, NodeView nodeView) {
		System.out.println("rendering by: "+name);
		
		// TODO Auto-generated method stub
		float xMin = floatBuff1[0]; 
		float yMin =  floatBuff1[1];
		float xMax = floatBuff1[2];
		float yMax = floatBuff1[3];
		
		Paint fillPaint; 
		if (nodeView.isSelected()) {
			fillPaint = Color.cyan;
		} else {
			fillPaint = Color.darkGray;
		}

		m_g2d.setPaint(fillPaint);
		m_ellp2d.setFrame(xMin, yMin, xMax - xMin, yMax - yMin);
		m_g2d.fill(m_ellp2d);
	}
}

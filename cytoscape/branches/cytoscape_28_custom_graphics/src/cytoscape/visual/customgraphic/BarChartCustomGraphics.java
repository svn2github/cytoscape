package cytoscape.visual.customgraphic;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import cytoscape.Cytoscape;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;
import ding.view.DNodeView;

public class BarChartCustomGraphics implements CyCustomGraphics {
	
	final String attrName;
	
	private static final Color DEF_COLOR = new Color(0, 180, 30, 100);
	
	public BarChartCustomGraphics(final String attrName) {
		this.attrName = attrName;
	}

	@Override
	public void applyGraphics(NodeView nv) {
		if(!(nv instanceof DNodeView)) return;

		Object value = Cytoscape.getNodeAttributes().getAttribute(nv.getNode().getIdentifier(), attrName);
		if(value == null || !(value instanceof Number)) return;
		
		final DNodeView dv = (DNodeView) nv;
		while(dv.getNumCustomGraphics() != 0) {
			CustomGraphic custom = dv.customGraphicIterator().next();
			dv.removeCustomGraphic(custom);
		}
		

		Rectangle2D bound = null;
		Paint paint = null;
		
		bound = new Rectangle2D.Double(10, 0, 20, Math.abs(Double.parseDouble(value.toString())*100));
		paint = DEF_COLOR;
		
		final CustomGraphic custom = new CustomGraphic(bound, paint, NodeDetails.ANCHOR_EAST);
		dv.addCustomGraphic(custom);
	}

}

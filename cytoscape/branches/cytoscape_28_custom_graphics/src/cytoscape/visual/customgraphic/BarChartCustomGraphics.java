package cytoscape.visual.customgraphic;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import cytoscape.Cytoscape;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;
import ding.view.DNodeView;

public class BarChartCustomGraphics implements CyCustomGraphics<CustomGraphic> {
	
	final String attrName;
	
	private static final Color DEF_COLOR = new Color(0, 180, 30, 100);
	
	public BarChartCustomGraphics(final String attrName) {
		this.attrName = attrName;
	}
	

//	@Override
//	public CustomGraphic getCustomGraphic() {
//		Rectangle2D bound = new Rectangle2D.Double(10, 0, 20, Math.abs(Double.parseDouble(value.toString())*100));
//		Paint paint = DEF_COLOR;
//		
//		return new CustomGraphic(bound, paint, NodeDetails.ANCHOR_EAST);
//	}

	@Override
	public Collection<CustomGraphic> getCustomGraphics() {
		return null;
	}


	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

}

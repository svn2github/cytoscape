package cytoscape.visual.customgraphic;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.util.Collection;

import cytoscape.Cytoscape;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;
import ding.view.DNodeView;


/**
 * Proof of concept code.  Generate images dynamically from attributes.
 * 
 * @author kono
 *
 */
public class DegreeCircleCustomGraphics implements CyCustomGraphics<CustomGraphic> {

	private static final Color DEF_COLOR = new Color(0, 30, 190, 100);
	private String attrName;

	public DegreeCircleCustomGraphics(final String attrName) {
		this.attrName = attrName;
	}

//	@Override
//	public void applyGraphics(NodeView nv) {
//		// TODO Auto-generated method stub
//
//		if (!(nv instanceof DNodeView))
//			return;
//
//		Object value = Cytoscape.getNodeAttributes().getAttribute(
//				nv.getNode().getIdentifier(), attrName);
//		if (value == null || !(value instanceof Number))
//			return;
//
//		final DNodeView dv = (DNodeView) nv;
//		while (dv.getNumCustomGraphics() != 0) {
//			CustomGraphic custom = dv.customGraphicIterator().next();
//			dv.removeCustomGraphic(custom);
//		}
//
//		
//	}

	@Override
	public Collection<CustomGraphic> getCustomGraphics() {
		
//
//		final double size = Math.abs(Double.parseDouble(value.toString()) * 40);
//
//		Shape bound = new java.awt.geom.Ellipse2D.Double(-size / 2, -size / 2, size,
//				size);
//		Paint paint = DEF_COLOR;
//
//		final CustomGraphic custom = new CustomGraphic(bound, paint,
//				NodeDetails.ANCHOR_CENTER);

		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}

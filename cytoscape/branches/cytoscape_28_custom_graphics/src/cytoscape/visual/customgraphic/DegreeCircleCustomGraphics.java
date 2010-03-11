package cytoscape.visual.customgraphic;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;

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
public class DegreeCircleCustomGraphics implements CyCustomGraphics {

	private static final Color DEF_COLOR = new Color(0, 30, 190, 100);
	private String attrName;

	public DegreeCircleCustomGraphics(final String attrName) {
		this.attrName = attrName;
	}

	@Override
	public void applyGraphics(NodeView nv) {
		// TODO Auto-generated method stub

		if (!(nv instanceof DNodeView))
			return;

		Object value = Cytoscape.getNodeAttributes().getAttribute(
				nv.getNode().getIdentifier(), attrName);
		if (value == null || !(value instanceof Number))
			return;

		final DNodeView dv = (DNodeView) nv;
		while (dv.getNumCustomGraphics() != 0) {
			CustomGraphic custom = dv.customGraphicIterator().next();
			dv.removeCustomGraphic(custom);
		}

		Shape bound = null;
		Paint paint = null;

		final double w = dv.getWidth();
		final double h = dv.getHeight();

		final double size = Math.abs(Double.parseDouble(value.toString()) * 40);

		bound = new java.awt.geom.Ellipse2D.Double(-size / 2, -size / 2, size,
				size);
		paint = DEF_COLOR;

		final CustomGraphic custom = new CustomGraphic(bound, paint,
				NodeDetails.ANCHOR_CENTER);
		dv.addCustomGraphic(custom);

	}

}

package cytoscape.visual.properties;

import giny.view.NodeView;
import giny.view.ObjectPosition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.Icon;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.customgraphic.NullCustomGraphics;
import cytoscape.visual.ui.ObjectPlacerGraphic;
import cytoscape.visual.ui.icon.NodeIcon;
import ding.view.DNodeView;
import ding.view.ObjectPositionImpl;

public class NodeCustomGraphicsPositionProp extends AbstractVisualProperty {

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_CUSTOM_GRAPHICS_POSITION;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param labelPos
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Icon getIcon(Object value) {
		int size = 55;

		final BufferedImage bi = new BufferedImage(size, size,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();

		ObjectPlacerGraphic lp = new ObjectPlacerGraphic(
				(ObjectPosition) value, size, false, "Custom Graphics", null,
				null);
		lp.paint(g2);

		NodeIcon icon = new NodeIcon() {

			private static final long serialVersionUID = -3190338664704873605L;

			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 0));
				super.paintIcon(c, g, x, y);
				g2d.drawImage(bi, 10, -5, null);
			}
		};

		return icon;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nv
	 *            DOCUMENT ME!
	 * @param o
	 *            DOCUMENT ME!
	 */
	public void applyToNodeView(NodeView nv, Object o,
			VisualPropertyDependency dep) {

		// This implementation is for Ding only.
		if ((o == null) || (nv == null) || o instanceof ObjectPosition == false
				|| o instanceof NullCustomGraphics
				|| nv instanceof DNodeView == false)
			return;

		final ObjectPosition p = (ObjectPosition) o;
		final DNodeView dv = (DNodeView) nv;
		
		final Iterator<CustomGraphic> itr = dv.customGraphicIterator();
		int i = 0;
		while(itr.hasNext()) {
			final CustomGraphic cg = itr.next();
			dv.setCustomGraphicsPosition(cg, p);
			System.out.println(i + " = " + dv.getLabel().getText() + ": CG Position = "
				+ dv.getCustomGraphicsPosition(cg).toString());
			i++;
		}
//		dv.setCustomGraphicsPosition(cg, p);
		
		//calc.calculate(graphicsPosition, dv);

		// Label nodelabel = nv.getLabel();
		//		 
		//		
		// Position newTextAnchor = labelPosition.getAnchor();
		//		
		// if (nodelabel.getTextAnchor() != newTextAnchor.getGinyConstant())
		// nodelabel.setTextAnchor(newTextAnchor.getGinyConstant());
		//		
		// Position newJustify = labelPosition.getJustify();
		//		
		// if (nodelabel.getJustify() != newJustify.getGinyConstant())
		// nodelabel.setJustify(newJustify.getGinyConstant());
		//		
		// Position newNodeAnchor = labelPosition.getTargetAnchor();
		//		
		// if (nv.getNodeLabelAnchor() != newNodeAnchor.getGinyConstant())
		// nv.setNodeLabelAnchor(newNodeAnchor.getGinyConstant());
		//		
		// double newOffsetX = labelPosition.getOffsetX();
		//		
		// if (nv.getLabelOffsetX() != newOffsetX)
		// nv.setLabelOffsetX(newOffsetX);
		//		
		// double newOffsetY = labelPosition.getOffsetY();
		//		
		// if (nv.getLabelOffsetY() != newOffsetY)
		// nv.setLabelOffsetY(newOffsetY);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return new ObjectPositionImpl();
	}
}

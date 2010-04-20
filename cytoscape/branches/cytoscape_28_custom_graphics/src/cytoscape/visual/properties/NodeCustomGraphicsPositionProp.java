package cytoscape.visual.properties;

import giny.view.NodeView;
import giny.view.ObjectPosition;
import giny.view.Position;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

import javax.swing.Icon;

import cytoscape.Cytoscape;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.ObjectPositionImpl;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.customgraphic.CustomGraphicsPositionCalculator;
import cytoscape.visual.customgraphic.NullCustomGraphics;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.ui.ObjectPlacerGraphic;
import cytoscape.visual.ui.icon.NodeIcon;
import ding.view.DNodeView;

public class NodeCustomGraphicsPositionProp extends AbstractVisualProperty {

	private CustomGraphicsPositionCalculator calc;

	public NodeCustomGraphicsPositionProp() {
		this.calc = new CustomGraphicsPositionCalculator();
	}

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

		final ObjectPosition graphicsPosition = (ObjectPosition) o;
		final DNodeView dv = (DNodeView) nv;
		// final String nodeID = nv.getNode().getIdentifier();
		System.out.println(dv.getLabel() + ": Custom Graphics Position = "
				+ graphicsPosition.toString());
		
		
		calc.calculate(graphicsPosition, dv);

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

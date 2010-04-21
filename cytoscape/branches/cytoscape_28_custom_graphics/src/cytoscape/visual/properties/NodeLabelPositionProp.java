/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.visual.properties;

import giny.view.NodeView;
import giny.view.ObjectPosition;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.ui.ObjectPlacerGraphic;
import cytoscape.visual.ui.icon.NodeIcon;
import ding.view.DNodeView;
import ding.view.ObjectPositionImpl;

/**
 *
 */
public class NodeLabelPositionProp extends AbstractVisualProperty {
	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_LABEL_POSITION;
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
				(ObjectPosition) value, size, false, "Label", null, null);
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
		if ((o == null) || (nv == null) || nv instanceof DNodeView == false
				|| o instanceof ObjectPosition == false)
			return;

		final DNodeView dv = (DNodeView) nv;
		dv.setPosition((ObjectPosition) o);

		System.out.println(dv.getLabel() + ": Node Label Position = "
				+ dv.getPosition().toString());

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

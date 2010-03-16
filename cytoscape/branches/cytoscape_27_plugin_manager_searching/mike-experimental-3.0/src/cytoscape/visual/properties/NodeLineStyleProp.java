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

import cytoscape.Cytoscape;

import cytoscape.visual.*;

import cytoscape.visual.parsers.*;

import cytoscape.visual.ui.icon.*;

import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;


/**
 *
 */
public class NodeLineStyleProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_LINE_STYLE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(final Object value) {
		final NodeIcon icon = new NodeIcon() {
	private final static long serialVersionUID = 1202339876386039L;
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 0));
				super.paintIcon(c, g, x, y);

				final BasicStroke stroke = (BasicStroke) ((LineStyle) value)
				                                                                    .getStroke(((Number) VisualPropertyType.NODE_LINE_WIDTH
				                                                                                .getDefault(Cytoscape.getVisualMappingManager()
				                                                                                                     .getVisualStyle()))
				                                                                               .floatValue());
				g2d.setStroke(stroke);
				g2d.setColor(Color.DARK_GRAY);
				g2d.translate(15, 4);
				g2d.draw(super.shape);
				g2d.translate(-15, -4);
				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
				g2d.setColor(new Color(10, 10, 10, 45));
				g2d.drawString(value.toString(), c.getX() + 12,
				               (int) ((c.getHeight() / 2) + 3));
				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
			}
		};

		return icon;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<Object, Icon> getIconSet() {
		return LineStyle.getIconSet();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nv DOCUMENT ME!
	 * @param o DOCUMENT ME!
	 */
	public void applyToNodeView(NodeView nv, Object o) {
		if ((o == null) || (nv == null))
			return;

		if (((LineStyle) o).getDashDef() != (((BasicStroke) nv.getBorder()).getDashArray())) {
			nv.setBorder(((LineStyle) o).getStroke(nv.getBorderWidth()));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param props DOCUMENT ME!
	 * @param baseKey DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object parseProperty(Properties props, String baseKey) {
		String s = props.getProperty(VisualPropertyType.NODE_LINE_STYLE.getDefaultPropertyKey(baseKey));

		if (s != null)
			return (new LineStyleParser()).parseLineStyle(s);
		else

			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return LineStyle.SOLID;
	}
}

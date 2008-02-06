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

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.ui.icon.NodeIcon;

import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import java.util.Properties;

import javax.swing.Icon;


/**
 *
 */
public class NodeToolTipProp extends AbstractVisualProperty {
	
	private StringBuilder buf = new StringBuilder();
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_TOOLTIP;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(final Object value) {
		final NodeIcon icon = new NodeIcon() {
	private final static long serialVersionUID = 1202339876422546L;
			public void paintIcon(Component c, Graphics g, int x, int y) {
				super.setColor(new Color(10, 10, 10, 25));
				super.paintIcon(c, g, x, y);
				g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
				g2d.setColor(Color.DARK_GRAY);

				String defLabel = value.toString();

				if (defLabel.length() > 15) {
					defLabel = defLabel.substring(0, 14) + "...";
				}

				g2d.drawString(defLabel, c.getX() + 7, (int) ((c.getHeight() / 2)));
				g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
			}
		};

		icon.setBottomPadding(-2);

		return icon;
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
		if(((String)o).startsWith("<html>")) {
			nv.setToolTip((String) o);
			return;
		}
		
		buf = new StringBuilder();
		buf.append("<html><body bgcolor=\"white\"><Div Align=\"center\"><Font Size=\"4\">");
		buf.append(((String)o).replaceAll("\\n", "<br>"));
		buf.append("</Font></div></body></html>");
		nv.setToolTip(buf.toString());
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
		String s = props.getProperty(VisualPropertyType.NODE_TOOLTIP.getDefaultPropertyKey(baseKey));

		if (s != null)
			return s;
		else
			return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getDefaultAppearanceObject() {
		return "";
	}
}

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

import cytoscape.visual.*;
import cytoscape.visual.parsers.*;
import cytoscape.visual.ui.icon.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import giny.view.Label;
import giny.view.NodeView;
import java.util.Properties;
import javax.swing.Icon;


/**
 *
 */
public class NodeFontFaceProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.NODE_FONT_FACE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getDefaultIcon() {
		return new NodeIcon() {
				public void paintIcon(Component c, Graphics g, int x, int y) {
					super.paintIcon(c, g, x, y);
					g2d.drawString(((Font) getDefault()).getFontName(), c.getX() + 10,
					               (int) (shape.getBounds2D().getCenterY()) + 5);
					g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
					g2d.setColor(new Color(10, 10, 10, 30));
					g2d.drawString("A", c.getX() + 10, (int) (shape.getBounds2D().getMaxY()) + 5);
					g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
				}
			};
	}

    public void applyToNodeView(NodeView nv, Object o) {
        if ( o == null || nv == null )
            return;

		Label nodelabel = nv.getLabel();

        if ( !((Font)o).equals(nodelabel.getFont()) )
            nodelabel.setFont((Font)o);
    }

    public Object parseProperty(Properties props, String baseKey) {
        String s = props.getProperty(
            VisualPropertyType.NODE_FONT_FACE.getDefaultPropertyKey(baseKey) );
        if ( s != null )
            return (new FontParser()).parseFont(s);
        else
            return null;
    }

    public Object getDefaultAppearanceObject() { return new Font(null, Font.PLAIN, 12); }

}

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
import giny.view.EdgeView;
import java.util.Properties;

import javax.swing.Icon;


/**
 *
 */
public class EdgeLabelPositionProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.EDGE_LABEL_POSITION;
	}
	
	public Icon getIcon(final Object value) {
		return null;
	}
/*
    public void applyToEdgeView(EdgeView ev, Object o) {
        if ( o == null || ev == null )
            return;

        Label label = ev.getLabel();
        LabelPosition labelPosition = (LabelPosition)o;

        int newTextAnchor = labelPosition.getLabelAnchor();

        if (label.getTextAnchor() != newTextAnchor)
            label.setTextAnchor(newTextAnchor);

        int newJustify = labelPosition.getJustify();

        if (label.getJustify() != newJustify)
            label.setJustify(newJustify);

        int newNodeAnchor = labelPosition.getTargetAnchor();

        if (ev.getNodeLabelAnchor() != newNodeAnchor)
            ev.setNodeLabelAnchor(newNodeAnchor);

        double newOffsetX = labelPosition.getOffsetX();

        if (ev.getLabelOffsetX() != newOffsetX)
            ev.setLabelOffsetX(newOffsetX);

        double newOffsetY = labelPosition.getOffsetY();

        if (ev.getLabelOffsetY() != newOffsetY)
            ev.setLabelOffsetY(newOffsetY);
    }
*/

    public Object parseProperty(Properties props, String baseKey) {
        String s = props.getProperty(
            VisualPropertyType.EDGE_LABEL_POSITION.getDefaultPropertyKey(baseKey) );
        if ( s != null )
            return (new LabelPositionParser()).parseLabelPosition(s);
        else
            return null;
    }

    public Object getDefaultAppearanceObject() { return new LabelPosition(); }
}

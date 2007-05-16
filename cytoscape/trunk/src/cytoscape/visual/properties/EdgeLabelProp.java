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
import cytoscape.visual.*;
import cytoscape.visual.parsers.*;
import giny.view.Label;
import giny.view.EdgeView;
import java.util.Properties;


/**
 *
 */
public class EdgeLabelProp extends AbstractVisualProperty {
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyType getType() {
		return VisualPropertyType.EDGE_LABEL;
	}

    public void applyToEdgeView(EdgeView ev, Object o) {
        if ( o == null || ev == null )
            return;

        Label label = ev.getLabel();

        if (!((String)o).equals(label.getText()))
            label.setText((String)o);
    }

    public Object parseProperty(Properties props, String baseKey) {
        String s = props.getProperty(
            VisualPropertyType.EDGE_LABEL.getDefaultPropertyKey(baseKey) );
        if ( s != null )
            return s;
        else
            return null;
    }

    public Object getDefaultAppearanceObject() { return ""; }

}

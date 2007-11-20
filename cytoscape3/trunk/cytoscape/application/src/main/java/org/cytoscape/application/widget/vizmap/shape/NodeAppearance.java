/*
 File: NodeAppearance.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

//----------------------------------------------------------------------------
// $Revision: 10555 $
// $Date: 2007-06-21 11:35:22 -0700 (Thu, 21 Jun 2007) $
// $Author: kono $
//----------------------------------------------------------------------------
package org.cytoscape.application.widget.vizmap.shape;

import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_BORDER_COLOR;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_FILL_COLOR;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_FONT_FACE;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_FONT_SIZE;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_HEIGHT;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_LABEL;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_LABEL_COLOR;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_LABEL_POSITION;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_LINETYPE;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_OPACITY;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_SHAPE;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_SIZE;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_TOOLTIP;
import static org.cytoscape.application.widget.vizmap.shape.VisualPropertyType.NODE_WIDTH;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;


/**
 * Objects of this class hold data describing the appearance of a Node.
 */
public class NodeAppearance extends Appearance {

	/**
	 * Constructor.
	 */
	public NodeAppearance() {
		super();
	}

	/**
	 * Clone.
	 */
    public Object clone() {
		NodeAppearance ga = new NodeAppearance();
		ga.copy(this);
		return ga;
	}
}

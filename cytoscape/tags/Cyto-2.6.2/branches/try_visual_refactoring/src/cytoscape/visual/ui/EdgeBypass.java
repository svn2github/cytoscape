

/*
 File: EdgeBypass.java 
 
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

package cytoscape.visual.ui;


import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.VisualMappingManager;

import giny.model.Edge;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.Color;
import java.awt.Font;

public class EdgeBypass extends VizMapBypass {

    public JMenuItem addMenu(Edge e) {
    	graphObj = e;
	attrs = Cytoscape.getEdgeAttributes();

        JMenu menu =new JMenu("Visual Mapping Bypass");
        menu.add( new JMenuItem("Change Edge Visualization") );
        menu.addSeparator();
	
	menu.add( getMenuItem("Color", "edge.color", Color.class) ); 
	menu.add( getMenuItem("Line Type", "edge.lineType", LineType.class) ); 
	menu.add( getMenuItem("Source Arrow", "edge.sourceArrow", Arrow.class) ); 
	menu.add( getMenuItem("Target Arrow", "edge.targetArrow", Arrow.class) ); 
	menu.add( getMenuItem("Label", "edge.label", String.class) ); 
	menu.add( getMenuItem("Label Color", "edge.labelColor", Color.class) ); 
	menu.add( getMenuItem("Font", "edge.font", Font.class) ); 
	menu.add( getMenuItem("Font Size", "edge.fontSize", Font.class) ); 
	menu.add( getMenuItem("ToolTip", "edge.toolTip", String.class) ); 

        return menu;
    }
}

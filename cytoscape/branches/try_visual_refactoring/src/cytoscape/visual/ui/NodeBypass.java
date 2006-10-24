

/*
 File: NodeBypass.java 
 
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


import cytoscape.*;
import cytoscape.util.*;
import cytoscape.visual.*;
import cytoscape.visual.parsers.*;
import cytoscape.data.CyAttributes;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

import java.net.URL;
import java.util.*;
import java.awt.*;
import giny.model.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class NodeBypass {

    CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
    Frame parent = Cytoscape.getDesktop();
    Node node;
    VisualMappingManager vmm = Cytoscape.getVisualMappingManager();

    public JMenuItem addLinks(Node n) {
    	node = n;

        JMenu menu =new JMenu("Visual Mapping Bypass");
	
	menu.add( getMenuItem("Node Color", "node.fillColor", Color.class) ); 
	menu.add( getMenuItem("Node Border Color", "node.borderColor", Color.class) ); 
	menu.add( getMenuItem("Node Border Line Type", "node.lineType", LineType.class) ); 
	menu.add( getMenuItem("Node Label Position", "node.labelPosition", LabelPosition.class) ); 
	menu.add( getMenuItem("Node Size", "node.size",Double.class) ); 
	menu.add( getMenuItem("Node Width", "node.width",Double.class) ); 
	menu.add( getMenuItem("Node Height", "node.height",Double.class) ); 
	menu.add( getMenuItem("Node Shape", "node.shape",Byte.class) ); 
	menu.add( getMenuItem("Node ToolTip", "node.toolTip",String.class) ); 
	menu.add( getMenuItem("Node Label", "node.label",String.class) ); 
	menu.add( getMenuItem("Node Label Color", "node.labelColor",Color.class) ); 
	menu.add( getMenuItem("Node Font", "node.font",Font.class) ); 
	menu.add( getMenuItem("Node Font Size", "node.fontSize",Double.class) ); 
	
        return menu;
    }

	private JMenuItem getMenuItem(final String title, final String attrName, final Class c) {

		JMenuItem jmi = new JMenuItem (new AbstractAction(title) {
			public void actionPerformed (ActionEvent e) {
				Object obj = getBypassValue(title,c);
				if ( obj == null )
					return;
				String val = ObjectToString.getStringValue(obj);
				nodeAttrs.setAttribute(node.getIdentifier(),attrName,val);
				vmm.getNetworkView().redrawGraph(false, true);
			}

			private Object getBypassValue(String title, Class c) {
				if ( c == Color.class ) {
					return CyColorChooser.showDialog(parent,"Choose " + title,null);
				} else if ( c == Double.class ) {
					return PopupStringChooser.showDialog(parent,"Choose " + title,"Input a double:",
									     null,ValueDisplayer.DOUBLE);
				} else if ( c == Integer.class ) {
					return PopupStringChooser.showDialog(parent,"Choose " + title,"Input an integer:",
									     null,ValueDisplayer.INT);
				} else if ( c == Byte.class ) {
					IconSupport is = new IconSupport(null,ValueDisplayer.NODESHAPE);
					PopupIconChooser chooser = new PopupIconChooser("Choose " + title,null,
											is.getIcons(),null,parent);
					return chooser.showDialog();
				} else if ( c == Arrow.class ) {
					IconSupport is = new IconSupport(null,ValueDisplayer.ARROW);
					PopupIconChooser chooser = new PopupIconChooser("Choose " + title,null,
											is.getIcons(),null,parent);
					return chooser.showDialog();
				} else if ( c == LineType.class ) {
					IconSupport is = new IconSupport(null,ValueDisplayer.LINETYPE);
					PopupIconChooser chooser = new PopupIconChooser("Choose " + title,null,
											is.getIcons(),null,parent);
					return chooser.showDialog();
				} else if ( c == String.class ) {
					return PopupStringChooser.showDialog(parent,"Choose " + title,"Input a String:",
									     null,ValueDisplayer.STRING);
				} else if ( c == LabelPosition.class ) {
					return PopupLabelPlacementChooser.showDialog(parent,null);
				} else if ( c == Font.class ) {
					return PopupFontChooser.showDialog(parent,null);
				}

				return null;
			}
		}); 
		return jmi;
	}
}

/*
 File: EdgeMenuListener.java

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
package netlink;

import ding.view.EdgeContextMenuListener;

import giny.view.EdgeView;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;

class EdgeMenuListener implements EdgeContextMenuListener {

    EdgeMenuListener() { }

    /**
     * @param nodeView The clicked EdgeView
     * @param menu popup menu to add the  menu
     */
    public void addEdgeContextMenuItems(EdgeView edgeView, JPopupMenu menu) {
//		System.out.println("asdasdfasdfafs");
		CyEdge e = (CyEdge)edgeView.getEdge();
		String[] titles = e.getIdentifier().split("\\s+");
		String n1title = titles[0];
		String n2title = titles[2];

		CyNetwork match = null;

		for ( Object no : Cytoscape.getNetworkSet() ) {
			CyNetwork net = (CyNetwork)no;
//			System.out.println("	testing title: " + net.getTitle());
			if ( net.getTitle().matches( n1title + "\\W" + n2title ) ||
			     net.getTitle().matches( n2title + "\\W" + n1title ) ) {
				 match = net;
				 break;
			}
		}


		if ( match == null ) {
			System.out.println("Couldn't find matching child network for edge: " + e.getIdentifier());
			return;
		} else {
			//System.out.println("Found match: " + match.getTitle());
		}

        if (menu == null)
            menu = new JPopupMenu();

        menu.add( new JMenuItem(new NetLink(match)));
    }
}

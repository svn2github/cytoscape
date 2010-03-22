/*
 File: NewEmptyNetworkListener.java 

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

package cytoscape.editor.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

/**
 * Used to switch to the Editor cytopanel when a new empty network is created.
 */ 
class NewEmptyNetworkListener implements PropertyChangeListener {
	
	// AJK: 03/13/2008 need to check that network is NEW as well as empty
	private List<CyNetwork> networksSeen = new ArrayList<CyNetwork>();

	NewEmptyNetworkListener() {
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			.addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
	}

	public void propertyChange(PropertyChangeEvent e) {

		// listen for new views that get created 
		if ( e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			CyNetwork net = Cytoscape.getCurrentNetwork();

			// only if the network in question doesn't have any nodes (i.e. it's an
			// empty network), do we automatically switch to the editor
			// AJK: 02/28/08 but check against null network
//			if ( net.getNodeCount() <= 0 ) {
			// AJK: 03/13/2008 need to check that network is NEW as well as empty
//			if (( net.getNodeCount() <= 0 ) && (net != Cytoscape.getNullNetwork())){
			if (!networksSeen.contains (net))
			{
				networksSeen.add(net);
				if (( net.getNodeCount() <= 0 ) && (net != Cytoscape.getNullNetwork())) {									
					int idx = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent("Editor");
					if (idx >= 0) 
						Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(idx);
				}			
			}
		}
	}
}

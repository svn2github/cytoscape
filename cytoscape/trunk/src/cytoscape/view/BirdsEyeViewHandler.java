/*
 File: BirdsEyeViewHandler.java

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
package cytoscape.view;

import cytoscape.Cytoscape;

import ding.view.BirdsEyeView;
import ding.view.DGraphView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class handles the creation of the BirdsEyeView navigation object 
 * and handles the events which change view seen. 
 */
class BirdsEyeViewHandler implements PropertyChangeListener {
	BirdsEyeView bev;

	/**
	 * Creates a new BirdsEyeViewHandler object.
	 */
	BirdsEyeViewHandler() {
		bev = new BirdsEyeView((DGraphView) Cytoscape.getCurrentNetworkView()) {
				public Dimension getMinimumSize() {
					return new Dimension(180, 180);
				}
			};
	}

	/**
	 * Listens for NETWORK_VIEW_FOCUSED, NETWORK_VIEW_FOCUS, NETWORK_VIEW_DESTROYED,
	 * and CYTOSCAPE_INITIALIZED events and changes the network view accordingly.
	 *
	 * @param e The event triggering this method. 
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if ((e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED)
		    || (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS)
		    || (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED)
		    || (e.getPropertyName() == Cytoscape.CYTOSCAPE_INITIALIZED)) {
			bev.changeView((DGraphView) Cytoscape.getCurrentNetworkView());
		}
	}

	/**
	 * Returns a birds eye view component.
	 * @return The component that contains the birds eye view.
	 */
	Component getBirdsEyeView() {
		return bev;
	}
}

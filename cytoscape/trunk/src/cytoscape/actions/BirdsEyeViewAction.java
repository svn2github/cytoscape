/*
 File: BirdsEyeViewAction.java

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
package cytoscape.actions;

import cytoscape.Cytoscape;

import cytoscape.util.CytoscapeAction;

import cytoscape.view.CytoscapeDesktop;

import ding.view.BirdsEyeView;
import ding.view.DGraphView;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

/**
 *
 */
public class BirdsEyeViewAction extends CytoscapeAction implements PropertyChangeListener {
	BirdsEyeView bev;
	boolean on = false;

	/**
	 * Creates a new BirdsEyeViewAction object.
	 */
	public BirdsEyeViewAction() {
		super("Show/Hide Network Overview");
		setPreferredMenu("View");
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.CYTOSCAPE_INITIALIZED,this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if ((e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED)
		    || (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS)
		    || (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED)) {
			bev.changeView((DGraphView) Cytoscape.getCurrentNetworkView());
		} else if ( e.getPropertyName() == Cytoscape.CYTOSCAPE_INITIALIZED ) { 
			actionPerformed(null);	
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		if (!on) {
			bev = new BirdsEyeView((DGraphView) Cytoscape.getCurrentNetworkView()) {
					public Dimension getMinimumSize() {
						return new Dimension(180, 180);
					}
				};
			Cytoscape.getDesktop().getNetworkPanel().setNavigator(bev);
			Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);
			on = true;
			putValue(Action.NAME, "Hide Network Overview");	
		} else {
			if (bev != null) {
				bev.destroy();
				bev = null;
			}

			Cytoscape.getDesktop().getNetworkPanel()
			         .setNavigator(Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel());
			Cytoscape.getDesktop().getSwingPropertyChangeSupport().removePropertyChangeListener(this);
			on = false;
			putValue(Action.NAME, "Show Network Overview");	
		}
	}
}

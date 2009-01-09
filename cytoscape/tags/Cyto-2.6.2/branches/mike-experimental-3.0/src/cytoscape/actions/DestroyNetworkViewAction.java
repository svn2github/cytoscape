/*
 File: DestroyNetworkViewAction.java

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
import org.cytoscape.GraphPerspective;

import cytoscape.util.CytoscapeAction;

import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class DestroyNetworkViewAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869451581L;
	/**
	 * Creates a new DestroyNetworkViewAction object.
	 */
	public DestroyNetworkViewAction() {
		super("Destroy View");
		setPreferredMenu("Edit");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_W, ActionEvent.CTRL_MASK);
	}

	/**
	 * Creates a new DestroyNetworkViewAction object.
	 *
	 * @param label  DOCUMENT ME!
	 */
	public DestroyNetworkViewAction(boolean label) {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// Get the list first, then iterate. If you do this:
		//     for ( CyNetworkView cv : Cytoscape.getSelectedNetworkViews() )
		// you will notice that the list of selected networks changes
		// as you iterate through it.  This is due to events getting fired
		// as a result of the deletion.
		java.util.List<CyNetworkView> l = Cytoscape.getSelectedNetworkViews();
		for ( CyNetworkView cv : l )
			Cytoscape.destroyNetworkView(cv);
	}

	/**
	 * @deprecated Use Cytoscape.destroyNetworkView() instead. Will go 11/2008.
	 */
	@Deprecated
	public static void destroyViewFromCurrentNetwork() {
		Cytoscape.destroyNetworkView(Cytoscape.getCurrentNetwork());
	}

	public void menuSelected(MenuEvent e) {
		enableForNetworkAndView();
	}
}

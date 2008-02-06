/*
  File: DestroyNetworkAction.java

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
import cytoscape.CyNetwork;

import cytoscape.util.*;

import java.awt.event.*;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class DestroyNetworkAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869440949L;
	/**
	 * Creates a new DestroyNetworkAction object.
	 */
	public DestroyNetworkAction() {
		super("Destroy Network");
		setPreferredMenu("Edit");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_W,
		                    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
	}

	/**
	 * Creates a new DestroyNetworkAction object.
	 *
	 * @param label  DOCUMENT ME!
	 */
	public DestroyNetworkAction(boolean label) {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		java.util.List<CyNetwork> l = Cytoscape.getSelectedNetworks();
		for ( CyNetwork n : l )
			Cytoscape.destroyNetwork(n);
	}

	/**
	 * @deprecated Use Cytoscape.destroyNetwork(Cytoscape.getCurrentNetwork()) instead.
	 * Will be gone 11/2008.
	 */
	@Deprecated
	public static void destroyCurrentNetwork() {
		Cytoscape.destroyNetwork(Cytoscape.getCurrentNetwork());
	}

	/**
	 * Sets the action state based on whether a current network exists. 
	 */
	public void menuSelected(MenuEvent e) {
		enableForNetwork();
	}
}

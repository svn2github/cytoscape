/*
 File: NewSessionAction.java

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

import cytoscape.CyNetworkManager;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;

import javax.swing.*;
import java.awt.event.ActionEvent;


/**
 *
 */
public class NewSessionAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869847514L;
	/**
	 * Creates a new NewSessionAction object.
	 */
	private final CytoscapeDesktop desktop;
	public NewSessionAction(final CytoscapeDesktop desktop, CyNetworkManager netmgr) {
		super("Session",netmgr);
		setPreferredMenu("File.New");
		this.desktop = desktop;
	}

	// Create dialog
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {

		// Show warning
		String warning = "Current session (all networks/attributes) will be lost.\nDo you want to continue?";

		int result = JOptionPane.showConfirmDialog(desktop, warning, "Caution!",
		                                           JOptionPane.YES_NO_OPTION,
		                                           JOptionPane.WARNING_MESSAGE, null);

		if (result == JOptionPane.YES_OPTION) {
			Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
			Cytoscape.createNewSession();
			// TODO should be reworked so that desktop listens for changes to the session and
			// then updates itself
			desktop.setTitle("Cytoscape Desktop (New Session)");
			desktop.getNetworkPanel().repaint();
			desktop.repaint();
			Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
			
			Cytoscape.getPropertyChangeSupport().firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);
		} else {
			return;
		}
	}
}

/*
 File: NewSessionAction.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
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

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

public class NewSessionAction extends CytoscapeAction {

	public NewSessionAction() {
		super("Session");
		setPreferredMenu("File.New");
	}

	// Create dialog
	public void actionPerformed(ActionEvent e) {

		int currentNetworkCount = Cytoscape.getNetworkSet().size();

		if (currentNetworkCount != 0) {
			// Show warning
			String warning = "Current session will be lost.\nDo you want to continue?";

			int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
					warning, "Caution!", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {
				cleanWorkspace();
				Cytoscape.getDesktop().setTitle("Cytoscape Desktop (New Session)");
				Cytoscape.setCurrentSessionFileName(null);
			} else {
				return;
			}
		}

	}

	private void cleanWorkspace() {

		Set netSet = Cytoscape.getNetworkSet();
		Iterator it = netSet.iterator();

		while (it.hasNext()) {
			CyNetwork net = (CyNetwork) it.next();
			Cytoscape.destroyNetwork(net);
		}

		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String[] nodeAttrNames = nodeAttributes.getAttributeNames();
		for (int i = 0; i < nodeAttrNames.length; i++) {
			nodeAttributes.deleteAttribute(nodeAttrNames[i]);
		}

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String[] edgeAttrNames = edgeAttributes.getAttributeNames();
		for (int i = 0; i < edgeAttrNames.length; i++) {
			edgeAttributes.deleteAttribute(edgeAttrNames[i]);
		}
	}

}

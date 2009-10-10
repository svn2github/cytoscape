/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 * 
 * This file is part of PaxtoolsPlugin.
 *
 *  PaxtoolsPlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.coreplugins.biopax.action;

import cytoscape.coreplugins.biopax.ui.MergeBioPAXDialog;
import cytoscape.coreplugins.biopax.util.BioPAXUtilRex;
import cytoscape.util.CytoscapeAction;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import java.awt.event.ActionEvent;


import javax.swing.event.MenuEvent;

public class MergeBioPAXAction extends CytoscapeAction {
    public MergeBioPAXAction() {
		super("Merge BioPAX networks");
		setPreferredMenu("Plugins");
	}

    public void actionPerformed(ActionEvent e) {
        MergeBioPAXDialog dialog = new MergeBioPAXDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    public void menuSelected(MenuEvent e) {
        for(CyNetwork cyNetwork: Cytoscape.getNetworkSet()) {
            if( BioPAXUtilRex.isBioPAXNetwork(cyNetwork) ) {
                enableForNetwork();
                return;
            }
        }

        setEnabled(false);
    }
}

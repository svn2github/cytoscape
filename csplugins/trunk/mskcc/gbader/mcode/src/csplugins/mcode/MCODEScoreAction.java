/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 **
 ** Code written by: Gary Bader
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 ** Date: Nov 14, 2003
 ** Time: 3:48:51 PM
 ** Description: Action code for PathDistPlugin
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Action to only score a network
 */
public class MCODEScoreAction implements ActionListener {
	/**
	 * This method is called when the user selects the menu item.
	 * @param event Menu Item Selected.
	 */
	public void actionPerformed(ActionEvent event) {
        String callerID = "MCODEScoreAction.actionPerformed";
        //get the network object; this contains the graph
        CyNetwork network = Cytoscape.getCurrentNetwork();
        if (network == null) {
            System.err.println("In " + callerID + ":");
            System.err.println("Can't get current network.");
            return;
        }

        if (network.getNodeCount() < 1) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "You must have a network loaded to run this plugin.");
            return;
        }

		//run MCODE scoring algorithm - node scores are saved as node attributes
		long msTimeBefore = System.currentTimeMillis();
        MCODEAlgorithm alg = new MCODEAlgorithm();
		alg.scoreGraph(network);
        network.putClientData("MCODE_alg", alg);
		long msTimeAfter = System.currentTimeMillis();
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
		        "Network was scored in " + (msTimeAfter - msTimeBefore) + " ms.");
	}
}

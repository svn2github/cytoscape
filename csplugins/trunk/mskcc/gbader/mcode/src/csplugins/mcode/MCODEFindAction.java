package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 ** User: Gary Bader
 ** Date: Jan 22, 2004
 ** Time: 9:04:33 PM
 ** Description: Action to only find complexes given a previously scored network
 **/

/**
 * Action to only find complexes given a previously scored network
 */
public class MCODEFindAction implements ActionListener {
    private MCODEResultsDialog resultDialog;

    /**
     * This method is called when the user selects to find complexes.
     *
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

        //run MCODE complex finding algorithm after the nodes have been scored
        //the score action saves the resulting alg as network client data for retrieval
        MCODEAlgorithm alg = (MCODEAlgorithm) network.getClientData("MCODE_alg");
        if (alg == null) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "The network has not been scored.  Please run the scoring step first.");
            return;
        } else {
            ArrayList complexes = alg.findComplexes(network);
            //display complexes in a new non modal dialog box
            resultDialog = new MCODEResultsDialog(Cytoscape.getDesktop(), complexes, network, null);
            resultDialog.pack();
            resultDialog.setVisible(true);
        }
    }
}

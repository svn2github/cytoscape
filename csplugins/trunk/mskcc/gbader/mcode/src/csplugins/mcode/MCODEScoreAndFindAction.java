package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTask;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
 * * User: Gary Bader
 * * Date: May 5, 2004
 * * Time: 8:46:19 PM
 * * Description: simple score and find action for MCODE
 */

/**
 * Simple score and find action for MCODE. This should be the default for general users.
 */
public class MCODEScoreAndFindAction implements ActionListener {
    private MCODEResultsDialog resultDialog;

    /**
     * This method is called when the user selects the menu item.
     *
     * @param event Menu Item Selected.
     */
    public void actionPerformed(ActionEvent event) {
        String callerID = "MCODEScoreAction.actionPerformed";
        //get the network object; this contains the graph
        final CyNetwork network = Cytoscape.getCurrentNetwork();
        if (network == null) {
            System.err.println("In " + callerID + ":");
            System.err.println("Can't get current network.");
            return;
        }

        //MCODE needs a network of at least 1 node
        if (network.getNodeCount() < 1) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "You must have a network loaded to run this plugin.");
            return;
        }

        //check if MCODE is already running on this network
        Boolean isRunning = (Boolean) network.getClientData("MCODE_running");
        if ((isRunning != null) && (isRunning.booleanValue())) {
            //MCODE is already running - tell user and exit
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "MCODE is already running on this network. Please wait for it to complete.");
            return;
        } else {
            //MCODE is about to run - mark network that we are using it
            network.putClientData("MCODE_running", new Boolean(true));
        }

        //check if MCODE has already been run on this network
        resultDialog = (MCODEResultsDialog) network.getClientData("MCODE_dialog");
        if (resultDialog != null) {
            resultDialog.setVisible(true);
            network.putClientData("MCODE_running", new Boolean(false));
        } else {
            //run MCODE
            MCODEScoreAndFindTask MCODEScoreAndFindTask = new MCODEScoreAndFindTask(network);
            //Configure JTask
            JTaskConfig config = new JTaskConfig();

            //Show Cancel/Close Buttons
            config.displayCancelButton(true);
            config.displayStatus(true);

            //Execute Task via TaskManager
            //This automatically pops-open a JTask Dialog Box
            TaskManager.executeTask(MCODEScoreAndFindTask, config);
            //display clusters in a new non modal dialog box
            if (MCODEScoreAndFindTask.isCompletedSuccessfully()) {
                resultDialog = new MCODEResultsDialog(Cytoscape.getDesktop(), MCODEScoreAndFindTask.getClusters(),
                        network, MCODEScoreAndFindTask.getImageList());
                resultDialog.pack();
                //store the results dialog box if the user wants to see it later
                network.putClientData("MCODE_dialog", resultDialog);
                network.putClientData("MCODE_running", new Boolean(false));
                resultDialog.setVisible(true);
            }
        }
    }
}

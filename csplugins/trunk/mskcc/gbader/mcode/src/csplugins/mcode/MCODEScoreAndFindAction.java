package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import giny.model.GraphPerspective;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Copyright (c) 2003 Institute for Systems Biology, University of
 * * California at San Diego, and Memorial Sloan-Kettering Cancer Center.
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
 * * documentation provided hereunder is on an "as is" basis, and the
 * * Institute for Systems Biology, the University of California at San Diego
 * * and/or Memorial Sloan-Kettering Cancer Center
 * * have no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Institute for Systems Biology, the University of California at San Diego
 * * and/or Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if the
 * * Institute for Systems Biology, the University of California at San
 * * Diego and/or Memorial Sloan-Kettering Cancer Center
 * * have been advised of the possibility of such damage.  See
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
        if((isRunning!=null)&&(isRunning.booleanValue())) {
            //MCODE is already running - tell user and exit
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "MCODE is already running on this network. Please wait for it to complete.");
            return;
        }
        else {
            //MCODE is about to run - mark network that we are using it
            network.putClientData("MCODE_running", new Boolean(true));
        }

        //check if MCODE has already been run on this network
        resultDialog = (MCODEResultsDialog) network.getClientData("MCODE_dialog");
        if(resultDialog!=null) {
            resultDialog.setVisible(true);
            network.putClientData("MCODE_running", new Boolean(false));
        }
        else {
            class ThreadReturnInfo {
                ArrayList complexes;
                Image imageList[];

                public ThreadReturnInfo(ArrayList complexes, Image[] imageList) {
                    this.complexes = complexes;
                    this.imageList = imageList;
                }
            }

            //set up progress bar
            final MCODEProgressBarDialog progressBarDialog = new MCODEProgressBarDialog(Cytoscape.getDesktop());
            progressBarDialog.setIndeterminate(true);
            progressBarDialog.pack();
            progressBarDialog.setVisible(true);

            //threaded because MCODE may take a while
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    //run MCODE scoring algorithm - node scores are saved as node attributes
                    MCODEAlgorithm alg = new MCODEAlgorithm();
                    progressBarDialog.setString("Scoring Network (Step 1 of 3)");
                    alg.scoreGraph(network);
                    if(progressBarDialog.isCancelled()) {
                        progressBarDialog.dispose();
                        network.putClientData("MCODE_running", new Boolean(false));
                        return null;
                    }
                    progressBarDialog.setString("Finding Clusters (Step 2 of 3)");
                    ArrayList complexes = alg.findComplexes(network);
                    if (progressBarDialog.isCancelled()) {
                        progressBarDialog.dispose();
                        network.putClientData("MCODE_running", new Boolean(false));
                        return null;
                    }
                    progressBarDialog.setIndeterminate(false);
                    progressBarDialog.setLengthOfTask(complexes.size());
                    progressBarDialog.setString("Drawing Results (Step 3 of 3)");
                    //store this MCODE instance with the network to avoid duplicating the calculation
                    network.putClientData("MCODE_alg", alg);
                    System.err.println("Network was scored in " + alg.getLastScoreTime() + " ms.");
                    //also create all the images here for the complexes, since it can be a time consuming operation
                    GraphPerspective gpComplexArray[] = MCODEUtil.convertComplexListToSortedNetworkList(complexes, network, alg);
                    Image imageList[] = new Image[complexes.size()];
                    int imageSize = MCODECurrentParameters.getInstance().getParamsCopy().getDefaultRowHeight();
                    for (int i = 0; i < gpComplexArray.length; i++) {
                        if (progressBarDialog.isCancelled()) {
                            progressBarDialog.dispose();
                            network.putClientData("MCODE_running", new Boolean(false));
                            return null;
                        }
                        imageList[i] = MCODEUtil.convertNetworkToImage(gpComplexArray[i], imageSize, imageSize);
                        progressBarDialog.setValue(i+1);
                    }
                    ThreadReturnInfo returnInfo = new ThreadReturnInfo(complexes, imageList);
                    return returnInfo;
                }

                /**
                 * Called on the event dispatching thread (not on the worker thread)
                 * after the <code>construct</code> method has returned.
                 */
                public void finished() {
                    //display complexes in a new non modal dialog box
                    ThreadReturnInfo returnInfo = (ThreadReturnInfo) this.get();
                    if(returnInfo!=null) {
                        resultDialog = new MCODEResultsDialog(Cytoscape.getDesktop(), returnInfo.complexes, network, returnInfo.imageList);
                        resultDialog.pack();
                        //store the results dialog box if the user wants to see it later
                        network.putClientData("MCODE_dialog", resultDialog);
                        network.putClientData("MCODE_running", new Boolean(false));
                        progressBarDialog.dispose();
                        resultDialog.setVisible(true);
                    }
                }
            };
            worker.start();
        }
    }
}

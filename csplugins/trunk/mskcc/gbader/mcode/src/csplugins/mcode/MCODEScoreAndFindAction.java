package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

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

// TODO: We must account for scope and optimization and use the appropriate algorithm

/**
 * Simple score and find action for MCODE. This should be the default for general users.
 */
public class MCODEScoreAndFindAction implements ActionListener {

    private boolean showResultPanel = false;

    final static int FIRST_TIME = 0;
    final static int RESCORE = 1;
    final static int REFIND = 2;
    final static int NO_CHANGE = 3;
    int analyze = FIRST_TIME;

    int resultsCounter = 0;

    MCODEParameterSet currentParamsCopy;

    MCODEScoreAndFindAction () {}

    MCODEScoreAndFindAction (MCODEParameterSet currentParamsCopy) {
        this.currentParamsCopy = currentParamsCopy;
    }

    /**
     * This method is called when the user clicks Analyze.
     *
     * @param event Click of the analyzeButton on the MCODEMainPanel.
     */
    public void actionPerformed(ActionEvent event) {
        MCODEResultsPanel resultPanel;
        //get a copy of the last saved parameters for comparison with the current ones
        MCODEParameterSet savedParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy();

        //these statements determine which portion of the algorithm needs to be conducted by
        //testing which parameters have been modified compared to the last saved parameters.
        //Here we ensure that only relavant parameters are looked at.  For example, fluff density
        //parameter is irrelevant if fluff is not used in the current parameters.  Also, none of
        //the clustering parameters are relevant if the optimization is used
        if (currentParamsCopy.getKCore() != savedParamsCopy.getKCore() ||
                currentParamsCopy.isIncludeLoops() != savedParamsCopy.isIncludeLoops() ||
                currentParamsCopy.getDegreeCutOff() != savedParamsCopy.getDegreeCutOff() ||
                analyze == FIRST_TIME) {
            analyze = RESCORE;
            resultsCounter++;
            System.out.println("Analysis: network scoring, cluster finding");
        } else if (!currentParamsCopy.getScope().equals(savedParamsCopy.getScope()) ||
                currentParamsCopy.isOptimize() != savedParamsCopy.isOptimize() ||
                (!currentParamsCopy.isOptimize() &&
                        (currentParamsCopy.getMaxDepthFromStart() != savedParamsCopy.getMaxDepthFromStart() ||
                                currentParamsCopy.isHaircut() != savedParamsCopy.isHaircut() ||
                                currentParamsCopy.isFluff() != savedParamsCopy.isFluff() ||
                                (currentParamsCopy.isFluff() &&
                                        currentParamsCopy.getFluffNodeDensityCutOff() != savedParamsCopy.getFluffNodeDensityCutOff()) ||
                                (currentParamsCopy.getScope().equals(MCODEParameterSet.NETWORK) &&
                                        currentParamsCopy.isPreprocessNetwork() != savedParamsCopy.isPreprocessNetwork())))) {
            analyze = REFIND;
            resultsCounter++;
            System.out.println("Analysis: cluster finding");
        } else {
            analyze = NO_CHANGE;
            System.out.println("Analysis: parameters unchanged");
        }
        //finally we save the current parameters
        MCODECurrentParameters.getInstance().setParams(currentParamsCopy);

        String callerID = "MCODEScoreAndFindAction.actionPerformed";
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
        resultPanel = (MCODEResultsPanel) network.getClientData("MCODE_panel");
        if (analyze == NO_CHANGE) {
            //resultPanel.setVisible(true);
            network.putClientData("MCODE_running", new Boolean(false));
            showResultPanel = true;
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "The parameters you specified have not changed.");
        } else {
            //run MCODE
            MCODEScoreAndFindTask MCODEScoreAndFindTask = new MCODEScoreAndFindTask(network, analyze);
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
                resultPanel = new MCODEResultsPanel(MCODEScoreAndFindTask.getClusters(), network, MCODEScoreAndFindTask.getImageList());

                //store the results dialog box if the user wants to see it later
                network.putClientData("MCODE_panel", resultPanel);
                network.putClientData("MCODE_running", new Boolean(false));
                //resultDialog.setVisible(true);
                showResultPanel = true;
            }
        }
        if (showResultPanel) {
            //display MCODEResultsPanel in right cytopanel
            CytoscapeDesktop desktop = Cytoscape.getDesktop();
            CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);

            String componentTitle = "Results " + resultsCounter;
            resultPanel.setComponentTitle(componentTitle);

            URL iconURL = this.getClass().getResource("resources/logo2.png");
            if (iconURL != null){
                Icon icon = new ImageIcon(iconURL);
                String tip = "MCODE Cluster Finder";
                cytoPanel.add(componentTitle, icon, resultPanel, tip);
            } else {
                cytoPanel.add(componentTitle, resultPanel);
            }

            int index = cytoPanel.indexOfComponent(resultPanel);
            cytoPanel.setSelectedIndex(index);
            cytoPanel.setState(CytoPanelState.DOCK);
        }
    }
}

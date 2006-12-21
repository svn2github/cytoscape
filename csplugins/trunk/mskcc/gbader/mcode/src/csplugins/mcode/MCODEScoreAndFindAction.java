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
import java.util.HashMap;

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

// TODO: We must account for scope

/**
 * Simple score and find action for MCODE. This should be the default for general users.
 */
public class MCODEScoreAndFindAction implements ActionListener {
    //private MCODEAlgorithm alg;
    private HashMap networkManager;
    private boolean resultFound = false;
    private MCODEResultsPanel resultPanel; 

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
        networkManager = new HashMap();
        //alg = new MCODEAlgorithm();
    }

    /**
     * This method is called when the user clicks Analyze.
     *
     * @param event Click of the analyzeButton on the MCODEMainPanel.
     */
    public void actionPerformed(ActionEvent event) {
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
        MCODEAlgorithm alg;
        MCODEParameterSet savedParamsCopy;
        if (networkManager.containsKey(network.getIdentifier())) {
            alg = (MCODEAlgorithm) networkManager.get(network.getIdentifier());
            //get a copy of the last saved parameters for comparison with the current ones
            savedParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy(network.getIdentifier());
        } else {
            alg = new MCODEAlgorithm(null);
            savedParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy(null);
            networkManager.put(network.getIdentifier(), alg);
            analyze = FIRST_TIME;
        }

        //these statements determine which portion of the algorithm needs to be conducted by
        //testing which parameters have been modified compared to the last saved parameters.
        //Here we ensure that only relavant parameters are looked at.  For example, fluff density
        //parameter is irrelevant if fluff is not used in the current parameters.  Also, none of
        //the clustering parameters are relevant if the optimization is used
        if (currentParamsCopy.getKCore() != savedParamsCopy.getKCore() ||
                currentParamsCopy.isIncludeLoops() != savedParamsCopy.isIncludeLoops() ||
                currentParamsCopy.getDegreeCutoff() != savedParamsCopy.getDegreeCutoff() ||
                analyze == FIRST_TIME) {
            analyze = RESCORE;
            System.out.println("Analysis: network scoring, cluster finding");
        } else if (!currentParamsCopy.getScope().equals(savedParamsCopy.getScope()) ||
                currentParamsCopy.isOptimize() != savedParamsCopy.isOptimize() ||
                (!currentParamsCopy.isOptimize() &&
                        (currentParamsCopy.getMaxDepthFromStart() != savedParamsCopy.getMaxDepthFromStart() ||
                                currentParamsCopy.isHaircut() != savedParamsCopy.isHaircut() ||
                                currentParamsCopy.getNodeScoreCutoff() != savedParamsCopy.getNodeScoreCutoff() ||
                                currentParamsCopy.isFluff() != savedParamsCopy.isFluff() ||
                                (currentParamsCopy.isFluff() &&
                                        currentParamsCopy.getFluffNodeDensityCutoff() != savedParamsCopy.getFluffNodeDensityCutoff()) ||
                                (currentParamsCopy.getScope().equals(MCODEParameterSet.NETWORK) &&
                                        currentParamsCopy.isPreprocessNetwork() != savedParamsCopy.isPreprocessNetwork())))) {
            analyze = REFIND;
            System.out.println("Analysis: cluster finding");
        } else {
            analyze = NO_CHANGE;
            System.out.println("Analysis: parameters unchanged");
        }
        //finally we save the current parameters
        MCODECurrentParameters.getInstance().setParams(currentParamsCopy, "Results " + (resultsCounter + 1), network.getIdentifier());

        if (analyze == NO_CHANGE) {
            //network.putClientData("MCODE_running", new Boolean(false));
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "The parameters you specified have not changed.");
        } else {
            //run MCODE
            MCODEScoreAndFindTask MCODEScoreAndFindTask = new MCODEScoreAndFindTask(network, analyze, "Results " + (resultsCounter + 1), alg);
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
                if (MCODEScoreAndFindTask.getClusters().length > 0) {
                    resultFound = true;
                    resultsCounter++;

                    resultPanel = new MCODEResultsPanel(MCODEScoreAndFindTask.getClusters(), MCODEScoreAndFindTask.getAlg(), network, MCODEScoreAndFindTask.getImageList(), "Results " + resultsCounter);

                    //store the results dialog box if the user wants to see it later
                    //network.putClientData("MCODE_panel", resultPanel);
                } else {
                    resultFound = false;
                    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No clusters were found.\nTry changing the MCODE parameters.");
                }
                //network.putClientData("MCODE_running", new Boolean(false));
            }
        }
        //display MCODEResultsPanel in right cytopanel
        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
        //if there is no change, then we simply focus the last produced results (below), otherwise we
        //load the new results panel
        if (resultFound) {
            String resultTitle = "Results " + resultsCounter;
            resultPanel.setResultsTitle(resultTitle);

            URL iconURL = this.getClass().getResource("resources/logo2.png");
            if (iconURL != null) {
                Icon icon = new ImageIcon(iconURL);
                String tip = "MCODE Cluster Finder";
                cytoPanel.add(resultTitle, icon, resultPanel, tip);
            } else {
                cytoPanel.add(resultTitle, resultPanel);
            }
        }
        //this makes sure that the east cytopanel is not loaded if there are no results in it
        if (resultFound || (analyze == NO_CHANGE && cytoPanel.indexOfComponent(resultPanel) >= 0)) {
            //focus the result panel
            int index = cytoPanel.indexOfComponent(resultPanel);
            cytoPanel.setSelectedIndex(index);
            cytoPanel.setState(CytoPanelState.DOCK);
        }
    }
}

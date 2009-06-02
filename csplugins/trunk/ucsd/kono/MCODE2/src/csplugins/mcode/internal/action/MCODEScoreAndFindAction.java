package csplugins.mcode.internal.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import csplugins.mcode.MCODEPlugin;
import csplugins.mcode.internal.MCODEAlgorithm;
import csplugins.mcode.internal.MCODECurrentParameters;
import csplugins.mcode.internal.MCODEParameterSet;
import csplugins.mcode.internal.MCODEVisualStyle;
import csplugins.mcode.internal.ui.MCODEResultsPanel;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.visual.VisualMappingManager;

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
 * Simple score and find action for MCODE. This should be the default for
 * general users.
 */
public class MCODEScoreAndFindAction implements ActionListener {

	// Keeps track of netowrks (id is key) and their respective algorithms
	private final Map<String, MCODEAlgorithm> networkManager;
	
	private boolean resultFound = false;
	private MCODEResultsPanel resultPanel;

	final static int FIRST_TIME = 0;
	final static int RESCORE = 1;
	final static int REFIND = 2;
	final static int INTERRUPTION = 3;
	int analyze = FIRST_TIME;

	int resultCounter = 0;

	MCODEParameterSet currentParamsCopy;
	MCODEVisualStyle MCODEVS;

	public MCODEScoreAndFindAction(MCODEParameterSet currentParamsCopy,
			MCODEVisualStyle MCODEVS) {
		this.currentParamsCopy = currentParamsCopy;
		this.MCODEVS = MCODEVS;
		networkManager = new HashMap<String, MCODEAlgorithm>();
	}

	/**
	 * This method is called when the user clicks Analyze.
	 * 
	 * @param event
	 *            Click of the analyzeButton on the MCODEMainPanel.
	 */
	public void actionPerformed(ActionEvent event) {
		String resultTitlePartA = "MCODE Result ";

		String callerID = "MCODEScoreAndFindAction.actionPerformed";
		String interruptedMessage = "";
		// get the network object, this contains the graph
		final CyNetwork network = Cytoscape.getCurrentNetwork();
		if (network == null) {
			System.err.println("In " + callerID + ":");
			System.err.println("Can't get current network.");
			return;
		}
		// MCODE needs a network of at least 1 node
		if (network.getNodeCount() < 1) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"You must have a network loaded\nto run this plugin.",
					"Analysis Interrupted", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		Set<CyNode> selectedNodes = network.getSelectedNodes();
		Integer[] selectedNodesRGI = new Integer[selectedNodes.size()];
		int c = 0;
		for (CyNode node: selectedNodes) {
			selectedNodesRGI[c] = new Integer(node.getRootGraphIndex());
			c++;
		}
		currentParamsCopy.setSelectedNodes(selectedNodesRGI);

		MCODEAlgorithm alg;
		MCODEParameterSet savedParamsCopy;
		// Here we determine if we have already run mcode on this network before
		// if we have then we use the stored alg class and the last saved
		// parameters
		// of that network (so as to determine if rescoring/refinding is
		// required for
		// this network without interference by parameters of other networks)
		// otherwise we construct a new alg class
		if (networkManager.containsKey(network.getIdentifier())) {
			alg = networkManager.get(network.getIdentifier());
			// get a copy of the last saved parameters for comparison with the
			// current ones
			savedParamsCopy = MCODECurrentParameters.getParamsCopy(network.getIdentifier());
		} else {
			alg = new MCODEAlgorithm();
			savedParamsCopy = MCODECurrentParameters.getParamsCopy(null);
			networkManager.put(network.getIdentifier(), alg);
			analyze = FIRST_TIME;
		}

		// these statements determine which portion of the algorithm needs to be
		// conducted by
		// testing which parameters have been modified compared to the last
		// saved parameters.
		// Here we ensure that only relavant parameters are looked at. For
		// example, fluff density
		// parameter is irrelevant if fluff is not used in the current
		// parameters. Also, none of
		// the clustering parameters are relevant if the optimization is used
		if (currentParamsCopy.isIncludeLoops() != savedParamsCopy
				.isIncludeLoops()
				|| currentParamsCopy.getDegreeCutoff() != savedParamsCopy
						.getDegreeCutoff() || analyze == FIRST_TIME) {
			analyze = RESCORE;
			System.err.println("Analysis: score network, find clusters");
			MCODECurrentParameters.setParams(currentParamsCopy,
					resultTitlePartA + (resultCounter + 1),
					network.getIdentifier());
		} else if (!currentParamsCopy.getScope().equals(
				savedParamsCopy.getScope())
				|| (!currentParamsCopy.getScope().equals(
						MCODEParameterSet.NETWORK) && currentParamsCopy
						.getSelectedNodes() != savedParamsCopy
						.getSelectedNodes())
				|| currentParamsCopy.isOptimize() != savedParamsCopy
						.isOptimize()
				|| (!currentParamsCopy.isOptimize() && (currentParamsCopy
						.getKCore() != savedParamsCopy.getKCore()
						|| currentParamsCopy.getMaxDepthFromStart() != savedParamsCopy
								.getMaxDepthFromStart()
						|| currentParamsCopy.isHaircut() != savedParamsCopy
								.isHaircut()
						|| currentParamsCopy.getNodeScoreCutoff() != savedParamsCopy
								.getNodeScoreCutoff()
						|| currentParamsCopy.isFluff() != savedParamsCopy
								.isFluff() || (currentParamsCopy.isFluff() && currentParamsCopy
						.getFluffNodeDensityCutoff() != savedParamsCopy
						.getFluffNodeDensityCutoff())))) {
			analyze = REFIND;
			System.err.println("Analysis: find clusters");
			MCODECurrentParameters.setParams(currentParamsCopy,
					resultTitlePartA + (resultCounter + 1),
					network.getIdentifier());
		} else {
			analyze = INTERRUPTION;
			interruptedMessage = "The parameters you specified\nhave not changed.";
			MCODECurrentParameters.setParams(currentParamsCopy,
					resultTitlePartA + resultCounter, network.getIdentifier());
		}
		// finally we save the current parameters
		// MCODECurrentParameters.getInstance().setParams(currentParamsCopy,
		// resultTitlePartA + (resultCounter + 1), network.getIdentifier());

		// incase the user selected selection scope we must make sure that they
		// selected at least 1 node
		if (currentParamsCopy.getScope().equals(MCODEParameterSet.SELECTION)
				&& currentParamsCopy.getSelectedNodes().length < 1) {
			analyze = INTERRUPTION;
			interruptedMessage = "You must select ONE OR MORE NODES\nfor this scope.";
		}

		if (analyze == INTERRUPTION) {
			System.err.println("Analysis: interrupted");
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					interruptedMessage, "Analysis Interrupted",
					JOptionPane.WARNING_MESSAGE);
		} else {
			// run MCODE
			MCODEScoreAndFindTask scoreAndFindTask = new MCODEScoreAndFindTask(
					network, analyze, resultTitlePartA + (resultCounter + 1),
					alg);

			// Configure JTask
			JTaskConfig config = new JTaskConfig();

			// Show Cancel/Close Buttons
			config.displayCancelButton(true);
			config.displayStatus(true);

			// Execute Task via TaskManager
			// This automatically pops-open a JTask Dialog Box
			TaskManager.executeTask(scoreAndFindTask, config);
			// display clusters in a new modal dialog box
			if (scoreAndFindTask.isCompletedSuccessfully()) {
				if (scoreAndFindTask.getClusters().length > 0) {
					resultFound = true;
					resultCounter++;

					resultPanel = new MCODEResultsPanel(scoreAndFindTask
							.getClusters(), scoreAndFindTask.getAlg(),
							network, scoreAndFindTask.getImageList(),
							resultTitlePartA + resultCounter);
				} else {
					resultFound = false;
					JOptionPane
							.showMessageDialog(
									Cytoscape.getDesktop(),
									"No clusters were found.\n"
											+ "You can try changing the MCODE parameters or\n"
											+ "modifying your node selection if you are using\n"
											+ "a selection-specific scope.",
									"No Results", JOptionPane.WARNING_MESSAGE);
				}
			}
			scoreAndFindTask = null;
		}
		
		
		// display MCODEResultsPanel in right cytopanel
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
		// if there is no change, then we simply focus the last produced results
		// (below), otherwise we
		// load the new results panel
		if (resultFound) {
			String resultTitle = resultTitlePartA + resultCounter;
			resultPanel.setResultTitle(resultTitle);

			URL iconURL = MCODEPlugin.class.getResource("resources/logo2.png");
			if (iconURL != null) {
				Icon icon = new ImageIcon(iconURL);
				String tip = "MCODE Cluster Finder";
				try {
					cytoPanel.add(resultTitle, icon, resultPanel, tip);
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				} // exception thrown by java functions when more than one tab
					// is added, all tabs are deleted and then a new tab is
					// added...
			} else {
				cytoPanel.add(resultTitle, resultPanel);
			}
		}
		// this if statemet ensures that the east cytopanel is not loaded if
		// there are no results in it
		if (resultFound
				|| (analyze == INTERRUPTION && cytoPanel
						.indexOfComponent(resultPanel) >= 0)) {
			// focus the result panel
			int index = cytoPanel.indexOfComponent(resultPanel);
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);

			// We also make sure that the MCODE visual style is applied whenever
			// new results are produced
			VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
			vmm.setVisualStyle(MCODEVS);
			vmm.applyAppearances();
		}
	}
}

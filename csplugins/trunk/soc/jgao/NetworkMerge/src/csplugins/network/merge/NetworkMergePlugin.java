/* File: NetworkMergePlugin.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.network.merge;

import csplugins.network.merge.ui.NetworkMergeDialog;
import csplugins.network.merge.NetworkMerge.Operation;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;

import giny.model.Node;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;


import java.awt.event.ActionEvent;

import java.util.List;
import java.util.Arrays;

/**
 * Plugin to merge networks
 * 
 * 
 */
public class NetworkMergePlugin extends CytoscapePlugin {
    public NetworkMergePlugin() {
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new NetworkMergeAction());
        //Cytoscape.createNetworkFromFile("testData\\data1.cys");
    }
    
    class NetworkMergeAction extends CytoscapeAction {
        public NetworkMergeAction() {
            super("Advanced Merge networks"); //TODO rename
	}

        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(final ActionEvent ae) {
            prepare(); //TODO: remove in Cytoscape3
            
            final NetworkMergeDialog dialog = new NetworkMergeDialog(new javax.swing.JFrame(), true);
            dialog.setLocationRelativeTo(Cytoscape.getDesktop());
            dialog.setVisible(true);
            if (!dialog.isCancelled()) {
                final NetworkMergeSessionTask task = new NetworkMergeSessionTask(
                                    dialog.getMatchingAttribute(),
                                    dialog.getNodeAttributeMapping(),
                                    dialog.getEdgeAttributeMapping(),
                                    dialog.getSelectedNetworkList(),
                                    dialog.getOperation(),
                                    dialog.getMergedNetworkName());
                
                // Configure JTask Dialog Pop-Up Box
                final JTaskConfig jTaskConfig = new JTaskConfig();
                jTaskConfig.setOwner(Cytoscape.getDesktop());
                jTaskConfig.displayCloseButton(true);
                jTaskConfig.displayCancelButton(false);
                jTaskConfig.displayStatus(true);
                jTaskConfig.setAutoDispose(false);

                // Execute Task in New Thread; pop open JTask Dialog Box.
                TaskManager.executeTask(task, jTaskConfig);
            }
        }
        
        //TODO: remove in Cytoscape3
        /*
         * Copy node ID to canonicalName if canonicalName does not exist
         * 
         */
        private void prepare() {
            CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
            if (!Arrays.asList(cyAttributes.getAttributeNames()).contains(Semantics.CANONICAL_NAME)) {
                List<Node> nodeList = Cytoscape.getCyNodesList();
                int n = nodeList.size();
                for (int i=0; i<n; i++) {
                    String nodeID = nodeList.get(i).getIdentifier();
                    cyAttributes.setAttribute(nodeID, Semantics.CANONICAL_NAME, nodeID);
                }
            }            
        }//TODO: remove in Cytoscape3
    }
}

class NetworkMergeSessionTask implements Task {
    private MatchingAttribute matchingAttribute;
    private AttributeMapping nodeAttributeMapping;
    private AttributeMapping edgeAttributeMapping;
    private List<CyNetwork> selectedNetworkList;
    private Operation operation;
    private String mergedNetworkName; 
    private TaskMonitor taskMonitor;

    /**
     * Constructor.<br>
     *
     */
    NetworkMergeSessionTask( final MatchingAttribute matchingAttribute,
                             final AttributeMapping nodeAttributeMapping,
                             final AttributeMapping edgeAttributeMapping,
                             final List<CyNetwork> selectedNetworkList,
                             final Operation operation,
                             final String mergedNetworkName) {
        this.matchingAttribute = matchingAttribute;
        this.nodeAttributeMapping = nodeAttributeMapping;
        this.edgeAttributeMapping = edgeAttributeMapping;
        this.selectedNetworkList = selectedNetworkList;
        this.operation = operation;
        this.mergedNetworkName = mergedNetworkName;
    }

    /**
     * Executes Task
     *
     * @throws
     * @throws Exception
     */
    public void run() {
        taskMonitor.setStatus("Merging networks.\n\nIt may take a while.\nPlease wait...");
        taskMonitor.setPercentCompleted(0);
        
        CyNetwork mergedNetwork;

        try {
            final NetworkMerge networkMerge = new DefaultNetworkMerge(
                                matchingAttribute,
                                nodeAttributeMapping,
                                edgeAttributeMapping);
            mergedNetwork = networkMerge.mergeNetwork(
                                selectedNetworkList,
                                operation,
                                mergedNetworkName);

            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("The selected networks were successfully merged into network '"
                                  + mergedNetwork.getTitle()
                                  + "'");
            
            //TODO handle attribute conflicts here
        
        } catch(Exception e) {
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("Network Merge Failed!");
            e.printStackTrace();
        }
        
    }

    /**
     * Halts the Task: Not Currently Implemented.
     */
    public void halt() {
            // Task can not currently be halted.
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("Failed!!!");
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor
     *            TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
            this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return Task Title.
     */
    public String getTitle() {
            return "Merging networks";
    }
}


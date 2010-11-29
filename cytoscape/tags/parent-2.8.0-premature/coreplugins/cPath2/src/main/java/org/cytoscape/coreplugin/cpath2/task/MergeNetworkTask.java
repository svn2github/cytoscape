// $Id: MergeNetworkTask.java,v 1.3 2007/04/20 15:49:12 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath2.task;

// imports

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.readers.GraphReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.util.undo.CyUndo;
import org.cytoscape.coreplugin.cpath2.cytoscape.MergeNetworkEdit;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Task to merge a network.
 *
 * @author Benjamin Gross
 */
public class MergeNetworkTask implements Task {

    /**
     * ref to cpathInstanceURL
     */
    private URL cpathInstanceURL;

    /**
     * ref to cyNetwork
     */
    private CyNetwork cyNetwork;

    /**
     * ref to our graph reader
     */
    private GraphReader reader;

    /**
     * ref to the task monitor
     */
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param cpathURL URL
     * @param cyNetwork         CyNetwork
     */
    public MergeNetworkTask(URL cpathURL, CyNetwork cyNetwork) {

        // init member vars
        this.cpathInstanceURL = cpathURL;
        this.cyNetwork = cyNetwork;
        reader = Cytoscape.getImportHandler().getReader(cpathURL);
    }

    /**
     * Our implementation of Task.halt()
     */
    public void halt() {
        // Task can not currently be halted.
    }

    /**
     * Our implementation of Task.setTaskMonitor().
     *
     * @param taskMonitor TaskMonitor
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Our implementation of Task.getTitle.
     *
     * @return Task Title.
     */
    public String getTitle() {
        return CPathProperties.getInstance().getCPathServerName() + " Plugin - Merge Network";
    }

    /**
     * Our implementation of Task.run().
     */
    public void run() {

        try {

            // read the network from cpath instance
            taskMonitor.setPercentCompleted(-1);
            taskMonitor.setStatus("Reading in Network Data from "
                    + CPathProperties.getInstance().getCPathServerName()  + "...");
            reader.read();

            // unselect all nodes / edges
            cyNetwork.unselectAllNodes();
            cyNetwork.unselectAllEdges();

            // refs to capture new nodes and edgets
            Set<CyNode> newNodes = new HashSet<CyNode>();
            Set<CyEdge> newEdges = new HashSet<CyEdge>();

            // add new nodes and edges to existing network
            // tbd: worry about networks that exceed # node/edge threshold
            final int[] nodes = reader.getNodeIndicesArray();
            final int[] edges = reader.getEdgeIndicesArray();
            for (int node : nodes) {
                cyNetwork.addNode(node);
                newNodes.add((CyNode) Cytoscape.getRootGraph().getNode(node));
            }
            for (int edge : edges) {
                cyNetwork.addEdge(edge);
                newEdges.add((CyEdge) Cytoscape.getRootGraph().getEdge(edge));
            }

            // execute any post processing -
            // in this case, biopax style is applied, network attributes set, etc
            reader.doPostProcessing(cyNetwork);

            // select nodes / edges
            cyNetwork.setSelectedNodeState(newNodes, true);
            cyNetwork.setSelectedEdgeState(newEdges, true);

            // setup undo
            CyUndo.getUndoableEditSupport().postEdit(new MergeNetworkEdit(cyNetwork, newNodes, newEdges));

            // fire Cytoscape.NETWORK_MODIFIED - should be removed when undo support is back in
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, cyNetwork);

            // update the task monitor
            taskMonitor.setStatus(getMergeStatus(cyNetwork, nodes.length, edges.length));
            taskMonitor.setPercentCompleted(100);

        } catch (Exception e) {
            taskMonitor.setException(e, "Unable to merge networks.");
        }
    }

    /**
     * Constructs merge status string.
     * (based on cytoscape.action.LoadNetworkFromUrlTask.informUserOfGraphStats)
     *
     * @param cyNetwork CyNetwork
     * @param nodeCount int
     * @param edgeCount int
     * @return String
     */
    private String getMergeStatus(CyNetwork cyNetwork, int nodeCount, int edgeCount) {

        NumberFormat formatter = new DecimalFormat("#,###,###");
        StringBuffer sb = new StringBuffer();

        // construct status string
        sb.append("Succesfully merged network from:  ");
        sb.append(cpathInstanceURL.toString() + ".\n");
        sb.append(formatter.format(nodeCount) + " nodes and " + formatter.format(edgeCount) + " edges have been merged.");
        sb.append("  The merged network contains a total of " + formatter.format(cyNetwork.getNodeCount()));
        sb.append(" nodes and " + formatter.format(cyNetwork.getEdgeCount()) + " edges.");

        // outta here
        return sb.toString();
	}
}

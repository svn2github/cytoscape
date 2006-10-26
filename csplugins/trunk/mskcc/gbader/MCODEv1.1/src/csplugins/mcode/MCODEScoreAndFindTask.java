package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import giny.model.GraphPerspective;

import java.awt.*;
import java.util.ArrayList;

/**
 * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
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
 * * User: GaryBader
 * * Date: Jan 25, 2005
 * * Time: 8:41:53 PM
 * * Description: MCODE Score network and find cluster task
 */
public class MCODEScoreAndFindTask implements Task {
    private TaskMonitor taskMonitor = null;
    private boolean interrupted = false;
    private CyNetwork network = null;
    private MCODEAlgorithm alg = null;
    private ArrayList clusters = null;
    private Image imageList[] = null;
    private boolean completedSuccessfully = false;

    /**
     * Scores and finds clusters in a given network
     *
     * @param network The network to cluster
     */
    public MCODEScoreAndFindTask(CyNetwork network) {
        this.network = network;
    }

    /**
     * Run MCODE (Both score and find steps)
     */
    public void run() {
        if (taskMonitor == null) {
            throw new IllegalStateException("Task Monitor is not set.");
        }
        try {
            //run MCODE scoring algorithm - node scores are saved as node attributes
            alg = new MCODEAlgorithm(taskMonitor);
            taskMonitor.setPercentCompleted(0);
            taskMonitor.setStatus("Scoring Network (Step 1 of 3)");
            alg.scoreGraph(network);
            if (interrupted) {
                network.putClientData("MCODE_running", new Boolean(false));
                return;
            }
            System.err.println("Network was scored in " + alg.getLastScoreTime() + " ms.");
            taskMonitor.setPercentCompleted(0);
            taskMonitor.setStatus("Finding Clusters (Step 2 of 3)");
            clusters = alg.findComplexes(network);
            if (interrupted) {
                network.putClientData("MCODE_running", new Boolean(false));
                return;
            }
            //store this MCODE instance with the network to avoid duplicating the calculation
            network.putClientData("MCODE_alg", alg);
            taskMonitor.setPercentCompleted(0);
            taskMonitor.setStatus("Drawing Results (Step 3 of 3)");
            //also create all the images here for the clusters, since it can be a time consuming operation
            GraphPerspective gpComplexArray[] = MCODEUtil.convertComplexListToSortedNetworkList(clusters, network, alg);
            imageList = new Image[clusters.size()];
            int imageSize = MCODECurrentParameters.getInstance().getParamsCopy().getDefaultRowHeight();
            for (int i = 0; i < gpComplexArray.length; i++) {
                if (interrupted) {
                    network.putClientData("MCODE_running", new Boolean(false));
                    return;
                }
                imageList[i] = MCODEUtil.convertNetworkToImage(gpComplexArray[i], imageSize, imageSize);
                taskMonitor.setPercentCompleted((i * 100) / gpComplexArray.length);
            }
            completedSuccessfully = true;
        } catch (Exception e) {
            //TODO: ask Ethan if interrupt exception should be thrown from within code or should 'return' just be used?
            network.putClientData("MCODE_running", new Boolean(false));
            taskMonitor.setException(e, "MCODE cancelled");
        }
    }

    /**
     * @return true if the task has completed successfully
     */
    public boolean isCompletedSuccessfully() {
        return completedSuccessfully;
    }

    /**
     * Get computed clusters once MCODE has been run.  Will be null if not computed.
     *
     * @return ArrayList of computed clusters
     */
    public ArrayList getClusters() {
        return clusters;
    }

    /**
     * Get image list of computed clusters to be used for display
     *
     * @return Array of images
     */
    public Image[] getImageList() {
        return imageList;
    }

    /**
     * Non-blocking call to interrupt the task.
     */
    public void halt() {
        this.interrupted = true;
        alg.setCancelled(true);
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return human readable task title.
     */
    public String getTitle() {
        return new String("MCODE Network Cluster Detection");
    }
}

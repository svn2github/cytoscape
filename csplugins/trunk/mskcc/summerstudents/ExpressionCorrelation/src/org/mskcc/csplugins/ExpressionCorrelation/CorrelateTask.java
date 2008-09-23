package org.mskcc.csplugins.ExpressionCorrelation;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

/**
 * Copyright (c) 2007
 * *
 * * Code written by: Shirley Hui
 * * Authors: Gary Bader, Elena Potylitsine, Chris Sander, Weston Whitaker
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
 * * User: Shirley Hui
 * * Date: Apr 4, 2007
 * * Time: 3:35:11 April 7, 2004
 * * Description: CorrelateTask
 * *
 * *
 */


/**
 * This class represents a task that calls methods to calculate a correlation network and/or corresponding histogram
 */
public class CorrelateTask implements Task {

    private CorrelateSimilarityNetwork network;
    private TaskMonitor taskMonitor = null;
    private CorrelateHistogramWindow histogram = null;
    
    boolean colDone = false;         // True when the column network is complete

    /**
     * Sets which event thread to run and time
     * case 1: Run and time both the column and row networks
     * case 2: Run and time the column network
     * case 3: Run and time the column histogram
     * case 4: Run and time the row network
     * case 5: Run and time the row histogram
     */
    public int event = 0;

    /**
     * Constructor.
     *
     * @param event Indicates which event to run.
     * @param network The network upon which to calculate correlations
     */
    public CorrelateTask(int event, CorrelateSimilarityNetwork network) {

        this.network = network;
        this.event = event;
    }

    /**
     * Perform the correlation calculations on the network.
     */
    public void run() {
        if (network.getTaskMonitor() == null) {
            throw new IllegalStateException("Task Monitor is not set.");
        }
        
        switch (event) {
            case 1:
                colRun();
                rowRun();
                break;
            case 2:
                colRun();
                break;
            case 3:
                colHistogram();
                break;
            case 4:
                rowRun();
                break;
            case 5:
                rowHistogram();
                break;
        }
    }


    private void cleanup(String networkId)
    {
        System.out.println("Removing network with id: " + networkId);
        if (networkId != null)
        {
            CyNetwork destroyNetwork = Cytoscape.getNetwork(networkId);
            Cytoscape.destroyNetwork( destroyNetwork );
        }

        System.out.println("Finished destroying Network");

    }

    /**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
        network.cancel();
        System.out.println("User cancelled task.");
        if (histogram!=null)
        {
            System.out.println("Disposing histogram dialog");
            histogram.dispose();
            histogram.setVisible(false);
        }
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) {
        if (this.taskMonitor != null) {
            throw new IllegalStateException("Task Monitor is already set.");
        }
        network.setTaskMonitor(taskMonitor);
    }

    /**
     * Gets the Task Title.
     *
     * @return human readable task title.
     */
    public String getTitle() {
        return new String("Performing correlation calculations");
    }

    /**
     * The condition matrix calculation
     */
    public void colRun() {
        if (Cytoscape.getExpressionData() != null) {
            System.out.println("Starting colRun");
            
            CyNetwork cyNetwork = network.calcCols();
            String conditionNetworkId = cyNetwork.getIdentifier();
            if (network.cancelled())
            {
                cleanup(conditionNetworkId);
            }
            System.out.println("Finished colRun");
        }
    }

    /**
     * The gene matrix calculation
     */
    public void rowRun() {

        if (Cytoscape.getExpressionData() != null) {
            System.out.println("Starting rowRun");
            CyNetwork cyNetwork = network.calcRows();
            String geneNetworkId = cyNetwork.getIdentifier();
            if (network.cancelled())
            {
                cleanup(geneNetworkId);
            }
            System.out.println("Finished rowRun");
        }
    }

    /**
     * The condition histogram calculation
     */
    public void colHistogram() {
        if (Cytoscape.getExpressionData() != null) {
            System.out.println("Starting colHistogram");
            histogram = new CorrelateHistogramWindow(Cytoscape.getDesktop(), false, network); //not row histogram
            histogram.pack();
            histogram.setVisible(true);
        }
    }

    /**
     * The gene histogram calculation
     */
    public void rowHistogram() {
        if (Cytoscape.getExpressionData() != null) {
            System.out.println("Starting rowHistogram");
            histogram = new CorrelateHistogramWindow(Cytoscape.getDesktop(), true, network); //row histogram
            histogram.pack();
            histogram.setVisible(true);
        }
    }
}
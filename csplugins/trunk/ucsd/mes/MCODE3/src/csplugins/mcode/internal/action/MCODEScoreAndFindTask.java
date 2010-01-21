package csplugins.mcode.internal.action;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import csplugins.mcode.internal.MCODEAlgorithm;
import csplugins.mcode.internal.MCODECluster;
import csplugins.mcode.internal.MCODEUtil;
import csplugins.mcode.internal.MCODEClustersToNestedNetworks;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.util.CyNetworkNaming;
import cytoscape.groups.CyGroup;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

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

/**
 * MCODE Score network and find cluster task.
 */
public class MCODEScoreAndFindTask implements Task {
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;
	private CyNetwork network = null;
	private MCODEAlgorithm alg = null;
	private MCODECluster[] clusters = null;
	private Image imageList[] = null;
	private boolean completedSuccessfully = false;
	private int analyze;
	private String resultSet;

	/**
	 * Scores and finds clusters in a given network
	 * 
	 * @param network
	 *            The network to cluster
	 * @param analyze
	 *            Tells the task if we need to rescore and/or refind
	 * @param resultSet
	 *            Identifier of the current result set
	 * @param alg
	 *            reference to the algorithm for this network
	 */
	public MCODEScoreAndFindTask(CyNetwork network, int analyze,
			String resultSet, MCODEAlgorithm alg) {
		this.network = network;
		this.analyze = analyze;
		this.resultSet = resultSet;
		this.alg = alg;
	}

	/**
	 * Run MCODE (Both score and find steps)
	 */
	public void run() {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		try {
			// run MCODE scoring algorithm - node scores are saved in the alg
			// object
			alg.setTaskMonitor(taskMonitor, network.getIdentifier());
			// only (re)score the graph if the scoring parameters have been
			// changed
			if (analyze == MCODEScoreAndFindAction.RESCORE) {
				taskMonitor.setPercentCompleted(0);
				taskMonitor.setStatus("Scoring Network (Step 1 of 3)");
				alg.scoreGraph(network, resultSet);
				if (interrupted) {
					return;
				}
				System.err.println("Network was scored in "
						+ alg.getLastScoreTime() + " ms.");
			}

			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Finding Clusters (Step 2 of 3)");

			clusters = alg.findClusters(network, resultSet);
			
			if (interrupted)
				return;

			taskMonitor.setPercentCompleted(0);
			taskMonitor.setStatus("Generating Nested Networks (Step 3 of 3)");

			MCODEClustersToNestedNetworks.convert(clusters);

			completedSuccessfully = true;
//			Cytoscape.getSwingPropertyChangeSupport().firePropertyChange("MODULE_SEARCH_FINISHED", null, null );
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: ask Ethan if interrupt exception should be thrown from
			// within code or should 'return' just be used?
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
	 * Get computed clusters once MCODE has been run. Will be null if not
	 * computed.
	 * 
	 * @return ArrayList of computed clusters
	 */
	public MCODECluster[] getClusters() {
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
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
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

	public MCODEAlgorithm getAlg() {
		return alg;
	}
}

/* 
  File: ClusterTask.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package clusterMaker.ui;

import clusterMaker.algorithms.ClusterAlgorithm;

import cytoscape.Cytoscape;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import cytoscape.task.ui.JTaskConfig;

/**
 * A wrapper for applying a cluster in a task. Use it something like
 * this:
 * <p>TaskManager.executeTask( new ClusterTask(cluster), ClusterTask.getDefaultTaskConfig() );

 */
public class ClusterTask implements Task {

	ClusterAlgorithm cluster;
	ClusterSettingsDialog dialog;
	TaskMonitor monitor;
	boolean done = false;

	/**
	 * Creates the task.
	 * 
	 * @param cluster The CyClusterAlgorithm to apply.
	 */
	public ClusterTask(ClusterAlgorithm cluster, ClusterSettingsDialog dialog) {
		this.cluster = cluster; 
		this.dialog = dialog;
		this.done = false;
	}

	/**
	 * Sets the task monitor to be used for the cluster. 
	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Run the algorithm.  
	 */
	public void run() {
		done = false;
		cluster.doCluster(monitor);
		if (dialog != null)
			dialog.updateVizButton();
		done = true;
	}

	/**
	 * Halt the algorithm if the ClusterAlgorithm supports it.
	 */
	public void halt() {
		cluster.halt();
	}

	/**
 	 * For callers that want to know when we're done...
 	 */
	public boolean done() { return done; }

	/**
	 * Get the "nice" title of this algorithm
	 *
	 * @return algorithm title
	 */
	public String getTitle() {
		return "Performing " + cluster.toString();
	}

	/**
	 * This method returns a default TaskConfig object.
	 * @return a default JTaskConfig object.
	 */
	public static JTaskConfig getDefaultTaskConfig() {
		return ClusterTask.getDefaultTaskConfig(true);
	}

	/**
	 * This method returns a default TaskConfig object.
	 * @param includeClose whether to include the close button
	 * @return a default JTaskConfig object.
	 */
	public static JTaskConfig getDefaultTaskConfig(boolean includeClose) {
		JTaskConfig result = new JTaskConfig();

		result.displayCancelButton(true);
		result.displayCloseButton(includeClose);
		result.displayStatus(true);
		result.displayTimeElapsed(false);
		if (includeClose)
			result.setAutoDispose(false);
		else
			result.setAutoDispose(true);
		result.setModal(true);
		if (includeClose)
			result.setMillisToPopup(1);
		result.setOwner(Cytoscape.getDesktop());

		return result;
	}
}

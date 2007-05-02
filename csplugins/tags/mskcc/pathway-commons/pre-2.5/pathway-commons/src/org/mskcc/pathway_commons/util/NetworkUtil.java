// $Id: NetworkUtil.java,v 1.12 2007/05/01 15:56:45 grossb Exp $
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
package org.mskcc.pathway_commons.util;

// imports
import org.mskcc.pathway_commons.task.MergeNetworkTask;

import org.mskcc.biopax_plugin.util.cytoscape.LayoutUtil;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;

import java.net.URL;
import java.net.MalformedURLException;

/**
 * This is a network utilities class.
 *
 * @author Benjamin Gross.
 */
public class NetworkUtil extends Thread {

	/**
	 * ref to pathwayCommonsURL
	 */
	private String pathwayCommonsRequest;

	/**
	 * ref to cyNetwork
	 */
	private CyNetwork cyNetwork;

	/**
	 * boolean indicated if we are merging
	 */
	private boolean merging;

	/**
	 * Constructor.
	 *
	 * @param pathwayCommonsRequest String
	 * @param cyNetwork CyNetwork
	 * @param merging boolean
	 */
	public NetworkUtil(String pathwayCommonsRequest, CyNetwork cyNetwork, boolean merging) {

		// init member vars
		this.pathwayCommonsRequest = pathwayCommonsRequest;
		this.cyNetwork = cyNetwork;
		this.merging = merging;
	}

	/**
	 * Our implementation of run.
	 */
	public void run() {

		try {
			URL pathwayCommonsURL = new URL(pathwayCommonsRequest);

			// are we merging ?
			if (merging) {
				// start merge network task
				TaskManager.executeTask(new MergeNetworkTask(pathwayCommonsURL, cyNetwork),
										setupTask());
				postProcess(cyNetwork, true);
			}
			else {
				// use cytoscape's canned load network task
				LoadNetworkTask.loadURL(pathwayCommonsURL, true);
				postProcess(Cytoscape.getCurrentNetwork(), false);
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to setup cytoscape task
	 */
	private JTaskConfig setupTask() {

		// configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);

		// outta here
		return jTaskConfig;
	}

	/**
	 * Method for any post processing of recently loaded network.
	 *
	 * @param cyNetwork CyNetwork
	 * @param doLayout boolean
	 */
	private void postProcess(CyNetwork cyNetwork, boolean doLayout) {

		// we like the BioPax plugin default layout
		// so we don't layout here
		if (doLayout) {
			CyNetworkView view = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
			LayoutUtil.layout(view);
			view.fitContent();
		}

		// set focus current
		Cytoscape.firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUS,
									 null, cyNetwork.getIdentifier());
	}
}

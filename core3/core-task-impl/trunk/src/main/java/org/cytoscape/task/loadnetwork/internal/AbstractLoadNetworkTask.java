/*
 File: AbstractLoadNetworkTask.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.task.loadnetwork.internal;

import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;
import java.io.IOException;

import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cytoscape.CyNetworkManager;
import cytoscape.util.CyNetworkNaming;

/**
 * Task to load a new network.
 */
abstract class AbstractLoadNetworkTask implements Task {

	protected CyReader reader;
	protected URI uri;
	protected TaskMonitor taskMonitor;
	protected String name;
	protected Thread myThread = null;
	protected boolean interrupted = false;
	protected CyReaderManager mgr;
	protected GraphViewFactory gvf;
	protected CyLayouts cyl;
	protected CyNetworkManager netmgr;
	protected Properties props;

	protected CyNetworkNaming namingUtil;

	public AbstractLoadNetworkTask(CyReaderManager mgr, GraphViewFactory gvf,
			CyLayouts cyl, CyNetworkManager netmgr, Properties props,
			CyNetworkNaming namingUtil) {
		this.mgr = mgr;
		this.gvf = gvf;
		this.cyl = cyl;
		this.netmgr = netmgr;
		this.props = props;
		this.namingUtil = namingUtil;
	}

	protected void loadNetwork(CyReader reader) throws Exception {
		if (reader == null)
			throw new Exception("Could not read file: file reader was null");

		try {
			myThread = Thread.currentThread();

			taskMonitor.setStatusMessage("Reading in Network Data...");

			taskMonitor.setProgress(-1.0);

			taskMonitor.setStatusMessage("Creating Cytoscape Network...");

			reader.read();

			CyNetwork cyNetwork = reader.getReadData(CyNetwork.class);
			cyNetwork.attrs().set("name",
					namingUtil.getSuggestedNetworkTitle(name, netmgr));
			GraphView view = reader.getReadData(GraphView.class);

			if (view == null)
				view = gvf.createGraphView(cyNetwork);

			// TODO NEED RENDERER
			view.fitContent();

			netmgr.addNetwork(cyNetwork);
			netmgr.addNetworkView(view);

			if (cyNetwork != null) {
				informUserOfGraphStats(cyNetwork);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not read network from: ");
				sb.append(name);
				sb.append("\nThis file may not be a valid file format.");
				throw new IOException(sb.toString());
			}

			taskMonitor.setProgress(1.0);

		} finally {
			reader = null;
		}
	}

	abstract public void run(TaskMonitor taskMonitor) throws Exception;

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfGraphStats(CyNetwork newNetwork) {
		NumberFormat formatter = new DecimalFormat("#,###,###");
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Successfully loaded network from:  ");
		sb.append(name);
		sb.append("\n\nNetwork contains "
				+ formatter.format(newNetwork.getNodeCount()));
		sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
		sb.append(" edges.\n\n");

		String thresh = props.getProperty("viewThreshold");

		if (newNetwork.getNodeCount() < Integer.parseInt(thresh)) {
			sb.append("Network is under " + thresh
					+ " nodes.  A view will be automatically created.");
		} else {
			sb.append("Network is over " + thresh
					+ " nodes.  A view has not been created."
					+ "  If you wish to view this network, use "
					+ "\"Create View\" from the \"Edit\" menu.");
		}

		taskMonitor.setStatusMessage(sb.toString());
	}

	public void cancel() {
		// Task can not currently be halted.
		System.out.println("Halt called");

		if (myThread != null) {
			myThread.interrupt();
			this.interrupted = true;
			if (taskMonitor != null) {
				taskMonitor.setProgress(1.0);
				taskMonitor.setStatusMessage("Task cancelled");
			}
		}
	}
}

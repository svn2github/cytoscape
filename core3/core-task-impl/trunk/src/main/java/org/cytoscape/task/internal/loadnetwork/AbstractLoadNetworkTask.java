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

package org.cytoscape.task.internal.loadnetwork;

import java.io.IOException;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

import org.cytoscape.io.read.CyNetworkViewProducer;
import org.cytoscape.io.read.CyNetworkViewProducerManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.AbstractTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;

/**
 * Task to load a new network.
 */
abstract class AbstractLoadNetworkTask extends AbstractTask {

	protected CyNetworkViewProducer reader;
	protected URI uri;
	protected TaskMonitor taskMonitor;
	protected String name;
	protected boolean interrupted = false;
	protected CyNetworkViewProducerManager mgr;
	protected CyNetworkManager netmgr;
	protected Properties props;
	protected CyNetworkNaming namingUtil;

	public AbstractLoadNetworkTask(CyNetworkViewProducerManager mgr, CyNetworkManager netmgr,
			Properties props, CyNetworkNaming namingUtil) {
		this.mgr = mgr;
		this.netmgr = netmgr;
		this.props = props;
		this.namingUtil = namingUtil;
	}

	protected void loadNetwork(final CyNetworkViewProducer viewProducer) throws Exception {
		
		if (viewProducer == null)
			throw new IllegalArgumentException("Could not read file: Network View Producer is null.");

		taskMonitor.setStatusMessage("Reading in Network Data...");
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Creating Cytoscape Network...");
		
		viewProducer.run(taskMonitor);
		
		final CyNetworkView[] cyNetworkViews = viewProducer.getNetworkViews();

		if (cyNetworkViews == null || cyNetworkViews.length < 0)
			throw new IOException("Could not create network for the producer.");

		for ( CyNetworkView view : cyNetworkViews ) {

			// Model should not be null.  It will be tested in ViewImpl.
			final CyNetwork cyNetwork = view.getModel();
			cyNetwork.attrs().set("name", namingUtil.getSuggestedNetworkTitle(name));

			netmgr.addNetwork(cyNetwork);
			netmgr.addNetworkView(view);
			
			view.fitContent();

			informUserOfGraphStats(cyNetwork);
		}

		taskMonitor.setProgress(1.0);
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
		super.cancel();
		if (reader != null) {
			reader.cancel();
		}
	}
}

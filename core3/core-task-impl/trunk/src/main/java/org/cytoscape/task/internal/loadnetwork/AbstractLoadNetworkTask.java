/*
 File: AbstractLoadNetworkTask.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * Task to load a new network.
 */
abstract public class AbstractLoadNetworkTask extends AbstractTask {
    protected CyNetworkViewReader reader;
    protected URI uri;
    protected TaskMonitor taskMonitor;
    protected String name;
    protected boolean interrupted = false;
    protected CyNetworkViewReaderManager mgr;
    protected CyNetworkManager networkManager;
    protected CyNetworkViewManager networkViewManager;
    protected Properties props;
    protected CyNetworkNaming namingUtil;

    public AbstractLoadNetworkTask(final CyNetworkViewReaderManager mgr, final CyNetworkManager networkManager,
	    final CyNetworkViewManager networkViewManager, final Properties props, final CyNetworkNaming namingUtil) {
	this.mgr = mgr;
	this.networkManager = networkManager;
	this.networkViewManager = networkViewManager;
	this.props = props;
	this.namingUtil = namingUtil;
    }

    protected void loadNetwork(final CyNetworkViewReader viewReader) throws Exception {
	if (viewReader == null)
	    throw new IllegalArgumentException("Could not read file: Network View Reader is null.");

	taskMonitor.setStatusMessage("Reading in Network Data...");
	taskMonitor.setProgress(-1.0);
	taskMonitor.setStatusMessage("Creating Cytoscape Network...");

	insertTasksAfterCurrentTask(viewReader, new GenerateNetworkViewsTask(name, viewReader, networkManager,
		networkViewManager, namingUtil, props));
    }

    @Override
    abstract public void run(TaskMonitor taskMonitor) throws Exception;
}

class GenerateNetworkViewsTask extends AbstractTask {
    private final String name;
    private final CyNetworkViewReader viewReader;
    private final CyNetworkManager networkManager;
    private final CyNetworkViewManager networkViewManager;
    private final CyNetworkNaming namingUtil;
    private final Properties props;

    GenerateNetworkViewsTask(final String name, final CyNetworkViewReader viewReader,
	    final CyNetworkManager networkManager, final CyNetworkViewManager networkViewManager,
	    final CyNetworkNaming namingUtil, final Properties props) {
	this.name = name;
	this.viewReader = viewReader;
	this.networkManager = networkManager;
	this.networkViewManager = networkViewManager;
	this.namingUtil = namingUtil;
	this.props = props;
    }

    public void run(final TaskMonitor taskMonitor) throws Exception {
	final CyNetworkView[] cyNetworkViews = viewReader.getNetworkViews();

	if (cyNetworkViews == null || cyNetworkViews.length < 0)
	    throw new IOException("Could not create network for the producer.");

	for (CyNetworkView view : cyNetworkViews) {
	    if (cancelled)
		return;

	    final CyNetwork cyNetwork = view.getModel();
	    cyNetwork.getCyRow().set(CyTableEntry.NAME, namingUtil.getSuggestedNetworkTitle(name));
	    networkManager.addNetwork(cyNetwork);
	    
	    // Do the following only for non-null views.
	    if (view.isEmptyView() == false) {
		networkViewManager.addNetworkView(view);
		view.fitContent();
	    } else {
		view = null;
	    }

	    informUserOfGraphStats(cyNetwork, taskMonitor);
	}

	taskMonitor.setProgress(1.0);
    }

    /**
     * Inform User of Network Stats.
     */
    private void informUserOfGraphStats(final CyNetwork newNetwork, final TaskMonitor taskMonitor) {
	NumberFormat formatter = new DecimalFormat("#,###,###");
	StringBuffer sb = new StringBuffer();

	// Give the user some confirmation
	sb.append("Successfully loaded network from:  ");
	sb.append(name);
	sb.append("\n\nNetwork contains " + formatter.format(newNetwork.getNodeCount()));
	sb.append(" nodes and " + formatter.format(newNetwork.getEdgeCount()));
	sb.append(" edges.\n\n");

	String thresh = props.getProperty("viewThreshold");

	if (newNetwork.getNodeCount() < Integer.parseInt(thresh)) {
	    sb.append("Network is under " + thresh + " nodes.  A view will be automatically created.");
	} else {
	    sb.append("Network is over " + thresh + " nodes.  A view has not been created."
		    + "  If you wish to view this network, use " + "\"Create View\" from the \"Edit\" menu.");
	}

	taskMonitor.setStatusMessage(sb.toString());
    }
}

/*
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

package org.cytoscape.search.ui.tasks;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.store.RAMDirectory;

import org.cytoscape.search.*;
import org.cytoscape.search.internal.EnhancedSearchFactoryImpl;
import org.cytoscape.search.internal.EnhancedSearchIndexImpl;
import org.cytoscape.search.internal.EnhancedSearchQueryImpl;
import org.cytoscape.search.ui.SearchPanelFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskMonitor;

public class IndexAndSearchTaskImpl implements IndexAndSearchTask {

	private CyNetworkManager netmgr;
	private String query = null;
	private TaskMonitor taskMonitor;
	private boolean interrupted = false;
	
	/**
	 * Constructor.
	 * 
	 * @param network
	 *            Network to execute query on.
	 * @param query
	 *            Query string.
	 */
	public IndexAndSearchTaskImpl(CyNetworkManager nm, String query) {
		this.netmgr = nm;
		this.query = query;
	}

	/**
	 * Executes Task: IndexAndSearch
	 */
	public void run(TaskMonitor tm) {
		this.taskMonitor = tm;
		final CyNetwork network = netmgr.getCurrentNetwork();
		System.out.println("I am in IndexandSearchTask Service");
		EnhancedSearchFactoryImpl esf = new EnhancedSearchFactoryImpl();
		final EnhancedSearch enhancedSearch = esf
				.getGlobalEnhancedSearchInstance();

		// Index the given network or use existing index
		RAMDirectory idx = null;

		String status = enhancedSearch.getNetworkIndexStatus(network);
		if (status == EnhancedSearch.INDEX_SET) {
			idx = enhancedSearch.getNetworkIndex(network);
		} else {
			taskMonitor.setStatusMessage("Indexing network");
			EnhancedSearchIndex indexHandler = new EnhancedSearchIndexImpl(
					network);
			idx = indexHandler.getIndex();
			enhancedSearch.setNetworkIndex(network, idx);

			if (interrupted) {
				return;
			}
		}

		// Execute query
		taskMonitor.setStatusMessage("Executing query");
		EnhancedSearchQuery queryHandler = new EnhancedSearchQueryImpl(idx,
				network);
		queryHandler.executeQuery(query);

		if (interrupted) {
			return;
		}

		int hitCount = queryHandler.getHitCount();
		if (hitCount == 0) {
			System.out.println("No hits. ");
			return;
		}
		System.out.println("There are " + hitCount + " hits.");
		taskMonitor.setStatusMessage("Displaying " + hitCount + " hits");

		int numCompleted = 0;
		ArrayList<CyNode> nodeList = queryHandler.getNodeHits();
		numCompleted = 50;
		ArrayList<CyEdge> edgeList = queryHandler.getEdgeHits();

		int percentCompleted = (numCompleted * 100 / hitCount);
		taskMonitor.setProgress(percentCompleted);

		String result = SearchPanelFactory.getGlobalInstance(netmgr)
				.getmainPanel().getResult();
		System.out.println("Selected Option: " + result);

		CyNetworkView view = netmgr.getCurrentNetworkView();
		if (result.equals("Select")) {
			SelectUtils.setSelectedNodes(nodeList, true);
			SelectUtils.setSelectedEdges(edgeList, true);

			if (view != null) {
				view.updateView();
			}

		} else if (result.equals("Show")) {
			if (view != null) {
				List<CyNode> compnodelist = network.getNodeList();
				for (CyNode n : nodeList) {
					compnodelist.remove(n);
				}
				HideUtils.setVisibleNodes(compnodelist, false, view);

				List<CyEdge> compedgelist = network.getEdgeList();
				for (CyEdge e : edgeList) {
					compedgelist.remove(e);
				}
				HideUtils.setVisibleEdges(compedgelist, false, view);
			}

		} else {
			if (view != null) {
				HideUtils.setVisibleNodes(nodeList, false, view);
				HideUtils.setVisibleEdges(edgeList, false, view);
			}
		}

	}

	/**
	 * DOCUMENT ME!
	 */
	public void halt() {
		this.interrupted = true;
	}

	public void cancel() {
		this.interrupted = true;
	}

	/**
	 * Sets the TaskMonitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 * @throws IllegalThreadStateException
	 *             Illegal Thread State.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets Title of Task.
	 * 
	 * @return Title of Task.
	 */
	public String getTitle() {
		return "Searching the network";
	}
}
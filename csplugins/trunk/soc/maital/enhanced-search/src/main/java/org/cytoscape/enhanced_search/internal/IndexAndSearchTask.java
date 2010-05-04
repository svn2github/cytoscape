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

package org.cytoscape.enhanced_search.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.lucene.store.RAMDirectory;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.task.AbstractNetworkViewTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

public class IndexAndSearchTask extends AbstractNetworkViewTask {

	private boolean interrupted = false;
	private EnhancedSearch enhancedSearch;
	private CyNetwork network;
	
	@Tunable(description="Search for:")
	public String query;

	/**
	 * The constructor. Any necessary data that is <i>not</i> provided by 
	 * the user should be provided as arguments to the constructor.  
	 */
	public IndexAndSearchTask(final CyNetworkView networkView, EnhancedSearch enhancedSearch) {
		// Will set a CyNetwork field called "net".
		super(networkView);
		network = networkView.getSource();
		this.enhancedSearch = enhancedSearch;
	}

    @Override
	public void run(final TaskMonitor taskMonitor) {

		// Give the task a title.
		taskMonitor.setTitle("Searching the network");

		
		// Index the given network or use existing index
		RAMDirectory idx = null;
		
		String status = enhancedSearch.getNetworkIndexStatus(network);
		if (status == EnhancedSearch.INDEX_SET) {
			idx = enhancedSearch.getNetworkIndex(network);
		} else {
			taskMonitor.setStatusMessage("Indexing network");
			EnhancedSearchIndex indexHandler = new EnhancedSearchIndex(network);
			idx = indexHandler.getIndex();
			enhancedSearch.setNetworkIndex(network, idx);
		}

		if (interrupted) {
			return;
		}
		

		// Execute query
		taskMonitor.setStatusMessage("Executing query");
		EnhancedSearchQuery queryHandler = new EnhancedSearchQuery(network, idx);
		queryHandler.executeQuery(query);

		if (interrupted) {
			return;
		}

		// Display results
		for (CyNode n : network.getNodeList()) {
			n.attrs().set("selected",false);
    	}
		for (CyEdge e : network.getEdgeList()) {
			e.attrs().set("selected",false);
		}

//		Cytoscape.getCurrentNetworkView().updateView();

		int nodeHitCount = queryHandler.getNodeHitCount();
		int edgeHitCount = queryHandler.getEdgeHitCount();
		if (nodeHitCount == 0 && edgeHitCount == 0) {
			System.out.println("No hits. ");
			return;
		}
		System.out.println("There are " + nodeHitCount + " node hits.");
		System.out.println("There are " + edgeHitCount + " edge hits.");

		taskMonitor.setStatusMessage("Selecting " + nodeHitCount + " and " + edgeHitCount + " edges");

		ArrayList<String> nodeHits = queryHandler.getNodeHits();
		ArrayList<String> edgeHits = queryHandler.getEdgeHits();


		Iterator nodeIt = nodeHits.iterator();
		int numCompleted = 0;
		while (nodeIt.hasNext() && !interrupted) {
		    int currESPIndex = Integer.parseInt(nodeIt.next().toString());
			CyNode currNode = network.getNode(currESPIndex);
			if (currNode != null) {
				currNode.attrs().set("selected", true);
			} else {
				System.out.println("Unknown node identifier " + (currESPIndex));
			}

			taskMonitor.setProgress(numCompleted++ / nodeHitCount);
		}

		Iterator edgeIt = edgeHits.iterator();
		numCompleted = 0;
		while (edgeIt.hasNext() && !interrupted) {
		    int currESPIndex = Integer.parseInt(edgeIt.next().toString());
			CyEdge currEdge = network.getEdge(currESPIndex);
			if (currEdge != null) {
				currEdge.attrs().set("selected", true);
			} else {
				System.out.println("Unknown edge identifier " + (currESPIndex));
			}

			taskMonitor.setProgress(numCompleted++ / edgeHitCount);
		}


		// Refresh view to show selected nodes and edges
		view.updateView();
//		Cytoscape.getCurrentNetworkView().updateView();

	}


    @Override
    public void cancel() {
		this.interrupted = true;
    }
}
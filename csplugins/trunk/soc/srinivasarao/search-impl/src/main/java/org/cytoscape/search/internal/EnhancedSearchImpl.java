
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

package org.cytoscape.search.internal;

import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.search.EnhancedSearch;

import org.apache.lucene.store.RAMDirectory;


/**
 * This object keeps the indexes of each network loaded in Cytoscape,
 * and the indexing status of each network.
 * It supplies methods to get and set indexes and indexing status for a given network.
*/
public class EnhancedSearchImpl implements EnhancedSearch {

	// Keeps the index for each network
	private HashMap<CyNetwork,RAMDirectory> networkIndexMap = new HashMap<CyNetwork,RAMDirectory>();

	// Keeps indexing status of each network
	private HashMap<CyNetwork,String> networkIndexStatusMap = new HashMap<CyNetwork,String>();

	//private CyNetwork network;

	/**
	 * Creates a new EnhancedSearchImpl object.
	 *
	 * @param network        CyNetwork object
	 */
	public EnhancedSearchImpl() {
		//this.network = network;
	}

	public void addNetwork(CyNetwork network){
		EnhancedSearchIndexImpl esi = new  EnhancedSearchIndexImpl(network);
		setNetworkIndex(network,esi.getIndex());
	}
	
	/**
	 * Removes the specified network from the global index. To free up memory,
	 * this method should be called whenever a network is destroyed.
	 * 
	 * @param network        CyNetwork object
	 */
	public synchronized void removeNetworkIndex(CyNetwork network) {
		networkIndexMap.remove(network);
		networkIndexStatusMap.remove(network);
	}

	/**
	 * Gets the index associated with the specified network.
	 * 
	 * @param network        CyNetwork object
	 * @return               the index for this network
	 */
	public synchronized RAMDirectory getNetworkIndex(CyNetwork network) {
		return (RAMDirectory) networkIndexMap.get(network);
	}

	/**
	 * Gets the indexing status of a specified network.
	 * 
	 * @param network        CyNetwork object
	 * @return               network indexing status
	 */
	public synchronized String getNetworkIndexStatus(CyNetwork network) {
		return (String) networkIndexStatusMap.get(network);
	}

	/**
	 * Sets the index for the specified network.
	 * 
	 * @param network        CyNetwork object
	 * @param index          the index that suits this network
	 */
	public synchronized void setNetworkIndex(CyNetwork network, RAMDirectory index) {
		networkIndexMap.put(network, index);
		networkIndexStatusMap.put(network, INDEX_SET);
	}

	/**
	 * Sets the indexing status of the specified network.
	 * 
	 * @param network        CyNetwork object
	 * @param status         the indexing status required for this network
	 */
	public synchronized void setNetworkIndexStatus(CyNetwork network, String status) {
		if (status == INDEX_SET || status == REINDEX) {
			networkIndexStatusMap.put(network, status);
		} else {
			System.out.println("Invalid status '" + status + "'");
		}
	}	

	
}

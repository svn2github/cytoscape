/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
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
 *
 * User: Vuk Pavlovic
 * Date: Nov 29, 2006
 * Time: 5:34:46 PM
 * Description: Stores various cluster information for simple get/set purposes
 */
package org.cytoscape.view.ui.networkpanel.internal;

import giny.model.GraphPerspective;

import java.util.List;
import java.util.Map;

import cytoscape.CyNetwork;
import ding.view.DGraphView;


/**
 * Stores various cluster information for simple get/set purposes.
 */
public class MCODECluster {
	private List<Integer> alCluster = null;
	private GraphPerspective gpCluster = null;
	private DGraphView dgView = null; //keeps track of layout so that layout process doesn't have to be repeated unecessarily
	private Integer seedNode;
	private Map<Integer, Boolean> nodeSeenHashMap; //stores the nodes that have already been included in higher ranking clusters
	private double clusterScore;
	private String clusterName; //Pretty much unsed so far, but could store name by user's input
	private int rank;
	private String resultTitle;
	
	// Added by kono: parent network of this cluster
	private CyNetwork parent;

	/**
	 * Creates a new MCODECluster object.
	 */
	public MCODECluster(CyNetwork parent) {
		this.parent = parent;
	}
	
	public CyNetwork getParentNetwork() {
		return this.parent;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getResultTitle() {
		return resultTitle;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param resultTitle DOCUMENT ME!
	 */
	public void setResultTitle(String resultTitle) {
		this.resultTitle = resultTitle;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getClusterName() {
		return clusterName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param clusterName DOCUMENT ME!
	 */
	public void setClusterName(final String clusterName) {
		this.clusterName = clusterName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public DGraphView getDGView() {
		return dgView;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param dgView DOCUMENT ME!
	 */
	public void setDGView(DGraphView dgView) {
		this.dgView = dgView;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getClusterScore() {
		return clusterScore;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param clusterScore DOCUMENT ME!
	 */
	public void setClusterScore(double clusterScore) {
		this.clusterScore = clusterScore;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public GraphPerspective getGPCluster() {
		return gpCluster;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param gpCluster DOCUMENT ME!
	 */
	public void setGPCluster(GraphPerspective gpCluster) {
		this.gpCluster = gpCluster;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<Integer> getALCluster() {
		return alCluster;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param alCluster DOCUMENT ME!
	 */
	public void setALCluster(List<Integer> alCluster) {
		this.alCluster = alCluster;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Integer getSeedNode() {
		return seedNode;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param seedNode DOCUMENT ME!
	 */
	public void setSeedNode(Integer seedNode) {
		this.seedNode = seedNode;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<Integer, Boolean> getNodeSeenHashMap() {
		return nodeSeenHashMap;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeSeenHashMap DOCUMENT ME!
	 */
	public void setNodeSeenHashMap(Map<Integer, Boolean> nodeSeenHashMap) {
		this.nodeSeenHashMap = nodeSeenHashMap;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getRank() {
		return rank;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param rank DOCUMENT ME!
	 */
	public void setRank(int rank) {
		this.rank = rank;
		this.clusterName = "Cluster " + (rank + 1);
	}
}

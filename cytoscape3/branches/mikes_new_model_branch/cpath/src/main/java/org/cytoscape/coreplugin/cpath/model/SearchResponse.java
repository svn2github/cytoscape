/*
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
package org.cytoscape.coreplugin.cpath.model;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;

import java.util.ArrayList;


/**
 * Encapsulates a Search Response received by cPath.
 *
 * @author Ethan Cerami
 */
public class SearchResponse {
	/**
	 * Array List of DataService Interaction Objects.
	 */
	private ArrayList interactions;

	/**
	 * An Exception Object (if one has occurred).
	 */
	private Throwable exception;

	/**
	 * The Corresponding GraphPerspective in Cytoscape.
	 */
	private CyNetwork cyNetwork;

	/**
	 * The Corresponding GraphView in Cytoscape.
	 */
	private GraphView cyNetworkView;

	/**
	 * Gets List of Interaction Objects.
	 *
	 * @return ArrayList of Data Service Interaction Object.
	 */
	public ArrayList getInteractions() {
		return interactions;
	}

	/**
	 * Sets List of Interaction Objects.
	 *
	 * @param interactions ArrayList of Data Service Interaction Object.
	 */
	public void setInteractions(ArrayList interactions) {
		this.interactions = interactions;
	}

	/**
	 * Gets Exception (if one has occurred).
	 * If no exception has occurred, this method will return null.
	 *
	 * @return exception or null.
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Sets the Exception.
	 *
	 * @param exception Exception Object.
	 */
	public void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * Gets the GraphPerspective Corresponding to this list of interactions.
	 *
	 * @return Cytoscape GraphPerspective Object.
	 */
	public CyNetwork getGraphPerspective() {
		return cyNetwork;
	}

	/**
	 * Sets the GraphPerspective Corresponding to this list of interactions.
	 *
	 * @param cyNetwork Cytoscape GraphPerspective Object.
	 */
	public void setGraphPerspective(CyNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}

	/**
	 * Gets the GraphView Corresponding to this list of interactions.
	 *
	 * @return Cytoscape GraphView Object.
	 */
	public GraphView getGraphView() {
		return cyNetworkView;
	}

	/**
	 * Sets the GraphView Corresponding to this list of interactions.
	 *
	 * @param cyNetworkView Cytoscape GraphView Object.
	 */
	public void setGraphView(GraphView cyNetworkView) {
		this.cyNetworkView = cyNetworkView;
	}
}

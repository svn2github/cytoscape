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


/**
 * Encapsulates a Search Request and its corresponding Search Response.
 * Each SearchBundle is identified with a unique integer ID value.
 *
 * @author Ethan Cerami
 */
public class SearchBundle {
	/**
	 * The Search Request Object.
	 */
	private SearchRequest request;

	/**
	 * The Search Respone Object.
	 */
	private SearchResponse response;

	/**
	 * A Unique Identifier
	 */
	private int id;

	/**
	 * Stores the Next Bundle ID.
	 */
	private static int nextId = 0;

	/**
	 * Constructor.
	 *
	 * @param request  SearchRequest.
	 * @param response SearchResponse.
	 */
	public SearchBundle(SearchRequest request, SearchResponse response) {
		this.id = generateId();
		this.request = request;
		this.response = response;
	}

	/**
	 * Gets Unique Identifier.
	 *
	 * @return Unique Identifier.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets Search Request Object.
	 *
	 * @return Search Request.
	 */
	public SearchRequest getRequest() {
		return request;
	}

	/**
	 * Gets Search Response Object.
	 *
	 * @return Search Response.
	 */
	public SearchResponse getResponse() {
		return response;
	}

	/**
	 * Generates a Unique ID based on Static next_id variable.
	 *
	 * @return
	 */
	private synchronized int generateId() {
		return nextId++;
	}
}

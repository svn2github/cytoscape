package org.cytoscape.io.webservice.biomart;

import org.cytoscape.io.webservice.client.Query;


/**
 * Most basic implementation of Query interface.
 * This is mainly for simple REST clients.
 * 
 *
 */
public class BasicStringQuery implements Query {
	
	private final String query;
	
	public BasicStringQuery(final String query) {
		this.query = query;
	}

	@Override
	public String getQueryAsString() {
		return query;
	}

}

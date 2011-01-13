package org.cytoscape.io.webservice.biomart;

import org.cytoscape.io.webservice.client.Query;

public final class BiomartQuery implements Query {
	
	private final String xmlQuery;
	private final String keyColumnName;
	
	public BiomartQuery(final String xmlQuery, final String keyColumnName) {
		this.keyColumnName = keyColumnName;
		this.xmlQuery = xmlQuery;
	}
	
	public String getKeyColumnName () {
		return this.keyColumnName;
	}

	@Override
	public String getQueryAsString() {
		return xmlQuery;
	}

}

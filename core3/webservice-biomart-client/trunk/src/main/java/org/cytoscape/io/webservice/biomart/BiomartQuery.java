package org.cytoscape.io.webservice.biomart;


public final class BiomartQuery {
	
	private final String xmlQuery;
	private final String keyColumnName;
	
	public BiomartQuery(final String xmlQuery, final String keyColumnName) {
		this.keyColumnName = keyColumnName;
		this.xmlQuery = xmlQuery;
	}
	
	public String getKeyColumnName () {
		return this.keyColumnName;
	}


	public String getQueryString() {
		return xmlQuery;
	}

}

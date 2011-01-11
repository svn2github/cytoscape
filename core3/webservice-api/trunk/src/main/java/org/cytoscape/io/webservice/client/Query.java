package org.cytoscape.io.webservice.client;

/**
 * Wrapper interface for arbitrary query objects.
 * 
 * If client is JAX-RS based, Query is {@link MultivaluedMap}.
 * Otherwise, it is a class generated from WSDL.
 * 
 *
 */
public interface Query {
	
	/**
	 * Get the query as a string.
	 * 
	 * @return String version of query.
	 */
	String getQueryAsString();
}

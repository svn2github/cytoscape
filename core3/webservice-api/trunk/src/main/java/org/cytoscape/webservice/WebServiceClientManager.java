package org.cytoscape.webservice;

import java.net.URI;
import java.util.Set;

/**
 * Manager object of web service clients.
 * 
 * User bundles can access registered web services by injecting this object.
 *
 */
public interface WebServiceClientManager {
	
	/**
	 * Returns all registered services.
	 * 
	 * @return set of all registered web service clients.
	 * 
	 */
	Set<WebServiceClient<?>> getAllClients();
	
	
	/**
	 * Get a service client by URI.
	 * 
	 * @param serviceURI Service location as URI.
	 * 
	 * @return registered web service client.
	 * 
	 */
	WebServiceClient<?> getClient(final URI serviceURI);

}

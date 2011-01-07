package org.cytoscape.webservice;

import java.net.URI;
import java.util.Set;

public interface WebServiceClientManager {
	
	Set<WebServiceClient<?>> getAllClients();
	
	WebServiceClient<?> getClient(URI serviceURI);

}

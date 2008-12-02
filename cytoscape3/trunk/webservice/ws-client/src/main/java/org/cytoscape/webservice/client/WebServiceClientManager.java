package org.cytoscape.webservice.client;

import java.util.List;

import org.cytoscape.webservice.client.internal.CyWebServiceEventSupport;

public interface WebServiceClientManager {

	public WebServiceClient<?> getClient(String name);

	public List<WebServiceClient<?>> getAllClients();

	public CyWebServiceEventSupport getCyWebServiceEventSupport();

	public void registerClient(final WebServiceClient<?> client);

	public void removeClient(String serviceName);

}

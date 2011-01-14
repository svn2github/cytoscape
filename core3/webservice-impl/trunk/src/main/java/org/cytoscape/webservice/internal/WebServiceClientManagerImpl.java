/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.webservice.internal;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.webservice.WebServiceClient;
import org.cytoscape.io.webservice.WebServiceClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Web Service Client Manager manages available web service
 * clients in Cytoscape.  All clients will be loaded & registered through
 * Plugin Manager architecture.
 *
 *
 */
public class WebServiceClientManagerImpl implements WebServiceClientManager {
	
	private static final Logger logger = LoggerFactory.getLogger(WebServiceClientManagerImpl.class);
	
	// Clients available in Cytoscape.
	private final Map<URI, WebServiceClient<?>> clients;

	
	public WebServiceClientManagerImpl() {
		clients = new HashMap<URI, WebServiceClient<?>>();
	}

	
	/**
	 * Register client to this manager.
	 * 
	 * This will be used by Spring DM.
	 * 
	 * @param client 
	 * @param props metadata from 
	 */
	public void registerClient(final WebServiceClient<?> client, Map props) {
		if (client == null) {
			logger.warn("Could not register client: Web Service Client is null.");
			return;
		}
		
		clients.put(client.getServiceLocation(), client);
	}
	
	/**
	 * Unregister client from this manager.
	 * 
	 * This will be called by Spring DM.
	 * 
	 * @param client Client to be removed. 
	 * @param props Metadata for this client.
	 */
	public void removeClient(WebServiceClient<?> client, Map props) {
		if (client == null) {
			logger.warn("No such client.");
			return;
		}
		
		clients.remove(client.getServiceLocation());
		client = null;
	}

	
	@Override public Set<WebServiceClient<?>> getAllClients() {
		return new HashSet<WebServiceClient<?>>(clients.values());
	}

	
	@Override public WebServiceClient<?> getClient(final URI serviceURI) {
		return clients.get(serviceURI);
	}

	
	
}

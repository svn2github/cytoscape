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
package cytoscape.data.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;


/**
 * Web Service Client Manager manages available web service
 * clients in Cytoscape.  All clients will be loaded & registered through
 * Plugin Manager architecture.
 *
 *  @author Keiichiro Ono
 *  @since Cytoscape 2.5
 *  @version 0.5
 *
 */
public class WebServiceClientManager {
	
	/**
	 * ClientType defines types/characteristics of
	 * the web service clients.
	 */
	public enum ClientType {
		ANALYSIS,
		ATTRIBUTE,
		NETWORK;
	}

	// List of loaded clients in Cytoscape.
	// Key is 
	private static Map<String, WebServiceClient> clients;
	private static Map<String, WebServiceClient> clientNameMap;

	// Handles events between core and web service clients.
	private static CyWebServiceEventSupport cwseSupport;

	static {
		new WebServiceClientManager();
	}

	private WebServiceClientManager() {
		clients = new HashMap<String, WebServiceClient>();
		clientNameMap = new HashMap<String, WebServiceClient>();

		cwseSupport = new CyWebServiceEventSupport();
	}

	/**
	 *  Register client to the manager.
	 *
	 * @param client DOCUMENT ME!
	 */
	public static void registerClient(final WebServiceClient client) {
		if (client == null) {
			return;
		}

		clients.put(client.getDisplayName(), client);
		clientNameMap.put(client.getClientID(), client);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static List<WebServiceClient> getAllClients() {
		final List<WebServiceClient> clientList = new ArrayList<WebServiceClient>();

		for (String key : clients.keySet()) {
			clientList.add(clients.get(key));
		}

		return clientList;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param clientID DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static WebServiceClient getClient(String clientID) {
		return clientNameMap.get(clientID);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param serviceName DOCUMENT ME!
	 */
	public static void removeClient(String serviceName) {
		if (serviceName == null) {
			return;
		}

		WebServiceClient cl = clientNameMap.get(serviceName);

		if (cl != null) {
			clientNameMap.remove(serviceName);
			clients.remove(cl.getDisplayName());
			cl = null;
		}
	}

	public static CyWebServiceEventSupport getCyWebServiceEventSupport() {
		return cwseSupport;
	}
}

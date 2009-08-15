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

package csplugins.id.mapping;

import cytoscape.CytoscapeInit;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;

/**
 *
 * @author gjj
 */
public class IDMapperClientManager {

    private static Map<String, IDMapperClient> clientNameMap;
    private static Map<String, IDMapperClient> clientIDMap;

    static {
        new IDMapperClientManager();
    }

    private IDMapperClientManager() {
        clientNameMap = new HashMap();
        clientIDMap = new HashMap();
        reloadFromCytoscapeProperties();
    }

    public static void reloadFromCytoscapeProperties() {
        clientNameMap.clear();
        clientIDMap.clear();

        Properties props = CytoscapeInit.getProperties();

        String prefix = IDMapperClientProperties.moduleName+".";

        Set<String> propIds = new HashSet();

        // Find all properties with this prefix
        Enumeration iter = props.propertyNames();
        while (iter.hasMoreElements()) {
            String property = (String) iter.nextElement();

            if (property.startsWith(prefix)) {
                int start = prefix.length();
                int end = property.indexOf('.', start);
                if (end!=-1) {
                    propIds.add(property.substring(start, end));
                }
            }
        }

        for (String pid : propIds) {
            IDMapperClientProperties imcp = new IDMapperClientProperties(pid);
            IDMapperClient client = null;
            try {
                client = new IDMapperClientImplTunables(imcp);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IDMapperException e) {
                e.printStackTrace();
            }

            if (client == null) {
                //imcp.release();
            } else {
                registerClient(client);
            }

        }
    }

    public static Set<IDMapperClient> allClients() {
        return new HashSet(clientNameMap.values());
    }

    public static Set<IDMapperClient> selectedClients() {
        Set<IDMapperClient> clients = new HashSet();
        for (IDMapperClient client : clientNameMap.values()) {
            if (client.isSelected()) {
                clients.add(client);
            }
        }

        return clients;
    }

    public static IDMapperStack selectedIDMapperStack() {
        IDMapperStack idMapperStack = new IDMapperStack();
        for (IDMapperClient client : selectedClients()) {
                idMapperStack.addIDMapper(client.getIDMapper());
        }

        return idMapperStack;
    }
    
    public static IDMapperClient getClient(String clientId) {
        return clientIDMap.get(clientId);
    }

    public static boolean removeClient(String clientId) {
        if (clientId == null) {
            return false;
        }
    
        IDMapperClient cl = clientIDMap.get(clientId);
        return removeClient(cl);
    }
    
    public static IDMapperClient getClientByDisplayName(String clientDisName) {
        return clientNameMap.get(clientDisName);
    }

    public static boolean removeClientByDisplayName(String clientName) {
        if (clientName == null) {
            return false;
        }

        IDMapperClient cl = clientNameMap.get(clientName);
        return removeClient(cl);
    }

    public static boolean removeClient(final IDMapperClient client) {
        if (client == null) {
            return false;
        }

        if (!clientIDMap.containsValue(client)) {
            return false;
        }

        clientNameMap.remove(client.getDisplayName());
        clientIDMap.remove(client.getId());

        if (client instanceof IDMapperClientImplTunables) {
            ((IDMapperClientImplTunables)client).close();
        }
        
        return true;
    }

    /**
     *
     * @param client
     * @return true if registered.
     */
    public static boolean registerClient(final IDMapperClient client) {
        return registerClient(client, false);
    }

    /**
     *  Register client to the manager.
     *
     * @param client DOCUMENT ME!
     * @param replace indicate whether replace the existing client if client id
     *                or display name is the same
     * @return true if registered
     */
    public static boolean registerClient(final IDMapperClient client,
            boolean replace) {
        if (client == null) {
            throw new IllegalArgumentException();
        }

        if (!replace && (getClient(client.getId())!=null ||
                getClientByDisplayName(client.getDisplayName())!=null)) {
            return false;
        }

        clientNameMap.put(client.getDisplayName(), client);
        clientIDMap.put(client.getId(), client);

        return true;
    }

}

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
        init();
    }

    private void init() {
        Properties props = CytoscapeInit.getProperties();
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

    public static void removeClient(String clientId) {
        if (clientId == null) {
            return;
        }
    
        IDMapperClient cl = clientIDMap.get(clientId);

        if (cl != null) {
            clientNameMap.remove(cl.getDisplayName());
            clientIDMap.remove(clientId);
            cl = null;
        }
    }
    
    public static IDMapperClient getClientByDisplayName(String clientDisName) {
        return clientNameMap.get(clientDisName);
    }

    public static void removeClientByDisplayName(String clientName) {
        if (clientName == null) {
            return;
        }

        IDMapperClient cl = clientNameMap.get(clientName);

        if (cl != null) {
            clientNameMap.remove(clientName);
            clientIDMap.remove(cl.getId());
            cl = null;
        }
    }

//    public static boolean selectClient(String clientId) {
//        IDMapperClient client = getClient(clientId);
//        if (client==null) return false;
//
//        return selectedClients.add(client);
//    }
//
//    public static boolean unselectClient(String clientId) {
//        IDMapperClient client = getClient(clientId);
//        if (client==null) return false;
//
//        return selectedClients.remove(client);
//    }

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

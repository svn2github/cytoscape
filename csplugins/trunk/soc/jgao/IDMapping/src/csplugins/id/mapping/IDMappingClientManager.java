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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.bridgedb.IDMapperStack;

/**
 *
 * @author gjj
 */
public class IDMappingClientManager {

    public enum ClientType {
        FILE,
        RDB,
        WEBSERVICE;
    }

    private static Map<String, IDMappingClient> clientNameMap;
    private static Map<String, IDMappingClient> clientIDMap;

    static {
        new IDMappingClientManager();
    }

    private IDMappingClientManager() {
        clientNameMap = new HashMap();
        clientIDMap = new HashMap();
    }

    /**
     *
     * @param client
     * @return true if registered.
     */
    public static boolean registerClient(final IDMappingClient client) {
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
    public static boolean registerClient(final IDMappingClient client,
            boolean replace) {
        if (client == null) {
            throw new IllegalArgumentException();
        }

        if (!replace && (getClient(client.getClientID())!=null ||
                getClientByDisplayName(client.getDisplayName())!=null)) {
            return false;
        }

        clientNameMap.put(client.getDisplayName(), client);
        clientIDMap.put(client.getClientID(), client);
        client.getProps().saveProperties();

        if (client instanceof AbstractIDMappingClient) {
            AbstractIDMappingClient cli = (AbstractIDMappingClient) client;
            cli.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    client.getProps().saveProperties();
                }
            });
        }

        return true;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static IDMapperStack selectedIDMapperStack() {
        IDMapperStack idMapperStack = new IDMapperStack();
        for (IDMappingClient client : clientNameMap.values()) {
            if (client.isSelected())
                idMapperStack.addIDMapper(client.getIDMapper());
        }
        
        return idMapperStack;
    }

    /**
     *  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Set<IDMappingClient> allClients() {
        return new HashSet(clientNameMap.values());
    }

    /**
     *  DOCUMENT ME!
     *
     * @param clientID DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static IDMappingClient getClient(String clientID) {
        return clientIDMap.get(clientID);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param clientID DOCUMENT ME!
     */
    public static void removeClient(String clientID) {
        if (clientID == null) {
            return;
        }

        removeClient(clientIDMap.get(clientID));
    }

    /**
     *  DOCUMENT ME!
     *
     * @param clientID DOCUMENT ME!
     */
    public static void removeClient(IDMappingClient client) {
        if (client == null) {
            return;
        }

        clientIDMap.remove(client.getClientID());
        clientNameMap.remove(client.getDisplayName());
    }

    /**
     *  DOCUMENT ME!
     *
     * @param clientName DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static IDMappingClient getClientByDisplayName(String clientName) {
        return clientNameMap.get(clientName);
    }

/**
     *  DOCUMENT ME!
     *
     * @param clientName DOCUMENT ME!
     */
    public static void removeClientByDisplayName(String clientName) {
        if (clientName == null) {
            return;
        }

        IDMappingClient cl = clientNameMap.get(clientName);

        if (cl != null) {
            clientIDMap.remove(clientName);
            clientNameMap.remove(cl.getClientID());
            cl = null;
        }
    }

}

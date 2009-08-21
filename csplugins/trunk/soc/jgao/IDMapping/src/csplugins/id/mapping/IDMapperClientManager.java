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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        reloadFromCytoscapeSessionProperties();
    }

    public static void reloadFromCytoscapeSessionProperties() {
        //remove all of the current clients
        clientNameMap.clear();
        clientIDMap.clear();
        
        //removeAllClients(); // this cannot be used here. it will delete the
                              // corresponding session properties

        Properties props = CytoscapeInit.getProperties();

        String prefix = FinalStaticValues.CLIENT_SESSION_PROPS+".";

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

    public static boolean reloadFromCytoscapeGlobalProperties() throws IOException{
        String fileName = FinalStaticValues.CLIENT_GLOBAL_PROPS;
        File file = cytoscape.CytoscapeInit.getConfigFile(fileName);
        if (!file.exists()) {
            // no default clients have been set
            return false;
        }

        Set<IDMapperClient> clients = new HashSet();

        BufferedReader in = new BufferedReader(new FileReader(file));
        String line = in.readLine();
        if (line==null) { //empty file
            return false;
        }

        int nClients = Integer.parseInt(line);
        for (int i=0; i<nClients; i++) {
            String clientId = in.readLine();
            String classStr = in.readLine();
            String connStr = in.readLine();
            String display = in.readLine();
            boolean selected = Boolean.parseBoolean(in.readLine());
            IDMapperClient client;
            try {
                client = new IDMapperClientImplTunables(connStr,
                        classStr, display, clientId, selected);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            } catch (IDMapperException e) {
                e.printStackTrace();
                continue;
            }

            clients.add(client);
        }

//        clientNameMap.clear();
//        clientIDMap.clear();
        removeAllClients(); // remove all of the current clients

        for (IDMapperClient client : clients) {
            registerClient(client);
        }

        return true;
    }

    public static void saveCurrentToCytoscapeGlobalProperties() throws IOException {
        String fileName = FinalStaticValues.CLIENT_GLOBAL_PROPS;
        File file = cytoscape.CytoscapeInit.getConfigFile(fileName);
        BufferedWriter out = new BufferedWriter(new FileWriter(file));

        Set<IDMapperClient> clients = IDMapperClientManager.allClients();
        out.write(Integer.toString(clients.size()));
        out.newLine();

        for (IDMapperClient client : clients) {
            String clientId = client.getId();
            out.write(clientId);
            out.newLine();

            String classStr = client.getClassString();
            out.write(classStr);
            out.newLine();

            String connStr = client.getConnectionString();
            out.write(connStr);
            out.newLine();

            String display = client.getDisplayName();
            out.write(display);
            out.newLine();

            boolean selected = client.isSelected();
            out.write(Boolean.toString(selected));
            out.newLine();
        }

        out.close();
    }

    public static int countClients() {
        return clientNameMap.size();
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

    public static void removeAllClients() {
        for (IDMapperClient client : allClients()) {
            removeClient(client);
        }
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

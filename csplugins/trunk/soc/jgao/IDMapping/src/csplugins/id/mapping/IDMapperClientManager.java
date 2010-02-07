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

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperStack;

/**
 *
 * @author gjj
 */
public class IDMapperClientManager {

    private static Map<String, IDMapperClient> clientConnectionStringMap;

    static {
        new IDMapperClientManager();
    }

    private IDMapperClientManager() {
        clientConnectionStringMap = new HashMap();
        //reloadFromCytoscapeSessionProperties();
    }

    public static void reloadFromCytoscapeSessionProperties() {
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

        int i= 0;
        for (String pid : propIds) {
            String newPId = ""+(i++)+"-"+System.currentTimeMillis();
            IDMapperClientProperties imcp = new IDMapperClientProperties(pid);

            IDMapperClient client  = new IDMapperClientImplTunables(imcp, newPId);
            registerClient(client);
        }
    }

    private static final String CLIENT_START = "#client start";
    private static final String CLIENT_END = "#client end";
    private static final String CLIENT_ID = "Client ID:\t";
    private static final String CLIENT_CONN_STR = "Connection String:\t";
    private static final String CLIENT_CLASS_PATH = "Class Path:\t";
    private static final String CLIENT_DISPLAY_NAME = "Display Name:\t";
    private static final String CLIENT_SELECTED = "Selected:\t";

    public static boolean reloadFromCytoscapeGlobalProperties() {
        removeAllClients(true); // remove all of the current clients

        String fileName = FinalStaticValues.CLIENT_GLOBAL_PROPS;
        File file = cytoscape.CytoscapeInit.getConfigFile(fileName);
        if (!file.exists()) {
            // no default clients have been set
            return false;
        }

        Set<IDMapperClient> clients = new HashSet();

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            String clientId = null;
            String classStr = null;
            String connStr = null;
            String display = null;
            boolean selected = true;

            String line;
            while ((line=in.readLine())!=null) {
                if (line.compareTo(CLIENT_START)==0) {
                    clientId = null;
                    classStr = null;
                    connStr = null;
                    display = null;
                    selected = true;
                } else if (line.compareTo(CLIENT_END)==0) {
                    if (classStr!=null && connStr!=null) {
                        IDMapperClient client = new IDMapperClientImplTunables(connStr,
                                    classStr, display, clientId, selected);

                        clients.add(client);
                    } else {
                    // something is wrong with the file
                    }
                } else if (line.startsWith(CLIENT_ID)) {
                    clientId = line.substring(CLIENT_ID.length());
                } else if (line.startsWith(CLIENT_CONN_STR)) {
                    connStr = line.substring(CLIENT_CONN_STR.length());
                } else if (line.startsWith(CLIENT_CLASS_PATH)) {
                    classStr = line.substring(CLIENT_CLASS_PATH.length());
                } else if (line.startsWith(CLIENT_DISPLAY_NAME)) {
                    display = line.substring(CLIENT_DISPLAY_NAME.length());
                } else if (line.startsWith(CLIENT_SELECTED)) {
                    selected = Boolean.parseBoolean(line.substring(
                            CLIENT_SELECTED.length()));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

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

        for (IDMapperClient client : clients) {
            out.write(CLIENT_START);
            out.newLine();

            String clientId = client.getId();
            out.write(CLIENT_ID+clientId);
            out.newLine();

            String classStr = client.getClassString();
            out.write(CLIENT_CLASS_PATH+classStr);
            out.newLine();

            String connStr = client.getConnectionString();
            out.write(CLIENT_CONN_STR+connStr);
            out.newLine();

            String display = client.getDisplayName();
            out.write(CLIENT_DISPLAY_NAME+display);
            out.newLine();

            boolean selected = client.isSelected();
            out.write(CLIENT_SELECTED+Boolean.toString(selected));
            out.newLine();

            out.write(CLIENT_END);
            out.newLine();
        }

        out.close();
    }

    public static int countClients() {
        return clientConnectionStringMap.size();
    }

    public static Set<IDMapperClient> allClients() {
        return new HashSet(clientConnectionStringMap.values());
    }

    public static Set<IDMapperClient> selectedClients() {
        Set<IDMapperClient> clients = new HashSet();
        for (IDMapperClient client : clientConnectionStringMap.values()) {
            if (client.isSelected()) {
                clients.add(client);
            }
        }

        return clients;
    }

    public static IDMapperStack selectedIDMapperStack() {
        IDMapperStack idMapperStack = new IDMapperStack();
        for (IDMapperClient client : selectedClients()) {
            IDMapper idMapper = client.getIDMapper();
            if (idMapper!=null) {
                idMapperStack.addIDMapper(idMapper);
            }
        }

        return idMapperStack;
    }
    
    public static IDMapperClient getClient(String clientConnStr) {
        return clientConnectionStringMap.get(clientConnStr);
    }

    public static boolean removeClient(String clientConnStr) {
        return removeClient(clientConnStr, true);
    }

    public static boolean removeClient(String clientConnStr,
            boolean removeSessionProps) {
        if (clientConnStr == null) {
            return false;
        }
    
        IDMapperClient cl = clientConnectionStringMap.get(clientConnStr);
        return removeClient(cl, removeSessionProps);
    }

    public static boolean removeClient(final IDMapperClient client) {
        return removeClient(client, true);
    }

    public static boolean removeClient(final IDMapperClient client,
            boolean removeSessionProps) {
        if (client == null) {
            return false;
        }

        if (!clientConnectionStringMap.containsValue(client)) {
            return false;
        }

        clientConnectionStringMap.remove(client.getConnectionString());

        if (removeSessionProps &&
                client instanceof IDMapperClientImplTunables) {
            ((IDMapperClientImplTunables)client).close();
        }
        
        return true;
    }

    public static void removeAllClients() {
        removeAllClients(true);
    }

    public static void removeAllClients(boolean removeSessionProps) {
        for (IDMapperClient client : allClients()) {
            removeClient(client, removeSessionProps);
        }
    }

    /**
     * Register a client. If there exists a client in the manager with the same
     * connection string, that client will be replaced with the new client.
     * @param client
     */
    public static void registerClient(final IDMapperClient client) {
        if (client == null) {
            throw new IllegalArgumentException();
        }

        IDMapperClient oldClient = getClient(client.getConnectionString());
        if (oldClient!=null) {
            removeClient(oldClient);
        }

        //preprocess(client); // set fullname if null

        clientConnectionStringMap.put(client.getConnectionString(), client);
    }

}

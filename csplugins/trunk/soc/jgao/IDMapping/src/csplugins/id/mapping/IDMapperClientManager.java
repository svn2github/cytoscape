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

import csplugins.id.mapping.util.BridgeRestUtil;
import csplugins.id.mapping.util.DataSourceWrapper;

import cytoscape.CytoscapeInit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.bridgedb.AttributeMapper;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperStack;

/**
 *
 * @author gjj
 */
public class IDMapperClientManager {

    private enum CacheStatus {
        UNCACHED, CACHING, CACHED;
    }

    public interface IDMapperChangeListener {
        public void changed();
    }

    public static class TrackIDMapperChangeListener {
        private boolean changed = false;

        public void changed() {
            changed = true;
        }

        public boolean reset() {
            boolean tmp = changed;
            changed = false;
            return tmp;
        }

        public boolean isChanged() {
            return changed;
        }
    }

    private static Map<String, IDMapperClient> clientConnectionStringMap;
    private static Set<IDMapperClient> selectedClients;

    private static CacheStatus cacheStatus = CacheStatus.UNCACHED;
    private static Set<DataSourceWrapper> srcTypes = null;
    private static Set<DataSourceWrapper> tgtTypes = null;
    private static Set<List<DataSourceWrapper>> supportedMapping = null;
    private static IDMapperStack selectedIDMapperStack = null;
    private static List<IDMapperChangeListener> listeners;

    private static ExecutorService executor = null;

    private static int waitSeconds = 5;

    static {
//        new IDMapperClientManager();
        clientConnectionStringMap = new HashMap<String, IDMapperClient>();
        selectedClients = new HashSet<IDMapperClient>();
        listeners = new ArrayList<IDMapperChangeListener>();
        addIDMapperChangeListener(new IDMapperChangeListener() {
            public void changed() {
                cacheStatus = CacheStatus.UNCACHED;
            }
        });
    }

    public static void addIDMapperChangeListener(IDMapperChangeListener listener) {
        if (listener==null)
            throw new NullPointerException();

        listeners.add(listener);
    }

    private static void fireIDMapperChange() {
        for (IDMapperChangeListener listener : listeners) {
            listener.changed();
        }
    }

    private IDMapperClientManager() {
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

            IDMapperClientImplTunables client  = new IDMapperClientImplTunables(imcp, newPId);
            registerClient(client, client.isSelected());
        }
    }

    private static final String CLIENT_START = "#client start";
    private static final String CLIENT_END = "#client end";
    private static final String CLIENT_ID = "Client ID:\t";
    private static final String CLIENT_CONN_STR = "Connection String:\t";
    private static final String CLIENT_CLASS_PATH = "Class Path:\t";
    private static final String CLIENT_DISPLAY_NAME = "Display Name:\t";
    private static final String CLIENT_SELECTED = "Selected:\t";
    private static final String CLIENT_TYPE = "Client Type:\t";

    public static boolean reloadFromCytoscapeGlobalProperties() {
        removeAllClients(true); // remove all of the current clients

        String fileName = FinalStaticValues.CLIENT_GLOBAL_PROPS;
        File file = cytoscape.CytoscapeInit.getConfigFile(fileName);
        if (!file.exists()) {
            // no default clients have been set
            return false;
        }

//        Set<IDMapperClient> clients = new HashSet();

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            String clientId = null;
            String classStr = null;
            String connStr = null;
            String display = null;
            boolean selected = true;
            IDMapperClient.ClientType clientType = null;

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
                        try {
                            IDMapperClient client = new IDMapperClientImplTunables
                                    .Builder(connStr, classStr)
                                    .displayName(display)
                                    .id(clientId)
                                    .selected(selected)
                                    .clientType(clientType)
                                    .build();
                            registerClient(client, selected);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
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
                } else if (line.startsWith(CLIENT_TYPE)) {
                    clientType = IDMapperClient.ClientType.valueOf(
                            line.substring(CLIENT_TYPE.length()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    static boolean registerDefaultClient() {
        Properties props = CytoscapeInit.getProperties();
        String defaultSpecies = props.getProperty(FinalStaticValues.DEFAULT_SPECIES_NAME);
        return registerDefaultClient(defaultSpecies);
    }

    static boolean registerDefaultClient(String defaultSpecies) {
        return registerDefaultClient(defaultSpecies, null);
    }

    static boolean registerDefaultClient(String newDefaultSpecies, String oldDefaultSpecies) {
        if (newDefaultSpecies==null) {
            throw new IllegalArgumentException("newDefaultSpecies is null");
        }

        if (oldDefaultSpecies!=null) {
            removeClient("idmapper-bridgerest:"+BridgeRestUtil.defaultBaseUrl+"/"+oldDefaultSpecies);
        }

        List<String> orgs = BridgeRestUtil.supportedOrganisms(BridgeRestUtil.defaultBaseUrl);
        if (!orgs.contains(newDefaultSpecies)) {
            System.err.println("No default ID mapping resources for species: "+newDefaultSpecies
                    +". Please configure manually.");
            return false;
        }

        String classPath = "org.bridgedb.webservice.bridgerest.BridgeRest";
        String connStr = "idmapper-bridgerest:"+BridgeRestUtil.defaultBaseUrl+"/"+newDefaultSpecies;
        IDMapperClient client;
        try {
            client = new IDMapperClientImplTunables
                                .Builder(connStr, classPath)
                                .displayName("BridgeDb("+BridgeRestUtil.defaultBaseUrl+"/"+newDefaultSpecies+")")
                                .selected(true)
                                .clientType(IDMapperClient.ClientType.WEBSERVICE)
                                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        registerClient(client);
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

            boolean selected = selectedClients.contains(client);
            out.write(CLIENT_SELECTED+Boolean.toString(selected));
            out.newLine();

            IDMapperClient.ClientType clientType = client.getClientType();
            out.write(CLIENT_TYPE+clientType.name());
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
        return Collections.unmodifiableSet(selectedClients);
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

        fireIDMapperChange();
        
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

    public static void registerClient(final IDMapperClient client) {
        registerClient(client, true);
    }

    /**
     * Register a client. If there exists a client in the manager with the same
     * connection string, that client will be replaced with the new client.
     * @param client
     */
    public static void registerClient(final IDMapperClient client, boolean selected) {
        if (client == null) {
            throw new IllegalArgumentException();
        }

        IDMapperClient oldClient = getClient(client.getConnectionString());
        if (oldClient!=null) {
            removeClient(oldClient);
        }

        //preprocess(client); // set fullname if null

        clientConnectionStringMap.put(client.getConnectionString(), client);

        if (selected) {
            selectedClients.add(client);
        }
        
        fireIDMapperChange();
    }

    public static void setClientSelection(IDMapperClient client, boolean select) {
        boolean changed;
        if (select)
            changed = selectedClients.add(client);
        else
            changed = selectedClients.remove(client);

        if (changed) {
            if (client instanceof IDMapperClientImplTunables) {
                ((IDMapperClientImplTunables)client).setSelected(select);
            }
            fireIDMapperChange();
        }
    }

    public static boolean isClientSelected(IDMapperClient client) {
        return selectedClients.contains(client);
    }

    public static IDMapperStack selectedIDMapperStack() {
        cacheAndWait(waitSeconds);
        return selectedIDMapperStack;
    }

    /**
     *
     * @return supported source ID types by the selected resources
     */
    public static Set<DataSourceWrapper> getSupportedSrcTypes() {
        cacheAndWait(waitSeconds);
        return srcTypes;
    }

    /**
     *
     * @return supported target ID types by the selected resources
     */
    public static Set<DataSourceWrapper> getSupportedTgtTypes() {
        cacheAndWait(waitSeconds);
        return tgtTypes;
    }

    public static boolean isMappingSupported(DataSourceWrapper srcType, DataSourceWrapper tgtType) {
        cacheAndWait(waitSeconds);
        List<DataSourceWrapper> dsws = new ArrayList<DataSourceWrapper>(2);
        dsws.add(srcType);
        dsws.add(tgtType);
        return supportedMapping.contains(dsws);
    }

    public static void resetCache() {
        cacheStatus = CacheStatus.UNCACHED;
    }

    public static void reCache() {
        resetCache();
        cache();
    }

    public static void cacheAndWait(int seconds) {
        cache();
        if (cacheStatus == CacheStatus.CACHED)
            return;
        try {
            if (!executor.awaitTermination(seconds, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            cacheStatus = CacheStatus.CACHED;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cache() {
        if (cacheStatus == CacheStatus.CACHED)
            return;

        if (cacheStatus == CacheStatus.CACHING) {
            if (executor.isTerminated()) {
                cacheStatus = CacheStatus.CACHED;
            }
            return;
        }

        cacheStatus = CacheStatus.CACHING;

        selectedIDMapperStack = new IDMapperStack();
        srcTypes = Collections.synchronizedSet(new HashSet<DataSourceWrapper>());
        tgtTypes = Collections.synchronizedSet(new HashSet<DataSourceWrapper>());
        supportedMapping = Collections.synchronizedSet(new HashSet<List<DataSourceWrapper>>());

        executor = Executors.newCachedThreadPool();

        for (IDMapperClient client : selectedClients()) {
            final IDMapper idMapper = client.getIDMapper();
            if (idMapper==null)
                continue;

            //selectedIDMapperStack
            selectedIDMapperStack.addIDMapper(idMapper);

            executor.execute(new Runnable() {
                public void run() {
                    IDMapperCapabilities caps = idMapper.getCapabilities();

                    Set<DataSource> srcs, tgts;
                    try {
                        srcs = caps.getSupportedSrcDataSources();
                        tgts = caps.getSupportedTgtDataSources();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    // srcTypes
                    if (srcs!=null) {
                        for (DataSource ds : srcs) {
                            srcTypes.add(DataSourceWrapper.getInstance(
                                    ds.getFullName(), DataSourceWrapper.DsAttr.DATASOURCE));
                        }
                    }

                    // tgtTypes
                    if (tgts!=null) {
                        for (DataSource ds : tgts) {
                            tgtTypes.add(DataSourceWrapper.getInstance(
                                    ds.getFullName(), DataSourceWrapper.DsAttr.DATASOURCE));
                        }
                    }

                    // mapping from type to type
                    if (srcs!=null && tgts!=null) {
                        for (DataSource src : srcs) {
                            for (DataSource tgt : tgts) {
                                boolean spt = false;
                                try {
                                    spt = caps.isMappingSupported(src, tgt);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (spt) {
                                    List<DataSourceWrapper> dsws = new ArrayList<DataSourceWrapper>(2);
                                    dsws.add(DataSourceWrapper.getInstance(
                                        src.getFullName(), DataSourceWrapper.DsAttr.DATASOURCE));
                                    dsws.add(DataSourceWrapper.getInstance(
                                        tgt.getFullName(), DataSourceWrapper.DsAttr.DATASOURCE));
                                    supportedMapping.add(dsws);
                                }
                            }
                        }
                    }

                    // AttributeMapper
                    if (!(idMapper instanceof AttributeMapper))
                        return;

                    AttributeMapper attrMapper = (AttributeMapper)idMapper;
                    Set<String> attrs = null;
                    try {
                        attrs = attrMapper.getAttributeSet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (attrs==null)
                        return;
                    
                    for (String attr : attrs) {
                        DataSourceWrapper dsw = DataSourceWrapper.getInstance(attr, DataSourceWrapper.DsAttr.ATTRIBUTE);
                        if (attrMapper.isFreeAttributeSearchSupported()) {
                            srcTypes.add(dsw);
                            if (tgts!=null) {
                                for (DataSource tgt : tgts) {
                                    List<DataSourceWrapper> dsws = new ArrayList<DataSourceWrapper>(2);
                                    dsws.add(dsw);
                                    dsws.add(DataSourceWrapper.getInstance(
                                        tgt.getFullName(), DataSourceWrapper.DsAttr.DATASOURCE));
                                    supportedMapping.add(dsws);
                                }
                            }
                        }

                        tgtTypes.add(dsw);
                        if (srcs!=null) {
                            for (DataSource src : srcs) {
                                List<DataSourceWrapper> dsws = new ArrayList<DataSourceWrapper>(2);
                                dsws.add(DataSourceWrapper.getInstance(
                                    src.getFullName(), DataSourceWrapper.DsAttr.DATASOURCE));
                                dsws.add(dsw);
                                supportedMapping.add(dsws);
                            }
                        }
                    }
                }
            });
        }
    }
}

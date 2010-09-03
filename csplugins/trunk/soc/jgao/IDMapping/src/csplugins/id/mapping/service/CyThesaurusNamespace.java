/**
 * Copyright (c) 2009 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package csplugins.id.mapping.service;

import csplugins.id.mapping.CyThesaurusPlugin;
import csplugins.id.mapping.IDMapperClient;
import csplugins.id.mapping.IDMapperClientImplTunables;
import csplugins.id.mapping.IDMapperClientManager;
import csplugins.id.mapping.ui.IDMappingSourceConfigDialog;
import csplugins.id.mapping.ui.CyThesaurusDialog;
import csplugins.id.mapping.AttributeBasedIDMapping;
import csplugins.id.mapping.AttributeBasedIDMappingImpl;
import csplugins.id.mapping.util.DataSourceWrapper;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import cytoscape.layout.Tunable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperStack;
import org.bridgedb.Xref;

/**
 * 
 */
public class CyThesaurusNamespace extends AbstractCommandHandler {
    public static final String NAME = "idmapping";
    
    // Commands
    private static final String VERSION = "version";
    private static final String MAIN_DIALOG = "main dialog";
    private static final String RESOURCE_DIALOG = "resource config dialog";
    private static final String LIST_RESOURCE = "list resources";
    private static final String LIST_SELECTED_RESOURCE = "list selected resources";
    private static final String ADD_RESOURCE = "add resource";
    private static final String REMOVE_RESOURCE = "remove resource";
    private static final String SELECT_RESOURCE = "select resource";
    private static final String DESELECT_RESOURCE = "deselect resource";
    private static final String GET_SRC_ID_TYPES = "get source id types";
    private static final String GET_TGT_ID_TYPES = "get target id types";
    private static final String CHECK_MAPPING_SUPPORT = "check mapping supported";
    private static final String ATTRIBUTE_BASED_MAPPING = "attribute based mapping";
    private static final String GENERAL_MAPPING = "general mapping";
    private static final String CHECK_ID_EXIST = "check id exist";
    private static final String GUESS_TYPE = "guess id type";

    // Arguments
    private static final String CLASS_PATH = "classpath";
    private static final String CONN_STRING = "connstring"; // connection string
    private static final String DISPLAY_NAME = "displayname";
    private static final String SOURCE_TYPE = "sourcetype";
    private static final String TARGET_TYPE = "targettype";
    private static final String NETWORK_LIST = "networklist";
    private static final String SOURCE_ATTR = "sourceattr";
    private static final String TARGET_ATTR = "targetattr";
    private static final String SOURCE_ID = "sourceid";

    protected CyThesaurusNamespace(CyCommandNamespace ns) {
        super(ns);
        // Define our subcommands
        addArgument(VERSION);
        addDescription(VERSION, "Get the version of ID mapping service from CyThesaurus");

        addArgument(MAIN_DIALOG);
        addDescription(MAIN_DIALOG, "Display the main dialog of CyThesaurus");

        addArgument(RESOURCE_DIALOG);
        addDescription(RESOURCE_DIALOG, "Display the dialog for configure ID mapping sources");

        addArgument(LIST_RESOURCE);
        addDescription(LIST_RESOURCE, "List all registered ID mapping sources");

        addArgument(LIST_SELECTED_RESOURCE);
        addDescription(LIST_SELECTED_RESOURCE, "List all selected ID mapping sources");

        addArgument(ADD_RESOURCE, CLASS_PATH);
        addArgument(ADD_RESOURCE, CONN_STRING);
        addArgument(ADD_RESOURCE, DISPLAY_NAME);
        addDescription(ADD_RESOURCE, "Add/register an ID mapping resource");

        addArgument(SELECT_RESOURCE, CONN_STRING);
        addDescription(SELECT_RESOURCE, "Select an ID mapping resource for use");

        addArgument(DESELECT_RESOURCE, CONN_STRING);
        addDescription(DESELECT_RESOURCE, "Deselect an ID mapping resource for use");

        addArgument(GET_SRC_ID_TYPES);
        addDescription(GET_SRC_ID_TYPES, "Get supported source ID types");

        addArgument(GET_TGT_ID_TYPES);
        addDescription(GET_TGT_ID_TYPES, "Get supported target ID types");

//        addArgument(IS_MAPPING_SUPPORT);
        addArgument(CHECK_MAPPING_SUPPORT, SOURCE_TYPE);
        addArgument(CHECK_MAPPING_SUPPORT, TARGET_TYPE);
        addDescription(CHECK_MAPPING_SUPPORT, "Check if ID mapping between two ID types is supported");

        addArgument(ATTRIBUTE_BASED_MAPPING, NETWORK_LIST); //optional
        addArgument(ATTRIBUTE_BASED_MAPPING, SOURCE_ATTR); // required
        addArgument(ATTRIBUTE_BASED_MAPPING, TARGET_ATTR); // optional
        addArgument(ATTRIBUTE_BASED_MAPPING, SOURCE_TYPE); // optional
        addArgument(ATTRIBUTE_BASED_MAPPING, TARGET_TYPE); // required
        addDescription(ATTRIBUTE_BASED_MAPPING, "Mapping IDs of the sourceidtype(s) in "
                + "sourceattribute to IDs of the targetidtype and save them to targetattribute.");

        addArgument(GENERAL_MAPPING, SOURCE_ID);
        addArgument(GENERAL_MAPPING, SOURCE_TYPE);
        addArgument(GENERAL_MAPPING, TARGET_TYPE);
        addDescription(GENERAL_MAPPING, "Mapping sourceid(s) of sourcetype to targettype.");

        addArgument(CHECK_ID_EXIST, SOURCE_ID);
        addArgument(CHECK_ID_EXIST, SOURCE_TYPE);
        addDescription(CHECK_ID_EXIST, "Check if ID exists.");

        addArgument(GUESS_TYPE, SOURCE_ID);
        addDescription(CHECK_ID_EXIST, "Guess ID types from a set of IDs.");

    }


    /**
     * commandName returns the command name.  This is used to build the
     * hash table of commands to hand to the command parser
     *
     * @return name of the command
     */
    public String getHandlerName() { return namespace.getNamespaceName(); }

    public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
            return execute(command, createKVMap(args));
    }

    public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException {
        if (command.equals(VERSION))
            return version(command, args);
        if (command.equals(MAIN_DIALOG))
            return mainDialog(command, args);
        if (command.equals(RESOURCE_DIALOG))
            return resourcesDialog(command, args);
        if (command.equals(LIST_RESOURCE))
            return listResources(command, args);
        if (command.equals(LIST_SELECTED_RESOURCE))
            return listSelectedResources(command, args);
        if (command.equals(ADD_RESOURCE))
            return addResource(command, args);
        if (command.equals(REMOVE_RESOURCE))
            remvoeResource(command, args);
        if (command.equals(SELECT_RESOURCE))
            return selectResource(command, args);
        if (command.equals(DESELECT_RESOURCE))
            return deselectResource(command, args);
        if (command.equals(GET_SRC_ID_TYPES))
            return getSrcIdTypes(command, args);
        if (command.equals(GET_TGT_ID_TYPES))
            return getTgtIdTypes(command, args);
        if (command.equals(CHECK_MAPPING_SUPPORT))
            return isMappingSupported(command, args);
        if (command.equals(ATTRIBUTE_BASED_MAPPING))
            return attributeBasedMapping(command, args);
        if (command.equals(GENERAL_MAPPING))
            return generalMapping(command, args);
        if (command.equals(CHECK_ID_EXIST))
            return idExists(command, args);
        if (command.equals(GUESS_TYPE))
            return guessIDTypes(command, args);
        throw new CyCommandException(getHandlerName()+": unknown command "+command);
    }

    private CyCommandResult version(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        result.addResult(CyThesaurusPlugin.VERSION);
        return result;
    }

    private CyCommandResult mainDialog(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        final CyThesaurusDialog dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
        dialog.setLocationRelativeTo(Cytoscape.getDesktop());
        dialog.setMapSrcAttrIDTypes(CyThesaurusPlugin.mapSrcAttrIDTypes);
        dialog.setVisible(true);
        return result;
    }

    private CyCommandResult resourcesDialog(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        IDMappingSourceConfigDialog srcConfDialog
                = new IDMappingSourceConfigDialog(Cytoscape.getDesktop(), true);
        srcConfDialog.setVisible(true);
        return result;
    }

    private CyCommandResult listResources(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        Set<String> mappers = new HashSet();
        Set<IDMapperClient> clients = IDMapperClientManager.allClients();
        String message = "There are "+clients.size()+" ID mapping resources:\n";

        for (IDMapperClient client : clients) {
            String connStr = client.getConnectionString();
            mappers.add(connStr);
            message += "\t"+connStr+"[class: "+client.getClassString()+"]"+"\n";
        }

        result.addMessage(message);
        result.addResult(mappers);
        return result;
    }

    private CyCommandResult listSelectedResources(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        Set<String> mappers = new HashSet();
        Set<IDMapperClient> clients = IDMapperClientManager.selectedClients();
        String message = "There are selected "+clients.size()+" ID mapping resources:\n";

        for (IDMapperClient client : clients) {
            String connStr = client.getConnectionString();
            mappers.add(connStr);
            message += "\t"+connStr+"[class: "+client.getClassString()+"]"+"\n";
        }

        result.addMessage(message);
        result.addResult(mappers);
        return result;
    }

    private CyCommandResult addResource(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        String classPath = getArg(command, CLASS_PATH, args);
        String connString = getArg(command, CONN_STRING, args);
        String displayName = getArg(command, DISPLAY_NAME, args);
        if (connString == null || classPath==null)
                throw new CyCommandException(CLASS_PATH + " and " + CONN_STRING + " cannot be null.");

        if (displayName==null)
            displayName = connString;

        try {
            IDMapperClient client = new IDMapperClientImplTunables
                            .Builder(connString, classPath)
                            .displayName(displayName)
                            .build();
            IDMapperClientManager.registerClient(client);
            result.addMessage("Successfully registered");
        } catch (Exception e) {
            // throw new CyCommandException(e);
            result.addError("Failed to add the resource\n"+e.getMessage());
        }
        return result;
    }

    private CyCommandResult remvoeResource(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        String connString = getArg(command, CONN_STRING, args);
        if (connString == null)
                throw new CyCommandException(CONN_STRING + " cannot be null.");

        try {
            if (IDMapperClientManager.removeClient(connString)) {
                result.addResult(Boolean.TRUE);
                result.addMessage("Successfully unregistered");
            } else {
                result.addResult(Boolean.FALSE);
                result.addMessage("Could not unregister. The specific ID mapping resource might not exist.");
            }

        } catch (Exception e) {
            // throw new CyCommandException(e);
            result.addError("Failed to remove the resource\n"+e.getMessage());
        }
        return result;
    }

    private CyCommandResult selectResource(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        String connString = getArg(command, CONN_STRING, args);
        if (connString == null)
                throw new CyCommandException(CONN_STRING + " cannot be null.");

        try {
            IDMapperClient client = IDMapperClientManager.getClient(connString);
            if (client == null) {
                result.addResult(Boolean.FALSE);
                result.addMessage("Could not select the specific ID mapping resource since it did not exist.");
            } else {
                client.setSelected(true);
                result.addResult(Boolean.TRUE);
                result.addMessage("Selected.");
            }
        } catch (Exception e) {
            // throw new CyCommandException(e);
            result.addError("Failed to remove the resource\n"+e.getMessage());
        }
        return result;
    }

    private CyCommandResult deselectResource(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        String connString = getArg(command, CONN_STRING, args);
        if (connString == null)
                throw new CyCommandException(CONN_STRING + "cannot be null.");

        try {
            IDMapperClient client = IDMapperClientManager.getClient(connString);
            if (client == null) {
                result.addResult(Boolean.FALSE);
                result.addMessage("Could not deselect the specific ID mapping resource since it did not exist.");
            } else {
                client.setSelected(false);
                result.addResult(Boolean.TRUE);
                result.addMessage("Deselected.");
            }
        } catch (Exception e) {
            // throw new CyCommandException(e);
            result.addError("Failed to remove the resource\n"+e.getMessage());
        }
        return result;
    }

    private CyCommandResult getSrcIdTypes(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        Set<String> types = new HashSet();
        Set<DataSource> dss;
        try {
            dss = IDMapperClientManager.selectedIDMapperStack().
                getCapabilities().getSupportedSrcDataSources();
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        String message = "There are "+dss.size()+" supported source ID types:\n";

        for (DataSource ds : dss) {
            types.add(ds.getFullName());
            message += "\t"+ds.getFullName()+"\n";
        }

        result.addMessage(message);
        result.addResult(types);
        return result;
    }

    private CyCommandResult getTgtIdTypes(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        Set<String> types = new HashSet();
        Set<DataSource> dss;
        try {
            dss = IDMapperClientManager.selectedIDMapperStack().
                getCapabilities().getSupportedTgtDataSources();
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        String message = "There are "+dss.size()+" supported target ID types:\n";

        for (DataSource ds : dss) {
            types.add(ds.getFullName());
            message += "\t"+ds.getFullName()+"\n";
        }

        result.addMessage(message);
        result.addResult(types);
        return result;
    }

    private CyCommandResult isMappingSupported(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        String srctype = getArg(command, SOURCE_TYPE, args);
        String tgttype = getArg(command, TARGET_TYPE, args);
        if (srctype == null || tgttype==null)
                throw new CyCommandException(SOURCE_TYPE + " and " + TARGET_TYPE + " cannot be null.");

       DataSource srcds = DataSource.getByFullName(srctype);
       DataSource tgtds = DataSource.getByFullName(tgttype);
        try {
             if (IDMapperClientManager.selectedIDMapperStack().
                getCapabilities().isMappingSupported(srcds, tgtds)) {
                 result.addResult(Boolean.TRUE);
                 result.addMessage("Yes, mapping from "+srctype+" to "+tgttype+" is supported.");
             } else {
                 result.addResult(Boolean.FALSE);
                 result.addMessage("No, mapping from "+srctype+" to "+tgttype+" is not supported.");
             }
        } catch (Exception e) {
            throw new CyCommandException(e);
        }
        return result;
    }

    private CyCommandResult attributeBasedMapping(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();

        Set<CyNetwork> networks = new HashSet();

        // parse networks
        Object obj = args.get(NETWORK_LIST);
        if (obj==null) {
            //succ = false;
            //error.append("Message content does not contain field \"" + NETWORK_ID +"\"\n");
            // maping id for all networks
            networks.addAll(Cytoscape.getNetworkSet());
            if (networks.isEmpty()) {
                throw new CyCommandException("No network available");
            }
        }else{
            if (obj instanceof CyNetwork) {
                networks.add((CyNetwork)obj);
            } else if (obj instanceof String) {
                String netId = (String)obj;
                CyNetwork network = Cytoscape.getNetwork(netId);
                if (network!=null && network!=Cytoscape.getNullNetwork()) {
                    networks.add(network);
                } else {
                    throw new CyCommandException("Network "+netId+" does not exist.");
                }
            } else if (obj instanceof Collection) {
                Collection nets = (Collection)obj;
                for (Object net : nets) {
                    if (net instanceof CyNetwork) {
                        networks.add((CyNetwork)net);
                    } else if (net instanceof String) {
                        String netId = (String)obj;
                        CyNetwork network = Cytoscape.getNetwork(netId);
                        if (network!=null && network!=Cytoscape.getNullNetwork()) {
                            networks.add(network);
                        } else {
                            throw new CyCommandException("Network "+netId+" does not exist.");
                        }
                    }
                }
            } else {
                throw new CyCommandException(NETWORK_LIST+" must be String or Collection<String> "
                        + "or CyNetwork or Collection<CyNetwork>.\n");
            }

            if (networks.isEmpty()) {
                throw new CyCommandException("No network to work on.");
            }
        }

        Set<String> attributes = new HashSet();
        attributes.add("ID"); //TODO: remove in Cy3
        attributes.addAll(java.util.Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));

        // parse source attributes
        Set<String> srcAttrs = new HashSet();
        obj = args.get(SOURCE_ATTR);
        if (obj==null) {
            throw new CyCommandException("Message content does not contain field \"" + SOURCE_ATTR +"\"");
        }else{
            if (obj instanceof String) {
                String attr = (String)obj;
                if (attributes.contains(attr)) {
                    srcAttrs.add(attr);
                } else {
                    throw new CyCommandException("Node attribute "+attr+" does not exist.");
                }
            } else if (obj instanceof Collection) {
                Collection<String> attrs = (Collection)obj;
                for (String attr : attrs) {
                    if (attributes.contains(attr)) {
                        srcAttrs.add(attr);
                    } else {
                        throw new CyCommandException("Node attribute "+attr+" does not exist.");
                    }
                }
            } else {
                throw new CyCommandException(SOURCE_ATTR+" must be String or Collection<String>.\n");
            }

            if (srcAttrs.isEmpty()) {
                throw new CyCommandException("No network to work on.");
            }
        }

        Set<DataSource> srcDataSources = null;
        Set<DataSource> tgtDataSources = null;
        try {
            IDMapperCapabilities cap
                  = IDMapperClientManager.selectedIDMapperStack().getCapabilities();
            srcDataSources = cap.getSupportedSrcDataSources();
            tgtDataSources = cap.getSupportedTgtDataSources();
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        if (srcDataSources==null || srcDataSources.isEmpty()
                    || tgtDataSources==null || tgtDataSources.isEmpty()) {
            throw new CyCommandException("No supported source or target id type. Please select mapping resources first.");
        }

        Set<String> supportedSrcTypes = new HashSet(srcDataSources.size());
        for (DataSource ds : srcDataSources) {
            String fullName = ds.getFullName();
            if (fullName!=null) {
                supportedSrcTypes.add(ds.getFullName());
            } else {
                // TODO: how to deal?
            }
        }

        Set<String> supportedTgtTypes = new HashSet(tgtDataSources.size());
        for (DataSource ds : tgtDataSources) {
            String fullName = ds.getFullName();
            if (fullName!=null) {
                supportedTgtTypes.add(ds.getFullName());
            } else {
                // TODO: how to deal?
            }
        }

        // parse source type
        Set<String> srcTypes = new HashSet();
        obj = args.get(SOURCE_TYPE);
        if (obj==null) {
            srcTypes.addAll(supportedSrcTypes);
        }else{
            if (obj instanceof String) {
                String type = (String)obj;
                if (supportedSrcTypes.contains(type)) {
                    srcTypes.add(type);
                } else {
                    throw new CyCommandException("Source ID type "+type+" does not exist.");
                }
            } else if (obj instanceof Set) {
                for (String type : (Set<String>)obj) {
                    if (supportedSrcTypes.contains(type)) {
                        srcTypes.add(type);
                    } else {
                        throw new CyCommandException("Source ID type "+type+" does not exist.");
                    }
                }

                if (srcTypes.isEmpty()) {
                    throw new CyCommandException("No source ID type to work on.");
                }
            } else {
                throw new CyCommandException(SOURCE_TYPE+" must be String or Collection<String>.");
            }
        }

        //parse target id type
        String tgtType = getArg(command, TARGET_TYPE, args);
        if (!supportedTgtTypes.contains(tgtType)) {
            throw new CyCommandException("Please specified a supported target ID type.");
        }

        String tgtAttr = getArg(command, TARGET_ATTR, args);
        Set<String> usedName = new HashSet();
        usedName.add("ID"); //TODO remove in Cy3
        usedName.addAll(java.util.Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));

        // mapping ids
        AttributeBasedIDMapping service
                    = new AttributeBasedIDMappingImpl();
        Map<String,Set<DataSourceWrapper>> mapAttrTypes = new HashMap();
        for (String attr : srcAttrs) {
            Set<DataSourceWrapper> dsws = new HashSet(srcTypes.size());
            for (String srcType : srcTypes) {
            dsws.add(DataSourceWrapper.getInstance(srcType,
                    DataSourceWrapper.DsAttr.DATASOURCE));
            }
            mapAttrTypes.put(attr, dsws);
        }

        DataSourceWrapper dsw = DataSourceWrapper.getInstance(tgtType,
                DataSourceWrapper.DsAttr.DATASOURCE);
        Map<String,DataSourceWrapper> mapTgtTypeAttr = Collections.singletonMap(tgtAttr, dsw);

        try {
            service.map(networks, mapAttrTypes, mapTgtTypeAttr);
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        result.addMessage(service.getReport());

        return result;
    }

    private CyCommandResult generalMapping(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        Set<String> srcIDs = null;
        Object obj = args.get(SOURCE_ID);
        if (obj instanceof String) {
            srcIDs = Collections.singleton((String)obj);
        } else if (obj instanceof Collection) {
            srcIDs = new HashSet((Collection<String>)obj);
        }

        String srcType = getArg(command, SOURCE_TYPE, args);
        String tgtType = getArg(command, TARGET_TYPE, args);

        if (srcIDs==null || srcType==null || tgtType==null) {
            throw new CyCommandException("Message content must contain " +
                    "Source_ID, SOURCE_ID_TYPE and TARGET_ID_TYPE.");
        }

        if (!DataSource.getFullNames().contains(srcType)) {
            throw new CyCommandException("Source type \""+srcType+"\" does not exist.");
        }

        if (!DataSource.getFullNames().contains(tgtType)) {
           throw new CyCommandException("Target type \""+tgtType+"\" does not exist.");
        }

        IDMapperStack stack = IDMapperClientManager.selectedIDMapperStack();

        IDMapperCapabilities caps = stack.getCapabilities();
        DataSource srcDs = DataSource.getByFullName(srcType);
        DataSource tgtDs = DataSource.getByFullName(tgtType);
        boolean supported = false;
        try {
            supported = caps.isMappingSupported(srcDs, tgtDs);
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        if (!supported)
            throw new CyCommandException("Mapping from \""+srcDs+"\" to \""+tgtDs+"\" is not supported.");

        Set<Xref> srcXrefs = new HashSet(srcIDs.size());
        for (String id : srcIDs) {
            srcXrefs.add(new Xref(id, srcDs));
        }

        DataSource[] tgtDataSources = {tgtDs};

        Map<Xref,Set<Xref>> mapping = null;
        try {
            mapping = stack.mapID(srcXrefs, tgtDataSources);
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        if (mapping==null) {
            result.addError("No mapping was performed.");
        } else {
            Map<String, Set<String>> mapSrcIdTargetIDs = new HashMap(mapping.size());
            for (Xref srcXref : mapping.keySet()) {
                Set<Xref> tgtXrefs = mapping.get(srcXref);
                if (tgtXrefs!=null) {
                    Set<String> tgtIds = new HashSet();
                    for (Xref tgtXref : tgtXrefs) {
                        tgtIds.add(tgtXref.getId());
                    }
                    mapSrcIdTargetIDs.put(srcXref.getId(), tgtIds);
                }
            }

            result.addResult(mapSrcIdTargetIDs);
            result.addMessage(""+mapSrcIdTargetIDs.size()+"out of "+srcIDs.size()
                    +" source IDs was mapping from type: "+srcType+" to type:"+tgtType);
        }

        return result;
    }

    private CyCommandResult idExists(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        
        Set<String> srcIDs = null;
        Object obj = args.get(SOURCE_ID);
        if (obj instanceof String) {
            srcIDs = Collections.singleton((String)obj);
        } else if (obj instanceof Collection) {
            srcIDs = new HashSet((Collection<String>)obj);
        }

        if (srcIDs==null || srcIDs.isEmpty()) {
            throw new CyCommandException("Parameter "+SOURCE_ID+" must contains one or more source IDs.");
        }

        Set<String> types = new HashSet<String>();
        for (String id : srcIDs) {
            Set<DataSource> dss = DataSourcePatterns.getDataSourceMatches(id);
            for (DataSource ds : dss)
                types.add(ds.getFullName());
        }

        result.addResult(types);

        StringBuilder sb = new StringBuilder();
        sb.append(types.size());
        sb.append(" ID types matchs:\n");
        for (String type : types) {
            sb.append("\t");
            sb.append(type);
            sb.append("\n");
        }
        result.addMessage(sb.toString());

        return result;
    }

    private CyCommandResult guessIDTypes(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();

        String id = getArg(command, SOURCE_ID, args);
        String type = getArg(command, SOURCE_TYPE, args);

        
        return result;
    }

    public static CyCommandHandler register(String namespace) throws RuntimeException {
            // Get the namespace
            CyCommandNamespace ns = CyCommandManager.reserveNamespace(namespace);

            // Handle the simple commands ourselves
            CyCommandHandler ch = new CyThesaurusNamespace(ns);

            return ch;
    }
}

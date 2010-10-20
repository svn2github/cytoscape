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
import csplugins.id.mapping.AttributeBasedIDMappingImpl;
import csplugins.id.mapping.util.DataSourceWrapper;
import csplugins.id.mapping.util.IDMapperWrapper;
import csplugins.id.mapping.util.XrefWrapper;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

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
    private static final String ADD_RESOURCE = "register resource";
    private static final String REMOVE_RESOURCE = "unregister resource";
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
    private static final String REPORT = "report";
    private static final String FIRST_ONLY = "firstonly";

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

        addArgument(REMOVE_RESOURCE, CONN_STRING);
        addDescription(REMOVE_RESOURCE, "Remove/unregister an ID mapping resource");

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
        addArgument(ATTRIBUTE_BASED_MAPPING, FIRST_ONLY); // optional
        addDescription(ATTRIBUTE_BASED_MAPPING, "Mapping IDs of the sourceidtype(s) in "
                + "sourceattribute to IDs of the targetidtype and save them to targetattribute.");

        addArgument(GENERAL_MAPPING, SOURCE_ID);
        addArgument(GENERAL_MAPPING, SOURCE_TYPE);
        addArgument(GENERAL_MAPPING, TARGET_TYPE);
        addArgument(GENERAL_MAPPING, FIRST_ONLY);
        addDescription(GENERAL_MAPPING, "Mapping sourceid(s) of sourcetype to targettype.");

        addArgument(CHECK_ID_EXIST, SOURCE_ID);
        addArgument(CHECK_ID_EXIST, SOURCE_TYPE);
        addDescription(CHECK_ID_EXIST, "Check if ID exists.");

        addArgument(GUESS_TYPE, SOURCE_ID);
        addDescription(GUESS_TYPE, "Guess ID types from a set of IDs (only ID types in the selected resources will be included).");

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
            removeResource(command, args);
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
        result.addMessage("Current version: "+CyThesaurusPlugin.VERSION);
        return result;
    }

    private CyCommandResult mainDialog(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        final CyThesaurusDialog dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
        dialog.setLocationRelativeTo(Cytoscape.getDesktop());
        dialog.setMapSrcAttrIDTypes(CyThesaurusPlugin.mapSrcAttrIDTypes);
        dialog.setVisible(true);
        result.addResult(!dialog.isCancelled());
        return result;
    }

    private CyCommandResult resourcesDialog(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        IDMappingSourceConfigDialog srcConfDialog
                = new IDMappingSourceConfigDialog(Cytoscape.getDesktop(), true);
        srcConfDialog.setVisible(true);
        result.addResult(srcConfDialog.isModified());
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
            boolean succ = IDMapperClientManager.registerClient(client, true, true);
            if (succ)
                result.addMessage("Successfully registered");
            else
                result.addError("Failed to register the resource\n");
        } catch (Exception e) {
            throw new CyCommandException(e);
//            result.addError("Failed to register the resource\n"+e.getMessage());
        }
        return result;
    }

    private CyCommandResult removeResource(String command, Map<String, Object>args) throws CyCommandException {
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
             throw new CyCommandException(e);
//            result.addError("Failed to remove the resource\n"+e.getMessage());
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
                IDMapperClientManager.setClientSelection(client, true);
                result.addResult(Boolean.TRUE);
                result.addMessage("Selected.");
            }
        } catch (Exception e) {
             throw new CyCommandException(e);
//            result.addError("Failed to remove the resource\n"+e.getMessage());
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
                IDMapperClientManager.setClientSelection(client, false);
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
        Set<DataSourceWrapper> dss = IDMapperClientManager.getSupportedSrcTypes();
        StringBuilder message = new StringBuilder();
        message.append("There are ");
        message.append(dss.size());
        message.append(" supported source ID types:\n");
        for (DataSourceWrapper ds : dss) {
            types.add(ds.value());
            message.append("\t");
            message.append(ds.value());
            message.append("\n");
        }

        result.addMessage(message.toString());
        result.addResult(types);
        return result;
    }

    private CyCommandResult getTgtIdTypes(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        Set<String> types = new HashSet();
        Set<DataSourceWrapper> dss = IDMapperClientManager.getSupportedTgtTypes();
        StringBuilder message = new StringBuilder();
        message.append("There are ");
        message.append(dss.size());
        message.append(" supported target ID types:\n");
        for (DataSourceWrapper ds : dss) {
            types.add(ds.value());
            message.append("\t");
            message.append(ds.value());
            message.append("\n");
        }

        result.addMessage(message.toString());
        result.addResult(types);
        return result;
    }

    private CyCommandResult isMappingSupported(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();
        String srctype = getArg(command, SOURCE_TYPE, args);
        String tgttype = getArg(command, TARGET_TYPE, args);
        if (srctype == null || tgttype==null)
                throw new CyCommandException(SOURCE_TYPE + " and " + TARGET_TYPE + " cannot be null.");

        Set<DataSourceWrapper> srcTypes = IDMapperClientManager.getSupportedSrcTypes();
        DataSourceWrapper srcds = DataSourceWrapper.getInstance(srctype);
        if (!srcTypes.contains(srcds)){
        	result.addResult(Boolean.FALSE);
            result.addMessage(srctype + " is not supported.");
        }
        Set<DataSourceWrapper> tgtTypes = IDMapperClientManager.getSupportedTgtTypes();
        DataSourceWrapper tgtds = DataSourceWrapper.getInstance(tgttype);
        if (!tgtTypes.contains(tgtds)){
        	result.addResult(Boolean.FALSE);
            result.addMessage(tgttype + " is not supported.");
        }
        try {
             if (IDMapperClientManager.isMappingSupported(srcds, tgtds)) {
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
        }

        if (networks.isEmpty()) {
            throw new CyCommandException("No network to work on.");
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
                if (!attributes.contains(attr)) {
                    throw new CyCommandException("Node attribute "+attr+" does not exist.");
                }
                srcAttrs.add(attr);
            } else if (obj instanceof Collection) {
                Collection<String> attrs = (Collection)obj;
                for (String attr : attrs) {
                    if (!attributes.contains(attr)) {
                        throw new CyCommandException("Node attribute "+attr+" does not exist.");
                    }
                    srcAttrs.add(attr);
                }
            } else {
                throw new CyCommandException(SOURCE_ATTR+" must be String or Collection<String>.\n");
            }

            if (srcAttrs.isEmpty()) {
                throw new CyCommandException("No source attribute to work on.");
            }
        }

        Set<DataSourceWrapper> srcDataSources = IDMapperClientManager.getSupportedSrcTypes();
        Set<DataSourceWrapper> tgtDataSources = IDMapperClientManager.getSupportedTgtTypes();

        if (srcDataSources==null || srcDataSources.isEmpty()
                    || tgtDataSources==null || tgtDataSources.isEmpty()) {
            throw new CyCommandException("No supported source or target id type. Please select mapping resources first.");
        }

        // parse source type
        Set<DataSourceWrapper> srcTypes;
        obj = args.get(SOURCE_TYPE);
        if (obj==null) {
            srcTypes = srcDataSources;
        }else{
            srcTypes = new HashSet<DataSourceWrapper>();
            if (obj instanceof String) {
                String type = (String)obj;
                DataSourceWrapper dsw = DataSourceWrapper.getInstance(type);
                if (srcDataSources.contains(dsw)) {
                    srcTypes.add(dsw);
                } else {
                    throw new CyCommandException("Source ID type "+type+" does not exist.");
                }
            } else if (obj instanceof Set) {
                for (String type : (Set<String>)obj) {
                    DataSourceWrapper dsw = DataSourceWrapper.getInstance(type);
                    if (srcDataSources.contains(dsw)) {
                        srcTypes.add(dsw);
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
        String type = getArg(command, TARGET_TYPE, args);
        DataSourceWrapper tgtType = DataSourceWrapper.getInstance(type);
        if (!tgtDataSources.contains(tgtType)) {
            throw new CyCommandException("Please specified a supported target ID type.");
        }

        String tgtAttr = getArg(command, TARGET_ATTR, args);
        if (tgtAttr==null) {
            if (attributes.contains(type)) {
                int num = 1;
                while (attributes.contains(type+"."+num)) {
                    num ++;
                }
                tgtAttr = type+"."+num;
            } else {
                tgtAttr = type;
            }
        } else {
            if (attributes.contains(tgtAttr)) {
//                throw new CyCommandException(tgtAttr+" is an existing attribute "
//                        + "and hence cannot be used as target attribute name.");
            }
        }

        obj = args.get(FIRST_ONLY);
        boolean firstOnly = false;
        if (obj instanceof Boolean) {
            firstOnly = (Boolean)obj;
        } else if (obj instanceof String) {
            firstOnly = ((String)obj).equalsIgnoreCase("yes")
                    || ((String)obj).equalsIgnoreCase("true");
        }

        // mapping ids
        AttributeBasedIDMappingImpl service
                    = new AttributeBasedIDMappingImpl();
        Map<String,Set<DataSourceWrapper>> mapAttrTypes = new HashMap();
        for (String attr : srcAttrs) {
            mapAttrTypes.put(attr, srcTypes);
        }

        Map<String,DataSourceWrapper> mapTgtTypeAttr = Collections.singletonMap(tgtAttr, tgtType);

        Map<String,Byte> attrNameType = Collections.singletonMap(tgtAttr,
                firstOnly?CyAttributes.TYPE_STRING:CyAttributes.TYPE_SIMPLE_LIST);
        service.suggestTgtAttrType(attrNameType);

        try {
            service.map(networks, mapAttrTypes, mapTgtTypeAttr);
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        result.addMessage(service.getReport());
        result.addMessage("Target IDs were save to attribute: " + tgtAttr);
        result.addResult(TARGET_ATTR, tgtAttr);
        result.addResult(REPORT, service.getReport());

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

        Set<DataSourceWrapper> srcTypes = IDMapperClientManager.getSupportedSrcTypes();
        DataSourceWrapper srcDs = DataSourceWrapper.getInstance(srcType);
        if (!srcTypes.contains(srcDs))
            throw new CyCommandException(srcType + " is not supported.");

        Set<DataSourceWrapper> tgtTypes = IDMapperClientManager.getSupportedTgtTypes();
        DataSourceWrapper tgtDs = DataSourceWrapper.getInstance(tgtType);
        if (!tgtTypes.contains(tgtDs))
            throw new CyCommandException(tgtType + " is not supported.");

        if (!IDMapperClientManager.isMappingSupported(srcDs, tgtDs))
            throw new CyCommandException("Mapping from \""+srcDs+"\" to \""+tgtDs+"\" is not supported.");

        Set<XrefWrapper> srcXrefs = new HashSet(srcIDs.size());
        for (String id : srcIDs) {
            srcXrefs.add(new XrefWrapper(id, srcDs));
        }

        Set<DataSourceWrapper> tgtDataSources = Collections.singleton(tgtDs);


        Map<XrefWrapper,Set<XrefWrapper>> mapping = IDMapperWrapper.mapID(srcXrefs, tgtDataSources);

        if (mapping==null) {
            result.addError("No mapping was performed.");
        } else {
            Map<String, Set<String>> mapSrcIdTargetIDs = new HashMap(mapping.size());
            StringBuilder mappedId = new StringBuilder();
            for (XrefWrapper srcXref : mapping.keySet()) {
                Set<XrefWrapper> tgtXrefs = mapping.get(srcXref);
                if (tgtXrefs!=null) {
                    String srcId = srcXref.getValue();
                    Set<String> tgtIds = new HashSet();
                    for (XrefWrapper tgtXref : tgtXrefs) {
                        tgtIds.add(tgtXref.getValue());
                    }
                    mapSrcIdTargetIDs.put(srcId, tgtIds);

                    mappedId.append(srcId);
                    mappedId.append("<=>");
                    mappedId.append(tgtIds.toString());
                    mappedId.append("\n");
                }
            }

            result.addResult(mapSrcIdTargetIDs);
            result.addMessage(""+mapSrcIdTargetIDs.size()+"out of "+srcIDs.size()
                    +" source IDs was mapping from type: "+srcType+" to type:"+tgtType
                    +"\n"+mappedId.toString());
        }

        return result;
    }

    private CyCommandResult idExists(String command, Map<String, Object>args) throws CyCommandException {
        CyCommandResult result = new CyCommandResult();

        String id = getArg(command, SOURCE_ID, args);
        String type = getArg(command, SOURCE_TYPE, args);

       if (id==null || type==null) {
            throw new CyCommandException("Null argument of "+SOURCE_ID+" or "+SOURCE_TYPE);
        }

        if (!DataSource.getFullNames().contains(type)) {
            throw new CyCommandException("Type \""+type+"\" does not exist.");
        }

        Set<DataSourceWrapper> srcTypes = IDMapperClientManager.getSupportedSrcTypes();
        DataSourceWrapper srcds = DataSourceWrapper.getInstance(type);
        if (!srcTypes.contains(srcds))
            throw new CyCommandException(type + " is not supported.");

        if (IDMapperWrapper.xrefExists(new XrefWrapper(id, srcds))) {
            result.addResult(Boolean.TRUE);
            result.addMessage(type+":"+id+" exists.");
        } else {
            result.addResult(Boolean.FALSE);
            result.addMessage(type+":"+id+" does not exist.");
        }

        return result;
    }

    private CyCommandResult guessIDTypes(String command, Map<String, Object>args) throws CyCommandException {
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

        Set<DataSource> selectedDss = new HashSet<DataSource>();
        try {
            IDMapperCapabilities cap = IDMapperClientManager.selectedIDMapperStack().getCapabilities();
            selectedDss.addAll(cap.getSupportedSrcDataSources());
            selectedDss.addAll(cap.getSupportedTgtDataSources());
        } catch (Exception e) {
            throw new CyCommandException(e);
        }

        Set<String> types = new HashSet<String>();
        for (String id : srcIDs) {
            Set<DataSource> dss = DataSourcePatterns.getDataSourceMatches(id);
            dss.retainAll(selectedDss);
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

    public static CyCommandHandler register(String namespace) throws RuntimeException {
            // Get the namespace
            CyCommandNamespace ns = CyCommandManager.reserveNamespace(namespace);

            // Handle the simple commands ourselves
            CyCommandHandler ch = new CyThesaurusNamespace(ns);

            return ch;
    }
}

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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;

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
    private static final String IS_MAPPING_SUPPORT = "is mapping supported";

    // Arguments
    private static final String CLASS_PATH = "classpath";
    private static final String CONN_STRING = "connstring"; // connection string
    private static final String DISPLAY_NAME = "displayname";
    private static final String SOURCE_TYPE = "sourcetype";
    private static final String TARGET_TYPE = "targettype";

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

        addArgument(IS_MAPPING_SUPPORT);
        addArgument(IS_MAPPING_SUPPORT, SOURCE_TYPE);
        addArgument(IS_MAPPING_SUPPORT, TARGET_TYPE);
        addDescription(IS_MAPPING_SUPPORT, "Check if ID mapping between two ID types is supported");
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
        CyCommandResult result = new CyCommandResult();

        if (command.equals(VERSION)) {
            result.addResult(CyThesaurusPlugin.VERSION);
        } else if (command.equals(MAIN_DIALOG)) {
            final CyThesaurusDialog dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
            dialog.setLocationRelativeTo(Cytoscape.getDesktop());
            dialog.setMapSrcAttrIDTypes(CyThesaurusPlugin.mapSrcAttrIDTypes);
            dialog.setVisible(true);
        } else if (command.equals(RESOURCE_DIALOG)) {
            IDMappingSourceConfigDialog srcConfDialog
                    = new IDMappingSourceConfigDialog(Cytoscape.getDesktop(), true);
            srcConfDialog.setVisible(true);
        } else if (command.equals(LIST_RESOURCE)) {
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
        } else if (command.equals(LIST_SELECTED_RESOURCE)) {
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
        } else if (command.equals(ADD_RESOURCE)) {
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
        } else if (command.equals(REMOVE_RESOURCE)) {
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
        } else if (command.equals(SELECT_RESOURCE)) {
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
        }  else if (command.equals(DESELECT_RESOURCE)) {
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
        } else if (command.equals(GET_SRC_ID_TYPES)) {
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
        } else if (command.equals(GET_TGT_ID_TYPES)) {
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
        } else if (command.equals(IS_MAPPING_SUPPORT)) {
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
        } else {
                throw new CyCommandException(getHandlerName()+": unknown command "+command);
        }

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

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


package cytoscape.cythesaurus.service;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author gjj
 */
public class CyThesaurusServiceCommandBasedClient
        implements CyThesaurusServiceClient{

    private final String NAMESPACE = "idmapping";

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

    public CyThesaurusServiceCommandBasedClient() {
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean isServiceAvailable() {
        List<String> cmds = CyCommandManager.getCommandList(NAMESPACE);
        return cmds!=null && !cmds.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public double serviceVersion() {
        CyCommandResult result = executeCommand(VERSION, null);
        if (result==null || !result.successful())
            return -1.0;

        return (Double)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public boolean openAttributeConfigDialog() {
        CyCommandResult result = executeCommand(MAIN_DIALOG, null);
        if (result==null || !result.successful())
            return false;

        return (Boolean)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public boolean openMappingResourceConfigDialog() {
        CyCommandResult result = executeCommand(RESOURCE_DIALOG, null);
        if (result==null || !result.successful())
            return false;

        return (Boolean)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> allIDMappers() {
        CyCommandResult result = executeCommand(LIST_RESOURCE, null);
        if (result==null || !result.successful())
            return Collections.emptySet();

        return (Set<String>)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> selectedIDMappers() {
        CyCommandResult result = executeCommand(LIST_SELECTED_RESOURCE, null);
        if (result==null || !result.successful())
            return Collections.emptySet();

        return (Set<String>)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public boolean registerIDMapper(String connectionString, String classPath, String displayName) {
        Map<String,Object> args = new HashMap<String,Object>(3);
        args.put(CONN_STRING, connectionString);
        args.put(CLASS_PATH, classPath);
        args.put(DISPLAY_NAME, displayName);
        CyCommandResult result = executeCommand(ADD_RESOURCE, args);
        return result!=null && result.successful();
    }

    /**
     * {@inheritDoc}
     */
    public boolean unregisterIDMapper(String connectionString) {
        Map<String,Object> args = new HashMap<String,Object>(1);
        args.put(CONN_STRING, connectionString);
        CyCommandResult result = executeCommand(REMOVE_RESOURCE, args);
        return result!=null && result.successful();
    }

    /**
     * {@inheritDoc}
     */
    public boolean setIDMapperSelect(String connectionString, boolean selected) {
        Map<String,Object> args = new HashMap<String,Object>(1);
        args.put(CONN_STRING, connectionString);
        CyCommandResult result;
        if (selected) {
            result = executeCommand(SELECT_RESOURCE, args);
        } else {
            result = executeCommand(DESELECT_RESOURCE, args);
        }
        return result!=null && result.successful();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> supportedSrcIDTypes() {
        CyCommandResult result = executeCommand(GET_SRC_ID_TYPES, null);
        if (result==null || !result.successful())
            return Collections.emptySet();

        return (Set<String>)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> supportedTgtIDTypes() {
        CyCommandResult result = executeCommand(GET_TGT_ID_TYPES, null);
        if (result==null || !result.successful())
            return Collections.emptySet();

        return (Set<String>)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMappingSupported(String srcType, String tgtType) {
        Map<String,Object> args = new HashMap<String,Object>(2);
        args.put(SOURCE_TYPE, srcType);
        args.put(TARGET_TYPE, tgtType);
        CyCommandResult result = executeCommand(CHECK_MAPPING_SUPPORT, args);
        return result!=null && result.successful();
    }

    /**
     * {@inheritDoc}
     */
    public boolean mapID(Set<String> netIds, String srcAttrName,
            String tgtAttrName, Set<String> srcIDTypes, String tgtIDType) {
        Map<String,Object> args = new HashMap<String,Object>(5);
        args.put(NETWORK_LIST, netIds);
        args.put(SOURCE_ATTR, srcAttrName);
        args.put(TARGET_ATTR, tgtAttrName);
        args.put(SOURCE_TYPE, srcIDTypes);
        args.put(TARGET_TYPE, tgtIDType);
        CyCommandResult result = executeCommand(ATTRIBUTE_BASED_MAPPING, args);
        return result!=null && result.successful();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String,String> mapID(Set<String> netIds, String srcAttrName,
            Set<String> srcIDTypes, Set<String> tgtIDTypes) {
        Map<String,String> mapTgtTypeAttr = new HashMap<String,String>(tgtIDTypes.size());
        for (String tgtType : tgtIDTypes) {
            Map<String,Object> args = new HashMap<String,Object>(5);
            args.put(NETWORK_LIST, netIds);
            args.put(SOURCE_ATTR, srcAttrName);
//            args.put(TARGET_ATTR, null);
            args.put(SOURCE_TYPE, srcIDTypes);
            args.put(TARGET_TYPE, tgtType);
            CyCommandResult result = executeCommand(ATTRIBUTE_BASED_MAPPING, args);
            if (result==null || !result.successful()) {
                continue; // TODO: how to deal?
            }

            mapTgtTypeAttr.put(tgtType, result.getStringResult(TARGET_ATTR));
        }

        return mapTgtTypeAttr;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Set<String>> mapID(Set<String> srcIDs, String srcIDType,
            String tgtIDType) {
        Map<String,Object> args = new HashMap<String,Object>(3);
        args.put(SOURCE_ID, srcIDs);
        args.put(SOURCE_TYPE, srcIDType);
        args.put(TARGET_TYPE, tgtIDType);
        CyCommandResult result = executeCommand(GENERAL_MAPPING, args);
        if (result==null || !result.successful()) {
            return Collections.emptyMap();
        }
        return (Map<String, Set<String>>)result.getResult();
    }

    /**
     * {@inheritDoc}
     */
    public boolean idExists(String id, String type) {
        Map<String,Object> args = new HashMap<String,Object>(2);
        args.put(SOURCE_ID, id);
        args.put(SOURCE_TYPE, type);
        CyCommandResult result = executeCommand(CHECK_ID_EXIST, args);
        return result!=null && result.successful();
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> guessType(Set<String> ids) {
        Map<String,Object> args = new HashMap<String,Object>(1);
        args.put(SOURCE_ID, ids);
        CyCommandResult result = executeCommand(GUESS_TYPE, args);
        if (result==null || !result.successful()) {
            return Collections.emptySet();
        }
        return (Set<String>)result.getResult();
    }

    private CyCommandResult executeCommand(String command, Map<String,Object> args) {
        CyCommandResult result = null;
        try {
            result = CyCommandManager.execute(NAMESPACE, command, args);
        } catch (CyCommandException e) {
        }

        return result;
    }
}

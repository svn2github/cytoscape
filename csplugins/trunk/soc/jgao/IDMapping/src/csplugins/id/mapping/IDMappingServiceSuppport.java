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

import csplugins.id.mapping.IDMapperClient;

import csplugins.id.mapping.ui.CyThesaurusDialog;
import csplugins.id.mapping.ui.IDMappingSourceConfigDialog;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperStack;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.Xref;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.util.plugins.communication.Message;
import cytoscape.util.plugins.communication.MessageListener;
import cytoscape.util.plugins.communication.PluginsCommunicationSupport;
import cytoscape.util.plugins.communication.ResponseMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gjj
 */
public class IDMappingServiceSuppport {
    private static final String MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE = "SUPPORTED_ID_TYPE";
    private static final String MSG_TYPE_REQUEST_ATTRIBUTE_BASED_MAPPING = "ATTRIBUTE_BASED_MAPPING";
    private static final String MSG_TYPE_REQUEST_MAPPING_SERVICE = "MAPPING_SERVICE";
    private static final String MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG = "MAPPING_SRC_CONFIG_DIALOG";
    private static final String MSG_TYPE_REQUEST_MAPPING_DIALOG = "MAPPING_DIALOG";
    private static final String MSG_TYPE_REQUEST_MAPPERS = "ID_MAPPERS";
    private static final String MSG_TYPE_REQUEST_REGISTER_MAPPER = "REGISTER_ID_MAPPER";
    private static final String MSG_TYPE_REQUEST_UNREGISTER_MAPPER = "UNREGISTER_ID_MAPPER";
    private static final String MSG_TYPE_REQUEST_SELECT_MAPPER = "SELECT_ID_MAPPER";;
    private static final String MSG_TYPE_REQUEST_ID_EXIST = "ID_EXIST";

    private static final String NETWORK_ID = "NETWORK_ID";
    private static final String SOURCE_ATTR = "SOURCE_ATTR";
    private static final String SOURCE_ID_TYPE = "SOURCE_ID_TYPE";
    private static final String MAP_TARGET_ID_TYPE_ATTR = "MAP_TARGET_ID_TYPE_ATTR";
    private static final String SELECTED = "SELECTED";
    private static final String CLASS_PATH = "CLASS_PATH";
    private static final String CONNECTION_STRING = "CONNECTION_STRING";
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String SOURCE_ID = "SOURCE_ID";

    private static final String SUCCESS = "SUCCESS";
    private static final String IS_CANCELLED = "IS_CANCELLED";
    private static final String REPORT = "REPORT";
    private static final String TARGET_ID_TYPE = "TGT_ID_TYPE";
    private static final String CLIENTS = "CLIENTS";
    private static final String ID_EXISTS = "ID_EXISTS";
    private static final String MAPPING_RESULT = "MAPPING_RESULT";
    
    private static final String pluginName = FinalStaticValues.PLUGIN_NAME;

    static void addService() {
        MessageListener ml = new MessageListener() {
            public void messagedReceived(Message msg) {
                String msgType = msg.getType();
                if (msgType==null) return;

                ResponseMessage response = null;
                if (msgType.compareTo(Message.MSG_TYPE_TEST)==0) {
                    response = testService(msg);
                } else if(msgType.compareTo(Message.MSG_TYPE_GET_RECEIVERS)==0) {
                    response = getReceiversService(msg);
                } else if(msgType.compareTo(Message.MSG_TYPE_GET_MSG_TYPES)==0) {
                    response = getSupportedTypesService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_ATTRIBUTE_BASED_MAPPING)==0) {
                    response = attributeBasedMappingService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG)==0) {
                    response = mappingSrcConfigDialogService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPING_DIALOG)==0) {
                    response = mappingDialogService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE)==0) {
                    response = supportedIdTypeService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPERS)==0) {
                    response = getIDMapperService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_REGISTER_MAPPER)==0) {
                    response = registerIDMapperService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_SELECT_MAPPER)==0) {
                    response = selectIDMapperService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_ID_EXIST)==0) {
                    response = idExistsService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPING_SERVICE)==0) {
                    response = mappingService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_UNREGISTER_MAPPER)==0) {
                    response = unregisterIDMapperService(msg);
                }


                
                // send respond message
                if (response!=null) {
                    PluginsCommunicationSupport.sendMessage(response);
                }
            }
        };

        PluginsCommunicationSupport.addMessageListener(pluginName, ml);
    }

    private static ResponseMessage createResponse(Message msg, Object responseContent) {
        String sender = msg.getSender();
        String msgId = msg.getId();
        if (sender==null || msgId==null) {
            return null;
        }

        String responseId = sender+"_"+msgId+"_"+pluginName+"_"+msgId;
        return new ResponseMessage(responseId, msgId, pluginName, sender, responseContent);
    }

    private static ResponseMessage testService(Message msg) {
        Map content = new HashMap();
        content.put(SUCCESS, true);

        return createResponse(msg, content);
    }

    private static ResponseMessage getReceiversService(Message msg) {
        return createResponse(msg, null);
    }

    private static ResponseMessage getSupportedTypesService(Message msg) {
        Map content = new HashMap();
        Set<String> supportedTypes = new HashSet();
        supportedTypes.add(Message.MSG_TYPE_TEST);
        supportedTypes.add(MSG_TYPE_REQUEST_ATTRIBUTE_BASED_MAPPING);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPING_DIALOG);
        supportedTypes.add(MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPERS);
        supportedTypes.add(MSG_TYPE_REQUEST_REGISTER_MAPPER);
        supportedTypes.add(MSG_TYPE_REQUEST_ID_EXIST);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPING_SERVICE);
        supportedTypes.add(MSG_TYPE_REQUEST_UNREGISTER_MAPPER);
        content.put(Message.MSG_TYPE_GET_MSG_TYPES, supportedTypes);

        return createResponse(msg, content);
    }

    private static ResponseMessage getIDMapperService(Message msg) {
        Set<String> mappers = new HashSet();

        Boolean selected = null; // by default all id mapping will be returned.
        Object obj = msg.getContent();
        if (obj!=null && obj instanceof Map) {
            obj = ((Map)obj).get(SELECTED);
            if (obj instanceof Boolean) {
                selected = (Boolean)obj;
            }
        }

        Set<IDMapperClient> clients = IDMapperClientManager.allClients();
        for (IDMapperClient client : clients) {
            if (selected==null || selected==client.isSelected()) {
                mappers.add(client.getConnectionString());
            }
        }

        Map content = new HashMap();
        content.put(CLIENTS, mappers);
        content.put(SUCCESS, true);

        return createResponse(msg, content);
    }

    private static ResponseMessage registerIDMapperService(Message msg) {
        String connStr = null;
        String classPath = null;
        String display = null;
        Object obj = msg.getContent();
        if (obj!=null && obj instanceof Map) {
            Object obj1 = ((Map)obj).get(CLASS_PATH);
            if (obj1 instanceof String) {
                classPath = (String)obj1;
            }

            obj1 = ((Map)obj).get(CONNECTION_STRING);
            if (obj1 instanceof String) {
                connStr = (String)obj1;
            }

            obj1 = ((Map)obj).get(DISPLAY_NAME);
            if (obj1 instanceof String) {
                display = (String)obj1;
            }
        }

        Map content = new HashMap();
        if (connStr==null || classPath==null) {
            content.put(SUCCESS, false);
            content.put(REPORT, "Message content must contain " +
                    "CONNECTION_STRING AND CLASS_PATH.");
        } else {
            IDMapperClient client = null;
            try {
                 client = new IDMapperClientImplTunables(connStr,
                        classPath, display);
            } catch (Exception e) {
                e.printStackTrace();
                content.put(SUCCESS, true);
                content.put(REPORT, "Error: "+e.getMessage());
            }

            if (client!=null) {
                IDMapperClientManager.registerClient(client);
                content.put(SUCCESS, true);
            }
        }

        return createResponse(msg, content);
    }

    private static ResponseMessage unregisterIDMapperService(Message msg) {
        String connStr = null;
        Object obj = msg.getContent();
        if (obj!=null && obj instanceof Map) {
            Object obj1 = ((Map)obj).get(CONNECTION_STRING);
            if (obj1 instanceof String) {
                connStr = (String)obj1;
            }
        }

        Map content = new HashMap();
        if (connStr==null) {
            content.put(SUCCESS, false);
            content.put(REPORT, "Message content must contain " +
                    "CONNECTION_STRING.");
        } else {
            IDMapperClientManager.removeClient(connStr);
            content.put(SUCCESS, true);
        }

        return createResponse(msg, content);
    }

    private static ResponseMessage selectIDMapperService(Message msg) {
        String connStr = null;
        Boolean selected = true; // select the id mapper by default
        Object obj = msg.getContent();
        if (obj!=null && obj instanceof Map) {
            Object obj1 = ((Map)obj).get(CONNECTION_STRING);
            if (obj1 instanceof String) {
                connStr = (String)obj1;
            }

            obj1 = ((Map)obj).get(SELECTED);
            if (obj1 instanceof Boolean) {
                selected = (Boolean)obj1;
            }
        }

        Map content = new HashMap();
        if (connStr==null) {
            content.put(SUCCESS, false);
            content.put(REPORT, "Message content must contain " +
                    "CONNECTION_STRING.");
        } else {
            IDMapperClient client = IDMapperClientManager.getClient(connStr);
            if (client==null) {
                content.put(SUCCESS, false);
                content.put(REPORT, "No such ID mapper.");
            } else {
                client.setSelected(selected);
                content.put(SUCCESS, true);
            }
        }

        return createResponse(msg, content);
    }

    private static ResponseMessage supportedIdTypeService(Message msg) {
        Map content = new HashMap();

        Set<DataSource> srcDataSources;
        Set<DataSource> tgtDataSources;
        IDMapperStack stack = IDMapperClientManager.selectedIDMapperStack();
        try {
            srcDataSources = stack.getCapabilities().getSupportedSrcDataSources();
            tgtDataSources = stack.getCapabilities().getSupportedTgtDataSources();
        } catch (IDMapperException ex) {
            ex.printStackTrace();
            content.put(SUCCESS, false);
            content.put(REPORT, "\nIDMapperException:\n"+ex.getMessage());
            return createResponse(msg, content);
        }

        Set<String> srcTypes = new HashSet();
        for(DataSource ds : srcDataSources) {
            srcTypes.add(ds.getFullName());
        }
        content.put(SOURCE_ID_TYPE, srcTypes);

        Set<String> tgtTypes = new HashSet();
        for(DataSource ds : tgtDataSources) {
            tgtTypes.add(ds.getFullName());
        }
        content.put(TARGET_ID_TYPE, tgtTypes);

        content.put(SUCCESS, true);

        return createResponse(msg, content);
    }

    private static ResponseMessage idExistsService(Message msg) {
        String id = null;
        String type = null;

        Object obj = msg.getContent();
        if (obj!=null && obj instanceof Map) {
            Object obj1 = ((Map)obj).get(SOURCE_ID);
            if (obj1 instanceof String) {
                id = (String)obj1;
            }

            obj1 = ((Map)obj).get(SOURCE_ID_TYPE);
            if (obj1 instanceof String) {
                type = (String)obj1;
            }
        }

        Map content = new HashMap();
        if (id==null || type==null) {
            content.put(SUCCESS, false);
            content.put(REPORT, "Message content must contain " +
                    "ID AND TYPE.");
        } else {
            if (!DataSource.getFullNames().contains(type)) {
                content.put(SUCCESS, false);
                content.put(REPORT, "Type \""+type+"\" does not exist.");
            } else {
                DataSource ds = DataSource.getByFullName(type);
                IDMapperStack stack = IDMapperClientManager.selectedIDMapperStack();
                boolean exist = false;
                try {
                    exist = stack.xrefExists(new Xref(id, ds));
                    content.put(SUCCESS, true);
                } catch (IDMapperException e) {
                    e.printStackTrace();
                    content.put(SUCCESS, false);
                    content.put(REPORT, "Error: "+e.getMessage());
                }
            }
        }

        return createResponse(msg, content);
    }

    private static ResponseMessage mappingSrcConfigDialogService(Message msg) {
        IDMappingSourceConfigDialog srcConfDialog
                = new IDMappingSourceConfigDialog(Cytoscape.getDesktop(), true);
        srcConfDialog.setVisible(true);

        Map content = new HashMap();
        content.put(SUCCESS, true);

        return createResponse(msg, content);
    }

    private static ResponseMessage mappingDialogService(Message msg) {
        final CyThesaurusDialog dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
        dialog.setLocationRelativeTo(Cytoscape.getDesktop());
        dialog.setMapSrcAttrIDTypes(CyThesaurusPlugin.mapSrcAttrIDTypes);
        dialog.setVisible(true);

        Map content = new HashMap();
        content.put(IS_CANCELLED, dialog.isCancelled());

        CyThesaurusPlugin.mapSrcAttrIDTypes = dialog.getMapSrcAttrIDTypes();

        return createResponse(msg, content);
    }

    private static ResponseMessage attributeBasedMappingService(Message msg) {
        Object content = msg.getContent();
        boolean succ = true;
        StringBuilder error = new StringBuilder();

        Map map = (Map) content;
        Set<CyNetwork> networks = new HashSet();
        Set<String> srcAttrs = new HashSet();
        Set<DataSource> srcTypes = new HashSet();
        Map<DataSource, String> tgtTypeAttr = new HashMap();

        if (content==null || !(content instanceof Map)) {
            succ = false;
            error.append("Message content must be non-null Map.\n");
        } else {

            // parse networks
            Object obj = map.get(NETWORK_ID);
            if (obj==null) {
                //succ = false;
                //error.append("Message content does not contain field \"" + NETWORK_ID +"\"\n");
                // maping id for all networks
                networks.addAll(Cytoscape.getNetworkSet());
                if (networks.isEmpty()) {
                    succ = false;
                    error.append("No network available\n");
                }
            }else{
                if (obj instanceof String) {
                    String netId = (String)obj;
                    CyNetwork net = Cytoscape.getNetwork(netId);
                    if (net!=null && net!=Cytoscape.getNullNetwork()) {
                        networks.add(net);
                    } else {
                        succ = false;
                        error.append("Network "+netId+" does not exist.\n");
                    }
                } else if (obj instanceof Set) {
                    Set<String> netIds = (Set)obj;
                    for (String netId : netIds) {
                        CyNetwork net = Cytoscape.getNetwork(netId);
                        if (net!=null && net!=Cytoscape.getNullNetwork()) {
                            networks.add(net);
                        } else {
                            error.append("Network "+netId+" does not exist.\n");
                        }
                    }

                    if (networks.isEmpty()) {
                        succ = false;
                    }
                } else {
                    succ = false;
                    error.append(NETWORK_ID + " must be Set<String>.\n");
                }
            }

            Set<String> attributes = new HashSet();
            attributes.add("ID"); //TODO: remove in Cy3
            attributes.addAll(java.util.Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));

            // parse source attributes
            obj = map.get(SOURCE_ATTR);
            if (obj==null) {
                succ = false;
                error.append("Message content does not contain field \"" + SOURCE_ATTR +"\"\n");
            }else{
                if (obj instanceof String) {
                    String attr = (String)obj;
                    if (attributes.contains(attr)) {
                        srcAttrs.add(attr);
                    } else {
                        succ = false;
                        error.append("Node attribute "+attr+" does not exist.\n");
                    }
                } else if (obj instanceof Set) {
                    Set<String> attrs = (Set)obj;
                    for (String attr : attrs) {
                        if (attributes.contains(attr)) {
                            srcAttrs.add(attr);
                        } else {
                            error.append("Node attribute "+attr+" does not exist.\n");
                        }
                    }

                    if (srcAttrs.isEmpty()) {
                        succ = false;
                    }
                } else {
                    succ = false;
                    error.append(SOURCE_ATTR+" must be String or Set<String>.\n");
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
                e.printStackTrace();
            }

            if (srcDataSources==null || srcDataSources.isEmpty()
                        || tgtDataSources==null || tgtDataSources.isEmpty()) {
                succ = false;
                error.append("No supported source or target id type");
            } else {
                // parse source type
                obj = map.get(SOURCE_ID_TYPE);
                if (obj==null) {
                    srcTypes.addAll(srcDataSources);
                }else{
                    Set<String> dss = new HashSet();
                    for (DataSource ds : srcDataSources) {
                        String fullName = ds.getFullName();
                        if (fullName!=null) {
                            dss.add(fullName);
                        } else {
                            //TODO: how to deal?
                        }
                    }

                    if (obj instanceof String) {
                        String type = (String)obj;
                        if (dss.contains(type)) {
                            srcTypes.add(DataSource.getByFullName(type));
                        } else {
                            succ = false;
                            error.append("Source ID type "+type+" does not exist.\n");
                        }
                    } else if (obj instanceof Set) {
                        for (String type : (Set<String>)obj) {
                            srcTypes.add(DataSource.getByFullName(type));
                        }

                        if (srcTypes.isEmpty()) {
                            succ = false;
                        }
                    } else {
                        succ = false;
                        error.append(SOURCE_ID_TYPE+" must be String or String[].\n");
                    }
                }

                //parse target id type
                obj = map.get(MAP_TARGET_ID_TYPE_ATTR);
                if (obj==null || (!(obj instanceof Map) && !(obj instanceof Set))) {
                    succ = false;
                    error.append("Message content must contain a non-null \"" + MAP_TARGET_ID_TYPE_ATTR +"\"\n");
                }else{
                    Set<String> dss = new HashSet();
                    for (DataSource ds : tgtDataSources) {
                        String fullName = ds.getFullName();
                        if (fullName!=null) {
                            dss.add(fullName);
                        } else {
                            //TODO: how to deal?
                        }
                    }

                    if (obj instanceof Map) {
                        Map<String, String> mapTypeAttr = (Map)obj;
                        for (String type : mapTypeAttr.keySet()) {
                            if (dss.contains(type)) {
                                DataSource ds = DataSource.getByFullName(type);
                                String attr = mapTypeAttr.get(type);
                                tgtTypeAttr.put(ds, attr);
                            } else {
                                error.append("Target ID type "+type+" does not exist.\n");
                            }
                        }

                    } else if (obj instanceof Set) { // only types
                        Set<String> types = (Set)obj;
                        for (String type : types) {
                            if (dss.contains(type)) {
                                DataSource ds = DataSource.getByFullName(type);
                                Set<String> usedName = new HashSet();
                                usedName.add("ID"); //TODO remove in Cy3
                                usedName.addAll(java.util.Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));
                                String attr;
                                if (usedName.contains(type)) {
                                    int num = 1;
                                    while (usedName.contains(type+"."+num)) {
                                        num ++;
                                    }
                                    attr = type+"."+num;
                                } else {
                                    attr = type;
                                }
                                tgtTypeAttr.put(ds, attr);
                            } else {
                                error.append("Target ID type "+type+" does not exist.\n");
                            }
                        }
                    }

                    if (tgtTypeAttr.isEmpty()) {
                        succ = false;
                        error.append("No target ID type.\n");
                    }
                }
            }
        }

        // mapping ids
        AttributeBasedIDMappingImpl service
                    = new AttributeBasedIDMappingImpl();
        if (succ) {
            Map<String,Set<DataSource>> mapAttrTypes = new HashMap();
            for (String attr : srcAttrs) {
                mapAttrTypes.put(attr, srcTypes);
            }

            try {
                service.map(networks, mapAttrTypes, tgtTypeAttr);
            } catch (Exception e) {
                e.printStackTrace();
                error.append(e.getMessage());
                succ = false;
            }
        }

        // send respond message
        Map responseContent = new HashMap();
        if (succ) {
            responseContent.put(SUCCESS, true);
            responseContent.put(REPORT, service.getReport());//+"\nErrors:\n"+error);
            Map<String, String> mappedTypeAttr = new HashMap();
            for (DataSource ds : tgtTypeAttr.keySet()) {
                mappedTypeAttr.put(ds.getFullName(), tgtTypeAttr.get(ds));
            }
            responseContent.put(MAP_TARGET_ID_TYPE_ATTR, mappedTypeAttr);
        } else {
            responseContent.put(SUCCESS, false);
            responseContent.put(REPORT, "Errors:\n"+error);
        }

        return createResponse(msg, responseContent);
    }

    private static ResponseMessage mappingService(Message msg) {
        Set<String> srcIDs = null;
        String srcIDType = null;
        String tgtIDType = null;

        Object obj = msg.getContent();
        if (obj!=null && obj instanceof Map) {
            Object obj1 = ((Map)obj).get(SOURCE_ID);
            if (obj1 instanceof String) {
                srcIDs = new HashSet(1);
                srcIDs.add((String)obj1);
            } else if (obj1 instanceof Set) {
                srcIDs = (Set<String>)obj1;
            }

            obj1 = ((Map)obj).get(SOURCE_ID_TYPE);
            if (obj1 instanceof String) {
                srcIDType = (String)obj1;
            }

            obj1 = ((Map)obj).get(TARGET_ID_TYPE);
            if (obj1 instanceof String) {
                tgtIDType = (String)obj1;
            }
        }

        Map content = new HashMap();
        if (srcIDs==null || srcIDType==null || tgtIDType==null) {
            content.put(SUCCESS, false);
            content.put(REPORT, "Message content must contain " +
                    "Source_ID, SOURCE_ID_TYPE and TARGET_ID_TYPE.");
        } else {
            if (!DataSource.getFullNames().contains(srcIDType)) {
                content.put(SUCCESS, false);
                content.put(REPORT, "Source type \""+srcIDType+"\" does not exist.");
            } else if (!DataSource.getFullNames().contains(tgtIDType)) {
                content.put(SUCCESS, false);
                content.put(REPORT, "Target type \""+tgtIDType+"\" does not exist.");
            } else {
                IDMapperStack stack = IDMapperClientManager.selectedIDMapperStack();
                
                IDMapperCapabilities caps = stack.getCapabilities();
                DataSource srcDs = DataSource.getByFullName(srcIDType);
                DataSource tgtDs = DataSource.getByFullName(tgtIDType);
                boolean supported = false;
                try {
                    supported = caps.isMappingSupported(srcDs, tgtDs);
                } catch (IDMapperException e) {
                    e.printStackTrace();
                    content.put(SUCCESS, false);
                    content.put(REPORT, "Error: "+e.getMessage());
                }

                if (!supported) {
                    content.put(SUCCESS, false);
                    content.put(REPORT, "Mapping from \""+srcIDType+"\" to \""+tgtIDType+"\" is not supported.");
                } else {
                    Set<Xref> srcXrefs = new HashSet(srcIDs.size());
                    for (String id : srcIDs) {
                        srcXrefs.add(new Xref(id, srcDs));
                    }

                    Set<DataSource> tgtDataSources = new HashSet(1);
                    tgtDataSources.add(tgtDs);

                    Map<Xref,Set<Xref>> mapping = null;
                    try {
                        mapping = stack.mapID(srcXrefs, tgtDataSources);
                    } catch (IDMapperException e) {
                        e.printStackTrace();
                        content.put(SUCCESS, false);
                        content.put(REPORT, "Error: "+e.getMessage());
                    }

                    if (mapping!=null) {
                        Map<String, Set<String>> result = new HashMap(mapping.size());
                        for (Xref srcXref : mapping.keySet()) {
                            Set<Xref> tgtXrefs = mapping.get(srcXref);
                            if (tgtXrefs!=null) {
                                Set<String> tgtIds = new HashSet();
                                for (Xref tgtXref : tgtXrefs) {
                                    tgtIds.add(tgtXref.getId());
                                }
                                result.put(srcXref.getId(), tgtIds);
                            }
                        }

                        content.put(SUCCESS, true);
                        content.put(MAPPING_RESULT, result);
                    }
                }
            }
        }

        return createResponse(msg, content);
    }
}

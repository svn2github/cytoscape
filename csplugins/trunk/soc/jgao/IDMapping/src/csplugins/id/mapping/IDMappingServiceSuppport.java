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

import csplugins.id.mapping.ui.CyThesaurusDialog;
import csplugins.id.mapping.ui.IDMappingSourceConfigDialog;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperStack;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperCapabilities;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import cytoscape.util.plugins.communication.PluginsCommunicationSupport;
import cytoscape.util.plugins.communication.MessageListener;
import cytoscape.util.plugins.communication.Message;
import cytoscape.util.plugins.communication.ResponseMessage;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author gjj
 */
public class IDMappingServiceSuppport {
    private static final String MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE = "REQUEST_SUPPORTED_ID_TYPE";
    private static final String MSG_TYPE_REQUEST_MAPPING = "REQUEST_MAPPING";
    private static final String MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG = "MAPPING_SRC_CONFIG_DIALOG";
    private static final String MSG_TYPE_REQUEST_MAPPING_DIALOG = "MAPPING_DIALOG";
    
    private static final String pluginName = CyThesaurusPlugin.pluginName;

    static void addService() {
        MessageListener ml = new MessageListener() {
            public void messagedReceived(Message msg) {
                String msgType = msg.getType();
                if (msgType==null) return;

                ResponseMessage response = null;
                if (msgType.compareTo(Message.MSG_TYPE_TEST)==0) {
                    response = testService(msg);
                } else if(msgType.compareTo(Message.MSG_TYPE_GET_RECEIVERS)==0) {
                    response = responseToGetReceivers(msg);
                } else if(msgType.compareTo(Message.MSG_TYPE_GET_MSG_TYPES)==0) {
                    response = responseToGetTypes(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPING)==0) {
                    response = mappingService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG)==0) {
                    response = mappingSrcConfigDialogService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_MAPPING_DIALOG)==0) {
                    response = mappingDialogService(msg);
                } else if (msgType.compareTo(MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE)==0) {
                    response = supportedIdTypeService(msg);
                }

                // send respond message
                if (response!=null) {
                    PluginsCommunicationSupport.sendMessage(response);
                }
            }
        };

        PluginsCommunicationSupport.addMessageListener(pluginName, ml);
    }

    private static final String NETWORK_ID = "NETWORK_ID";
    private static final String SOURCE_ATTR = "SOURCE_ATTR";
    private static final String SOURCE_ID_TYPE = "SOURCE_ID_TYPE";
    private static final String TARGET_ATTR = "TARGET_ATTR";
    private static final String TARGET_ID_TYPE = "TARGET_ID_TYPE";

    private static final String RESPONSE_SUCCESS = "SUCCESS";
    private static final String RESPONSE_REPORT = "REPORT";
    private static final String RESPONSE_SRC_ID_TYPE = "SRC_ID_TYPE";
    private static final String RESPONSE_TGT_ID_TYPE = "TGT_ID_TYPE";

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
        content.put(RESPONSE_SUCCESS, true);

        return createResponse(msg, content);
    }

    private static ResponseMessage responseToGetReceivers(Message msg) {
        return createResponse(msg, null);
    }

    private static ResponseMessage responseToGetTypes(Message msg) {
        Map content = new HashMap();
        Set<String> supportedTypes = new HashSet();
        supportedTypes.add(Message.MSG_TYPE_TEST);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPING);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG);
        supportedTypes.add(MSG_TYPE_REQUEST_MAPPING_DIALOG);
        supportedTypes.add(MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE);
        content.put(Message.MSG_TYPE_GET_MSG_TYPES, supportedTypes);

        return createResponse(msg, content);
    }

    private static ResponseMessage supportedIdTypeService(Message msg) {
        Map content = new HashMap();

        Set<DataSource> srcDataSources;
        Set<DataSource> tgtDataSources;
        IDMapperStack stack = IDMappingClientManager.selectedIDMapperStack();
        try {
            srcDataSources = stack.getCapabilities().getSupportedSrcDataSources();
            tgtDataSources = stack.getCapabilities().getSupportedTgtDataSources();
        } catch (IDMapperException ex) {
            ex.printStackTrace();
            content.put(RESPONSE_SUCCESS, false);
            content.put(RESPONSE_REPORT, "\nIDMapperException:\n"+ex.getMessage());
            return createResponse(msg, content);
        }

        String[] srcTypes = new String[srcDataSources.size()];
        int ids = 0;
        for(DataSource ds : srcDataSources) {
            srcTypes[ids++] = ds.getFullName();
        }
        content.put(RESPONSE_SRC_ID_TYPE, srcTypes);

        String[] tgtTypes = new String[tgtDataSources.size()];
        ids = 0;
        for(DataSource ds : tgtDataSources) {
            tgtTypes[ids++] = ds.getFullName();
        }
        content.put(RESPONSE_TGT_ID_TYPE, tgtTypes);

        content.put(RESPONSE_SUCCESS, true);

        return createResponse(msg, content);
    }

    private static ResponseMessage mappingSrcConfigDialogService(Message msg) {
        IDMappingSourceConfigDialog srcConfDialog
                = new IDMappingSourceConfigDialog(Cytoscape.getDesktop(), true);
        srcConfDialog.setVisible(true);

        Map content = new HashMap();
        content.put(RESPONSE_SUCCESS, !srcConfDialog.isCancelled());

        return createResponse(msg, content);
    }

    private static ResponseMessage mappingDialogService(Message msg) {
        final CyThesaurusDialog dialog = new CyThesaurusDialog(Cytoscape.getDesktop(), true);
        dialog.setLocationRelativeTo(Cytoscape.getDesktop());
        dialog.setMapSrcAttrIDTypes(CyThesaurusPlugin.mapSrcAttrIDTypes);
        dialog.setVisible(true);

        Map content = new HashMap();
        content.put(RESPONSE_SUCCESS, !dialog.isCancelled());

        if (!dialog.isCancelled()) {
            CyThesaurusPlugin.mapSrcAttrIDTypes = dialog.getMapSrcAttrIDTypes();
        }

        return createResponse(msg, content);
    }

    private static ResponseMessage mappingService(Message msg) {
        Object content = msg.getContent();
        boolean succ = true;
        StringBuilder error = new StringBuilder();

        Map map = (Map) content;
        Set<CyNetwork> networks = new HashSet();
        Set<String> srcAttrs = new HashSet();
        Set<DataSource> srcTypes = new HashSet();
        String tgtAttr = null;
        DataSource tgtType = null;

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
                } else if (obj instanceof String[]) {
                    String[] netIds = (String[])obj;
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
                    error.append(NETWORK_ID + " must be String or String[].\n");
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
                } else if (obj instanceof String[]) {
                    String[] attrs = (String[])obj;
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
                    error.append(SOURCE_ATTR+" must be String or String[].\n");
                }
            }

            Set<DataSource> srcDataSources = null;
            Set<DataSource> tgtDataSources = null;
            try {
                IDMapperCapabilities cap
                      = IDMappingClientManager.selectedIDMapperStack().getCapabilities();
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
                    } else if (obj instanceof String[]) {
                        String[] types = (String[])obj;
                        for (String type : types) {
                            if (dss.contains(type)) {
                                srcTypes.add(DataSource.getByFullName(type));
                            } else {
                                error.append("Source ID type "+type+" does not exist.\n");
                            }
                        }

                        if (srcAttrs.isEmpty()) {
                            succ = false;
                        }
                    } else {
                        succ = false;
                        error.append(SOURCE_ID_TYPE+" must be String or String[].\n");
                    }
                }

                //parse target id type
                obj = map.get(TARGET_ID_TYPE);
                if (obj==null || !(obj instanceof String)) {
                    succ = false;
                    error.append("Message content must contain a non-null \"" + TARGET_ID_TYPE +"\"\n");
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

                    String type = (String)obj;
                    if (dss.contains(type)) {
                        tgtType = DataSource.getByFullName(type);
                    } else {
                        succ = false;
                        error.append("Target ID type "+type+" does not exist.\n");
                    }
                }

                //parse target attribute
                obj = map.get(TARGET_ATTR);
                if (obj==null) {
                    if (succ) {
                        // set default target attribute name
                        String tgtTypeName = tgtType.getFullName();
                        if (!attributes.contains(tgtTypeName)) {
                            tgtAttr = tgtTypeName;
                        } else {
                            int i = 0;
                            while (attributes.contains(tgtTypeName+"."+(++i))){}
                            tgtAttr = tgtTypeName+"."+i;
                        }
                    }
                } else if (!(obj instanceof String)) {
                    succ = false;
                    error.append("Attribute name should be String.");
                } else {
                    String attr = (String)obj;
                    if (attr.length()==0) {
                        succ = false;
                        error.append(TARGET_ATTR+" cannot be empty.\n");
                    } else if (attr.compareTo("ID")==0) { //TODO: remove in cy3
                        succ = false;
                        error.append("Cannot save the target ids as node ID.\n");
                    } else {
                        tgtAttr = attr;
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

            Map<DataSource, String> MapTgtIDTypeAttrName = new HashMap();
            MapTgtIDTypeAttrName.put(tgtType, tgtAttr);

            try {
                service.map(networks, mapAttrTypes, MapTgtIDTypeAttrName);
            } catch (Exception e) {
                e.printStackTrace();
                error.append(e.getMessage());
                succ = false;
            }
        }

        // send respond message
        Map responseContent = new HashMap();
        if (succ) {
            responseContent.put(RESPONSE_SUCCESS, true);
            responseContent.put(RESPONSE_REPORT, service.getReport());//+"\nErrors:\n"+error);
            responseContent.put(TARGET_ATTR, tgtAttr);
        } else {
            responseContent.put(RESPONSE_SUCCESS, false);
            responseContent.put(RESPONSE_REPORT, "Errors:\n"+error);
        }

        return createResponse(msg, responseContent);
    }
}

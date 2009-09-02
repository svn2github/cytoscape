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

import cytoscape.util.plugins.communication.PluginsCommunicationSupport;
import cytoscape.util.plugins.communication.ResponseMessage;
import cytoscape.util.plugins.communication.Message;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author gjj
 */
public class CyThesaurusServiceMessageBasedClient
        implements CyThesaurusServiceClient{

    private final String CYTHESAURUS = "CyThesaurus";

    private final String MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE = "SUPPORTED_ID_TYPE";
    private final String MSG_TYPE_REQUEST_ATTRIBUTE_BASED_MAPPING = "ATTRIBUTE_BASED_MAPPING";
    private final String MSG_TYPE_REQUEST_MAPPING_SERVICE = "MAPPING_SERVICE";
    private final String MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG = "MAPPING_SRC_CONFIG_DIALOG";
    private final String MSG_TYPE_REQUEST_MAPPING_DIALOG = "MAPPING_DIALOG";
    private final String MSG_TYPE_REQUEST_MAPPERS = "ID_MAPPERS";
    private final String MSG_TYPE_REQUEST_REGISTER_MAPPER = "REGISTER_ID_MAPPER";
    private final String MSG_TYPE_REQUEST_UNREGISTER_MAPPER = "UNREGISTER_ID_MAPPER";
    private final String MSG_TYPE_REQUEST_SELECT_MAPPER = "SELECT_ID_MAPPER";;
    private final String MSG_TYPE_REQUEST_ID_EXIST = "ID_EXIST";

    private final String NETWORK_ID = "NETWORK_ID";
    private final String SOURCE_ATTR = "SOURCE_ATTR";
    private final String SOURCE_ID_TYPE = "SOURCE_ID_TYPE";
    private final String MAP_TARGET_ID_TYPE_ATTR = "MAP_TARGET_ID_TYPE_ATTR";
    private final String SELECTED = "SELECTED";
    private final String CLASS_PATH = "CLASS_PATH";
    private final String CONNECTION_STRING = "CONNECTION_STRING";
    private final String DISPLAY_NAME = "DISPLAY_NAME";
    private final String ID = "ID";
    private final String TYPE = "TYPE";
    private final String SOURCE_ID = "SOURCE_ID";

    private final String SUCCESS = "SUCCESS";
    private final String IS_CANCELLED = "IS_CANCELLED";
    private final String REPORT = "REPORT";
    private final String TARGET_ID_TYPE = "TGT_ID_TYPE";
    private final String CLIENTS = "CLIENTS";
    private final String ID_EXISTS = "ID_EXISTS";
    private final String MAPPING_RESULT = "MAPPING_RESULT";
    
    private final String requester;

    public CyThesaurusServiceMessageBasedClient() {
        this(null);
    }

    public CyThesaurusServiceMessageBasedClient(String requester) {
        if (requester==null) {
            this.requester = ""+System.currentTimeMillis();
        } else {
            this.requester = requester;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isServiceAvailable() {
        String receiver = CYTHESAURUS; // plugin name when passing messages
        String msgType = Message.MSG_TYPE_TEST; // indicate what this message requests for
        String msgId = receiver + "_" + System.currentTimeMillis();
        Message msg = new Message(msgId , requester, receiver, msgType , null);
        List<ResponseMessage> responses
                    = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean openAttributeConfigDialog() {
        String receiver = this.CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_MAPPING_DIALOG; // request for ID mapping main dialog
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Message msg = new Message(msgId , requester, receiver, msgType , null);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean openMappingResourceConfigDialog() {
        String receiver = this.CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_MAPPING_SRC_CONFIG_DIALOG; // request for ID mapping main dialog
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Message msg = new Message(msgId , requester, receiver, msgType , null);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = content.get(IS_CANCELLED);
                            if (obj instanceof Boolean) {
                                return !((Boolean) obj);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> allIDMappers() {
        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_MAPPERS; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Message msg = new Message(msgId , requester, receiver, msgType , null);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = content.get(CLIENTS);
                            if (obj instanceof Set) {
                                return (Set) obj;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> selectedIDMappers() {
        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_MAPPERS; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map map = new HashMap();
        map.put(SELECTED, true);
        Message msg = new Message(msgId , requester, receiver, msgType , map);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = content.get(CLIENTS);
                            if (obj instanceof Set) {
                                return (Set) obj;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean registerIDMapper(String connectionString, String classPath, String displayName) {
        if (connectionString==null || classPath==null) {
            throw new IllegalArgumentException();
        }

        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_REGISTER_MAPPER; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map map = new HashMap();
        map.put(CONNECTION_STRING, connectionString);
        map.put(CLASS_PATH, classPath);
        map.put(DISPLAY_NAME, displayName);
        Message msg = new Message(msgId , requester, receiver, msgType , map);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean unregisterIDMapper(String connectionString) {
        if (connectionString==null) {
            throw new IllegalArgumentException();
        }

        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_UNREGISTER_MAPPER; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map map = new HashMap();
        map.put(CONNECTION_STRING, connectionString);
        Message msg = new Message(msgId , requester, receiver, msgType , map);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean setIDMapperSelect(String connectionString, boolean selected) {
        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_SELECT_MAPPER; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map map = new HashMap();
        map.put(SELECTED, selected);
        map.put(CONNECTION_STRING, connectionString);
        Message msg = new Message(msgId , requester, receiver, msgType , map);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> supportedSrcIDTypes() {
        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE; // request for fetching supported ID types
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Message msg = new Message(msgId , requester, receiver, msgType , null);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);
        
        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = content.get(SOURCE_ID_TYPE);
                            if (obj instanceof Set) {
                                return (Set) obj;
                            }
                        } else {
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> supportedTgtIDTypes() {
        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_SUPPORTED_ID_TYPE; // request for fetching supported ID types
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Message msg = new Message(msgId , requester, receiver, msgType , null);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = content.get(TARGET_ID_TYPE);
                            if (obj instanceof Set) {
                                return (Set) obj;
                            }
                        } else {
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean mapID(Set<String> netIds, String srcAttrName,
            String tgtAttrName, Set<String> srcIDTypes, String tgtIDType) {
        String receiver = CYTHESAURUS;
        String msgType = MSG_TYPE_REQUEST_ATTRIBUTE_BASED_MAPPING; // request for ID mapping service
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map content = new HashMap();
        content.put(NETWORK_ID, netIds);
        content.put(SOURCE_ATTR, srcAttrName);
        content.put(SOURCE_ID_TYPE, srcIDTypes); // set source ID type
        Map<String, String> tgtTypeAttr = new HashMap(1);
        tgtTypeAttr.put(tgtIDType, tgtAttrName);
        content.put(MAP_TARGET_ID_TYPE_ATTR, tgtTypeAttr);

        Message msg = new Message(msgId , requester, receiver, msgType , content);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map contentRes = (Map) obj;
                    obj = contentRes.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            return true;
                        } 
                    }
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String,String> mapID(Set<String> netIds, String srcAttrName,
            Set<String> srcIDTypes, Set<String> tgtIDTypes) {
        if (srcIDTypes==null || tgtIDTypes==null) {
            throw new IllegalArgumentException();
        }

        String receiver = CYTHESAURUS;
        String msgType = MSG_TYPE_REQUEST_ATTRIBUTE_BASED_MAPPING; // request for ID mapping service
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map content = new HashMap();
        content.put(NETWORK_ID, netIds);
        content.put(SOURCE_ATTR, srcAttrName);
        content.put(SOURCE_ID_TYPE, srcIDTypes); // set source ID type
        content.put(MAP_TARGET_ID_TYPE_ATTR, tgtIDTypes);

        Message msg = new Message(msgId , requester, receiver, msgType , content);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map contentRes = (Map) obj;
                    obj = contentRes.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = contentRes.get(MAP_TARGET_ID_TYPE_ATTR);
                            if (obj instanceof Map) {
                                return (Map)obj;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Set<String>> mapID(Set<String> srcIDs, String srcIDType,
            String tgtIDType) {
        if (srcIDs==null || srcIDType==null || tgtIDType==null) {
            throw new IllegalArgumentException();
        }
        
        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_MAPPING_SERVICE; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map map = new HashMap();
        map.put(SOURCE_ID, srcIDs);
        map.put(SOURCE_ID_TYPE, srcIDType);
        map.put(TARGET_ID_TYPE, tgtIDType);
        Message msg = new Message(msgId , requester, receiver, msgType , map);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            obj = content.get(MAPPING_RESULT);
                            if (obj instanceof Map) {
                                return (Map) obj;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean idExists(String id, String type) {
        if (type==null || id==null) {
            throw new IllegalArgumentException();
        }

        String receiver = CYTHESAURUS;
        String msgType = this.MSG_TYPE_REQUEST_ID_EXIST; 
        String msgId = requester+receiver+msgType+ System.currentTimeMillis();
        Map map = new HashMap();
        map.put(SOURCE_ID, id);
        map.put(SOURCE_ID_TYPE, type);
        Message msg = new Message(msgId , requester, receiver, msgType , map);
        List<ResponseMessage> responses = PluginsCommunicationSupport.sendMessageAndGetResponses(msg);

        for (ResponseMessage response : responses) {
            if (response.getSender().compareTo(receiver)==0) {
                Object obj = response.getContent();
                if (obj instanceof Map) {
                    Map content = (Map) obj;
                    obj = content.get(SUCCESS);
                    if (obj instanceof Boolean) {
                        boolean succ = (Boolean) obj;
                        if (succ) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}

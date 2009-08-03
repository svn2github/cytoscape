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

package cytoscape.util.plugins.communication;

import java.util.Map;
import java.util.HashMap;

/**
 * Wrap the information of inter-plugin message in this class, such as message
 * ID, message sender, message receiver, message type and message content. The
 * API user can get the underlying map to add more information.
 *
 * @author gjj
 */
public class Message {
    private static final String MSG_SENDER = "MSG_SENDER";
    private static final String MSG_RECEIVER = "MSG_RECEIVER";
    private static final String MSG_TYPE = "MSG_TYPE";
    private static final String MSG_CONTENT = "MSG_CONTENT";
    private static final String MSG_ID = "MSG_ID";

    public static final String MSG_TYPE_GENERAL = "GENERAL";
    public static final String MSG_TYPE_REQUEST = "REQUEST";
    public static final String MSG_TYPE_RESPONSE = "RESPONSE";
    public static final String MSG_TYPE_TEST = "TEST";

    private final Map map;

    /**
     * Copy constructor
     * @param anotherMessage copy from
     */
    public Message(Message anotherMessage) {
        if (anotherMessage==null) {
            throw new IllegalArgumentException("Cannot copy from a null Message.");
        }
        map = new HashMap(anotherMessage.getMessageMap());
    }

    /**
     * 
     * @param sender sender plugin name
     * @param receiver receiver plugin name
     * @param content message content
     * @param msgId message identifier
     */
    public Message(final String msgId, final String sender, final String receiver,
                final String type, final Object content) {
        map = new HashMap();
        map.put(MSG_SENDER, sender);
        map.put(MSG_RECEIVER, receiver);
        map.put(MSG_CONTENT, content);
        map.put(MSG_ID, msgId);
        map.put(MSG_TYPE, type);
    }

    public String getSender() {
        return (String)map.get(MSG_SENDER);
    }

    public String getReceiver() {
        return (String)map.get(MSG_RECEIVER);
    }

    public Object getContent() {
        return map.get(MSG_CONTENT);
    }

    public String getType() {
        return (String)map.get(MSG_TYPE);
    }

    public String getId() {
        return (String)map.get(MSG_ID);
    }

    public void setId(String id) {
        map.put(MSG_ID, id);
    }

    public Map getMessageMap() {
        return map;
    }
}

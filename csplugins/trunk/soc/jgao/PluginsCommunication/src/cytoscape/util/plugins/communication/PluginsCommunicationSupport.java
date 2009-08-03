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

import cytoscape.Cytoscape;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * PluginsCommunicationSupport provide inter-plugin communication for Cytoscape
 * plugins. Plugin developer should include this package into their plugin to
 * communicate with other plugins.
 * <p>
 * Here follows some emample code for sending a message from one plugin (sender)
 * to another (receiver).
 * <p>
 * <h3> Code for sender: </h3>
 * The sender sents a message by the method <i>sendMessage</i>:
 * <pre>
 * String sender = "SENDER";
 * String receiver = "RECEIVER";
 * String id = sender + System.currentTimeMillis();
 * String type = Message.MSG_TYPE_TEST;
 * Map content = new HashMap(); // A Map of content is suggested here, but any
 *                              // kind of objects can be used here
 * // fill the message content here ...
 * PluginsCommunicationSupport.sendMessage(id, sender, receiver, type, content);
 * </pre>
 * <p>
 * Note: the message will be sent to the plugin who claims to be the receiver,
 * if recever is null, this message will be send to all plugins listening to
 * inter-plugin messages.
 * <p>
 * <h3> Code for receiver: </h3>
 * The receiver listens to the messages by adding <i>MessageListener</i>s:
 * <pre>
 * MessageListener ml = new MessageListener() {
 *     public void messagedReceived(Message msg) {
 *         String msgType = msg.getType();
 *         if (msgType==null) return;
 *         if (msgType.compareTo(Message.MSG_TYPE_TEST)==0) {
 *             // process the message here
 *         }
 *         String sender = msg.getSender();
 *         if (sender!=null) {
 *             // send respond message
 *             Map response = new HashMap();
 *             response.put("SUCCESS", true); //... and any other content as
 *                                            //long as the other plugin can
 *                                            // understand
 *             PluginsCommunicationSupport.sendMessage(null, receiver, sender,
 *                       Message.MSG_TYPE_RESPONSE, response);
 *         }
 *     }
 * }
 *
 * PluginsCommunicationSupport.addMessageListener(receiver, ml);
 * </pre>
 * <p>
 * Note: only messages sent to the receiver will be listened, but if receiver is
 * is null, all of inter-plugin messages will be listened.
 * <p>
 * <h3>Send message and get response</h3>
 * You can also send the message and get the corresponding responses, using the
 * {@link PluginsCommunicationSupport#sendMessageAndGetResponses} method.
 *
 * @author gjj
 */
public final class PluginsCommunicationSupport {
    private static final String INTER_PLUGIN_COMMUNICATION = "INTER_PLUGIN_COMMUNICATION";
    private static final Map<MessageListener, PropertyChangeListener> listenerMap = new HashMap();

    private PluginsCommunicationSupport() {}
    
    public static void sendMessage(final String msgId, final String sender,
            final String receiver, final String msgType) {
        sendMessage(msgId, sender, receiver, msgType, null);
    }

    /**
     * send a message between plugins
     * @param sender sender plugin name
     * @param receiver receiver plugin name
     * @param content message content
     */
    public static void sendMessage(final String msgId, final String sender,
            final String receiver, final String msgType, final Object content) {
        sendMessage(new Message(msgId, sender, receiver, msgType, content));
    }
    
    /**
     * send a message between plugins
     * @param sender sender plugin name
     * @param receiver receiver plugin name
     * @param content message content
     */
    public static void sendMessage(final Message msg) {
        if (msg==null) {
            throw new java.lang.IllegalArgumentException("Message cannot be null.");
        }

        PropertyChangeSupport pcs = Cytoscape.getPropertyChangeSupport();
        pcs.firePropertyChange(INTER_PLUGIN_COMMUNICATION, null, msg);
    }

    /**
     * Send the message and get the responses
     * @param msg message to be sent
     * @return responses
     */
    public static List<ResponseMessage> sendMessageAndGetResponses(final Message msg) {
        if (msg==null) {
            throw new java.lang.IllegalArgumentException("Message cannot be null.");
        }

        final Message msgCopy = new Message(msg);

        final List<ResponseMessage> rms = new ArrayList(1);

        final String sender = msgCopy.getSender();
        if (sender==null) {
            // message without a send won't receive any response
            sendMessage(msgCopy);
            return rms;
        } else {
            final String msgId;
            if (msgCopy.getId()==null) {
                msgId = sender + System.currentTimeMillis();
                msgCopy.setId(msgId);
            } else {
                msgId = msgCopy.getId();
            }

            // add listener to listen to the response
            MessageListener ml = new MessageListener() {
                public void messagedReceived(Message msg) {
                    if (!(msg instanceof ResponseMessage)) return;
                    ResponseMessage response = (ResponseMessage)msg;

                    if (response.getReceiver().compareTo(sender)!=0) return;
                    if (response.getRespondToId().compareTo(msgId)!=0) return;

                    rms.add(response);
                }
            };

            addMessageListener(sender, ml);

            // send message
            sendMessage(msgCopy);

            //remove message listener
            removeMessageListener(ml);

            // return response
            return rms;
        }

    }

    /**
     * Add a {@link MessageListener} to listen to all inter-plugin messages
     * @param listener {@link MessageListener}
     */
    public static void addMessageListener(final MessageListener listener) {
        addMessageListener(null, listener);
    }

    /**
     * Add the {@link MessageListener} to listen to inter-plugin messages send
     * to receiver.
     * @param receiver message receiver
     * @param listener {@link MessageListener}
     */
    public static void addMessageListener(final String receiver,
                final MessageListener listener) {
        if (listener==null) {
            throw new java.lang.IllegalArgumentException("listener cannot be null.");
        }

        PropertyChangeSupport pcs = Cytoscape.getPropertyChangeSupport();
        pcs.addPropertyChangeListener(INTER_PLUGIN_COMMUNICATION,
                    new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Object obj = evt.getNewValue();
                if (obj==null) return;
                if (!(obj instanceof Message)) return;

                Message msg = (Message)obj;
                String msgReceiver = msg.getReceiver();

                if (receiver!=null && msgReceiver!=null
                        && msgReceiver.compareTo(receiver)!=0) return;

                listener.messagedReceived(msg);

                listenerMap.put(listener, this);
            }
        });
    }

    /**
     * Remove a {@link MessageListener}
     * @param listener {@link MessageListener}
     */
    public static void removeMessageListener(final MessageListener listener) {
        PropertyChangeSupport pcs = Cytoscape.getPropertyChangeSupport();
        PropertyChangeListener pcl = listenerMap.get(listener);
        if (pcl!=null) {
            pcs.removePropertyChangeListener(INTER_PLUGIN_COMMUNICATION, pcl);
        }
    }
}

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

/**
 * ResponseMessage is used to carries informations for reponses
 *
 * @author gjj
 */
public class ResponseMessage extends Message {
    private static final String ID_OF_RESPOND_TO_MSG = "RESPOND_TO";

    /**
     *
     * @param responseId response message ID
     * @param respondToId the ID of the message to be responded
     * @param responder sender
     * @param respondee receiver
     * @param content message content
     */
    public ResponseMessage(final String responseId, final String respondToId,
                final String responder, final String respondee, final Object content) {
        super(responseId, assertNullString(responder), respondee,
                    Message.MSG_TYPE_RESPONSE, content);

        assertNullString(respondToId);

        getMessageMap().put(ID_OF_RESPOND_TO_MSG, respondToId);
    }

    private static String assertNullString(final String str) {
        if (str==null) {
            throw new IllegalArgumentException("Null String argument");
        }

        return str;
    }

    /**
     *
     * @return the ID of the message to be responded
     */
    public String getRespondToId() {
        return (String)getMessageMap().get(ID_OF_RESPOND_TO_MSG);
    }
}

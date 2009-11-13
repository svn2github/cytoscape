/* File: DefaultAttributeConflictHandler.java

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

package csplugins.network.merge.conflict;

import cytoscape.data.CyAttributes;

import java.util.Set;
import java.util.TreeSet;
import java.util.Map;

/**
 *
 * 
 */
public class DefaultAttributeConflictHandler implements AttributeConflictHandler {

        /**
         * Handle attribute conflict when merging (copying from one attr to another)
         *
         * @param conflict
         *      attribute conflict
         * @return
         *      true if successful, false if failed
         */
        public boolean handleIt(final String toID,
                                final String toAttr,
                                final Map<String,String> mapFromIDFromAttr,
                                final CyAttributes attrs) {
                //TODO: write a reasonable default one
                if (toID==null || toAttr==null || mapFromIDFromAttr==null || attrs==null) {
                        throw new java.lang.NullPointerException();
                }

                byte type = attrs.getType(toAttr);
                
                if (type == CyAttributes.TYPE_STRING) {
                        final String toValue = attrs.getStringAttribute(toID, toAttr);
                        Set<String> values = new TreeSet<String>();
                        values.add(toValue);

                        for (Map.Entry<String,String> entry : mapFromIDFromAttr.entrySet()) {
                                String fromID = entry.getKey();
                                String fromAttr = entry.getValue();
                                Object fromValue = attrs.getAttribute(fromID, fromAttr);
                                if (fromValue!=null) {
                                        values.add(fromValue.toString());
                                }
                        }
                        
                        StringBuilder str = new StringBuilder();
                        for (String v : values) {
                                str.append(v+";");
                        }
                        
                        str.deleteCharAt(str.length()-1);
                        
                        attrs.setAttribute(toID, toAttr, str.toString());

                        return true;
                }

                // how about Integer, Double, Boolean?

                return false;
        }
}

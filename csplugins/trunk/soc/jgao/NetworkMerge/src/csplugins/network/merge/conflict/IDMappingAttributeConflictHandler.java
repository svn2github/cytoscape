/* File: IDMappingAttributeConflictHandler.java

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

import csplugins.id.mapping.model.AttributeBasedIDMappingData;
import csplugins.id.mapping.util.IDMappingDataUtils;

import cytoscape.data.CyAttributes;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;


/**
 *
 * 
 */
public class IDMappingAttributeConflictHandler implements AttributeConflictHandler {
        AttributeBasedIDMappingData idMapping;

        public IDMappingAttributeConflictHandler(AttributeBasedIDMappingData idMapping) {
                this.idMapping = idMapping;
        }

        /**
         * Handle attribute conflict when merging (copying from one attr to another)
         *
         * @param conflict
         *      attribute conflict
         * @return
         *      true if successful, false if failed
         */
        @Override
        public boolean handleIt(final AttributeConflict conflict) {
                //idMapping.getMapTgtTypeIDs(toID, toAttrName)
                final CyAttributes attrs = conflict.getCyAttributes();
                final String fromID = conflict.getFromID();
                final String fromAttr = conflict.getFromAttr();
                final String toID = conflict.getToID();
                final String toAttr = conflict.getToAttr();

                //final Object fromValue = attrs.getAttribute(fromID, fromAttr);
                final Object toValue = attrs.getAttribute(toID, toAttr);

                if (!(toValue instanceof String)) { //TODO: deal with other types
                        return false;
                }

                Map<String,String> mapGOAttr = new HashMap<String,String>();
                mapGOAttr.put(fromID, fromAttr);
                mapGOAttr.put(toID, toAttr);
                Map<String,Set<String>> overlappedMapTypeIDs = IDMappingDataUtils.getOverlappingIDMapping(idMapping, mapGOAttr);

                if (overlappedMapTypeIDs==null||overlappedMapTypeIDs.isEmpty()) {
                        return false;
                }

                //if the src type in, pick it, otherwise pick the first type
                //TODO : realize a type priority
                String srcType = idMapping.getSrcIDType(toID, toAttr);
                Set<String> values = overlappedMapTypeIDs.get(srcType);
                if (values==null) {
                        values = overlappedMapTypeIDs.values().iterator().next();
                }
                String value = values.iterator().next();
                attrs.setAttribute(toID, toAttr, value);
                return true;
        }
}

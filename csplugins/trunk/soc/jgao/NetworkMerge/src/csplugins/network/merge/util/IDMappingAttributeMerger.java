/* File: IDMappingAttributeMerger.java

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

package csplugins.network.merge.util;

import csplugins.network.merge.conflict.AttributeConflictCollector;
import csplugins.network.merge.conflict.AttributeConflict;
import csplugins.network.merge.conflict.AttributeConflictImpl;

import csplugins.id.mapping.model.AttributeBasedIDMappingData;
import csplugins.id.mapping.util.IDMappingDataUtils;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;

/**
 *
 * 
 */
public class IDMappingAttributeMerger extends DefaultAttributeMerger {

        protected final AttributeBasedIDMappingData idMapping;

        public IDMappingAttributeMerger(final AttributeConflictCollector conflictCollector,
                final AttributeBasedIDMappingData idMapping) {
                super(conflictCollector);
                this.idMapping = idMapping;
        }

        /**
         * Merge one attribute into another
         * @param fromIDs
         * @param fromAttrName
         * @param toID
         * @param toAttrName
         * @param attrs
         * @param conflictCollector
         */
        @Override
        public void mergeAttribute(final Map<String,String> mapGOAttr,
                                     final String toID,
                                     final String toAttrName,
                                     final CyAttributes attrs) {
                Map<String,Set<String>> mapTypeIDs = IDMappingDataUtils.getOverlappingIDMapping(idMapping, mapGOAttr);
                if (mapTypeIDs==null||mapTypeIDs.isEmpty()) {
                        super.mergeAttribute(mapGOAttr, toID, toAttrName, attrs);
                        return;
                }
//
//                final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
//
//                Iterator<Map.Entry<String,String>> itEntryGOAttr = mapGOAttr.entrySet().iterator();
//                while (itEntryGOAttr.hasNext()) {
//                        Map.Entry<String,String> entryGOAttr = itEntryGOAttr.next();
//                        String fromID = entryGOAttr.getKey();
//                        String fromAttrName = entryGOAttr.getValue();
//
//                        if (!attrNames.contains(fromAttrName)||!attrNames.contains(toAttrName)) { // toAttrName must be defined before calling this method
//                            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' or '"+toAttrName+"' not exists");
//                        }
//
//                        if (!AttributeValueCastUtils.isAttributeTypeConvertable(fromAttrName,toAttrName,attrs)) {
//                            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' cannot be converted to '"+toAttrName+"'");
//                        }
//
//                        if (toID.compareTo(fromID)==0 && toAttrName.compareTo(fromAttrName)==0) {
//                            //TODO: if local attribute is realized, process here
//                            return;
//                        }
//
//                        if (!attrs.hasAttribute(fromID, fromAttrName)) {
//                            return;
//                        }
//                }

                byte type2 = attrs.getType(toAttrName);
                if (type2 == CyAttributes.TYPE_STRING) {
                        //TODO how to select?
                        String srcType = idMapping.getSrcIDType(toID, toAttrName);
                        Set<String> ids = mapTypeIDs.get(srcType);
                        if (ids==null) {
                                ids = mapTypeIDs.values().iterator().next(); //pick one
                        }
                        
                        String value = ids.iterator().next(); //pick one

                        String value_ori = attrs.getStringAttribute(toID, toAttrName);
                        if (value_ori!=null && value_ori.compareTo(value)==0) {
                                return; // no need to change
                        }

                        attrs.setAttribute(toID, toAttrName, value);
                } else if (type2==CyAttributes.TYPE_SIMPLE_LIST) {
                        //TODO how to select?
                        String srcType = idMapping.getSrcIDType(toID, toAttrName);
                        Set<String> ids = mapTypeIDs.get(srcType);
                        if (ids==null) {
                                ids = mapTypeIDs.values().iterator().next(); //pick one
                        }

                        attrs.setListAttribute(toID, toAttrName, new Vector(ids));
                } else {
                        super.mergeAttribute(mapGOAttr, toID, toAttrName, attrs);
                }

        }


}

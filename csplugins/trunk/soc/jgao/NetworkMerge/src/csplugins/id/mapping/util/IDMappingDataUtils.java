/* File: IDMappingDataUtils.java

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

package csplugins.id.mapping.util;

import csplugins.id.mapping.IDMapper;
import csplugins.id.mapping.model.AttributeBasedIDMappingData;

import cytoscape.data.CyAttributes;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;

/**
 *
 * 
 */
public class IDMappingDataUtils {
        /**
         * Get ID mapping from IDMapper for an attribute of all of nodes/edges in a network
         *
         * @param idMappingByGO
         *      Data structure to store ID mappings
         * @param idMapper
         *      ID Mapper
         * @param goIDs
         *      identifiers of nodes/edges
         * @param cyAttributes
         *      CyAttributes
         * @param attrName
         *      attribute name
         * @param potentialSrcTypes
         *      potential source ID types
         * @param tgtTypes
         *      target ID types
         *
         * @throws NullPointerException if one or more parameters is null
         * @throws IllegalArgumentException if network does not exist or one or more ids in potentialSrcTypes or tgtTypes are not supported by the idMapper
         */
        public static void addAttributeBasedIDMappingFromIDMapper(
                                             AttributeBasedIDMappingData idMapping,
                                             final IDMapper idMapper,
                                             final Set<String> goIDs,
                                             final CyAttributes cyAttributes,
                                             final String attrName,
                                             final Set<String> potentialSrcTypes,
                                             final Set<String> tgtTypes) {
                if (idMapping==null||idMapper==null || goIDs==null || attrName==null || potentialSrcTypes==null || tgtTypes==null) {
                        throw new java.lang.NullPointerException();
                }

                if (!idMapper.getSupportedSrcIDTypes().containsAll(potentialSrcTypes)
                        || !idMapper.getSupportedTgtIDTypes().containsAll(tgtTypes)) {
                        throw new java.lang.IllegalArgumentException("one or more ids in potentialSrcTypes or tgtTypes are not supported by the idMapper");
                }

                byte type = cyAttributes.getType(attrName);
                if (type<=0 && type!=cyAttributes.TYPE_SIMPLE_LIST) { // only simple type and simple list are supported
                        return;
                }

                // first decide the source type of all nodes/edges
                Map<String,Map<String,Set<String>>> mapSrcTypeSrcIDGOIDs = new HashMap<String,Map<String,Set<String>>>();

                Map<String,String> mapGOIDSrcType = new HashMap<String,String>();// recode for each go, replace this by ambiguity collecter?

                Iterator<String> itGO = goIDs.iterator();
                while (itGO.hasNext()) {
                        String idGO = itGO.next();                        

                        // get all values for the current object
                        Set<String> srcIDs = new HashSet();
                        if (type>0) { // simple type
                                Object value = cyAttributes.getAttribute(idGO, attrName);
                                if (value==null) {
                                        continue;
                                }

                                srcIDs.add(value.toString());
                        } else { // simple list
                                List values = cyAttributes.getListAttribute(idGO, attrName);
                                for(Object value : values) {
                                        if (value==null) {
                                                continue;
                                        }

                                        srcIDs.add(value.toString());
                                }
                        }

                        for (String srcID : srcIDs) {
                                if (srcID.length()==0) {
                                        continue;
                                }

                                Iterator<String> itSrcType = potentialSrcTypes.iterator();
                                while (itSrcType.hasNext()) {
                                        String srcType = itSrcType.next();
                                        //if (idMapper.idExistsInSrcIDType(srcID, srcType)) {
                                                //TODO if idGO found in several types, it's ambiguous
                                                // for now, just take the first one
                                                //use some ambiguity collector here
                                                String srcTypeIn = mapGOIDSrcType.get(idGO);
                                                if (srcTypeIn!=null&&srcTypeIn.compareTo(srcType)!=0) { //ambigouis
                                                        continue;
                                                }

                                                Map<String,Set<String>> mapSrcIDGOIDs = mapSrcTypeSrcIDGOIDs.get(srcType);
                                                if (mapSrcIDGOIDs == null) {
                                                        mapSrcIDGOIDs = new HashMap<String,Set<String>>();
                                                        mapSrcTypeSrcIDGOIDs.put(srcType, mapSrcIDGOIDs);
                                                }

                                                Set<String> idsGO = mapSrcIDGOIDs.get(srcID);
                                                if (idsGO==null) {
                                                        idsGO = new HashSet<String>();
                                                        mapSrcIDGOIDs.put(srcID, idsGO);
                                                }

                                                idsGO.add(idGO);

                                                mapGOIDSrcType.put(idGO, srcID);

                                                break;
                                        //}
                                }
                        }
                }

                // add ID mapping
                Iterator<Map.Entry<String,Map<String,Set<String>>>> itEntrySrcTypeSrcIDGOIDs = mapSrcTypeSrcIDGOIDs.entrySet().iterator();
                while (itEntrySrcTypeSrcIDGOIDs.hasNext()) {
                        Map.Entry<String,Map<String,Set<String>>> entrySrcTypeSrcIDGOIDs = itEntrySrcTypeSrcIDGOIDs.next();
                        String srcType = entrySrcTypeSrcIDGOIDs.getKey();
                        Map<String,Set<String>> mapSrcIDGOIDs = entrySrcTypeSrcIDGOIDs.getValue();
                        Set<String> srcIDs = mapSrcIDGOIDs.keySet();

                        Iterator<String> itTgtType = tgtTypes.iterator();
                        while (itTgtType.hasNext()) { // for each target
                                String tgtType = itTgtType.next();
                                Map<String,Set<String>> mapSrcIDTgtIDs = idMapper.mapID(srcIDs, srcType, tgtType);

                                Iterator<Map.Entry<String,Set<String>>> itEntrySrcIDTgtIDs = mapSrcIDTgtIDs.entrySet().iterator();
                                while (itEntrySrcIDTgtIDs.hasNext()) {
                                        Map.Entry<String,Set<String>> entrySrcIDTgtIDs = itEntrySrcIDTgtIDs.next();
                                        String srcID = entrySrcIDTgtIDs.getKey();
                                        Set<String> tgtIDs = entrySrcIDTgtIDs.getValue();

                                        Set<String> idsGO = mapSrcIDGOIDs.get(srcID);

                                        Iterator<String> itIDsGO = idsGO.iterator();
                                        while (itIDsGO.hasNext()) {
                                                String idGO = itIDsGO.next();
                                                //idMapping.addTgtIDs(idGO, attrName, tgt, tgtIDs);
                                                idMapping.addIDMapping(idGO, attrName, srcType, srcID, tgtType, tgtIDs);
                                        }
                                }
                        }
                }

        }

        /**
         * Get overlapping ID mapping for an entry (node/edge, attr)
         * @param idMapping
         * @param idGO1
         * @param attr1
         * @param idGO2
         * @param attr2
         * @return map from target id type to target ids
         */
        public static Map<String,Set<String>> getOverlappingIDMapping(
                                             AttributeBasedIDMappingData idMapping,
                                             Map<String,String> mapGOAttr) {
                if (idMapping==null || mapGOAttr==null) {
                        throw new java.lang.NullPointerException();
                }

                if (mapGOAttr.isEmpty()) {
                        return null;
                }

                Iterator<Map.Entry<String,String>> itEntryGOAttr = mapGOAttr.entrySet().iterator();
                Map.Entry<String,String> entryGOAttr = itEntryGOAttr.next();
                String idGO = entryGOAttr.getKey();
                String attr = entryGOAttr.getValue();

                Map<String,Set<String>> mapTypeIDs = idMapping.getMapTgtTypeIDs(idGO, attr);
                if (mapTypeIDs==null||mapTypeIDs.isEmpty()) {
                        return null;
                }

                //deep copy of the first
                Map<String,Set<String>> overlappedMapTypeIDs = new TreeMap<String,Set<String>>();
                Iterator<Map.Entry<String,Set<String>>> itEntryTypeIDs = mapTypeIDs.entrySet().iterator();
                while (itEntryTypeIDs.hasNext()) {
                        Map.Entry<String,Set<String>> entryTypeIDs = itEntryTypeIDs.next();
                        String type = entryTypeIDs.getKey();
                        Set<String> ids = entryTypeIDs.getValue();
                        overlappedMapTypeIDs.put(type, new HashSet(ids));
                }

                // for rest
                while (itEntryGOAttr.hasNext()) {
                        entryGOAttr = itEntryGOAttr.next();
                        idGO = entryGOAttr.getKey();
                        attr = entryGOAttr.getValue();
                        mapTypeIDs = idMapping.getMapTgtTypeIDs(idGO, attr);
                        if (mapTypeIDs==null||mapTypeIDs.isEmpty()) {
                                return null;
                        }
                        
                        Set<String> overlappedTypes = new HashSet<String>(overlappedMapTypeIDs.keySet());
                        for (String type : overlappedTypes) {
                                //type
                                Set<String> ids = mapTypeIDs.get(type);
                                if (ids==null||ids.isEmpty()) {
                                        overlappedMapTypeIDs.remove(type);
                                        continue;
                                }

                                //ids
                                Set<String> overlappedIDs = overlappedMapTypeIDs.get(type);
                                overlappedIDs.retainAll(ids);
                                if (overlappedIDs.isEmpty()) {
                                        overlappedMapTypeIDs.remove(type);
                                }
                        }

                        if (overlappedMapTypeIDs.isEmpty()) {
                                return null;
                        }
                }

                if (overlappedMapTypeIDs.isEmpty()) {
                                return null;
                }

                return overlappedMapTypeIDs;
        }
}

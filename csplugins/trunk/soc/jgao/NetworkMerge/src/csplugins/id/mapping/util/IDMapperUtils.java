/* File: IDMapperUtils.java

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
import csplugins.id.mapping.model.AttributeBasedIDMappingModel;

import cytoscape.data.CyAttributes;


import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * 
 */
public class IDMapperUtils {
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
        public static void addIDMappingFromIDMapper(AttributeBasedIDMappingModel idMappingByGO,
                                             final IDMapper idMapper,
                                             final Set<String> goIDs,
                                             final CyAttributes cyAttributes,
                                             final String attrName,
                                             final Set<String> potentialSrcTypes,
                                             final Set<String> tgtTypes) {
                if (idMappingByGO==null||idMapper==null || goIDs==null || attrName==null || potentialSrcTypes==null || tgtTypes==null) {
                        throw new java.lang.NullPointerException();
                }

                if (!idMapper.getSupportedSrcIDTypes().containsAll(potentialSrcTypes)
                        || !idMapper.getSupportedTgtIDTypes().containsAll(tgtTypes)) {
                        throw new java.lang.IllegalArgumentException("one or more ids in potentialSrcTypes or tgtTypes are not supported by the idMapper");
                }

                // first decide the source type of all nodes/edges
                Map<String,Map<String,Set<String>>> mapSrcSrcIDGOIDs = new HashMap<String,Map<String,Set<String>>>();

                Iterator<String> itGO = goIDs.iterator();
                while (itGO.hasNext()) {
                        String idGO = itGO.next();
                        String srcID = cyAttributes.getAttribute(idGO, attrName).toString();
                        if (srcID==null || srcID.length()==0) {
                                continue;
                        }

                        Iterator<String> itSrc = potentialSrcTypes.iterator();
                        while (itSrc.hasNext()) {
                                String src = itSrc.next();
                                if (idMapper.idExistsInSrcIDType(srcID, src)) {
                                        // TODO if idGO found in several types, it's ambiguous
                                        // for now, just take the first one
                                        idMappingByGO.setSrcIDType(idGO, attrName, src);

                                        Map<String,Set<String>> mapSrcIDGOIDs = mapSrcSrcIDGOIDs.get(src);
                                        if (mapSrcIDGOIDs == null) {
                                                mapSrcIDGOIDs = new HashMap<String,Set<String>>();
                                                mapSrcSrcIDGOIDs.put(src, mapSrcIDGOIDs);
                                        }

                                        Set<String> idsGO = mapSrcIDGOIDs.get(srcID);
                                        if (idsGO==null) {
                                                idsGO = new HashSet<String>();
                                                mapSrcIDGOIDs.put(srcID, idsGO);
                                        }

                                        idsGO.add(idGO);

                                        break;
                                }
                        }
                }

                // add ID mapping
                Iterator<Map.Entry<String,Map<String,Set<String>>>> itEntrySrcSrcIDGOIDs = mapSrcSrcIDGOIDs.entrySet().iterator();
                while (itEntrySrcSrcIDGOIDs.hasNext()) {
                        Map.Entry<String,Map<String,Set<String>>> entrySrcSrcIDGOIDs = itEntrySrcSrcIDGOIDs.next();
                        String src = entrySrcSrcIDGOIDs.getKey();
                        Map<String,Set<String>> mapSrcIDGOIDs = entrySrcSrcIDGOIDs.getValue();
                        Set<String> srcIDs = mapSrcIDGOIDs.keySet();

                        Iterator<String> itTgt = tgtTypes.iterator();
                        while (itTgt.hasNext()) {
                                String tgt = itTgt.next();
                                Map<String,Set<String>> mapSrcIDTgtIDs = idMapper.mapID(srcIDs, src, tgt);

                                Iterator<Map.Entry<String,Set<String>>> itEntrySrcIDTgtIDs = mapSrcIDTgtIDs.entrySet().iterator();
                                while (itEntrySrcIDTgtIDs.hasNext()) {
                                        Map.Entry<String,Set<String>> entrySrcIDTgtIDs = itEntrySrcIDTgtIDs.next();
                                        String srcID = entrySrcIDTgtIDs.getKey();
                                        Set<String> tgtIDs = mapSrcIDTgtIDs.get(srcID);

                                        Set<String> idsGO = entrySrcIDTgtIDs.getValue();

                                        Iterator<String> itIDsGO = idsGO.iterator();
                                        while (itIDsGO.hasNext()) {
                                                String idGO = itIDsGO.next();
                                                idMappingByGO.addTgtIDs(idGO, attrName, tgt, tgtIDs);
                                        }
                                }
                        }
                }

        }
}

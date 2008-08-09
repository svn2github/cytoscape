/* File: AttributeBasedIDMappingDataImpl.java

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

package csplugins.id.mapping.model;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * To store ID mapping for attributes of nodes/edges
 * 
 */
public class AttributeBasedIDMappingDataImpl implements AttributeBasedIDMappingData {
        private final Map<String, Map<String,String>> mapGOAttrSrcType; // Node/edge id -> attribute name -> souce ID type
        //private final Map<String, Map<String,String>> mapGOAttrSrcID; // Node/edge id -> attribute name -> souce ID
        private final Map<String,Map<String, Map<String, Set<String>>>> mapGoAttrTgtTypeTgtIDs; // Node/edge id -> attribute name -> target ID type -> target IDs
        private final Set<String> idTypes;

        public AttributeBasedIDMappingDataImpl() {
                mapGOAttrSrcType = new HashMap<String, Map<String,String>>();
                //mapGOAttrSrcID = new HashMap<String, Map<String,String>>();
                mapGoAttrTgtTypeTgtIDs = new HashMap<String,Map<String, Map<String, Set<String>>>>();
                idTypes = new HashSet<String>();
        }

        @Override
        public boolean isEmpty() {
                return mapGOAttrSrcType.isEmpty();
        }

        /**
         *
         * @return id type set of all data
         */
        @Override
        public Set<String> getIDTypes() {
                return idTypes;
        }

        /**
         * remove all data
         */
        @Override
        public void clear() {
                mapGOAttrSrcType.clear();
                //mapGOAttrSrcID.clear();
                mapGoAttrTgtTypeTgtIDs.clear();
                idTypes.clear();
        }

        /**
         *
         * @return map of nodes/edges to attributes
         */
        @Override
        public Map<String,Set<String>> getMapGOAttrs() {
                Map<String,Set<String>> mapGOAttrs = new HashMap<String,Set<String>>();

                Iterator<Map.Entry<String,Map<String,String>>> itEntryGOAttrSrc = mapGOAttrSrcType.entrySet().iterator();
                while (itEntryGOAttrSrc.hasNext()) {
                        Map.Entry<String,Map<String,String>> entryGOAttrSrc = itEntryGOAttrSrc.next();
                        mapGOAttrs.put(entryGOAttrSrc.getKey(), entryGOAttrSrc.getValue().keySet());
                }

                return mapGOAttrs;
        }

        /**
         * Get source ID type attribute attrName of node/edge id
         *
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         *
         * @return
         *      source ID type if exist, null otherwise
         *
         * @throws NullPointerException if id or attrName is null
         */
        @Override
        public String getSrcIDType(final String id, final String attrName) {
                if (id==null || attrName==null) {
                        throw new java.lang.NullPointerException();
                }

                final Map<String,String> mapAttrSrc = mapGOAttrSrcType.get(id);
                if (mapAttrSrc==null) {
                        return null;
                }

                final String src = mapAttrSrc.get(attrName);
                return src;
        }

        /**
         * Get source ID attribute attrName of node/edge id
         *
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         *
         * @return
         *      source ID set if exist, null otherwise
         *
         * @throws NullPointerException if id or attrName is null
         */
        @Override
        public Set<String> getSrcIDs(final String id, final String attrName) {
                if (id==null || attrName==null) {
                        throw new java.lang.NullPointerException();
                }

                String type = getSrcIDType(id,attrName);
                if (type==null) {
                        return null;
                }

                return getTgtIDs(id, attrName, type);

//                if (ids==null) {
//                        throw new java.lang.IllegalStateException();
//                }

        }

        /**
         * Get target IDs
         *
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         * @param tgtType
         *      target ID type
         *
         * @return
         *      Set of target IDs if exist, null otherwise
         *
         * @throws NullPointerException if id or attrName or tgtType is null
         */
        @Override
        public Set<String> getTgtIDs(final String id, String attrName, final String tgtType) {
                if (id==null || attrName==null || tgtType==null) {
                        throw new java.lang.NullPointerException();
                }

                final Map<String, Map<String, Set<String>>> mapAttrTgtTypeTgtIDs = mapGoAttrTgtTypeTgtIDs.get(id);
                if (mapAttrTgtTypeTgtIDs==null) {
                        return null;
                }

                final Map<String, Set<String>> mapTgtTypeTgtIDs = mapAttrTgtTypeTgtIDs.get(attrName);
                if (mapTgtTypeTgtIDs==null) {
                        return null;
                }

                final Set<String> ids = mapTgtTypeTgtIDs.get(tgtType);
                return ids;
        }

        /**
         * Get target map from tartget ID types to IDs
         *
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         *
         * @return
         *      Map from target ID type to set of target IDs if exist, null otherwise
         *
         * @throws NullPointerException if id or attrName or tgtType is null
         */
        @Override
        public Map<String,Set<String>> getMapTgtTypeIDs(String id, String attrName) {
                if (id==null || attrName==null) {
                        throw new java.lang.NullPointerException();
                }

                final Map<String, Map<String, Set<String>>> mapAttrTgtTypeTgtIDs = mapGoAttrTgtTypeTgtIDs.get(id);
                if (mapAttrTgtTypeTgtIDs==null) {
                        return null;
                }

                final Map<String, Set<String>> mapTgtTypeTgtIDs = mapAttrTgtTypeTgtIDs.get(attrName);
                return mapTgtTypeTgtIDs;
        }

        /**
         * Add id mapping
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         * @param tgtType
         *      target ID type
         *
         * @param tgtIDs
         *      Set of target IDs
         */
        @Override
        public void addIDMapping(final String idGO,
                                 final String attrName,
                                 final String srcIDType,
                                 final String srcID,
                                 final String tgtIDType,
                                 final Set<String> tgtIDs) {
                if (idGO==null
                        || attrName==null
                        || srcIDType==null
                        || srcID==null
                        || tgtIDType==null
                        || tgtIDs==null) {
                        throw new java.lang.NullPointerException();
                }

                this.setSrcIDType(idGO, attrName, srcIDType);

                if (this.getTgtIDs(idGO, attrName, srcIDType)==null) { //add source type, source id
                        Set<String> srcIDs = new HashSet<String>();
                        srcIDs.add(srcID);
                        this.addTgtIDs(idGO, attrName, srcIDType, srcIDs);
                }

                this.addTgtIDs(idGO, attrName, tgtIDType, tgtIDs);
        }

        /**
         * Set source ID type
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         * @param idType
         *      source ID type
         *
         * @throws NullPointerException if id or attrName or idType is null
         */
        protected void setSrcIDType(String id, String attrName, String idType) {
                if (id==null || attrName==null || idType==null) {
                        throw new java.lang.NullPointerException();
                }

                // put type
                Map<String,String> mapAttrSrcType = mapGOAttrSrcType.get(id);
                if (mapAttrSrcType==null) {
                        mapAttrSrcType = new HashMap<String,String>();
                        mapGOAttrSrcType.put(id, mapAttrSrcType);
                }

                mapAttrSrcType.put(attrName, idType);
                idTypes.add(idType);
        }

        /**
         * Add target IDs
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         * @param tgtType
         *      target ID type
         *
         * @param tgtIDs
         *      Set of target IDs
         */
        protected void addTgtIDs(final String id, final String attrName, final String tgtType, final Set<String> tgtIDs) {
                if (id==null || attrName==null || tgtType==null || tgtIDs==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String, Map<String, Set<String>>> mapAttrTgtTypeTgtIDs = mapGoAttrTgtTypeTgtIDs.get(id);
                if (mapAttrTgtTypeTgtIDs==null) {
                        mapAttrTgtTypeTgtIDs = new HashMap<String, Map<String, Set<String>>>();
                        mapGoAttrTgtTypeTgtIDs.put(id, mapAttrTgtTypeTgtIDs);
                }

                Map<String, Set<String>> mapTgtTypeTgtIDs = mapAttrTgtTypeTgtIDs.get(attrName);
                if (mapTgtTypeTgtIDs==null) {
                        mapTgtTypeTgtIDs = new HashMap<String, Set<String>>();
                        mapAttrTgtTypeTgtIDs.put(attrName, mapTgtTypeTgtIDs);
                }

                Set<String> ids = mapTgtTypeTgtIDs.get(tgtType);
                if (ids==null) {
                        ids = new HashSet<String>();
                        mapTgtTypeTgtIDs.put(tgtType, ids);
                }

                ids.addAll(tgtIDs);
                idTypes.add(tgtType);
        }

}

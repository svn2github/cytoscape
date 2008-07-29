/* File: AttributeBasedIDMappingModelImpl.java

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

/**
 * To store ID mapping for attributes of nodes/edges
 * 
 */
public class AttributeBasedIDMappingModelImpl implements AttributeBasedIDMappingModel {
        private final Map<String, Map<String,String>> mapGOAttrSrc; // Node/edge id -> attribute name -> souce ID
        private final Map<String,Map<String, Map<String, Set<String>>>> mapGoAttrTgtTypeTgtIDs; // Node/edge id -> attribute name -> target ID type -> target IDs

        public AttributeBasedIDMappingModelImpl() {
                mapGOAttrSrc = new HashMap<String, Map<String,String>>();
                mapGoAttrTgtTypeTgtIDs = new HashMap<String,Map<String, Map<String, Set<String>>>>();
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

                final Map<String,String> mapAttrSrc = mapGOAttrSrc.get(id);
                if (mapAttrSrc==null) {
                        return null;
                }

                final String src = mapAttrSrc.get(attrName);
                return src;
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
        @Override
        public void setSrcIDType(final String id, final String attrName, final String idType) {
                if (id==null || attrName==null || idType==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,String> mapAttrSrc = mapGOAttrSrc.get(id);
                if (mapAttrSrc==null) {
                        mapAttrSrc = new HashMap<String,String>();
                        mapGOAttrSrc.put(id, mapAttrSrc);
                }

                mapAttrSrc.put(attrName, idType);
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
        @Override
        public void addTgtIDs(final String id, final String attrName, final String tgtType, final Set<String> tgtIDs) {
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

                ids.addAll(ids);
        }

}

/* File: IDMappingDataImpl.java

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

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Store ID mapping data
 *
 */
public class IDMappingDataImpl implements IDMappingData {
       // private Set<Map<String,Set<String>>> idMappings;
        
        // map key: id type; value: map of id to map of type to ids
        // this is for fast search for an ID in a type, any better way?
        // note: one id of a type can only have one entry
        private Map<String,Map<String,Map<String,Set<String>>>> mapTypeIDMapTypeIDs;


        public IDMappingDataImpl() {
                //idMappings = new HashSet<Map<String,Set<String>>>();
                mapTypeIDMapTypeIDs =  new HashMap<String,Map<String,Map<String,Set<String>>>>();
        }


        /**
         * Get supported ID types
         *
         * @return
         *      the set of supported ID types
         */
        //@Override
        public Set<String> getIDTypes() {
                return mapTypeIDMapTypeIDs.keySet();
        }

        /**
         * Get whether a id type is contained
         *
         * @param type
         *      ID type
         *
         * @return
         *      true if contained; false otherwise
         *
         * @throws NullPointerException if type is null
         */
        //@Override
        public boolean isIDTypeContained(final String type) {
                if (type==null) {
                        throw new java.lang.NullPointerException();
                }

                return getIDTypes().contains(type);
        }

        /**
         * Add an new ID type
         *
         * @param type
         *      ID typs
         *
         * @return
         *      true if successful; false otherwise, e.g. this ID type has
         *      already existed
         *
         * @throws NullPointerException if type is null
         */
        //@Override
        public boolean addIDType(final String type) {
                if (type==null) {
                        throw new java.lang.NullPointerException();
                }

                if (isIDTypeContained(type)) {
                        return false;
                }

                mapTypeIDMapTypeIDs.put(type, new HashMap<String,Map<String,Set<String>>>());
                return true;
        }

                /**
         * Add an new ID type
         *
         * @param type
         *      ID type
         *
         * @throws NullPointerException if types is null
         */
        //@Override
        public void addIDTypes(Set<String> types) {
                if (types==null) {
                        throw new java.lang.NullPointerException();
                }

                Iterator<String> it = types.iterator();
                while (it.hasNext()) {
                        this.addIDType(it.next());
                }
        }

        /**
         * Get the number of ID mapping
         *
         * @return
         *      the number of ID mapping
         */
//        @Override
//        public int getIDMappingCount() {
//                return idMappings.size();
//        }

        /**
         * return all the id mapping from id of type
         * @param type
         * @param id
         * @return
         */
        public Map<String,Set<String>> getIDMapping(String type, String id) {
                if (type==null || id==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Map<String,Set<String>>> mapIDMapTypeIDs = mapTypeIDMapTypeIDs.get(type);
                if (mapIDMapTypeIDs==null) {
                        return null;
                }

                return mapIDMapTypeIDs.get(id);
        }

        /**
         * Add an ID mapping, this may merging existing id mapping
         *
         * @param idMapping
         *      ID mapping--map from ID types to ID sets
         *
         * @throws NullPointerException if idMapping is null
         * @throws IllegalArgumentException if one of more types are not found
         */
        //@Override
        public void addIDMapping(final Map<String,Set<String>> idMapping) {
                if (idMapping==null) {
                        throw new java.lang.NullPointerException();
                }

                Set<String> types = idMapping.keySet();
                if (!mapTypeIDMapTypeIDs.keySet().containsAll(types)) {
                        throw new java.lang.IllegalArgumentException("One or more types do not exist. Add type first.");
                }

                // TODO: do not necessary to merge
                // find the existing id mapping containing the ids to be added
                HashSet<Map<String,Set<String>>> toMerge = new HashSet<Map<String,Set<String>>>();
                Iterator<String> itType = types.iterator();
                while (itType.hasNext()) {
                        String type = itType.next();
                        Set<String> ids = idMapping.get(type);
                        Iterator<String> itID = ids.iterator();
                        while (itID.hasNext()) {
                                String id = itID.next();
                                Map<String,Set<String>> map = this.getIDMapping(type, id);
                                if (map!=null) {
                                        toMerge.add(map);
                                }
                        }
                }

                // merge existing id mapping
                Map<String,Set<String>> newIDMapping;
                if (toMerge.isEmpty()) {
                        newIDMapping = new HashMap<String,Set<String>>();
                        //idMappings.add(newIDMapping);
                } else {
                        newIDMapping = this.mergeIDMapping(toMerge);
                }

                // add new id mapping
                itType = types.iterator();
                while (itType.hasNext()) {
                        String type = itType.next();
                        Set<String> ids = idMapping.get(type);
                        this.addIDMapping(newIDMapping, type, ids);
                }
        }

        /**
         * Add an ID mapping
         *
         * @param type1
         *      ID type 1
         * @param ids1
         *      ID set 1
         * @param type2
         *      ID type 2
         * @param ids2
         *      ID set 2
         *
         * @return
         *       true if successful; false otherwise
         *
         * @throws NullPointerException if type1 or ids1 or type2 or ids2 is null
         * @throws IllegalArgumentException if type1 or type2 is not found
         */
        //@Override
        public void addIDMapping(final String type1, final Set<String> ids1,
                                 final String type2, final Set<String> ids2) {
                if (type1==null || ids1==null || type2==null || ids2==null) {
                        throw new java.lang.NullPointerException();
                }

                if (!this.isIDTypeContained(type1) || !this.isIDTypeContained(type2)) {
                        throw new java.lang.IllegalArgumentException("'"+type1+"' or '"+type2+"' do not exist. Add type first.");
                }

                Map<String,Set<String>> map = new HashMap<String,Set<String>>();
                map.put(type1, ids1);
                map.put(type2, ids2);

                addIDMapping(map);
        }

        /**
         * Supports one-to-one mapping and one-to-many mapping.
         *
         * @param
         *      ids a set of source IDs
         * @param
         *      srcType type of source IDs
         * @param
         *      tgtType type of target IDs
         *
         * @return
         *      map from each source ID to a set of target IDs
         *
         * @throws NullPointerException if ids or srcType or tgtType is null
         */
        //@Override
        public Map<String, Set<String>> mapID(final Set<String> ids, final String srcType, final String tgtType) {
                if (ids==null || ids.isEmpty()) {
                        throw new java.lang.IllegalArgumentException("Null of empty ids");
                }

                if (srcType==null || tgtType==null) {
                        throw new java.lang.NullPointerException();
                }

                if (!this.isIDTypeContained(srcType) || !this.isIDTypeContained(tgtType)) {
                        return null;
                        //throw new java.lang.IllegalArgumentException("'"+srcType+"' or '"+tgtType+"' does not supported.");
                }

                Map<String, Set<String>> return_this = new HashMap<String, Set<String>>(ids.size());

                for (String id : ids) {
                        Map<String,Set<String>> map = this.getIDMapping(srcType, id);
                        Set<String> tgtIDs = map.get(tgtType);
                        if (tgtIDs!=null) {
                                return_this.put(id, tgtIDs);
                        }
                }

                return return_this;
        }

        /**
         * Merge the id mapping together
         * @param indices
         */
        protected Map<String,Set<String>> mergeIDMapping(final HashSet<Map<String,Set<String>>> toMerge) {
                if (toMerge==null) {
                        throw new java.lang.NullPointerException();
                }

                Iterator<Map<String,Set<String>>> it = toMerge.iterator();
                if (!it.hasNext()) {
                        throw new java.lang.IllegalArgumentException("empty set");
                }

                Set<String> types = this.getIDTypes();

                Map<String,Set<String>> mergeTo = it.next();

                while (it.hasNext()) {
                        Map<String,Set<String>> mergeFrom = it.next();
                        for (String type : types) {
                                Set<String> idsFrom = mergeFrom.get(type);
                                if (idsFrom!=null && !idsFrom.isEmpty()) {
                                        addIDMapping(mergeTo,type,idsFrom);
                                }


                        }

                        //idMappings.remove(mergeFrom);
                }

                return mergeTo;
        }

        /**
         * Add id mapping
         * @param idMapping
         * @param type
         * @param ids
         */
        protected void addIDMapping(Map<String,Set<String>> toIDMapping, final String type, final Set<String> ids) {
                Set<String> idsMerged = toIDMapping.get(type);
                if (idsMerged==null) {
                        toIDMapping.put(type, ids);
                } else {
                        idsMerged.addAll(ids);
                }

                // set the mapTypeIDIndex
                Map<String,Map<String,Set<String>>> map = mapTypeIDMapTypeIDs.get(type);
                for (String id : ids) {
                        map.put(id, toIDMapping);
                }
        }
}

/* File: IDMappingListImpl.java

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
public class IDMappingListImpl implements IDMappingList {

        private Set<String> idTypes;

        // List of map fromid type to set of ids
        // TODO: Use index instead of actual id type to save memory?
        private List<Map<String,Set<String>>> idMappings;
        
        // map key: id type; value: map of id to index in the list
        // this is for fast search for an ID in a type, any better way?
        // note: one id of a type can only have one entry
        private Map<String,Map<String,Integer>> mapTypeIDIndex;


        public IDMappingListImpl() {
                idTypes = new HashSet<String>();
                idMappings = new Vector<Map<String,Set<String>>>();
                mapTypeIDIndex =  new HashMap<String,Map<String,Integer>>();
        }


        /**
         * Get supported ID types
         *
         * @return
         *      the set of supported ID types
         */
        @Override
        public Set<String> getIDTypes() {
                return idTypes;
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
        @Override
        public boolean isIDTypeContained(final String type) {
                if (type==null) {
                        throw new java.lang.NullPointerException();
                }

                return idTypes.contains(type);
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
        @Override
        public boolean addIDType(final String type) {
                if (type==null) {
                        throw new java.lang.NullPointerException();
                }

                if (isIDTypeContained(type)) {
                        return false;
                }

                boolean success = idTypes.add(type);
                if (success) {
                        mapTypeIDIndex.put(type, new HashMap<String,Integer>());
                }

                return success;
        }

                /**
         * Add an new ID type
         *
         * @param type
         *      ID type
         *
         * @throws NullPointerException if types is null
         */
        @Override
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
        @Override
        public int getIDMappingCount() {
                return idMappings.size();
        }

        /**
         * Get the ith ID mapping
         *
         * @param i
         *      index of ID mapping
         *
         * @return
         *      the ith ID mapping
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         */
        @Override
        public Map<String,Set<String>> getIDMapping(final int i) {
                if (i<0||i>=getIDMappingCount()) {
                        throw new java.lang.IndexOutOfBoundsException();
                }

                return idMappings.get(i);
        }

        /**
         * Get the ID set of type in the ith ID mapping
         *
         * @param i
         *      index of ID mapping
         * @param type
         *      ID type
         *
         * @return
         *      ID set if type exists; null otherwise
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         * @throws NullPointerException if idTypes is null
         */
        @Override
        public Set<String> getIDMapping(final int i, final String type) {
                if (i<0||i>=getIDMappingCount()) {
                        throw new java.lang.IndexOutOfBoundsException();
                }

                if (type==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Set<String>> map = getIDMapping(i);

                Set<String> ids = map.get(type);
                if (ids==null || ids.isEmpty()) {
                        return null;
                }

                return ids;               
        }

        /**
         * Get the index of id of type
         *
         * @param type
         *      ID type
         * @param id
         *      ID
         *
         * @return
         *      Index of ID if exists; -1, otherwise
         *
         * @throws NullPointerException if type or id is null
         */
        @Override
        public int indexOf(final String type, final String id) {
                if (type==null || id==null ) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Integer> map = mapTypeIDIndex.get(type);
                if (map==null) {
                        return -1;
                }

                Integer i = map.get(id);
                if (i==null) {
                        return -1;
                }

                return i;
        }

        /**
         * Add an ID mapping, this may merging existing id mapping
         *
         * @param idMapping
         *      ID mapping--map from ID types to ID sets
         *
         * @return true if successful; false otherwise
         *
         * @throws NullPointerException if idMapping is null
         * @throws IllegalArgumentException if one of more types are not found
         */
        @Override
        public void addIDMapping(final Map<String,Set<String>> idMapping) {
                if (idMapping==null) {
                        throw new java.lang.NullPointerException();
                }

                Set<String> types = idMapping.keySet();
                if (!idTypes.containsAll(types)) {
                        throw new java.lang.IllegalArgumentException("One or more types do not exist. Add type first.");
                }

                // find the existing id mapping containing the ids to be added
                TreeSet<Integer> indices = new TreeSet<Integer>();
                Iterator<String> itType = types.iterator();
                while (itType.hasNext()) {
                        String type = itType.next();
                        Set<String> ids = idMapping.get(type);
                        Iterator<String> itID = ids.iterator();
                        while (itID.hasNext()) {
                                String id = itID.next();
                                int index = this.indexOf(type, id);
                                if (index!=-1) {
                                        indices.add(index);
                                }
                        }
                }

                // add id mapping
                int iNewIDMapping;
                if (indices.isEmpty()) {
                        Map<String,Set<String>> newIDMapping = new HashMap<String,Set<String>>();
                        idMappings.add(newIDMapping);
                        iNewIDMapping = idMappings.size()-1;
                } else {
                        iNewIDMapping = this.mergeIDMapping(indices);
                }

                itType = types.iterator();
                while (itType.hasNext()) {
                        String type = itType.next();
                        Set<String> ids = idMapping.get(type);
                        this.addIDMapping(iNewIDMapping, type, ids);
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
        @Override
        public void addIDMapping(final String type1, final Set<String> ids1,
                                 final String type2, final Set<String> ids2) {
                if (type1==null || ids1==null || type2==null || ids2==null) {
                        throw new java.lang.NullPointerException();
                }

                if (!idTypes.contains(type1) || !idTypes.contains(type2)) {
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
        @Override
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

                final Iterator<String> it = ids.iterator();
                while (it.hasNext()) {
                        String id = it.next();
                        int index = this.indexOf(srcType, id);
                        Set<String> tgtIDs = idMappings.get(index).get(tgtType);
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
        protected int mergeIDMapping(final TreeSet<Integer> indices) {
                if (indices==null) {
                        throw new java.lang.NullPointerException();
                }

                if (indices.size()==0) {
                        throw new java.lang.IllegalArgumentException("empty indices");
                }

                Iterator<Integer> it = indices.iterator();
                int iMerged = it.next(); //merge to the lowest
                if (iMerged<0 || iMerged>=this.getIDMappingCount()) {
                        throw new java.lang.IndexOutOfBoundsException();
                }

                //Map<String,Set<String>> idMappingMerged = idMappings.get(iMerged);

                it = indices.descendingIterator(); // from last to second
                while (it.hasNext()) {
                        int i = it.next();
                        if (iMerged==i) break;
                        if (i<0 || i>=this.getIDMappingCount()) {
                                throw new java.lang.IndexOutOfBoundsException();
                        }

                        Map<String,Set<String>> idMapping = idMappings.get(i);
                        Iterator<Map.Entry<String,Set<String>>> itEntry = idMapping.entrySet().iterator();
                        while (itEntry.hasNext()) {
                                Map.Entry<String,Set<String>> entry = itEntry.next();
                                String type = entry.getKey();
                                Set<String> ids = entry.getValue();
                                this.addIDMapping(iMerged, type, ids);
                        }

                        idMappings.remove(i); // remove after merged
                }

                return iMerged;
        }

        /**
         * Add id mapping
         * @param idMapping
         * @param type
         * @param ids
         */
        protected void addIDMapping(final int index, final String type, final Set<String> ids) {
                Map<String,Set<String>> idMapping = idMappings.get(index);
                Set<String> idsMerged = idMapping.get(type);
                if (idsMerged==null) {
                        idMapping.put(type, ids);
                } else {
                        idsMerged.addAll(ids);
                }

                // set the mapTypeIDIndex
                Map<String,Integer> map = mapTypeIDIndex.get(type);
                final Iterator<String> it = ids.iterator();
                while (it.hasNext()) {
                        String id = it.next();
                        map.put(id, index);
                }

                //return idMapping;
        }
}

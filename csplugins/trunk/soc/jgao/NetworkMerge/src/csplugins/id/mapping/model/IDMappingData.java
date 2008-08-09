/* File: IDMappingData.java

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
import java.util.Map;

/**
 * Store ID mapping data in a list, could be viewed as a 2-d table
 * 
 */
public interface IDMappingData {

        /**
         * Get supported ID types
         *
         * @return
         *      the set of supported ID types
         */
        public Set<String> getIDTypes();

        /**
         * Add an new ID type
         *
         * @param type
         *      ID type
         *
         * @return
         *      true if successful; false otherwise, e.g. this ID type has
         *      already existed
         *
         * @throws NullPointerException if type is null
         */
        public boolean addIDType(String type);

        /**
         * Add an new ID type
         *
         * @param type
         *      ID type
         *
         * @throws NullPointerException if types is null
         */
        public void addIDTypes(Set<String> types);

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
        public boolean isIDTypeContained(String type);

        /**
         * Get the number of ID mapping
         *
         * @return
         *      the number of ID mapping
         */
        //public int getIDMappingCount();

        /**
         * return all the id mapping from source ID id of and source type type
         * @param type
         * @param id
         * @return
         */
        public Map<String,Set<String>> getIDMapping(String type, String id);

        /**
         * Add an ID mapping
         *
         * @param idMapping
         *      ID mapping--map from ID types to ID sets
         *
         * @throws NullPointerException if idMapping is null
         * @throws IllegalArgumentException if one of more types are not found
         */
        public void addIDMapping(Map<String,Set<String>> idMapping);

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
         * @throws NullPointerException if type1 or ids1 or type2 or ids2 is null
         * @throws IllegalArgumentException if type1 or type2 is not found
         */
        public void addIDMapping(String type1, Set<String> ids1, String type2, Set<String> ids2);

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
        public Map<String, Set<String>> mapID(Set<String> ids, String srcType, String tgtType);
}

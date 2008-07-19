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

import java.util.Set;
import java.util.Map;

/**
 * Store ID mapping data
 *
 */
public class IDMappingListImpl implements IDMappingList {

        /**
         * Get supported ID types
         *
         * @return
         *      the set of supported ID types
         */
        public Set<String> getIDTypes() {
                return null;
        }

        /**
         * Set the supported ID types
         *
         * @param idTypes
         *      the supported ID types
         *
         * @throws NullPointerException if idTypes is null
         */
        public void setIDTypes(Set<String> idTypes) {

        }

        /**
         * Get the number of ID mapping
         *
         * @return
         *      the number of ID mapping
         */
        public int getIDMappingCount() {
                return -1;
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
        public Map<String,Set<String>> getIDMapping(int i) {
                return null;
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
         *      ID set
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         * @throws NullPointerException if idTypes is null
         */
        public Set<String> getIDMapping(int i, String type) {
                return null;
        }

        /**
         * Add an ID mapping
         *
         * @param idMapping
         *      ID mapping--map from ID types to ID sets
         *
         * @return true if successful; false otherwise
         *
         * @throws NullPointerException if idMapping is null
         * @throws TypeNotPresentException if one of more types are not found
         */
        public boolean addIDMapping(Map<String,Set<String>> idMapping) {
                return false;
        }

        /**
         * Add an ID mapping on the ith position
         *
         * @param i
         *      index of ID mapping
         * @param idMapping
         *      ID mapping--map from ID types to ID sets
         *
         * @return true if successful; false otherwise
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         * @throws NullPointerException if idMapping is null
         * @throws TypeNotPresentException if one of more types are not found
         */
        public boolean addIDMapping(int i, Map<String,Set<String>> idMapping) {
                return false;
        }

        /**
         * Add an ID id to type in ID mapping on the ith position
         *
         * @param i
         *      index of ID mapping
         * @param type
         *      ID type
         * @param id
         *      ID
         *
         * @return true if successful; false otherwise
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         * @throws NullPointerException if type or id is null
         * @throws TypeNotPresentException if one of more types are not found
         */
        public boolean addIDMapping(int i, String type, String id) {
                return false;
        }

        /**
         * Set the ID mapping on the ith position
         *
         * @param i
         *      index of ID mapping
         * @param idMapping
         *      ID mapping--map from ID types to ID sets
         *
         * @return the old ID mapping if successful; null otherwise
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         * @throws NullPointerException if idMapping is null
         * @throws TypeNotPresentException if one of more types are not found
         */
        public Map<String,Set<String>> setIDMapping(int i, Map<String,Set<String>> idMapping) {
                return null;
        }

        /**
         * Set the ID mapping on the ith position
         *
         * @param i
         *      index of ID mapping
         * @param type
         *      ID type
         * @param idSet
         *      ID set
         *
         * @return the old IDs if successful; null otherwise
         *
         * @throws IndexOutOfBoundsException if i is out of bound
         * @throws NullPointerException if type or idSet is null
         * @throws TypeNotPresentException if one of more types are not found
         */
        public Set<String> setIDMapping(int i, String type, Set<String> idSet) {
                return null;
        }
}

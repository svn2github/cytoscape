/* File: AttributeBasedIDMappingData.java

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
 * To store ID mapping for attributes of nodes/edges
 * 
 */
public interface AttributeBasedIDMappingData {

        public boolean isEmpty();

        /**
         * 
         * @return map of nodes/edges to attributes
         */
        public Map<String,Set<String>> getMapGOAttrs();

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
        public String getSrcIDType(String id, String attrName);

        /**
         * Get source ID attribute attrName of node/edge id
         *
         * @param id
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         *
         * @return
         *      source ID if exist, null otherwise
         *
         * @throws NullPointerException if id or attrName is null
         */
        public Set<String> getSrcIDs(String id, String attrName);

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
        public Set<String> getTgtIDs(String id, String attrName, String tgtType);

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
        public Map<String,Set<String>> getMapTgtTypeIDs(String id, String attrName);
        
        /**
         * 
         * @return id type set of all data
         */
        public Set<String> getIDTypes();

        /**
         * Add id mapping
         * @param idGO
         *      identifier of the node/edge
         * @param attrName
         *      attribute name
         * @param srcIDType
         *      source ID type
         * @param srcID
         *      source ID
         * @param tgtIDType
         *      target ID type
         * @param tgtIDs
         *      Set of target IDs
         */
        public void addIDMapping(final String idGO,
                                 final String attrName,
                                 final String srcIDType,
                                 final String srcID,
                                 final String tgtIDType,
                                 final Set<String> tgtIDs);

//        /**
//         * Set source ID type
//         * @param id
//         *      identifier of the node/edge
//         * @param attrName
//         *      attribute name
//         * @param idType
//         *      source ID type
//         *
//         * @throws NullPointerException if id or attrName or idType is null
//         */
//        public void setSrcIDAndType(String id, String attrName, String idType, String idSrc);
//
//        /**
//         * Add target IDs
//         * @param id
//         *      identifier of the node/edge
//         * @param attrName
//         *      attribute name
//         * @param tgtType
//         *      target ID type
//         *
//         * @param tgtIDs
//         *      Set of target IDs
//         */
//        public void addTgtIDs(String id, String attrName, String tgtType, Set<String> tgtIDs);

        /**
         * remove all data
         */
        public void clear();

}

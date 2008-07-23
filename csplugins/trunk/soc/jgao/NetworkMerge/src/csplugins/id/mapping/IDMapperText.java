/* File: IDMapperText.java

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

package csplugins.id.mapping;

import csplugins.id.mapping.model.IDMappingList;

import java.util.Map;
import java.util.Set;

import java.net.URL;

/**
 * class for ID mapping from Delimited text file
 * 
 */ 
public class IDMapperText implements IDMapperFile {
        private URL url;
        private IDMappingList idMappingList;

        public IDMapperText() {
                url = null;
                idMappingList = null;
        }

        public IDMapperText(final URL url) {
                this.url = url;
                //idMappingList
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
        public Map<String, Set<String>> mapID(Set<String> ids, String srcType, String tgtType) {
                return null;
        }

        /*
        * @return supported source ID types
        *
        */
        @Override
        public Set<String> getSupportedSrcIDType() {
                return null;
        }

        /*
        * @return supported target ID types
        *
        */
        @Override
        public Set<String> getSupportedTgtIDType() {
                return null;
        }

        /**
         *
         * @return file URL
         */
        @Override
        public URL getURL() {
                return url;
        }

        /**
         *
         * @param url
         *      file URL
         */
        @Override
        public void setURL(URL url) {
                this.url = url;
        }

}

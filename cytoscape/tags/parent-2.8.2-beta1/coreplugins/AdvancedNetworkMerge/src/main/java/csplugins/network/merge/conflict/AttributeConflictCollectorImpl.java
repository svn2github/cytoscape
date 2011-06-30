/* File: AttributeConflict.java

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


package csplugins.network.merge.conflict;

import cytoscape.data.CyAttributes;

import java.util.Map;
import java.util.HashMap;

/**
 * Collect attribute conflicts
 *
 * Assumption: for each from_node, only one attribute to be merged into to_node
 * 
 */
public class AttributeConflictCollectorImpl implements AttributeConflictCollector {

        protected class Conflicts {
                public final CyAttributes cyAttributes;
                public Map<String,String> mapFromIDFromAttr;

                public Conflicts(final CyAttributes cyAttributes) {
                        this.cyAttributes = cyAttributes;
                        mapFromIDFromAttr = new HashMap<String,String>();
                }

                public void addConflict(final String fromID, final String fromAttr) {
                        mapFromIDFromAttr.put(fromID, fromAttr);
                }

                public boolean removeConflict(final String fromID, final String fromAttr) {
                        String attr = mapFromIDFromAttr.get(fromID);
                        if (attr==null || attr.compareTo(fromAttr)!=0) {
                                return false;
                        }

                        mapFromIDFromAttr.remove(fromID);
                        return true;
                }
        }

        protected Map<String,Map<String,Conflicts>> mapToIDToAttrConflicts;

        public AttributeConflictCollectorImpl() {
                this.mapToIDToAttrConflicts = new HashMap<String,Map<String,Conflicts>>();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isEmpty() {
                return mapToIDToAttrConflicts.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        public Map<String,String> getMapToIDAttr() {
                Map<String,String> mapToIDAttr = new HashMap<String,String>();
                for (Map.Entry<String,Map<String,Conflicts>> entry : mapToIDToAttrConflicts.entrySet()) {
                        String id = entry.getKey();
                        for (String attr : entry.getValue().keySet()) {
                                mapToIDAttr.put(id,attr);
                        }
                }

                return mapToIDAttr;
        }

        /**
         * {@inheritDoc}
         */
        public Map<String,String> getConflicts(final String toID, final String toAttr){
                if (toID==null || toAttr==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Conflicts> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        return null;
                }

                return mapToAttrConflicts.get(toAttr).mapFromIDFromAttr;
        }

        /**
         * {@inheritDoc}
         */
        public CyAttributes getCyAttributes(final String toID, final String toAttr) {
                if (toID==null || toAttr==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Conflicts> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        return null;
                }

                return mapToAttrConflicts.get(toAttr).cyAttributes;
        }

        /**
         * {@inheritDoc}
         */
        public void addConflict(final String fromID,
                                        final String fromAttr,
                                        final String toID,
                                        final String toAttr,
                                        final CyAttributes cyAttributes) {
                if (fromID==null || fromAttr==null || toID==null || toAttr==null || cyAttributes==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Conflicts> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        mapToAttrConflicts = new HashMap<String,Conflicts>();
                        mapToIDToAttrConflicts.put(toID, mapToAttrConflicts);
                }

                Conflicts conflicts = mapToAttrConflicts.get(toAttr);
                if (conflicts==null) {
                        conflicts = new Conflicts(cyAttributes);
                        mapToAttrConflicts.put(toAttr, conflicts);
                } else {
                        if (conflicts.cyAttributes!=cyAttributes) {
                                throw new java.lang.IllegalArgumentException("CyAttributes are different!");
                        }
                }

                conflicts.addConflict(fromID, fromAttr);
        }

        /**
         * {@inheritDoc}
         */
        public boolean removeConflicts(String toID, String toAttr) {
                if (toID==null || toAttr==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Conflicts> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        return false;
                }

                if (mapToAttrConflicts.get(toAttr)==null) {
                        return false;
                }

                mapToAttrConflicts.remove(toAttr);
                return true;
        }

        /**
         * {@inheritDoc}
         */
        public boolean removeConflict(final String fromID, final String fromAttr, final String toID, final String toAttr) {
                if (fromID==null || fromAttr==null || toID==null || toAttr==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,Conflicts> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        return false;
                }

                Conflicts conflicts = mapToAttrConflicts.get(toAttr);
                if (conflicts==null) {
                        return false;
                }

                boolean ret = conflicts.removeConflict(fromID, fromAttr);
                if (ret && conflicts.mapFromIDFromAttr.isEmpty()) {
                        mapToAttrConflicts.remove(toAttr);
                        if (mapToAttrConflicts.isEmpty()) {
                                mapToIDToAttrConflicts.remove(toID);
                        }
                }

                return ret;
        }

}

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

import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

/**
 * Collect attribute conflicts
 * 
 */
public class AttributeConflictCollectorImpl implements AttributeConflictCollector {

        protected List<AttributeConflict> conflictList;
        protected Map<String,Map<String,List<AttributeConflict>>> mapToIDToAttrConflicts;

        public AttributeConflictCollectorImpl() {
                conflictList = new Vector<AttributeConflict>();
                mapToIDToAttrConflicts = new HashMap<String,Map<String,List<AttributeConflict>>>();
        }

        @Override
        public List<AttributeConflict> getConflictList() {
                return conflictList;
        }

        @Override
        public int getConfilctCount() {
                return conflictList.size();
        }

        @Override
        public boolean isEmpty() {
                return conflictList.isEmpty();
        }

        @Override
        public AttributeConflict getConflict(final int index) {
                if (index<0 || index>=getConfilctCount()) {
                        throw new java.lang.IndexOutOfBoundsException();
                }
                return conflictList.get(index);
        }

        @Override
        public List<AttributeConflict> getConflicts(final String toID, final String toAttr){
                if (toID==null || toAttr==null) {
                        throw new java.lang.NullPointerException();
                }

                Map<String,List<AttributeConflict>> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        return null;
                }

                return mapToAttrConflicts.get(toAttr);
        }

        @Override
        public void addConflict(final AttributeConflict conflict) {
                if (conflict==null) {
                        throw new java.lang.NullPointerException();
                }

                // add to conflictList
                conflictList.add(conflict);

                // add to mapToIDToAttrConflicts
                String toID = conflict.getToID();
                String toAttr = conflict.getToAttr();

                Map<String,List<AttributeConflict>> mapToAttrConflicts = mapToIDToAttrConflicts.get(toID);
                if (mapToAttrConflicts==null) {
                        mapToAttrConflicts = new HashMap<String,List<AttributeConflict>>();
                        mapToIDToAttrConflicts.put(toID, mapToAttrConflicts);
                }

                List<AttributeConflict> conflicts = mapToAttrConflicts.get(toAttr);
                if (conflicts==null) {
                        conflicts = new Vector<AttributeConflict>();
                        mapToAttrConflicts.put(toAttr, conflicts);
                }

                conflicts.add(conflict);

        }

        @Override
        public boolean removeConflict(final AttributeConflict conflict) {
                if (conflict==null) {
                        throw new java.lang.NullPointerException();
                }
                return conflictList.remove(conflict);
        }

        @Override
        public AttributeConflict removeConflict(int index) {
                if (index<0 || index>=getConfilctCount()) {
                        throw new java.lang.IndexOutOfBoundsException();
                }
                return conflictList.remove(index);
        }

}

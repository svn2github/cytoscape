/* File: AttributeConflictImpl.java

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

/**
 * Store attribute conflict when merge two nodes/edges' attribute
 * 
 */
public class AttributeConflictImpl implements AttributeConflict {
        protected String fromID;
        protected String fromAttr;
        protected String toID;
        protected String toAttr;
        protected CyAttributes cyAttributes;

        public AttributeConflictImpl(final String fromID,
                                     final String fromAttr,
                                     final String toID,
                                     final String toAttr,
                                     final CyAttributes cyAttributes) {
                this.fromAttr = fromAttr;
                this.fromID = fromID;
                this.toAttr = toAttr;
                this.toID = toID;
                this.cyAttributes = cyAttributes;
        }

        @Override
        public String getFromAttr() {
                return fromAttr;
        }

        @Override
        public String getFromID() {
                return fromID;
        }

        @Override
        public String getToAttr() {
                return toAttr;
        }

        @Override
        public String getToID() {
                return toID;
        }

        @Override
        public CyAttributes getCyAttributes() {
                return cyAttributes;
        }

}

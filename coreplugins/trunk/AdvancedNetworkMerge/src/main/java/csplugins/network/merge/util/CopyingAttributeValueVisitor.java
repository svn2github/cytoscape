/* File: AbstractNetworkMerge.java

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

package csplugins.network.merge.util;


import cytoscape.data.CyAttributes;
import cytoscape.data.AttributeValueVisitor;

import java.util.Arrays;
/**
 *
 * @
 */

    // this is different from CopyingAttributeValueVisitor in CyAtributeUtil
    // the attributes copying from and to can be different
    /**
     * copy each attribute value from copyID to an
     * attribute value of copyAttribute associated with objTraversedCopyID.
     */
    public class CopyingAttributeValueVisitor implements AttributeValueVisitor {
        private String toID;
        private String toAttrName;

        public CopyingAttributeValueVisitor(String toID, String toAttrName) {
            this.toID = toID;
            this.toAttrName = toAttrName;
        }

        //@Override
        public void visitingAttributeValue(String fromID, String attrName,
                                           CyAttributes attrs, Object[] keySpace,
                                           Object visitedValue) {
            if (!Arrays.asList(attrs.getAttributeNames()).contains(toAttrName)) { // if toAttribute not exists
                throw new java.lang.IllegalStateException("'"+toAttrName+"' must be defined before calling this method");
            }

            attrs.getMultiHashMap().setAttributeValue(toID, toAttrName, visitedValue, keySpace);
        }
    }

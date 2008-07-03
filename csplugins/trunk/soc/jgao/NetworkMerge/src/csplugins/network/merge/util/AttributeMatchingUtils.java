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
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.AttributeValueVisitor;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

/**
 *
 * 
 */
public class AttributeMatchingUtils {
    
    public static boolean isAttributeTypeSame(final Set<String> attrNames, final CyAttributes attrs) {
        if (attrNames==null || attrs==null) {
            throw new java.lang.NullPointerException("Null attrNames or attrs");
        }
        
        final Iterator<String> it = attrNames.iterator();
        if (!it.hasNext()) {
            throw new java.lang.IllegalArgumentException("Empty attrNames");
        }
        
        final String first = it.next();
        while (it.hasNext()) {
            if (!isAttributeTypeSame(first,it.next(),attrs)) {
                return false;
            }
        }
        return true;
    }
    /*
     * Check if the types are the same for two attributes
     * 
     * 
     * 
     */
    public static boolean isAttributeTypeSame(final String attrName1, 
                                              final String attrName2, 
                                              final CyAttributes attrs) {
        if (attrName1==null || attrName2==null || attrs==null) {
            throw new java.lang.NullPointerException("Null attrName1 or attrName2 or attrs");
        }
        
        final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
        if (!attrNames.contains(attrName1) || !attrNames.contains(attrName2)) {
            throw new java.lang.IllegalArgumentException("attrName1 or/and attrNames not exists");
        }
        
        final MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();
        final byte valType1 = mmapDef.getAttributeValueType(attrName1);
        final byte valType2 = mmapDef.getAttributeValueType(attrName2);
        
        if (valType1 != valType2) { // if value type is not the same
                return false; 
        }
        
        if (valType1 < 0 || valType2 < 0) { // for undefined type
                return false;
        }

        final byte[] dimTypes1 = mmapDef.getAttributeKeyspaceDimensionTypes(attrName1);
        final byte[] dimTypes2 = mmapDef.getAttributeKeyspaceDimensionTypes(attrName2);

        final int n = dimTypes1.length;
        if (n!=dimTypes2.length) {
            return false;
        }
        
        for (int i=0; i<n; i++) {
            if (dimTypes1[i]!=dimTypes2[i]) {
                return false;
            }
        }
        
        // do we need to check whether th key span is the same?

        return true;
    }
    
    public static boolean isAttributeValueSame(final String id1,
                                               final String attrName1,
                                               final String id2, 
                                               final String attrName2, 
                                               final CyAttributes attrs) {
        if ((id1 == null) || (attrName1 == null) || (id2 == null) || (attrName2==null) || (attrs == null)) {
            throw new java.lang.IllegalArgumentException("Null argument.");
        }        
                
        final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
        if (!attrNames.contains(attrName1) || !attrNames.contains(attrName2)) {
            throw new java.lang.IllegalArgumentException("attrName1 or/and attrNames not exists");
        }
        
        //if (!isAttributeTypeSame(attrName1,attrName2,attrs)) {
        //    throw new java.lang.UnsupportedOperationException("isAttributeValueSame does not support two attribute with diffrent types");
        //}

        if (id1.compareTo(id2)==0 && attrName1.compareTo(attrName2)==0) {
                return true;
        }
        
        
        if (!attrs.hasAttribute(id1, attrName1) 
                && !attrs.hasAttribute(id2, attrName2)) {
            return true; // if both of them are null
        } else if (!attrs.hasAttribute(id1, attrName1) 
                || !attrs.hasAttribute(id2, attrName2)) { // Is it neccessary to handle empty string?
            return false; // 
        }
              

        //TODO use a idmapping visitor to compare
        CmpAttributeValueVisitor cmpVisitor = new CmpAttributeValueVisitor(id1,attrName1);

        CyAttributesUtils.traverseAttributeValues(id2, attrName2, attrs, cmpVisitor);

        return cmpVisitor.getIsSame();
    }
    
    //TODO could this function move to cytoscape.data.CyAttributeUtil
    /**
     * Copy a specific attribute of a given object to another attribute in
     * the same or another object. This includes complex attributes.
     * @param originalID the identifier of the object we are copying
     *                   from (e.g, equivalent to CyNode.getIdentifier()).
     * @param copyID the identifier of the object we are copying to.
     * @param attrName the name of the attribute we wish to copy.
     * @param attrs the CyAttributes from which to copy and retrieve the attribute.
     * 
     * @throws IllegalArgumentException
     */
    static public void copyAttribute(final String fromID,
                                     final String fromAttrName,
                                     final String toID, 
                                     final String toAttrName, 
                                     final CyAttributes attrs) {
        if ((fromID == null) || (fromAttrName == null) || (toID == null) || (toAttrName == null) || (attrs==null)) {
            throw new java.lang.IllegalArgumentException("Null argument.");
        }
        
                
        final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
        if (!attrNames.contains(fromAttrName)) {
            throw new java.lang.IllegalArgumentException("fromAttrName not exists");
        }

        if (toID.compareTo(fromID)==0 && toAttrName.compareTo(fromAttrName)==0) {
            //TODO: if local attribute is realized, process here
            return;
        }

        AttributeValueVisitor copyVisitor = new CopyingAttributeValueVisitor(toID,toAttrName);

        CyAttributesUtils.traverseAttributeValues(fromID, fromAttrName, attrs, copyVisitor);
    }
    
    private static class CmpAttributeValueVisitor implements AttributeValueVisitor {
        private String cmpToID;
        private String cmpToAttr;
        private Boolean isSame;

        public CmpAttributeValueVisitor(final String cmpToID, final String cmpToAttr) {
                this.cmpToID = cmpToID;
                this.cmpToAttr = cmpToAttr;
        }

        public boolean getIsSame() {
            if (isSame==null) {
                throw new java.lang.IllegalStateException("Has not been travelled");
            }
            return isSame;
        }

        public void visitingAttributeValue(final String objTraversedID, 
                                           final String attrName,
                                           final CyAttributes attrs,
                                           final Object[] keySpace,
                                           final Object visitedValue) {
            if (isSame==null) { // first value
                isSame=true;
            }
            
            if (!isSame) { // if already have different value, return
                return;
            }

            Object cmpToValue = attrs.getMultiHashMap().getAttributeValue(cmpToID, cmpToAttr, keySpace);
            if (cmpToValue==null||visitedValue==null) {
                return; // ignore null
            }
            isSame = cmpToValue.equals(visitedValue); //is it OK to use .equal()?
        }
    }
    
    // this is different from CopyingAttributeValueVisitor in CyAtributeUtil
    // the attributes copying from and to can be different
    /**
     * copy each attribute value from copyID to an
     * attribute value of copyAttribute associated with objTraversedCopyID.
     */
    private static class CopyingAttributeValueVisitor implements AttributeValueVisitor {
        private String toID;
        private String toAttribute;

        public CopyingAttributeValueVisitor(String toID, String toAttribute) {
            this.toID = toID;
            this.toAttribute = toAttribute;
        }

        public void visitingAttributeValue(String objTraversedID, String attrName,
                                           CyAttributes attrs, Object[] keySpace,
                                           Object visitedValue) {
            if (!Arrays.asList(attrs.getAttributeNames()).contains(toAttribute)) { // if toAttribute not exists
                final MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();
                mmapDef.defineAttribute(toAttribute,
                                        mmapDef.getAttributeValueType(attrName),
                                        mmapDef.getAttributeKeyspaceDimensionTypes(attrName));
            }
            attrs.getMultiHashMap().setAttributeValue(toID, toAttribute, visitedValue, keySpace);
        }
    }
}

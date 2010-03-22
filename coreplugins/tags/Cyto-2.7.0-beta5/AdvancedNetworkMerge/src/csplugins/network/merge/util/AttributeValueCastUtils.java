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
import cytoscape.data.attr.MultiHashMapDefinition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

/**
 *
 * 
 */
public class AttributeValueCastUtils {
    /**
     * Check if the types are convertable for two attributes
     * @param fromAttrName
     * @param toAttrName
     * @param attrs
     * @return
     */
    public static boolean isAttributeTypeConvertable(final String fromAttrName, 
                                              final String toAttrName, 
                                              final CyAttributes attrs) {
        if (fromAttrName==null || toAttrName==null || attrs==null) {
            throw new java.lang.NullPointerException("Null fromAttrName or toAttrName or attrs");
        }
        
        final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
        if (!attrNames.contains(fromAttrName)) {
            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' not exists");
        }
        
        if (!attrNames.contains(toAttrName)) {
            return true; // always convertable to a non-existing attribute
        }

        // first check the basic type is convertible
        final MultiHashMapDefinition mmapDef = attrs.getMultiHashMapDefinition();
        final byte valType1 = mmapDef.getAttributeValueType(fromAttrName);
        final byte valType2 = mmapDef.getAttributeValueType(toAttrName);
        
        if (valType1 != valType2) { // if value type is not the same
            switch (valType1) {
                case MultiHashMapDefinition.TYPE_BOOLEAN:
                    if (valType2!=MultiHashMapDefinition.TYPE_BOOLEAN
                            && valType2!=MultiHashMapDefinition.TYPE_STRING) {
                        return false;
                    }
                    break;
                case MultiHashMapDefinition.TYPE_FLOATING_POINT:
                    if (valType2!=MultiHashMapDefinition.TYPE_FLOATING_POINT
                            && valType2!=MultiHashMapDefinition.TYPE_STRING) {
                        return false;
                    }
                    break;
                case MultiHashMapDefinition.TYPE_INTEGER:
                    if (valType2!=MultiHashMapDefinition.TYPE_INTEGER
                            && valType2!=MultiHashMapDefinition.TYPE_FLOATING_POINT
                            && valType2!=MultiHashMapDefinition.TYPE_STRING) {
                        return false;
                    }
                    break;
                case MultiHashMapDefinition.TYPE_STRING:
                    if (valType2!=MultiHashMapDefinition.TYPE_STRING) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }

        // check list, map ...
        final byte[] dimTypes1 = mmapDef.getAttributeKeyspaceDimensionTypes(fromAttrName);
        final byte[] dimTypes2 = mmapDef.getAttributeKeyspaceDimensionTypes(toAttrName);

        final int n1 = dimTypes1.length;
        final int n2 = dimTypes2.length;
        
        if (n2==0 && valType2==MultiHashMapDefinition.TYPE_STRING) {
            return true; // any type can convert to String
        }
        
        if (n1==0 && n2==0) { // simple types
            return true;
        }
        
        if (n1==0 && n2==1) {
            return dimTypes2[0] == MultiHashMapDefinition.TYPE_INTEGER; // converting from simple type to simple list
        }
        
        if (n1!=n2) {
            return false;
        }
        
        for (int i=0; i<n1; i++) {
            if (dimTypes1[i]!=dimTypes2[i]) {
                return false;
            }
        }
        
        return valType1==valType2; // for complex case, type must be the same
    }

    public static boolean isAttributeTypeConvertable(final byte fromType, final byte toType) {
            switch (fromType) {
                case CyAttributes.TYPE_BOOLEAN:
                    return toType==CyAttributes.TYPE_BOOLEAN
                            || toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_LIST;
                case CyAttributes.TYPE_FLOATING:
                    return toType==CyAttributes.TYPE_FLOATING
                            || toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_LIST;
                case CyAttributes.TYPE_INTEGER:
                    return toType==CyAttributes.TYPE_INTEGER
                            || toType==CyAttributes.TYPE_FLOATING
                            || toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_LIST;
                case CyAttributes.TYPE_STRING:
                    return toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_LIST;
                case CyAttributes.TYPE_SIMPLE_LIST:
                    return toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_LIST;
                case CyAttributes.TYPE_SIMPLE_MAP:
                    return toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_MAP;
                case CyAttributes.TYPE_COMPLEX:
                    return toType==CyAttributes.TYPE_STRING
                            || toType==CyAttributes.TYPE_SIMPLE_MAP;
                default:
                    return false;
            }
    }
    
    /*
     * Return the attribute with the highest compatible attribute
     * 
     */
    public static String getMostCompatibleAttribute(final Set<String> attrNames, final CyAttributes attrs) {
        if (attrNames==null || attrs==null) {
            throw new java.lang.NullPointerException("Null attrNames or attrs");
        }
        
        attrNames.remove(null);
        if (attrNames.isEmpty()) {
            throw new java.lang.IllegalArgumentException("Empty attrNames");
        }
        
        if (!Arrays.asList(attrs.getAttributeNames()).containsAll(attrNames)) {
            throw new java.lang.IllegalStateException("Attribute not exists");
        }
        
        String[] attrArray = attrNames.toArray(new String[0]);
        Set<String> compatibleSet = new HashSet<String>();
        
        int n = attrArray.length;
        for (int i=0; i<n; i++) {
            String attr1 = attrArray[i];
            
            boolean compatible = true;
            for (int j=0; j<n; j++) {
                if (j==i) continue;
                String attr2 = attrArray[j];
                if (!AttributeValueCastUtils.isAttributeTypeConvertable(attr2, attr1, attrs)) {
                    compatible = false;
                    break;
                }
            }
            
            if (compatible) {
                compatibleSet.add(attr1);
            }
        }
        
        if (compatibleSet.isEmpty()) {
            return null;
        }
        
        final Iterator<String> it = compatibleSet.iterator();
        while (it.hasNext()) { 
            // if exists list, return it
            String attr = it.next();
            byte type = attrs.getType(attr);
            if (type==CyAttributes.TYPE_SIMPLE_LIST) {
                return attr;
            }
        }
        return compatibleSet.iterator().next();
        
    }

    
    public static Object attributeValueCast(byte typeTo, Object value) {
        switch (typeTo) {
            case CyAttributes.TYPE_BOOLEAN:
                    return Boolean.valueOf(value.toString());
            case CyAttributes.TYPE_INTEGER:
                    return Integer.valueOf(value.toString());
            case CyAttributes.TYPE_FLOATING:
                    return Double.valueOf(value.toString());
            case CyAttributes.TYPE_STRING:
                    return value.toString();
            default:
                    throw new java.lang.IllegalStateException("wrong type");
        }
    }
    
}

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

package org.cytoscape.network.merge.internal.util;


import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

import org.cytoscape.model.CyTable;

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
                                              final CyTable attrs) {
        if (fromAttrName==null || toAttrName==null || attrs==null) {
            throw new java.lang.NullPointerException("Null fromAttrName or toAttrName or attrs");
        }
        
        final Set<String> attrNames = attrs.getColumnTypeMap().keySet();
        if (!attrNames.contains(fromAttrName)) {
            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' not exists");
        }
        
        if (!attrNames.contains(toAttrName)) {
            return true; // always convertible to a non-existing attribute
        }

        // first check the basic type is convertible
        final Class<?> valType1 = attrs.getType(fromAttrName);
        final Class<?> valType2 = attrs.getType(toAttrName);
        
        if (valType1 != valType2) { // if value type is not the same
            if ( valType1 == Boolean.class ) {
                    if (valType2!=Boolean.class && valType2!=String.class) {
                        return false;
                    }
            } else if ( valType1 == Double.class ) {
                    if (valType2!=Double.class && valType2!=String.class) {
                        return false;
                    }           
            } else if ( valType1 == Integer.class) {
                    if (valType2!=Integer.class && valType2!=Double.class && valType2!=String.class) {
                        return false;
                    }
            } else if ( valType1 ==  String.class ) {
                    if (valType2!=String.class) {
                        return false;
                    }
            } else {
                    return false;
            }
        }

        // TODO figure out what to do with lists and maps!
        // check list, map ...
//        final byte[] dimTypes1 = mmapDef.getAttributeKeyspaceDimensionTypes(fromAttrName);
//        final byte[] dimTypes2 = mmapDef.getAttributeKeyspaceDimensionTypes(toAttrName);
//
//        final int n1 = dimTypes1.length;
//        final int n2 = dimTypes2.length;
//        
//        if (n2==0 && valType2==MultiHashMapDefinition.TYPE_STRING) {
//            return true; // any type can convert to String
//        }
//        
//        if (n1==0 && n2==0) { // simple types
//            return true;
//        }
//        
//        if (n1==0 && n2==1) {
//            return dimTypes2[0] == MultiHashMapDefinition.TYPE_INTEGER; // converting from simple type to simple list
//        }
//        
//        if (n1!=n2) {
//            return false;
//        }
//        
//        for (int i=0; i<n1; i++) {
//            if (dimTypes1[i]!=dimTypes2[i]) {
//                return false;
//            }
//        }
        
        return valType1==valType2; // for complex case, type must be the same
    }

	public static boolean isAttributeTypeConvertable(final Class<?> fromType,
			final Class<?> toType) {
		if (fromType == Boolean.class) {
			return toType == Boolean.class || toType == String.class;
			// || toType==CyAttributes.TYPE_SIMPLE_LIST;
		} else if (fromType == Double.class) {
			return toType == Double.class || toType == String.class;
			// || toType==CyAttributes.TYPE_SIMPLE_LIST;
		} else if (fromType == Integer.class) {
			return toType == Integer.class || toType == Double.class
					|| toType == String.class;
			// || toType==CyAttributes.TYPE_SIMPLE_LIST;
		} else if (fromType == String.class) {
			return toType == String.class;
			// || toType==CyAttributes.TYPE_SIMPLE_LIST;
			// } else if ( fromType == CyAttributes.TYPE_SIMPLE_LIST ) {
			// return toType==String.class
			// || toType==CyAttributes.TYPE_SIMPLE_LIST;
			// } else if ( fromType == CyAttributes.TYPE_SIMPLE_MAP) {
			// return toType==CyAttributes.TYPE_STRING
			// || toType==CyAttributes.TYPE_SIMPLE_MAP;
			// } else if ( fromType == CyAttributes.TYPE_COMPLEX ) {
			// return toType==CyAttributes.TYPE_STRING
			// || toType==CyAttributes.TYPE_SIMPLE_MAP;
		} else {
			return false;
		}
	}
    
    /*
     * Return the attribute with the highest compatible attribute
     * 
     */
    public static String getMostCompatibleAttribute(final Set<String> attrNames, final CyTable attrs) {
        if (attrNames==null || attrs==null) {
            throw new java.lang.NullPointerException("Null attrNames or attrs");
        }
        
        attrNames.remove(null);
        if (attrNames.isEmpty()) {
            throw new java.lang.IllegalArgumentException("Empty attrNames");
        }
        
        if (!attrs.getColumnTypeMap().keySet().containsAll(attrNames)) {
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
            Class<?> type = attrs.getType(attr);
            if (type==List.class) {
                return attr;
            }
        }
        return compatibleSet.iterator().next();
        
    }

	public static Object attributeValueCast(Class<?> typeTo, Object value) {
		if (typeTo == Boolean.class) {
			return Boolean.valueOf(value.toString());
		} else if (typeTo == Integer.class) {
			return Integer.valueOf(value.toString());
		} else if (typeTo == Double.class) {
			return Double.valueOf(value.toString());
		} else if (typeTo == String.class) {
			return value.toString();
		} else {
			throw new IllegalStateException("wrong type");
		}
	}
    
}

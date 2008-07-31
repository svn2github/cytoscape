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

import csplugins.network.merge.conflict.AttributeConflict;
import csplugins.network.merge.conflict.AttributeConflictImpl;
import csplugins.network.merge.conflict.AttributeConflictCollector;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.AttributeValueVisitor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

/**
 *
 * 
 */
public class AttributeMatchingUtils {
    
    /*
     * Check if the types are convertable for two attributes
     * 
     * 
     * 
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
            return dimTypes2[0] == MultiHashMapDefinition.TYPE_INTEGER; // converting from simple type to simpl list
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
                if (!AttributeMatchingUtils.isAttributeTypeConvertable(attr2, attr1, attrs)) {
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
//
//    public static boolean isAttributeValueConflict(final String id1,
//                                               final String attrName1,
//                                               final String id2,
//                                               final String attrName2,
//                                               final CyAttributes attrs,
//                                               final Comparator comparator) {
//        if ((id1 == null) || (attrName1 == null) || (id2 == null) || (attrName2==null) || (attrs == null)) {
//            throw new java.lang.IllegalArgumentException("Null argument.");
//        }
//
//        final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
//        if (!attrNames.contains(attrName1) || !attrNames.contains(attrName2)) {
//            throw new java.lang.IllegalArgumentException("'"+attrName1+"' or/and '"+attrName2+"' not exists");
//        }
//
//        //if (!isAttributeTypeSame(attrName1,attrName2,attrs)) {
//        //    throw new java.lang.UnsupportedOperationException("isAttributeValueSame does not support two attribute with diffrent types");
//        //}
//
//        if (id1.compareTo(id2)==0 && attrName1.compareTo(attrName2)==0) {
//                return false;
//        }
//
//        if (!attrs.hasAttribute(id1, attrName1)
//                || !attrs.hasAttribute(id2, attrName2)) { // Is it neccessary to handle empty string?
//            return false; // if one of them or both of them are null, no conflicts
//        }
//
//
//        //TODO use a idmapping
//
//        byte type1 = attrs.getType(attrName1);
//        byte type2 = attrs.getType(attrName2);
//
//        if ((type1<0&&type1!=CyAttributes.TYPE_SIMPLE_LIST)
//                ||(type2<0&&type2!=CyAttributes.TYPE_SIMPLE_LIST)) { // only support matching between simple types
//                                                                     // and simple lists for now
//                                                                     //TODO: support simple and complex map?
//            Object o1 = attrs.getAttribute(id1, attrName1);
//            Object o2 = attrs.getAttribute(id2, attrName2);
//            return comparator.compare(o1, o2)!=0;
//        }
//
//        if (type1>0&&type2>0) { // simple type
//            Object o1 = attrs.getAttribute(id1, attrName1);
//            Object o2 = attrs.getAttribute(id2, attrName2);
//            return comparator.compare(o1, o2)!=0;
//        } else {
//            return false; // no conflicts in these case
//                          // when merging, copy value of simple type or simple list to the other simple list
//        }
//    }
    
    //TODO could this function move to cytoscape.data.CyAttributeUtil?
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
                                     final CyAttributes attrs,
                                     final AttributeConflictCollector conflictCollector) {
        if ((fromID == null) || (fromAttrName == null) || (toID == null) || (toAttrName == null) || (attrs==null)) {
            throw new java.lang.IllegalArgumentException("Null argument.");
        }        
                
        final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());
        if (!attrNames.contains(fromAttrName)||!attrNames.contains(toAttrName)) { // toAttrName must be defined before calling this method
            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' or '"+toAttrName+"' not exists");
        }
        
        if (!isAttributeTypeConvertable(fromAttrName,toAttrName,attrs)) {
            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' cannot be converted to '"+toAttrName+"'");
        }

        if (toID.compareTo(fromID)==0 && toAttrName.compareTo(fromAttrName)==0) {
            //TODO: if local attribute is realized, process here
            return;
        }
        
        if (!attrs.hasAttribute(fromID, fromAttrName)) {
            return;
        }
                
        //byte type1 = attrs.getType(fromAttrName);
        byte type2 = attrs.getType(toAttrName);
        
        if (type2 == CyAttributes.TYPE_STRING) { // the case of inconvertable attributes and simple attributes to String
            Object o1 = attrs.getAttribute(fromID, fromAttrName); //Correct??
            Object o2 = attrs.getAttribute(toID, toAttrName);
            if (o2==null) { //null attribute
                            // how about empty attribute?
                attrs.setAttribute(toID, toAttrName, (String)o1);
            } else if (o1.equals(o2)) {
                return;// the same, do nothing
            } else { // attribute conflict
                //handle the conflicts

                // first check whether some entry in conflict collecter have the same from value
                // if yes, skip this one
                List<AttributeConflict> conflicts = conflictCollector.getConflicts(toID, toAttrName);
                if (conflicts!=null) {
                        for (AttributeConflict ac:conflicts) {
                                if (!attrs.equals(ac.getCyAttributes())) {
                                        throw new java.lang.IllegalStateException("different CyAttribute");
                                }
                                Object o = attrs.getAttribute(ac.getFromID(), ac.getFromAttr());
                                if (o.equals(o1)) {
                                        return;
                                }
                        }
                }

                // add to conflict
                AttributeConflict conflict = new AttributeConflictImpl(fromID, fromAttrName, toID, toAttrName, attrs);
                conflictCollector.addConflict(conflict);
                return;
            }
        }
        
        if (type2<0&&type2!=CyAttributes.TYPE_SIMPLE_LIST) { // only support matching between simple types
                                                                     // and simple lists for now
                                                                     //TODO: support simple and complex map?            
            Object o1 = attrs.getAttribute(fromID, fromAttrName); //Correct??
            Object o2 = attrs.getAttribute(toID, toAttrName);
            if (o2==null) {
                AttributeValueVisitor copyVisitor = new CopyingAttributeValueVisitor(toID,toAttrName);
                CyAttributesUtils.traverseAttributeValues(fromID, fromAttrName, attrs, copyVisitor);
                return;
            } else if (o1.equals(o2)) {
                return; // the same, do nothing
            } else { // attribute conflict
                //handle the conflicts

                // first check whether some entry in conflict collecter have the same from value
                // if yes, skip this one
                List<AttributeConflict> conflicts = conflictCollector.getConflicts(toID, toAttrName);
                for (AttributeConflict ac:conflicts) {
                        if (!attrs.equals(ac.getCyAttributes())) {
                                throw new java.lang.IllegalStateException("different CyAttribute");
                        }
                        Object o = attrs.getAttribute(ac.getFromID(), ac.getFromAttr());
                        if (o.equals(o1)) {
                                return;
                        }
                }

                // add to conflict
                AttributeConflict conflict = new AttributeConflictImpl(fromID, fromAttrName, toID, toAttrName, attrs);
                conflictCollector.addConflict(conflict);
                return;
            }
        }
        
        if (type2>0) { // simple type (type1>0)
            Object o1 = attrs.getAttribute(fromID, fromAttrName);
            byte type1 = attrs.getType(fromAttrName);
            if (type1!=type2) {
                o1 = attributeValueCast(type2,o1);
            }
            
            Object o2 = attrs.getAttribute(toID, toAttrName);
            if (o2==null) {
                attrs.getMultiHashMap().setAttributeValue(toID, toAttrName, o1, null);                
            } else if (o1.equals(o2)) {
                return; // the same, do nothing
            } else { // attribute conflict
                //handle the conflicts

                // first check whether some entry in conflict collecter have the same from value
                // if yes, skip this one
                List<AttributeConflict> conflicts = conflictCollector.getConflicts(toID, toAttrName);
                for (AttributeConflict ac:conflicts) {
                        if (!attrs.equals(ac.getCyAttributes())) {
                                throw new java.lang.IllegalStateException("different CyAttribute");
                        }
                        Object o = attrs.getAttribute(ac.getFromID(), ac.getFromAttr());
                        if (o.equals(o1)) {
                                return;
                        }
                }

                // add to conflict
                AttributeConflict conflict = new AttributeConflictImpl(fromID, fromAttrName, toID, toAttrName, attrs);
                conflictCollector.addConflict(conflict);
                return;
            }
        } else { // toattr is list type
            //TODO: use a conflict handler to handle this part?

            type2 = attrs.getMultiHashMapDefinition().getAttributeValueType(toAttrName);
            byte type1 = attrs.getType(fromAttrName);
            if (type1>0) {
                Object o1 = attrs.getAttribute(fromID, fromAttrName);
                if (type1!=type2) {
                    o1 = attributeValueCast(type2,o1);
                }
                
                List l2 = attrs.getListAttribute(toID, toAttrName);
                if (l2==null) {
                    //l2 = new Vector();
                    throw new java.lang.IllegalStateException("Define '"+toAttrName+"' first");
                }
                
                if (!l2.contains(o1)) {
                    l2.add(o1);
                }
                
                attrs.setListAttribute(toID, toAttrName, l2);
                
                return;
            } else if (type1==CyAttributes.TYPE_SIMPLE_LIST) {
                type1 = attrs.getMultiHashMapDefinition().getAttributeValueType(fromAttrName);
                
                List l1 = attrs.getListAttribute(fromID, fromAttrName);
                List l2 = attrs.getListAttribute(toID, toAttrName);
                if (l2==null) {
                    //l2 = new Vector();
                    throw new java.lang.IllegalStateException("Define '"+toAttrName+"' first");
                }
                
                int nl1 = l1.size();
                for (int il1=0; il1<nl1; il1++) {
                    Object o1 = l1.get(il1);
                    if (type1!=type2) {
                        o1 = attributeValueCast(type2,o1);
                    }
                    if (!l2.contains(o1)) {
                        l2.add(o1);
                    }
                }
                
                attrs.setListAttribute(toID, toAttrName, l2);
                
                return;                
            } else {
                throw new java.lang.IllegalStateException("Wrong type");
            }
        } 
    }
    
    private static Object attributeValueCast(byte typeTo, Object value) {
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
        
    // this is different from CopyingAttributeValueVisitor in CyAtributeUtil
    // the attributes copying from and to can be different
    /**
     * copy each attribute value from copyID to an
     * attribute value of copyAttribute associated with objTraversedCopyID.
     */
    private static class CopyingAttributeValueVisitor implements AttributeValueVisitor {
        private String toID;
        private String toAttrName;

        public CopyingAttributeValueVisitor(String toID, String toAttrName) {
            this.toID = toID;
            this.toAttrName = toAttrName;
        }

        @Override
        public void visitingAttributeValue(String fromID, String attrName,
                                           CyAttributes attrs, Object[] keySpace,
                                           Object visitedValue) {
            if (!Arrays.asList(attrs.getAttributeNames()).contains(toAttrName)) { // if toAttribute not exists
                throw new java.lang.IllegalStateException("'"+toAttrName+"' must be defined before calling this method");
            }
            
            attrs.getMultiHashMap().setAttributeValue(toID, toAttrName, visitedValue, keySpace);
        }
    }
}

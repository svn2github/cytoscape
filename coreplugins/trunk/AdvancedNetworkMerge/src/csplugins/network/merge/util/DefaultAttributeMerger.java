/* File: DefaultAttributeMerger.java

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

import csplugins.network.merge.conflict.AttributeConflictCollector;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;

/**
 *
 * 
 */
public class DefaultAttributeMerger implements AttributeMerger {

        protected final AttributeConflictCollector conflictCollector;

        public DefaultAttributeMerger(final AttributeConflictCollector conflictCollector) {
                this.conflictCollector = conflictCollector;
        }

        /**
         * Merge one attribute into another
         * @param fromIDs
         * @param fromAttrName
         * @param toID
         * @param toAttrName
         * @param attrs
         * @param conflictCollector
         */
        //@Override
        public void mergeAttribute(final Map<String,String> mapGOAttr,
                                     final String toID,
                                     final String toAttrName,
                                     final CyAttributes attrs) {
                if ((mapGOAttr == null) || (toID == null) || (toAttrName == null) || (attrs==null)) {
                    throw new java.lang.IllegalArgumentException("Null argument.");
                }

                final List<String> attrNames = Arrays.asList(attrs.getAttributeNames());

                Iterator<Map.Entry<String,String>> itEntryGOAttr = mapGOAttr.entrySet().iterator();
                while (itEntryGOAttr.hasNext()) {
                        Map.Entry<String,String> entryGOAttr = itEntryGOAttr.next();
                        String fromID = entryGOAttr.getKey();
                        String fromAttrName = entryGOAttr.getValue();

                        if (!attrNames.contains(fromAttrName)||!attrNames.contains(toAttrName)) { // toAttrName must be defined before calling this method
                            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' or '"+toAttrName+"' not exists");
                        }

                        if (!AttributeValueCastUtils.isAttributeTypeConvertable(fromAttrName,toAttrName,attrs)) {
                            throw new java.lang.IllegalArgumentException("'"+fromAttrName+"' cannot be converted to '"+toAttrName+"'");
                        }

                        if (toID.compareTo(fromID)==0 && toAttrName.compareTo(fromAttrName)==0) {
                            //TODO: if local attribute is realized, process here
                            continue;
                        }

                        if (!attrs.hasAttribute(fromID, fromAttrName)) {
                            continue;
                        }

                        //byte type1 = attrs.getType(fromAttrName);
                        byte type2 = attrs.getType(toAttrName);

                        if (type2 == CyAttributes.TYPE_STRING) { // the case of inconvertable attributes and simple attributes to String
                            Object o1 = attrs.getAttribute(fromID, fromAttrName); //Correct??
                            String o2 = attrs.getStringAttribute(toID, toAttrName);
                            if (o2==null||o2.length()==0) { //null or empty attribute
                                attrs.setAttribute(toID, toAttrName, o1.toString());
                                //continue;
                            } else if (o1.equals(o2)) {
                                //continue;// the same, do nothing
                            } else { // attribute conflict
                                
                                // add to conflict collector
                                conflictCollector.addConflict(fromID, fromAttrName, toID, toAttrName,attrs);
                                //continue;
                            }
                        } else if (type2<0&&type2!=CyAttributes.TYPE_SIMPLE_LIST) { // only support matching between simple types
                                                                                     // and simple lists for now
                                                                                     //TODO: support simple and complex map?
                            Object o1 = attrs.getAttribute(fromID, fromAttrName); //Correct??
                            Object o2 = attrs.getAttribute(toID, toAttrName);
                            if (o2==null) {
                                CopyingAttributeValueVisitor copyVisitor = new CopyingAttributeValueVisitor(toID,toAttrName);
                                CyAttributesUtils.traverseAttributeValues(fromID, fromAttrName, attrs, copyVisitor);
                                //continue;
                            } else if (o1.equals(o2)) {
                                //continue; // the same, do nothing
                            } else { // attribute conflict

                                // add to conflict collector
                                conflictCollector.addConflict(fromID, fromAttrName, toID, toAttrName,attrs);
                                //continue;
                            }
                        } else if (type2>0) { // simple type (type1>0) (Integer, Double, Boolean)
                            Object o1 = attrs.getAttribute(fromID, fromAttrName);
                            byte type1 = attrs.getType(fromAttrName);
                            if (type1!=type2) {
                                o1 = AttributeValueCastUtils.attributeValueCast(type2,o1);
                            }

                            Object o2 = attrs.getAttribute(toID, toAttrName);
                            if (o2==null) {
                                attrs.getMultiHashMap().setAttributeValue(toID, toAttrName, o1, null);
                                //continue;
                            } else if (o1.equals(o2)) {
                                //continue; // the same, do nothing
                            } else { // attribute conflict

                                // add to conflict collector
                                conflictCollector.addConflict(fromID, fromAttrName, toID, toAttrName,attrs);
                                //continue;
                            }
                        } else { // toattr is list type
                            //TODO: use a conflict handler to handle this part?

                            type2 = attrs.getMultiHashMapDefinition().getAttributeValueType(toAttrName);
                            byte type1 = attrs.getType(fromAttrName);
                            if (type1>0) {
                                Object o1 = attrs.getAttribute(fromID, fromAttrName);
                                if (type1!=type2) {
                                    o1 = AttributeValueCastUtils.attributeValueCast(type2,o1);
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

                                //continue;
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
                                        o1 = AttributeValueCastUtils.attributeValueCast(type2,o1);
                                    }
                                    if (!l2.contains(o1)) {
                                        l2.add(o1);
                                    }
                                }

                                attrs.setListAttribute(toID, toAttrName, l2);

                                //continue;
                            } else {
                                throw new java.lang.IllegalStateException("Wrong type");
                            }
                        }
                }


        }


}

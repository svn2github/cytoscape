/* File: DefaultAttributeValueMatcher.java

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

import java.util.List;
import java.util.Arrays;

/**
 * Match attribute values
 *
 * 
 */
public class DefaultAttributeValueMatcher implements AttributeValueMatcher {

        /**
         * Check whether two attributes of two nodes/edges are "match"
         * @param id1
         * @param attr1
         * @param id2
         * @param attr2
         * @param cyAttributes
         * @return true if matched; false otherwise
         */
        //@Override
        public boolean matched(String id1, String attr1,
                String id2, String attr2, CyAttributes cyAttributes) {
                if ((id1 == null) || (attr1 == null) || (id2 == null) || (attr2==null) || (cyAttributes == null)) {
                    throw new java.lang.IllegalArgumentException("Null argument.");
                }

                final List<String> attrNames = Arrays.asList(cyAttributes.getAttributeNames());
                if (!attrNames.contains(attr1) || !attrNames.contains(attr2)) {
                    throw new java.lang.IllegalArgumentException("'"+attr1+"' or/and '"+attr2+"' not exists");
                }

                if (id1.compareTo(id2)==0 && attr1.compareTo(attr2)==0) {
                        return true;
                }

                if (!cyAttributes.hasAttribute(id1, attr1)
                        || !cyAttributes.hasAttribute(id2, attr2)) { // Is it neccessary to handle empty string?
                    return false; // return false for null attribute
                }

                //CmpAttributeValueVisitor cmpVisitor = new CmpAttributeValueVisitor(id1,attr1);

                //CyAttributesUtils.traverseAttributeValues(id2, attr2, cyAttributes, cmpVisitor);

                //return cmpVisitor.getIsSame();

                byte type1 = cyAttributes.getType(attr1);
                byte type2 = cyAttributes.getType(attr2);

                if ((type1<0&&type1!=CyAttributes.TYPE_SIMPLE_LIST)
                        ||(type2<0&&type2!=CyAttributes.TYPE_SIMPLE_LIST)) { // only support matching between simple types
                                                                             // and simple lists for now
                                                                             //TODO: support simple and complex map?
                    Object o1 = cyAttributes.getAttribute(id1, attr1);
                    Object o2 = cyAttributes.getAttribute(id2, attr2);

                    type1 = cyAttributes.getMultiHashMapDefinition().getAttributeValueType(attr1);
                    type2 = cyAttributes.getMultiHashMapDefinition().getAttributeValueType(attr2);

                    return o1.equals(o2) && type1==type2; // must be the same type for complex map
                }

                if (type1>0&&type2>0) { // simple type
                    Object o1 = cyAttributes.getAttribute(id1, attr1);
                    Object o2 = cyAttributes.getAttribute(id2, attr2);
                    return o1.equals(o2); //TODO: idmapping
                } else {
                    if (type1>0||type2>0) { // then one is simple type; the other is simple list
                        Object o;
                        List l;
                        if (type1>0) { // then type2 is simple list
                            o = cyAttributes.getAttribute(id1, attr1);
                            l = cyAttributes.getListAttribute(id2, attr2);
                        } else { // type2 is simple type and type 1 is simple list
                            l = cyAttributes.getListAttribute(id1, attr1);
                            o = cyAttributes.getAttribute(id2, attr2);
                        }

                        int nl = l.size();
                        for (int il=0; il<nl; il++) { // for each value in the list, find if match
                                                      // cannot use List.contains(), because type may be different
                            Object o2 = l.get(il);
                            if (o.equals(o2)) {// if one of the value in the list is the same as the other value
                                return true;
                            }
                        }
                        return false; // if no value match
                    } else { // both of them are simple lists
                        //TODO: use a list comparator?
                        List l1 = cyAttributes.getListAttribute(id1, attr1);
                        List l2 = cyAttributes.getListAttribute(id2, attr2);
                        int nl1 = l1.size();
                        int nl2 = l2.size();
                        for (int il1=0; il1<nl1; il1++) {
                            Object o1 = l1.get(il1);
                            for (int il2=0; il2<nl2; il2++) {
                                Object o2 = l2.get(il2);
                                if (o1.equals(o2)) { // if the two lists have intersections
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                }
        }
}

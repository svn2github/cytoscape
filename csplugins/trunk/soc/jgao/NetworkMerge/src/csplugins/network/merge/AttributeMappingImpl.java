/* File: AttributeMappingImpl.java

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

package csplugins.network.merge;

import csplugins.network.merge.util.AttributeMatchingUtils;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import java.util.Vector;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;

/**
 * Class to instore the information how to mapping the attributes 
 * in the original networks to those in the resulting networks
 * 
 * 
 */
public class AttributeMappingImpl implements AttributeMapping {
    private Map<String,Vector<String>> attributeMapping; //attribute mapping
    private Vector<String> attributeMerged;
    private CyAttributes cyAttributes;
    private final String nullAttr = ""; // to hold a position in vector standing that it's not a attribute

    public AttributeMappingImpl(final CyAttributes cyAttributes) {
        this.cyAttributes = cyAttributes;
        attributeMapping = new HashMap<String,Vector<String>>();
        attributeMerged = new Vector<String>();
    }
    
    public CyAttributes getCyAttributes() {
        return cyAttributes;
    }
    
    /*
     * Get attributes' names in the merged network
     * 
     */
    public String[] getMergedAttributes() {
        return (String[])attributeMerged.toArray(new String[0]);
    }
   
    /*
     * Get number of the attribute in the merged network
     * 
     */
    public int getSizeMergedAttributes() {
        return attributeMerged.size();
    }
            
    /*
     * Get the ith attribute name in the merged network
     * 
     */
    public String getMergedAttribute(final int index) {
        if (index<0 || index>=getSizeMergedAttributes()) {
            throw new java.lang.IndexOutOfBoundsException("Index out of boundary.");
        }
        
        //if (index>=attributeMerged.size()) return null;
        return attributeMerged.get(index);
    }
     
    /*
     * Set the ith attribute name in the merged network
     * 
     */
    public String setMergedAttribute(final int index, final String attributeName) {
        if (attributeName==null) {
            throw new java.lang.NullPointerException("Attribute name is null.");
        }
        
        // check whether all the type of the original attribute are the same
        if (!isAttributeTypeSame(index)) { // if not same type, not allowed to use existing attributes in original networks
            if (attributeExistsInOriginalNetwork(attributeName)) {
                return null;
            }
        }
        
        return attributeMerged.set(index, attributeName);
        
    }
            
    /*
     * Check if an attribute exists in the merged attributes
     * 
     */
    public boolean containsMergedAttributes(final String attributeName) {
        if (attributeName==null) {
            throw new java.lang.NullPointerException("Attribute name is null.");
        }
        return attributeMerged.contains(attributeName);
    }
    
    /*
     * Get the original attribute name in the network before merged
     * 
     */
    public String getOriginalAttribute(final String netID, final String mergedAttributeName) {
        final int index = attributeMerged.indexOf(mergedAttributeName);
        if (index==-1) {
            return null;
        }
        return getOriginalAttribute(netID, index);
    }
    
    /*
     * Get the original attribute name before merged, corresponding to the ith merged attribute
     * 
     */
    public String getOriginalAttribute(final String netID, final int index) {
        final Vector<String> attrs = attributeMapping.get(netID);
        if (attrs==null) return null;
        if (index>=attrs.size()||index<0) return null;
        final String attr = attrs.get(index);
        if (attr.compareTo(nullAttr)==0) return null;
        return attr;
    }
    
    /*
     * Set attribute mapping
     * 
     */
    public String setOriginalAttribute(final String netID, final String attributeName, final String mergedAttributeName) {
        final int index = attributeMerged.indexOf(mergedAttributeName);
        if (index==-1) {
            return null;
        }
        return setOriginalAttribute(netID, attributeName, index);
    }
            
    /*
     * Set attribute mapping
     * 
     */
    public String setOriginalAttribute(final String netID, final String attributeName, final int index){
        final Vector<String> attrs = attributeMapping.get(netID);
        if (attrs==null) return null;
        if (index>=attrs.size()||index<0) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        final String old;
        if (attributeName==null) {
            old = attrs.set(index, nullAttr);
            pack(index);
        } else {
            old = attrs.get(index);
            if (old.compareTo(attributeName)!=0) {                           
                attrs.set(index, attributeName);

                if (!isAttributeTypeSame(index)) {// reset the merged attribute names
                    String mergedAttr = getMergedAttribute(index);
                    if (attributeExistsInOriginalNetwork(mergedAttr)) {
                        setMergedAttribute(index,getDefaultMergedAttrName(mergedAttr,true));
                    }
                }
            }
 
        }
        return old;
    }
    
    /*
     * remove original attribute 
     * 
     */
    public String removeOriginalAttribute(final String netID, final String mergedAttributeName) {
        final int index = attributeMerged.indexOf(mergedAttributeName);
        return removeOriginalAttribute(netID, index);
    }
    
    /*
     * remove original attribute 
     * 
     */
    public String removeOriginalAttribute(final String netID, final int index) {
        return setOriginalAttribute(netID,null,index);
    }
    
    public void addNewAttribute(final String netID, final String attributeName) {
        final Iterator<Vector<String>> it = attributeMapping.values().iterator();
        while (it.hasNext()) { // add an empty attr for each network
            it.next().add(nullAttr);
        }
        final Vector<String> attrs = attributeMapping.get(netID);
        attrs.set(attrs.size()-1, attributeName); // set attr
        
        String attrMerged = attributeName;
        //TODO remove in Cytosape3
        if (attributeName.compareTo(Semantics.CANONICAL_NAME)==0) {
            attrMerged = netID+"."+Semantics.CANONICAL_NAME;
        }//TODO remove in Cytosape3
        
        attributeMerged.add(getDefaultMergedAttrName(attrMerged,false)); // add in merged attr  
    }

    /*
     * 
     * 
     */
    public void addNetwork(final String netID) {
        final String[] attributeNames = cyAttributes.getAttributeNames();
        final int nAttr = attributeNames.length;
        if (attributeMapping.isEmpty()) { // for the first network added
            
            final Vector<String> attrs = new Vector<String>();
            attributeMapping.put(netID, attrs);
                            

            for (int i=0; i<nAttr; i++) {
                //TODO REMOVE IN Cytoscape3.0
                if (attributeNames[i].compareTo(Semantics.CANONICAL_NAME)==0) {
                    continue;
                }//TODO REMOVE IN Cytoscape3.0
                
                addNewAttribute(netID, attributeNames[i]);
            }
            
            //TODO REMOVE IN 3.0, canonicalName in each network form a separate attribute in resulting network
            addNewAttribute(netID, Semantics.CANONICAL_NAME);//TODO REMOVE IN Cytoscape3.0
            

        } else { // for each attributes to be added, search if the same attribute exists
                 // if yes, add to that group; otherwise create a new one
            Vector<String> attrs = attributeMapping.get(netID);
            if (attrs!=null) { // this network already exist
                System.err.println("Error: this network already exist");
                return;
            }

            final int nr = attributeMerged.size(); // # of rows, the same as the # of attributes in merged network

            attrs = new Vector<String>(nr); // new map
            for (int i=0; i<nr; i++) {
                attrs.add(nullAttr);
            }
            attributeMapping.put(netID, attrs);

            for (int i=0; i<nAttr; i++) {
                final String at = attributeNames[i];
                 
                //TODO REMOVE IN Cytoscape3.0, canonicalName in each network form a separate attribute in resulting network
                if (at.compareTo(Semantics.CANONICAL_NAME)==0) {
                    addNewAttribute(netID, Semantics.CANONICAL_NAME);
                    continue;
                }//TODO REMOVE IN Cytoscape3.0
                 
                boolean found = false;             
                for (int ir=0; ir<nr; ir++) {
                    if (attrs.get(ir).compareTo(nullAttr)!=0) continue; // if the row is occupied
                    if (attributeMerged.get(ir).compareTo(at)==0) {
                        found = true;
                        attrs.set(ir, at);// add the attribute on the ir row
                        break; 
                    }

                    final Iterator<String> it = attributeMapping.keySet().iterator();
                    while (it.hasNext()) {
                        final String net_curr = it.next();
                        final String attr_curr = attributeMapping.get(net_curr).get(ir);
                        if (attr_curr.compareTo(at)==0) {
                            //if (AttributeMatchingUtils.isAttributeTypeSame(attr_curr,at,attributes)) // not neccessay in Cytoscape2.6
                                                                                                       // since attributes are global
                            found = true;
                            attrs.set(ir, at); // add the attribute on the ir row
                            break;
                        }
                    }

                    if (found) break;
                }

                if (!found) { //no same attribute found
                    addNewAttribute(netID,at);
                }                 
            }
        }
    }
    
    /*
     * get all network titles
     * 
     */
    public Set<String> getNetworkSet() {
        return attributeMapping.keySet();
    }

    /*
     * get number of networks
     * 
     */
    public int getSizeNetwork() {
        return attributeMapping.size();
    }   
    
    /*
     * 
     * 
     */
    public void removeNetwork(final String netID) {
        final Vector<String> removed = attributeMapping.remove(netID);
        final int n = removed.size();
        for (int i=n-1; i>=0; i--) {
            if (removed.get(i).compareTo(nullAttr)!=0) { // if the attribute is not empty
                pack(i);
            }
        }
    }
    
    /*
     * Check whether original attribute in each network has the same type for the merged attribute
     * 
     */    
    public boolean isAttributeTypeSame(final String mergedAttributeName) {        
        final int index = attributeMerged.indexOf(mergedAttributeName);
        if (index==-1) {
            throw new java.lang.IllegalArgumentException("No attribute has such a name!");
        }
        
        return isAttributeTypeSame(index);        
    }
    
    
    /*
     * Check whether ith original attribute in each network has the same type
     * 
     */    
    public boolean isAttributeTypeSame(final int index) {
        if (index<0 || index>=getSizeMergedAttributes()) {
            throw new java.lang.IndexOutOfBoundsException("Index out of boundary.");
        }
                
        if (attributeMerged.size()==0) {
            throw new java.lang.IllegalStateException("No network has been added");
        }
        
        Iterator<Vector<String>> it = attributeMapping.values().iterator();
        
        // check whether all the type of the original attribute are the same
        String attrName = it.next().get(index);
        while (it.hasNext()) {
            if (!isAttributeTypeSame(attrName, it.next().get(index))) {
                return false;
            }
        }
        
        return true;        
    }
    
    
    /*
     * Check whether two attributes have the same types
     * 
     */    
    public boolean isAttributeTypeSame(final String attr1, final String attr2) {        
        return AttributeMatchingUtils.isAttributeTypeSame(attr1, attr2, cyAttributes);            
    }
    
    /*
     * Return the type of merged attribute
     * 
     */
    public byte getMergedAttributeType(final String mergedAttributeName) {
        final int index = attributeMerged.indexOf(mergedAttributeName);
        if (index==-1) {
            throw new java.lang.IllegalArgumentException("No attribute has such a name!");
        }
        
        return getMergedAttributeType(index);
    }
            
    /*
     * Return the type of merged attribute
     * 
     */
    public byte getMergedAttributeType(final int index) {
        if (index<0 || index>=getSizeMergedAttributes()) {
            throw new java.lang.IndexOutOfBoundsException("Index out of boundary.");
        }
        
        if (attributeMerged.size()==0) {
            throw new java.lang.IllegalStateException("No network has been added");
        }
        
        if (isAttributeTypeSame(index)) {        
            String attr = attributeMapping.values().iterator().next().get(index);
            return cyAttributes.getType(attr);
        } else { // if not the same return type of Strng
            return CyAttributes.TYPE_STRING;
        }
    }
    
    
    
    /* 
     * Remove empty rows from the current attribute mapping
     * 
     * @param attributeMapping the current attribute mapping
     * 
     */
    private void pack(final int index) {

        Iterator<Vector<String>> it = attributeMapping.values().iterator();
        while (it.hasNext()) {
            if (it.next().get(index).compareTo(nullAttr)!=0) {
                return;
            }
        }

        attributeMerged.remove(index);

        it = attributeMapping.values().iterator();
        while ( it.hasNext() ) {
            it.next().remove(index);
        }

//        if (attributeMapping.isEmpty()) {
//            attributeMerged.clear();
//        }

    }
    
    private boolean attributeExistsInOriginalNetwork(String attr) {
        return Arrays.asList(cyAttributes.getAttributeNames()).contains(attr);
    }
    
    private String getDefaultMergedAttrName(final String attr, boolean excludeOriginalAttribute) {
        String appendix = "";
        int i = 0;

        while (true) {
            String attr_ret = attr+appendix;
            if (attributeMerged.contains(attr_ret)||(excludeOriginalAttribute&&attributeExistsInOriginalNetwork(attr_ret))){
                appendix = "." + ++i;
            } else {
                return attr+appendix;
            } 
        }
    }

}

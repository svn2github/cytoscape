/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.network.merge;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author gjj
 */
public class MatchingAttributeImpl implements MatchingAttribute {
    private Map<String,String> attributeForMatching; // network name to attribute name

    public MatchingAttributeImpl() {
        attributeForMatching = new HashMap<String,String>();
    }
    
    /*
     * Get the attribute of network for matching node
     * 
     */
    public String getAttributeForMatching(String netID) {
        return attributeForMatching.get(netID);
    }
    
    /*
     * Set the attribute of network for matching node
     * 
     */
    public void putAttributeForMatching(String netID, String attributeName) {
        attributeForMatching.put(netID, attributeName);
    }

    /*
     * add/select the attribute of network for matching node
     * 
     */
    /*public void addNetwork(String netID, String[] attributeNames) {
        if ( getAttributeForMatching(netID)!=null) { // this network already exist
            System.err.println("Error: this network already exist");
            return;
        }

        Collection<String> attr_curr = getAttributes();
        int nAttr = size();
        for (int i=0; i<nAttr; i++) {
             String at = attributeNames[i];
             if (attr_curr.contains(at)) {
                     putAttributeForMatching(netID, at);
                     return;
             }

        }

        // if no matched attribute found, use ID
        putAttributeForMatching(netID, NetworkMerge.ID);
    }*/
            
    /*
     * Remove the network, return the attribute
     * 
     */
    public String removeNetwork(String netID) {
        return attributeForMatching.remove(netID);
    }
    
    /*
     * 
     * 
     */
    public int size() {
        return attributeForMatching.size();
    }
    
    /*
     * 
     * 
     */
    public Set<String> getNetworkSet() {
        return attributeForMatching.keySet();
    }
    
    /*
     * 
     * 
     */
    public Collection<String> getAttributes() {
        return attributeForMatching.values();
    }
    
}

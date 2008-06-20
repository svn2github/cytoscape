/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.network.merge;

import java.util.Set;

/**
 *
 * @author gjj
 */
public interface AttributeMapping {
    
    /*
     * Get attributes' names in the merged network
     * 
     */
    public String[] getMergedAttributes();
        
    /*
     * Get number of the attribute in the merged network
     * 
     */
    public int getSizeMergedAttributes();
    
    /*
     * Get the ith attribute name in the merged network
     * 
     */
    public String getMergedAttribute(int index);
     
    /*
     * Set the ith attribute name in the merged network
     * 
     */
    public String setMergedAttribute(int index, String attributeName);
    
    /*
     * Check if an attribute exists in the merged attributes
     * 
     */
    public boolean containsMergedAttributes(String attributeName);
    
    /*
     * get all network titles
     * 
     */
    public Set<String> getNetworkSet();
    
    /*
     * get number of networks
     * 
     */
    public int getSizeNetwork();
    
    /*
     * Get the original attribute name in the network before merged, corresponding to the merged attribute
     * 
     */
    public String getOriginalAttribute(String netID, String mergedAttributeName);
    
    /*
     * Get the original attribute name before merged, corresponding to the ith merged attribute
     * 
     */
    public String getOriginalAttribute(String netID, int index);
    
    /*
     * Set attribute mapping
     * 
     */
    public String setOriginalAttribute(String netID, String attributeName, String mergedAttributeName);
    
    /*
     * Set attribute mapping
     * 
     */
    public String setOriginalAttribute(String netID, String attributeName, int index);
    
    /*
     * Add a new attribute at ith with attributeName in the original network and add a new attribute as attrMerged in resulting network
     * 
     */
    public void addNewAttribute(String netID, String attributeName, String attrMerged);

    /*
     * Get the default attribute name for a new merged one 
     * 
     */
    public String getDefaultMergedAttrName(String attr);
    
    /*
     * 
     * 
     */
    public void addNetwork(String netID, String[] attributeNames);

    /*
     * 
     * 
     */
    public void removeNetwork(String netID);

}
